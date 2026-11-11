package net.cebularz.winterwonders.entity.ai.pingin;

import java.util.Comparator;
import java.util.EnumSet;

import net.cebularz.winterwonders.entity.custom.PinginEntity;
// import net.cebularz.winterwonders.block.entity.custom.FishRackBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class PinginFoodGoal extends Goal {
    private final PinginEntity pingin;
    private PinginEntity pinginBaby;
    private int eatTicks, nextPathTick;
    private boolean reachedHome;

    public PinginFoodGoal(PinginEntity pingin) {
        this.pingin = pingin;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (pingin.isAlive() && pingin.getRecruiterId() == null && pingin.getTarget() == null && !pingin.isAdmiring()
                && pingin.onGround() && !pingin.isInWaterOrBubble()) {
            ItemStack heldItem = pingin.getMainHandItem();
            boolean carryingFish = heldItem.is(ItemTags.FISHES);
            // FishRackBlockEntity rack = pingin.getFishRack();
            // boolean hasFood = !pingin.getFoodInventory().isEmpty() || rack != null && rack.fishCount() > 0;
            boolean hasFood = !pingin.getFoodInventory().isEmpty();
            if (carryingFish || hasFood && (heldItem.isEmpty() || pingin.hasWeapon() && pingin.wantsFish())) {
                pinginBaby = pingin.isBaby() || pingin.hasWeapon() ? null : findBaby();
                if (pingin.wantsFish() || pinginBaby != null) {
                    return true;
                } else if (carryingFish) {
                    BlockPos homePos = pingin.getHomePos();
                    return homePos == null || pingin.distanceToSqr(Vec3.atBottomCenterOf(homePos)) > 4D
                            // || rack != null && rack.fishCount() < 32
                            || pingin.getFoodInventory().canAddItem(heldItem);
                }
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        ItemStack heldItem = pingin.getMainHandItem();
        // FishRackBlockEntity rack = pingin.getFishRack();
        // boolean rackHasFish = rack != null && rack.fishCount() > 0;
        // boolean recovering = pingin.hasWeapon() && pingin.wantsFish() && (!pingin.getFoodInventory().isEmpty() || rackHasFish);
        boolean recovering = pingin.hasWeapon() && pingin.wantsFish() && !pingin.getFoodInventory().isEmpty();
        if (pingin.isAlive() && pingin.getRecruiterId() == null && pingin.getTarget() == null && !pingin.isAdmiring()
                && pingin.onGround() && !pingin.isInWaterOrBubble()
                // && (heldItem.is(ItemTags.FISHES) || recovering || heldItem.isEmpty() && rackHasFish)) {
                && (heldItem.is(ItemTags.FISHES) || recovering)) {
            if (reachedHome) {
                return pingin.wantsFish() || canFeedBaby(pinginBaby)
                        // || rack != null && rack.fishCount() < 32
                        || pingin.getFoodInventory().canAddItem(heldItem);
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public void start() {
        if (pingin.getHomePos() == null) {
            pingin.setHomePos(pingin.blockPosition());
        }
        reachedHome = pingin.distanceToSqr(Vec3.atBottomCenterOf(pingin.getHomePos())) <= 4D;
        eatTicks = 0;
        nextPathTick = 0;
        if (pingin.getMainHandItem().isEmpty()) {
            for (int slot = 0; slot < pingin.getFoodInventory().getContainerSize(); slot++) {
                if (pingin.getFoodInventory().getItem(slot).is(ItemTags.FISHES)) {
                    pingin.setItemInHand(InteractionHand.MAIN_HAND, pingin.getFoodInventory().removeItem(slot, 1));
                    break;
                }
            }
            // FishRackBlockEntity rack = pingin.getFishRack();
            // if (pingin.getMainHandItem().isEmpty() && rack != null && reachedHome) {
                // pingin.setItemInHand(InteractionHand.MAIN_HAND, rack.takeFish());
            // }
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        Vec3 homeTarget = Vec3.atBottomCenterOf(pingin.getHomePos());
        if (pingin.distanceToSqr(homeTarget) <= 4D) {
            reachedHome = true;
        }
        // FishRackBlockEntity rack = pingin.getFishRack();
        // if (reachedHome && rack != null) {
            // rack.finishTrip(pingin);
        // }
        // if (reachedHome && pingin.getMainHandItem().isEmpty() && rack != null) {
            // pingin.setItemInHand(InteractionHand.MAIN_HAND, rack.takeFish());
        // }
        if (reachedHome && !pingin.isBaby() && !pingin.hasWeapon() && !canFeedBaby(pinginBaby)) {
            pinginBaby = findBaby();
        }

        if (!reachedHome) {
            if (pingin.tickCount >= nextPathTick) {
                Path path = pingin.getNavigation().createPath(pingin.getHomePos(), 0);
                pingin.getNavigation().moveTo(path, 1.1D);
                nextPathTick = pingin.tickCount + 20;
            }
        } else if (canFeedBaby(pinginBaby)) {
            pingin.getLookControl().setLookAt(pinginBaby, 30F, 30F);
            if (pingin.distanceToSqr(pinginBaby) <= 2.25D) {
                pingin.getNavigation().stop();
                pinginBaby.setItemInHand(InteractionHand.MAIN_HAND, pingin.getMainHandItem().split(1));
                pinginBaby.setDropChance(EquipmentSlot.MAINHAND, 1F);
                pingin.startSwimAction(PinginEntity.CATCH);
            } else if (pingin.tickCount >= nextPathTick) {
                Path path = pingin.getNavigation().createPath(pinginBaby, 0);
                pingin.getNavigation().moveTo(path, 1.1D);
                nextPathTick = pingin.tickCount + 20;
            }
        } else if (pingin.wantsFish()) {
            pingin.getNavigation().stop();
            eatTicks++;
            if (eatTicks % 20 == 0) {
                pingin.startSwimAction(PinginEntity.CATCH);
                pingin.playSound(SoundEvents.FOX_EAT, 0.6F, 1.3F);
                if (pingin.getMainHandItem().is(ItemTags.FISHES)) {
                    Vec3 beakPos = pingin.getEyePosition().add(pingin.getViewVector(1F).scale(0.3D));
                    ((ServerLevel)pingin.level()).sendParticles(new ItemParticleOption(ParticleTypes.ITEM, pingin.getMainHandItem()),
                            beakPos.x, beakPos.y, beakPos.z, 4, 0.08D, 0.05D, 0.08D, 0.01D);
                }
            }
            if (eatTicks >= 60) {
                pingin.eatFish();
                eatTicks = 0;
            }
        } else {
            pingin.getNavigation().stop();
            // if (rack != null && rack.fishCount() < 32) {
                // pingin.setItemInHand(InteractionHand.MAIN_HAND, rack.store(pingin.getMainHandItem()));
            // }
            pingin.setItemInHand(InteractionHand.MAIN_HAND, pingin.getFoodInventory().addItem(pingin.getMainHandItem()));
        }
    }

    private PinginEntity findBaby() {
        return pingin.level().getEntitiesOfClass(PinginEntity.class, pingin.getBoundingBox().inflate(12D), this::canFeedBaby).stream()
                .min(Comparator.comparing((PinginEntity pinginBaby) -> !pingin.isParentOf(pinginBaby))
                        .thenComparingDouble(pingin::distanceToSqr)).orElse(null);
    }

    private boolean canFeedBaby(PinginEntity pinginBaby) {
        return pinginBaby != null && pinginBaby.isAlive() && pinginBaby.isBaby()
                && pinginBaby.wantsFish() && pinginBaby.getMainHandItem().isEmpty()
                && pinginBaby.onGround() && !pinginBaby.isInWaterOrBubble() && pingin.distanceToSqr(pinginBaby) < 144D
                && pingin.getSensing().hasLineOfSight(pinginBaby);
    }

    @Override
    public void stop() {
        pingin.getNavigation().stop();
        pinginBaby = null;
        eatTicks = 0;
    }
}

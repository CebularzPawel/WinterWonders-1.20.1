package net.cebularz.winterwonders.entity.ai.pingin;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

import net.cebularz.winterwonders.entity.custom.PinginEntity;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class PinginTradeWithPlayerGoal extends Goal {
    private final PinginEntity pingin;
    private final List<Item> tradableItemList;
    private ItemEntity targetItem;
    private int checkingTimer = 0;
    private int nextCheck;
    private boolean isChecking = false;
    private boolean hasItemInHand = false;
    private static final int CHECKING_TIME = 40;

    public PinginTradeWithPlayerGoal(PinginEntity pingin, List<Item> tradableItemList) {
        this.pingin = pingin;
        this.tradableItemList = tradableItemList;
        this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!pingin.getMainHandItem().is(ItemTags.FISHES)) {
            if (pingin.isAlive() && !pingin.isInWater() && pingin.onGround() && pingin.getTarget() == null) {
                if (isChecking || hasItemInHand) {
                    return true;
                } else {
                    if (!pingin.hasWeapon() && isTradable(pingin.getMainHandItem())) {
                        hasItemInHand = true;
                        return true;
                    } else if (pingin.tickCount >= nextCheck) {
                        nextCheck = pingin.tickCount + 20 + pingin.getRandom().nextInt(20);
                        List<ItemEntity> droppedItems = pingin.level().getEntitiesOfClass(
                                ItemEntity.class,
                                pingin.getBoundingBox().inflate(16.0D));

                        targetItem = droppedItems.stream()
                                .filter(this::canAccept)
                                .min(Comparator.comparingDouble(item -> item.distanceToSqr(pingin)))
                                .orElse(null);

                        return targetItem != null;
                    } else {
                        return false;
                    }
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (pingin.isInWater() || pingin.getMainHandItem().is(ItemTags.FISHES) || pingin.getTarget() != null) {
            return false;
        } else {
            return isChecking || hasItemInHand || (targetItem != null && canAccept(targetItem));
        }
    }

    @Override
    public void start() {
        if (!pingin.hasWeapon() && isTradable(pingin.getMainHandItem())) {
            hasItemInHand = true;
            startChecking();
        }
    }

    @Override
    public void tick() {
        if (isChecking) {
            checkingTimer--;

            if (checkingTimer % 20 == 0 && pingin.getRandom().nextFloat() < 0.3F) {
                pingin.playSound(SoundEvents.PIGLIN_ADMIRING_ITEM, 0.5F,
                        1.0F + (pingin.getRandom().nextFloat() - pingin.getRandom().nextFloat()) * 0.2F);
            }

            if (checkingTimer <= 0) {
                giveReward();
                isChecking = false;
                hasItemInHand = false;
            }
        } else {
            if (!hasItemInHand && !pingin.hasWeapon() && isTradable(pingin.getMainHandItem())) {
                hasItemInHand = true;
                startChecking();
            } else if (targetItem != null && canAccept(targetItem)) {
                double distance = pingin.distanceToSqr(targetItem);

                if (distance > 1.5) {
                    pingin.getNavigation().moveTo(targetItem, 1.0);
                } else {
                    pingin.getNavigation().stop();
                    pickUpItem();
                }
            }
        }
    }

    private boolean isTradable(ItemStack stack) {
        return tradableItemList.stream().anyMatch(stack::is);
    }

    private boolean canAccept(ItemEntity item) {
        if (item.isAlive() && !item.hasPickUpDelay()) {
            ItemStack stack = item.getItem();
            if (stack.is(ItemTags.FISHES)) {
                return !pingin.isBaby() && item.getOwner() instanceof Player player && player.isAlive()
                        && pingin.likesPlayer(player) && (pingin.getRecruiterId() == null || pingin.getRecruiterId().equals(player.getUUID()));
            } else if (PinginEntity.isWeapon(stack)) {
                return pingin.wantsWeapon(stack);
            } else {
                return !pingin.hasWeapon() && pingin.getMainHandItem().isEmpty() && isTradable(stack);
            }
        } else {
            return false;
        }
    }

    private void pickUpItem() {
        if (targetItem != null && canAccept(targetItem)) {
            pingin.playSound(SoundEvents.ITEM_PICKUP, 0.5F,
                    1.0F + (pingin.getRandom().nextFloat() - pingin.getRandom().nextFloat()) * 0.2F);
            ItemStack stack = targetItem.getItem();
            if (stack.is(ItemTags.FISHES) && targetItem.getOwner() instanceof Player player) {
                pingin.recruit(player);
                ((ServerLevel)pingin.level()).sendParticles(new ItemParticleOption(ParticleTypes.ITEM, stack.copyWithCount(1)),
                        pingin.getX(), pingin.getEyeY(), pingin.getZ(), 4, 0.08D, 0.05D, 0.08D, 0.01D);
                pingin.take(targetItem, 1);
                stack.shrink(1);
            } else if (PinginEntity.isWeapon(stack)) {
                if (pingin.equipWeapon(stack)) {
                    pingin.take(targetItem, 1);
                    stack.shrink(1);
                    hasItemInHand = true;
                    startChecking();
                }
            } else {
                pingin.setItemInHand(InteractionHand.MAIN_HAND, stack.copy());
                stack.setCount(0);
                hasItemInHand = true;
                startChecking();
            }
            if (stack.isEmpty()) {
                targetItem.discard();
            }
            targetItem = null;
        }
    }

    private void startChecking() {
        isChecking = true;
        checkingTimer = CHECKING_TIME + pingin.getRandom().nextInt(40);
        pingin.setAdmiring(true);
        pingin.getNavigation().stop();
    }

    private void giveReward() {
        if (pingin.hasWeapon()) {
            pingin.setAdmiring(false);
        } else if (!pingin.getMainHandItem().isEmpty()) {
            pingin.getMainHandItem().shrink(1);

            if (pingin.getTradeContainer() != null && !pingin.getTradeContainer().isEmpty()) {
                int index = pingin.getRandom().nextInt(pingin.getTradeContainer().getContainerSize());
                ItemStack rewardItem = pingin.getTradeContainer().getItem(index).copy();

                if (!rewardItem.isEmpty()) {
                    rewardItem.setCount(1 + pingin.getRandom().nextInt(3));

                    ItemEntity throwingItem = new ItemEntity(
                            pingin.level(),
                            pingin.getX(),
                            pingin.getEyeY() - 0.3,
                            pingin.getZ(),
                            rewardItem
                    );

                    double dx = pingin.getRandom().nextGaussian() * 0.13D;
                    double dz = pingin.getRandom().nextGaussian() * 0.13D;
                    throwingItem.setDeltaMovement(dx, 0.3, dz);

                    pingin.level().addFreshEntity(throwingItem);

                    pingin.playSound(SoundEvents.VILLAGER_YES, 1.0F, 1.0F);
                }

                hasItemInHand = false;
                pingin.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                pingin.setAdmiring(false);
            }
        }
    }

    @Override
    public void stop() {
        this.targetItem = null;
        this.isChecking = false;
        this.hasItemInHand = false;
        this.checkingTimer = 0;
        this.pingin.setAdmiring(false);
    }
}

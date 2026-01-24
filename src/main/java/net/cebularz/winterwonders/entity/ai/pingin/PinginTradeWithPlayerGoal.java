package net.cebularz.winterwonders.entity.ai.pingin;

import net.cebularz.winterwonders.entity.custom.PinginEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;
import java.util.List;

public class PinginTradeWithPlayerGoal extends Goal {
    private final PinginEntity mob;
    private final List<Item> tradableItemList;
    private ItemEntity targetItem;
    private int checkingTimer = 0;
    private boolean isChecking = false;
    private boolean hasItemInHand = false;
    private static final int CHECKING_TIME = 40;
    private static final int ADMIRE_ITEM_TIME = 120;
    private int admireItemTimer;

    public PinginTradeWithPlayerGoal(PinginEntity mob, List<Item> tradableItemList) {
        this.mob = mob;
        this.tradableItemList = tradableItemList;
        this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!this.mob.isAlive() || this.mob.isInWater() || !this.mob.onGround()) {
            return false;
        }

        if (isChecking || hasItemInHand) {
            return true;
        }

        if (tradableItemList.stream().anyMatch(item -> mob.getMainHandItem().is(item))) {
            hasItemInHand = true;
            return true;
        }

        List<ItemEntity> droppedItems = mob.level().getEntitiesOfClass(
                ItemEntity.class,
                mob.getBoundingBox().inflate(16.0D));

        targetItem = droppedItems.stream()
                .filter(itemEntity -> !itemEntity.hasPickUpDelay() && tradableItemList.stream()
                        .anyMatch(item -> itemEntity.getItem().is(item)))
                .min((a, b) -> Double.compare(
                        a.distanceToSqr(mob),
                        b.distanceToSqr(mob)))
                .orElse(null);

        return targetItem != null;
    }

    @Override
    public boolean canContinueToUse() {
        return isChecking || hasItemInHand || (targetItem != null && targetItem.isAlive());
    }

    @Override
    public void start() {
        if (tradableItemList.stream().anyMatch(item -> mob.getMainHandItem().is(item))) {
            hasItemInHand = true;
            startChecking();
        }
    }

    @Override
    public void tick() {
        if (isChecking) {
            checkingTimer--;

            //if (checkingTimer % 5 == 0) {
            //    this.mob.getLookControl().setLookAt(
            //            this.mob.getX() + this.mob.getRandom().nextGaussian() * 2.0D,
            //            this.mob.getEyeY(),
            //            this.mob.getZ() + this.mob.getRandom().nextGaussian() * 2.0D
            //    );
            //}

            if (checkingTimer % 20 == 0 && mob.getRandom().nextFloat() < 0.3F) {
                mob.playSound(SoundEvents.PIGLIN_ADMIRING_ITEM, 0.5F,
                        1.0F + (mob.getRandom().nextFloat() - mob.getRandom().nextFloat()) * 0.2F);
            }

            if (checkingTimer <= 0) {
                giveReward();
                isChecking = false;
                hasItemInHand = false;
            }
            return;
        }

        if (!hasItemInHand) {
            for (Item likedItem : this.tradableItemList) {
                if (mob.getMainHandItem().is(likedItem)) {
                    hasItemInHand = true;
                    startChecking();
                    return;
                }
            }
        }

        if (targetItem != null && targetItem.isAlive()) {
            double distance = mob.distanceToSqr(targetItem);

            if (distance > 1.5) {
                mob.getNavigation().moveTo(targetItem, 1.0);
            } else {
                mob.getNavigation().stop();
                pickUpItem();
            }
        }
    }

    private void pickUpItem() {
        if (targetItem != null && targetItem.isAlive()) {
            mob.playSound(SoundEvents.ITEM_PICKUP, 0.5F,
                    1.0F + (mob.getRandom().nextFloat() - mob.getRandom().nextFloat()) * 0.2F);

            mob.setItemInHand(InteractionHand.MAIN_HAND, targetItem.getItem().copy());
            targetItem.discard();
            targetItem = null;
            hasItemInHand = true;

            startChecking();
        }
    }

    private void startChecking() {
        isChecking = true;
        checkingTimer = CHECKING_TIME + mob.getRandom().nextInt(40);
        mob.setAdmiring(true);
        mob.getNavigation().stop();
    }

    private void giveReward() {
        if (mob.getMainHandItem().isEmpty()) {
            return;
        }

        mob.getMainHandItem().shrink(1);

        if (mob.getTradeContainer() == null || mob.getTradeContainer().isEmpty()) {
            return;
        }

        int index = mob.getRandom().nextInt(mob.getTradeContainer().getContainerSize());
        ItemStack rewardItem = mob.getTradeContainer().getItem(index).copy();

        if (!rewardItem.isEmpty()) {
            rewardItem.setCount(1 + mob.getRandom().nextInt(3));

            ItemEntity throwingItem = new ItemEntity(
                    mob.level(),
                    mob.getX(),
                    mob.getEyeY() - 0.3,
                    mob.getZ(),
                    rewardItem
            );

            double dx = mob.getRandom().nextGaussian() * 0.13D;
            double dz = mob.getRandom().nextGaussian() * 0.13D;
            throwingItem.setDeltaMovement(dx, 0.3, dz);

            mob.level().addFreshEntity(throwingItem);

            mob.playSound(SoundEvents.VILLAGER_YES, 1.0F, 1.0F);
        }

        hasItemInHand = false;
        mob.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        mob.setAdmiring(false);
    }

    @Override
    public void stop() {
        this.targetItem = null;
        this.isChecking = false;
        this.hasItemInHand = false;
        this.checkingTimer = 0;
        this.mob.setAdmiring(false);
    }
}
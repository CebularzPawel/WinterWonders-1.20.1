package net.cebularz.winterwonders.entity.ai.pingin;

import java.util.Comparator;
import java.util.EnumSet;

import net.cebularz.winterwonders.entity.custom.PinginEntity;
import net.cebularz.winterwonders.entity.custom.projectile.ThrowingSpearProjectile;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.Path;

public class PinginRetrieveSpearGoal extends Goal {
    private final PinginEntity pingin;
    private ThrowingSpearProjectile spear;
    private Path path;
    private int nextCheck, nextPathTick, endTick;

    public PinginRetrieveSpearGoal(PinginEntity pingin) {
        this.pingin = pingin;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (pingin.tickCount >= nextCheck && pingin.isAlive() && !pingin.isBaby() && !pingin.isAdmiring()
                && !pingin.isAttacking() && pingin.getAirSupply() >= 400) {
            nextCheck = pingin.tickCount + 20;
            LivingEntity target = pingin.getTarget() == null ? pingin.getLastHurtByMob() : pingin.getTarget();
            if (target == null || pingin.distanceToSqr(target) > 16D) {
                spear = pingin.level().getEntitiesOfClass(ThrowingSpearProjectile.class, pingin.getBoundingBox().inflate(16D),
                        spear -> spear.getOwner() == pingin && spear.isRetrievable() && pingin.wantsWeapon(spear.getItem())
                                && (target == null || target.distanceToSqr(spear) > 16D)).stream()
                        .min(Comparator.comparingDouble(pingin::distanceToSqr)).orElse(null);
                if (spear != null) {
                    path = pingin.getNavigation().createPath(spear.blockPosition(), 1);
                    return pingin.distanceToSqr(spear) <= 2.25D || path != null && path.canReach();
                }
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = pingin.getTarget() == null ? pingin.getLastHurtByMob() : pingin.getTarget();
        return pingin.isAlive() && !pingin.isAdmiring() && !pingin.isAttacking() && pingin.getAirSupply() >= 400
                && spear != null && spear.isAlive() && spear.isRetrievable() && pingin.wantsWeapon(spear.getItem())
                && pingin.tickCount < endTick && (target == null || pingin.distanceToSqr(target) > 16D && target.distanceToSqr(spear) > 16D)
                && (!pingin.getNavigation().isDone() || pingin.distanceToSqr(spear) <= 2.25D);
    }

    @Override
    public void start() {
        endTick = pingin.tickCount + 160;
        nextPathTick = pingin.tickCount + 20;
        pingin.getNavigation().moveTo(path, 1.15D);
    }

    @Override
    public void tick() {
        pingin.getLookControl().setLookAt(spear, 30F, 30F);
        if (pingin.distanceToSqr(spear) <= 2.25D && pingin.equipWeapon(spear.getItem())) {
            pingin.take(spear, 1);
            spear.discard();
            pingin.playSound(SoundEvents.ITEM_PICKUP, 0.5F, 1.2F);
        } else if (pingin.tickCount >= nextPathTick) {
            pingin.getNavigation().moveTo(pingin.getNavigation().createPath(spear.blockPosition(), 1), 1.15D);
            nextPathTick = pingin.tickCount + 20;
        }
    }

    @Override
    public void stop() {
        pingin.getNavigation().stop();
        spear = null;
        path = null;
    }
}

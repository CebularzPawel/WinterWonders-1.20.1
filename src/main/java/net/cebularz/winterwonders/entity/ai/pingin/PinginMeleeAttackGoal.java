package net.cebularz.winterwonders.entity.ai.pingin;

import net.cebularz.winterwonders.entity.custom.PinginAttack;
import net.cebularz.winterwonders.entity.custom.PinginEntity;
import net.cebularz.winterwonders.entity.custom.projectile.ThrowingSpearProjectile;
import net.cebularz.winterwonders.item.custom.ThrowingSpearItem;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class PinginMeleeAttackGoal extends MeleeAttackGoal {
    private static final double THROW_MIN = 4D, THROW_MAX = 8D;
    private final PinginEntity pingin;
    private int hitDelay;
    private int attackTicks;
    private LivingEntity hitTarget;

    public PinginMeleeAttackGoal(PinginEntity pingin) {
        super(pingin, 1.15D, true);
        this.pingin = pingin;
    }

    private boolean canFight() {
        return pingin.canFight() && !pingin.isAdmiring() && !pingin.isOnFire();
    }

    @Override
    public boolean canUse() {
        return canFight() && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return canFight() && super.canContinueToUse();
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity target, double distanceSquared) {
        attackTicks = Math.max(0, attackTicks - 1);
        if (attackTicks == 0 && hitDelay == 0) {
            ItemStack main = pingin.getMainHandItem(), reserve = pingin.getOffhandItem();
            boolean mainSpear = main.getItem() instanceof ThrowingSpearItem;
            boolean reserveSpear = reserve.getItem() instanceof ThrowingSpearItem;
            if (main.isEmpty() && PinginEntity.isWeapon(reserve)
                    || distanceSquared <= 16D && mainSpear && PinginEntity.isWeapon(reserve) && !reserveSpear
                    || distanceSquared >= 25D && distanceSquared <= THROW_MAX * THROW_MAX && reserveSpear) {
                pingin.setItemInHand(InteractionHand.MAIN_HAND, reserve);
                pingin.setItemInHand(InteractionHand.OFF_HAND, main);
            }
        }
        ItemStack stack = pingin.getMainHandItem();
        boolean spear = stack.getItem() instanceof ThrowingSpearItem;
        double reachSquared = getAttackReachSqr(target);
        boolean close = distanceSquared <= reachSquared;
        boolean visible = pingin.getSensing().hasLineOfSight(target);
        boolean throwing = spear && stack.getDamageValue() < stack.getMaxDamage() - 1
                && distanceSquared >= THROW_MIN * THROW_MIN && distanceSquared <= THROW_MAX * THROW_MAX
                && (attackTicks > 0 || canThrowAt(target));
        boolean counter = hitDelay > 0 && pingin.getAttackType() == PinginAttack.THROW && close && visible;
        if (close || throwing || attackTicks > 0 && pingin.getAttackType().spear) {
            pingin.getNavigation().stop();
        }
        if (hitDelay > 0 && !counter) {
            if (--hitDelay == 0) {
                if (target == hitTarget && target.isAlive() && visible) {
                    pingin.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
                    if (pingin.getAttackType() == PinginAttack.THROW) {
                        if (distanceSquared <= THROW_MAX * THROW_MAX) {
                            throwSpear(target);
                        }
                    } else if (close) {
                        pingin.doHurtTarget(target);
                    }
                }
                hitTarget = null;
            }
        } else if ((close || throwing) && (isTimeToAttack() && attackTicks == 0 || counter) && visible) {
            PinginAttack attack;
            if (counter) {
                attack = pingin.startAttackAnimation(PinginAttack.POKE);
            } else if (throwing) {
                attack = pingin.startAttackAnimation(PinginAttack.THROW);
            } else {
                attack = pingin.startAttackAnimation();
            }
            resetAttackCooldown();
            hitTarget = target;
            hitDelay = attack.hitTick;
            attackTicks = attack.duration + 2;
        }
        if (hitDelay == 0 && attackTicks > 0 && distanceSquared < reachSquared * 0.64D
                && pingin.onGround() && !pingin.isInWaterOrBubble()) {
            Vec3 step = pingin.position().subtract(target.position()).multiply(1D, 0D, 1D).normalize().scale(0.8D);
            BlockPos pos = BlockPos.containing(pingin.position().add(step));
            BlockPos nextPos = BlockPos.containing(pingin.position().add(step.scale(0.05D)));
            NodeEvaluator nodeEvaluator = pingin.getNavigation().getNodeEvaluator();
            if (pingin.level().getFluidState(pos).isEmpty()
                    && pingin.level().getBlockState(pos.below()).isFaceSturdy(pingin.level(), pos.below(), Direction.UP)
                    && nodeEvaluator.getBlockPathType(pingin.level(), pos.getX(), pos.getY(), pos.getZ()) == BlockPathTypes.WALKABLE
                    && nodeEvaluator.getBlockPathType(pingin.level(), nextPos.getX(), nextPos.getY(), nextPos.getZ()) == BlockPathTypes.WALKABLE
                    && pingin.level().noCollision(pingin, pingin.getBoundingBox().move(step))) {
                pingin.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
                pingin.getNavigation().stop();
                pingin.getMoveControl().strafe(-0.75F, 0F);
            }
        }
    }

    private boolean canThrowAt(LivingEntity target) {
        Vec3 start = pingin.position().add(0D, pingin.getBbHeight() + 0.1D, 0D), end = target.getEyePosition();
        return pingin.level().getEntitiesOfClass(LivingEntity.class, new AABB(start, end).inflate(0.6D),
                ally -> ally != pingin && ally != target && (ally instanceof PinginEntity || pingin.isAlliedTo(ally))).stream()
                .noneMatch(ally -> ally.getBoundingBox().inflate(0.6D).clip(start, end).isPresent());
    }

    private void throwSpear(LivingEntity target) {
        ItemStack stack = pingin.getMainHandItem();
        if (stack.getItem() instanceof ThrowingSpearItem && stack.getDamageValue() < stack.getMaxDamage() - 1 && canThrowAt(target)) {
            stack.hurtAndBreak(1, pingin, pingin -> pingin.broadcastBreakEvent(InteractionHand.MAIN_HAND));
            ItemStack thrown = stack.copy();
            thrown.setCount(1);
            ThrowingSpearProjectile spear = new ThrowingSpearProjectile(pingin.level(), pingin, thrown);
            Vec3 forward = pingin.getViewVector(1F).multiply(1D, 0D, 1D).normalize();
            Vec3 side = new Vec3(-forward.z, 0D, forward.x).scale(pingin.isLeftHanded() ? -0.3D : 0.3D);
            Vec3 origin = pingin.position().add(forward.scale(0.4D)).add(side).add(0D, pingin.getBbHeight() + 0.1D, 0D);
            spear.setPos(origin);
            double x = target.getX() - origin.x, z = target.getZ() - origin.z;
            double y = target.getY(0.5D) - origin.y + Math.sqrt(x * x + z * z) * 0.2D;
            spear.setThrowCharge(pingin.getAttackType().hitTick / (float) ThrowingSpearItem.FULL_CHARGE_TICKS);
            spear.shoot(x, y, z, 1.6F, 1F);
            if (pingin.level().addFreshEntity(spear)) {
                stack.shrink(1);
                pingin.playSound(SoundEvents.TRIDENT_THROW, 1F, 1F);
            }
        }
    }

    @Override
    protected double getAttackReachSqr(LivingEntity target) {
        double reach = 2D;
        if (pingin.getMainHandItem().getItem() instanceof ThrowingSpearItem spear) {
            reach = spear.getReach(pingin);
        }
        reach += target.getBbWidth() * 0.5D;
        return reach * reach;
    }

    @Override
    public void stop() {
        super.stop();
        hitDelay = 0;
        attackTicks = 0;
        hitTarget = null;
        if (!pingin.canFight() || pingin.getTarget() != null && !pingin.getTarget().isAlive()) {
            pingin.setTarget(null);
        }
    }
}

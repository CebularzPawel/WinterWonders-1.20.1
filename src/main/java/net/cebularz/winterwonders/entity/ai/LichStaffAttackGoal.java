package net.cebularz.winterwonders.entity.ai;

import net.cebularz.winterwonders.entity.custom.LichEntity;
import net.cebularz.winterwonders.item.custom.LichBlizzardStaffItem;
import net.cebularz.winterwonders.item.custom.impl.IStaffItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.Random;

public class LichStaffAttackGoal extends Goal {
    private final LichEntity lich;
    private final double speedModifier;
    private int attackInterval;
    private final int attackIntervalMin;
    private final int attackIntervalMax;
    private final float attackRadius;
    private int attackTime = -1;
    private int seeTime;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;
    private int chargeTime = 0;
    private static final int CHARGE_TIME_MAX = 60; // 3 seconds
    private AttackType currentAttackType = AttackType.BASIC_PROJECTILE;
    private int attackCycle = 0;
    private final Random random = new Random();

    public enum AttackType {
        BASIC_PROJECTILE,   // Snowball with freeze effect
        SPECIAL_ATTACK,     // Ice Spike Volley
        FLOOR_SPIKES,     // Floor Spikes
        FREEZING_CUBE,       // Ice Cubes
        BLIZZARD,           // Large AoE, Chilled effects
        WHIRLWIND           // Swirls player around and away with snowballs
    }

    public LichStaffAttackGoal(LichEntity pLich, double pSpeedModifier, int pAttackIntervalMin, int pAttackIntervalMax, float pAttackRadius) {
        this.lich = pLich;
        this.speedModifier = pSpeedModifier;
        this.attackIntervalMin = pAttackIntervalMin;
        this.attackIntervalMax = pAttackIntervalMax;
        this.attackRadius = pAttackRadius;
        this.attackInterval = pAttackIntervalMin;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.lich.getTarget();
        return target != null && target.isAlive() && this.lich.isTargetInRangedAttackRange(target);
    }

    @Override
    public boolean canContinueToUse() {
        return (this.canUse() || !this.lich.getNavigation().isDone()) &&
                !this.lich.isOnAttackCooldown();
    }

    @Override
    public void start() {
        super.start();
        chargeTime = 0;
        this.lich.setAggressive(true);
        this.attackTime = -1;

        attackCycle = (attackCycle + 1) % 4;

        selectAttackType();
    }

    private void selectAttackType() {
        LivingEntity target = this.lich.getTarget();
        if (target == null) return;

        double distance = this.lich.distanceToSqr(target);
        float lichHealth = this.lich.getHealth() / this.lich.getMaxHealth() * 100f;

        if (lichHealth <= 50) {
            if (distance < 36.0) { // Within 6 blocks
                currentAttackType = random.nextBoolean() ? AttackType.WHIRLWIND : AttackType.BLIZZARD;
            } else {
                switch (attackCycle) {
                    case 0:
                        currentAttackType = AttackType.BASIC_PROJECTILE; // Snowball (low damage, easy to apply)
                        break;
                    case 1:
                        currentAttackType = AttackType.SPECIAL_ATTACK;   // Ice Spike Volley (higher damage)
                        break;
                    case 2:
                        currentAttackType = distance < 100.0 ?
                                AttackType.FLOOR_SPIKES : AttackType.FREEZING_CUBE;
                        break;
                    case 3:
                        currentAttackType = AttackType.BLIZZARD;         // Large AoE with Chilled effect
                        break;
                }
            }
        } else {
            if (distance < 36.0) {
                currentAttackType = AttackType.WHIRLWIND;
            } else if (distance < 100.0) {
                switch (attackCycle) {
                    case 0:
                    case 2:
                        currentAttackType = AttackType.BASIC_PROJECTILE;
                        break;
                    case 1:
                        currentAttackType = AttackType.SPECIAL_ATTACK;
                        break;
                    case 3:
                        currentAttackType = AttackType.FLOOR_SPIKES;
                        break;
                }
            } else {
                switch (attackCycle) {
                    case 0:
                    case 2:
                        currentAttackType = AttackType.BASIC_PROJECTILE;
                        break;
                    case 1:
                        currentAttackType = AttackType.SPECIAL_ATTACK;
                        break;
                    case 3:
                        currentAttackType = AttackType.FREEZING_CUBE;
                        break;
                }
            }
        }
    }

    @Override
    public void stop() {
        super.stop();
        this.lich.setAggressive(false);
        this.seeTime = 0;
        this.attackTime = -1;
        chargeTime = 0;
        IStaffItem staffItem = this.lich.getStaffItem();
        staffItem.setChargeTime(0);
        this.lich.setAttackCooldown(0);
    }

    @Override
    public void tick() {
        LivingEntity target = this.lich.getTarget();
        if (target == null) return;

        IStaffItem staffItem = this.lich.getStaffItem();
        if (!(staffItem instanceof LichBlizzardStaffItem lichStaff)) return;
        lichStaff.setCaster(this.lich);

        double distanceToTargetSq = this.lich.distanceToSqr(target);
        boolean canSeeTarget = this.lich.getSensing().hasLineOfSight(target);
        boolean hasSeen = this.seeTime > 0;

        if (canSeeTarget != hasSeen) {
            this.seeTime = 0;
        }

        if (canSeeTarget) {
            ++this.seeTime;
        } else {
            --this.seeTime;
        }

        if (distanceToTargetSq <= (double)(this.attackRadius * this.attackRadius) && this.seeTime >= 20) {
            this.lich.getNavigation().stop();
            ++this.strafingTime;
        } else {
            this.lich.getNavigation().moveTo(target, this.speedModifier);
            this.strafingTime = -1;
        }

        if (this.strafingTime >= 20) {
            if ((double)this.lich.getRandom().nextFloat() < 0.3D) {
                this.strafingClockwise = !this.strafingClockwise;
            }

            if ((double)this.lich.getRandom().nextFloat() < 0.3D) {
                this.strafingBackwards = !this.strafingBackwards;
            }

            this.strafingTime = 0;
        }

        if (this.strafingTime > -1) {
            if (distanceToTargetSq > (double)(this.attackRadius * this.attackRadius * 0.75F)) {
                this.strafingBackwards = false;
            } else if (distanceToTargetSq < (double)(this.attackRadius * this.attackRadius * 0.25F)) {
                this.strafingBackwards = true;
            }

            this.lich.getMoveControl().strafe(this.strafingBackwards ? -0.5F : 0.5F, this.strafingClockwise ? 0.5F : -0.5F);
            this.lich.lookAt(target, 30.0F, 30.0F);
        } else {
            this.lich.getLookControl().setLookAt(target, 30.0F, 30.0F);
        }

        if (--this.attackTime == 0) {
            if (!canSeeTarget) {
                return;
            }

            if (chargeTime < CHARGE_TIME_MAX) {
                chargeTime++;
                staffItem.setChargeTime(chargeTime);
                return;
            }

            executeAttack(lichStaff, target);

            chargeTime = 0;
            staffItem.setChargeTime(0);

            this.attackTime = this.attackIntervalMin + this.lich.getRandom().nextInt(this.attackIntervalMax - this.attackIntervalMin);

            this.lich.setAttackCooldown(40);
        } else if (this.attackTime < 0) {
            float f = (float)Math.sqrt(distanceToTargetSq) / this.attackRadius;
            this.attackTime = Math.max(120, (int)(f * (float)(this.attackIntervalMax - this.attackIntervalMin) + (float)this.attackIntervalMin));
        }
    }

    private void executeAttack(LichBlizzardStaffItem lichStaff, LivingEntity target) {
        switch (currentAttackType) {
            case BASIC_PROJECTILE:
                lichStaff.executeSnowballVolley(target);
                break;
            case SPECIAL_ATTACK:
                lichStaff.executeSpecialAttack(target);
                break;
            case FLOOR_SPIKES:
                lichStaff.executeTerrainAttack(target, true);
                break;
            case FREEZING_CUBE:
                lichStaff.executeTerrainAttack(target, false);
                break;
            case BLIZZARD:
                lichStaff.executeBlizzardAttack(target);
                break;
            case WHIRLWIND:
                lichStaff.executeWhirlwindAttack();
                break;
        }
    }
}
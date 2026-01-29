package net.cebularz.winterwonders.entity.ai.lich;

import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.entity.custom.LichEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.Random;

public class LichAttackGoal extends Goal {
    private final LichEntity lich;
    private final double speedModifier;
    private final int attackIntervalMin;
    private final int attackIntervalMax;
    private final float attackRadius;

    private int attackTime = -1;
    private int seeTime;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;

    private AttackType currentAttackType = AttackType.BASIC_PROJECTILE;
    private int attackCycle = 0;

    private final Random random = new Random();
    private static final double MELEE_SQ = 32.0;
    private static final double SUMMON_MINIONS_SQ = 256.0;

    public enum AttackType {
        BASIC_PROJECTILE,
        SPECIAL_ATTACK,
        ICE_SPIKES,
        FREEZING_CUBE,
        BLIZZARD,
        WHIRLWIND,
        SUMMON_MINIONS
    }

    public LichAttackGoal(LichEntity lich, double speedModifier, int attackIntervalMin, int attackIntervalMax, float attackRadius) {
        this.lich = lich;
        this.speedModifier = speedModifier;
        this.attackIntervalMin = attackIntervalMin;
        this.attackIntervalMax = attackIntervalMax;
        this.attackRadius = attackRadius;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.lich.getTarget();
        boolean canUse = target != null
                && target.isAlive()
                && this.lich.isTargetInRangedAttackRange(target);

        if (!canUse && WinterWonders.LOGGER.isDebugEnabled()) {
            WinterWonders.LOGGER.debug(
                    "[Lich {}] canUse=false | target={} alive={} inRange={}",
                    lich.getId(),
                    target != null,
                    target != null && target.isAlive(),
                    target != null && this.lich.isTargetInRangedAttackRange(target)
            );
        }

        return canUse;
    }


    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public void start() {
        super.start();
        //WinterWonders.LOGGER.debug("[Lich {}] AttackGoal STARTED", lich.getId());

        this.lich.setAggressive(true);
        this.attackTime = -1;
        this.seeTime = 0;
        this.strafingTime = -1;

        attackCycle = (attackCycle + 1) & 3;
        selectAttackType();

        //WinterWonders.LOGGER.debug(
        //        "[Lich {}] Initial attackType={} cycle={}",
        //        lich.getId(),
        //        currentAttackType,
        //        attackCycle
        //);
    }

    @Override
    public void stop() {
        super.stop();
        //WinterWonders.LOGGER.debug("[Lich {}] AttackGoal STOPPED", lich.getId());

        this.lich.setAggressive(false);
        this.seeTime = 0;
        this.attackTime = -1;
    }


    private void selectAttackType() {
        LivingEntity target = this.lich.getTarget();
        if (target == null) return;

        final double distSq = this.lich.distanceToSqr(target);
        if (this.lich.hasPhased() && distSq >= SUMMON_MINIONS_SQ) {
            currentAttackType = AttackType.SUMMON_MINIONS;
            return;
        }

        if (distSq <= MELEE_SQ) {
            currentAttackType = random.nextBoolean() ? AttackType.WHIRLWIND : AttackType.ICE_SPIKES;
            return;
        }

        final boolean isRanged = distSq >= MELEE_SQ;
        final boolean enraged = lich.hasPhased();

        if (enraged) {
            switch (attackCycle) {
                case 0 -> currentAttackType = AttackType.BASIC_PROJECTILE;
                case 1 -> currentAttackType = AttackType.SPECIAL_ATTACK;
                case 2 -> currentAttackType = isRanged ? AttackType.SPECIAL_ATTACK : AttackType.ICE_SPIKES;
                case 3 -> currentAttackType = AttackType.BLIZZARD;
            }
        } else {
            switch (attackCycle) {
                case 0, 2 -> currentAttackType = AttackType.BASIC_PROJECTILE;
                case 1 -> currentAttackType = AttackType.SPECIAL_ATTACK;
                case 3 -> currentAttackType = isRanged ? AttackType.SPECIAL_ATTACK : AttackType.ICE_SPIKES;
            }
        }
    }

    @Override
    public void tick() {
        LivingEntity target = this.lich.getTarget();
        if (target == null) {
            //WinterWonders.LOGGER.debug("[Lich {}] tick() aborted: no target", lich.getId());
            return;
        }

        if (this.lich.tickCount % 20 == 0) {
            //WinterWonders.LOGGER.debug(
            //        "[Lich {}] distSq={} canSee={} seeTime={} attackTime={}",
            //        lich.getId(),
            //        this.lich.distanceToSqr(target),
            //        this.lich.getSensing().hasLineOfSight(target),
            //        this.seeTime,
            //        this.attackTime
            //);
        }

        if (this.lich.isOnCooldown()) {
            return;
        }

        double distanceToTargetSq = this.lich.distanceToSqr(target);
        boolean canSeeTarget = this.lich.getSensing().hasLineOfSight(target);
        boolean hasSeen = this.seeTime > 0;

        if (canSeeTarget != hasSeen) this.seeTime = 0;
        this.seeTime += canSeeTarget ? 1 : -1;

        if (distanceToTargetSq <= (double)(this.attackRadius * this.attackRadius) && this.seeTime >= 20) {
            this.lich.getNavigation().stop();
            ++this.strafingTime;
        } else {
            this.lich.getNavigation().moveTo(target, this.speedModifier);
            this.strafingTime = -1;
        }

        if (this.strafingTime >= 20) {
            if (this.lich.getRandom().nextFloat() < 0.3F) this.strafingClockwise = !this.strafingClockwise;
            if (this.lich.getRandom().nextFloat() < 0.3F) this.strafingBackwards = !this.strafingBackwards;
            this.strafingTime = 0;
        }

        if (this.strafingTime > -1) {
            double rSq = this.attackRadius * this.attackRadius;
            if (distanceToTargetSq > rSq * 0.75F) this.strafingBackwards = false;
            else if (distanceToTargetSq < rSq * 0.25F) this.strafingBackwards = true;

            this.lich.getMoveControl().strafe(
                    this.strafingBackwards ? -0.5F : 0.5F,
                    this.strafingClockwise ? 0.5F : -0.5F
            );
            this.lich.lookAt(target, 30.0F, 30.0F);
        } else {
            this.lich.getLookControl().setLookAt(target, 30.0F, 30.0F);
        }

        if (this.attackTime > 0) {
            this.attackTime--;
            return;
        }

        if (!canSeeTarget) {
            if (this.lich.tickCount % 20 == 0) {
                //WinterWonders.LOGGER.debug(
                //        "[Lich {}] Cast stalled: no LOS",
                //        lich.getId());
            }
            return;
        }

        //WinterWonders.LOGGER.info(
        //        "[Lich {}] CASTING {} at target {}",
        //        lich.getId(),
        //        currentAttackType,
        //        target.getId()
        //);

        lich.executeSpell(target, currentAttackType);
        this.attackTime = this.attackIntervalMin + this.lich.getRandom().nextInt(this.attackIntervalMax - this.attackIntervalMin);
        attackCycle = (attackCycle + 1) & 3;
        selectAttackType();
        this.lich.setAttackCooldown(40);
    }
}

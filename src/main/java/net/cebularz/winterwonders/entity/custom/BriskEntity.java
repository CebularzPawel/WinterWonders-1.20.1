package net.cebularz.winterwonders.entity.custom;

import net.cebularz.winterwonders.entity.custom.projectile.ChillingSnowballEntity;
import net.cebularz.winterwonders.entity.custom.projectile.IceSpikeProjectileEntity;
import net.cebularz.winterwonders.init.ModAttributes;
import net.cebularz.winterwonders.effect.ModEffects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.turtleboi.turtlecore.effect.CoreEffects;

import java.util.EnumSet;

public class BriskEntity extends Monster {
    private float allowedHeightOffset = 0.5F;
    private int nextHeightOffsetChangeTick;
    private static final EntityDataAccessor<Byte> DATA_FLAGS_ID;

    public BriskEntity(EntityType<? extends BriskEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE,8.0F);
        this.setPathfindingMalus(BlockPathTypes.LAVA,8.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER,-1.0F);
        this.setPathfindingMalus(BlockPathTypes.POWDER_SNOW,-1.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_POWDER_SNOW,-1.0F);
        this.xpReward = 25;
        this.setMaxUpStep(1.0F);
    }

    public AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    private void setupAnimationStates(){
        if(this.idleAnimationTimeout <= 0){
            this.idleAnimationTimeout = 40;
            this.idleAnimationState.start(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(4, new BriskEntity.BriskAttackGoal(this));
        this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0F));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0F, 0.0F));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.ATTACK_DAMAGE, 6.0F)
                .add(Attributes.MOVEMENT_SPEED, 0.175F)
                .add(Attributes.FOLLOW_RANGE, 48.0F)
                .add(ModAttributes.FROST_RESISTANCE.get(), 50.0F);
    }

    @Override
    public void aiStep() {
        if (!this.onGround() && this.getDeltaMovement().y < (double)0.0F) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0F, 0.25, 1.0F));
        }

        super.aiStep();
        if (this.level().isClientSide){
            setupAnimationStates();
            int particleMax = 2;

            if (isSuperCharged()){
                particleMax = 5;
            }

            for(int particles = 0; particles < particleMax; ++particles) {
                this.level().addParticle(
                        ParticleTypes.SNOWFLAKE,
                        this.getRandomX(0.5F),
                        this.getRandomY(),
                        this.getRandomZ(0.5F),
                        0.0F,
                        0.0F,
                        0.0F);
            }
        }
    }

    @Override
    protected void customServerAiStep() {
        --this.nextHeightOffsetChangeTick;
        if (this.nextHeightOffsetChangeTick <= 0) {
            this.nextHeightOffsetChangeTick = 100;
            this.allowedHeightOffset = (float)this.random.triangle(0.5F, 6.891);
        }

        LivingEntity target = this.getTarget();
        if (target != null && target.getEyeY() > this.getEyeY() + (double)this.allowedHeightOffset && this.canAttack(target)) {
            Vec3 deltaMovement = this.getDeltaMovement();
            this.setDeltaMovement(this.getDeltaMovement().add(0.0F, ((double)0.3F - deltaMovement.y) * (double)0.3F, 0.0F));
            this.hasImpulse = true;
        }

        super.customServerAiStep();
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        return false;
    }

    @Override
    public boolean isSensitiveToWater() {
        return false;
    }

    @Override
    public void lavaHurt() {
        super.lavaHurt();
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
    }

    @Override
    public float getLightLevelDependentMagicValue() {
        return 0.0F;
    }

    private boolean isSuperCharged(){
        return this.level().isRaining() && this.level().getBiome(this.getOnPos()).value().coldEnoughToSnow(this.getOnPos());
    }

    void setCharged(boolean pCharged) {
        byte chargedBoolean = this.entityData.get(DATA_FLAGS_ID);
        if (pCharged) {
            chargedBoolean = (byte)(chargedBoolean | 1);
        } else {
            chargedBoolean = (byte)(chargedBoolean & -2);
        }

        this.entityData.set(DATA_FLAGS_ID, chargedBoolean);
    }

    static {
        DATA_FLAGS_ID = SynchedEntityData.defineId(BriskEntity.class, EntityDataSerializers.BYTE);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.AMETHYST_CLUSTER_BREAK;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.EVOKER_CAST_SPELL;
    }

    static class BriskAttackGoal extends Goal {
        private final BriskEntity briskEntity;
        private int attackStep;
        private int attackTime;
        private int lastSeen;

        public BriskAttackGoal(BriskEntity pBlaze) {
            this.briskEntity = pBlaze;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity target = this.briskEntity.getTarget();
            return target != null && target.isAlive() && this.briskEntity.canAttack(target);
        }

        public void start() {
            this.attackStep = 0;
        }

        public void stop() {
            this.briskEntity.setCharged(false);
            this.lastSeen = 0;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            --this.attackTime;
            LivingEntity target = this.briskEntity.getTarget();
            if (target != null) {
                boolean hasLineOfSight = this.briskEntity.getSensing().hasLineOfSight(target);
                if (hasLineOfSight) {
                    this.lastSeen = 0;
                } else {
                    ++this.lastSeen;
                }

                double distanceToSqr = this.briskEntity.distanceToSqr(target);
                if (distanceToSqr < (double) 4.0F) {
                    if (!hasLineOfSight) {
                        return;
                    }

                    if (this.attackTime <= 0) {
                        this.attackTime = 20;
                        this.briskEntity.doHurtTarget(target);
                    }

                    this.briskEntity.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 1.0F);
                } else if (distanceToSqr < this.getFollowDistance() * this.getFollowDistance() && hasLineOfSight) {
                    double toTargetX = target.getX() - this.briskEntity.getX();
                    double toTargetY = target.getEyeY() - briskEntity.getBoundingBox().getCenter().y;
                    double toTargetZ = target.getZ() - this.briskEntity.getZ();
                    if (this.attackTime <= 0) {
                        ++this.attackStep;

                        int attackMax = 5;
                        int attackInterval = 20;

                        if (briskEntity.isSuperCharged()){
                            attackMax = 9;
                            attackInterval = 10;
                        }

                        if (this.attackStep == 1) {
                            this.attackTime = 60;
                            this.briskEntity.setCharged(true);
                        } else if (this.attackStep <= attackMax) {
                            this.attackTime = attackInterval;
                        } else {
                            this.attackTime = 100;
                            this.attackStep = 0;
                            this.briskEntity.setCharged(false);
                        }

                        if (this.attackStep > 1) {
                            if (!this.briskEntity.isSilent()) {
                                this.briskEntity.level().playSound(null, this.briskEntity.getX(), this.briskEntity.getY(), this.briskEntity.getZ(),
                                        SoundEvents.EVOKER_CAST_SPELL, SoundSource.HOSTILE, 1.25f, 0.4f / (this.briskEntity.level().getRandom().nextFloat() * 0.4f + 0.8f));
                            }

                            int chilledAmplifier = target.hasEffect(CoreEffects.CHILLED.get()) ? target.getEffect(CoreEffects.CHILLED.get()).getAmplifier() : 0;

                            for (int projectileCount = 0; projectileCount < 1; ++projectileCount) {
                                Vec3 targetDirection = new Vec3(toTargetX, toTargetY, toTargetZ).normalize();

                                double chanceForIceSpike = 0.1 + (0.2 * chilledAmplifier);
                                boolean shootIceSpike = briskEntity.getRandom().nextDouble() < chanceForIceSpike || chilledAmplifier > 3;

                                if (shootIceSpike) {
                                    IceSpikeProjectileEntity iceSpike = new IceSpikeProjectileEntity(briskEntity.level(), briskEntity);
                                    iceSpike.setPos(this.briskEntity.getX(), this.briskEntity.getY() + 1.0, this.briskEntity.getZ());
                                    iceSpike.setDeltaMovement(targetDirection.scale(2.0));
                                    this.briskEntity.level().addFreshEntity(iceSpike);
                                } else {
                                    ChillingSnowballEntity chillingSnowball = new ChillingSnowballEntity(briskEntity.level(), briskEntity);
                                    chillingSnowball.setPos(this.briskEntity.getX(), this.briskEntity.getY() + 1.0, this.briskEntity.getZ());
                                    chillingSnowball.setDeltaMovement(targetDirection.scale(2.0));
                                    this.briskEntity.level().addFreshEntity(chillingSnowball);
                                }
                            }
                        }
                    }

                    this.briskEntity.getLookControl().setLookAt(target, 10.0F, 10.0F);
                } else if (this.lastSeen < 5) {
                    this.briskEntity.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 1.0F);
                }

                super.tick();
            }
        }

        private double getFollowDistance() {
            return this.briskEntity.getAttributeValue(Attributes.FOLLOW_RANGE);
        }
    }
}

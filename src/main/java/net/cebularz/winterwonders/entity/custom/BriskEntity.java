package net.cebularz.winterwonders.entity.custom;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

public class BriskEntity extends Monster {
    private static final EntityDataAccessor<Byte> DATA_FLAGS_ID;

    public BriskEntity(EntityType<? extends BriskEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE,-1.0F);
        this.setPathfindingMalus(BlockPathTypes.LAVA,-1.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER,8.0F);
        this.setPathfindingMalus(BlockPathTypes.POWDER_SNOW,8.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_POWDER_SNOW,2.0F);
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
                .add(Attributes.FOLLOW_RANGE, 48.0F);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide){
            setupAnimationStates();

            for(int particles = 0; particles < 2; ++particles) {
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

    void setCharged(boolean pCharged) {
        byte $$1 = (Byte)this.entityData.get(DATA_FLAGS_ID);
        if (pCharged) {
            $$1 = (byte)($$1 | 1);
        } else {
            $$1 = (byte)($$1 & -2);
        }

        this.entityData.set(DATA_FLAGS_ID, $$1);
    }

    static {
        DATA_FLAGS_ID = SynchedEntityData.defineId(BriskEntity.class, EntityDataSerializers.BYTE);
    }
}

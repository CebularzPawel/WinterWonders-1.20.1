package net.cebularz.winterwonders.entity.custom;

import net.cebularz.winterwonders.init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

public class SnowWeaselEntity extends Animal {
    public SnowWeaselEntity(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.0D));

        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.0D, Ingredient.of(Items.RABBIT), false));

        this.goalSelector.addGoal(4, new FollowParentGoal(this, 0.7D));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 0.75D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 3f));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    public AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    @Override
    protected void updateWalkAnimation(float pPartialTick) {
        float f;
        if(this.getPose() == Pose.STANDING) {
            f = Math.min(pPartialTick * 6F, 1f);
        } else {
            f = 0f;
        }

        this.walkAnimation.update(f, 0.2f);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide){
            setupAnimationStates();
        }
    }

    private void setupAnimationStates(){
        if(this.idleAnimationTimeout <= 0){
            this.idleAnimationTimeout = 80;
            this.idleAnimationState.start(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }
    }

    public static AttributeSupplier.Builder createAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.5D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return ModEntities.SNOW_WEASEL.get().create(serverLevel);
    }

    @Override
    public boolean isFood(ItemStack pStack) {
        return pStack.is(Items.RABBIT);
    }

    public static boolean canSpawn(EntityType<SnowWeaselEntity> entityType, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random){
        return checkMobSpawnRules(entityType, level, spawnType, pos, random);
    }
}

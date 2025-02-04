package net.turtleboi.winterwonders.entity.custom;

import net.minecraft.core.BlockPos;
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
import net.turtleboi.winterwonders.entity.ai.SlideOnIceGoal;
import net.turtleboi.winterwonders.init.ModEntities;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.entity.monster.Monster.isDarkEnoughToSpawn;

public class PinginEntity extends Animal {
    public PinginEntity(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 2.0D));

        this.goalSelector.addGoal(2, new BreedGoal(this, 1.15D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.15D, Ingredient.of(Items.SALMON), false));

        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));

        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 0.75D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 3f));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.goalSelector.addGoal(8, new SlideOnIceGoal(this, 1.2D));
    }

    public AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    public AnimationState slideAnimationState = new AnimationState();
    public int slideTimer = 0;
    public final int slideDuration = 40;

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
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide){
            setupAnimationStates();
        }
    }

    private void setupAnimationStates(){
        if(this.idleAnimationTimeout <= 0){
            this.idleAnimationTimeout = 40;
            this.idleAnimationState.start(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }

        if (this.slideTimer <= 0 && this.isOnIce() && this.random.nextFloat() < 0.02F) {
            this.slideTimer = slideDuration;
            this.slideAnimationState.start(this.tickCount);
        }

        if (this.slideTimer > 0) {
            this.slideTimer--;
            if (this.slideTimer <= 0) {
                this.slideAnimationState.stop();
            }
        }
    }

    public static AttributeSupplier.Builder createAttributes(){
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 12D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 24D);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return ModEntities.PINGIN.get().create(serverLevel);
    }

    @Override
    public boolean isFood(ItemStack pStack) {
        return pStack.is(Items.SALMON);
    }

    public void startSliding(int duration) {
        this.slideTimer = duration;
    }

    public static boolean canSpawn(EntityType<PinginEntity> entityType, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random){
        return checkMobSpawnRules(entityType, level, spawnType, pos, random);
    }

    public boolean isOnIce() {
        BlockPos pos = this.blockPosition().below();
        return this.level().getBlockState(pos).is(net.minecraft.world.level.block.Blocks.ICE)
                || this.level().getBlockState(pos).is(net.minecraft.world.level.block.Blocks.PACKED_ICE)
                || this.level().getBlockState(pos).is(net.minecraft.world.level.block.Blocks.BLUE_ICE);
    }

    public boolean isSliding() {
        return this.slideTimer > 0;
    }
}

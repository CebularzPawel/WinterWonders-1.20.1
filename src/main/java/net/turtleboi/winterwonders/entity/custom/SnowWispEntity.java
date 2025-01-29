package net.turtleboi.winterwonders.entity.custom;

import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.turtleboi.winterwonders.entity.ai.SnowWispAI;
import net.turtleboi.winterwonders.init.ModParticles;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

import static net.minecraft.world.entity.monster.Monster.checkMonsterSpawnRules;
import static net.minecraft.world.entity.monster.Monster.isDarkEnoughToSpawn;

public class SnowWispEntity extends PathfinderMob {
    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(SnowWispEntity.class, EntityDataSerializers.INT);
    private int startColor;
    private int endColor;
    private float colorTransition = 0.0F;

    public SnowWispEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.setPathfindingMalus(BlockPathTypes.LAVA, 8.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);

        this.startColor = POSSIBLE_COLORS[this.random.nextInt(POSSIBLE_COLORS.length)];
        do {
            this.endColor = POSSIBLE_COLORS[this.random.nextInt(POSSIBLE_COLORS.length)];
        } while (this.endColor == this.startColor);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(COLOR, 0xFFFFFF); // default
    }

    public AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    private BlockPos lastLightPos = null;

    @Override
    protected void updateWalkAnimation(float pPartialTick) {
        super.updateWalkAnimation(pPartialTick);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED,0.5D)
                .add(Attributes.MAX_HEALTH, 4D)
                .add(Attributes.FLYING_SPEED, 0.5D);
    }

    protected @NotNull PathNavigation createNavigation(@NotNull Level pLevel) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, pLevel);
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(true);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }

    @Override
    protected @NotNull Brain<?> makeBrain(@NotNull Dynamic<?> pDynamic) {
        return SnowWispAI.makeBrain(this,pDynamic);
    }

    @Override
    public Brain<SnowWispEntity> getBrain() {
        return (Brain<SnowWispEntity>) super.getBrain();
    }

    protected void customServerAiStep() {
        ServerLevel level = (ServerLevel)this.level();
        level.getProfiler().push("wispBrain");
        this.getBrain().tick(level, this);
        this.level().getProfiler().pop();
        this.level().getProfiler().push("wispUpdateActivity");
        SnowWispAI.updateActivity(this);
        this.level().getProfiler().pop();
        super.customServerAiStep();
    }

    @Override
    public void tick() {
        if (this.level().isClientSide){
            setupAnimationStates();
        }
        super.tick();
    }

    private void setupAnimationStates(){
        if(this.idleAnimationTimeout <= 0){
            this.idleAnimationTimeout = this.random.nextInt(40) + 160;
            this.idleAnimationState.start(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }
    }

    private static final int[] POSSIBLE_COLORS = new int[] {
            0xD0B9F3,
            0xFFFCC2,
            0xFDD6BD,
            0xFFC2D9,
            0xC8FFF8
    };

    private static int blendColor(int colorA, int colorB, float progress) {
        int rA = (colorA >> 16) & 0xFF;
        int gA = (colorA >> 8)  & 0xFF;
        int bA =  colorA        & 0xFF;

        int rB = (colorB >> 16) & 0xFF;
        int gB = (colorB >> 8)  & 0xFF;
        int bB =  colorB        & 0xFF;

        int r = (int) (rA + (rB - rA) * progress);
        int g = (int) (gA + (gB - gA) * progress);
        int b = (int) (bA + (bB - bA) * progress);

        return (r << 16) | (g << 8) | b;
    }

    @Override
    protected void checkFallDamage(double pY, boolean pOnGround, BlockState pState, BlockPos pPos) {

    }

    @Override
    protected boolean isFlapping() {
        return !this.onGround();
    }


    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            this.colorTransition += 0.01F;
            if (this.colorTransition >= 1.0F) {
                this.startColor = this.endColor;
                this.colorTransition = 0.0F;
                int newColor;
                do {
                    newColor = POSSIBLE_COLORS[this.random.nextInt(POSSIBLE_COLORS.length)];
                } while (newColor == this.startColor);
                this.endColor = newColor;
            }
            int newColor = blendColor(this.startColor, this.endColor, this.colorTransition);
            this.entityData.set(COLOR, newColor);

            BlockPos currentPos = this.blockPosition();
            if (this.lastLightPos != null && !this.lastLightPos.equals(currentPos)) {
                if (this.level().getBlockState(this.lastLightPos).is(Blocks.LIGHT)) {
                    this.level().setBlock(this.lastLightPos, Blocks.AIR.defaultBlockState(), 3);
                }
            }

            if (this.level().getBlockState(currentPos).isAir()) {
                this.level().setBlock(
                        currentPos,
                        Blocks.LIGHT.defaultBlockState().setValue(BlockStateProperties.LEVEL, 11),
                        3
                );
                this.lastLightPos = currentPos;
            }
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        if (this.lastLightPos != null
                && this.level().getBlockState(this.lastLightPos).is(Blocks.LIGHT)) {
            this.level().setBlock(this.lastLightPos, Blocks.AIR.defaultBlockState(), 3);
        }
    }

    public int getColorMultiplier() {
        return this.entityData.get(COLOR);
    }

    public static boolean canSpawn(EntityType<SnowWispEntity> entityType, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random){
        return isDarkEnoughToSpawn(level, pos, random) && checkMobSpawnRules(entityType, level, spawnType, pos, random);
    }
}

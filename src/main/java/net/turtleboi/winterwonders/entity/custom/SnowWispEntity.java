package net.turtleboi.winterwonders.entity.custom;

import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
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

public class SnowWispEntity extends Monster {
    private int startColor;
    private int endColor;
    private float colorTransition = 0.0F;
    private int currentColor = 0xFFFFFF;

    public SnowWispEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
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

    public AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    private BlockPos lastLightPos = null;
    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide){
            setupAnimationStates();

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
            this.currentColor = blendColor(this.startColor, this.endColor, this.colorTransition);
        }
    }

    private void setupAnimationStates(){
        if(this.idleAnimationTimeout <= 0){
            this.idleAnimationTimeout = this.random.nextInt(40) +80;
            this.idleAnimationState.start(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }
    }

    private static final int[] POSSIBLE_COLORS = new int[] {
            0xD0F4F9,
            0xD0F9DF,
            0xF9D0F7,
            0xF9F7D0
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

    //This is so that it would be passive but spawn at night
    @Override
    public boolean doHurtTarget(@NotNull Entity pEntity) {
        return !(pEntity instanceof Player);
    }

    @Override
    public boolean canAttack(@NotNull LivingEntity pTarget) {
        return !(pTarget instanceof Player);
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
    protected void checkFallDamage(double pY, boolean pOnGround, BlockState pState, BlockPos pPos) {

    }

    @Override
    protected boolean isFlapping() {
        return !this.onGround();
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.onGround()) {
            double entityX = getX();
            double entityY = getY() - 0.5;
            double entityZ = getZ();

            double spreadX = (random.nextDouble() - 0.5) * 0.4;
            double spreadZ = (random.nextDouble() - 0.5) * 0.4;

            if (this.level().isClientSide) {
                this.level().addParticle(
                        ModParticles.AURORA_PARTICLE.get(),
                        true,
                        entityX + spreadX,
                        entityY,
                        entityZ + spreadZ,
                        0, -0.05, 0
                );
            }
        }

        if (!this.level().isClientSide) {
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
        return currentColor;
    }

    public static boolean canSpawn(EntityType<SnowWispEntity> entityType, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random){
        return checkMonsterSpawnRules(entityType, level, spawnType, pos, random);
    }
}

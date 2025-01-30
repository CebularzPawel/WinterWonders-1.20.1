package net.turtleboi.winterwonders.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static net.minecraft.world.entity.monster.Monster.isDarkEnoughToSpawn;

public class SnowWispEntity extends PathfinderMob {
    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(SnowWispEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_PLAYING = SynchedEntityData.defineId(SnowWispEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> LIKES_PLAYER = SynchedEntityData.defineId(SnowWispEntity.class, EntityDataSerializers.BOOLEAN);
    private int startColor;
    private int endColor;
    private float colorTransition = 0.0F;

    //Playing things
    private Player targetPlayer;
    private int playingTicks = 0;
    private static final int PLAYING_DURATION = 200;

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
        this.entityData.define(IS_PLAYING, false);
        this.entityData.define(LIKES_PLAYER, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SnowWispPlayGoal(this));
        this.goalSelector.addGoal(3, new SnowWispFollowPlayerGoal(this, 1.0D, 2.0F, 10.0F));
        this.goalSelector.addGoal(4, new SnowWispGroupBehaviorGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
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
    public void tick() {
        if (this.level().isClientSide){
            setupAnimationStates();
        }

        if (this.getNavigation().isDone()) {
            this.setDeltaMovement(this.getDeltaMovement().scale(0.7));
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

    @Override
    public @NotNull InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.isPlaying() && player.hasGlowingTag() && player == this.targetPlayer) {
            player.removeEffect(MobEffects.GLOWING);
            this.setPlaying(false);
            this.setLikesPlayer(true);
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    // Helper methods
    public boolean isPlaying() {
        return this.entityData.get(IS_PLAYING);
    }

    public void setPlaying(boolean playing) {
        this.entityData.set(IS_PLAYING, playing);
    }

    public boolean likesPlayer() {
        return this.entityData.get(LIKES_PLAYER);
    }

    public void setLikesPlayer(boolean likes) {
        this.entityData.set(LIKES_PLAYER, likes);
    }

    // Group behavior goal
    private static class SnowWispGroupBehaviorGoal extends Goal {
        private final SnowWispEntity wisp;
        private final double speedModifier;
        private SnowWispEntity leader;
        private int groupUpdateTime;

        public SnowWispGroupBehaviorGoal(SnowWispEntity wisp, double speedModifier) {
            this.wisp = wisp;
            this.speedModifier = speedModifier;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (--this.groupUpdateTime <= 0) {
                this.groupUpdateTime = 20;
                List<SnowWispEntity> nearbyWisps = this.wisp.level().getEntitiesOfClass(
                        SnowWispEntity.class,
                        this.wisp.getBoundingBox().inflate(8.0D),
                        entity -> entity != this.wisp
                );

                if (!nearbyWisps.isEmpty()) {
                    this.leader = nearbyWisps.get(this.wisp.getRandom().nextInt(nearbyWisps.size()));
                    return !this.leader.isPlaying() && !this.wisp.isPlaying();
                }
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return this.leader != null && this.leader.isAlive() &&
                    !this.leader.isPlaying() && !this.wisp.isPlaying();
        }

        @Override
        public void tick() {
            if (this.wisp.distanceTo(this.leader) > 3.0D) {
                this.wisp.getNavigation().moveTo(this.leader, this.speedModifier);
            }
        }
    }

    // Play with player goal
    private static class SnowWispPlayGoal extends Goal {
        private final SnowWispEntity wisp;
        private Player targetPlayer;
        private int cooldown = 0;

        public SnowWispPlayGoal(SnowWispEntity wisp) {
            this.wisp = wisp;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (this.wisp.isPlaying() || cooldown > 0 || this.wisp.likesPlayer()) {
                if (cooldown > 0) {
                    cooldown--;
                }
                return false;
            }

            if (this.wisp.getRandom().nextFloat() >= 0.2F) {
                return false;
            }

            List<Player> nearbyPlayers = this.wisp.level().getEntitiesOfClass(
                    Player.class,
                    this.wisp.getBoundingBox().inflate(8.0D)
            );

            if (!nearbyPlayers.isEmpty()) {
                this.targetPlayer = nearbyPlayers.get(this.wisp.getRandom().nextInt(nearbyPlayers.size()));
                return true;
            }

            return false;
        }

        @Override
        public void start() {
            if (this.targetPlayer != null) {
                this.wisp.setPlaying(true);
                this.wisp.targetPlayer = this.targetPlayer;
            }
        }

        @Override
        public void tick() {
            if (this.targetPlayer == null || !this.targetPlayer.isAlive()) {
                this.stop();
                return;
            }

            if (this.wisp.distanceTo(this.targetPlayer) <= 1.0D && !this.targetPlayer.hasGlowingTag()) {
                this.targetPlayer.addEffect(new MobEffectInstance(MobEffects.GLOWING, 100, 0));
            } else {
                this.wisp.getNavigation().moveTo(this.targetPlayer, 1.2D);
            }
        }

        @Override
        public boolean canContinueToUse() {
            return this.wisp.isPlaying() &&
                    this.targetPlayer != null &&
                    this.targetPlayer.isAlive() &&
                    !this.wisp.likesPlayer();
        }

        @Override
        public void stop() {
            this.wisp.setPlaying(false);
            this.wisp.targetPlayer = null;
            cooldown = 100;
        }
    }

    // Follow liked player goal
    private static class SnowWispFollowPlayerGoal extends Goal {
        private final SnowWispEntity wisp;
        private final double speedModifier;
        private final float minDist;
        private final float maxDist;
        private Player player;

        public SnowWispFollowPlayerGoal(SnowWispEntity wisp, double speedModifier, float minDist, float maxDist) {
            this.wisp = wisp;
            this.speedModifier = speedModifier;
            this.minDist = minDist;
            this.maxDist = maxDist;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (!this.wisp.likesPlayer() || this.wisp.isPlaying()) {
                return false;
            }

            List<Player> nearbyPlayers = this.wisp.level().getEntitiesOfClass(
                    Player.class,
                    this.wisp.getBoundingBox().inflate(this.maxDist)
            );

            for (Player nearbyPlayer : nearbyPlayers) {
                double distance = this.wisp.distanceTo(nearbyPlayer);
                if (distance >= this.minDist && distance <= this.maxDist) {
                    this.player = nearbyPlayer;
                    return true;
                }
            }

            return false;
        }

        @Override
        public void tick() {
            this.wisp.getLookControl().setLookAt(this.player, 10.0F, this.wisp.getMaxHeadXRot());
            if (this.wisp.distanceTo(this.player) >= this.minDist) {
                this.wisp.getNavigation().moveTo(this.player, this.speedModifier);
            }
        }

        @Override
        public boolean canContinueToUse() {
            return this.player != null && this.player.isAlive() &&
                    this.wisp.likesPlayer() && !this.wisp.isPlaying() &&
                    this.wisp.distanceTo(this.player) <= this.maxDist;
        }
    }


}

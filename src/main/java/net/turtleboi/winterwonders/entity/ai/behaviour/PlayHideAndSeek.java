package net.turtleboi.winterwonders.entity.ai.behaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PlayHideAndSeek<E extends PathfinderMob> extends Behavior<E> {
    private static final double MOVEMENT_SPEED = 1.9;
    private static final int SEARCH_RADIUS = 10;
    private static final int SEARCH_HEIGHT = 3;
    private static final Random RANDOM = new Random();
    private static final float PLAY_CHANCE = 0.2F;
    private enum GamePhase {
        WANDERING,
        SEEKING_HIDING_SPOT,
        HIDING,
        RUNNING_TO_PLAYER,
        BEFRIENDED,
        GAME_OVER
    }

    private GamePhase currentPhase = GamePhase.SEEKING_HIDING_SPOT;
    private Player targetPlayer = null;
    private BlockPos hidingSpot = null;
    private int hideTime = 0;
    private static final int MAX_HIDE_TIME = 200; // 10 seconds

    public PlayHideAndSeek(Map<MemoryModuleType<?>, MemoryStatus> pEntryCondition) {
        super(pEntryCondition);
    }

    @Override
    protected boolean checkExtraStartConditions(@NotNull ServerLevel pLevel, E pOwner) {
        boolean shouldPlay = pOwner.getBrain().hasMemoryValue(MemoryModuleType.NEAREST_VISIBLE_PLAYER) &&
                !pOwner.getBrain().hasMemoryValue(MemoryModuleType.LIKED_PLAYER) &&
                RANDOM.nextFloat() < PLAY_CHANCE;
        if (!shouldPlay) {
            startWandering(pOwner);
        }
        return shouldPlay;
    }


    @Override
    protected void start(@NotNull ServerLevel pLevel, E pEntity, long pGameTime) {
        targetPlayer = pEntity.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER).orElse(null);

        if (targetPlayer != null && RANDOM.nextFloat() < PLAY_CHANCE) {
            notifyPlayer(targetPlayer, "A snow wisp wants to play hide and seek! Try to find it!");
            findHidingSpot(pLevel, pEntity);
        } else {
            currentPhase = GamePhase.WANDERING;
        }
    }

    private void findHidingSpot(ServerLevel level, E entity) {
        List<BlockPos> potentialSpots = new ArrayList<>();
        BlockPos entityPos = entity.blockPosition();

        for (int x = -SEARCH_RADIUS; x <= SEARCH_RADIUS; x++) {
            for (int y = -SEARCH_HEIGHT; y <= SEARCH_HEIGHT; y++) {
                for (int z = -SEARCH_RADIUS; z <= SEARCH_RADIUS; z++) {
                    BlockPos pos = entityPos.offset(x, y, z);
                    if (isGoodHidingSpot(level, pos)) {
                        potentialSpots.add(pos);
                    }
                }
            }
        }

        if (!potentialSpots.isEmpty()) {
            hidingSpot = potentialSpots.get(RANDOM.nextInt(potentialSpots.size()));
            currentPhase = GamePhase.SEEKING_HIDING_SPOT;
        }
    }

    private boolean isGoodHidingSpot(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isSolid() &&
                level.getBlockState(pos.above()).isAir() &&
                level.getBlockState(pos.above(2)).isAir();
    }

    @Override
    protected void tick(@NotNull ServerLevel pLevel, @NotNull E pEntity, long pGameTime) {
        if (currentPhase == GamePhase.GAME_OVER || targetPlayer == null || !targetPlayer.isAlive()) {
            startWandering(pEntity);
            return;
        }

        switch (currentPhase) {
            case SEEKING_HIDING_SPOT -> {
                if (hidingSpot != null) {
                    Vec3 hidePos = Vec3.atCenterOf(hidingSpot.above());
                    pEntity.getNavigation().moveTo(hidePos.x, hidePos.y, hidePos.z, MOVEMENT_SPEED);
                    if (pEntity.position().distanceToSqr(hidePos) < 2.0) {
                        currentPhase = GamePhase.HIDING;
                        hideTime = 0;
                    }
                }
            }
            case HIDING -> {
                hideTime++;
                if (pEntity.distanceToSqr(targetPlayer) < 3.0 * 3.0 || hideTime > MAX_HIDE_TIME) {
                    currentPhase = GamePhase.RUNNING_TO_PLAYER;
                    notifyPlayer(targetPlayer, "The snow wisp has been found! Right-click to befriend it!");
                    playEffects(pLevel, pEntity, true);
                }
            }
            case RUNNING_TO_PLAYER -> {
                pEntity.getNavigation().moveTo(targetPlayer, MOVEMENT_SPEED);
                if (targetPlayer.distanceToSqr(pEntity) < 2.0 * 2.0) {
                    currentPhase = GamePhase.BEFRIENDED;
                }
            }
            case BEFRIENDED -> {
                if (targetPlayer.distanceToSqr(pEntity) < 2.0 * 2.0) {
                    pEntity.getBrain().setMemory(MemoryModuleType.LIKED_PLAYER, targetPlayer.getUUID());
                    notifyPlayer(targetPlayer, "The snow wisp is now your friend!");
                    playEffects(pLevel, pEntity, false);
                    currentPhase = GamePhase.GAME_OVER;
                }
            }
            case WANDERING -> startWandering(pEntity);
            case GAME_OVER -> stopGame(pLevel, pEntity);
        }
    }

    private void playEffects(ServerLevel level, E entity, boolean isFound) {
        level.sendParticles(ParticleTypes.SNOWFLAKE,
                entity.getX(), entity.getY() + 1, entity.getZ(),
                10, 0.5, 0.5, 0.5, 0.1);
        entity.playSound(isFound ? SoundEvents.SNOW_BREAK : SoundEvents.PLAYER_LEVELUP, 1.0F, 1.0F);
    }

    private void notifyPlayer(Player player, String message) {
        player.displayClientMessage(Component.literal(message), true);
    }

    private void startWandering(E pEntity) {
        Vec3 randomPos = getRandomNearbyPosition(pEntity);
        if (randomPos != null) {
            pEntity.getNavigation().moveTo(randomPos.x, randomPos.y, randomPos.z, MOVEMENT_SPEED / 2);
        }
    }

    private Vec3 getRandomNearbyPosition(E pEntity) {
        BlockPos entityPos = pEntity.blockPosition();
        for (int i = 0; i < 10; i++) {
            int offsetX = RANDOM.nextInt(SEARCH_RADIUS * 2) - SEARCH_RADIUS;
            int offsetZ = RANDOM.nextInt(SEARCH_RADIUS * 2) - SEARCH_RADIUS;
            int offsetY = RANDOM.nextInt(SEARCH_HEIGHT * 2) - SEARCH_HEIGHT;

            BlockPos randomPos = entityPos.offset(offsetX, offsetY, offsetZ);
            if (pEntity.level().getBlockState(randomPos).isAir() && pEntity.level().getBlockState(randomPos.below()).isSolid()) {
                return Vec3.atCenterOf(randomPos);
            }
        }
        return null;
    }

    private void stopGame(ServerLevel level, E entity) {
        entity.getNavigation().stop();
        entity.removeEffect(MobEffects.GLOWING);
        currentPhase = GamePhase.GAME_OVER;
    }

    @Override
    protected void stop(@NotNull ServerLevel pLevel, @NotNull E pEntity, long pGameTime) {
        stopGame(pLevel, pEntity);
    }

    @Override
    protected boolean canStillUse(@NotNull ServerLevel pLevel, @NotNull E pEntity, long pGameTime) {
        return currentPhase != GamePhase.GAME_OVER && targetPlayer != null && targetPlayer.isAlive();
    }
}
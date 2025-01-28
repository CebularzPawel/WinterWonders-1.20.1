package net.turtleboi.winterwonders.entity.ai.behaviour;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.UUID;

public class FollowLikedPlayer<E extends PathfinderMob> extends Behavior<E> {
    private static final double FOLLOW_SPEED = 1.2;
    private static final double MIN_FOLLOW_DISTANCE = 2.0;
    private static final double MAX_FOLLOW_DISTANCE = 12.0;

    public FollowLikedPlayer() {
        super(Map.of(
                MemoryModuleType.LIKED_PLAYER, MemoryStatus.VALUE_PRESENT
        ));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        return entity.getBrain().hasMemoryValue(MemoryModuleType.LIKED_PLAYER);
    }

    @Override
    protected boolean canStillUse(ServerLevel level, E entity, long gameTime) {
        return checkExtraStartConditions(level, entity);
    }

    @Override
    protected void tick(ServerLevel level, E entity, long gameTime) {
        UUID likedPlayerUUID = entity.getBrain().getMemory(MemoryModuleType.LIKED_PLAYER).get();
        Player likedPlayer = level.getPlayerByUUID(likedPlayerUUID);

        if (likedPlayer != null && likedPlayer.isAlive()) {
            double distance = entity.distanceToSqr(likedPlayer);
            Vec3 likedPlayerDelta = new Vec3(likedPlayer.getX(),likedPlayer.getEyeY(),likedPlayer.getZ());
            if (distance > MIN_FOLLOW_DISTANCE * MIN_FOLLOW_DISTANCE &&
                    distance < MAX_FOLLOW_DISTANCE * MAX_FOLLOW_DISTANCE) {
                entity.getNavigation().moveTo(likedPlayerDelta.x(),likedPlayerDelta.y(),likedPlayerDelta.z(), FOLLOW_SPEED);
            } else if (distance >= MAX_FOLLOW_DISTANCE * MAX_FOLLOW_DISTANCE) {
                entity.teleportTo(
                        likedPlayer.getX() + (Math.random() * 2 - 1),
                        likedPlayer.getY(),
                        likedPlayer.getZ() + (Math.random() * 2 - 1)
                );
            } else {
                entity.getNavigation().stop();
            }
        }
    }

    @Override
    protected void stop(ServerLevel level, E entity, long gameTime) {
        entity.getNavigation().stop();
    }
}
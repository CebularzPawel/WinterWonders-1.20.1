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
    private static final double ROTATION_SPEED = 0.15;
    private static final double PATH_RECALCULATION_THRESHOLD = 0.5;

    private Vec3 lastTargetPos = null;
    private int pathfindingCooldown = 0;

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
            Vec3 currentTargetPos = new Vec3(likedPlayer.getX(), likedPlayer.getEyeY(), likedPlayer.getZ());
            double distance = entity.distanceToSqr(likedPlayer);

            if (distance > MIN_FOLLOW_DISTANCE * MIN_FOLLOW_DISTANCE &&
                    distance < MAX_FOLLOW_DISTANCE * MAX_FOLLOW_DISTANCE) {

                boolean shouldRecalculatePath = shouldRecalculatePath(currentTargetPos, entity);

                if (shouldRecalculatePath && pathfindingCooldown <= 0) {
                    double offsetX = (Math.random() * 2 - 1) * MIN_FOLLOW_DISTANCE * 0.5;
                    double offsetZ = (Math.random() * 2 - 1) * MIN_FOLLOW_DISTANCE * 0.5;

                    entity.getNavigation().moveTo(
                            currentTargetPos.x + offsetX,
                            currentTargetPos.y,
                            currentTargetPos.z + offsetZ,
                            FOLLOW_SPEED
                    );

                    lastTargetPos = currentTargetPos;
                    pathfindingCooldown = 10;
                }

                updateRotation(entity, currentTargetPos);

            } else if (distance >= MAX_FOLLOW_DISTANCE * MAX_FOLLOW_DISTANCE) {
                double offsetX = (Math.random() * 2 - 1) * 2;
                double offsetZ = (Math.random() * 2 - 1) * 2;
                entity.teleportTo(
                        likedPlayer.getX() + offsetX,
                        likedPlayer.getY(),
                        likedPlayer.getZ() + offsetZ
                );
                lastTargetPos = null;
            } else {
                entity.getNavigation().stop();
                lastTargetPos = null;
            }

            if (pathfindingCooldown > 0) {
                pathfindingCooldown--;
            }
        }
    }

    private boolean shouldRecalculatePath(Vec3 currentTargetPos, E entity) {
        if (lastTargetPos == null) return true;
        if (!entity.getNavigation().isInProgress()) return true;

        return lastTargetPos.distanceToSqr(currentTargetPos) > PATH_RECALCULATION_THRESHOLD * PATH_RECALCULATION_THRESHOLD;
    }

    private void updateRotation(E entity, Vec3 targetPos) {
        double dx = targetPos.x - entity.getX();
        double dz = targetPos.z - entity.getZ();

        double targetYaw = Math.atan2(dz, dx) * (180.0 / Math.PI) - 90.0;

        double currentYaw = entity.getYRot() % 360.0;
        if (currentYaw < 0) currentYaw += 360.0;

        double diff = targetYaw - currentYaw;
        if (diff < -180.0) diff += 360.0;
        if (diff > 180.0) diff -= 360.0;

        entity.setYRot((float)(currentYaw + diff * ROTATION_SPEED));
    }

    @Override
    protected void stop(ServerLevel level, E entity, long gameTime) {
        entity.getNavigation().stop();
        lastTargetPos = null;
        pathfindingCooldown = 0;
    }
}
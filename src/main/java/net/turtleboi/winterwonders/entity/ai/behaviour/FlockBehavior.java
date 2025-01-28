package net.turtleboi.winterwonders.entity.ai.behaviour;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.phys.Vec3;
import net.turtleboi.winterwonders.entity.custom.SnowWispEntity;

import java.util.*;

public class FlockBehavior<E extends PathfinderMob> extends Behavior<E> {
    private static final double FLOCK_RADIUS = 10.0;
    private static final double FOLLOW_SPEED = 1.0;
    private static final double MIN_FOLLOW_DISTANCE = 2.0;
    private static final int LEADER_CHANGE_TIME = 200; // 10 seconds
    private static final double SEPARATION_DISTANCE = 3.0;

    private UUID currentLeader = null;
    private int leaderTimer = 0;
    private Random random = new Random();

    public FlockBehavior() {
        super(Map.of(
                MemoryModuleType.LIKED_PLAYER, MemoryStatus.VALUE_ABSENT // Only flock when not following a player
        ));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        // Only start if there are nearby wisps and we're not already following a player
        return !entity.getBrain().hasMemoryValue(MemoryModuleType.LIKED_PLAYER) &&
                !getNearbyWisps(level, entity).isEmpty();
    }

    private List<SnowWispEntity> getNearbyWisps(ServerLevel level, E entity) {
        List<SnowWispEntity> nearbyWisps = new ArrayList<>();

        // Get all nearby entities within FLOCK_RADIUS
        level.getEntitiesOfClass(SnowWispEntity.class,
                        entity.getBoundingBox().inflate(FLOCK_RADIUS),
                        wisp -> wisp != entity && !wisp.getBrain().hasMemoryValue(MemoryModuleType.LIKED_PLAYER))
                .forEach(nearbyWisps::add);

        return nearbyWisps;
    }

    @Override
    protected void tick(ServerLevel level, E entity, long gameTime) {
        List<SnowWispEntity> flock = getNearbyWisps(level, entity);

        if (flock.isEmpty()) {
            return;
        }

        // Check if we need a new leader
        if (currentLeader == null || leaderTimer <= 0) {
            // Pick a random wisp from the flock (including this one) as leader
            List<UUID> possibleLeaders = new ArrayList<>();
            possibleLeaders.add(entity.getUUID());
            flock.forEach(wisp -> possibleLeaders.add(wisp.getUUID()));

            currentLeader = possibleLeaders.get(random.nextInt(possibleLeaders.size()));
            leaderTimer = LEADER_CHANGE_TIME;
        }

        leaderTimer--;

        // If we're the leader, move randomly
        if (entity.getUUID().equals(currentLeader)) {
            if (!entity.getNavigation().isInProgress()) {
                // Move to a random position
                double randomX = entity.getX() + (random.nextDouble() - 0.5) * 10;
                double randomY = entity.getY() + (random.nextDouble() - 0.5) * 4;
                double randomZ = entity.getZ() + (random.nextDouble() - 0.5) * 10;
                entity.getNavigation().moveTo(randomX, randomY, randomZ, FOLLOW_SPEED);
            }
        } else {
            // Find the leader
            Optional<SnowWispEntity> leader = flock.stream()
                    .filter(wisp -> wisp.getUUID().equals(currentLeader))
                    .findFirst();

            if (leader.isPresent()) {
                // Calculate separation vector
                Vec3 separationVector = calculateSeparation(entity.position(), flock);

                // Get leader position
                Vec3 leaderPos = leader.get().position();

                // Combine leader following with separation
                Vec3 targetPos = leaderPos.add(separationVector);

                // Only move if we're not too close
                if (entity.position().distanceToSqr(targetPos) > MIN_FOLLOW_DISTANCE * MIN_FOLLOW_DISTANCE) {
                    entity.getNavigation().moveTo(
                            targetPos.x,
                            targetPos.y,
                            targetPos.z,
                            FOLLOW_SPEED
                    );
                }
            }
        }
    }

    private Vec3 calculateSeparation(Vec3 position, List<SnowWispEntity> flock) {
        Vec3 separation = Vec3.ZERO;
        int count = 0;

        for (SnowWispEntity other : flock) {
            double distance = position.distanceTo(other.position());
            if (distance < SEPARATION_DISTANCE && distance > 0) {
                // Add a vector pointing away from the nearby wisp
                Vec3 diff = position.subtract(other.position());
                diff = diff.normalize().scale(1.0 / distance); // Weight by distance
                separation = separation.add(diff);
                count++;
            }
        }

        if (count > 0) {
            separation = separation.scale(1.0 / count);
        }

        return separation;
    }

    @Override
    protected void stop(ServerLevel level, E entity, long gameTime) {
        entity.getNavigation().stop();
    }
}
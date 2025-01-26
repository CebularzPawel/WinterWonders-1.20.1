package net.turtleboi.winterwonders.entity.ai.behaviour;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;

import java.util.Map;

public class FollowPlayerAround extends Behavior<Mob> {
    private final int followDuration;
    private long endTime;
    private static final MemoryModuleType<Player> REQUIRED_MEMORY = MemoryModuleType.NEAREST_VISIBLE_PLAYER;


    public FollowPlayerAround(Map<MemoryModuleType<?>, MemoryStatus> pEntryCondition, int followDuration) {
        super(pEntryCondition);
        this.followDuration = followDuration;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel pLevel, Mob pOwner) {
        return pOwner.getBrain().hasMemoryValue(REQUIRED_MEMORY);
    }

    @Override
    protected boolean canStillUse(ServerLevel pLevel, Mob pEntity, long pGameTime) {
        return pGameTime < endTime && pEntity.getBrain().hasMemoryValue(REQUIRED_MEMORY);

    }

    @Override
    protected void start(ServerLevel pLevel, Mob pEntity, long pGameTime) {
        this.endTime = pGameTime + (this.followDuration * 1000L);
        pEntity.getBrain().setMemory(REQUIRED_MEMORY, pEntity.getBrain().getMemory(REQUIRED_MEMORY).orElse(null));
    }

    @Override
    protected void tick(ServerLevel pLevel, Mob pOwner, long pGameTime) {
        pOwner.getBrain().getMemory(REQUIRED_MEMORY).ifPresent(player -> {
            if (player != null && player.isAlive()) {
                Path path = pOwner.getNavigation().createPath(player.blockPosition(), 1);
                if (path != null) {
                    pOwner.getNavigation().moveTo(path, 1.0);
                }
            }
        });
    }

    @Override
    protected void stop(ServerLevel pLevel, Mob pEntity, long pGameTime) {
        pEntity.getNavigation().stop();
        pEntity.getBrain().eraseMemory(REQUIRED_MEMORY);
    }
}

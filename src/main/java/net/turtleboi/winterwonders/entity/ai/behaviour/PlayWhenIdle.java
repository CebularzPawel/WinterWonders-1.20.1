package net.turtleboi.winterwonders.entity.ai.behaviour;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.util.AirRandomPos;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.Map;

public class PlayWhenIdle<E extends LivingEntity> extends Behavior<E>
{
    private static final double SPEED_MULTIPLIER = 1.5D;
    private static final int MAX_DISTANCE = 10;
    private static final int CHECK_INTERVAL = 10;

    public PlayWhenIdle(Map<MemoryModuleType<?>, MemoryStatus> pEntryCondition) {
        super(pEntryCondition);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel pLevel, E pOwner) {
        return !pOwner.getBrain().hasMemoryValue(MemoryModuleType.WALK_TARGET) &&
                !pOwner.getBrain().hasMemoryValue(MemoryModuleType.LOOK_TARGET) &&
                !pOwner.getBrain().hasMemoryValue(MemoryModuleType.LIKED_PLAYER) &&
                !pOwner.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET);
    }

    @Override
    protected void start(ServerLevel pLevel, E pEntity, long pGameTime) {
        super.start(pLevel, pEntity, pGameTime);

//        Vec3 fleeTarget = getRandomFleePosition(pEntity);
//
//        if (fleeTarget != null) {
//            pEntity.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new net.minecraft.world.entity.ai.memory.WalkTarget(fleeTarget, (float) SPEED_MULTIPLIER, 0));
//        }
    }

    @Override
    protected boolean canStillUse(ServerLevel pLevel, E pEntity, long pGameTime) {
        return pEntity.getBrain().hasMemoryValue(MemoryModuleType.WALK_TARGET);
    }

    @Override
    protected void stop(ServerLevel pLevel, E pEntity, long pGameTime) {
        super.stop(pLevel, pEntity, pGameTime);
        pEntity.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
    }


}

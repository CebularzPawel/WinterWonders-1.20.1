package net.turtleboi.winterwonders.entity.ai;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.turtleboi.winterwonders.entity.ai.behaviour.FlockBehavior;
import net.turtleboi.winterwonders.entity.ai.behaviour.FollowLikedPlayer;
import net.turtleboi.winterwonders.entity.ai.behaviour.PlayHideAndSeek;
import net.turtleboi.winterwonders.entity.ai.behaviour.PlayWhenIdle;
import net.turtleboi.winterwonders.entity.custom.SnowWispEntity;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class SnowWispAI
{
    public static final int FOLLOW_PLAYER_DURATION = Mth.ceil(10.3D);
    private static final List<SensorType<? extends Sensor<? super SnowWispEntity>>> SENSOR_TYPES;
    private static final List<MemoryModuleType<?>> MEMORY_TYPES;

    public  static void updateActivity(SnowWispEntity pEntity)
    {
        pEntity.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.IDLE,Activity.PANIC,Activity.REST));
    }

    public static Brain<?> makeBrain (SnowWispEntity pEntity, Dynamic<?> pOps)
    {
        Brain.Provider<SnowWispEntity> provider = Brain.provider(MEMORY_TYPES,SENSOR_TYPES);
        Brain<SnowWispEntity> wispBrain = provider.makeBrain(pOps);
        wispBrain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        initCoreActivities(wispBrain);
        initIdleActivities(wispBrain);
        wispBrain.setDefaultActivity(Activity.IDLE);
        wispBrain.useDefaultActivity();
        return wispBrain;
    }

    private static void initCoreActivities(Brain<SnowWispEntity> brain) {
        brain.addActivityWithConditions(
                Activity.CORE,
                ImmutableList.of(
                        Pair.of(0, new FollowLikedPlayer<>()),
                        Pair.of(1, new PlayHideAndSeek<>(Map.of(
                                MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryStatus.VALUE_PRESENT
                        ))),
                        Pair.of(2, new AnimalPanic(1.0F))
                ),
                ImmutableSet.of()
        );
    }




    private static void initIdleActivities(Brain<SnowWispEntity> brain)
    {
        brain.addActivityWithConditions(Activity.IDLE, ImmutableList.of(
                Pair.of(1, new FlockBehavior<>()),
                Pair.of(1, new RunOne(ImmutableList.of(Pair.of(RandomStroll.fly(1.0F), 2),
                        Pair.of(SetWalkTargetFromLookTarget.create(1.0F, 3), 2),
                        Pair.of(new DoNothing(30, 60), 1)))),
                        Pair.of(2, SetEntityLookTargetSometimes.create(6.0F, UniformInt.of(30, 60)))),

                ImmutableSet.of());
    }

    static {
        SENSOR_TYPES = List.of(SensorType.NEAREST_PLAYERS);
        MEMORY_TYPES = List.of(MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.LOOK_TARGET, MemoryModuleType.PATH,MemoryModuleType.LIKED_PLAYER);
    }


    //Utility fix class
    //TODO::FIX THE AI TO BE MORE BOILERPLATED LMAO
    static class MaintainHeightAboveGround extends Behavior<SnowWispEntity> {
        private static final double TARGET_HEIGHT = 1.0D;
        private static final double TOLERANCE = 0.05D;
        private static final double MOVEMENT_ADJUSTMENT = 0.02D;

        public MaintainHeightAboveGround() {
            super(Map.of());
        }

        @Override
        protected boolean checkExtraStartConditions(ServerLevel pLevel, SnowWispEntity pOwner) {
            return true;
        }


        @Override
        protected void start(ServerLevel level, SnowWispEntity entity, long pGameTime) {
            Vec3 position = entity.position();
            double groundHeight = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, entity.blockPosition()).getY();
            double targetY = groundHeight + TARGET_HEIGHT;

            double deltaY = targetY - position.y;

            if (Math.abs(deltaY) > TOLERANCE) {
                double adjustment = Math.signum(deltaY) * MOVEMENT_ADJUSTMENT;
                entity.setDeltaMovement(entity.getDeltaMovement().add(0, adjustment, 0));
            }
        }
    }
}

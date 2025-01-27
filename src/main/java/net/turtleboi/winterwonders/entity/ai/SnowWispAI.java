package net.turtleboi.winterwonders.entity.ai;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.Mth;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import net.turtleboi.winterwonders.entity.ai.behaviour.FollowPlayerAround;
import net.turtleboi.winterwonders.entity.custom.SnowWispEntity;

import java.util.List;
import java.util.Map;

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

    private static void initCoreActivities(Brain<SnowWispEntity> brain)
    {
        brain.addActivity(Activity.CORE,0,ImmutableList.of(new LookAtTargetSink(45, 90), new FollowPlayerAround(Map.of(MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryStatus.VALUE_PRESENT),FOLLOW_PLAYER_DURATION), new AnimalPanic(1.0F)));
    }

    private static void initIdleActivities(Brain<SnowWispEntity> brain)
    {
        brain.addActivityWithConditions(Activity.IDLE, ImmutableList.of(Pair.of(0, GoToWantedItem.create((p_218428_) -> true, 1.75F, true, 32)), Pair.of(3, SetEntityLookTargetSometimes.create(6.0F, UniformInt.of(30, 60))), Pair.of(4, new RunOne(ImmutableList.of(Pair.of(RandomStroll.fly(1.0F), 2), Pair.of(SetWalkTargetFromLookTarget.create(1.0F, 3), 2), Pair.of(new DoNothing(30, 60), 1))))), ImmutableSet.of());
    }

    static {
        SENSOR_TYPES = List.of(SensorType.NEAREST_PLAYERS);
        MEMORY_TYPES = List.of(MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.LOOK_TARGET, MemoryModuleType.PATH);
    }
}

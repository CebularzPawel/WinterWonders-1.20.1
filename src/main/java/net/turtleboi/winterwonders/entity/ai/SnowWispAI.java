package net.turtleboi.winterwonders.entity.ai;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.AnimalPanic;
import net.minecraft.world.entity.ai.behavior.DoNothing;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.RandomStroll;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import net.turtleboi.winterwonders.entity.ai.behaviour.FollowPlayerAround;
import net.turtleboi.winterwonders.entity.custom.SnowWisp;

import java.util.List;
import java.util.Map;

public class SnowWispAI
{
    public static final int FOLLOW_PLAYER_DURATION = Mth.ceil(10.3D);
    private static final List<SensorType<? extends Sensor<? super SnowWisp>>> SENSOR_TYPES;
    private static final List<MemoryModuleType<?>> MEMORY_TYPES;

    public  static void updateActivity(SnowWisp pEntity)
    {
        pEntity.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.IDLE,Activity.PANIC,Activity.REST));
    }

    public static Brain<?> makeBrain (SnowWisp pEntity, Dynamic<?> pOps)
    {
        Brain.Provider<SnowWisp> provider = Brain.provider(MEMORY_TYPES,SENSOR_TYPES);
        Brain<SnowWisp> wispBrain = provider.makeBrain(pOps);
        wispBrain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        initCoreActivities(wispBrain);
        initIdleActivities(wispBrain);
        wispBrain.setDefaultActivity(Activity.IDLE);
        wispBrain.useDefaultActivity();
        return wispBrain;
    }

    private static void initCoreActivities(Brain<SnowWisp> brain)
    {
        brain.addActivity(Activity.CORE,0,ImmutableList.of(new LookAtTargetSink(45, 90), new FollowPlayerAround(Map.of(MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryStatus.VALUE_PRESENT),FOLLOW_PLAYER_DURATION), new AnimalPanic(1.0F)));
    }

    private static void initIdleActivities(Brain<SnowWisp> brain)
    {
        brain.addActivity(Activity.IDLE, 10,ImmutableList.of(RandomStroll.stroll(1.0F),new DoNothing(30,60)));
    }

    static {
        SENSOR_TYPES = List.of(SensorType.NEAREST_PLAYERS);
        MEMORY_TYPES = List.of(MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.LOOK_TARGET, MemoryModuleType.PATH);
    }
}

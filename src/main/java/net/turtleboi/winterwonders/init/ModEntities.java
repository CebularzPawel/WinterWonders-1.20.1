package net.turtleboi.winterwonders.init;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.turtleboi.winterwonders.WinterWonders;
import net.turtleboi.winterwonders.entity.custom.SnowWisp;

public class ModEntities
{
    public static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, WinterWonders.MOD_ID);

    public static final RegistryObject<EntityType<SnowWisp>> SNOW_WISP = TYPES.register("snow_wisp",()->
            EntityType.Builder.of(SnowWisp::new, MobCategory.MONSTER).sized(0.35F, 0.6F).clientTrackingRange(8).updateInterval(2).build("snow_wisp"));

    public static void register(IEventBus eventBus)
    {
        TYPES.register(eventBus);
    }
}

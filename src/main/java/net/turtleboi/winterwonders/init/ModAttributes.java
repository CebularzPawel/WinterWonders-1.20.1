package net.turtleboi.winterwonders.init;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.turtleboi.winterwonders.WinterWonders;

@Mod.EventBusSubscriber(modid = WinterWonders.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModAttributes {
    public static final DeferredRegister<Attribute> REGISTRY =
            DeferredRegister.create(ForgeRegistries.ATTRIBUTES, WinterWonders.MOD_ID);

    public static final RegistryObject<Attribute> COLD_RESISTANCE =
            create("cold_resistance", 0D, 0D);

    private static RegistryObject<Attribute> create(
            String name, double defaultValue, double minValue) {
        String descriptionId = "attribute.name." + WinterWonders.MOD_ID + "." + name;
        return REGISTRY.register(
                name,
                () -> new RangedAttribute(descriptionId, defaultValue, minValue, 1024D).setSyncable(true));
    }

    @SubscribeEvent
    public static void attachAttributes(EntityAttributeModificationEvent event) {
        REGISTRY.getEntries().stream()
                .map(RegistryObject::get)
                .forEach(attribute -> event.add(EntityType.PLAYER, attribute));
    }
}

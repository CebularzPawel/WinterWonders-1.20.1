// package net.cebularz.winterwonders.init;

// import java.util.Set;

// import net.cebularz.winterwonders.WinterWonders;
// import net.cebularz.winterwonders.block.ModBlocks;
// import net.minecraft.world.entity.ai.village.poi.PoiType;
// import net.minecraftforge.eventbus.api.IEventBus;
// import net.minecraftforge.registries.DeferredRegister;
// import net.minecraftforge.registries.ForgeRegistries;
// import net.minecraftforge.registries.RegistryObject;

// public class ModPoiTypes {
    // public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(ForgeRegistries.POI_TYPES, WinterWonders.MOD_ID);
    // public static final RegistryObject<PoiType> FISH_RACK = POI_TYPES.register("fish_rack",
            // () -> new PoiType(Set.copyOf(ModBlocks.FISH_RACK.get().getStateDefinition().getPossibleStates()), 24, 32));
    // public static final RegistryObject<PoiType> PINGIN_NEST = POI_TYPES.register("pingin_nest",
            // () -> new PoiType(Set.copyOf(ModBlocks.PINGIN_NEST.get().getStateDefinition().getPossibleStates()), 2, 8));

    // public static void register(IEventBus eventBus) {
        // POI_TYPES.register(eventBus);
    // }
// }

package net.cebularz.winterwonders.worldgen.tree;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.worldgen.tree.greypine.GreypineTrunkPlacer;

public class ModTrunkPlacers {
    public static final DeferredRegister<TrunkPlacerType<?>> TRUNK_PLACERS =
            DeferredRegister.create(Registries.TRUNK_PLACER_TYPE, WinterWonders.MOD_ID);

    public static final RegistryObject<TrunkPlacerType<GreypineTrunkPlacer>> GREYPINE_TRUNK_PLACER =
            TRUNK_PLACERS.register("greypine_trunk_placer", () -> new TrunkPlacerType<>(GreypineTrunkPlacer.CODEC));

    public static void register(IEventBus eventBus){
        TRUNK_PLACERS.register(eventBus);
    }
}

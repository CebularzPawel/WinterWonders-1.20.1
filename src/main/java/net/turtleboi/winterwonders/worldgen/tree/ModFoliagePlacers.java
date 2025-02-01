package net.turtleboi.winterwonders.worldgen.tree;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.turtleboi.winterwonders.WinterWonders;
import net.turtleboi.winterwonders.worldgen.tree.greypine.GreypineFoliagePlacer;

public class ModFoliagePlacers {
    public static final DeferredRegister<FoliagePlacerType<?>> FOLIAGE_PLACERS =
            DeferredRegister.create(Registries.FOLIAGE_PLACER_TYPE, WinterWonders.MOD_ID);

    public static final RegistryObject<FoliagePlacerType<GreypineFoliagePlacer>> GREYPINE_FOLIAGE_PLACER =
            FOLIAGE_PLACERS.register("greypine_foliage_placer", () -> new FoliagePlacerType<>(GreypineFoliagePlacer.CODEC));

    public static void register(IEventBus eventBus){
        FOLIAGE_PLACERS.register(eventBus);
    }
}

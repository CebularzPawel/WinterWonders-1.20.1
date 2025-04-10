package net.cebularz.winterwonders.worldgen;

import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.worldgen.customfeatures.ColdstoneSpikeFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(ForgeRegistries.FEATURES, WinterWonders.MOD_ID);

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> COLDSTONE_SPIKE =
            FEATURES.register("coldstone_spike",
                    () -> new ColdstoneSpikeFeature(NoneFeatureConfiguration.CODEC)
            );

    public static void register(IEventBus eventBus) {
        FEATURES.register(eventBus);
    }
}

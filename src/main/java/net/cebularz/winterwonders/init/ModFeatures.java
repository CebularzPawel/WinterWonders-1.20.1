package net.cebularz.winterwonders.init;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.worldgen.customfeatures.IceStoneSpikeFeature;

public class ModFeatures {
    public static final DeferredRegister<Feature<?>> DEF_REG = DeferredRegister.create(ForgeRegistries.FEATURES, WinterWonders.MOD_ID);

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> ICE_STONE_SPIKE = DEF_REG.register("ice_stone_spike", () -> new IceStoneSpikeFeature(NoneFeatureConfiguration.CODEC));


}

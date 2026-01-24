package net.cebularz.winterwonders.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ClampedInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;
import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.block.ModBlocks;

import java.util.List;

public final class ModPlacedFeatures {
    public static final ResourceKey<PlacedFeature> GREYPINE_PLACED_KEY = registerKey("greypine_placed");
    public static final ResourceKey<PlacedFeature> MYST_WILLOW_PLACED_KEY = registerKey("myst_willow_placed");

    public static final ResourceKey<PlacedFeature> COLDSTONE_SPIKE_KEY = registerKey("coldstone_spike");

    public static final ResourceKey<PlacedFeature> WONDER_SHROOM_PATCH_KEY = registerKey("wonder_shroom_patch");

    public static final ResourceKey<PlacedFeature> WINTER_FROST_FLOWERS_KEY = registerKey("winter_frost_flowers");
    public static final ResourceKey<PlacedFeature> MAGICAL_FLOWERS_KEY = registerKey("magical_flowers");

    public static final ResourceKey<PlacedFeature> MUSCARIS_KEY = registerKey("muscaris");


    public static void bootstrap(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        register(context, GREYPINE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.GREYPINE_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(8, 0.5f, 4),
                        ModBlocks.GREYPINE_SAPLING.get()));

        register(context, MYST_WILLOW_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.MYST_WILLOW_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(8, 0.5f, 4),
                        ModBlocks.MYST_WILLOW_SAPLING.get()));

        PlacementUtils.register(context, MAGICAL_FLOWERS_KEY, configuredFeatures.getOrThrow(
                ModConfiguredFeatures.FROSTPETAL_KEY),
                NoiseThresholdCountPlacement.of(3, 2, 5),
                InSquarePlacement.spread(),
                PlacementUtils.HEIGHTMAP,
                BiomeFilter.biome());

        PlacementUtils.register(context,WINTER_FROST_FLOWERS_KEY,configuredFeatures.getOrThrow(
                ModConfiguredFeatures.RIMEBLOOM_KEY),
                CountPlacement.of(1),
                InSquarePlacement.spread(),
                PlacementUtils.HEIGHTMAP,
                BiomeFilter.biome());

        PlacementUtils.register(context,MUSCARIS_KEY,configuredFeatures.getOrThrow(
                        ModConfiguredFeatures.MUSCARI_KEY),
                CountPlacement.of(1),
                InSquarePlacement.spread(),
                PlacementUtils.HEIGHTMAP,
                BiomeFilter.biome());

        PlacementUtils.register(context,WONDER_SHROOM_PATCH_KEY,configuredFeatures.getOrThrow(
                ModConfiguredFeatures.WUNDERSHROOM_KEY),
                RarityFilter.onAverageOnceEvery(1),
                InSquarePlacement.spread(),
                PlacementUtils.HEIGHTMAP,
                CountPlacement.of(ClampedInt.of(UniformInt.of(-3, 1), 0, 1)),
                BiomeFilter.biome());

        PlacementUtils.register(context, COLDSTONE_SPIKE_KEY, configuredFeatures.getOrThrow(
                ModConfiguredFeatures.COLDSTONE_SPIKE_KEY),
                CountPlacement.of(1),
                RarityFilter.onAverageOnceEvery(2),
                InSquarePlacement.spread(),
                PlacementUtils.HEIGHTMAP,
                BiomeFilter.biome());
    }

    private static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(WinterWonders.MOD_ID, name));
    }

    private static void register(BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuration,
                                 List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}

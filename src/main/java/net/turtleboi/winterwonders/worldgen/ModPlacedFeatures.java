package net.turtleboi.winterwonders.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.turtleboi.winterwonders.WinterWonders;
import net.turtleboi.winterwonders.init.ModBlocks;

import java.util.List;

public final class ModPlacedFeatures {
    public static final ResourceKey<PlacedFeature> GREYPINE_PLACED_KEY = registerKey("greypine_placed");
    public static final ResourceKey<PlacedFeature> MYST_WILLOW_PLACED_KEY = registerKey("myst_willow_placed");

    public static final ResourceKey<PlacedFeature> ICE_STONE_SPIKE_KEY = registerKey("ice_stone_spike");

    public static final ResourceKey<PlacedFeature> WONDER_SHROOM_PATCH_KEY = registerKey("wonder_shroom_patch");

    public static void bootstrap(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        register(context, GREYPINE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.GREYPINE_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(8, 0.5f, 4),
                        ModBlocks.GREYPINE_SAPLING.get()));

        register(context, MYST_WILLOW_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.MYST_WILLOW_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(8, 0.5f, 4),
                        ModBlocks.MYST_WILLOW_SAPLING.get()));

        PlacementUtils.register(context, ICE_STONE_SPIKE_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.ICE_STONE_SPIKE_KEY), new PlacementModifier[]{CountPlacement.of(1),RarityFilter.onAverageOnceEvery(2), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()});

    }

    private static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(WinterWonders.MOD_ID, name));
    }

    private static void register(BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuration,
                                 List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}

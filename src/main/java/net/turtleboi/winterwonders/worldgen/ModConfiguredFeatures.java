package net.turtleboi.winterwonders.worldgen;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIntImmutablePair;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.AcaciaFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.DarkOakFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.BendingTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.DarkOakTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.turtleboi.winterwonders.WinterWonders;
import net.turtleboi.winterwonders.init.ModBlocks;
import net.turtleboi.winterwonders.worldgen.tree.greypine.GreypineFoliagePlacer;
import net.turtleboi.winterwonders.worldgen.tree.greypine.GreypineTrunkPlacer;

public final class ModConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> GREYPINE_KEY = registerKey("grey_pine");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MYST_WILLOW_KEY = registerKey("myst_willow");

    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
        register(context, GREYPINE_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.GREYPINE_LOG.get()),
                new GreypineTrunkPlacer(8, 1, 3),
                BlockStateProvider.simple(ModBlocks.GREYPINE_LEAVES.get()),
                new GreypineFoliagePlacer(ConstantInt.of(3), ConstantInt.of(2), 3),
                new TwoLayersFeatureSize(1, 0, 2)).build());

        register(context, MYST_WILLOW_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.MYST_WILLOW_LOG.get()),
                new DarkOakTrunkPlacer(4, 2, 1),
                BlockStateProvider.simple(ModBlocks.MYST_WILLOW_LEAVES.get()),
                new DarkOakFoliagePlacer(ConstantInt.of(3), ConstantInt.of(2)),
                new TwoLayersFeatureSize(1, 0, 2)).build());
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(WinterWonders.MOD_ID, name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstapContext<ConfiguredFeature<?, ?>> context,
                                                                                          ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}

package net.cebularz.winterwonders.worldgen;

import net.cebularz.winterwonders.worldgen.customfeatures.ColdstoneSpikeFeature;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.PinkPetalsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.DarkOakFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.DarkOakTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.block.ModBlocks;
import net.cebularz.winterwonders.worldgen.tree.greypine.GreypineFoliagePlacer;
import net.cebularz.winterwonders.worldgen.tree.greypine.GreypineTrunkPlacer;

public final class ModConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> GREYPINE_KEY = registerKey("grey_pine");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MYST_WILLOW_KEY = registerKey("myst_willow");

    public static final ResourceKey<ConfiguredFeature<?,?>> COLDSTONE_SPIKE_KEY = registerKey("coldstone_spike");

    public static final ResourceKey<ConfiguredFeature<?,?>> WUNDERSHROOM_KEY = registerKey("wundershroom");

    public static final ResourceKey<ConfiguredFeature<?,?>> RIMEBLOOM_KEY = registerKey("rimebloom");
    public static final ResourceKey<ConfiguredFeature<?,?>> FROSTPETAL_KEY = registerKey("frostpetal");

    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
        FeatureUtils.register(context, COLDSTONE_SPIKE_KEY, ModFeatures.COLDSTONE_SPIKE.get());
        //register(context, COLDSTONE_SPIKE_KEY, ModFeatures.COLDSTONE_SPIKE.get(), NoneFeatureConfiguration.INSTANCE);

        register(context, WUNDERSHROOM_KEY,Feature.RANDOM_PATCH, new RandomPatchConfiguration(32, 7, 3, PlacementUtils.onlyWhenEmpty(
                        Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.WUNDERSHROOM.get())))
                )
        );
        SimpleWeightedRandomList.Builder<BlockState> $$33 = SimpleWeightedRandomList.builder();

        for(int $$34 = 1; $$34 <= 4; ++$$34) {

            for (Direction $$35 : Direction.Plane.HORIZONTAL) {
                $$33.add(ModBlocks.FROSTPETAL.get().defaultBlockState().setValue(PinkPetalsBlock.AMOUNT, $$34).setValue(PinkPetalsBlock.FACING, $$35), 1);
            }
        }
        register(context, FROSTPETAL_KEY,Feature.FLOWER, new RandomPatchConfiguration(96, 6, 2, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(new WeightedStateProvider($$33)))));

        register(context, RIMEBLOOM_KEY,Feature.SIMPLE_RANDOM_SELECTOR,new SimpleRandomFeatureConfiguration(HolderSet.direct(PlacementUtils.inlinePlaced(Feature.RANDOM_PATCH, FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.ARCANILLUM.get()))), new PlacementModifier[0]), PlacementUtils.inlinePlaced(Feature.RANDOM_PATCH, FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.RIMEBLOOM.get()))), new PlacementModifier[0]))));

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

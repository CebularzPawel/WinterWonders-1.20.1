package net.cebularz.winterwonders.worldgen.tree.mystwillow;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.cebularz.winterwonders.worldgen.ModConfiguredFeatures;
import org.jetbrains.annotations.Nullable;

public class MystWillowTreeGrower extends AbstractTreeGrower {
    @Override
    protected @Nullable ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource pRandom, boolean pHasFlowers) {
        return ModConfiguredFeatures.MYST_WILLOW_KEY;
    }
}

package net.turtleboi.winterwonders.worldgen.tree.greypine;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.turtleboi.winterwonders.worldgen.ModConfiguredFeatures;
import org.jetbrains.annotations.Nullable;

public class GreypineTreeGrower extends AbstractTreeGrower {
    @Override
    protected @Nullable ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource pRandom, boolean pHasFlowers) {
        return ModConfiguredFeatures.GREYPINE_KEY;
    }
}

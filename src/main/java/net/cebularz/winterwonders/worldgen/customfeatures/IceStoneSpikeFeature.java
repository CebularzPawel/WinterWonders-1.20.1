package net.cebularz.winterwonders.worldgen.customfeatures;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.cebularz.winterwonders.init.ModBlocks;

public class IceStoneSpikeFeature extends Feature<NoneFeatureConfiguration> {
    public IceStoneSpikeFeature(Codec<NoneFeatureConfiguration> pCodec) {
        super(pCodec);
    }

    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        BlockPos pos = context.origin();
        RandomSource random = context.random();
        WorldGenLevel world = context.level();

        // Move down to find ground level
        while (world.isEmptyBlock(pos) && pos.getY() > world.getMinBuildHeight() + 2) {
            pos = pos.below();
        }

        if (!isDirt(world.getBlockState(pos))) {
            return false;
        }

        pos = pos.above(random.nextInt(2)); // Start slightly above ground
        int height = random.nextInt(3) + 4; // Height range: 4-6
        int width = height / 3 + random.nextInt(1); // Adjusted width

        for (int y = 0; y < height; ++y) {
            float radius = (1.0F - (float) y / (float) height) * (float) width;
            int radiusCeil = Mth.ceil(radius);

            for (int dx = -radiusCeil; dx <= radiusCeil; ++dx) {
                float xDist = (float) Mth.abs(dx) - 0.25F;

                for (int dz = -radiusCeil; dz <= radiusCeil; ++dz) {
                    float zDist = (float) Mth.abs(dz) - 0.25F;
                    if ((dx == 0 && dz == 0 || !(xDist * xDist + zDist * zDist > radius * radius)) &&
                            (dx != -radiusCeil && dx != radiusCeil && dz != -radiusCeil && dz != radiusCeil || !(random.nextFloat() > 0.75F))) {
                        BlockPos blockPos = pos.offset(dx, y, dz);
                        BlockState blockBelow = world.getBlockState(blockPos.below());

                        // Place block and ensure it extends down to solid ground
                        this.setBlock(world, blockPos, ModBlocks.COBBLED_ICE_STONE.get().defaultBlockState());

                        while (world.isEmptyBlock(blockPos.below()) && blockPos.getY() > world.getMinBuildHeight() + 2) {
                            blockPos = blockPos.below();
                            this.setBlock(world, blockPos, ModBlocks.COBBLED_ICE_STONE.get().defaultBlockState());
                        }
                    }
                }
            }
        }

        return true;
    }
}

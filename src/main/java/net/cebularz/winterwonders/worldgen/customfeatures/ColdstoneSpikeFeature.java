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
import net.cebularz.winterwonders.block.ModBlocks;

public class ColdstoneSpikeFeature extends Feature<NoneFeatureConfiguration> {
    public ColdstoneSpikeFeature(Codec<NoneFeatureConfiguration> pCodec) {
        super(pCodec);
    }

    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        BlockPos originPos = context.origin();
        RandomSource randomSource = context.random();
        WorldGenLevel world = context.level();

        while (world.isEmptyBlock(originPos) && originPos.getY() > world.getMinBuildHeight() + 2) {
            originPos = originPos.below();
        }

        if (!isDirt(world.getBlockState(originPos))) {
            return false;
        }

        originPos = originPos.above(randomSource.nextInt(2));
        int height = randomSource.nextInt(3) + 4;
        int width = height / 3 + randomSource.nextInt(1);

        for (int y = 0; y < height; ++y) {
            float radius = (1.0F - (float) y / (float) height) * (float) width;
            int radiusCeil = Mth.ceil(radius);

            for (int dx = -radiusCeil; dx <= radiusCeil; ++dx) {
                float xDist = (float) Mth.abs(dx) - 0.25F;

                for (int dz = -radiusCeil; dz <= radiusCeil; ++dz) {
                    float zDist = (float) Mth.abs(dz) - 0.25F;
                    if ((dx == 0 && dz == 0 || !(xDist * xDist + zDist * zDist > radius * radius)) &&
                            (dx != -radiusCeil && dx != radiusCeil && dz != -radiusCeil && dz != radiusCeil || !(randomSource.nextFloat() > 0.75F))) {
                        BlockPos blockPos = originPos.offset(dx, y, dz);
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

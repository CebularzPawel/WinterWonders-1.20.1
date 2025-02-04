package net.turtleboi.winterwonders.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class MushroomNoGrowableBlock extends MushroomBlock {

    public MushroomNoGrowableBlock(Properties pProperties) {
        super(pProperties, null);
    }

    public boolean isValidBonemealTarget(LevelReader pLevel, BlockPos pPos, BlockState pState, boolean pIsClient) {
        return false;
    }


}

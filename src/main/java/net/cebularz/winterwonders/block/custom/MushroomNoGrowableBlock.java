package net.cebularz.winterwonders.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.state.BlockState;

public class MushroomNoGrowableBlock extends MushroomBlock {

    public MushroomNoGrowableBlock(Properties pProperties) {
        super(pProperties, null);
    }

    public boolean isValidBonemealTarget(LevelReader pLevel, BlockPos pPos, BlockState pState, boolean pIsClient) {
        return false;
    }


}

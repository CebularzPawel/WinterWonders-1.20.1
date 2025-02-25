package net.cebularz.winterwonders.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class GreypineLeavesBlock extends LeavesBlock {
    public static final BooleanProperty SNOWY = BlockStateProperties.SNOWY;

    public GreypineLeavesBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(SNOWY, Boolean.valueOf(false)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(SNOWY);
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        if (pFacing == Direction.UP) {
            return pState.setValue(SNOWY, pFacingState.is(Blocks.SNOW) || pFacingState.is(Blocks.SNOW_BLOCK));
        }
        return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        BlockState aboveState = pLevel.getBlockState(pPos.above());
        boolean snowy = aboveState.is(Blocks.SNOW) || aboveState.is(Blocks.SNOW_BLOCK);
        if (pState.getValue(SNOWY) != snowy) {
            pLevel.setBlock(pPos, pState.setValue(SNOWY, snowy), 3);
        }
        super.randomTick(pState, pLevel, pPos, pRandom);
    }
}

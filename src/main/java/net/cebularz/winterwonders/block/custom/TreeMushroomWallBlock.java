package net.cebularz.winterwonders.block.custom;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Map;

public class TreeMushroomWallBlock extends TreeMushroomBlock {


    public TreeMushroomWallBlock(Properties pProperties) {
        super(pProperties);
    }

    public static final DirectionProperty FACING;
    private static final Map<Direction, VoxelShape> SHAPES;



    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return (VoxelShape)SHAPES.get(pState.getValue(FACING));
    }

    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return (BlockState)pState.setValue(FACING, pRotation.rotate((Direction)pState.getValue(FACING)));
    }

    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation((Direction)pState.getValue(FACING)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(new Property[]{FACING});
    }

    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {

        return pFacing.getOpposite() == pState.getValue(FACING) && !pState.canSurvive(pLevel, pCurrentPos) ? Blocks.AIR.defaultBlockState() : pState;
    }

    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        Direction $$3 = (Direction)pState.getValue(FACING);
        BlockPos $$4 = pPos.relative($$3.getOpposite());
        BlockState $$5 = pLevel.getBlockState($$4);
        return $$5.isFaceSturdy(pLevel, $$4, $$3);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState $$1 = super.getStateForPlacement(pContext);
        LevelReader $$2 = pContext.getLevel();
        BlockPos $$3 = pContext.getClickedPos();
        Direction[] $$4 = pContext.getNearestLookingDirections();
        Direction[] var6 = $$4;
        int var7 = $$4.length;

        for(int var8 = 0; var8 < var7; ++var8) {
            Direction $$5 = var6[var8];
            if ($$5.getAxis().isHorizontal()) {
                $$1 = (BlockState)$$1.setValue(FACING, $$5.getOpposite());
                if ($$1.canSurvive($$2, $$3)) {
                    return $$1;
                }
            }
        }

        return null;
    }

    static {
        FACING = HorizontalDirectionalBlock.FACING;
        SHAPES = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, Block.box(0.0, 4.0, 5.0, 16.0, 12.0, 16.0), Direction.SOUTH, Block.box(0.0, 4.0, 0.0, 16.0, 12.0, 11.0), Direction.WEST, Block.box(5.0, 4.0, 0.0, 16.0, 12.0, 16.0), Direction.EAST, Block.box(0.0, 4.0, 0.0, 11.0, 12.0, 16.0)));
    }



}

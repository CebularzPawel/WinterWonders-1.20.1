package net.cebularz.winterwonders.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.cebularz.winterwonders.block.entity.custom.IcyVinesPlantBlockEntity;
import net.cebularz.winterwonders.block.ModBlocks;
import org.jetbrains.annotations.Nullable;

public class IcyVinesPlantBlock extends GrowingPlantBodyBlock implements EntityBlock{
    public IcyVinesPlantBlock(BlockBehaviour.Properties properties) {
        super(properties, Direction.DOWN, SHAPE, false);
    }

    public static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    protected boolean mayPlaceOn(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return pState.is(BlockTags.DIRT) || pState.is(Blocks.FARMLAND);
    }

    protected GrowingPlantHeadBlock getHeadBlock() {
        return (GrowingPlantHeadBlock) ModBlocks.ICY_VINES.get();
    }

    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        BlockPos $$3 = pPos.relative(this.growthDirection.getOpposite());
        BlockState $$4 = pLevel.getBlockState($$3);
        if (!this.canAttachTo($$4)) {
            return false;
        } else {
            return $$4.is(this.getHeadBlock()) || $$4.is(this.getBodyBlock()) || $$4.isCollisionShapeFullBlock(pLevel, $$3);
        }
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new IcyVinesPlantBlockEntity(blockPos, blockState);
    }
}

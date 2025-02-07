package net.turtleboi.winterwonders.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.turtleboi.winterwonders.block.entity.IcyVinesBlockEntity;
import net.turtleboi.winterwonders.init.ModBlockEntities;
import net.turtleboi.winterwonders.init.ModBlocks;
import org.jetbrains.annotations.Nullable;

public class IcyVinesBlock extends GrowingPlantHeadBlock implements EntityBlock{

    public IcyVinesBlock(Properties p_154975_) {
        super(p_154975_, Direction.DOWN, SHAPE, false,0.1);
    }
    protected static final VoxelShape SHAPE = Block.box(4.0, 9.0, 4.0, 12.0, 16.0, 12.0);

    protected int getBlocksToGrowWhenBonemealed(RandomSource p_222680_) {
        return NetherVines.getBlocksToGrowWhenBonemealed(p_222680_);
    }

    protected Block getBodyBlock() {
        return ModBlocks.ICY_VINES_PLANT.get();
    }

    protected boolean canGrowInto(BlockState p_154971_) {
        return isValidGrowthState(p_154971_);
    }

    public static boolean isValidGrowthState(BlockState pState) {
        return pState.isAir();
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
        return new IcyVinesBlockEntity(blockPos, blockState);
    }
}


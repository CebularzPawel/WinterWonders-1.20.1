package net.cebularz.winterwonders.block.custom;

import net.cebularz.winterwonders.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.NotNull;

public class TallBerryBushBlock extends BushBlock implements BonemealableBlock {

    public static final IntegerProperty AGE = BlockStateProperties.AGE_4;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

    public TallBerryBushBlock(Properties props) {
        super(props);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(AGE, 0)
                        .setValue(HALF, DoubleBlockHalf.LOWER)
        );
    }

    /* ---------------- GROWTH ---------------- */

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER && state.getValue(AGE) < 4;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(AGE) < 4 && random.nextInt(5) == 0) {
            grow(level, pos, state);
        }
    }

    private void grow(ServerLevel level, BlockPos pos, BlockState state) {
        int newAge = Math.min(4, state.getValue(AGE) + 1);
        level.setBlock(pos, state.setValue(AGE, newAge), 2);

        if (newAge >= 1) {
            ensureUpper(level, pos, newAge);
        }
    }

    /* ---------------- BONEMEAL ---------------- */

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean client) {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER && state.getValue(AGE) < 4;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource rand, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource rand, BlockPos pos, BlockState state) {
        grow(level, pos, state);
    }

    /* ---------------- TALL LOGIC ---------------- */

    private void ensureUpper(Level level, BlockPos pos, int age) {
        BlockPos above = pos.above();

        if (level.getBlockState(above).isAir()) {
            level.setBlock(
                    above,
                    this.defaultBlockState()
                            .setValue(HALF, DoubleBlockHalf.UPPER)
                            .setValue(AGE, age),
                    3
            );
        }
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            Direction dir,
            BlockState other,
            LevelAccessor level,
            BlockPos pos,
            BlockPos otherPos
    ) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            BlockState below = level.getBlockState(pos.below());
            if (!below.is(this) || below.getValue(AGE) == 0) {
                return Blocks.AIR.defaultBlockState();
            }
        }
        return super.updateShape(state, dir, other, level, pos, otherPos);
    }

    /* ---------------- BREAKING ---------------- */

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            BlockPos above = pos.above();
            if (level.getBlockState(above).is(this)) {
                level.destroyBlock(above, false);
            }
        }
        super.onRemove(state, level, pos, newState, moving);
    }

    /* ---------------- SHAPES ---------------- */

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return state.getValue(AGE) == 0
                ? Block.box(3, 0, 3, 13, 8, 13)
                : Block.box(1, 0, 1, 15, 16, 15);
    }

    /* ---------------- BLOCKSTATE ---------------- */

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE, HALF);
    }
    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            BlockState below = level.getBlockState(pos.below());
            return below.is(this) && below.getValue(AGE) > 0;
        }
        return super.canSurvive(state, level, pos);
    }

}

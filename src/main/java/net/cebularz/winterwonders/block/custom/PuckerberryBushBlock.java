package net.cebularz.winterwonders.block.custom;

import net.cebularz.winterwonders.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PuckerberryBushBlock extends BushBlock implements BonemealableBlock {
    public static final IntegerProperty FORM = IntegerProperty.create("form", 0, 1);
    public static final IntegerProperty BERRIES = IntegerProperty.create("berries", 0, 2);
    public static final BooleanProperty TALL = BooleanProperty.create("tall");

    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    private static final VoxelShape SAPLING_SHAPE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 8.0D, 13.0D);
    private static final VoxelShape MID_GROWTH_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public PuckerberryBushBlock(Properties props) {
        super(props);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(HALF, DoubleBlockHalf.LOWER)
                        .setValue(FORM, 0)
                        .setValue(BERRIES, 0)
                        .setValue(TALL, false)
        );
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return new ItemStack(ModItems.PUCKERBERRY.get());
    }

    private static boolean isUpper(BlockState state) {
        return state.getValue(HALF) == DoubleBlockHalf.UPPER;
    }

    private boolean isTall(LevelReader level, BlockPos lowerPos) {
        BlockState state = level.getBlockState(lowerPos);
        if (!state.is(this)) return false;
        return state.getValue(TALL);
    }

    private BlockPos getOtherHalfPos(BlockPos pos, BlockState state) {
        return isUpper(state) ? pos.below() : pos.above();
    }

    private @Nullable BlockState getOtherHalfState(LevelReader level, BlockPos pos, BlockState state) {
        BlockPos otherPos = getOtherHalfPos(pos, state);
        BlockState otherState = level.getBlockState(otherPos);
        if (!otherState.is(this)) return null;
        if (isUpper(state) && otherState.getValue(HALF) != DoubleBlockHalf.LOWER) return null;
        if (!isUpper(state) && otherState.getValue(HALF) != DoubleBlockHalf.UPPER) return null;
        return otherState;
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState blockState, ServerLevel level, BlockPos blockPos, RandomSource randomSource) {
        if (level.getRawBrightness(blockPos.above(), 0) < 9) return;

        if (isUpper(blockState)) {
            BlockState belowPos = level.getBlockState(blockPos.below());

            if (!belowPos.is(this)
                    || belowPos.getValue(HALF) != DoubleBlockHalf.LOWER
                    || !belowPos.getValue(TALL)) {
                level.removeBlock(blockPos, false);
                return;
            }


            if (!blockState.getValue(TALL)) {
                blockState = blockState.setValue(TALL, true);
                level.setBlock(blockPos, blockState, 2);
            }

            if (randomSource.nextInt(5) != 0) return;
            if (blockState.getValue(FORM) == 0) {
                BlockState growState = blockState.setValue(FORM, 1).setValue(BERRIES, 0);
                level.setBlock(blockPos, growState, 2);
                level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(growState));
                return;
            }

            if (blockState.getValue(BERRIES) < 2) {
                BlockState growState = blockState.setValue(BERRIES, blockState.getValue(BERRIES) + 1);
                level.setBlock(blockPos, growState, 2);
                level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(growState));
            }
            return;
        }

        if (!blockState.getValue(TALL)) {
            if (randomSource.nextInt(5) != 0) return;
            int form = blockState.getValue(FORM);
            if (form < 1) {
                BlockState next = blockState.setValue(FORM, 1).setValue(BERRIES, 0).setValue(TALL, false);
                level.setBlock(blockPos, next, 2);
                level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(next));
                return;
            }


            if (!level.getBlockState(blockPos.above()).canBeReplaced()) return;
            BlockState newLower = blockState
                    .setValue(HALF, DoubleBlockHalf.LOWER)
                    .setValue(FORM, 1)
                    .setValue(BERRIES, 0)
                    .setValue(TALL, true);

            BlockState newUpper = defaultBlockState()
                    .setValue(HALF, DoubleBlockHalf.UPPER)
                    .setValue(FORM, 0)
                    .setValue(BERRIES, 0)
                    .setValue(TALL, true);

            level.setBlock(blockPos, newLower, 2);
            level.setBlock(blockPos.above(), newUpper, 2);
            level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(newLower));
            level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos.above(), GameEvent.Context.of(newUpper));
            return;
        }

        if (blockState.getValue(BERRIES) >= 2) return;
        if (randomSource.nextInt(5) != 0) return;
        if (blockState.getValue(BERRIES) == 0) {
            BlockState growState = blockState.setValue(BERRIES, 2);
            level.setBlock(blockPos, growState, 2);
            level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(growState));
        }
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (player.getItemInHand(interactionHand).is(Items.BONE_MEAL)) {
            return InteractionResult.PASS;
        }

        int growthStage = blockState.getValue(BERRIES);
        boolean isTall = (isUpper(blockState))
                ? (level.getBlockState(blockPos.below()).is(this) && level.getBlockState(blockPos.below()).getValue(HALF) == DoubleBlockHalf.LOWER)
                : isTall(level, blockPos);

        if (isTall && growthStage >= 2) {
            if (!level.isClientSide) {
                int berryCount = 1 + level.random.nextInt(2) + 1;
                popResource(level, blockPos, new ItemStack(ModItems.PUCKERBERRY.get(), berryCount));

                BlockState growState = blockState.setValue(BERRIES, 0);
                if (isUpper(blockState)) {
                    growState = growState.setValue(FORM, 1).setValue(BERRIES, 1);
                }

                level.setBlock(blockPos, growState, 2);
                level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(player, growState));
            }

            level.playSound(
                    null,
                    blockPos,
                    SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES,
                    SoundSource.BLOCKS,
                    1.0F,
                    0.8F + level.random.nextFloat() * 0.4F
            );

            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return super.use(blockState, level, blockPos, player, interactionHand, blockHitResult);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos blockPos, BlockState blockState, boolean isClient) {
        if (isUpper(blockState)) {
            BlockState belowPos = level.getBlockState(blockPos.below());
            return belowPos.is(this) && belowPos.getValue(HALF) == DoubleBlockHalf.LOWER && blockState.getValue(BERRIES) < 2;
        }

        if (!isTall(level, blockPos)) {
            return true;
        }

        return blockState.getValue(BERRIES) < 2;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel serverLevel, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {
        if (isUpper(blockState)) {
            BlockState belowPos = serverLevel.getBlockState(blockPos.below());
            if (!belowPos.is(this) || belowPos.getValue(HALF) != DoubleBlockHalf.LOWER || !belowPos.getValue(TALL)) return;

            if (!blockState.getValue(TALL)) {
                blockState = blockState.setValue(TALL, true);
            }

            if (blockState.getValue(FORM) == 0) {
                BlockState growState = blockState.setValue(FORM, 1).setValue(BERRIES, 0);
                serverLevel.setBlock(blockPos, growState, 2);
                serverLevel.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(growState));
                return;
            }

            if (blockState.getValue(BERRIES) < 2) {
                BlockState growState = blockState.setValue(BERRIES, 2);
                serverLevel.setBlock(blockPos, growState, 2);
                serverLevel.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(growState));
            }
            return;
        }


        if (!blockState.getValue(TALL)) {
            int form = blockState.getValue(FORM);

            if (form < 1) {
                BlockState growState = blockState.setValue(FORM, 1).setValue(BERRIES, 0).setValue(TALL, false);
                serverLevel.setBlock(blockPos, growState, 2);
                serverLevel.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(growState));
                return;
            }

            if (!serverLevel.getBlockState(blockPos.above()).canBeReplaced()) return;
            BlockState newLower = blockState
                    .setValue(HALF, DoubleBlockHalf.LOWER)
                    .setValue(FORM, 1)
                    .setValue(BERRIES, 0)
                    .setValue(TALL, true);

            BlockState newUpper = defaultBlockState()
                    .setValue(HALF, DoubleBlockHalf.UPPER)
                    .setValue(FORM, 0)
                    .setValue(BERRIES, 0)
                    .setValue(TALL, true);

            serverLevel.setBlock(blockPos, newLower, 2);
            serverLevel.setBlock(blockPos.above(), newUpper, 2);

            serverLevel.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(newLower));
            serverLevel.gameEvent(GameEvent.BLOCK_CHANGE, blockPos.above(), GameEvent.Context.of(newUpper));
            return;
        }

        if (blockState.getValue(BERRIES) < 2) {
            BlockState growState = blockState.setValue(BERRIES, 2);
            serverLevel.setBlock(blockPos, growState, 2);
            serverLevel.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(growState));
        }
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        if (!isUpper(blockState) && blockGetter instanceof LevelReader level && !isTall(level, blockPos)) {
            return (blockState.getValue(FORM) == 0) ? SAPLING_SHAPE : MID_GROWTH_SHAPE;
        }

        return super.getShape(blockState, blockGetter, blockPos, collisionContext);
    }

    @Override
    public boolean canSurvive(BlockState blockState, LevelReader level, BlockPos blockPos) {
        if (isUpper(blockState)) {
            BlockState below = level.getBlockState(blockPos.below());
            return below.is(this) && below.getValue(HALF) == DoubleBlockHalf.LOWER && super.canSurvive(below, level, blockPos.below());
        }

        return super.canSurvive(blockState, level, blockPos);
    }

    @Override
    public @NotNull BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState,
                                           LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        if (isUpper(pState) && pFacing == Direction.DOWN) {
            if (!pFacingState.is(this) || pFacingState.getValue(HALF) != DoubleBlockHalf.LOWER || !pFacingState.getValue(TALL)) {
                return Blocks.AIR.defaultBlockState();
            }
        }

        if (!isUpper(pState) && pFacing == Direction.UP) {
            if (!(pFacingState.is(this) && pFacingState.getValue(HALF) == DoubleBlockHalf.UPPER && pFacingState.getValue(TALL))) {
                return pState.setValue(TALL, false).setValue(FORM, 1).setValue(BERRIES, 0);
            }
        }

        return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }


    @Override
    public void playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
        if (!level.isClientSide) {
            BlockState otherState = getOtherHalfState(level, blockPos, blockState);
            if (otherState != null) {
                BlockPos otherPos = getOtherHalfPos(blockPos, blockState);
                level.setBlock(otherPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                level.levelEvent(player, 2001, otherPos, Block.getId(otherState));
            }
        }
        super.playerWillDestroy(level, blockPos, blockState, player);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HALF, FORM, BERRIES, TALL);
    }
}

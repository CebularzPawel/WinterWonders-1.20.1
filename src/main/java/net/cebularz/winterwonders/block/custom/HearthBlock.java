package net.cebularz.winterwonders.block.custom;

import net.cebularz.winterwonders.entity.ModEntities;
import net.cebularz.winterwonders.entity.custom.MittenMouseEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class HearthBlock extends Block {
    public static final EnumProperty<@NotNull Direction> FACING = BlockStateProperties.FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public HearthBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(LIT, Boolean.FALSE));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(LIT, Boolean.FALSE);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        ItemStack itemStack = pPlayer.getItemInHand(pHand);
        if (itemStack.getItem() instanceof FlintAndSteelItem && !pState.getValue(LIT)) {
            if (pLevel.isClientSide()) {
                return InteractionResult.SUCCESS;
            }

            pLevel.setBlock(pPos, pState.setValue(LIT, true), Block.UPDATE_ALL);
            pLevel.playSound(null, pPos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.PLAYERS, 1.0f, 1.0f);
            itemStack.hurtAndBreak(1, pPlayer, player -> player.broadcastBreakEvent(pHand));
            return InteractionResult.sidedSuccess(false);
        }

        if (itemStack.getItem() instanceof ShovelItem && pState.getValue(LIT)) {
            if (pLevel.isClientSide()) {
                pPlayer.swing(pHand, true);
                return InteractionResult.SUCCESS;
            }

            pPlayer.swing(pHand, true);
            pLevel.setBlock(pPos, pState.setValue(LIT, false), Block.UPDATE_ALL);
            pLevel.levelEvent(null, 1009, pPos, 0);
            return InteractionResult.sidedSuccess(false);
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource) {
        if (blockState.getValue(LIT)) {
            Direction facing = blockState.getValue(FACING);
            double frontOffset = 0.51;
            double y = 0.4;
            double sidewaysOffset = 0.3;

            int dx = facing.getStepX();
            int dy = facing.getStepY();
            int dz = facing.getStepZ();

            double x = blockPos.getX() + 0.5 + dx * frontOffset;
            double cy = blockPos.getY() + y + dy * 0.05;
            double z = blockPos.getZ() + 0.5 + dz * frontOffset;

            double sidewaysRandomOffset = (randomSource.nextDouble() - 0.5) * 2.0 * sidewaysOffset;
            if (facing.getAxis() == Direction.Axis.Z) {
                x += sidewaysRandomOffset;
            } else if (facing.getAxis() == Direction.Axis.X) {
                z += sidewaysRandomOffset;
            } else {
                x += sidewaysRandomOffset * 0.7;
                z += (randomSource.nextDouble() - 0.5) * 2.0 * sidewaysOffset * 0.7;
            }

            if (randomSource.nextFloat() < 0.35f) {
                level.addParticle(ParticleTypes.FLAME, x, cy, z, 0.0, 0.01, 0.0);
            }
            if (randomSource.nextFloat() < 0.20f) {
                level.addParticle(ParticleTypes.SMOKE, x, cy + 0.05, z, 0.0, 0.02, 0.0);
            }

            if (randomSource.nextInt(4) == 0) {
                float volume = 1.0f + (randomSource.nextFloat() - randomSource.nextFloat()) * 0.15f;
                level.playLocalSound(
                        blockPos.getX(), blockPos.getY(), blockPos.getZ(),
                        SoundEvents.CAMPFIRE_CRACKLE,
                        SoundSource.BLOCKS,
                        volume,
                        1.0f,
                        true
                );
            }
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState blockState) {
        return true;
    }

    @Override
    public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        if (!blockState.getValue(LIT)) {
            //System.out.println("[HEARTH] randomTick fired at " + blockPos);
            Direction facing = blockState.getValue(FACING);
            BlockPos frontPos = blockPos.relative(facing);

            MittenMouseEntity mouse = ModEntities.MITTEN_MOUSE.get().create(serverLevel);
            if (mouse == null) return;

            double x = frontPos.getX() + 0.5;
            double y = frontPos.getY();
            double z = frontPos.getZ() + 0.5;

            mouse.moveTo(x, y, z, facing.toYRot(), 0.0F);
            mouse.setVariant(randomSource.nextInt(3));
            mouse.setPersistenceRequired();
            serverLevel.addFreshEntity(mouse);

            serverLevel.sendParticles(ParticleTypes.CLOUD, x, y + 0.2, z, 18, 0.25, 0.25, 0.25, 0.02);
            serverLevel.sendParticles(ParticleTypes.SMOKE, x, y + 0.2, z, 10, 0.20, 0.20, 0.20, 0.01);
            float volume = 1.0f + (randomSource.nextFloat() - randomSource.nextFloat()) * 0.15f;
            float pitch = 1.0f + (randomSource.nextFloat() - randomSource.nextFloat()) * 0.2f;
            serverLevel.playSound(
                    null,
                    frontPos,
                    SoundEvents.BARREL_CLOSE,
                    SoundSource.BLOCKS,
                    volume,
                    pitch);
        }
    }
}

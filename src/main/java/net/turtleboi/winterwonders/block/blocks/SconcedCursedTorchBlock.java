package net.turtleboi.winterwonders.block.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class SconcedCursedTorchBlock extends TorchBlock {
    private final Supplier<SimpleParticleType> particleTypeSupplier;

    public SconcedCursedTorchBlock(Properties properties, Supplier<SimpleParticleType> particleTypeSupplier) {
        super(properties, ParticleTypes.FLAME);
        this.particleTypeSupplier = particleTypeSupplier;
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        SimpleParticleType particle = particleTypeSupplier.get();
        double d0 = (double)pPos.getX() + 0.5D;
        double d1 = (double)pPos.getY() + 0.7D;
        double d2 = (double)pPos.getZ() + 0.5D;
        pLevel.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        pLevel.addParticle(particle, d0, d1, d2, 0.0D, 0.0D, 0.0D);
    }
}


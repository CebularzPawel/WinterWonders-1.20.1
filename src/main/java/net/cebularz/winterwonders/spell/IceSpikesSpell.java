package net.cebularz.winterwonders.spell;

import net.cebularz.winterwonders.network.ModNetworking;
import net.cebularz.winterwonders.network.packets.IceSpikeVisualS2C;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.turtleboi.turtlecore.effect.CoreEffects;
import net.turtleboi.turtlecore.spell.Spell;
import net.turtleboi.turtlecore.spell.SpellScheduler;

import java.util.List;

public class IceSpikesSpell implements Spell {
    private final int spikeCount;
    private final float damage;
    private final double arcDegrees;
    private final double minRadius;
    private final double maxRadius;
    private final boolean canDamageAllies;

    public IceSpikesSpell() {
        this(18, 8.0F, 90.0, 1.0, 6.0, false);
    }

    public IceSpikesSpell(int spikeCount, float damage, double arcDegrees, double minRadius, double maxRadius, boolean canDamageAllies) {
        this.spikeCount = Math.max(1, spikeCount);
        this.damage = Math.max(0.0F, damage);
        this.arcDegrees = arcDegrees;
        this.minRadius = minRadius;
        this.maxRadius = maxRadius;
        this.canDamageAllies = canDamageAllies;
    }

    @Override
    public void cast(ServerLevel level, LivingEntity caster, LivingEntity target, RandomSource random) {
        if (level == null || caster == null || target == null || random == null) return;
        if (!caster.isAlive() || !target.isAlive()) return;

        Vec3 casterPos = caster.position();
        Vec3 targetPos = target.position();
        double dx = targetPos.x - casterPos.x;
        double dz = targetPos.z - casterPos.z;

        double centerAngle = Math.atan2(dz, dx);
        double distance = Math.sqrt(dx * dx + dz * dz);
        double baseRadius = Mth.clamp(distance, minRadius, maxRadius);

        double arcRadians = Math.toRadians(arcDegrees);
        boolean reverseSweep = random.nextBoolean();

        for (int i = 0; i < spikeCount; i++) {
            double totalSpikes = (spikeCount == 1) ? 0.0 : ((double) i / (spikeCount - 1) - 0.5);

            double angle = centerAngle + totalSpikes * arcRadians;
            double spikeX = casterPos.x + Math.cos(angle) * baseRadius;
            double spikeZ = casterPos.z + Math.sin(angle) * baseRadius;

            BlockPos spikePos = new BlockPos(Mth.floor(spikeX), caster.blockPosition().getY(), Mth.floor(spikeZ));
            double dxSpike = spikeX - casterPos.x;
            double dzSpike = spikeZ - casterPos.z;
            float yawDegrees = (float) Math.toDegrees(Math.atan2(dxSpike, dzSpike));

            long baseDelay = reverseSweep ? (spikeCount - 1 - i) : i;
            for (int stage = 0; stage < 3; stage++) {
                int spellStage = stage;
                long delay = baseDelay + spellStage * 2;

                SpellScheduler.schedule(level, delay, () -> {
                    if (caster.isAlive()) {
                        doIceSpikeStage(level, caster, spikePos, damage, spellStage, yawDegrees, random);
                    }
                });
            }
        }
    }

    private void doIceSpikeStage(ServerLevel level, LivingEntity caster, BlockPos blockPos, float damage, int stage, float yawDegrees, RandomSource random) {
        level.sendParticles(
                ParticleTypes.ITEM_SNOWBALL,
                blockPos.getX() + 0.5,
                blockPos.getY() + stage * 0.5,
                blockPos.getZ() + 0.5,
                15, 0.2, 0.1, 0.2, 0.05
        );

        level.sendParticles(
                ParticleTypes.SNOWFLAKE,
                blockPos.getX() + 0.5,
                blockPos.getY() + stage * 0.5 + 0.25,
                blockPos.getZ() + 0.5,
                5, 0.3, 0.1, 0.3, 0.01
        );

        if (stage == 0) {
            level.playSound(
                    null,
                    blockPos,
                    SoundEvents.PLAYER_HURT_FREEZE,
                    SoundSource.BLOCKS,
                    1.0F,
                    1.2F + random.nextFloat() * 0.2F
            );

            BlockState below = level.getBlockState(blockPos.below());
            if (below.getFluidState().getType() == Fluids.WATER) {
                level.setBlockAndUpdate(blockPos.below(), Blocks.ICE.defaultBlockState());
                level.setBlockAndUpdate(blockPos, Blocks.ICE.defaultBlockState());
            } else if (below.hasProperty(BlockStateProperties.WATERLOGGED)
                    && below.getValue(BlockStateProperties.WATERLOGGED)) {

                level.setBlock(
                        blockPos.below(),
                        below.setValue(BlockStateProperties.WATERLOGGED, Boolean.FALSE),
                        Block.UPDATE_ALL
                );
            }
        }


        if (stage == 2) {
            ModNetworking.sendNear(new IceSpikeVisualS2C(blockPos, yawDegrees), caster);
            AABB damageArea = new AABB(
                    blockPos.getX() - 1, blockPos.getY(), blockPos.getZ() - 1,
                    blockPos.getX() + 1, blockPos.getY() + 2.5, blockPos.getZ() + 1
            );

            List<Entity> entities = level.getEntities(
                    caster, damageArea, e -> e instanceof LivingEntity
            );

            for (Entity entity : entities) {
                LivingEntity victim = (LivingEntity) entity;
                if (victim == caster) continue;
                if (!canDamageAllies && victim.isAlliedTo(caster)) continue;

                victim.hurt(victim.damageSources().indirectMagic(caster, caster), damage);
                Vec3 knockbackDir = victim.position().subtract(caster.position());
                if (knockbackDir.lengthSqr() < 1.0e-4) {
                    knockbackDir = new Vec3(0.0, 0.0, 1.0);
                }

                knockbackDir = knockbackDir.normalize();
                victim.setDeltaMovement(
                        victim.getDeltaMovement().add(
                                knockbackDir.x * 1.1,
                                0.35,
                                knockbackDir.z * 1.1
                        )
                );

                victim.hurtMarked = true;
                victim.addEffect(new MobEffectInstance(CoreEffects.CHILLED.get(), 120, 1));

                level.playSound(
                        null,
                        victim.blockPosition(),
                        SoundEvents.PLAYER_HURT_FREEZE,
                        SoundSource.PLAYERS,
                        0.8F,
                        1.0F
                );
            }
        }
    }
}

package net.cebularz.winterwonders.item.custom;

import net.cebularz.winterwonders.client.renderer.util.ParticleSpawnQueue;
import net.cebularz.winterwonders.entity.custom.projectile.IceSpikeProjectileEntity;
import net.cebularz.winterwonders.init.ModEffects;
import net.cebularz.winterwonders.network.ModNetworking;
import net.cebularz.winterwonders.network.packets.SendParticlesS2C;
import net.cebularz.winterwonders.particle.ModParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class BlizzardStaffItem extends Item {
    public BlizzardStaffItem(Properties pProperties) {
        super(pProperties.durability(128));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);

        pPlayer.getCooldowns().addCooldown(this, 40);

        itemStack.hurtAndBreak(1, pPlayer,
                (player) -> player.broadcastBreakEvent(pUsedHand)
        );

        pLevel.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(),
                SoundEvents.EVOKER_CAST_SPELL, SoundSource.NEUTRAL, 0.75f, 0.4f / (pLevel.getRandom().nextFloat() * 0.4f + 0.8f));

        if(!pLevel.isClientSide){
            for (int i = 0; i < 20; i++) {
                ModNetworking.sendToNear(new SendParticlesS2C(
                        ParticleTypes.SNOWFLAKE,
                        pPlayer.getX(),
                        (pPlayer.getY() + pPlayer.getBbHeight() / 2),
                        pPlayer.getZ(),
                        1.0, 0.5, 1.0
                ), pPlayer);
            }

            double maxRadius = 6.0;
            long startTime = System.currentTimeMillis();
            int totalDegrees = 360;
            double rotationSpeed = Math.toRadians(totalDegrees);
            int stepAdvanceInterval = 3;
            int minParticles = 1;
            int maxParticles = 24;
            RandomSource random = pPlayer.getRandom();

            for (int step = 0; step < totalDegrees; step += stepAdvanceInterval) {
                int delay = step * stepAdvanceInterval;
                int finalStep = step;
                ParticleSpawnQueue.schedule(delay, () -> {
                    double timeElapsed = (System.currentTimeMillis() - startTime) / 1000.0;
                    double particleAngle = Math.toRadians(finalStep) + rotationSpeed * timeElapsed;
                    double progress = Math.min(1.0, timeElapsed / ((double) (totalDegrees * stepAdvanceInterval) / 1000.0));
                    double newRadius = maxRadius * progress;
                    double x = pPlayer.getX() + Math.sin(particleAngle) * newRadius;
                    double y = pPlayer.getY() + (timeElapsed * 5);
                    double z = pPlayer.getZ() + Math.cos(particleAngle) * newRadius;

                    int particleCount = (int) (minParticles + (maxParticles - minParticles) * progress);
                    double spread = 0.25 + (1.0 - 0.25) * progress;
                    for (int i = 0; i < particleCount; i++) {
                        double particleX = x + (random.nextDouble() - 0.5) * spread;
                        double particleY = pPlayer.getY() + (random.nextDouble() - 0.5) * spread + (timeElapsed * 5);
                        double particleZ = z + (random.nextDouble() - 0.5) * spread;

                        ModNetworking.sendToNear(new SendParticlesS2C(
                                ModParticles.CHILLED_PARTICLES.get(),
                                particleX, particleY, particleZ,
                                0, 0, 0), pPlayer);
                    }

                    AABB whirlwindArea = pPlayer.getBoundingBox().inflate(maxRadius);

                    List<LivingEntity> targets = pPlayer.level().getEntitiesOfClass(LivingEntity.class, whirlwindArea);
                    double attractionStrength = Math.max(0.02, 0.05 * progress);
                    for (LivingEntity livingTargets : targets) {
                        if (livingTargets != pPlayer) {
                            double dx = x - livingTargets.getX();
                            double dy = y - livingTargets.getY();
                            double dz = z - livingTargets.getZ();

                            livingTargets.setDeltaMovement(livingTargets.getDeltaMovement().add(
                                    dx * attractionStrength, dy * attractionStrength, dz * attractionStrength));
                            livingTargets.hurtMarked = true;

                            if (!livingTargets.hasEffect(ModEffects.CHILLED.get())){
                                livingTargets.addEffect(new MobEffectInstance(
                                        ModEffects.CHILLED.get(),
                                        300,
                                        2
                                ));
                            } else if (livingTargets.hasEffect(ModEffects.CHILLED.get()) && livingTargets.getEffect(ModEffects.CHILLED.get()).getAmplifier() <= 2) {
                                livingTargets.addEffect(new MobEffectInstance(
                                        ModEffects.CHILLED.get(),
                                        300,
                                        2,
                                        livingTargets.getEffect(ModEffects.CHILLED.get()).isAmbient(),
                                        livingTargets.getEffect(ModEffects.CHILLED.get()).isVisible(),
                                        livingTargets.getEffect(ModEffects.CHILLED.get()).showIcon()
                                ));
                            }
                        }
                    }
                });
            }
        }

        return InteractionResultHolder.sidedSuccess(itemStack, pLevel.isClientSide());
    }
}


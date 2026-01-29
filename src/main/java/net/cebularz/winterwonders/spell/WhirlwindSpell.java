package net.cebularz.winterwonders.spell;

import net.cebularz.winterwonders.entity.custom.LichEntity;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.turtleboi.turtlecore.network.CoreNetworking;
import net.turtleboi.turtlecore.network.packet.util.SendParticlesS2C;
import net.turtleboi.turtlecore.spell.Spell;
import net.turtleboi.turtlecore.spell.SpellScheduler;

import java.util.List;

public class WhirlwindSpell implements Spell {
    private final double maxRadius;
    private final ParticleOptions particle;
    private final boolean canAffectAllies;

    private final MobEffect effect;
    private final Integer effectDuration;
    private final Integer effectAmplifier;

    public WhirlwindSpell(double maxRadius, ParticleOptions particle, boolean canAffectAllies, MobEffect effect, Integer effectDuration, Integer effectAmplifier) {
        this.maxRadius = Math.max(0.5, maxRadius);
        this.particle = (particle != null) ? particle : ParticleTypes.SNOWFLAKE;
        this.canAffectAllies = canAffectAllies;

        this.effect = effect;
        this.effectDuration = effectDuration;
        this.effectAmplifier = effectAmplifier;
    }

    @Override
    public void cast(ServerLevel level, LivingEntity caster, LivingEntity target, RandomSource random) {
        if (level == null || caster == null || random == null) return;
        if (!caster.isAlive()) return;

        final int durationTicks = 20;
        final int stepIntervalTicks = 1;

        final double swirlHeight = 5.0;
        final double baseAttraction = 0.125;
        final double maxAttraction = 0.25;

        final int minParticles = 1;
        final int maxParticles = 24;
        final double minSpread = 0.25;
        final double maxSpread = 1.0;

        for (int i = 0; i < 4; i++) {
            level.sendParticles(
                    ParticleTypes.SNOWFLAKE,
                    caster.getX(), caster.getY() + caster.getBbHeight() / 2.0, caster.getZ(),
                    20, 1.0, 0.5, 1.0, 0.2
            );
        }

        for (int tick = 0; tick <= durationTicks; tick += stepIntervalTicks) {
            int scheduledTick = tick;

            SpellScheduler.schedule(level, scheduledTick, () -> {
                if (!caster.isAlive()) return;

                double progress = Mth.clamp((double) scheduledTick / (double) durationTicks, 0.0, 1.0);
                double angle = progress * Math.PI * 2.0;
                double radius = maxRadius * progress;

                double x = caster.getX() + Math.sin(angle) * radius;
                double z = caster.getZ() + Math.cos(angle) * radius;
                double y = caster.getY() + (swirlHeight * progress);

                int particleCount = (int) Mth.lerp(progress, minParticles, maxParticles);
                double spread = Mth.lerp(progress, minSpread, maxSpread);

                for (int i = 0; i < particleCount; i++) {
                    double px = x + (random.nextDouble() - 0.5) * spread;
                    double py = y + (random.nextDouble() - 0.5) * spread;
                    double pz = z + (random.nextDouble() - 0.5) * spread;

                    CoreNetworking.sendToNear(new SendParticlesS2C(
                            particle,
                            px, py, pz,
                            0, 0, 0
                    ), caster);
                }

                AABB area = caster.getBoundingBox().inflate(maxRadius);
                List<LivingEntity> victims = caster.level().getEntitiesOfClass(LivingEntity.class, area);

                double attractionStrength = Math.max(baseAttraction, maxAttraction * progress);
                for (LivingEntity victim : victims) {
                    if (victim == caster) continue;
                    if (!canAffectAllies && victim.isAlliedTo(caster)) continue;
                    if (caster instanceof LichEntity lichEntity && lichEntity.isMinion(victim))continue;

                    double dx = x - victim.getX();
                    double dy = y - victim.getY();
                    double dz = z - victim.getZ();

                    victim.setDeltaMovement(victim.getDeltaMovement().add(
                            dx * attractionStrength,
                            dy * attractionStrength,
                            dz * attractionStrength
                    ));
                    victim.hurtMarked = true;

                    applyOptionalEffect(victim);
                }
            });
        }
    }

    private void applyOptionalEffect(LivingEntity victim) {
        if (effect == null || effectDuration == null || effectAmplifier == null) return;

        MobEffectInstance existing = victim.getEffect(effect);

        if (existing == null) {
            victim.addEffect(new MobEffectInstance(effect, effectDuration, effectAmplifier));
            return;
        }

        if (existing.getAmplifier() <= effectAmplifier) {
            victim.addEffect(new MobEffectInstance(
                    effect,
                    effectDuration,
                    effectAmplifier,
                    existing.isAmbient(),
                    existing.isVisible(),
                    existing.showIcon()
            ));
        }
    }
}

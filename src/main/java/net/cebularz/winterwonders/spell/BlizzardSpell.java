package net.cebularz.winterwonders.spell;

import net.cebularz.winterwonders.entity.custom.projectile.IcicleProjectileEntity;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.turtleboi.turtlecore.spell.Spell;
import net.turtleboi.turtlecore.spell.SpellScheduler;

public class BlizzardSpell implements Spell {
    private final double radius;
    private final double heightAboveTarget;

    private final int durationTicks;
    private final int cloudIntervalTicks;
    private final int icicleIntervalTicks;

    private final int cloudParticlesPerBurst;
    private final ParticleOptions cloudParticle;

    private final float damagePercent;
    private final double icicleSpeed;
    private final double angleSpread;
    private final boolean homing;

    public BlizzardSpell(double radius, double heightAboveTarget, int durationTicks, int cloudIntervalTicks, int icicleIntervalTicks,
                         int cloudParticlesPerBurst, ParticleOptions cloudParticle, float damagePercent, double icicleSpeed, double angleSpread, boolean homing) {
        this.radius = Math.max(0.5, radius);
        this.heightAboveTarget = Math.max(1.0, heightAboveTarget);

        this.durationTicks = Math.max(1, durationTicks);
        this.cloudIntervalTicks = Math.max(1, cloudIntervalTicks);
        this.icicleIntervalTicks = Math.max(1, icicleIntervalTicks);

        this.cloudParticlesPerBurst = Math.max(1, cloudParticlesPerBurst);
        this.cloudParticle = cloudParticle == null ? ParticleTypes.SNOWFLAKE : cloudParticle;

        this.damagePercent = Math.max(0.0F, damagePercent);
        this.icicleSpeed = Math.max(0.05, icicleSpeed);
        this.angleSpread = Math.max(0.0, angleSpread);
        this.homing = homing;
    }

    @Override
    public void cast(ServerLevel level, LivingEntity caster, LivingEntity target, RandomSource random) {
        if (level == null || caster == null || target == null || random == null) return;
        if (!caster.isAlive() || !target.isAlive()) return;

        final double domeHeight = radius * 0.6;
        final double shearStrength = Math.min(radius * 0.65, 3.5);
        Vec3 windDir = new Vec3(random.nextDouble() - 0.5, 0.0, random.nextDouble() - 0.5);
        if (windDir.lengthSqr() < 1.0e-6) windDir = new Vec3(1, 0, 0);
        windDir = windDir.normalize();

        for (int tick = 0; tick <= durationTicks; tick++) {
            if (tick % cloudIntervalTicks == 0) {
                Vec3 finalWindDir = windDir;
                SpellScheduler.schedule(level, tick, () -> {
                    if (!caster.isAlive() || !target.isAlive()) return;
                    Vec3 center = target.position().add(0, target.getBbHeight() + heightAboveTarget, 0);
                    spawnCloudParticles(level, center, domeHeight, finalWindDir, shearStrength, random);
                });
            }

            if (tick % icicleIntervalTicks == 0) {
                Vec3 finalWindDir = windDir;
                SpellScheduler.schedule(level, tick, () -> {
                    if (!caster.isAlive() || !target.isAlive()) return;

                    Vec3 center = target.position().add(0, target.getBbHeight() + heightAboveTarget, 0);
                    spawnIcicle(level, caster, target, center, domeHeight, finalWindDir, shearStrength, random);
                });
            }
        }
    }

    private void spawnCloudParticles(ServerLevel level, Vec3 center, double domeHeight, Vec3 windDir, double shearStrength, RandomSource random) {
        for (int i = 0; i < cloudParticlesPerBurst; i++) {
            Vec3 randomPoint = randomPointInCloud(center, radius, domeHeight, windDir, shearStrength, random);

            level.sendParticles(
                    cloudParticle,
                    randomPoint.x, randomPoint.y, randomPoint.z,
                    1,
                    0.0, 0.0, 0.0,
                    0.0
            );
        }
    }

    private void spawnIcicle(ServerLevel level, LivingEntity caster, LivingEntity target, Vec3 cloudCenter,
                             double domeHeight, Vec3 windDir, double shearStrength, RandomSource random) {
        Vec3 spawnPos = randomPointInCloud(cloudCenter, radius, domeHeight, windDir, shearStrength, random);
        IcicleProjectileEntity icicle = new IcicleProjectileEntity(level, caster, damagePercent / 100.0F, homing);
        if (homing) icicle.setHomingTarget(target);
        icicle.setPos(spawnPos.x, spawnPos.y, spawnPos.z);

        final double impactRadius = Math.max(0.35, radius * 0.22);
        final double impactHeightJitter = 0.45;
        final double biasExp = 3.2;
        final double leadTicks = 7.0;
        final double inaccuracy = Math.max(0.0, angleSpread);

        Vec3 targetMid = target.position().add(0, target.getBbHeight() * 0.45, 0);
        Vec3 lead = target.getDeltaMovement().scale(leadTicks);
        Vec3 aimCenter = targetMid.add(lead.x, 0.0, lead.z);

        double ru = Math.pow(random.nextDouble(), biasExp) * impactRadius;
        double rt = random.nextDouble() * (Math.PI * 2.0);

        double ox = Math.cos(rt) * ru;
        double oz = Math.sin(rt) * ru;
        double oy = (random.nextDouble() - 0.5) * impactHeightJitter;

        Vec3 impactPoint = aimCenter.add(ox, oy, oz);

        Vec3 toImpact = impactPoint.subtract(spawnPos);
        if (toImpact.lengthSqr() < 1.0e-6) {
            toImpact = new Vec3(0.0, -1.0, 0.0);
        }

        Vec3 dir = toImpact.normalize();
        if (inaccuracy > 0.0) {
            Vec3 jitter = new Vec3(
                    random.nextGaussian() * inaccuracy,
                    random.nextGaussian() * (inaccuracy * 0.30),
                    random.nextGaussian() * inaccuracy
            );
            dir = dir.add(jitter).normalize();
        }

        icicle.setDeltaMovement(dir.scale(icicleSpeed));
        level.addFreshEntity(icicle);
    }

    private static Vec3 randomPointInCloud(Vec3 center, double radius, double domeHeight, Vec3 windDir, double shearStrength, RandomSource random) {
        double u = random.nextDouble();
        double v = random.nextDouble();
        double randomizedRadius = Math.sqrt(u) * radius;
        double theta = v * (Math.PI * 2.0);

        double localX = Math.cos(theta) * randomizedRadius;
        double localZ = Math.sin(theta) * randomizedRadius;

        double denominator = radius * radius;
        double t = (denominator <= 1.0e-6) ? 0.0 : (randomizedRadius * randomizedRadius) / denominator;
        double domeMaxY = Math.sqrt(Math.max(0.0, 1.0 - t)) * domeHeight;


        double localY = random.nextDouble() * domeMaxY;
        double shearT = (domeHeight <= 1.0e-6) ? 0.0 : (localY / domeHeight);
        double shearX = windDir.x * shearStrength * shearT;
        double shearZ = windDir.z * shearStrength * shearT;

        return new Vec3(
                center.x + localX + shearX,
                center.y + localY,
                center.z + localZ + shearZ
        );
    }
}

package net.cebularz.winterwonders.spell;

import net.cebularz.winterwonders.entity.custom.projectile.ChillingSnowballEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.turtleboi.turtlecore.spell.Spell;
import net.turtleboi.turtlecore.spell.SpellScheduler;

public class SnowballSpell implements Spell {
    private final CastMode mode;
    private final int shots;
    private final int intervalTicks;

    private final float damagePercent;
    private final int chillAmplifier;
    private final double speed;
    private final double aimAccuracy;

    public SnowballSpell(CastMode mode, int shots, int intervalTicks, float damagePercent, int chillAmplifier, double speed, double aimAccuracy) {
        this.mode = mode;
        this.shots = Math.max(1, shots);
        this.intervalTicks = Math.max(0, intervalTicks);

        this.damagePercent = damagePercent;
        this.chillAmplifier = Math.max(0, chillAmplifier);
        this.speed = speed;
        this.aimAccuracy = Math.max(0.0, aimAccuracy);
    }

    @Override
    public void cast(ServerLevel level, LivingEntity caster, LivingEntity target, RandomSource random) {
        if (level == null || caster == null || random == null) return;
        if (!caster.isAlive()) return;

        if (mode == CastMode.SINGLE) {
            singularCast(level, caster, target, random);
            return;
        }

        for (int i = 0; i < shots; i++) {
            int delay = i * intervalTicks;
            SpellScheduler.schedule(level, delay, () -> {
                if (!caster.isAlive()) return;
                singularCast(level, caster, target, random);
            });
        }
    }

    private void singularCast(ServerLevel level, LivingEntity caster, LivingEntity target, RandomSource random) {
        ChillingSnowballEntity snowball = new ChillingSnowballEntity(level, caster, chillAmplifier, true, (int) damagePercent);
        Vec3 eyePos = caster.getEyePosition(1.0F);
        Vec3 normalizedDir = caster.getLookAngle().normalize();
        Vec3 spawnPos = eyePos.add(normalizedDir.scale(0.35));
        snowball.setPos(spawnPos.x, spawnPos.y, spawnPos.z);

        Vec3 dir;
        if (target != null && target.isAlive() && !target.isRemoved()) {
            Vec3 aimPos = target.position().add(
                    random.nextGaussian() * aimAccuracy,
                    target.getBbHeight() * 0.5,
                    random.nextGaussian() * aimAccuracy
            );
            dir = aimPos.subtract(eyePos).normalize();
        } else {
            dir = normalizedDir;
        }

        snowball.setDeltaMovement(dir.scale(speed));
        level.addFreshEntity(snowball);

        level.sendParticles(
                ParticleTypes.SNOWFLAKE,
                spawnPos.x, spawnPos.y, spawnPos.z,
                10, 0.2, 0.2, 0.2, 0.1
        );
    }
}

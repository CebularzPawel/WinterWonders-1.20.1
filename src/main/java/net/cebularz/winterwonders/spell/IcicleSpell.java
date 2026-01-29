package net.cebularz.winterwonders.spell;

import net.cebularz.winterwonders.entity.custom.projectile.IcicleProjectileEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.turtleboi.turtlecore.particle.CoreParticles;
import net.turtleboi.turtlecore.spell.Spell;
import net.turtleboi.turtlecore.spell.SpellScheduler;

public class IcicleSpell implements Spell {
    private final CastMode mode;
    private final int shots;
    private final int intervalTicks;
    private final float damagePercent;
    private final boolean homing;
    private final double speed;
    private final double aimAccuracy;

    public IcicleSpell(CastMode mode, int shots, int intervalTicks, float damagePercent, boolean homing, double speed, double aimAccuracy) {
        this.mode = mode;
        this.shots = Math.max(1, shots);
        this.intervalTicks = Math.max(0, intervalTicks);

        this.damagePercent = damagePercent;
        this.homing = homing;
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
        IcicleProjectileEntity icicle = new IcicleProjectileEntity(level, caster, damagePercent / 100.0F, homing);
        Vec3 eyePos = caster.getEyePosition(1.0F);
        Vec3 normalizedDir = caster.getLookAngle().normalize();
        Vec3 spawnPos = eyePos.add(normalizedDir.scale(0.35));
        icicle.setPos(spawnPos.x, spawnPos.y, spawnPos.z);

        Vec3 dir;
        boolean hasValidTarget = target != null && target.isAlive() && !target.isRemoved();
        if (hasValidTarget) {
            Vec3 aimPos = target.position().add(
                    random.nextGaussian() * aimAccuracy, target.getBbHeight() * 0.5, random.nextGaussian() * aimAccuracy);
            dir = aimPos.subtract(eyePos).normalize();
        } else {
            dir = normalizedDir;
        }

        if (homing && hasValidTarget) {
            icicle.setHomingTarget(target);
        }

        icicle.setDeltaMovement(dir.scale(speed));
        level.addFreshEntity(icicle);
        level.sendParticles(
                CoreParticles.CHILLED_PARTICLES.get(),
                spawnPos.x, spawnPos.y, spawnPos.z,
                20, 0.2, 0.2, 0.2, 0.1
        );
    }
}

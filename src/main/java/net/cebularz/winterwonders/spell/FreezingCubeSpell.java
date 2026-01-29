package net.cebularz.winterwonders.spell;

import net.cebularz.winterwonders.entity.ModEntities;
import net.cebularz.winterwonders.entity.custom.projectile.IceCubeEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.turtleboi.turtlecore.spell.Spell;
import net.turtleboi.turtlecore.spell.SpellScheduler;

public class FreezingCubeSpell implements Spell {
    private final float cubeHealth;
    private final int windupTicks;
    private final double speed;
    private final double targetHitRadius;
    private final int freezeTicks;
    private final boolean canAffectAllies;

    private final int cubeCount;
    private final int minDelayTicks;
    private final int maxDelayTicks;

    public FreezingCubeSpell(float cubeHealth, int windupTicks, double speed, double targetHitRadius, int freezeTicks, boolean canAffectAllies,
                             int cubeCount, int minReleaseDelayTicks, int maxReleaseDelayTicks) {
        this.cubeHealth = Math.max(1.0F, cubeHealth);
        this.windupTicks = Math.max(0, windupTicks);
        this.speed = Math.max(0.05, speed);
        this.targetHitRadius = Math.max(0.5, targetHitRadius);
        this.freezeTicks = Math.max(20, freezeTicks);
        this.canAffectAllies = canAffectAllies;

        this.cubeCount = Mth.clamp(cubeCount, 1, 12);
        this.minDelayTicks = Math.max(0, minReleaseDelayTicks);
        this.maxDelayTicks = Math.max(this.minDelayTicks, maxReleaseDelayTicks);
    }

    @Override
    public void cast(ServerLevel level, LivingEntity caster, LivingEntity target, RandomSource random) {
        if (level == null || caster == null || target == null || random == null) return;
        if (!caster.isAlive() || !target.isAlive()) return;
        if (!canAffectAllies && target.isAlliedTo(caster)) return;

        for (int i = 0; i < cubeCount; i++) {
            IceCubeEntity cube = ModEntities.ICE_CUBE.get().create(level);
            if (cube == null) continue;

            cube.setOwner(caster);
            cube.setTarget(target);
            cube.configureStats(cubeHealth, windupTicks, speed, targetHitRadius, freezeTicks, canAffectAllies);

            cube.setArcSlot(i, cubeCount);
            Vec3 spawnPos = caster.position().add(0, caster.getBbHeight() + 1.45, 0);
            cube.setPos(spawnPos.x, spawnPos.y, spawnPos.z);

            level.addFreshEntity(cube);

            int delay = random.nextIntBetweenInclusive(minDelayTicks, maxDelayTicks) + (i * random.nextIntBetweenInclusive(minDelayTicks, maxDelayTicks));
            SpellScheduler.schedule(level, delay, () -> {
                if (!caster.isAlive() || !target.isAlive() || cube.isRemoved()) return;
                cube.releaseNow();
            });
        }
    }
}

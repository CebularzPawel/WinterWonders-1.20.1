package net.cebularz.winterwonders.spell.lich;

import java.util.Arrays;
import java.util.function.BooleanSupplier;

import net.cebularz.winterwonders.entity.ai.lich.LichAttackGoal;
import net.cebularz.winterwonders.entity.custom.LichEntity.CastState.Kind;
import net.cebularz.winterwonders.entity.custom.LichEntity.CastState;
import net.cebularz.winterwonders.entity.custom.LichEntity;
import net.cebularz.winterwonders.spell.*;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;

import net.turtleboi.turtlecore.effect.CoreEffects;
import net.turtleboi.turtlecore.particle.CoreParticles;
import net.turtleboi.turtlecore.spell.Spell.CastMode;

public final class LichSpellbook {
    private static final double snowballSpeed = 2.125;
    private static final double icicleSpeed = 3.25;
    public static final SnowballSpell SNOWBALL_PHASE1 =
            new SnowballSpell(CastMode.VOLLEY, 4, 10, 5.0F, 0, snowballSpeed, 0.15);
    public static final SnowballSpell SNOWBALL_PHASE2 =
            new SnowballSpell(CastMode.VOLLEY, 4, 5, 10.0F, 2, snowballSpeed * 1.25, 0.0);

    public static final IcicleSpell ICICLE_PHASE1 =
            new IcicleSpell(CastMode.VOLLEY, 4, 10, 10.0F, false, icicleSpeed, 0.15);
    public static final IcicleSpell ICICLE_PHASE2 =
            new IcicleSpell(CastMode.VOLLEY, 4, 5, 20.0F, true, icicleSpeed * 1.25, 0.0);

    public static final IceSpikesSpell ICE_SPIKES = new IceSpikesSpell();

    public static final FreezingCubeSpell FREEZING_CUBE_PHASE1 =
            new FreezingCubeSpell(10.0F, 20, 0.1, 1.0, 100,
                    false, 1, 20, 20);
    public static final FreezingCubeSpell FREEZING_CUBE_PHASE2 =
            new FreezingCubeSpell(16.0F, 20, 0.125, 1.0, 100,
                    false, 3, 20, 60);

    public static final BlizzardSpell BLIZZARD =
            new BlizzardSpell(6.0, 6.0, 200, 2, 2, 120,
                    ParticleTypes.SNOWFLAKE, 12.0F, 1.65, 0.25, false);

    public static final WhirlwindSpell WHIRLWIND = new WhirlwindSpell(
            6.0, CoreParticles.CHILLED_PARTICLES.get(), false, CoreEffects.CHILLED.get(), 300, 2);

    public static final SummonMinionsSpell SUMMON_MINIONS = new SummonMinionsSpell(2, 2, 2.0, true);

    public static Kind animationForAttack(LichAttackGoal.AttackType type, LichEntity caster, LivingEntity target) {
        return switch (type) {
            case BASIC_PROJECTILE -> target.hasEffect(CoreEffects.FROZEN.get()) || caster.getRandom().nextFloat() < .5F ? Kind.ICICLE : Kind.SNOWBALL;
            case SPECIAL_ATTACK, FREEZING_CUBE -> Kind.CUBES;
            case ICE_SPIKES -> Kind.SPIKES;
            case BLIZZARD -> Kind.BLIZZARD;
            case WHIRLWIND -> Kind.WHIRLWIND;
            case SUMMON_MINIONS -> Kind.SUMMON;
        };
    }

    public static int windupTicks(Kind kind) {
        return switch (kind) {
            case SNOWBALL, ICICLE -> 16;
            case CUBES -> 8;
            case SPIKES, WHIRLWIND -> 18;
            case BLIZZARD -> 22;
            case SUMMON -> 20;
            default -> 0;
        };
    }

    public static CastState prepare(Kind kind, long started, int serial, int phase, boolean reverse, RandomSource random) {
        int[] releases;
        if (kind == Kind.SNOWBALL || kind == Kind.ICICLE) {
            int interval = phase >= 2 ? 5 : 10;
            releases = new int[4];
            for (int index = 0; index < releases.length; index++) {
                releases[index] = windupTicks(kind) + index * interval;
            }
        } else if (kind == Kind.CUBES) {
            FreezingCubeSpell spell = phase >= 2 ? FREEZING_CUBE_PHASE2 : FREEZING_CUBE_PHASE1;
            releases = spell.planReleaseDelays(random);
            int firstRelease = Arrays.stream(releases).min().orElse(0);
            for (int index = 0; index < releases.length; index++) {
                releases[index] += CastState.CUBE_RELEASE_TICKS - firstRelease;
            }
        } else if (kind == Kind.SUMMON) {
            releases = new int[SUMMON_MINIONS.chooseCount(random)];
            for (int index = 0; index < releases.length; index++) {
                releases[index] = windupTicks(kind) + index * SummonMinionsSpell.SPAWN_INTERVAL_TICKS;
            }
        } else {
            releases = new int[]{windupTicks(kind)};
        }
        return CastState.create(kind, started, serial, phase, reverse, releases);
    }

    public static void release(CastState cast, ServerLevel level, LichEntity caster, LivingEntity target) {
        RandomSource random = level.getRandom();
        BooleanSupplier active = () -> caster.isAlive() && !caster.isRemoved()
                && caster.level() == level && caster.getCastState().serial() == cast.serial() && caster.isCastAnimating();
        switch (cast.kind()) {
            case SNOWBALL -> (cast.enraged() ? SNOWBALL_PHASE2 : SNOWBALL_PHASE1)
                    .castWithOrigin(level, caster, target, random, entity -> caster.getStaffCastOrigin(), active);
            case ICICLE -> (cast.enraged() ? ICICLE_PHASE2 : ICICLE_PHASE1)
                    .castWithOrigin(level, caster, target, random, entity -> caster.getStaffCastOrigin(), active);
            case CUBES -> {
                int[] delays = cast.releases();
                for (int i = 0; i < delays.length; i++) {
                    delays[i] -= windupTicks(cast.kind());
                }
                (cast.enraged() ? FREEZING_CUBE_PHASE2 : FREEZING_CUBE_PHASE1)
                        .castWithDelays(level, caster, target, random, delays, active, 0);
            }
            case SPIKES -> ICE_SPIKES.cast(level, caster, target, random, cast.reverse());
            case SUMMON -> SUMMON_MINIONS.castCount(level, caster, target, random, cast.releaseCount(), active);
            case BLIZZARD -> BLIZZARD.cast(level, caster, target, random);
            case WHIRLWIND -> WHIRLWIND.cast(level, caster, target, random);
            default -> {
            }
        }
    }

}

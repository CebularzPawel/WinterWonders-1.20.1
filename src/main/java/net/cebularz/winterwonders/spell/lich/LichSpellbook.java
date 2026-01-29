package net.cebularz.winterwonders.spell.lich;

import net.cebularz.winterwonders.entity.ai.lich.LichAttackGoal;
import net.cebularz.winterwonders.entity.custom.LichEntity;
import net.cebularz.winterwonders.spell.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.turtleboi.turtlecore.effect.CoreEffects;
import net.turtleboi.turtlecore.particle.CoreParticles;
import net.turtleboi.turtlecore.spell.Spell;
import net.turtleboi.turtlecore.spell.Spell.*;

public final class LichSpellbook {
    static double snowballSpeed = 2.125;
    static double icicleSpeed = 3.25;
    public static final Spell SNOWBALL_PHASE1 =
            new SnowballSpell(CastMode.VOLLEY, 4, 10, 5.0F, 0, snowballSpeed, 0.15);
    public static final Spell SNOWBALL_PHASE2 =
            new SnowballSpell(CastMode.VOLLEY, 4, 5, 10.0F, 2, snowballSpeed * 1.25, 0.0);

    public static final Spell ICICLE_PHASE1 =
            new IcicleSpell(CastMode.VOLLEY, 4, 10, 10.0F, false, icicleSpeed, 0.15);
    public static final Spell ICICLE_PHASE2 =
            new IcicleSpell(CastMode.VOLLEY, 4, 5, 20.0F, true, icicleSpeed * 1.25, 0.0);

    public static final Spell ICE_SPIKES = new IceSpikesSpell();

    public static final Spell FREEZING_CUBE_PHASE1 =
            new FreezingCubeSpell(10.0F, 20, 0.1, 1.0, 100,
                    false, 1, 20,20);
    public static final Spell FREEZING_CUBE_PHASE2 =
            new FreezingCubeSpell(16.0F, 20, 0.125, 1.0, 100,
                    false, 3, 20,60);

    public static final Spell BLIZZARD =
            new BlizzardSpell(6.0, 6.0, 200, 2, 2, 120,
                    ParticleTypes.SNOWFLAKE, 12.0F, 1.65, 0.25, false);

    public static final Spell WHIRLWIND = new WhirlwindSpell(
            6.0, CoreParticles.CHILLED_PARTICLES.get(), false, CoreEffects.CHILLED.get(), 300, 2);

    public static final Spell SUMMON_MINIONS = new SummonMinionsSpell(2, 2, 2.0, true);

    public static Spell pickBasicProjectileSpell(LivingEntity caster, LivingEntity target, RandomSource random) {
        if (caster instanceof LichEntity lichEntity) {
            boolean phased = lichEntity.hasPhased();
            boolean frozenTarget = target.hasEffect(CoreEffects.FROZEN.get());
            boolean pickIcicle = frozenTarget || random.nextFloat() < 0.5F;

            if (pickIcicle) {
                return phased ? ICICLE_PHASE2 : ICICLE_PHASE1;
            }
            return phased ? SNOWBALL_PHASE2 : SNOWBALL_PHASE1;
        }
        return null;
    }

    public static Spell pickSpecialAttackSpell(LivingEntity caster) {
        if (caster instanceof LichEntity lichEntity) {
            boolean phased = lichEntity.hasPhased();
            return phased ? FREEZING_CUBE_PHASE2 : FREEZING_CUBE_PHASE1;
        }
        return null;
    }

    public static Spell fromAttackType(LichAttackGoal.AttackType type) {
        return switch (type) {
            case ICE_SPIKES -> ICE_SPIKES;
            case FREEZING_CUBE, BASIC_PROJECTILE, SPECIAL_ATTACK -> null;
            case BLIZZARD -> BLIZZARD;
            case WHIRLWIND -> WHIRLWIND;
            case SUMMON_MINIONS -> SUMMON_MINIONS;
        };
    }

    public static void castForAttackType(LichAttackGoal.AttackType type, ServerLevel level, LivingEntity caster, LivingEntity target) {
        if (level == null || caster == null || target == null) return;
        if (!caster.isAlive() || !target.isAlive()) return;

        RandomSource random = level.getRandom();

        if (type == LichAttackGoal.AttackType.BASIC_PROJECTILE) {
            Spell chosen = pickBasicProjectileSpell(caster, target, random);
            chosen.cast(level, caster, target, random);
            return;
        }

        if (type == LichAttackGoal.AttackType.SPECIAL_ATTACK) {
            Spell chosen = pickSpecialAttackSpell(caster);
            chosen.cast(level, caster, target, random);
            return;
        }

        Spell spell = fromAttackType(type);
        if (spell != null) {
            spell.cast(level, caster, target, random);
        }
    }
}

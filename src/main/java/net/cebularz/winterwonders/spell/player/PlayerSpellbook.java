package net.cebularz.winterwonders.spell.player;

import net.cebularz.winterwonders.spell.IcicleSpell;
import net.cebularz.winterwonders.spell.SnowballSpell;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.turtleboi.turtlecore.spell.Spell;

public class PlayerSpellbook {
    static double snowballSpeed = 1.75;
    static double icicleSpeed = 3.0;

    public static final Spell SNOWBALL=
            new SnowballSpell(Spell.CastMode.SINGLE, 1, 4, 5.0F, 0, snowballSpeed, 0.15);
    public static final Spell SNOWBALL_BARRAGE1=
            new SnowballSpell(Spell.CastMode.VOLLEY, 2, 4, 5.0F, 0, snowballSpeed, 0.15);
    public static final Spell SNOWBALL_BARRAGE2=
            new SnowballSpell(Spell.CastMode.VOLLEY, 3, 4, 5.0F, 0, snowballSpeed, 0.15);
    public static final Spell SNOWBALL_BARRAGE3=
            new SnowballSpell(Spell.CastMode.VOLLEY, 4, 4, 5.0F, 0, snowballSpeed, 0.15);

    public static final Spell ICICLE =
            new IcicleSpell(Spell.CastMode.SINGLE, 1, 4, 10.0F, false, icicleSpeed, 0.15);
    public static final Spell ICICLE_HOMING =
            new IcicleSpell(Spell.CastMode.SINGLE, 1, 4, 10.0F, true, icicleSpeed, 0.15);
    public static final Spell ICICLE_BARRAGE1 =
            new IcicleSpell(Spell.CastMode.VOLLEY, 2, 4, 10.0F, false, icicleSpeed, 0.15);
    public static final Spell ICICLE_HOMING_BARRAGE1 =
            new IcicleSpell(Spell.CastMode.VOLLEY, 2, 4, 10.0F, true, icicleSpeed, 0.15);
    public static final Spell ICICLE_BARRAGE2 =
            new IcicleSpell(Spell.CastMode.VOLLEY, 3, 4, 10.0F, false, icicleSpeed, 0.15);
    public static final Spell ICICLE_HOMING_BARRAGE2 =
            new IcicleSpell(Spell.CastMode.VOLLEY, 3, 4, 10.0F, true, icicleSpeed, 0.15);
    public static final Spell ICICLE_BARRAGE3 =
            new IcicleSpell(Spell.CastMode.VOLLEY, 4, 4, 10.0F, false, icicleSpeed, 0.15);
    public static final Spell ICICLE_HOMING_BARRAGE3 =
            new IcicleSpell(Spell.CastMode.VOLLEY, 4, 4, 10.0F, true, icicleSpeed, 0.15);

    public static boolean cast(ServerLevel level, Player caster, LivingEntity target, Spell spell) {
        if (level == null || caster == null || spell == null) return false;
        if (!caster.isAlive()) return false;
        if (target != null && (!target.isAlive() || target.isRemoved() || target.level() != level)) {
            target = null;
        }

        spell.cast(level, caster, target, level.getRandom());
        return true;
    }


}

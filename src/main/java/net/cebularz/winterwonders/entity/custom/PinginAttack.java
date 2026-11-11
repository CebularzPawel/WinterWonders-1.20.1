package net.cebularz.winterwonders.entity.custom;

import java.util.List;

public enum PinginAttack {

    SLASH   (18, 7, -30,  38, -24,  16, 1.7F, 0F,   0F, 16F, 32F, 1F,   0F),
    BACKHAND(18, 7,  32, -42,  16, -24, 1.4F, 0F,   0F, 12F,-30F, 1F,   0F),
    SWEEP   (20, 8,  42, -58, -10,  12, 2.0F, 0F,   0F, 22F, 32F, 1.1F, 0F),
    JAB     (16, 7,  16, -14, -12, -30, 3.0F, 0F,   0F,-40F, 55F, 0.1F, 0F),
    SPIN    (26,16,  28, -28, -12,  10, 0.9F, 4.0F,-360F,36F, 45F, 0.5F, 0.24F),
    THRUST  (16, 7, 1F, 1F, 0F, 0F, 0F, 0F),
    POKE    (16, 6, 0.65F, -0.7F, -1.5F, 0.5F, -0.3F, 0F),
    LUNGE   (24,11, 1.25F, 1.3F, 8F, 6F, 0.3F, 0F),
    FLICK   (18, 8, 0.65F, 0.8F, 2F, 1F, 0F, 0.5F),
    RAKE    (20, 9, 0.8F, -0.9F, 6.5F, 6.5F, 0F, -0.3F),
    THROW   (36,20, 1F, 1F, 0F, 0F, 0F, 0F);

    public static final List<PinginAttack> HANDHELD = List.of(SLASH, BACKHAND, SWEEP, JAB, SPIN);
    public static final List<PinginAttack> SPEARS = List.of(THRUST, POKE, LUNGE, FLICK, RAKE);

    public final int duration, hitTick;
    public final float windupYaw, strikeYaw, windupPitch, strikePitch;
    public final float lunge, hop, turn, flare, weaponAngle, inertia, weaponLead;
    public final boolean spear;
    public final float power, twist, lift, level, slope, sweep;

    PinginAttack(int duration, int hitTick, float windupYaw, float strikeYaw,
                 float windupPitch, float strikePitch, float lunge, float hop,
                 float turn, float flare, float weaponAngle, float inertia, float weaponLead) {
        this.duration = duration;
        this.hitTick = hitTick;
        this.windupYaw = windupYaw;
        this.strikeYaw = strikeYaw;
        this.windupPitch = windupPitch;
        this.strikePitch = strikePitch;
        this.lunge = lunge;
        this.hop = hop;
        this.turn = turn;
        this.flare = flare;
        this.weaponAngle = weaponAngle;
        this.inertia = inertia;
        this.weaponLead = weaponLead;
        spear = false;
        power = twist = 1F;
        lift = level = slope = sweep = 0F;
    }

    PinginAttack(int duration, int hitTick, float power, float twist, float lift, float level, float slope, float sweep) {
        this.duration = duration;
        this.hitTick = hitTick;
        this.power = power;
        this.twist = twist;
        this.lift = lift;
        this.level = level;
        this.slope = slope;
        this.sweep = sweep;
        spear = true;
        windupYaw = strikeYaw = windupPitch = strikePitch = 0F;
        lunge = hop = turn = flare = weaponAngle = 0F;
        inertia = weaponLead = 0F;
    }

    public float seconds() {
        return duration / 20F;
    }
}

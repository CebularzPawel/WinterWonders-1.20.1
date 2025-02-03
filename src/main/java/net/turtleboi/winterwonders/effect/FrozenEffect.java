package net.turtleboi.winterwonders.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.turtleboi.winterwonders.init.ModEffects;


public class FrozenEffect extends MobEffect {
    public FrozenEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }
    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        if (!pLivingEntity.level().isClientSide()) {

        }
        super.applyEffectTick(pLivingEntity, pAmplifier);
    }
    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    }
}


package net.cebularz.winterwonders.effect;

import net.cebularz.winterwonders.init.ModAttributes;
import net.cebularz.winterwonders.util.AttributeModifierUtil;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.spongepowered.asm.mixin.injection.At;


public class ColdResistanceEffect extends MobEffect {
    public ColdResistanceEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }
    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        if (!pLivingEntity.level().isClientSide()) {
            AttributeModifierUtil.applyPermanentModifier(
                    pLivingEntity,
                    ModAttributes.COLD_RESISTANCE.get(),
                    "cold_resistance",
                    20 * (1 + pAmplifier),
                    AttributeModifier.Operation.ADDITION
            );
        }
        super.applyEffectTick(pLivingEntity, pAmplifier);
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    }

    @Override
    public void removeAttributeModifiers(LivingEntity pLivingEntity, AttributeMap pAttributeMap, int pAmplifier) {
        super.removeAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
        if (!pLivingEntity.level().isClientSide()) {
            AttributeModifierUtil.removeModifier(
                    pLivingEntity,
                    ModAttributes.COLD_RESISTANCE.get(),
                    "cold_resistance"
            );
        }
    }
}


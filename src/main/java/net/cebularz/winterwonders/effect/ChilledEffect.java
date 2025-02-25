package net.cebularz.winterwonders.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.cebularz.winterwonders.init.ModDamageSources;
import net.cebularz.winterwonders.init.ModEffects;
import net.cebularz.winterwonders.util.AttributeModifierUtil;


public class ChilledEffect extends MobEffect {
    public ChilledEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }
    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        if (!pLivingEntity.level().isClientSide()) {
            AttributeModifierUtil.applyPermanentModifier(
                    pLivingEntity,
                    Attributes.MOVEMENT_SPEED,
                    "chilled_movement_speed",
                    -0.25 * (1 + pAmplifier),
                    AttributeModifier.Operation.MULTIPLY_TOTAL);

            if (pAmplifier > 0){
                ModDamageSources.hurtWithColdDamage(pLivingEntity, null, 0.05f * (1 + pAmplifier));
            }

            if (pAmplifier > 2){
                pLivingEntity.addEffect(new MobEffectInstance(ModEffects.FROZEN.get(), 100, 0));
                pLivingEntity.removeEffect(ModEffects.CHILLED.get());
            }
        }
        super.applyEffectTick(pLivingEntity, pAmplifier);
    }
    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return pDuration % 20 * (Math.max(0.5F, (float)(3 - (pAmplifier/2)))) == 0;
    }

    @Override
    public void removeAttributeModifiers(LivingEntity pLivingEntity, AttributeMap pAttributeMap, int pAmplifier) {
        super.removeAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
        AttributeModifierUtil.removeModifier(
                pLivingEntity,
                Attributes.MOVEMENT_SPEED,
                "chilled_movement_speed");
    }
}


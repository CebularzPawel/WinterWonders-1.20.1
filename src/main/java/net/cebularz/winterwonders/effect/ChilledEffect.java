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
                    -0.2 * (1 + pAmplifier),
                    AttributeModifier.Operation.MULTIPLY_TOTAL);

            ModDamageSources.hurtWithColdDamage(pLivingEntity, null, 0.05f * (1 + pAmplifier));

            if (pAmplifier > 4){
                pLivingEntity.addEffect(new MobEffectInstance(ModEffects.FROZEN.get(), 100, 0));
                pLivingEntity.removeEffect(ModEffects.CHILLED.get());
            }
        }
        super.applyEffectTick(pLivingEntity, pAmplifier);
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        int interval = Math.max(10, 80 / (int) Math.pow(2, Math.min(pAmplifier, 3)));
        return pDuration % interval == 0;
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


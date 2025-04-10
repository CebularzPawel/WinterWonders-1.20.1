package net.cebularz.winterwonders.effect.custom;

import net.cebularz.winterwonders.init.ModAttributes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.turtleboi.turtlecore.init.CoreAttributeModifiers;


public class FrostResistanceEffect extends MobEffect {
    public FrostResistanceEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }
    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        if (!pLivingEntity.level().isClientSide()) {
            CoreAttributeModifiers.applyPermanentModifier(
                    pLivingEntity,
                    ModAttributes.FROST_RESISTANCE.get(),
                    "frost_resistance",
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
            CoreAttributeModifiers.removeModifier(
                    pLivingEntity,
                    ModAttributes.FROST_RESISTANCE.get(),
                    "frost_resistance"
            );
        }
    }
}


package net.cebularz.winterwonders.effect;

import net.cebularz.winterwonders.network.ModNetworking;
import net.cebularz.winterwonders.network.packets.SendParticlesS2C;
import net.cebularz.winterwonders.particle.ModParticles;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.cebularz.winterwonders.init.ModDamageSources;
import net.cebularz.winterwonders.init.ModEffects;
import net.cebularz.winterwonders.util.AttributeModifierUtil;
import net.minecraft.world.entity.player.Player;

import java.util.function.Supplier;


public class ChilledEffect extends MobEffect {
    private final String attributeModifierName = "chilled_movement_speed";
    public ChilledEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }
    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        if (pLivingEntity.hasEffect(ModEffects.CHILLED.get())) {
            MobEffectInstance originalInstance = pLivingEntity.getEffect(ModEffects.CHILLED.get());
            if (originalInstance.isVisible()) {
                originalInstance.update(
                        new MobEffectInstance(
                                ModEffects.CHILLED.get(),
                                originalInstance.getDuration(),
                                originalInstance.getAmplifier(),
                                originalInstance.isAmbient(),
                                false,
                                originalInstance.showIcon()));
            }
        }

        if (!pLivingEntity.level().isClientSide && pLivingEntity.tickCount % 5 == 0){
            for (int i = 0; i < ((pAmplifier + 1) * 2); i++) {
                double offX = (pLivingEntity.level().random.nextDouble() - 0.5) * 0.5;
                double offY = pLivingEntity.getBbHeight() * pLivingEntity.level().random.nextDouble();
                double offZ = (pLivingEntity.level().random.nextDouble() - 0.5) * 0.5;
                ModNetworking.sendToNear(new SendParticlesS2C(
                        ModParticles.CHILLED_PARTICLES.get(),
                        pLivingEntity.getX() + offX,
                        pLivingEntity.getY() + offY,
                        pLivingEntity.getZ() + offZ,
                        0,0,0), pLivingEntity);
            }
        }

        if (!pLivingEntity.level().isClientSide()) {
            if(pLivingEntity instanceof Mob mob) {
                AttributeModifierUtil.applyPermanentModifier(
                        mob,
                        Attributes.MOVEMENT_SPEED,
                        attributeModifierName,
                        -0.125 * (1 + pAmplifier),
                        AttributeModifier.Operation.MULTIPLY_TOTAL);
            }

            if(pLivingEntity instanceof Player player) {
                AttributeModifierUtil.applyPermanentModifier(
                        player,
                        Attributes.MOVEMENT_SPEED,
                        attributeModifierName,
                        -0.2 * (1 + pAmplifier),
                        AttributeModifier.Operation.MULTIPLY_TOTAL);
            }

           //ModDamageSources.hurtWithColdDamage(pLivingEntity, null, 0.05f * (1 + pAmplifier));

            if (pAmplifier > 3){
                pLivingEntity.removeEffect(ModEffects.CHILLED.get());
                pLivingEntity.addEffect(new MobEffectInstance(ModEffects.FROZEN.get(), 100, 0));
            }
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
        AttributeModifierUtil.removeModifier(
                pLivingEntity,
                Attributes.MOVEMENT_SPEED,
                attributeModifierName);
    }
}


package net.cebularz.winterwonders.effect.custom;

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
import net.cebularz.winterwonders.effect.ModEffects;
import net.cebularz.winterwonders.util.AttributeModifierUtil;
import net.minecraft.world.entity.player.Player;


public class ChilledEffect extends MobEffect {
    private final String moveSpeedAttributeName = "chilled_movement_speed";
    private final String attackSpeedAttributeName = "chilled_attack_speed";
    private final String attackAttributeName = "chilled_attack";
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
                        moveSpeedAttributeName,
                        -0.125 * (1 + pAmplifier),
                        AttributeModifier.Operation.MULTIPLY_TOTAL);
                AttributeModifierUtil.applyPermanentModifier(
                        mob,
                        Attributes.MOVEMENT_SPEED,
                        attackSpeedAttributeName,
                        -0.125 * (1 + pAmplifier),
                        AttributeModifier.Operation.MULTIPLY_TOTAL);
                AttributeModifierUtil.applyPermanentModifier(
                        mob,
                        Attributes.MOVEMENT_SPEED,
                        attackAttributeName,
                        -0.125 * (1 + pAmplifier),
                        AttributeModifier.Operation.MULTIPLY_TOTAL);
            }

            if(pLivingEntity instanceof Player player) {
                AttributeModifierUtil.applyPermanentModifier(
                        player,
                        Attributes.MOVEMENT_SPEED,
                        moveSpeedAttributeName,
                        -0.2 * (1 + pAmplifier),
                        AttributeModifier.Operation.MULTIPLY_TOTAL);
                AttributeModifierUtil.applyPermanentModifier(
                        player,
                        Attributes.ATTACK_SPEED,
                        attackSpeedAttributeName,
                        -0.2 * (1 + pAmplifier),
                        AttributeModifier.Operation.MULTIPLY_TOTAL);
                AttributeModifierUtil.applyPermanentModifier(
                        player,
                        Attributes.ATTACK_DAMAGE,
                        attackAttributeName,
                        -0.2 * (1 + pAmplifier),
                        AttributeModifier.Operation.MULTIPLY_TOTAL);
            }

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
                moveSpeedAttributeName);
        AttributeModifierUtil.removeModifier(
                pLivingEntity,
                Attributes.ATTACK_SPEED,
                attackSpeedAttributeName);
        AttributeModifierUtil.removeModifier(
                pLivingEntity,
                Attributes.ATTACK_DAMAGE,
                attackAttributeName);
    }
}


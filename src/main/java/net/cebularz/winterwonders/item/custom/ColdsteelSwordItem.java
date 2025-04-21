package net.cebularz.winterwonders.item.custom;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.turtleboi.turtlecore.effect.CoreEffects;

public class ColdsteelSwordItem extends SwordItem {
    public ColdsteelSwordItem(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
    }

    @Override
    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
        if (!pTarget.level().isClientSide){
            pTarget.addEffect(new MobEffectInstance(CoreEffects.CHILLED.get(), 100, 0));
        }
        return super.hurtEnemy(pStack, pTarget, pAttacker);
    }
}

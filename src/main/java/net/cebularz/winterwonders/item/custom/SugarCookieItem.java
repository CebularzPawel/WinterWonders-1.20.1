package net.cebularz.winterwonders.item.custom;

import net.cebularz.winterwonders.effect.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SugarCookieItem extends Item {
    private final int jollyDurationTicks;
    private final int jollyAmplifier;

    public SugarCookieItem(Properties properties, int jollyDurationTicks, int jollyAmplifier) {
        super(properties);
        this.jollyDurationTicks = jollyDurationTicks;
        this.jollyAmplifier = jollyAmplifier;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);

        if (!level.isClientSide()) {
            entity.addEffect(new MobEffectInstance(ModEffects.JOLLY.get(), jollyDurationTicks, jollyAmplifier));
        }

        return result;
    }
}

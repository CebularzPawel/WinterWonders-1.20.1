package net.cebularz.winterwonders.item.util;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.cebularz.winterwonders.effect.ModEffects;

public class ModArmorItem extends ArmorItem {
    public ModArmorItem(ArmorMaterial pMaterial, Type pType, Properties pProperties) {
        super(pMaterial, pType, pProperties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (!level.isClientSide() && entity instanceof Player player) {
            int pieces = countArmorPieces(player, this.getMaterial());
            if (pieces >= 2) {
                int amplifier = pieces == 4 ? 2 : 1;
                MobEffectInstance effect = new MobEffectInstance(ModEffects.FROST_RESISTANCE.get(), 200, amplifier - 1, true, false, true);
                if (!player.hasEffect(ModEffects.FROST_RESISTANCE.get()) ||
                        player.getEffect(ModEffects.FROST_RESISTANCE.get()).getAmplifier() < amplifier - 1) {
                    player.addEffect(effect);
                }
            }
        }
    }

    private int countArmorPieces(Player player, ArmorMaterial material) {
        int count = 0;
        for (ItemStack armorStack : player.getInventory().armor) {
            if (!armorStack.isEmpty() && armorStack.getItem() instanceof ArmorItem armorItem) {
                if (armorItem.getMaterial() == material) {
                    count++;
                }
            }
        }
        return count;
    }
}

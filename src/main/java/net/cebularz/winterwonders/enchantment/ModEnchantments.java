package net.cebularz.winterwonders.enchantment;

import net.cebularz.winterwonders.WinterWonders;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, WinterWonders.MOD_ID);

    public static void register(IEventBus eventBus){
        ENCHANTMENTS.register(eventBus);
    }
}

package net.turtleboi.winterwonders.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.turtleboi.winterwonders.WinterWonders;
import net.turtleboi.winterwonders.block.ModBlocks;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, WinterWonders.MOD_ID);

    public static final RegistryObject<CreativeModeTab> WINTERWONDERS_TAB = CREATIVE_MODE_TABS.register("winterwonders_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.COLDSTEEL_INGOT.get()))
                    .title(Component.translatable("creativetab.winterwonders_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.FROST_ESSENCE.get());
                        pOutput.accept(ModItems.TRUEICE_SHARD.get());
                        pOutput.accept(ModItems.FROZEN_HEART.get());
                        pOutput.accept(ModItems.COLDSTEEL_SCRAP.get());
                        pOutput.accept(ModItems.COLDSTEEL_INGOT.get());
                        pOutput.accept(ModItems.FROSTBITE_WAND.get());
                    })
                    .build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}

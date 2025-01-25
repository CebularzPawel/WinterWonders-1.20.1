package net.turtleboi.winterwonders.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.turtleboi.winterwonders.WinterWonders;

import java.util.ArrayList;
import java.util.List;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, WinterWonders.MOD_ID);

    public static final List<Item> ITEM_LIST = new ArrayList<>();
    public static final List<Block> BLOCK_LIST = new ArrayList<>();

    public static final RegistryObject<CreativeModeTab> WINTERWONDERS_ITEM_TAB = CREATIVE_MODE_TABS.register("winterwonders_item_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.COLDSTEEL_INGOT.get()))
                    .title(Component.translatable("creativetab.winterwonders_item_tab"))
                    .displayItems((pParameters, pOutput) -> ITEM_LIST.forEach(pOutput::accept))
                    .build());
    public static final RegistryObject<CreativeModeTab> WINTERWONDERS_BLOCK_TAB = CREATIVE_MODE_TABS.register("winterwonders_block_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.COLDSTEEL_INGOT.get()))
                    .title(Component.translatable("creativetab.winterwonders_block_tab"))
                    .displayItems((pParameters, pOutput) -> BLOCK_LIST.forEach(pOutput::accept))
                    .build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}

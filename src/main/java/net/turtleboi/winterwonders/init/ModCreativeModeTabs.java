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

    public static final RegistryObject<CreativeModeTab> WINTERWONDERS_TAB = CREATIVE_MODE_TABS.register("winterwonders_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.COLDSTEEL_INGOT.get()))
                    .title(Component.translatable("creativetab.winterwonders_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.FROST_ESSENCE.get());
                        pOutput.accept(ModItems.TRUEICE_SHARD.get());
                        pOutput.accept(ModItems.FROZEN_HEART.get());
                        pOutput.accept(ModItems.COLDSTEEL_SCRAP.get());
                        pOutput.accept(ModItems.COLDSTEEL_INGOT.get());
                        pOutput.accept(ModBlocks.COLDSTEEL_BLOCK.get());

                        pOutput.accept(ModItems.COLDSTEEL_SWORD.get());
                        pOutput.accept(ModItems.COLDSTEEL_PICKAXE.get());
                        pOutput.accept(ModItems.COLDSTEEL_AXE.get());
                        pOutput.accept(ModItems.COLDSTEEL_SHOVEL.get());
                        pOutput.accept(ModItems.COLDSTEEL_HOE.get());

                        pOutput.accept(ModItems.COLDSTEEL_HELMET.get());
                        pOutput.accept(ModItems.COLDSTEEL_CHESTPLATE.get());
                        pOutput.accept(ModItems.COLDSTEEL_LEGGINGS.get());
                        pOutput.accept(ModItems.COLDSTEEL_BOOTS.get());

                        pOutput.accept(ModItems.FROSTBITE_WAND.get());

                        pOutput.accept(ModBlocks.GREYPINE_LOG.get());
                        pOutput.accept(ModBlocks.GREYPINE_WOOD.get());
                        pOutput.accept(ModBlocks.STRIPPED_GREYPINE_LOG.get());
                        pOutput.accept(ModBlocks.STRIPPED_GREYPINE_WOOD.get());
                        pOutput.accept(ModBlocks.GREYPINE_PLANKS.get());
                        pOutput.accept(ModBlocks.GREYPINE_LEAVES.get());
                        pOutput.accept(ModBlocks.GREYPINE_SAPLING.get());

                        pOutput.accept(ModBlocks.MYST_WILLOW_LOG.get());
                        pOutput.accept(ModBlocks.MYST_WILLOW_WOOD.get());
                        pOutput.accept(ModBlocks.STRIPPED_MYST_WILLOW_LOG.get());
                        pOutput.accept(ModBlocks.STRIPPED_MYST_WILLOW_WOOD.get());
                        pOutput.accept(ModBlocks.MYST_WILLOW_PLANKS.get());
                        pOutput.accept(ModBlocks.MYST_WILLOW_LEAVES.get());
                        pOutput.accept(ModBlocks.MYST_WILLOW_SAPLING.get());

                        pOutput.accept(ModBlocks.COBBLED_ICE_STONE.get());
                        pOutput.accept(ModBlocks.COBBLED_ICE_STONE_STAIRS.get());
                        pOutput.accept(ModBlocks.COBBLED_ICE_STONE_SLAB.get());
                        pOutput.accept(ModBlocks.COBBLED_ICE_STONE_WALL.get());


                        pOutput.accept(ModBlocks.ICE_STONE_BRICKS.get());
                        pOutput.accept(ModBlocks.ICE_STONE_BRICKS_STAIRS.get());
                        pOutput.accept(ModBlocks.ICE_STONE_BRICKS_SLAB.get());
                        pOutput.accept(ModBlocks.ICE_STONE_BRICKS_WALL.get());

                        pOutput.accept(ModBlocks.ICE_STONE_TILES.get());
                        pOutput.accept(ModBlocks.ICE_STONE_TILES_STAIRS.get());
                        pOutput.accept(ModBlocks.ICE_STONE_TILES_SLAB.get());
                        pOutput.accept(ModBlocks.ICE_STONE_TILES_WALL.get());

                        pOutput.accept(ModBlocks.POLISHED_ICE_STONE.get());
                        pOutput.accept(ModBlocks.POLISHED_ICE_STONE_STAIRS.get());
                        pOutput.accept(ModBlocks.POLISHED_ICE_STONE_SLAB.get());
                        pOutput.accept(ModBlocks.POLISHED_ICE_STONE_WALL.get());

                        pOutput.accept(ModBlocks.ICE_STONE_PILLAR.get());

                        pOutput.accept(ModBlocks.WONDER_SHROOM.get());
                        pOutput.accept(ModItems.WONDER_TREE_SHROOM.get());

                        pOutput.accept(ModBlocks.MAGICAL_ROSE.get());
                        pOutput.accept(ModBlocks.ICE_FLOWER.get());

                        pOutput.accept(ModItems.PUCKERBERRY.get());

                        pOutput.accept(ModItems.SNOW_WISP_SPAWN_EGG.get());
                        pOutput.accept(ModItems.REVENANT_SPAWN_EGG.get());
                        pOutput.accept(ModItems.BRISK_SPAWN_EGG.get());
                        pOutput.accept(ModItems.PINGIN_SPAWN_EGG.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}

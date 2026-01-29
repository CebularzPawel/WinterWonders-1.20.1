package net.cebularz.winterwonders.datagen;

import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.block.ModBlocks;
import net.cebularz.winterwonders.effect.ModEffects;
import net.cebularz.winterwonders.entity.ModEntities;
import net.cebularz.winterwonders.item.ModItems;
import net.minecraft.data.PackOutput;
import net.turtleboi.turtlecore.datagen.CoreLanguageProvider;

public class ModLanguageProvider extends CoreLanguageProvider {
    public ModLanguageProvider(PackOutput output) {
        super(output, WinterWonders.MOD_ID);
    }

    @Override
    protected void addTranslations() {
        add("creativetab.winterwonders_tab", "Winter Wonders");
        addSimpleNameEffect(ModEffects.FROST_RESISTANCE);

        add(ModEntities.SNOW_WISP.get(), "Snow Wisp");
        add(ModEntities.REVENANT.get(), "Revenant");
        add(ModEntities.BRISK.get(), "Brisk");
        add(ModEntities.PINGIN.get(), "Pingin");
        add(ModEntities.SNOW_WEASEL.get(), "Snow Weasel");
        add(ModEntities.LICH.get(), "The Lich");

        addSimpleNameBlock(ModBlocks.HEARTH);
        addSimpleItemName(ModItems.HEARTHSTONE);
        addSimpleItemName(ModItems.NOEL_STAFF);
        addSimpleNameBlock(ModBlocks.COLDSTEEL_BLOCK);

        addSimpleNameBlock(ModBlocks.COBBLED_ICE_STONE);
        addSimpleNameBlock(ModBlocks.COBBLED_ICE_STONE_WALL);
        addSimpleNameBlock(ModBlocks.COBBLED_ICE_STONE_SLAB);
        addSimpleNameBlock(ModBlocks.COBBLED_ICE_STONE_STAIRS);

        addSimpleNameBlock(ModBlocks.MOSSY_COBBLED_ICE_STONE);
        addSimpleNameBlock(ModBlocks.MOSSY_COBBLED_ICE_STONE_WALL);
        addSimpleNameBlock(ModBlocks.MOSSY_COBBLED_ICE_STONE_SLAB);
        addSimpleNameBlock(ModBlocks.MOSSY_COBBLED_ICE_STONE_STAIRS);

        addSimpleNameBlock(ModBlocks.ICE_STONE_BRICKS);
        addSimpleNameBlock(ModBlocks.ICE_STONE_BRICKS_WALL);
        addSimpleNameBlock(ModBlocks.ICE_STONE_BRICKS_SLAB);
        addSimpleNameBlock(ModBlocks.ICE_STONE_BRICKS_STAIRS);

        addSimpleNameBlock(ModBlocks.ICE_STONE_TILES);
        addSimpleNameBlock(ModBlocks.ICE_STONE_TILES_WALL);
        addSimpleNameBlock(ModBlocks.ICE_STONE_TILES_SLAB);
        addSimpleNameBlock(ModBlocks.ICE_STONE_TILES_STAIRS);

        addSimpleNameBlock(ModBlocks.POLISHED_ICE_STONE);
        addSimpleNameBlock(ModBlocks.POLISHED_ICE_STONE_WALL);
        addSimpleNameBlock(ModBlocks.POLISHED_ICE_STONE_SLAB);
        addSimpleNameBlock(ModBlocks.POLISHED_ICE_STONE_STAIRS);

        addSimpleNameBlock(ModBlocks.ICE_STONE_PILLAR);

        addSimpleNameBlock(ModBlocks.WUNDERSHROOM);
        add(ModBlocks.WUNDERSHROOM_TREE.get(), "Tree Wundershroom");

        addSimpleNameBlock(ModBlocks.ARCANILLUM);
        addSimpleNameBlock(ModBlocks.RIMEBLOOM);
        addSimpleNameBlock(ModBlocks.FROSTPETAL);
        addSimpleNameBlock(ModBlocks.MUSCARI);
        addSimpleNameBlock(ModBlocks.WHITE_MUSCARI);

        addSimpleNameBlock(ModBlocks.GREYPINE_LOG);
        addSimpleNameBlock(ModBlocks.GREYPINE_WOOD);
        addSimpleNameBlock(ModBlocks.STRIPPED_GREYPINE_LOG);
        addSimpleNameBlock(ModBlocks.STRIPPED_GREYPINE_WOOD);
        addSimpleNameBlock(ModBlocks.GREYPINE_LEAVES);
        addSimpleNameBlock(ModBlocks.GREYPINE_PLANKS);
        addSimpleNameBlock(ModBlocks.GREYPINE_SAPLING);
        addSimpleNameBlock(ModBlocks.GREYPINE_SLAB);
        addSimpleNameBlock(ModBlocks.GREYPINE_STAIRS);
        addSimpleNameBlock(ModBlocks.GREYPINE_BUTTON);
        addSimpleNameBlock(ModBlocks.GREYPINE_PRESSURE_PLATE);
        addSimpleNameBlock(ModBlocks.GREYPINE_FENCE);
        addSimpleNameBlock(ModBlocks.GREYPINE_FENCE_GATE);
        addSimpleNameBlock(ModBlocks.GREYPINE_DOOR);
        addSimpleNameBlock(ModBlocks.GREYPINE_TRAPDOOR);

        addSimpleNameBlock(ModBlocks.MYST_WILLOW_LOG);
        addSimpleNameBlock(ModBlocks.MYST_WILLOW_WOOD);
        addSimpleNameBlock(ModBlocks.STRIPPED_MYST_WILLOW_LOG);
        addSimpleNameBlock(ModBlocks.STRIPPED_MYST_WILLOW_WOOD);
        addSimpleNameBlock(ModBlocks.MYST_WILLOW_LEAVES);
        addSimpleNameBlock(ModBlocks.MYST_WILLOW_PLANKS);
        addSimpleNameBlock(ModBlocks.MYST_WILLOW_SAPLING);
        addSimpleNameBlock(ModBlocks.MYST_WILLOW_SLAB);
        addSimpleNameBlock(ModBlocks.MYST_WILLOW_STAIRS);
        addSimpleNameBlock(ModBlocks.MYST_WILLOW_BUTTON);
        addSimpleNameBlock(ModBlocks.MYST_WILLOW_PRESSURE_PLATE);
        addSimpleNameBlock(ModBlocks.MYST_WILLOW_FENCE);
        addSimpleNameBlock(ModBlocks.MYST_WILLOW_FENCE_GATE);
        //addSimpleNameBlock(ModBlocks.MYST_WILLOW_DOOR);
        //addSimpleNameBlock(ModBlocks.MYST_WILLOW_TRAPDOOR);

        addSimpleItemName(ModItems.FROST_ESSENCE);
        addSimpleItemName(ModItems.BRISK_ROD);
        addSimpleItemName(ModItems.TRUEICE_SHARD);
        addSimpleItemName(ModItems.FROZEN_HEART);

        add(ModItems.COLDSTEEL_SCRAP.get(), "Coldsteel Scraps");
        addSimpleItemName(ModItems.COLDSTEEL_INGOT);

        addSimpleItemName(ModItems.SNOW_WISP_SPAWN_EGG);
        addSimpleItemName(ModItems.REVENANT_SPAWN_EGG);
        addSimpleItemName(ModItems.BRISK_SPAWN_EGG);
        addSimpleItemName(ModItems.PINGIN_SPAWN_EGG);
        addSimpleItemName(ModItems.SNOW_WEASEL_SPAWN_EGG);
        addSimpleItemName(ModItems.LICH_SPAWN_EGG);

        addSimpleItemName(ModItems.COLDSTEEL_SWORD);
        addSimpleItemName(ModItems.COLDSTEEL_PICKAXE);
        addSimpleItemName(ModItems.COLDSTEEL_AXE);
        addSimpleItemName(ModItems.COLDSTEEL_SHOVEL);
        addSimpleItemName(ModItems.COLDSTEEL_HOE);

        addSimpleItemName(ModItems.COLDSTEEL_HELMET);
        addSimpleItemName(ModItems.COLDSTEEL_CHESTPLATE);
        addSimpleItemName(ModItems.COLDSTEEL_LEGGINGS);
        addSimpleItemName(ModItems.COLDSTEEL_BOOTS);

        addSimpleItemName(ModItems.PUCKERBERRY);

        addSimpleItemName(ModItems.FROSTBITE_WAND);
        addSimpleItemName(ModItems.BLIZZARD_STAFF);
        addItem(ModItems.TITANFELLER, "Titanfeller");

        addSimpleItemName(ModItems.PINGIN_FEATHER);
        addSimpleItemName(ModItems.PINGIN_MEAT);
        addSimpleItemName(ModItems.COOKED_PINGIN_MEAT);

        add("death.attack.frozen", "%s froze to death");
        add("death.attack.frozen.item", "%s was frozen by %s using %s");
        add("death.attack.frozen.player", "%s's frozen body shattered whilst trying to escape %s");
    }
}

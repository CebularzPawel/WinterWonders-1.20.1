package net.turtleboi.winterwonders.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import net.turtleboi.winterwonders.WinterWonders;
import net.turtleboi.winterwonders.init.ModBlocks;
import net.turtleboi.winterwonders.init.ModItems;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, WinterWonders.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(ModItems.FROST_ESSENCE);
        simpleItem(ModItems.TRUEICE_SHARD);
        simpleItem(ModItems.FROZEN_HEART);
        simpleItem(ModItems.COLDSTEEL_SCRAP);
        simpleItem(ModItems.COLDSTEEL_INGOT);

        handheldItem(ModItems.COLDSTEEL_SWORD);
        handheldItem(ModItems.COLDSTEEL_PICKAXE);
        handheldItem(ModItems.COLDSTEEL_AXE);
        handheldItem(ModItems.COLDSTEEL_SHOVEL);
        handheldItem(ModItems.COLDSTEEL_HOE);
        handheldItem(ModItems.FROSTBITE_WAND);

        saplingItem(ModBlocks.GREYPINE_SAPLING);
    }

    private ItemModelBuilder saplingItem(RegistryObject<Block> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(WinterWonders.MOD_ID,"block/" + item.getId().getPath()));
    }

    private ItemModelBuilder handheldItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/handheld")).texture("layer0",
                new ResourceLocation(WinterWonders.MOD_ID,"item/" + item.getId().getPath()));
    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(WinterWonders.MOD_ID,"item/" + item.getId().getPath()));
    }
}

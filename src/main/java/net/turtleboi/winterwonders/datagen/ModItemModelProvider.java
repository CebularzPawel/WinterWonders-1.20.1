package net.turtleboi.winterwonders.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import net.turtleboi.winterwonders.WinterWonders;
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
        simpleItem(ModItems.COLDSTEEL_INGOT);
        simpleItem(ModItems.COLDSTEEL_SWORD);
        simpleItem(ModItems.COLDSTEEL_PICKAXE);
        simpleItem(ModItems.COLDSTEEL_AXE);
        simpleItem(ModItems.COLDSTEEL_SHOVEL);
        simpleItem(ModItems.COLDSTEEL_HOE);
        simpleItem(ModItems.FROSTBITE_WAND);
    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(WinterWonders.MOD_ID,"item/" + item.getId().getPath()));
    }
}

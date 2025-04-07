package net.cebularz.winterwonders.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.init.ModBlocks;
import net.cebularz.winterwonders.init.ModItems;

import java.util.LinkedHashMap;

public class ModItemModelProvider extends ItemModelProvider {
    private static LinkedHashMap<ResourceKey<TrimMaterial>, Float> trimMaterials = new LinkedHashMap<>();
    static {
        trimMaterials.put(TrimMaterials.QUARTZ, 0.1F);
        trimMaterials.put(TrimMaterials.IRON, 0.2F);
        trimMaterials.put(TrimMaterials.NETHERITE, 0.3F);
        trimMaterials.put(TrimMaterials.REDSTONE, 0.4F);
        trimMaterials.put(TrimMaterials.COPPER, 0.5F);
        trimMaterials.put(TrimMaterials.GOLD, 0.6F);
        trimMaterials.put(TrimMaterials.EMERALD, 0.7F);
        trimMaterials.put(TrimMaterials.DIAMOND, 0.8F);
        trimMaterials.put(TrimMaterials.LAPIS, 0.9F);
        trimMaterials.put(TrimMaterials.AMETHYST, 1.0F);
    }

    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, WinterWonders.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(ModItems.FROST_ESSENCE);
        simpleItem(ModItems.BRISK_ROD);
        simpleItem(ModItems.TRUEICE_SHARD);
        simpleItem(ModItems.FROZEN_HEART);
        simpleItem(ModItems.COLDSTEEL_SCRAP);
        simpleItem(ModItems.COLDSTEEL_INGOT);

        wallItem(ModBlocks.COBBLED_ICE_STONE_WALL, ModBlocks.COBBLED_ICE_STONE);
        evenSimplerBlockItem(ModBlocks.COBBLED_ICE_STONE_STAIRS);
        evenSimplerBlockItem(ModBlocks.COBBLED_ICE_STONE_SLAB);

        wallItem(ModBlocks.ICE_STONE_BRICKS_WALL, ModBlocks.ICE_STONE_BRICKS);
        evenSimplerBlockItem(ModBlocks.ICE_STONE_BRICKS_STAIRS);
        evenSimplerBlockItem(ModBlocks.ICE_STONE_BRICKS_SLAB);

        wallItem(ModBlocks.ICE_STONE_TILES_WALL, ModBlocks.ICE_STONE_TILES);
        evenSimplerBlockItem(ModBlocks.ICE_STONE_TILES_STAIRS);
        evenSimplerBlockItem(ModBlocks.ICE_STONE_TILES_SLAB);

        wallItem(ModBlocks.POLISHED_ICE_STONE_WALL, ModBlocks.POLISHED_ICE_STONE);
        evenSimplerBlockItem(ModBlocks.POLISHED_ICE_STONE_STAIRS);
        evenSimplerBlockItem(ModBlocks.POLISHED_ICE_STONE_SLAB);

        handheldItem(ModItems.COLDSTEEL_SWORD);
        handheldItem(ModItems.COLDSTEEL_PICKAXE);
        handheldItem(ModItems.COLDSTEEL_AXE);
        handheldItem(ModItems.COLDSTEEL_SHOVEL);
        handheldItem(ModItems.COLDSTEEL_HOE);
        handheldItem(ModItems.FROSTBITE_WAND);

        saplingItem(ModBlocks.GREYPINE_SAPLING);
        evenSimplerBlockItem(ModBlocks.GREYPINE_SLAB);
        evenSimplerBlockItem(ModBlocks.GREYPINE_STAIRS);
        simpleBlockItem(ModBlocks.GREYPINE_DOOR);
        trapdoorItem(ModBlocks.GREYPINE_TRAPDOOR);
        fenceItem(ModBlocks.GREYPINE_FENCE, ModBlocks.GREYPINE_PLANKS);
        evenSimplerBlockItem(ModBlocks.GREYPINE_FENCE_GATE);
        buttonItem(ModBlocks.GREYPINE_BUTTON, ModBlocks.GREYPINE_PLANKS);
        evenSimplerBlockItem(ModBlocks.GREYPINE_PRESSURE_PLATE);

        saplingItem(ModBlocks.MYST_WILLOW_SAPLING);

        trimmedArmorItem(ModItems.COLDSTEEL_HELMET);
        trimmedArmorItem(ModItems.COLDSTEEL_CHESTPLATE);
        trimmedArmorItem(ModItems.COLDSTEEL_LEGGINGS);
        trimmedArmorItem(ModItems.COLDSTEEL_BOOTS);

        withExistingParent(ModItems.SNOW_WISP_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.REVENANT_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.BRISK_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.PINGIN_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.SNOW_WEASEL_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.LICH_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));

        simpleItem(ModItems.PUCKERBERRY);

        simpleBlockItemBlockTexture(ModBlocks.ARCANILLUM);
        simpleBlockItemBlockTexture(ModBlocks.RIMEBLOOM);

        simpleItem(ModItems.PINGIN_FEATHER);
        simpleItem(ModItems.PINGIN_MEAT);
        simpleItem(ModItems.COOKED_PINGIN_MEAT);
    }

    private void trimmedArmorItem(RegistryObject<Item> itemRegistryObject) {
        final String MOD_ID = WinterWonders.MOD_ID;

        if(itemRegistryObject.get() instanceof ArmorItem armorItem) {
            trimMaterials.entrySet().forEach(entry -> {

                ResourceKey<TrimMaterial> trimMaterial = entry.getKey();
                float trimValue = entry.getValue();

                String armorType = switch (armorItem.getEquipmentSlot()) {
                    case HEAD -> "helmet";
                    case CHEST -> "chestplate";
                    case LEGS -> "leggings";
                    case FEET -> "boots";
                    default -> "";
                };

                String armorItemPath = "item/" + armorItem;
                String trimPath = "trims/items/" + armorType + "_trim_" + trimMaterial.location().getPath();
                String currentTrimName = armorItemPath + "_" + trimMaterial.location().getPath() + "_trim";
                ResourceLocation armorItemResLoc = new ResourceLocation(MOD_ID, armorItemPath);
                ResourceLocation trimResLoc = new ResourceLocation(trimPath);
                ResourceLocation trimNameResLoc = new ResourceLocation(MOD_ID, currentTrimName);
                existingFileHelper.trackGenerated(trimResLoc, PackType.CLIENT_RESOURCES, ".png", "textures");

                getBuilder(currentTrimName)
                        .parent(new ModelFile.UncheckedModelFile("item/generated"))
                        .texture("layer0", armorItemResLoc)
                        .texture("layer1", trimResLoc);

                this.withExistingParent(itemRegistryObject.getId().getPath(),
                                mcLoc("item/generated"))
                        .override()
                        .model(new ModelFile.UncheckedModelFile(trimNameResLoc))
                        .predicate(mcLoc("trim_type"), trimValue).end()
                        .texture("layer0",
                                new ResourceLocation(MOD_ID,
                                        "item/" + itemRegistryObject.getId().getPath()));
            });
        }
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

    public void evenSimplerBlockItem(RegistryObject<Block> block) {
        this.withExistingParent(WinterWonders.MOD_ID + ":" + ForgeRegistries.BLOCKS.getKey(block.get()).getPath(),
                modLoc("block/" + ForgeRegistries.BLOCKS.getKey(block.get()).getPath()));
    }

    public void wallItem(RegistryObject<Block> block, RegistryObject<Block> baseBlock) {
        this.withExistingParent(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(), mcLoc("block/wall_inventory"))
                .texture("wall",  new ResourceLocation(WinterWonders.MOD_ID, "block/" + ForgeRegistries.BLOCKS.getKey(baseBlock.get()).getPath()));
    }

    public void trapdoorItem(RegistryObject<Block> block) {
        this.withExistingParent(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(),
                modLoc("block/" + ForgeRegistries.BLOCKS.getKey(block.get()).getPath() + "_bottom"));
    }

    public void fenceItem(RegistryObject<Block> block, RegistryObject<Block> baseBlock) {
        this.withExistingParent(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(), mcLoc("block/fence_inventory"))
                .texture("texture",  new ResourceLocation(WinterWonders.MOD_ID, "block/" + ForgeRegistries.BLOCKS.getKey(baseBlock.get()).getPath()));
    }

    public void buttonItem(RegistryObject<Block> block, RegistryObject<Block> baseBlock) {
        this.withExistingParent(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(), mcLoc("block/button_inventory"))
                .texture("texture",  new ResourceLocation(WinterWonders.MOD_ID, "block/" + ForgeRegistries.BLOCKS.getKey(baseBlock.get()).getPath()));
    }

    private ItemModelBuilder simpleBlockItemBlockTexture(RegistryObject<Block> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(WinterWonders.MOD_ID,"block/" + item.getId().getPath()));
    }

    private ItemModelBuilder simpleBlockItem(RegistryObject<Block> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(WinterWonders.MOD_ID,"item/" + item.getId().getPath()));
    }
}

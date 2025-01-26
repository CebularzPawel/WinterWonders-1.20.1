package net.turtleboi.winterwonders.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.turtleboi.winterwonders.WinterWonders;
import net.turtleboi.winterwonders.init.ModBlocks;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, WinterWonders.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(ModBlocks.COLDSTEEL_BLOCK);

        logBlock(((RotatedPillarBlock) ModBlocks.GREYPINE_LOG.get()));
        axisBlock(((RotatedPillarBlock) ModBlocks.GREYPINE_WOOD.get()), blockTexture(ModBlocks.GREYPINE_LOG.get()), blockTexture(ModBlocks.GREYPINE_LOG.get()));

        axisBlock(((RotatedPillarBlock) ModBlocks.STRIPPED_GREYPINE_LOG.get()), blockTexture(ModBlocks.STRIPPED_GREYPINE_LOG.get()),
                new ResourceLocation(WinterWonders.MOD_ID, "block/stripped_greypine_log"));
        axisBlock(((RotatedPillarBlock) ModBlocks.STRIPPED_GREYPINE_WOOD.get()), blockTexture(ModBlocks.STRIPPED_GREYPINE_LOG.get()), blockTexture(ModBlocks.STRIPPED_GREYPINE_LOG.get()));

        blockItem(ModBlocks.GREYPINE_LOG);
        blockItem(ModBlocks.GREYPINE_WOOD);
        blockItem(ModBlocks.STRIPPED_GREYPINE_LOG);
        blockItem(ModBlocks.STRIPPED_GREYPINE_WOOD);

        blockWithItem(ModBlocks.GREYPINE_PLANKS);

        leavesBlock(ModBlocks.GREYPINE_LEAVES);

        saplingBlock(ModBlocks.GREYPINE_SAPLING);
    }

    private void saplingBlock(RegistryObject<Block> blockRegistryObject) {
        simpleBlock(blockRegistryObject.get(),
                models().cross(ForgeRegistries.BLOCKS.getKey(blockRegistryObject.get()).getPath(), blockTexture(blockRegistryObject.get())).renderType("cutout"));
    }

    private void leavesBlock(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(),
                models().singleTexture(ForgeRegistries.BLOCKS.getKey(blockRegistryObject.get()).getPath(), new ResourceLocation("minecraft:block/leaves"),
                        "all", blockTexture(blockRegistryObject.get())).renderType("cutout"));
    }

    private void blockItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockItem(blockRegistryObject.get(), new ModelFile.UncheckedModelFile(WinterWonders.MOD_ID +
                ":block/" + ForgeRegistries.BLOCKS.getKey(blockRegistryObject.get()).getPath()));
    }

    private void blockWithItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }
}

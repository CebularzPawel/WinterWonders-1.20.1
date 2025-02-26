package net.cebularz.winterwonders.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.block.custom.PuckerberryBushBlock;
import net.cebularz.winterwonders.init.ModBlocks;

import java.util.function.Function;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, WinterWonders.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(ModBlocks.COLDSTEEL_BLOCK);

        blockWithItem(ModBlocks.COBBLED_ICE_STONE);
        stairsBlock(((StairBlock) ModBlocks.COBBLED_ICE_STONE_STAIRS.get()),blockTexture(ModBlocks.COBBLED_ICE_STONE.get()));
        slabBlock(((SlabBlock) ModBlocks.COBBLED_ICE_STONE_SLAB.get()),blockTexture(ModBlocks.COBBLED_ICE_STONE.get()),blockTexture(ModBlocks.COBBLED_ICE_STONE.get()));
        wallBlock(((WallBlock) ModBlocks.COBBLED_ICE_STONE_WALL.get()),blockTexture(ModBlocks.COBBLED_ICE_STONE.get()));

        blockWithItem(ModBlocks.ICE_STONE_BRICKS);
        stairsBlock(((StairBlock) ModBlocks.ICE_STONE_BRICKS_STAIRS.get()),blockTexture(ModBlocks.ICE_STONE_BRICKS.get()));
        slabBlock(((SlabBlock) ModBlocks.ICE_STONE_BRICKS_SLAB.get()),blockTexture(ModBlocks.ICE_STONE_BRICKS.get()),blockTexture(ModBlocks.ICE_STONE_BRICKS.get()));
        wallBlock(((WallBlock) ModBlocks.ICE_STONE_BRICKS_WALL.get()),blockTexture(ModBlocks.ICE_STONE_BRICKS.get()));

        blockWithItem(ModBlocks.ICE_STONE_TILES);
        stairsBlock(((StairBlock) ModBlocks.ICE_STONE_TILES_STAIRS.get()),blockTexture(ModBlocks.ICE_STONE_TILES.get()));
        slabBlock(((SlabBlock) ModBlocks.ICE_STONE_TILES_SLAB.get()),blockTexture(ModBlocks.ICE_STONE_TILES.get()),blockTexture(ModBlocks.ICE_STONE_TILES.get()));
        wallBlock(((WallBlock) ModBlocks.ICE_STONE_TILES_WALL.get()),blockTexture(ModBlocks.ICE_STONE_TILES.get()));

        blockWithItem(ModBlocks.POLISHED_ICE_STONE);
        stairsBlock(((StairBlock) ModBlocks.POLISHED_ICE_STONE_STAIRS.get()),blockTexture(ModBlocks.POLISHED_ICE_STONE.get()));
        slabBlock(((SlabBlock) ModBlocks.POLISHED_ICE_STONE_SLAB.get()),blockTexture(ModBlocks.POLISHED_ICE_STONE.get()),blockTexture(ModBlocks.POLISHED_ICE_STONE.get()));
        wallBlock(((WallBlock) ModBlocks.POLISHED_ICE_STONE_WALL.get()),blockTexture(ModBlocks.POLISHED_ICE_STONE.get()));

        simpleBlockWithItem(ModBlocks.MAGICAL_ROSE.get(), models().cross(blockTexture(ModBlocks.MAGICAL_ROSE.get()).getPath(),
                blockTexture(ModBlocks.MAGICAL_ROSE.get())).renderType("cutout"));
        simpleBlockWithItem(ModBlocks.POTTED_MAGICAL_ROSE.get(), models().singleTexture("potted_magical_rose", new ResourceLocation("flower_pot_cross"), "plant",
                blockTexture(ModBlocks.MAGICAL_ROSE.get())).renderType("cutout"));

        simpleBlockWithItem(ModBlocks.ICE_FLOWER.get(), models().cross(blockTexture(ModBlocks.ICE_FLOWER.get()).getPath(),
                blockTexture(ModBlocks.ICE_FLOWER.get())).renderType("cutout"));
        simpleBlockWithItem(ModBlocks.POTTED_ICE_FLOWER.get(), models().singleTexture("potted_ice_flower", new ResourceLocation("flower_pot_cross"), "plant",
                blockTexture(ModBlocks.ICE_FLOWER.get())).renderType("cutout"));

        axisBlock(((RotatedPillarBlock) ModBlocks.ICE_STONE_PILLAR.get()), blockTexture(ModBlocks.ICE_STONE_PILLAR.get()),
                new ResourceLocation(WinterWonders.MOD_ID, "block/ice_stone_pillar_end"));
        blockItem(ModBlocks.ICE_STONE_PILLAR);

        logBlock(((RotatedPillarBlock) ModBlocks.GREYPINE_LOG.get()));
        axisBlock(((RotatedPillarBlock) ModBlocks.GREYPINE_WOOD.get()), blockTexture(ModBlocks.GREYPINE_LOG.get()), blockTexture(ModBlocks.GREYPINE_LOG.get()));

        axisBlock(((RotatedPillarBlock) ModBlocks.STRIPPED_GREYPINE_LOG.get()), blockTexture(ModBlocks.STRIPPED_GREYPINE_LOG.get()),
                new ResourceLocation(WinterWonders.MOD_ID, "block/stripped_greypine_log_top"));
        axisBlock(((RotatedPillarBlock) ModBlocks.STRIPPED_GREYPINE_WOOD.get()), blockTexture(ModBlocks.STRIPPED_GREYPINE_LOG.get()), blockTexture(ModBlocks.STRIPPED_GREYPINE_LOG.get()));

        blockItem(ModBlocks.GREYPINE_LOG);
        blockItem(ModBlocks.GREYPINE_WOOD);
        blockItem(ModBlocks.STRIPPED_GREYPINE_LOG);
        blockItem(ModBlocks.STRIPPED_GREYPINE_WOOD);

        blockWithItem(ModBlocks.GREYPINE_PLANKS);
        stairsBlock((StairBlock) ModBlocks.GREYPINE_STAIRS.get(), blockTexture(ModBlocks.GREYPINE_PLANKS.get()));
        slabBlock((SlabBlock) ModBlocks.GREYPINE_SLAB.get(), blockTexture(ModBlocks.GREYPINE_PLANKS.get()), blockTexture(ModBlocks.GREYPINE_PLANKS.get()));
        buttonBlock((ButtonBlock) ModBlocks.GREYPINE_BUTTON.get(), blockTexture(ModBlocks.GREYPINE_PLANKS.get()));
        pressurePlateBlock((PressurePlateBlock) ModBlocks.GREYPINE_PRESSURE_PLATE.get(), blockTexture(ModBlocks.GREYPINE_PLANKS.get()));
        fenceBlock((FenceBlock) ModBlocks.GREYPINE_FENCE.get(), blockTexture(ModBlocks.GREYPINE_PLANKS.get()));
        fenceGateBlock((FenceGateBlock) ModBlocks.GREYPINE_FENCE_GATE.get(), blockTexture(ModBlocks.GREYPINE_PLANKS.get()));
        doorBlockWithRenderType((DoorBlock) ModBlocks.GREYPINE_DOOR.get(), modLoc("block/greypine_door_bottom"),
                modLoc("block/greypine_door_top"), "cutout");
        trapdoorBlockWithRenderType((TrapDoorBlock) ModBlocks.GREYPINE_TRAPDOOR.get(), modLoc("block/greypine_trapdoor"),
                true, "cutout");
        //leavesBlock(ModBlocks.GREYPINE_LEAVES);

        saplingBlock(ModBlocks.GREYPINE_SAPLING);


        logBlock(((RotatedPillarBlock) ModBlocks.MYST_WILLOW_LOG.get()));
        axisBlock(((RotatedPillarBlock) ModBlocks.MYST_WILLOW_WOOD.get()), blockTexture(ModBlocks.MYST_WILLOW_LOG.get()), blockTexture(ModBlocks.MYST_WILLOW_LOG.get()));

        axisBlock(((RotatedPillarBlock) ModBlocks.STRIPPED_MYST_WILLOW_LOG.get()), blockTexture(ModBlocks.STRIPPED_MYST_WILLOW_LOG.get()),
                new ResourceLocation(WinterWonders.MOD_ID, "block/stripped_myst_willow_log_top"));
        axisBlock(((RotatedPillarBlock) ModBlocks.STRIPPED_MYST_WILLOW_WOOD.get()), blockTexture(ModBlocks.STRIPPED_MYST_WILLOW_LOG.get()), blockTexture(ModBlocks.STRIPPED_MYST_WILLOW_LOG.get()));

        blockItem(ModBlocks.MYST_WILLOW_LOG);
        blockItem(ModBlocks.MYST_WILLOW_WOOD);
        blockItem(ModBlocks.STRIPPED_MYST_WILLOW_LOG);
        blockItem(ModBlocks.STRIPPED_MYST_WILLOW_WOOD);

        blockWithItem(ModBlocks.MYST_WILLOW_PLANKS);

        leavesBlock(ModBlocks.MYST_WILLOW_LEAVES);

        saplingBlock(ModBlocks.MYST_WILLOW_SAPLING);

        makeBerryBush((BushBlock) ModBlocks.PUCKERBERRY_BUSH.get(), "puckerberry_stage", "puckerberry_stage");
    }

    public void makeBerryBush(BushBlock block, String modelName, String textureName) {
        Function<BlockState, ConfiguredModel[]> function = state -> bushStates(state, block, modelName, textureName);
        getVariantBuilder(block).forAllStates(function);
    }

    private ConfiguredModel[] bushStates(BlockState state, BushBlock block, String modelName, String textureName) {
        ConfiguredModel[] models = new ConfiguredModel[1];
        models[0] = new ConfiguredModel(models().cross(modelName + ((PuckerberryBushBlock) block).getAge(state),
                new ResourceLocation(WinterWonders.MOD_ID, "block/" + textureName + ((PuckerberryBushBlock) block).getAge(state))).renderType("cutout"));

        return models;
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

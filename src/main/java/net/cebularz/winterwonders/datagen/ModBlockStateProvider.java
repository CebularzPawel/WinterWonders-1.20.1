package net.cebularz.winterwonders.datagen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.block.custom.PuckerberryBushBlock;
import net.cebularz.winterwonders.block.ModBlocks;

import java.util.Objects;
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

        blockWithItem(ModBlocks.MOSSY_COBBLED_ICE_STONE);
        stairsBlock(((StairBlock) ModBlocks.MOSSY_COBBLED_ICE_STONE_STAIRS.get()),blockTexture(ModBlocks.MOSSY_COBBLED_ICE_STONE.get()));
        slabBlock(((SlabBlock) ModBlocks.MOSSY_COBBLED_ICE_STONE_SLAB.get()),blockTexture(ModBlocks.MOSSY_COBBLED_ICE_STONE.get()),blockTexture(ModBlocks.MOSSY_COBBLED_ICE_STONE.get()));
        wallBlock(((WallBlock) ModBlocks.MOSSY_COBBLED_ICE_STONE_WALL.get()),blockTexture(ModBlocks.MOSSY_COBBLED_ICE_STONE.get()));

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

        simpleBlock(ModBlocks.ARCANILLUM.get(), models().cross(blockTexture(ModBlocks.ARCANILLUM.get()).getPath(),
                blockTexture(ModBlocks.ARCANILLUM.get())).renderType("cutout"));
        simpleBlock(ModBlocks.POTTED_ARCANILLUM.get(), models().singleTexture("potted_magical_rose", new ResourceLocation("flower_pot_cross"), "plant",
                blockTexture(ModBlocks.ARCANILLUM.get())).renderType("cutout"));

        simpleBlock(ModBlocks.RIMEBLOOM.get(), models().cross(blockTexture(ModBlocks.RIMEBLOOM.get()).getPath(),
                blockTexture(ModBlocks.RIMEBLOOM.get())).renderType("cutout"));
        simpleBlock(ModBlocks.POTTED_RIMEBLOOM.get(), models().singleTexture("potted_ice_flower", new ResourceLocation("flower_pot_cross"), "plant",
                blockTexture(ModBlocks.RIMEBLOOM.get())).renderType("cutout"));

        simpleBlock(ModBlocks.MUSCARI.get(), models().cross(blockTexture(ModBlocks.MUSCARI.get()).getPath(),
                blockTexture(ModBlocks.MUSCARI.get())).renderType("cutout"));
        simpleBlock(ModBlocks.POTTED_MUSCARI.get(), models().singleTexture("potted_muscari", new ResourceLocation("flower_pot_cross"), "plant",
                blockTexture(ModBlocks.MUSCARI.get())).renderType("cutout"));

        simpleBlock(ModBlocks.WHITE_MUSCARI.get(), models().cross(blockTexture(ModBlocks.WHITE_MUSCARI.get()).getPath(),
                blockTexture(ModBlocks.WHITE_MUSCARI.get())).renderType("cutout"));
        simpleBlock(ModBlocks.POTTED_WHITE_MUSCARI.get(), models().singleTexture("potted_white_muscari", new ResourceLocation("flower_pot_cross"), "plant",
                blockTexture(ModBlocks.MUSCARI.get())).renderType("cutout"));

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
        stairsBlock((StairBlock) ModBlocks.MYST_WILLOW_STAIRS.get(), blockTexture(ModBlocks.MYST_WILLOW_PLANKS.get()));
        slabBlock((SlabBlock) ModBlocks.MYST_WILLOW_SLAB.get(), blockTexture(ModBlocks.MYST_WILLOW_PLANKS.get()), blockTexture(ModBlocks.MYST_WILLOW_PLANKS.get()));
        buttonBlock((ButtonBlock) ModBlocks.MYST_WILLOW_BUTTON.get(), blockTexture(ModBlocks.MYST_WILLOW_PLANKS.get()));
        pressurePlateBlock((PressurePlateBlock) ModBlocks.MYST_WILLOW_PRESSURE_PLATE.get(), blockTexture(ModBlocks.MYST_WILLOW_PLANKS.get()));
        fenceBlock((FenceBlock) ModBlocks.MYST_WILLOW_FENCE.get(), blockTexture(ModBlocks.MYST_WILLOW_PLANKS.get()));
        fenceGateBlock((FenceGateBlock) ModBlocks.MYST_WILLOW_FENCE_GATE.get(), blockTexture(ModBlocks.MYST_WILLOW_PLANKS.get()));

        leavesBlock(ModBlocks.MYST_WILLOW_LEAVES);

        saplingBlock(ModBlocks.MYST_WILLOW_SAPLING);

        logBlock(((RotatedPillarBlock) ModBlocks.WYRMSCALE_FIR_LOG.get()));
        axisBlock(((RotatedPillarBlock) ModBlocks.WYRMSCALE_FIR_WOOD.get()), blockTexture(ModBlocks.WYRMSCALE_FIR_LOG.get()), blockTexture(ModBlocks.WYRMSCALE_FIR_LOG.get()));

        axisBlock(((RotatedPillarBlock) ModBlocks.STRIPPED_WYRMSCALE_FIR_LOG.get()), blockTexture(ModBlocks.STRIPPED_WYRMSCALE_FIR_LOG.get()),
                new ResourceLocation(WinterWonders.MOD_ID, "block/stripped_wyrmscale_fir_log_top"));
        axisBlock(((RotatedPillarBlock) ModBlocks.STRIPPED_WYRMSCALE_FIR_WOOD.get()), blockTexture(ModBlocks.STRIPPED_WYRMSCALE_FIR_LOG.get()), blockTexture(ModBlocks.STRIPPED_WYRMSCALE_FIR_LOG.get()));

        blockItem(ModBlocks.WYRMSCALE_FIR_LOG);
        blockItem(ModBlocks.WYRMSCALE_FIR_WOOD);
        blockItem(ModBlocks.STRIPPED_WYRMSCALE_FIR_LOG);
        blockItem(ModBlocks.STRIPPED_WYRMSCALE_FIR_WOOD);
        puckerberryBush(ModBlocks.PUCKERBERRY_BUSH.get());
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

    private ModelFile cutoutCross(String name, ResourceLocation texture) {
        return models()
                .withExistingParent(name, mcLoc("block/cross"))
                .texture("cross", texture)
                .renderType("cutout");
    }

    private void puckerberryBush(Block block) {
        String bushName = Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(block)).getPath();

        ModelFile stage0 = cutoutCross(bushName + "_stage0", modLoc("block/puckerberry_stage0"));
        ModelFile stage1 = cutoutCross(bushName + "_stage1", modLoc("block/puckerberry_stage1"));

        ModelFile stage2Bottom = cutoutCross(bushName + "_stage2_bottom", modLoc("block/puckerberry_stage2_bottom"));
        ModelFile stage2Top = cutoutCross(bushName + "_stage2_top", modLoc("block/puckerberry_stage2_top"));

        ModelFile stage3Bottom = cutoutCross(bushName + "_stage3_bottom", modLoc("block/puckerberry_stage3_bottom"));
        ModelFile stage3Top = cutoutCross(bushName + "_stage3_top", modLoc("block/puckerberry_stage3_top"));

        ModelFile stage4Bottom = cutoutCross(bushName + "_stage4_bottom", modLoc("block/puckerberry_stage3_bottom"));
        ModelFile stage4Top = cutoutCross(bushName + "_stage4_top", modLoc("block/puckerberry_stage4_top"));

        getVariantBuilder(block).forAllStates(state -> {
            boolean isTall = state.getValue(PuckerberryBushBlock.TALL);
            DoubleBlockHalf halfBush = state.getValue(PuckerberryBushBlock.HALF);
            int growthStage = state.getValue(PuckerberryBushBlock.BERRIES);
            int bushForm = state.getValue(PuckerberryBushBlock.FORM);

            ModelFile bushModel;

            if (isTall) {
                if (halfBush == DoubleBlockHalf.LOWER) {
                    bushModel = switch (growthStage) {
                        case 0 -> stage2Bottom;
                        case 1 -> stage3Bottom;
                        case 2 -> stage4Bottom;
                        default -> stage2Bottom;
                    };
                } else {
                    bushModel = switch (growthStage) {
                        case 0 -> stage2Top;
                        case 1 -> stage3Top;
                        case 2 -> stage4Top;
                        default -> stage2Top;
                    };
                }
            } else {
                if (halfBush == DoubleBlockHalf.LOWER) {
                    bushModel = (bushForm == 0) ? stage0 : stage1;
                } else {
                    bushModel = stage1;
                }
            }
            return ConfiguredModel.builder().modelFile(bushModel).build();
        });
    }

}

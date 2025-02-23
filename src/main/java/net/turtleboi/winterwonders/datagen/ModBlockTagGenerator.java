package net.turtleboi.winterwonders.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.common.Mod;
import net.turtleboi.winterwonders.WinterWonders;
import net.turtleboi.winterwonders.init.ModBlocks;
import net.turtleboi.winterwonders.init.ModTags;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagGenerator extends BlockTagsProvider {
    public ModBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, WinterWonders.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(BlockTags.LOGS_THAT_BURN)
                .add(ModBlocks.GREYPINE_LOG.get())
                .add(ModBlocks.GREYPINE_WOOD.get())
                .add(ModBlocks.STRIPPED_GREYPINE_LOG.get())
                .add(ModBlocks.STRIPPED_GREYPINE_WOOD.get())
                .add(ModBlocks.MYST_WILLOW_LOG.get())
                .add(ModBlocks.MYST_WILLOW_WOOD.get())
                .add(ModBlocks.STRIPPED_MYST_WILLOW_LOG.get())
                .add(ModBlocks.STRIPPED_MYST_WILLOW_WOOD.get());

        this.tag(BlockTags.PLANKS)
                .add(ModBlocks.GREYPINE_PLANKS.get())
                .add(ModBlocks.MYST_WILLOW_PLANKS.get());

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)

                .add(ModBlocks.COLDSTEEL_BLOCK.get())
                .add(ModBlocks.COBBLED_ICE_STONE.get())
                .add(ModBlocks.COBBLED_ICE_STONE_STAIRS.get())
                .add(ModBlocks.COBBLED_ICE_STONE_SLAB.get())
                .add(ModBlocks.COBBLED_ICE_STONE_WALL.get())
                .add(ModBlocks.ICE_STONE_BRICKS.get())
                .add(ModBlocks.ICE_STONE_BRICKS_STAIRS.get())
                .add(ModBlocks.ICE_STONE_BRICKS_SLAB.get())
                .add(ModBlocks.ICE_STONE_BRICKS_WALL.get())
                .add(ModBlocks.ICE_STONE_TILES.get())
                .add(ModBlocks.ICE_STONE_TILES_STAIRS.get())
                .add(ModBlocks.ICE_STONE_TILES_SLAB.get())
                .add(ModBlocks.ICE_STONE_TILES_WALL.get())
                .add(ModBlocks.POLISHED_ICE_STONE.get())
                .add(ModBlocks.POLISHED_ICE_STONE_STAIRS.get())
                .add(ModBlocks.POLISHED_ICE_STONE_SLAB.get())
                .add(ModBlocks.POLISHED_ICE_STONE_WALL.get())
                .add(ModBlocks.ICE_STONE_PILLAR.get());
        this.tag(BlockTags.WALLS)
                .add(ModBlocks.COBBLED_ICE_STONE_WALL.get())
                .add(ModBlocks.ICE_STONE_BRICKS_WALL.get())
                .add(ModBlocks.ICE_STONE_TILES_WALL.get())
                .add(ModBlocks.POLISHED_ICE_STONE_WALL.get());

    }
}

package net.turtleboi.winterwonders.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.turtleboi.winterwonders.WinterWonders;
import net.turtleboi.winterwonders.init.ModBlocks;
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
                .add(ModBlocks.STRIPPED_GREYPINE_WOOD.get());

        this.tag(BlockTags.PLANKS)
                .add(ModBlocks.GREYPINE_PLANKS.get());
    }
}

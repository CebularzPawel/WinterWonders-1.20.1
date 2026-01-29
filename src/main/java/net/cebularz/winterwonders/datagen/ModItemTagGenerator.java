package net.cebularz.winterwonders.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.block.ModBlocks;
import net.cebularz.winterwonders.item.ModItems;
import net.cebularz.winterwonders.init.ModTags;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagGenerator extends ItemTagsProvider {
    public ModItemTagGenerator(PackOutput p_275343_, CompletableFuture<HolderLookup.Provider> p_275729_,
                               CompletableFuture<TagLookup<Block>> p_275322_, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_275343_, p_275729_, p_275322_, WinterWonders.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(ItemTags.LOGS_THAT_BURN)
                .add(ModBlocks.GREYPINE_LOG.get().asItem())
                .add(ModBlocks.GREYPINE_WOOD.get().asItem())
                .add(ModBlocks.STRIPPED_GREYPINE_LOG.get().asItem())
                .add(ModBlocks.STRIPPED_GREYPINE_WOOD.get().asItem())
                .add(ModBlocks.MYST_WILLOW_LOG.get().asItem())
                .add(ModBlocks.MYST_WILLOW_WOOD.get().asItem())
                .add(ModBlocks.STRIPPED_MYST_WILLOW_LOG.get().asItem())
                .add(ModBlocks.STRIPPED_MYST_WILLOW_WOOD.get().asItem());

        this.tag(ItemTags.PLANKS)
                .add(ModBlocks.MYST_WILLOW_PLANKS.get().asItem())
                .add(ModBlocks.GREYPINE_PLANKS.get().asItem());

        this.tag(ItemTags.TRIMMABLE_ARMOR)
                .add(ModItems.COLDSTEEL_HELMET.get())
                .add(ModItems.COLDSTEEL_CHESTPLATE.get())
                .add(ModItems.COLDSTEEL_LEGGINGS.get())
                .add(ModItems.COLDSTEEL_BOOTS.get());
        this.tag(ModTags.Blocks.GREYPINE_LOGS)
                .add(ModBlocks.GREYPINE_LOG.get().asItem())
                .add(ModBlocks.STRIPPED_GREYPINE_LOG.get().asItem());

        this.tag(ItemTags.SWORDS)
                .add(ModItems.COLDSTEEL_SWORD.get())
                .add(ModItems.TITANFELLER.get());
    }
}

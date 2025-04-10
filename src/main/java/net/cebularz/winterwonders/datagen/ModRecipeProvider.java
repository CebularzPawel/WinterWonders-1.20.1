package net.cebularz.winterwonders.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.cebularz.winterwonders.block.ModBlocks;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        ///ICE STONE PILLAR
        stonecutterResultFromBase(pWriter,RecipeCategory.MISC, ModBlocks.ICE_STONE_PILLAR.get().asItem(), ModBlocks.COBBLED_ICE_STONE.get());
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.ICE_STONE_PILLAR.get(),2)
                .pattern("M  ")
                .pattern("M  ")
                .pattern("   ")
                .define('M', ModBlocks.COBBLED_ICE_STONE_SLAB.get())
                .unlockedBy(getHasName(ModBlocks.COBBLED_ICE_STONE_SLAB.get()), has(ModBlocks.COBBLED_ICE_STONE_SLAB.get()))
                .save(pWriter);

        //ICE STONE TILES
        stonecutterResultFromBase(pWriter,RecipeCategory.MISC, ModBlocks.ICE_STONE_TILES.get().asItem(), ModBlocks.COBBLED_ICE_STONE.get());
        stonecutterResultFromBase(pWriter,RecipeCategory.MISC, ModBlocks.ICE_STONE_TILES_STAIRS.get().asItem(), ModBlocks.COBBLED_ICE_STONE.get());
        stonecutterResultFromBase(pWriter,RecipeCategory.MISC, ModBlocks.ICE_STONE_TILES_WALL.get().asItem(), ModBlocks.COBBLED_ICE_STONE.get());
        stonecutterResultFromBase(pWriter,RecipeCategory.MISC, ModBlocks.ICE_STONE_TILES_SLAB.get().asItem(), ModBlocks.COBBLED_ICE_STONE.get(),2);
        stonecutterResultFromBase(pWriter,RecipeCategory.MISC, ModBlocks.ICE_STONE_TILES_STAIRS.get().asItem(), ModBlocks.ICE_STONE_TILES.get());
        stonecutterResultFromBase(pWriter,RecipeCategory.MISC, ModBlocks.ICE_STONE_TILES_WALL.get().asItem(), ModBlocks.ICE_STONE_TILES.get());
        stonecutterResultFromBase(pWriter, RecipeCategory.MISC, ModBlocks.ICE_STONE_TILES_SLAB.get().asItem(), ModBlocks.ICE_STONE_TILES.get(), 2);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.ICE_STONE_TILES_SLAB.get(),6)
                .pattern("   ")
                .pattern("   ")
                .pattern("MMM")
                .define('M', ModBlocks.ICE_STONE_TILES.get())
                .unlockedBy(getHasName(ModBlocks.ICE_STONE_TILES.get()), has(ModBlocks.ICE_STONE_TILES.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.ICE_STONE_TILES_STAIRS.get(),4)
                .pattern("M  ")
                .pattern("MM ")
                .pattern("MMM")
                .define('M', ModBlocks.ICE_STONE_TILES.get())
                .unlockedBy(getHasName(ModBlocks.ICE_STONE_TILES.get()), has(ModBlocks.ICE_STONE_TILES.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.ICE_STONE_TILES_WALL.get(),6)
                .pattern("   ")
                .pattern("MMM")
                .pattern("MMM")
                .define('M', ModBlocks.ICE_STONE_TILES.get())
                .unlockedBy(getHasName(ModBlocks.ICE_STONE_TILES.get()), has(ModBlocks.ICE_STONE_TILES.get()))
                .save(pWriter);
        //ICE STONE BRICKS
        stonecutterResultFromBase(pWriter,RecipeCategory.MISC, ModBlocks.ICE_STONE_BRICKS.get().asItem(), ModBlocks.COBBLED_ICE_STONE.get());
        stonecutterResultFromBase(pWriter,RecipeCategory.MISC, ModBlocks.ICE_STONE_BRICKS_STAIRS.get().asItem(), ModBlocks.COBBLED_ICE_STONE.get());
        stonecutterResultFromBase(pWriter,RecipeCategory.MISC, ModBlocks.ICE_STONE_BRICKS_WALL.get().asItem(), ModBlocks.COBBLED_ICE_STONE.get());
        stonecutterResultFromBase(pWriter,RecipeCategory.MISC, ModBlocks.ICE_STONE_BRICKS_SLAB.get().asItem(), ModBlocks.COBBLED_ICE_STONE.get(),2);
        stonecutterResultFromBase(pWriter,RecipeCategory.MISC, ModBlocks.ICE_STONE_BRICKS_STAIRS.get().asItem(), ModBlocks.ICE_STONE_BRICKS.get());
        stonecutterResultFromBase(pWriter,RecipeCategory.MISC, ModBlocks.ICE_STONE_BRICKS_WALL.get().asItem(), ModBlocks.ICE_STONE_BRICKS.get());
        stonecutterResultFromBase(pWriter, RecipeCategory.MISC, ModBlocks.ICE_STONE_BRICKS_SLAB.get().asItem(), ModBlocks.ICE_STONE_BRICKS.get(), 2);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.ICE_STONE_BRICKS_SLAB.get(),6)
                .pattern("   ")
                .pattern("   ")
                .pattern("MMM")
                .define('M', ModBlocks.ICE_STONE_BRICKS.get())
                .unlockedBy(getHasName(ModBlocks.ICE_STONE_BRICKS.get()), has(ModBlocks.ICE_STONE_BRICKS.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.ICE_STONE_BRICKS_STAIRS.get(),4)
                .pattern("M  ")
                .pattern("MM ")
                .pattern("MMM")
                .define('M', ModBlocks.ICE_STONE_BRICKS.get())
                .unlockedBy(getHasName(ModBlocks.ICE_STONE_BRICKS.get()), has(ModBlocks.ICE_STONE_BRICKS.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.ICE_STONE_BRICKS_WALL.get(),6)
                .pattern("   ")
                .pattern("MMM")
                .pattern("MMM")
                .define('M', ModBlocks.ICE_STONE_BRICKS.get())
                .unlockedBy(getHasName(ModBlocks.ICE_STONE_BRICKS.get()), has(ModBlocks.ICE_STONE_BRICKS.get()))
                .save(pWriter);
        //COBBLED ICE STONE
        stonecutterResultFromBase(pWriter,RecipeCategory.MISC, ModBlocks.COBBLED_ICE_STONE_STAIRS.get().asItem(), ModBlocks.COBBLED_ICE_STONE.get());
        stonecutterResultFromBase(pWriter,RecipeCategory.MISC, ModBlocks.COBBLED_ICE_STONE_WALL.get().asItem(), ModBlocks.COBBLED_ICE_STONE.get());
        stonecutterResultFromBase(pWriter,RecipeCategory.MISC, ModBlocks.COBBLED_ICE_STONE_SLAB.get().asItem(), ModBlocks.COBBLED_ICE_STONE.get(),2);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.COBBLED_ICE_STONE_SLAB.get(),6)
                .pattern("   ")
                .pattern("   ")
                .pattern("MMM")
                .define('M', ModBlocks.COBBLED_ICE_STONE.get())
                .unlockedBy(getHasName(ModBlocks.COBBLED_ICE_STONE.get()), has(ModBlocks.COBBLED_ICE_STONE.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.COBBLED_ICE_STONE_STAIRS.get(),4)
                .pattern("M  ")
                .pattern("MM ")
                .pattern("MMM")
                .define('M', ModBlocks.COBBLED_ICE_STONE.get())
                .unlockedBy(getHasName(ModBlocks.COBBLED_ICE_STONE.get()), has(ModBlocks.COBBLED_ICE_STONE.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.COBBLED_ICE_STONE_WALL.get(),6)
                .pattern("   ")
                .pattern("MMM")
                .pattern("MMM")
                .define('M', ModBlocks.COBBLED_ICE_STONE.get())
                .unlockedBy(getHasName(ModBlocks.COBBLED_ICE_STONE.get()), has(ModBlocks.COBBLED_ICE_STONE.get()))
                .save(pWriter);
    }
}

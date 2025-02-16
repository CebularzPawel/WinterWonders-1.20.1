package net.turtleboi.winterwonders.datagen.loot;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;
import net.turtleboi.winterwonders.block.custom.PuckerberryBushBlock;
import net.turtleboi.winterwonders.init.ModBlocks;
import net.turtleboi.winterwonders.init.ModItems;

import java.util.Set;

public class ModBlockLootTables extends BlockLootSubProvider {
    public ModBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        this.dropSelf(ModBlocks.COLDSTEEL_BLOCK.get());

        this.dropSelf(ModBlocks.COBBLED_ICE_STONE.get());
        this.add(ModBlocks.COBBLED_ICE_STONE_SLAB.get(),
                block -> createSlabItemTable(ModBlocks.COBBLED_ICE_STONE_SLAB.get()));
        this.dropSelf(ModBlocks.COBBLED_ICE_STONE_STAIRS.get());
        this.dropSelf(ModBlocks.COBBLED_ICE_STONE_WALL.get());

        this.dropSelf(ModBlocks.ICE_STONE_BRICKS.get());
        this.add(ModBlocks.ICE_STONE_BRICKS_SLAB.get(),
                block -> createSlabItemTable(ModBlocks.ICE_STONE_BRICKS_SLAB.get()));
        this.dropSelf(ModBlocks.ICE_STONE_BRICKS_STAIRS.get());
        this.dropSelf(ModBlocks.ICE_STONE_BRICKS_WALL.get());

        this.dropSelf(ModBlocks.ICE_STONE_TILES.get());
        this.add(ModBlocks.ICE_STONE_TILES_SLAB.get(),
                block -> createSlabItemTable(ModBlocks.ICE_STONE_TILES_SLAB.get()));
        this.dropSelf(ModBlocks.ICE_STONE_TILES_STAIRS.get());
        this.dropSelf(ModBlocks.ICE_STONE_TILES_WALL.get());

        this.dropSelf(ModBlocks.POLISHED_ICE_STONE.get());
        this.add(ModBlocks.POLISHED_ICE_STONE_SLAB.get(),
                block -> createSlabItemTable(ModBlocks.POLISHED_ICE_STONE_SLAB.get()));
        this.dropSelf(ModBlocks.POLISHED_ICE_STONE_STAIRS.get());
        this.dropSelf(ModBlocks.POLISHED_ICE_STONE_WALL.get());

        this.dropSelf(ModBlocks.GREYPINE_LOG.get());
        this.dropSelf(ModBlocks.GREYPINE_WOOD.get());
        this.dropSelf(ModBlocks.STRIPPED_GREYPINE_LOG.get());
        this.dropSelf(ModBlocks.STRIPPED_GREYPINE_WOOD.get());
        this.dropSelf(ModBlocks.GREYPINE_PLANKS.get());
        this.dropSelf(ModBlocks.GREYPINE_SAPLING.get());
        this.add(ModBlocks.GREYPINE_LEAVES.get(), block ->
                createLeavesDrops(block, ModBlocks.GREYPINE_SAPLING.get(), NORMAL_LEAVES_SAPLING_CHANCES));

        this.dropSelf(ModBlocks.MYST_WILLOW_LOG.get());
        this.dropSelf(ModBlocks.MYST_WILLOW_WOOD.get());
        this.dropSelf(ModBlocks.STRIPPED_MYST_WILLOW_LOG.get());
        this.dropSelf(ModBlocks.STRIPPED_MYST_WILLOW_WOOD.get());
        this.dropSelf(ModBlocks.MYST_WILLOW_PLANKS.get());
        this.dropSelf(ModBlocks.MYST_WILLOW_SAPLING.get());
        this.add(ModBlocks.MYST_WILLOW_LEAVES.get(), block ->
                createLeavesDrops(block, ModBlocks.MYST_WILLOW_SAPLING.get(), NORMAL_LEAVES_SAPLING_CHANCES));

        this.dropWhenSilkTouch(ModBlocks.ICE_SLUSH.get());

        this.dropSelf(ModBlocks.WONDER_SHROOM.get());
        this.dropSelf(ModBlocks.WONDER_TREE_SHROOM.get());
        this.dropOther(ModBlocks.WONDER_TREE_SHROOM_WALL.get(),ModItems.WONDER_TREE_SHROOM.get());
        this.dropWhenSilkTouch(ModBlocks.ICY_VINES.get());
        this.dropWhenSilkTouch(ModBlocks.ICY_VINES_PLANT.get());

        this.dropSelf(ModBlocks.MAGICAL_ROSE.get());
        this.add(ModBlocks.POTTED_MAGICAL_ROSE.get(),createPotFlowerItemTable(ModBlocks.MAGICAL_ROSE.get()));
        this.dropSelf(ModBlocks.ICE_FLOWER.get());
        this.add(ModBlocks.POTTED_ICE_FLOWER.get(),createPotFlowerItemTable(ModBlocks.ICE_FLOWER.get()));

        LootItemCondition.Builder lootitemcondition$builder2 = LootItemBlockStatePropertyCondition
                .hasBlockStateProperties(ModBlocks.PUCKERBERRY_BUSH.get())
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(PuckerberryBushBlock.AGE, 2))
                .or(LootItemBlockStatePropertyCondition
                        .hasBlockStateProperties(ModBlocks.PUCKERBERRY_BUSH.get())
                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(PuckerberryBushBlock.AGE,3)));

        this.add(ModBlocks.PUCKERBERRY_BUSH.get(),
                createCropDrops(
                        ModBlocks.PUCKERBERRY_BUSH.get(),
                        ModItems.PUCKERBERRY.get(),
                        ModItems.PUCKERBERRY.get(),
                        lootitemcondition$builder2));
    }

    protected LootTable.Builder createCopperLikeOreDrops(Block pBlock, Item item) {
        return createSilkTouchDispatchTable(pBlock,
                this.applyExplosionDecay(pBlock,
                        LootItem.lootTableItem(item)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 5.0F)))
                                .apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}

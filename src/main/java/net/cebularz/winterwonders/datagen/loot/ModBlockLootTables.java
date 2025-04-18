package net.cebularz.winterwonders.datagen.loot;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.RegistryObject;
import net.cebularz.winterwonders.block.custom.PuckerberryBushBlock;
import net.cebularz.winterwonders.block.ModBlocks;
import net.cebularz.winterwonders.item.ModItems;

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
        this.dropSelf(ModBlocks.GREYPINE_SAPLING.get());
        this.add(ModBlocks.GREYPINE_LEAVES.get(), block ->
                createLeavesDrops(block, ModBlocks.GREYPINE_SAPLING.get(), NORMAL_LEAVES_SAPLING_CHANCES));

        this.dropSelf(ModBlocks.GREYPINE_PLANKS.get());
        this.add(ModBlocks.GREYPINE_SLAB.get(),
                block -> createSlabItemTable(ModBlocks.GREYPINE_SLAB.get()));
        this.dropSelf(ModBlocks.GREYPINE_STAIRS.get());
        this.dropSelf(ModBlocks.GREYPINE_BUTTON.get());
        this.dropSelf(ModBlocks.GREYPINE_PRESSURE_PLATE.get());
        this.add(ModBlocks.GREYPINE_DOOR.get(),
                block -> createDoorTable(ModBlocks.GREYPINE_DOOR.get()));
        this.dropSelf(ModBlocks.GREYPINE_TRAPDOOR.get());
        this.dropSelf(ModBlocks.GREYPINE_FENCE.get());
        this.dropSelf(ModBlocks.GREYPINE_FENCE_GATE.get());

        this.dropSelf(ModBlocks.MYST_WILLOW_LOG.get());
        this.dropSelf(ModBlocks.MYST_WILLOW_WOOD.get());
        this.dropSelf(ModBlocks.STRIPPED_MYST_WILLOW_LOG.get());
        this.dropSelf(ModBlocks.STRIPPED_MYST_WILLOW_WOOD.get());
        this.dropSelf(ModBlocks.MYST_WILLOW_PLANKS.get());
        this.dropSelf(ModBlocks.MYST_WILLOW_SAPLING.get());
        this.add(ModBlocks.MYST_WILLOW_LEAVES.get(), block ->
                createLeavesDrops(block, ModBlocks.MYST_WILLOW_SAPLING.get(), NORMAL_LEAVES_SAPLING_CHANCES));

        this.dropWhenSilkTouch(ModBlocks.ICE_SLUSH.get());

        this.dropSelf(ModBlocks.WUNDERSHROOM.get());
        this.dropSelf(ModBlocks.WUNDERSHROOM_TREE.get());
        this.dropSelf(ModBlocks.WUNDERSHROOM_TREE_WALL.get());
        this.dropWhenSilkTouch(ModBlocks.ICY_VINES.get());
        this.dropWhenSilkTouch(ModBlocks.ICY_VINES_PLANT.get());
        this.dropSelf(ModBlocks.ICE_STONE_PILLAR.get());

        this.dropSelf(ModBlocks.ARCANILLUM.get());
        this.add(ModBlocks.POTTED_ARCANILLUM.get(),createPotFlowerItemTable(ModBlocks.ARCANILLUM.get()));
        this.dropSelf(ModBlocks.RIMEBLOOM.get());
        this.add(ModBlocks.POTTED_RIMEBLOOM.get(),createPotFlowerItemTable(ModBlocks.RIMEBLOOM.get()));

        this.dropSelf(ModBlocks.MUSCARI.get());
        this.add(ModBlocks.POTTED_MUSCARI.get(),createPotFlowerItemTable(ModBlocks.MUSCARI.get()));
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

        this.add(ModBlocks.FROSTPETAL.get(),
                createPetalsDrops(ModBlocks.FROSTPETAL.get()));
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

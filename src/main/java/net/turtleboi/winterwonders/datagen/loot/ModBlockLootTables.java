package net.turtleboi.winterwonders.datagen.loot;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import net.turtleboi.winterwonders.block.ModBlocks;
import net.turtleboi.winterwonders.item.ModItems;

import java.util.Set;

public class ModBlockLootTables extends BlockLootSubProvider {
    public ModBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        this.dropSelf(ModBlocks.LAPIDARIST_TABLE.get());
    }

    //protected LootTable.Builder createCopperLikeOreDrops(Block pBlock, Item item) {
    //    return createSilkTouchDispatchTable(pBlock,
    //            this.applyExplosionDecay(pBlock,
    //                    LootItem.lootTableItem(item)
    //                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 5.0F)))
    //                            .apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))));
    //}

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}

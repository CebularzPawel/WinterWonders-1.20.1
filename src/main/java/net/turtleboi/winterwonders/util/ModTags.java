package net.turtleboi.winterwonders.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.turtleboi.winterwonders.WinterWonders;

public class ModTags {
    public static class Blocks {
        private static TagKey<Block> tag(String name){
            return BlockTags.create(new ResourceLocation(WinterWonders.MOD_ID, name));
        }
    }

    public static class Items {
        public static final TagKey<Item> PRECIOUS_GEMS = tag("precious_gems");
        public static final TagKey<Item> ANCIENT_GEMS = tag("ancient_gems");
        public static final TagKey<Item> MAJOR_GEMS = tag("major_gems");
        public static final TagKey<Item> MINOR_GEMS = tag("minor_gems");

        private static TagKey<Item> tag(String name){
            return ItemTags.create(new ResourceLocation(WinterWonders.MOD_ID, name));
        }
    }
}

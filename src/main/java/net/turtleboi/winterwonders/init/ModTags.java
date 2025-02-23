package net.turtleboi.winterwonders.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.turtleboi.winterwonders.WinterWonders;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> NEEDS_COLDSTEEL_TOOL = tagblock("needs_coldsteel_tool");

        public static final TagKey<Item> GREYPINE_LOGS = tagitem("greypine_logs");


        private static TagKey<Block> tagblock(String name){
            return BlockTags.create(new ResourceLocation(WinterWonders.MOD_ID, name));
        }
        private static TagKey<Item> tagitem(String name){
            return ItemTags.create(new ResourceLocation(WinterWonders.MOD_ID, name));
        }
    }
}

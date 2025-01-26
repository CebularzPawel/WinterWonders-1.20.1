package net.turtleboi.winterwonders.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.turtleboi.winterwonders.WinterWonders;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> NEEDS_COLDSTEEL_TOOL = tag("needs_coldsteel_tool");

        private static TagKey<Block> tag(String name){
            return BlockTags.create(new ResourceLocation(WinterWonders.MOD_ID, name));
        }
    }
}

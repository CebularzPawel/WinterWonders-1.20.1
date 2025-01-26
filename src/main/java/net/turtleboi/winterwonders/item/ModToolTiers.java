package net.turtleboi.winterwonders.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.TierSortingRegistry;
import net.turtleboi.winterwonders.WinterWonders;
import net.turtleboi.winterwonders.init.ModItems;
import net.turtleboi.winterwonders.init.ModTags;

import java.util.List;

public class ModToolTiers {
    public static final Tier COLDSTEEL = TierSortingRegistry.registerTier(
            new ForgeTier(2, 512, 7f, 2.5f, 15,
                    ModTags.Blocks.NEEDS_COLDSTEEL_TOOL, () -> Ingredient.of(ModItems.COLDSTEEL_INGOT.get())),
            new ResourceLocation(WinterWonders.MOD_ID, "coldsteel"), List.of(Tiers.IRON), List.of());
}

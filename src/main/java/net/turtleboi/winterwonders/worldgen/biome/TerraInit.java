package net.turtleboi.winterwonders.worldgen.biome;

import net.minecraft.resources.ResourceLocation;
import net.turtleboi.winterwonders.WinterWonders;
import terrablender.api.Regions;

public class TerraInit
{
    public static void register()
    {
        Regions.register(new OurOverworldRegion(new ResourceLocation(WinterWonders.MOD_ID, "overworld"), 12));
    }
}

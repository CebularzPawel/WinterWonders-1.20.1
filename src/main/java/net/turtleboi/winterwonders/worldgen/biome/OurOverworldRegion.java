package net.turtleboi.winterwonders.worldgen.biome;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.turtleboi.winterwonders.WinterWonders;
import terrablender.api.Region;
import terrablender.api.RegionType;
import terrablender.worldgen.DefaultOverworldRegion;

import java.util.function.Consumer;


public class OurOverworldRegion extends Region {
    public OurOverworldRegion(ResourceLocation name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint,
                    ResourceKey<Biome>>> mapper) {
        this.addModifiedVanillaOverworldBiomes(mapper, modifiedVanillaOverworldBuilder -> {
            modifiedVanillaOverworldBuilder.replaceBiome(Biomes.FOREST, WinterFrostBiome.WINTER_FROST);
        });
    }
}
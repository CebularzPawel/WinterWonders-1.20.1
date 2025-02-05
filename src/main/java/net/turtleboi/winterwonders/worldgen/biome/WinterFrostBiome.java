package net.turtleboi.winterwonders.worldgen.biome;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.turtleboi.winterwonders.WinterWonders;
import net.turtleboi.winterwonders.init.ModEntities;
import net.turtleboi.winterwonders.worldgen.ModPlacedFeatures;

public final class WinterFrostBiome {
    public static final ResourceKey<Biome> WINTER_FROST = ResourceKey.create(Registries.BIOME, new ResourceLocation(WinterWonders.MOD_ID, "winter_frost"));

    public static void bootstrap(BootstapContext<Biome> context) {
        context.register(WINTER_FROST, createBiome(context));
    }

    public static void globalOverworldGeneration(BiomeGenerationSettings.Builder builder) {
        BiomeDefaultFeatures.addDefaultCarversAndLakes(builder);
        BiomeDefaultFeatures.addDefaultCrystalFormations(builder);
        BiomeDefaultFeatures.addDefaultMonsterRoom(builder);
        BiomeDefaultFeatures.addDefaultUndergroundVariety(builder);
        BiomeDefaultFeatures.addDefaultSprings(builder);
        BiomeDefaultFeatures.addSurfaceFreezing(builder);
    }

    private static Biome createBiome(BootstapContext<Biome> context) {
        MobSpawnSettings.Builder spawnSettings = new MobSpawnSettings.Builder()
                .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.REVENANT.get(), 12, 6, 10))
                .addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(ModEntities.SNOW_WISP.get(), 8, 6, 8));

        BiomeSpecialEffects effects = new BiomeSpecialEffects.Builder()
                .fogColor(0x6A98D4)
                .skyColor(0xA3D0F8)
                .waterColor(0x4E89D5)
                .waterFogColor(0x5FA2E5)
                .grassColorOverride(0x78B1E1)
                .build();

        BiomeGenerationSettings.Builder settings = new BiomeGenerationSettings.Builder(
                context.lookup(Registries.PLACED_FEATURE),
                context.lookup(Registries.CONFIGURED_CARVER));

        globalOverworldGeneration(settings);
        BiomeDefaultFeatures.addMossyStoneBlock(settings);
        BiomeDefaultFeatures.addDefaultOres(settings);
        BiomeDefaultFeatures.addFerns(settings);
        BiomeDefaultFeatures.addDefaultFlowers(settings);
        settings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.GREYPINE_PLACED_KEY);

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .downfall(0.9F) // Adjusted to fit a wintery environment
                .temperature(-0.5F) // Freezing cold environment
                .mobSpawnSettings(spawnSettings.build())
                .specialEffects(effects)
                .generationSettings(settings.build())
                .temperatureAdjustment(Biome.TemperatureModifier.FROZEN)
                .build();
    }
}

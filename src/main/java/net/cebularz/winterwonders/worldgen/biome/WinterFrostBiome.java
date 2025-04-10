package net.cebularz.winterwonders.worldgen.biome;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.entity.ModEntities;
import net.cebularz.winterwonders.worldgen.ModPlacedFeatures;

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
                .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.BRISK.get(), 4, 1, 2))
                .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.REVENANT.get(), 8, 3, 6))
                .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.STRAY, 8, 3, 6))
                .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.SPIDER, 6, 2, 4))
                .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.CREEPER, 6, 2, 4))
                .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.ENDERMAN, 4, 1, 2))
                .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SNOW_WISP.get(), 12, 1, 3))

                .addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.PIG, 7, 2, 4))
                .addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.COW, 7, 2, 4))
                .addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.SHEEP, 7, 2, 4))
                .addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.FROG, 4, 1, 2))
                .addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.FOX, 3, 1, 3));

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
        settings.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, ModPlacedFeatures.COLDSTONE_SPIKE_KEY);
        BiomeDefaultFeatures.addDefaultOres(settings);
        BiomeDefaultFeatures.addFerns(settings);
        BiomeDefaultFeatures.addDefaultFlowers(settings);
        BiomeDefaultFeatures.addTaigaGrass(settings);
        settings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.GREYPINE_PLACED_KEY);
        settings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.WONDER_SHROOM_PATCH_KEY);
        settings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.WINTER_FROST_FLOWERS_KEY);
        settings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.MAGICAL_FLOWERS_KEY);


        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .downfall(0.9F)
                .temperature(0F)
                .mobSpawnSettings(spawnSettings.build())
                .specialEffects(effects)
                .generationSettings(settings.build())
                .temperatureAdjustment(Biome.TemperatureModifier.FROZEN)
                .build();
    }
}

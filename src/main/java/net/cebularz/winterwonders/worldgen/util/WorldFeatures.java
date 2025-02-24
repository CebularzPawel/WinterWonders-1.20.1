package net.cebularz.winterwonders.worldgen.util;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.cebularz.winterwonders.WinterWonders;

import java.util.HashMap;
import java.util.*;
import java.util.function.Supplier;

public class WorldFeatures
{
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, WinterWonders.MOD_ID);

    private static <C extends FeatureConfiguration, F extends Feature<C>> RegistryObject<F> register(final String name, final Supplier<? extends F> supplier) {
        return FEATURES.register(name, supplier);
    }

    public static HashMap<String, Boolean> LOADED_FEATURES;

    private static List<String> ADDED_FEATURES;

    static {
        LOADED_FEATURES = new HashMap<>();
    }
    public static void addFeatures(Holder<Biome> biome, HashMap<String, Holder<PlacedFeature>> features, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        ADDED_FEATURES = new ArrayList<>();

        if (!ADDED_FEATURES.isEmpty()) {
            StringBuilder featureList = new StringBuilder();

            for (String feature : ADDED_FEATURES) {
                featureList.append("\n").append("\t- ").append(feature);
            }

            WinterWonders.LOGGER.debug("Added the following features to the biome [{}]: {}", biome.unwrapKey().get().location(), featureList);
        }

        ADDED_FEATURES = null;
    }

    private static void addFeatureToBiome(ResourceKey<PlacedFeature> feature, HashMap<String, Holder<PlacedFeature>> features, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        addFeatureToBiome(feature, features, builder, GenerationStep.Decoration.SURFACE_STRUCTURES);
    }

    private static void addFeatureToBiome(ResourceKey<PlacedFeature> featureResource, HashMap<String, Holder<PlacedFeature>> features, ModifiableBiomeInfo.BiomeInfo.Builder builder, GenerationStep.Decoration step) {
        String identifier = featureResource.location().toString();
        Holder<PlacedFeature> feature = features.get(identifier);

        if (feature != null) {
            builder.getGenerationSettings().getFeatures(step).add(feature);
            LOADED_FEATURES.put(identifier, true);
            ADDED_FEATURES.add(identifier);
        } else {
            WinterWonders.LOGGER.warn("Feature [{}] could not be found", identifier);
        }
    }
}

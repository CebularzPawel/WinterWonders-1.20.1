package net.turtleboi.winterwonders.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.registries.ForgeRegistries;
import net.turtleboi.winterwonders.WinterWonders;
import net.turtleboi.winterwonders.worldgen.ModBiomeModifiers;
import net.turtleboi.winterwonders.worldgen.ModConfiguredFeatures;
import net.turtleboi.winterwonders.worldgen.ModPlacedFeatures;
import net.turtleboi.winterwonders.worldgen.biome.WinterFrostBiome;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModWorldGenProvider extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, ModConfiguredFeatures::bootstrap)
            .add(Registries.PLACED_FEATURE, ModPlacedFeatures::bootstrap)
            .add(Registries.BIOME, WinterFrostBiome::bootstrap)
            .add(ForgeRegistries.Keys.BIOME_MODIFIERS, ModBiomeModifiers::bootstrap);

    public ModWorldGenProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(WinterWonders.MOD_ID));
    }
}

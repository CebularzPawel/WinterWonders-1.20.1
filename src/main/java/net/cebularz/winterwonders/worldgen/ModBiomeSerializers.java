package net.cebularz.winterwonders.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.registries.ForgeRegistries;
import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.worldgen.util.ListHolderSet;

import java.util.ArrayList;
import java.util.List;

public class ModBiomeSerializers
{
    public static ResourceKey<BiomeModifier> FEATURES = createKey("winterwonders_features");

    public static ResourceKey<BiomeModifier> createKey(String name) {
        return ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, new ResourceLocation(WinterWonders.MOD_ID, name));
    }

    private static ListHolderSet<PlacedFeature> createHolderSet(HolderGetter<PlacedFeature> holderGetter, List<ResourceKey<PlacedFeature>> features) {
        List<Holder<PlacedFeature>> holders = new ArrayList<>();
        features.forEach(feature -> holders.add(holderGetter.getOrThrow(feature)));
        return new ListHolderSet<>(holders);
    }

    public static void bootstap(BootstapContext<BiomeModifier> context)
    {
        HolderGetter<PlacedFeature> holderGetter = context.lookup(Registries.PLACED_FEATURE);
        List<ResourceKey<PlacedFeature>> features = List.of(
        );
        context.register(FEATURES, new ModFeatureBiomeModifier(createHolderSet(holderGetter, features)));
    }
}

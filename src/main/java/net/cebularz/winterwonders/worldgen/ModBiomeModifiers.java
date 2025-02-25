package net.cebularz.winterwonders.worldgen;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.registries.ForgeRegistries;
import net.cebularz.winterwonders.WinterWonders;

public class ModBiomeModifiers {
//    public static final ResourceKey<BiomeModifier> ADD_TREE_GREYPINE = registerKey("add_tree_pine");

    public static void bootstrap(BootstapContext<BiomeModifier> context) {
//        var placedFeatures = context.lookup(Registries.PLACED_FEATURE);
//        var biomes = context.lookup(Registries.BIOME);
//
//        context.register(ADD_TREE_GREYPINE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
//                biomes.getOrThrow(Tags.Biomes.IS_CONIFEROUS),
//                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.GREYPINE_PLACED_KEY)),
//                GenerationStep.Decoration.VEGETAL_DECORATION));
    }

    private static ResourceKey<BiomeModifier> registerKey(String name) {
        return ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, new ResourceLocation(WinterWonders.MOD_ID, name));
    }
}

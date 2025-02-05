package net.turtleboi.winterwonders.worldgen;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.turtleboi.winterwonders.WinterWonders;
import net.turtleboi.winterwonders.worldgen.util.WorldFeatures;

import java.util.HashMap;

public class ModFeatureBiomeModifier implements BiomeModifier {
    private static final RegistryObject<Codec<? extends BiomeModifier>> SERIALIZER = RegistryObject.create(new ResourceLocation(WinterWonders.MOD_ID, "winterwonders_features"), ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, WinterWonders.MOD_ID);
    private final HolderSet<PlacedFeature> features;
    public final HashMap<String, Holder<PlacedFeature>> featureMap = new HashMap<>();

    public ModFeatureBiomeModifier(HolderSet<PlacedFeature> features) {
        this.features = features;
        this.features.forEach(feature -> featureMap.put(feature.unwrapKey().get().location().toString(), feature));
    }

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase == Phase.ADD) {
            WorldFeatures.addFeatures(biome, featureMap, builder);
        }
    }

    public Codec<? extends BiomeModifier> codec() {
        return SERIALIZER.get();
    }

    public static Codec<ModFeatureBiomeModifier> makeCodec() {
        return RecordCodecBuilder.create(config -> config.group(PlacedFeature.LIST_CODEC.fieldOf("features").forGetter((otherConfig) -> otherConfig.features)).apply(config, ModFeatureBiomeModifier::new));
    }
}
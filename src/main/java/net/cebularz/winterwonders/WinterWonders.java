package net.cebularz.winterwonders;

import com.mojang.logging.LogUtils;
import net.cebularz.winterwonders.network.ModNetworking;
import net.cebularz.winterwonders.particle.ModParticles;
import net.cebularz.winterwonders.worldgen.biome.BiomeMusicManager;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.cebularz.winterwonders.entity.custom.PinginEntity;
import net.cebularz.winterwonders.entity.custom.SnowWispEntity;
import net.cebularz.winterwonders.init.*;
import net.cebularz.winterwonders.worldgen.biome.TerraInit;
import net.cebularz.winterwonders.worldgen.biome.WWSurfaceRules;
import net.cebularz.winterwonders.worldgen.tree.ModFoliagePlacers;
import net.cebularz.winterwonders.worldgen.tree.ModTrunkPlacers;
import org.slf4j.Logger;
import terrablender.api.SurfaceRuleManager;

@Mod(WinterWonders.MOD_ID)
public class WinterWonders {
    public static final String MOD_ID = "winterwonders";
    public static final Logger LOGGER = LogUtils.getLogger();

    public WinterWonders() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModFeatures.DEF_REG.register(modEventBus);
        ModParticles.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        ModEffects.register(modEventBus);
        ModEntities.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModAttributes.REGISTRY.register(modEventBus);
        modEventBus.addListener(this::commonSetup);
        ModTrunkPlacers.register(modEventBus);
        ModFoliagePlacers.register(modEventBus);
        ModSongs.REGISTRAR.register(modEventBus);
        TerraInit.register();
        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            SpawnPlacements.register(ModEntities.SNOW_WISP.get(), SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.WORLD_SURFACE, SnowWispEntity::canSpawn);
            SpawnPlacements.register(ModEntities.REVENANT.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE, Monster::checkMonsterSpawnRules);
            SpawnPlacements.register(ModEntities.BRISK.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE, Monster::checkMonsterSpawnRules);
            SpawnPlacements.register(ModEntities.PINGIN.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE, PinginEntity::canSpawn);
            SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD,MOD_ID, WWSurfaceRules.makeRules());

            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.MAGICAL_ROSE.getId(),ModBlocks.POTTED_MAGICAL_ROSE);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.ICE_FLOWER.getId(),ModBlocks.POTTED_ICE_FLOWER);
        });
        ModNetworking.register();
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.ICE_SLUSH.get(),RenderType.translucent());
        }
    }
}

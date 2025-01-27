package net.turtleboi.winterwonders;

import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.SpawnPlacements;
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
import net.turtleboi.winterwonders.client.renderers.entity.SnowWispRenderer;
import net.turtleboi.winterwonders.entity.custom.SnowWispEntity;
import net.turtleboi.winterwonders.init.*;
import org.slf4j.Logger;

@Mod(WinterWonders.MOD_ID)
public class WinterWonders {
    public static final String MOD_ID = "winterwonders";
    public static final Logger LOGGER = LogUtils.getLogger();

    public WinterWonders() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        ModEntities.register(modEventBus);
        ModParticles.register(modEventBus);
        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            SpawnPlacements.register(ModEntities.SNOW_WISP.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE, SnowWispEntity::canSpawn);
        });
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.ICE_SLUSH.get(),RenderType.translucent());
            EntityRenderers.register(ModEntities.SNOW_WISP.get(), SnowWispRenderer::new);
        }
    }
}

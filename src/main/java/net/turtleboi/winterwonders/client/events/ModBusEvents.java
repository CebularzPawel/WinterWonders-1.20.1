package net.turtleboi.winterwonders.client.events;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.turtleboi.winterwonders.WinterWonders;
import net.turtleboi.winterwonders.client.models.ModModelLayers;
import net.turtleboi.winterwonders.client.models.entity.SnowWispModel;
import net.turtleboi.winterwonders.client.particles.AuroraParticle;
import net.turtleboi.winterwonders.client.renderers.entity.SnowWispRenderer;
import net.turtleboi.winterwonders.entity.custom.SnowWisp;
import net.turtleboi.winterwonders.init.ModEntities;
import net.turtleboi.winterwonders.init.ModParticles;

@Mod.EventBusSubscriber(modid = WinterWonders.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBusEvents
{

    @SubscribeEvent
    public static void onClientSetupEvent(FMLClientSetupEvent event)
    {
        EntityRenderers.register(ModEntities.SNOW_WISP.get(), SnowWispRenderer::new);
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event)
    {
        event.put(ModEntities.SNOW_WISP.get(), SnowWisp.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerEntityLayer(EntityRenderersEvent.RegisterLayerDefinitions event)
    {
        event.registerLayerDefinition(ModModelLayers.SNOW_WISP_LAYER, SnowWispModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.AURORA_PARTICLE.get(),
                AuroraParticle.Provider::new);
    }
}

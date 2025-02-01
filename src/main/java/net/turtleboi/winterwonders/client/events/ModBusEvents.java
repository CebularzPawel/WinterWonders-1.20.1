package net.turtleboi.winterwonders.client.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.turtleboi.winterwonders.WinterWonders;
import net.turtleboi.winterwonders.client.models.entity.BriskModel;
import net.turtleboi.winterwonders.client.models.entity.PinginModel;
import net.turtleboi.winterwonders.client.models.entity.SnowWispModel;
import net.turtleboi.winterwonders.client.models.entity.projectile.IceSpikeModel;
import net.turtleboi.winterwonders.client.particles.AuroraParticle;
import net.turtleboi.winterwonders.client.renderers.entity.BriskRenderer;
import net.turtleboi.winterwonders.client.renderers.entity.PinginRenderer;
import net.turtleboi.winterwonders.client.renderers.entity.RevenantRenderer;
import net.turtleboi.winterwonders.client.renderers.entity.SnowWispRenderer;
import net.turtleboi.winterwonders.client.renderers.entity.projectile.IceSpikeRenderer;
import net.turtleboi.winterwonders.entity.custom.BriskEntity;
import net.turtleboi.winterwonders.entity.custom.PinginEntity;
import net.turtleboi.winterwonders.entity.custom.RevenantEntity;
import net.turtleboi.winterwonders.entity.custom.SnowWispEntity;
import net.turtleboi.winterwonders.init.ModEntities;
import net.turtleboi.winterwonders.init.ModParticles;

@Mod.EventBusSubscriber(modid = WinterWonders.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBusEvents
{

    @SubscribeEvent
    public static void onClientSetupEvent(FMLClientSetupEvent event) {
        EntityRenderers.register(ModEntities.SNOW_WISP.get(), SnowWispRenderer::new);
        EntityRenderers.register(ModEntities.REVENANT.get(), RevenantRenderer::new);
        EntityRenderers.register(ModEntities.PINGIN.get(), PinginRenderer::new);
        EntityRenderers.register(ModEntities.BRISK.get(), BriskRenderer::new);
        EntityRenderers.register(ModEntities.ICE_SPIKE.get(), IceSpikeRenderer::new);
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.SNOW_WISP.get(), SnowWispEntity.createAttributes().build());
        event.put(ModEntities.REVENANT.get(), RevenantEntity.createAttributes().build());
        event.put(ModEntities.PINGIN.get(), PinginEntity.createAttributes().build());
        event.put(ModEntities.BRISK.get(), BriskEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerEntityLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SnowWispModel.SNOW_WISP_LAYER, SnowWispModel::createBodyLayer);
        event.registerLayerDefinition(PinginModel.PINGIN_LAYER, PinginModel::createBodyLayer);
        event.registerLayerDefinition(BriskModel.BRISK_LAYER, BriskModel::createBodyLayer);
        event.registerLayerDefinition(IceSpikeModel.ICE_SPIKE_LAYER, IceSpikeModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.AURORA_PARTICLE.get(),
                AuroraParticle.Provider::new);
    }
}

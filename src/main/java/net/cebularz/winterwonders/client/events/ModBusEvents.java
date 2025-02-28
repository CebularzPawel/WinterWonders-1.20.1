package net.cebularz.winterwonders.client.events;

import net.cebularz.winterwonders.client.models.entity.LichModel;
import net.cebularz.winterwonders.client.renderers.entity.*;
import net.cebularz.winterwonders.entity.custom.*;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.client.models.blocks.IcyVinesModel;
import net.cebularz.winterwonders.client.models.blocks.IcyVinesPlantModel;
import net.cebularz.winterwonders.client.models.entity.BriskModel;
import net.cebularz.winterwonders.client.models.entity.PinginModel;
import net.cebularz.winterwonders.client.models.entity.SnowWispModel;
import net.cebularz.winterwonders.client.models.entity.projectile.IceSpikeModel;
import net.cebularz.winterwonders.client.particles.AuroraParticle;
import net.cebularz.winterwonders.client.renderers.block.IcyVinesBlockEntityRenderer;
import net.cebularz.winterwonders.client.renderers.block.IcyVinesPlantBlockEntityRenderer;
import net.cebularz.winterwonders.client.renderers.entity.projectile.IceSpikeRenderer;
import net.cebularz.winterwonders.init.ModBlockEntities;
import net.cebularz.winterwonders.init.ModEntities;
import net.cebularz.winterwonders.init.ModParticles;

@Mod.EventBusSubscriber(modid = WinterWonders.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBusEvents
{

    @SubscribeEvent
    public static void onClientSetupEvent(FMLClientSetupEvent event) {
        EntityRenderers.register(ModEntities.SNOW_WISP.get(), SnowWispRenderer::new);
        EntityRenderers.register(ModEntities.REVENANT.get(), RevenantRenderer::new);
        EntityRenderers.register(ModEntities.PINGIN.get(), PinginRenderer::new);
        EntityRenderers.register(ModEntities.LICH.get(), LichRenderer::new);
        EntityRenderers.register(ModEntities.BRISK.get(), BriskRenderer::new);
        EntityRenderers.register(ModEntities.ICE_SPIKE.get(), IceSpikeRenderer::new);
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.SNOW_WISP.get(), SnowWispEntity.createAttributes().build());
        event.put(ModEntities.REVENANT.get(), RevenantEntity.createAttributes().build());
        event.put(ModEntities.PINGIN.get(), PinginEntity.createAttributes().build());
        event.put(ModEntities.BRISK.get(), BriskEntity.createAttributes().build());
        event.put(ModEntities.LICH.get(), LichEntity.createMobAttributes().build());
    }

    @SubscribeEvent
    public static void registerEntityLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SnowWispModel.SNOW_WISP_LAYER, SnowWispModel::createBodyLayer);
        event.registerLayerDefinition(PinginModel.PINGIN_LAYER, PinginModel::createBodyLayer);
        event.registerLayerDefinition(BriskModel.BRISK_LAYER, BriskModel::createBodyLayer);
        event.registerLayerDefinition(LichModel.LICH_LAYER, LichModel::createBodyLayer);
        event.registerLayerDefinition(IceSpikeModel.ICE_SPIKE_LAYER, IceSpikeModel::createBodyLayer);
        event.registerLayerDefinition(IcyVinesModel.ICY_VINES_LAYER, IcyVinesModel::createBodyLayer);
        event.registerLayerDefinition(IcyVinesPlantModel.ICY_VINES_PLANT_LAYER, IcyVinesModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event){
        event.registerBlockEntityRenderer(ModBlockEntities.ICY_VINES_BE.get(), IcyVinesBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.ICY_VINES_PLANT_BE.get(), IcyVinesPlantBlockEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.AURORA_PARTICLE.get(),
                AuroraParticle.Provider::new);
    }
}

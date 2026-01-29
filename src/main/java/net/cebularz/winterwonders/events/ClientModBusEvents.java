package net.cebularz.winterwonders.events;

import net.cebularz.winterwonders.block.ModBlocks;
import net.cebularz.winterwonders.entity.models.projectile.OrnamentModel;
import net.cebularz.winterwonders.entity.renderers.projectile.IceCubeRenderer;
import net.cebularz.winterwonders.entity.custom.*;
import net.cebularz.winterwonders.entity.models.*;
import net.cebularz.winterwonders.entity.renderers.*;
import net.cebularz.winterwonders.entity.renderers.projectile.OrnamentRenderer;
import net.cebularz.winterwonders.item.ModItems;
import net.cebularz.winterwonders.item.custom.HearthstoneItem;
import net.cebularz.winterwonders.particle.ModParticles;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.block.entity.custom.models.IcyVinesModel;
import net.cebularz.winterwonders.block.entity.custom.models.IcyVinesPlantModel;
import net.cebularz.winterwonders.entity.models.projectile.IcicleProjectileModel;
import net.cebularz.winterwonders.particle.particles.AuroraParticle;
import net.cebularz.winterwonders.block.entity.custom.renderers.IcyVinesBlockEntityRenderer;
import net.cebularz.winterwonders.block.entity.custom.renderers.IcyVinesPlantBlockEntityRenderer;
import net.cebularz.winterwonders.entity.renderers.projectile.IcicleProjectileRenderer;
import net.cebularz.winterwonders.block.entity.ModBlockEntities;
import net.cebularz.winterwonders.entity.ModEntities;

@Mod.EventBusSubscriber(modid = WinterWonders.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModBusEvents {

    @SubscribeEvent
    public static void onClientSetupEvent(FMLClientSetupEvent event) {
        EntityRenderers.register(ModEntities.SNOW_WISP.get(), SnowWispRenderer::new);
        EntityRenderers.register(ModEntities.REVENANT.get(), RevenantRenderer::new);
        EntityRenderers.register(ModEntities.PINGIN.get(), PinginRenderer::new);
        EntityRenderers.register(ModEntities.MITTEN_MOUSE.get(), MittenMouseRenderer::new);
        EntityRenderers.register(ModEntities.SNOW_WEASEL.get(), SnowWeaselRenderer::new);
        EntityRenderers.register(ModEntities.BRISK.get(), BriskRenderer::new);
        EntityRenderers.register(ModEntities.LICH.get(), LichRenderer::new);
        EntityRenderers.register(ModEntities.ICICLE.get(), IcicleProjectileRenderer::new);
        EntityRenderers.register(ModEntities.ICE_CUBE.get(), IceCubeRenderer::new);
        EntityRenderers.register(ModEntities.ORNAMENT.get(), OrnamentRenderer::new);

        event.enqueueWork(() -> {
            ItemProperties.register(
                    ModItems.HEARTHSTONE.get(),
                    new ResourceLocation(WinterWonders.MOD_ID, "bound"),
                    (stack, level, entity, seed) -> HearthstoneItem.isBound(stack) ? 1.0F : 0.0F
            );
        });
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.SNOW_WISP.get(), SnowWispEntity.createAttributes().build());
        event.put(ModEntities.REVENANT.get(), RevenantEntity.createAttributes().build());
        event.put(ModEntities.PINGIN.get(), PinginEntity.createAttributes().build());
        event.put(ModEntities.MITTEN_MOUSE.get(), MittenMouseEntity.createAttributes().build());
        event.put(ModEntities.SNOW_WEASEL.get(), SnowWeaselEntity.createAttributes().build());
        event.put(ModEntities.BRISK.get(), BriskEntity.createAttributes().build());
        event.put(ModEntities.LICH.get(), LichEntity.createAttribute().build());
    }

    @SubscribeEvent
    public static void registerEntityLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SnowWispModel.SNOW_WISP_LAYER, SnowWispModel::createBodyLayer);
        event.registerLayerDefinition(PinginModel.PINGIN_LAYER, PinginModel::createBodyLayer);
        event.registerLayerDefinition(MittenMouseModel.MOUSE_LAYER, MittenMouseModel::createBodyLayer);
        event.registerLayerDefinition(SnowWeaselModel.SNOW_WEASEL_LAYER, SnowWeaselModel::createBodyLayer);
        event.registerLayerDefinition(BriskModel.BRISK_LAYER, BriskModel::createBodyLayer);
        event.registerLayerDefinition(LichModel.LICH_LAYER, LichModel::createBodyLayer);
        event.registerLayerDefinition(IcicleProjectileModel.ICICLE_LAYER, IcicleProjectileModel::createBodyLayer);
        event.registerLayerDefinition(OrnamentModel.ORNAMENT_LAYER, OrnamentModel::createBodyLayer);
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

package net.turtleboi.winterwonders.event;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.turtleboi.winterwonders.WinterWonders;
import net.turtleboi.winterwonders.entity.ModEntities;
import net.turtleboi.winterwonders.particle.ModParticleTypes;
import net.turtleboi.winterwonders.particle.custom.CursedFlameParticle;
import net.turtleboi.winterwonders.particle.custom.CursedParticle;
import net.turtleboi.winterwonders.particle.custom.HealParticle;
import net.turtleboi.winterwonders.particle.custom.SleepParticle;

@Mod.EventBusSubscriber(modid = WinterWonders.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientBusEvents {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(ModEntities.CURSED_PEARL.get(), ThrownItemRenderer::new);
        //MenuScreens.register(ModMenuTypes.LAPIDARIST_MENU.get(), LapidaristTableContainerScreen::new);
    }

    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event){
        //event.registerLayerDefinition(CursedPortalModel.CURSED_PORTAL_LAYER, CursedPortalModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event){
        //event.registerBlockEntityRenderer(ModBlockEntities.CURSED_ALTAR_BE.get(), CursedAltarRenderer::new);
    }

    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event){
        event.registerSpriteSet(ModParticleTypes.HEAL_PARTICLE.get(),
                HealParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.CURSED_PARTICLE.get(),
                CursedParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.SLEEP_PARTICLE.get(),
                SleepParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.CURSED_FLAME_PARTICLE.get(),
                CursedFlameParticle.Provider::new);
    }
}

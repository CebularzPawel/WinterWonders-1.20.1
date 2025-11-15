package net.cebularz.winterwonders.events;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.client.data.LichBossData;
import net.cebularz.winterwonders.client.gui.LichBossBar;
import net.cebularz.winterwonders.client.shaders.blizzard.BlizzardRenderer;
import net.cebularz.winterwonders.client.spell.SpellVisualManager;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.io.IOException;

@Mod.EventBusSubscriber(modid = WinterWonders.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(BlizzardRenderer.getInstance());
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen == null) {
            LichBossData.removeFarAwayBosses();
            int x = event.getWindow().getGuiScaledWidth() / 2 - 96;
            int y = 10;
            LichBossBar.render(event.getGuiGraphics(), x, y, minecraft);
        }
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        Camera camera = event.getCamera();
        SpellVisualManager.renderIceSpikes(poseStack, buffer, camera);
    }
}

package net.cebularz.winterwonders.client.events;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.client.shaders.blizzard.BlizzardRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.io.IOException;

@Mod.EventBusSubscriber(modid = WinterWonders.MOD_ID,bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ModClientEvents
{
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(BlizzardRenderer.getInstance());
    }

    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) {
        try {
            ShaderInstance shaderInstance = new ShaderInstance(
                    event.getResourceProvider(),
                    new ResourceLocation("winterwonders", "shaders/post/blizzard"),
                    DefaultVertexFormat.POSITION_TEX);
            event.registerShader(shaderInstance, shader -> {
                BlizzardRenderer.getInstance().setShader(shader);
            });
        } catch (IOException e) {
            WinterWonders.LOGGER.error("Failed to load blizzard shader", e);
        }
    }

    @Mod.EventBusSubscriber(modid = WinterWonders.MOD_ID, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            BlizzardRenderer.getInstance().tick();
        }
    }
}

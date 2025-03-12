package net.cebularz.winterwonders.client.shaders.blizzard;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BlizzardRenderer {
    private static final ResourceLocation BLIZZARD_SHADER =
            new ResourceLocation("winterwonders", "shaders/post/blizzard.json");

    private float effectTime = 0;
    private boolean isActive = false;
    private int duration = 0;
    private Vec3 blizzardCenter;
    private float intensity = 1.0f;
    private ShaderInstance shader;
    private static BlizzardRenderer INSTANCE;

    public static BlizzardRenderer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BlizzardRenderer();
        }

        return INSTANCE;
    }

    public void startBlizzard(Vec3 center, int durationTicks, float intensity) {
        this.blizzardCenter = center;
        this.duration = durationTicks;
        this.intensity = Math.min(Math.max(intensity, 0.1f), 1.0f);
        this.isActive = true;
        this.effectTime = 0;
    }

    public void stopBlizzard() {
        this.isActive = false;
    }

    public void tick() {
        if (isActive) {
            effectTime += 0.016f;

            if (duration > 0) {
                duration--;
                if (duration <= 0) {
                    isActive = false;
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderLevel(RenderLevelStageEvent event) {
        if (!isActive || event.getStage() != RenderLevelStageEvent.Stage.AFTER_WEATHER) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();

        if (shader == null) {
            return;
        }

        float distanceModifier = 1.0f;
        if (blizzardCenter != null && mc.player != null) {
            double distance = blizzardCenter.distanceTo(mc.player.position());
            distanceModifier = (float) Math.max(0.0, 1.0 - (distance / 16.0));
        }

        int width = mc.getWindow().getWidth();
        int height = mc.getWindow().getHeight();

        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        Vec3 windDirection = new Vec3(1.0, -0.5, 0.0);
        if (blizzardCenter != null && mc.player != null) {
            Vec3 playerPos = mc.player.position();
            Vec3 direction = playerPos.subtract(blizzardCenter).normalize();
            windDirection = new Vec3(direction.x, -0.5, direction.z);
        }

        shader.safeGetUniform("time").set(effectTime);
        shader.safeGetUniform("resolution").set((float) width, (float) height);
        shader.safeGetUniform("windStrength").set(0.7f * intensity * distanceModifier);
        shader.safeGetUniform("snowDensity").set(0.8f * intensity * distanceModifier);
        shader.safeGetUniform("windDirection").set((float) windDirection.x, (float) windDirection.y, (float) windDirection.z);

        RenderSystem.setShader(() -> shader);

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuilder();

        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        bufferBuilder.vertex(0, height, 0).uv(0, 1).endVertex();
        bufferBuilder.vertex(width, height, 0).uv(1, 1).endVertex();
        bufferBuilder.vertex(width, 0, 0).uv(1, 0).endVertex();
        bufferBuilder.vertex(0, 0, 0).uv(0, 0).endVertex();

        tessellator.end();

        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
    }

    public ShaderInstance getShader() {
        return shader;
    }

    public void setShader(ShaderInstance shader)
    {
        this.shader = shader;
    }
}
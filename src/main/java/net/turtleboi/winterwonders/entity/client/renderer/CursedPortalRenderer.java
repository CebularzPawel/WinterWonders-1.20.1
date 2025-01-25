package net.turtleboi.winterwonders.entity.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.turtleboi.winterwonders.WinterWonders;
import net.turtleboi.winterwonders.entity.CursedPortalEntity;
import net.turtleboi.winterwonders.entity.client.CursedPortalModel;
import org.jetbrains.annotations.NotNull;

public class CursedPortalRenderer extends EntityRenderer<CursedPortalEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(WinterWonders.MOD_ID, "textures/entity/cursed_portal.png");
    private final CursedPortalModel<CursedPortalEntity> model;

    public CursedPortalRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new CursedPortalModel<>(pContext.bakeLayer(CursedPortalModel.CURSED_PORTAL_LAYER));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull CursedPortalEntity pEntity) {
        int textureIndex = pEntity.getTextureIndex();
        return new ResourceLocation(WinterWonders.MOD_ID, "textures/entity/cursed_portal/cursed_portal" + textureIndex + ".png");
    }

    @Override
    public void render(CursedPortalEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            double playerX = player.getX();
            double playerZ = player.getZ();
            double entityX = entity.getX();
            double entityZ = entity.getZ();
            double dirX = playerX - entityX;
            double dirZ = playerZ - entityZ;

            float yaw = (float) (Math.atan2(dirZ, dirX) * (180 / Math.PI)) - 90.0F;

            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(-yaw));
            poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.depthMask(false);
            VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityTranslucentCull(this.getTextureLocation(entity)));
            model.renderToBuffer(poseStack, vertexConsumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.9F);
            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
            poseStack.popPose();
        }

        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, light);
    }

}

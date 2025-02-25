package net.cebularz.winterwonders.client.renderers.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.client.models.entity.SnowWispModel;
import net.cebularz.winterwonders.entity.custom.SnowWispEntity;

public class SnowWispEyesLayer extends RenderLayer<SnowWispEntity, SnowWispModel<SnowWispEntity>> {
    private static final ResourceLocation EYES_TEXTURE = new ResourceLocation(WinterWonders.MOD_ID, "textures/entity/snow_wisp_eyes.png");

    public SnowWispEyesLayer(RenderLayerParent<SnowWispEntity, SnowWispModel<SnowWispEntity>> parent) {
        super(parent);
    }

    @Override
    public void render(
            PoseStack poseStack, MultiBufferSource buffer, int packedLight, SnowWispEntity entity, float limbSwing,
            float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch){
        VertexConsumer eyesConsumer = buffer.getBuffer(RenderType.eyes(EYES_TEXTURE));
        this.getParentModel().renderToBuffer(poseStack, eyesConsumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}

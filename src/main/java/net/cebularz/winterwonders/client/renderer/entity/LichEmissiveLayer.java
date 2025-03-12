package net.cebularz.winterwonders.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.client.models.entity.LichModel;
import net.cebularz.winterwonders.entity.custom.LichEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class LichEmissiveLayer extends RenderLayer<LichEntity, LichModel<LichEntity>> {
    private static final ResourceLocation EYES_TEXTURE = new ResourceLocation(WinterWonders.MOD_ID, "textures/entity/lich_emissive.png");

    public LichEmissiveLayer(RenderLayerParent<LichEntity, LichModel<LichEntity>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, LichEntity zombie, float v, float v1, float v2, float v3, float v4, float v5) {
        VertexConsumer eyesConsumer = multiBufferSource.getBuffer(RenderType.eyes(EYES_TEXTURE));
        this.getParentModel().renderToBuffer(poseStack, eyesConsumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}

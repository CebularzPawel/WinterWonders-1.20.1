package net.cebularz.winterwonders.entity.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.entity.custom.LichEntity;
import net.cebularz.winterwonders.entity.models.LichModel;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class LichRenderer extends MobRenderer<LichEntity, LichModel<LichEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(WinterWonders.MOD_ID, "textures/entity/lich.png");
    private static final RenderType EYES = RenderType.eyes(new ResourceLocation(WinterWonders.MOD_ID, "textures/entity/lich_emissive.png"));

    public LichRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new LichModel<>(pContext.bakeLayer(LichModel.LICH_LAYER)), 0.4F);
        this.addLayer(new RenderLayer<>(this) {
            @Override
            public void render(PoseStack poseStack, MultiBufferSource buffers, int light, LichEntity entity, float limbSwing,
                               float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
                VertexConsumer eyes = buffers.getBuffer(EYES);
                getParentModel().renderToBuffer(
                        poseStack,
                        eyes,
                        LightTexture.FULL_BRIGHT,
                        OverlayTexture.NO_OVERLAY,
                        1F, 1F, 1F, 1F);
            }
        });
        this.addLayer(new ItemInHandLayer<>(this, pContext.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(LichEntity lichEntity) {
        return TEXTURE;
    }
}

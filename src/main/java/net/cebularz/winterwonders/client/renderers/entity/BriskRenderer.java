package net.cebularz.winterwonders.client.renderers.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.client.models.entity.BriskModel;
import net.cebularz.winterwonders.entity.custom.BriskEntity;

public class BriskRenderer extends MobRenderer<BriskEntity, BriskModel<BriskEntity>> {
    private static ResourceLocation TEXTURE = new ResourceLocation(WinterWonders.MOD_ID, "textures/entity/brisk.png");
    public BriskRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new BriskModel<>(pContext.bakeLayer(BriskModel.BRISK_LAYER)),0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(BriskEntity briskEntity) {
        return TEXTURE;
    }

    @Override
    public void render(BriskEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
    }
}

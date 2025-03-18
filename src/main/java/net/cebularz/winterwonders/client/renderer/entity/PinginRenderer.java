package net.cebularz.winterwonders.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.client.models.entity.PinginModel;
import net.cebularz.winterwonders.entity.custom.PinginEntity;

public class PinginRenderer extends MobRenderer<PinginEntity, PinginModel<PinginEntity>> {
    private static ResourceLocation TEXTURE = new ResourceLocation(WinterWonders.MOD_ID, "textures/entity/pingin.png");
    public PinginRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new PinginModel<>(pContext.bakeLayer(PinginModel.PINGIN_LAYER)),0.5f);
        this.addLayer(new ItemInHandLayer<>(this, pContext.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(PinginEntity pinginEntity) {
        return TEXTURE;
    }

    @Override
    public void render(PinginEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        if (pEntity.isBaby()){
            TEXTURE = new ResourceLocation(WinterWonders.MOD_ID, "textures/entity/pingin_baby.png");
            pPoseStack.scale(0.75f, 0.75f, 0.75f);
        } else {
            TEXTURE = new ResourceLocation(WinterWonders.MOD_ID, "textures/entity/pingin.png");
        }
        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
    }
}

package net.cebularz.winterwonders.client.renderers.entity;

import net.cebularz.winterwonders.client.models.entity.LichModel;
import net.cebularz.winterwonders.entity.custom.LichEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class LichRenderer extends MobRenderer<LichEntity, LichModel<LichEntity>> {
    public LichRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new LichModel<>(pContext.bakeLayer(LichModel.LAYER_LOCATION)), 0.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(LichEntity lichEntity) {
        return new ResourceLocation("winterwonders:textures/entity/lich.png");
    }
}

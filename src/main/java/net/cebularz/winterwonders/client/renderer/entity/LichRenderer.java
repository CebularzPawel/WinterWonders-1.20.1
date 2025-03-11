package net.cebularz.winterwonders.client.renderer.entity;

import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.client.models.entity.LichModel;
import net.cebularz.winterwonders.entity.custom.LichEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public class LichRenderer extends MobRenderer<LichEntity, LichModel<LichEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(WinterWonders.MOD_ID, "textures/entity/lich.png");
    public LichRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new LichModel<>(pContext.bakeLayer(LichModel.LICH_LAYER)), 0.4F);
        this.addLayer(new LichEmissiveLayer(this));
        this.addLayer(new ItemInHandLayer<>(this, pContext.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(LichEntity lichEntity) {
        return TEXTURE;
    }
}

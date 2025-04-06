package net.cebularz.winterwonders.client.renderer.entity;

import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.client.models.entity.PinginModel;
import net.cebularz.winterwonders.client.models.entity.SnowWeaselModel;
import net.cebularz.winterwonders.entity.custom.PinginEntity;
import net.cebularz.winterwonders.entity.custom.SnowWeaselEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public class SnowWeaselRenderer extends MobRenderer<SnowWeaselEntity, SnowWeaselModel<SnowWeaselEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(WinterWonders.MOD_ID, "textures/entity/snow_weasel.png");
    public SnowWeaselRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new SnowWeaselModel<>(pContext.bakeLayer(SnowWeaselModel.SNOW_WEASEL_LAYER)),0.2f);
    }

    @Override
    public ResourceLocation getTextureLocation(SnowWeaselEntity snowWeaselEntity) {
        return TEXTURE;
    }
}

package net.turtleboi.winterwonders.client.renderers.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.turtleboi.winterwonders.WinterWonders;
import net.turtleboi.winterwonders.client.models.ModModelLayers;
import net.turtleboi.winterwonders.client.models.entity.SnowWispModel;
import net.turtleboi.winterwonders.entity.custom.SnowWisp;

public class SnowWispRenderer extends MobRenderer<SnowWisp, SnowWispModel>
{

    public SnowWispRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new SnowWispModel(pContext.bakeLayer(ModModelLayers.SNOW_WISP_LAYER)), 0.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(SnowWisp snowWisp) {
        return new ResourceLocation(WinterWonders.MOD_ID,"textures/entity/snow_wisp.png");
    }
}

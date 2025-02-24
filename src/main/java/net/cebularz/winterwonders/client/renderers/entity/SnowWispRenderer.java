package net.cebularz.winterwonders.client.renderers.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.client.models.entity.SnowWispModel;
import net.cebularz.winterwonders.entity.custom.SnowWispEntity;
import org.jetbrains.annotations.NotNull;

public class SnowWispRenderer extends MobRenderer<SnowWispEntity, SnowWispModel<SnowWispEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(WinterWonders.MOD_ID, "textures/entity/snow_wisp.png");

    public SnowWispRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new SnowWispModel<>(pContext.bakeLayer(SnowWispModel.SNOW_WISP_LAYER)), 0.25f);
        this.addLayer(new SnowWispEyesLayer(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SnowWispEntity snowWispEntity) {
        return TEXTURE;
    }

    @Override
    public void render(SnowWispEntity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        int color = entity.getColorMultiplier();
        this.model.setColor(color);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

}

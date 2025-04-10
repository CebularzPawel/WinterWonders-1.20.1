package net.cebularz.winterwonders.entity.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.entity.models.SnowWeaselModel;
import net.cebularz.winterwonders.entity.custom.SnowWeaselEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class SnowWeaselRenderer extends MobRenderer<SnowWeaselEntity, SnowWeaselModel<SnowWeaselEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(WinterWonders.MOD_ID, "textures/entity/snow_weasel.png");
    public SnowWeaselRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new SnowWeaselModel<>(pContext.bakeLayer(SnowWeaselModel.SNOW_WEASEL_LAYER)),0.2f);
    }

    @Override
    public ResourceLocation getTextureLocation(SnowWeaselEntity snowWeaselEntity) {
        return TEXTURE;
    }

    @Override
    protected @Nullable RenderType getRenderType(SnowWeaselEntity pLivingEntity, boolean pBodyVisible, boolean pTranslucent, boolean pGlowing) {
        return RenderType.entityCutout(TEXTURE);
    }

    @Override
    public void render(SnowWeaselEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        if (pEntity.isBaby()){
            pPoseStack.scale(0.66f, 0.66f, 0.66f);
        }
        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
    }
}

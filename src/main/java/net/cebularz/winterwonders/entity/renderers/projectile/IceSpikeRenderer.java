package net.cebularz.winterwonders.entity.renderers.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.entity.models.projectile.IceSpikeModel;
import net.cebularz.winterwonders.entity.custom.projectile.IceSpikeProjectileEntity;
import org.jetbrains.annotations.NotNull;

public class IceSpikeRenderer extends EntityRenderer<IceSpikeProjectileEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(WinterWonders.MOD_ID, "textures/entity/ice_spike.png");
    private final IceSpikeModel<IceSpikeProjectileEntity> model;
    public IceSpikeRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new IceSpikeModel<>(pContext.bakeLayer(IceSpikeModel.ICE_SPIKE_LAYER));
    }

    @Override
    public void render(IceSpikeProjectileEntity pEntity, float pEntityYaw, float pPartialTicks,
                       PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        pPoseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(pPartialTicks, pEntity.yRotO, pEntity.getYRot()) - 180.0F));
        pPoseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(pPartialTicks, pEntity.xRotO, pEntity.getXRot())));
        pPoseStack.translate(0.0, -1, 0.0);
        VertexConsumer vertexConsumer = pBuffer.getBuffer(RenderType.entityCutout(TEXTURE));
        this.model.renderToBuffer(
                pPoseStack,
                vertexConsumer,
                pPackedLight,
                OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 1.0F
        );
        pPoseStack.popPose();
        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
    }


    @Override
    public ResourceLocation getTextureLocation(IceSpikeProjectileEntity iceSpikeProjectileEntity) {
        return TEXTURE;
    }
}

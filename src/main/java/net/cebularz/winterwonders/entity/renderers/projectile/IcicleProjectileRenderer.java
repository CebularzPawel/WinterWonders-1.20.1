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
import net.cebularz.winterwonders.entity.models.projectile.IcicleProjectileModel;
import net.cebularz.winterwonders.entity.custom.projectile.IcicleProjectileEntity;
import org.jetbrains.annotations.NotNull;

public class IcicleProjectileRenderer extends EntityRenderer<IcicleProjectileEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(WinterWonders.MOD_ID, "textures/entity/icicle.png");
    private final IcicleProjectileModel<IcicleProjectileEntity> model;
    public IcicleProjectileRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new IcicleProjectileModel<>(pContext.bakeLayer(IcicleProjectileModel.ICICLE_LAYER));
    }

    @Override
    public void render(IcicleProjectileEntity pEntity, float pEntityYaw, float pPartialTicks,
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
    public ResourceLocation getTextureLocation(IcicleProjectileEntity icicleProjectileEntity) {
        return TEXTURE;
    }
}

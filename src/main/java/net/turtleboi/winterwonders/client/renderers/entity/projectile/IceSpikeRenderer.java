package net.turtleboi.winterwonders.client.renderers.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.turtleboi.winterwonders.WinterWonders;
import net.turtleboi.winterwonders.client.models.entity.projectile.IceSpikeModel;
import net.turtleboi.winterwonders.entity.custom.projectile.IceSpikeProjectileEntity;
import org.jetbrains.annotations.NotNull;

public class IceSpikeRenderer extends EntityRenderer<IceSpikeProjectileEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(WinterWonders.MOD_ID, "textures/entity/ice_spike.png");
    private final IceSpikeModel<IceSpikeProjectileEntity> model;
    public IceSpikeRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new IceSpikeModel<>(pContext.bakeLayer(IceSpikeModel.ICE_SPIKE_LAYER));
    }

    @Override
    public void render(IceSpikeProjectileEntity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int light) {
        poseStack.pushPose();
        float yaw = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());
        float pitch = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
        poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
        poseStack.mulPose(Axis.ZP.rotationDegrees(pitch));
        poseStack.translate(0.0, -1.25, 0.0);
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutout(TEXTURE));
        this.model.renderToBuffer(
                poseStack,
                vertexConsumer,
                light,
                OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 1.0F
        );

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, light);
    }


    @Override
    public ResourceLocation getTextureLocation(IceSpikeProjectileEntity iceSpikeProjectileEntity) {
        return TEXTURE;
    }
}

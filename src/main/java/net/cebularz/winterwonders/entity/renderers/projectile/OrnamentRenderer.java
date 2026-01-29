package net.cebularz.winterwonders.entity.renderers.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.entity.custom.projectile.OrnamentEntity;
import net.cebularz.winterwonders.entity.models.projectile.OrnamentModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class OrnamentRenderer extends EntityRenderer<OrnamentEntity> {
    private static final ResourceLocation RED_ORNAMENT =
            new ResourceLocation(WinterWonders.MOD_ID, "textures/entity/ornament/red_ornament.png");
    private static final ResourceLocation BLUE_ORNAMENT =
            new ResourceLocation(WinterWonders.MOD_ID, "textures/entity/ornament/blue_ornament.png");
    private static final ResourceLocation GOLD_ORNAMENT =
            new ResourceLocation(WinterWonders.MOD_ID, "textures/entity/ornament/gold_ornament.png");
    private static final ResourceLocation BLACK_ORNAMENT =
            new ResourceLocation(WinterWonders.MOD_ID, "textures/entity/ornament/black_ornament.png");

    private final OrnamentModel<OrnamentEntity> model;
    public OrnamentRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new OrnamentModel<>(context.bakeLayer(OrnamentModel.ORNAMENT_LAYER));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(OrnamentEntity entity) {
        int variantId = entity.getVariant().getVariantId();
        return switch (variantId) {
            case 0 -> RED_ORNAMENT;
            case 1 -> BLUE_ORNAMENT;
            case 2 -> GOLD_ORNAMENT;
            default -> BLACK_ORNAMENT;
        };
    }

    @Override
    public void render(@NotNull OrnamentEntity ornamentEntity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        ResourceLocation texture = getTextureLocation(ornamentEntity);
        float yRot = Mth.lerp(partialTick, ornamentEntity.yRotO, ornamentEntity.getYRot());
        float xRot = Mth.lerp(partialTick, ornamentEntity.xRotO, ornamentEntity.getXRot());

        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(-yRot - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(xRot));

        this.model.setupAnim(ornamentEntity, 0.0F, 0.0F, ornamentEntity.tickCount + partialTick, 0.0F, 0.0F);
        var vertexConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
        this.model.renderToBuffer(
                poseStack,
                vertexConsumer,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 1.0F
        );

        poseStack.popPose();
        super.render(ornamentEntity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }
}

package net.cebularz.winterwonders.entity.renderers.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.cebularz.winterwonders.entity.custom.projectile.IceCubeEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.turtleboi.turtlecore.client.util.VertexBuilder;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class IceCubeRenderer extends EntityRenderer<IceCubeEntity> {
    private final BlockRenderDispatcher dispatcher;

    public IceCubeRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.dispatcher = pContext.getBlockRenderDispatcher();
        this.shadowRadius = 0.35F;
    }

    @Override
    public void render(IceCubeEntity entity, float entityYaw, float partialTick, PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        BlockState state = Blocks.ICE.defaultBlockState();
        float ticks = entity.tickCount + partialTick;

        poseStack.pushPose();
        poseStack.translate(-0.5D, -0.5D, -0.5D);
        poseStack.translate(0.5D, 1.0D, 0.5D);
        float grow = Mth.clamp(ticks / 15.0F, 0.0F, 1.0F);
        poseStack.scale(grow, grow, grow);
        float yDeg = Mth.wrapDegrees(ticks * 3.5F);
        float xDeg = Mth.wrapDegrees(ticks * 3.5F * 0.35F);
        poseStack.mulPose(Axis.YP.rotationDegrees(yDeg));
        poseStack.mulPose(Axis.XP.rotationDegrees(xDeg));
        poseStack.translate(-0.5D, -0.5D, -0.5D);

        dispatcher.renderSingleBlock(
                state,
                poseStack,
                buffer,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                ModelData.EMPTY,
                RenderType.translucent()
        );

        int stage = entity.getCrackStage();
        if (stage > 0) {
            ResourceLocation crackTex = new ResourceLocation("textures/block/destroy_stage_" + stage + ".png");
            VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(crackTex));
            drawCrackCube(poseStack, vertexConsumer, 170, 0.0025f);
        }

        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(IceCubeEntity iceCubeEntity) {
        return new ResourceLocation("minecraft:textures/block/ice.png");
    }

    public static void drawCrackCube(PoseStack poseStack, VertexConsumer vertexConsumer, int alpha, float inflate) {
        Matrix4f matrix = poseStack.last().pose();
        Matrix3f normalMatrix = poseStack.last().normal();

        float min = 0.0f - inflate;
        float max = 1.0f + inflate;
        int r = 255, g = 255, b = 255;

        VertexBuilder.vertex(vertexConsumer, matrix, normalMatrix, min, min, max, 0, 1, r,g,b, alpha);
        VertexBuilder.vertex(vertexConsumer, matrix, normalMatrix, max, min, max, 1, 1, r,g,b, alpha);
        VertexBuilder.vertex(vertexConsumer, matrix, normalMatrix, max, max, max, 1, 0, r,g,b, alpha);
        VertexBuilder.vertex(vertexConsumer, matrix, normalMatrix, min, max, max, 0, 0, r,g,b, alpha);

        VertexBuilder.vertex(vertexConsumer, matrix, normalMatrix, max, min, min, 0, 1, r,g,b, alpha);
        VertexBuilder.vertex(vertexConsumer, matrix, normalMatrix, min, min, min, 1, 1, r,g,b, alpha);
        VertexBuilder.vertex(vertexConsumer, matrix, normalMatrix, min, max, min, 1, 0, r,g,b, alpha);
        VertexBuilder.vertex(vertexConsumer, matrix, normalMatrix, max, max, min, 0, 0, r,g,b, alpha);

        VertexBuilder.vertex(vertexConsumer, matrix, normalMatrix, max, min, max, 0, 1, r,g,b, alpha);
        VertexBuilder.vertex(vertexConsumer, matrix, normalMatrix, max, min, min, 1, 1, r,g,b, alpha);
        VertexBuilder.vertex(vertexConsumer, matrix, normalMatrix, max, max, min, 1, 0, r,g,b, alpha);
        VertexBuilder.vertex(vertexConsumer, matrix, normalMatrix, max, max, max, 0, 0, r,g,b, alpha);

        VertexBuilder.vertex(vertexConsumer, matrix, normalMatrix, min, min, min, 0, 1, r,g,b, alpha);
        VertexBuilder.vertex(vertexConsumer, matrix, normalMatrix, min, min, max, 1, 1, r,g,b, alpha);
        VertexBuilder.vertex(vertexConsumer, matrix, normalMatrix, min, max, max, 1, 0, r,g,b, alpha);
        VertexBuilder.vertex(vertexConsumer, matrix, normalMatrix, min, max, min, 0, 0, r,g,b, alpha);

        VertexBuilder.vertex(vertexConsumer, matrix, normalMatrix, min, max, max, 0, 1, r,g,b, alpha);
        VertexBuilder.vertex(vertexConsumer, matrix, normalMatrix, max, max, max, 1, 1, r,g,b, alpha);
        VertexBuilder.vertex(vertexConsumer, matrix, normalMatrix, max, max, min, 1, 0, r,g,b, alpha);
        VertexBuilder.vertex(vertexConsumer, matrix, normalMatrix, min, max, min, 0, 0, r,g,b, alpha);

        VertexBuilder.vertex(vertexConsumer, matrix, normalMatrix, min, min, min, 0, 1, r,g,b, alpha);
        VertexBuilder.vertex(vertexConsumer, matrix, normalMatrix, max, min, min, 1, 1, r,g,b, alpha);
        VertexBuilder.vertex(vertexConsumer, matrix, normalMatrix, max, min, max, 1, 0, r,g,b, alpha);
        VertexBuilder.vertex(vertexConsumer, matrix, normalMatrix, min, min, max, 0, 0, r,g,b, alpha);
    }
}

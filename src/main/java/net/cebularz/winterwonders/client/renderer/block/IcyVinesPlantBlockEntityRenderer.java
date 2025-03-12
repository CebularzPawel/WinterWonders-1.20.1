package net.cebularz.winterwonders.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.block.entity.IcyVinesPlantBlockEntity;
import net.cebularz.winterwonders.client.models.blocks.IcyVinesPlantModel;

public class IcyVinesPlantBlockEntityRenderer implements BlockEntityRenderer<IcyVinesPlantBlockEntity> {
    private final IcyVinesPlantModel model;
    private static final ResourceLocation BASE_TEXTURE = new ResourceLocation(WinterWonders.MOD_ID, "textures/block/icy_vines_plant.png");
    private static final ResourceLocation EMISSIVE_TEXTURE = new ResourceLocation(WinterWonders.MOD_ID, "textures/block/icy_vines_plant_emissive.png");

    public IcyVinesPlantBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new IcyVinesPlantModel(context.bakeLayer(IcyVinesPlantModel.ICY_VINES_PLANT_LAYER));
    }

    @Override
    public void render(IcyVinesPlantBlockEntity pBlock, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        pPoseStack.pushPose();
        pPoseStack.mulPose(Axis.XP.rotationDegrees(180));
        pPoseStack.mulPose(Axis.YP.rotationDegrees(180));
        VertexConsumer baseVertexConsumer = pBuffer.getBuffer(RenderType.entityCutout(BASE_TEXTURE));
        model.renderToBuffer(pPoseStack, baseVertexConsumer, pPackedLight, pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        VertexConsumer emissiveVertexConsumer = pBuffer.getBuffer(RenderType.eyes(EMISSIVE_TEXTURE));
        model.renderToBuffer(pPoseStack, emissiveVertexConsumer, 15728640, pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        pPoseStack.popPose();
    }
}


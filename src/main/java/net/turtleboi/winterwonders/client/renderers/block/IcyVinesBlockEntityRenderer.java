package net.turtleboi.winterwonders.client.renderers.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.turtleboi.winterwonders.WinterWonders;
import net.turtleboi.winterwonders.block.entity.IcyVinesBlockEntity;
import net.turtleboi.winterwonders.client.models.blocks.IcyVinesModel;

public class IcyVinesBlockEntityRenderer implements BlockEntityRenderer<IcyVinesBlockEntity> {
    private final IcyVinesModel model;
    private static final ResourceLocation BASE_TEXTURE = new ResourceLocation(WinterWonders.MOD_ID, "textures/block/icy_vines.png");
    private static final ResourceLocation EMISSIVE_TEXTURE = new ResourceLocation(WinterWonders.MOD_ID, "textures/block/icy_vines_emissive.png");

    public IcyVinesBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new IcyVinesModel(context.bakeLayer(IcyVinesModel.ICY_VINES_LAYER));
    }

    @Override
    public void render(IcyVinesBlockEntity pBlock, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        VertexConsumer emissiveVertexConsumer = pBuffer.getBuffer(RenderType.eyes(EMISSIVE_TEXTURE));
        pPoseStack.pushPose();
        model.renderToBuffer(pPoseStack, emissiveVertexConsumer, 15728640, pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        pPoseStack.popPose();
    }
}


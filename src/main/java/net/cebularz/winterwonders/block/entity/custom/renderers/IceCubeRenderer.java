package net.cebularz.winterwonders.block.entity.custom.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.cebularz.winterwonders.entity.custom.projectile.IceCubeEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

public class IceCubeRenderer extends EntityRenderer<IceCubeEntity> {

    private final BlockRenderDispatcher dispatcher;

    public IceCubeRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.dispatcher = pContext.getBlockRenderDispatcher();
    }

    @Override
    public void render(IceCubeEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {

        BlockState state = Blocks.ICE.defaultBlockState();;

        pPoseStack.pushPose();
        dispatcher.renderSingleBlock(state,pPoseStack,pBuffer,pPackedLight, OverlayTexture.NO_OVERLAY, ModelData.builder().build(), RenderType.translucent());
        pPoseStack.popPose();
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(IceCubeEntity iceCubeEntity) {
        return new ResourceLocation("minecraft:textures/block/ice.png");
    }
}

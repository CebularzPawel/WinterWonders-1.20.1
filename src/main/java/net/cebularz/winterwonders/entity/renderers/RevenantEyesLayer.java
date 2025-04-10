package net.cebularz.winterwonders.entity.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;
import net.cebularz.winterwonders.WinterWonders;

public class RevenantEyesLayer extends RenderLayer<Zombie, ZombieModel<Zombie>> {
    private static final ResourceLocation EYES_TEXTURE = new ResourceLocation(WinterWonders.MOD_ID, "textures/entity/revenant_eyes.png");

    public RevenantEyesLayer(RenderLayerParent<Zombie, ZombieModel<Zombie>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, Zombie zombie, float v, float v1, float v2, float v3, float v4, float v5) {
        VertexConsumer eyesConsumer = multiBufferSource.getBuffer(RenderType.eyes(EYES_TEXTURE));
        this.getParentModel().renderToBuffer(poseStack, eyesConsumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}

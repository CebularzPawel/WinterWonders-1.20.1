package net.cebularz.winterwonders.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.cebularz.winterwonders.client.data.FrozenStatusCache;
import net.cebularz.winterwonders.client.renderer.util.RepeatingVertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.turtleboi.aspects.client.data.FrozenStatusCache;
import net.turtleboi.aspects.client.renderer.util.RepeatingVertexConsumer;
import net.turtleboi.aspects.network.payloads.FrozenData;
import org.jetbrains.annotations.NotNull;

public class FrozenRenderer {
    private static final ResourceLocation FROZEN_TEXTURE = new ResourceLocation("minecraft", "textures/block/blue_ice.png");

    public static class FrozenLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T,M> {
        private final LivingEntityRenderer<T, M> frozenRenderer;

        public FrozenLayer(LivingEntityRenderer<T, M> frozenRenderer) {
            super(frozenRenderer);
            this.frozenRenderer = frozenRenderer;
        }

        @Override
        public void render(@NotNull PoseStack pPostStack, @NotNull MultiBufferSource pBuffer, int pPackedLight, LivingEntity pLivingEntity, float pLimbSwing,
                           float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
            if (FrozenStatusCache.isFrozen(pLivingEntity.getId())) {
                //FrozenData.sendFrozenSync(pLivingEntity);
                EntityModel<T> model = this.frozenRenderer.getModel();
                VertexConsumer originalConsumer = pBuffer.getBuffer(RenderType.entityTranslucent(FROZEN_TEXTURE));
                VertexConsumer repeatingConsumer = new RepeatingVertexConsumer(originalConsumer, 8, 8);
                model.renderToBuffer(pPostStack, repeatingConsumer, pPackedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 0.5f);
            }
        }
    }
}

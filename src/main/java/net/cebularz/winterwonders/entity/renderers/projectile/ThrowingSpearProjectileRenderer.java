package net.cebularz.winterwonders.entity.renderers.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.cebularz.winterwonders.entity.custom.projectile.ThrowingSpearProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public class ThrowingSpearProjectileRenderer extends EntityRenderer<ThrowingSpearProjectile> {
    private final ItemRenderer items;

    public static ResourceLocation model(Item item) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
        return new ResourceLocation(id.getNamespace(), "item/" + id.getPath() + "_projectile");
    }

    public ThrowingSpearProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
        items = context.getItemRenderer();
    }

    @Override
    public void render(ThrowingSpearProjectile spear, float yaw, float partialTick, PoseStack pose, MultiBufferSource buffer, int light) {
        if (!spear.getItem().isEmpty()) {
            pose.pushPose();
            pose.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, spear.yRotO, spear.getYRot()) - 90));
            pose.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, spear.xRotO, spear.getXRot()) - 135));
            pose.scale(1.7F, 1.7F, .85F);
            pose.translate(.425F, -.425F, 0F);
            items.render(spear.getItem(), ItemDisplayContext.NONE, false, pose, buffer, light, OverlayTexture.NO_OVERLAY,
                    items.getItemModelShaper().getModelManager().getModel(model(spear.getItem().getItem())));
            pose.popPose();
            super.render(spear, yaw, partialTick, pose, buffer, light);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(ThrowingSpearProjectile spear) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}

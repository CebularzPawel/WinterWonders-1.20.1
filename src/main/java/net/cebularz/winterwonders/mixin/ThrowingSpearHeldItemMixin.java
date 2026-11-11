package net.cebularz.winterwonders.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.cebularz.winterwonders.entity.animations.ThrowingSpearAnimations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandLayer.class)
public abstract class ThrowingSpearHeldItemMixin {
    @Inject(method = "renderArmWithItem", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"))
    private void throwingSpearItem(LivingEntity entity, ItemStack stack, ItemDisplayContext context, HumanoidArm arm,
                           PoseStack pose, MultiBufferSource buffer, int light, CallbackInfo callback) {
        ThrowingSpearAnimations.held(pose, entity, stack, arm, Minecraft.getInstance().getFrameTime());
    }
}

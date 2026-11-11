package net.cebularz.winterwonders.mixin;

import net.cebularz.winterwonders.entity.animations.ThrowingSpearAnimations;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class ThrowingSpearPoseMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
    private void throwingSpearPose(LivingEntity entity, float stride, float amount, float age, float yaw, float pitch, CallbackInfo callback) {
        HumanoidModel<?> model = (HumanoidModel<?>) (Object) this;
        float partial = age - entity.tickCount;
        ThrowingSpearAnimations.arm(model, entity, HumanoidArm.RIGHT, partial);
        ThrowingSpearAnimations.arm(model, entity, HumanoidArm.LEFT, partial);
    }
}

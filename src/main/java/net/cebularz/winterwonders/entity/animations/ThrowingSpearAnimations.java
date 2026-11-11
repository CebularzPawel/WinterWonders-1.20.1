package net.cebularz.winterwonders.entity.animations;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.cebularz.winterwonders.item.custom.ThrowingSpearItem;
import net.cebularz.winterwonders.entity.custom.PinginEntity;

import static net.cebularz.winterwonders.entity.animations.AnimationUtil.*;

public final class ThrowingSpearAnimations {
    private ThrowingSpearAnimations() {}

    public static float windup(float time) {
        return inOutSine(progress(time, 0, .05F)) - inOutExpo(progress(time, .4F, 1));
    }

    public static float thrust(float time) {
        return inQuad(progress(time, .05F, .2F)) - inOutExpo(progress(time, .4F, 1));
    }

    public static void jab(PoseStack pose, float time, int side) {
        float prepare = inOutSine(progress(time, 0, .05F));
        float thrust = outBack(progress(time, .05F, .2F));
        float recover = inOutExpo(progress(time, .4F, 1));
        pose.translate(side * .1F * (prepare - thrust), -.075F * (prepare - recover), .65F * (prepare - thrust));
        pose.mulPose(Axis.XP.rotationDegrees(-70 * (prepare - recover)));
        pose.translate(0, 0, -.25F * (recover - thrust));
    }

    public static void arm(HumanoidModel<?> model, LivingEntity entity, HumanoidArm side, float partial) {
        InteractionHand hand = side == entity.getMainArm() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        if (entity.getItemInHand(hand).getItem() instanceof ThrowingSpearItem) {
            ModelPart arm = side == HumanoidArm.RIGHT ? model.rightArm : model.leftArm;
            if (entity.isUsingItem() && entity.getUsedItemHand() == hand) {
                arm.xRot = -Mth.PI + model.head.xRot;
                arm.yRot = 0;
            } else {
                arm.yRot = Mth.clamp((side == HumanoidArm.RIGHT ? -.1F : .1F) + model.head.yRot, -60 * Mth.DEG_TO_RAD, 60 * Mth.DEG_TO_RAD);
                arm.xRot = -Mth.HALF_PI + model.head.xRot + .8F;
                if (entity.isFallFlying() || entity.getSwimAmount(partial) > 0) {
                    arm.xRot -= .9599311F;
                }
                arm.xRot = Mth.clamp(arm.xRot, -120 * Mth.DEG_TO_RAD, 30 * Mth.DEG_TO_RAD);
                if (entity.swinging && entity.swingingArm == hand) {
                    float time = entity.getAttackAnim(partial);
                    arm.xRot += Mth.DEG_TO_RAD * (90 * windup(time) - 120 * thrust(time));
                }
            }
        }
    }

    public static void held(PoseStack pose, LivingEntity entity, ItemStack stack, HumanoidArm arm, float partial) {
        if (stack.getItem() instanceof ThrowingSpearItem && !entity.isUsingItem() && !(entity instanceof PinginEntity)) {
            InteractionHand hand = arm == entity.getMainArm() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            float time = entity.swingingArm == hand ? entity.getAttackAnim(partial) : 0;
            if (time > 0) {
                float thrust = thrust(time);
                pose.translate(0, -.125, .125);
                pose.mulPose(Axis.XN.rotationDegrees(70 * thrust));
                pose.translate(0, .125, -.125);
            }
        }
    }
}

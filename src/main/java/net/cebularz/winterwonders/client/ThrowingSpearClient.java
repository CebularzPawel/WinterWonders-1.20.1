package net.cebularz.winterwonders.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.cebularz.winterwonders.entity.animations.ThrowingSpearAnimations;
import net.cebularz.winterwonders.item.custom.ThrowingSpearItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
public final class ThrowingSpearClient implements IClientItemExtensions {
    private static final HumanoidModel.ArmPose POSE = HumanoidModel.ArmPose.create("winterwonders_throwing_spear", false,
            (model, entity, arm) -> ThrowingSpearAnimations.arm(model, entity, arm, 0));

    @Override
    public HumanoidModel.ArmPose getArmPose(LivingEntity entity, InteractionHand hand, ItemStack stack) {
        return entity.isUsingItem() && entity.getUsedItemHand() == hand ? HumanoidModel.ArmPose.THROW_SPEAR : POSE;
    }

    @Override
    public boolean applyForgeHandTransform(PoseStack pose, LocalPlayer player, HumanoidArm arm,
                                           ItemStack stack, float partial, float equip, float swing) {
        int side = arm == HumanoidArm.RIGHT ? 1 : -1;
        pose.translate(side * .56F, -.52F - equip * .6F, -.72F);
        InteractionHand hand = arm == player.getMainArm() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        if (player.isUsingItem() && player.getUsedItemHand() == hand) {
            float held = stack.getUseDuration() - (player.getUseItemRemainingTicks() - partial + 1);
            float charge = Mth.clamp(held / ThrowingSpearItem.FULL_CHARGE_TICKS, 0, 1);
            pose.translate(side * -.15F, .55F, .55F);
            pose.mulPose(Axis.XP.rotationDegrees(-110));
            pose.mulPose(Axis.YP.rotationDegrees(side * 25));
            pose.mulPose(Axis.ZP.rotationDegrees(side * 5));
            if (charge > .1F) {
                pose.translate(0, Mth.sin((held - .1F) * 1.3F) * (charge - .1F) * .004F, 0);
            }
            pose.translate(0, 0, charge * .15F);
            pose.mulPose(Axis.YN.rotationDegrees(side * charge * 30));
        } else {
            ThrowingSpearAnimations.jab(pose, swing, side);
        }
        return true;
    }
}

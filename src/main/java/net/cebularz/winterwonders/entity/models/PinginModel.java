package net.cebularz.winterwonders.entity.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.cebularz.winterwonders.entity.animations.PinginAnimations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.entity.custom.PinginEntity;
import net.minecraft.world.entity.HumanoidArm;

public class PinginModel<T extends Entity> extends HierarchicalModel<T> implements ArmedModel {
    public static final ModelLayerLocation PINGIN_LAYER = new ModelLayerLocation(new ResourceLocation(WinterWonders.MOD_ID, "pingin"), "main");
    private final ModelPart pingin;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart left_arm;
    private final ModelPart right_arm;
    private final ModelPart tail;
    private final ModelPart left_foot;
    private final ModelPart right_foot;


    public PinginModel(ModelPart root) {
        this.pingin = root.getChild("pingin");
        this.head = this.pingin.getChild("head");
        this.body = this.pingin.getChild("body");
        this.left_arm = this.body.getChild("left_arm");
        this.right_arm = this.body.getChild("right_arm");
        this.tail = this.body.getChild("tail");
        this.left_foot = this.pingin.getChild("left_foot");
        this.right_foot = this.pingin.getChild("right_foot");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition pingin = partdefinition.addOrReplaceChild("pingin", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition head = pingin.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 14).addBox(-4.0F, -6.0F, -3.0F, 8.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(8, 26).addBox(-2.0F, -2.5F, -4.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.5F, 0.0F));

        PartDefinition body = pingin.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -3.0F, 8.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.5F, 0.0F));

        PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(22, 20).addBox(0.0F, 0.0F, -3.0F, 1.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, -4.0F, 0.0F));

        PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 26).addBox(-1.0F, 0.0F, -3.0F, 1.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, -4.0F, 0.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offsetAndRotation(0.5F, 2.5F, 3.0F, 0.6545F, 0.0F, 0.0F));
        PartDefinition tail_r1 = tail.addOrReplaceChild("tail_r1", CubeListBuilder.create().texOffs(20, 6).addBox(-3.0F, 0.0F, -2.0F, 5.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.5F, 1.75F, 0.2618F, 0.0F, 0.0F));

        PartDefinition left_foot = pingin.addOrReplaceChild("left_foot", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -1.5F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(18, 14).addBox(-1.5F, 0.5F, -2.5F, 3.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(2.5F, -0.5F, 0.5F));

        PartDefinition right_foot = pingin.addOrReplaceChild("right_foot", CubeListBuilder.create().texOffs(18, 14).addBox(-1.5F, 0.5F, -2.5F, 3.0F, 0.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 3).addBox(-0.5F, -1.5F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.5F, -0.5F, 0.5F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        PinginEntity pingin = (PinginEntity) entity;

        float finalLimbSwing = limbSwing;
        float finalLimbSwingAmount = limbSwingAmount;

        if (pingin.isBaby()) {
            finalLimbSwing = limbSwing * 0.66f;
            finalLimbSwingAmount = limbSwingAmount * 0.66f;
        }

        if (pingin.isSliding()) {
            this.animate(pingin.slideAnimationState, PinginAnimations.SLIDE, ageInTicks, 1f);
        } else {
            this.applyHeadRotation(netHeadYaw, headPitch);
            this.animateWalk(PinginAnimations.WALKING, finalLimbSwing, finalLimbSwingAmount, 2f, 2.4f);
            this.animate(pingin.idleAnimationState, PinginAnimations.IDLE, ageInTicks, 1f);
            admireItem(pingin);
        }
    }

    private void applyHeadRotation(float pNetHeadYaw, float pHeadPitch) {
        pNetHeadYaw = Mth.clamp(pNetHeadYaw, -30.0F, 30.0F);
        pHeadPitch = Mth.clamp(pHeadPitch, -25.0F, 45.0F);

        this.head.yRot = pNetHeadYaw * ((float)Math.PI / 180F);
        this.head.xRot = pHeadPitch * ((float)Math.PI / 180F);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        pingin.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart root() {
        return pingin;
    }

    @Override
    public void translateToHand(HumanoidArm humanoidArm, PoseStack poseStack) {
        this.pingin.translateAndRotate(poseStack);
        this.body.translateAndRotate(poseStack);

        ModelPart armPart = humanoidArm == HumanoidArm.LEFT ? this.left_arm : this.right_arm;
        armPart.translateAndRotate(poseStack);

        float scale = 1.00f;
        float scaleDivider = 16.0f;
        poseStack.translate((1f * scale) / scaleDivider, (-2f * scale) / scaleDivider, 0.0f);
        poseStack.scale(scale, scale, scale);
    }

    private static float smoothStep(float ticks) {
        ticks = Mth.clamp(ticks, 0f, 1f);
        return ticks * ticks * ticks * (ticks * (6f * ticks - 15f) + 10f);
    }

    private void admireItem(PinginEntity pingin) {
        float partial = Minecraft.getInstance().getFrameTime();
        float raw = Mth.lerp(partial, pingin.lastAdmireProgress, pingin.admireProgress);
        float ease = smoothStep(raw);

        float nHeadX = this.head.xRot;
        float nHeadY = this.head.yRot;

        float nRArmX = this.right_arm.xRot;
        float nRArmY = this.right_arm.yRot;
        float nRArmZ = this.right_arm.zRot;
        float nRArmYPos = this.right_arm.y;

        float nLArmX = this.left_arm.xRot;
        float nLArmY = this.left_arm.yRot;
        float nLArmZ = this.left_arm.zRot;
        float nLArmYPos = this.left_arm.y;

        boolean leftHanded = pingin.isLeftHanded();

        float tHeadX = 0.25F;
        float tHeadY = leftHanded ? -0.5F : 0.5F;
        float tRArmX = nRArmX, tRArmY = nRArmY, tRArmZ = nRArmZ, tRArmYPos = nRArmYPos;
        float tLArmX = nLArmX, tLArmY = nLArmY, tLArmZ = nLArmZ, tLArmYPos = nLArmYPos;

        if (!leftHanded) {
            tRArmY = 0.5F;
            tRArmX = -1.9F;
            tRArmYPos = -0.5F;
            tLArmZ = -0.5F;
        } else {
            tLArmY = -0.5F;
            tLArmX = -1.9F;
            tLArmYPos = -0.5F;
            tRArmZ = 0.5F;
        }

        this.head.xRot = Mth.lerp(ease, nHeadX, tHeadX);
        this.head.yRot = Mth.lerp(ease, nHeadY, tHeadY);

        this.right_arm.xRot = Mth.lerp(ease, nRArmX, tRArmX);
        this.right_arm.yRot = Mth.lerp(ease, nRArmY, tRArmY);
        this.right_arm.zRot = Mth.lerp(ease, nRArmZ, tRArmZ);
        this.right_arm.y = Mth.lerp(ease, nRArmYPos, tRArmYPos);

        this.left_arm.xRot = Mth.lerp(ease, nLArmX, tLArmX);
        this.left_arm.yRot = Mth.lerp(ease, nLArmY, tLArmY);
        this.left_arm.zRot = Mth.lerp(ease, nLArmZ, tLArmZ);
        this.left_arm.y = Mth.lerp(ease, nLArmYPos, tLArmYPos);
    }

}

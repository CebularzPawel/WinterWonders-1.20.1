package net.cebularz.winterwonders.entity.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.entity.animations.AnimationUtil.BonePose;
import net.cebularz.winterwonders.entity.animations.LichAnimations;
import net.cebularz.winterwonders.entity.custom.LichEntity.CastState;
import net.cebularz.winterwonders.entity.custom.LichEntity;

import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.phys.Vec3;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public class LichModel<T extends LichEntity> extends HierarchicalModel<T> implements ArmedModel {
    public static final ModelLayerLocation LICH_LAYER = new ModelLayerLocation(new ResourceLocation(WinterWonders.MOD_ID, "lich"), "main");
    private final ModelPart lich;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart left_arm;
    private final ModelPart right_arm;
    private final Vector3f animationScratch = new Vector3f();
    private float staffRotation;
    private Vector3f floatingStaffPosition;
    private Quaternionf floatingStaffOrientation;

    public LichModel(ModelPart root) {
        this.lich = root.getChild("lich");
        this.body = this.lich.getChild("body");
        this.head = this.body.getChild("head");
        this.left_arm = this.body.getChild("left_arm");
        this.right_arm = this.body.getChild("right_arm");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition lich = partdefinition.addOrReplaceChild("lich", CubeListBuilder.create(), PartPose.ZERO);

        PartDefinition body = lich.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);

        PartDefinition head1 = head.addOrReplaceChild("head1", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition head2 = head.addOrReplaceChild("head2", CubeListBuilder.create().texOffs(0, 32).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition body1 = body.addOrReplaceChild("body1", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.ZERO);

        PartDefinition body2 = body.addOrReplaceChild("body2", CubeListBuilder.create().texOffs(16, 48).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.ZERO);

        PartDefinition body3 = body.addOrReplaceChild("body3", CubeListBuilder.create().texOffs(40, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.ZERO);

        PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(5.0F, 2.0F, 0.0F));

        PartDefinition left_arm1 = left_arm.addOrReplaceChild("left_arm1", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.ZERO);

        PartDefinition left_arm2 = left_arm.addOrReplaceChild("left_arm2", CubeListBuilder.create().texOffs(40, 48).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.15F)).mirror(false), PartPose.ZERO);

        PartDefinition left_arm3 = left_arm.addOrReplaceChild("left_arm3", CubeListBuilder.create().texOffs(48, 16).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.4F)).mirror(false)
                .texOffs(54, 32).mirror().addBox(0.0F, -5.25F, 0.0F, 5.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.ZERO);

        PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-5.0F, 2.0F, 0.0F));

        PartDefinition right_arm1 = right_arm.addOrReplaceChild("right_arm1", CubeListBuilder.create().texOffs(40, 16).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.ZERO);

        PartDefinition right_arm2 = right_arm.addOrReplaceChild("right_arm2", CubeListBuilder.create().texOffs(40, 48).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.15F)), PartPose.ZERO);

        PartDefinition right_arm3 = right_arm.addOrReplaceChild("right_arm3", CubeListBuilder.create().texOffs(48, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.4F))
                .texOffs(54, 32).addBox(-5.0F, -5.25F, 0.0F, 5.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.ZERO);

        PartDefinition left_leg = body.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(2.0F, 12.0F, 0.1F));

        PartDefinition left_leg1 = left_leg.addOrReplaceChild("left_leg1", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-1.0F, 0.0F, -1.1F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition left_leg2 = left_leg.addOrReplaceChild("left_leg2", CubeListBuilder.create().texOffs(0, 48).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)).mirror(false), PartPose.offset(-0.1F, 0.0F, 0.0F));

        PartDefinition right_leg = body.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-2.0F, 12.0F, 0.1F));

        PartDefinition right_leg1 = right_leg.addOrReplaceChild("right_leg1", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, 0.0F, -1.1F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition right_leg2 = right_leg.addOrReplaceChild("right_leg2", CubeListBuilder.create().texOffs(0, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(0.1F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float partialTick = Mth.clamp(ageInTicks - entity.tickCount, 0F, 1F);
        staffRotation = 0F;
        floatingStaffPosition = null;
        floatingStaffOrientation = null;
        if (entity.getCastState().kind() != CastState.Kind.NONE && entity.isCastAnimating()) {
            this.lich.getAllParts().forEach(ModelPart::resetPose);
            LichAnimations.Pose pose = LichAnimations.sample(entity.getCastState(), entity.getCastSeconds(partialTick), ageInTicks * .05F);
            applyPose(lich, pose.root);
            applyPose(body, pose.body);
            applyPose(head, pose.head);
            applyPose(right_arm, pose.rightArm);
            applyPose(left_arm, pose.leftArm);
            applyPose(body.getChild("left_leg"), pose.leftLeg);
            applyPose(body.getChild("right_leg"), pose.rightLeg);
            staffRotation = pose.staffPitch;
            floatingStaffPosition = pose.staffPosition;
            floatingStaffOrientation = pose.staffOrientation;
        } else {
            idlePose(ageInTicks, limbSwingAmount, netHeadYaw, headPitch);
        }
        updateFootParticleOrigins(entity);
    }

    private static void applyPose(ModelPart part, BonePose pose) {
        part.setPos(pose.x, pose.y, pose.z);
        part.setRotation(pose.xRot, pose.yRot, pose.zRot);
    }

    private void updateFootParticleOrigins(T entity) {
        PoseStack poseStack = new PoseStack();
        lich.translateAndRotate(poseStack);
        body.translateAndRotate(poseStack);
        Vec3[] footOffsets = new Vec3[2];
        String[] legNames = {"left_leg", "right_leg"};
        for (int leg = 0; leg < 2; leg++) {
            poseStack.pushPose();
            body.getChild(legNames[leg]).translateAndRotate(poseStack);
            Vector3f sole = poseStack.last().pose().transformPosition(new Vector3f((leg == 0 ? -.1F : .1F) / 16F, 12.25F / 16F, 0));
            footOffsets[leg] = new Vec3(sole.x, sole.y, sole.z);
            poseStack.popPose();
        }
        entity.updateClientFootOffsets(footOffsets[0], footOffsets[1]);
    }

    private void idlePose(float ageInTicks, float limbSwingAmount, float headYaw, float headPitch) {
        this.lich.getAllParts().forEach(ModelPart::resetPose);
        long timeMillis = (long) (ageInTicks * 50F);
        float glideWeight = Mth.clamp(limbSwingAmount * 3F, 0F, 1F);
        KeyframeAnimations.animate(this, LichAnimations.Locomotion.IDLE, timeMillis, 1F, animationScratch);
        KeyframeAnimations.animate(this, LichAnimations.Locomotion.GLIDING, timeMillis, glideWeight, animationScratch);
        this.head.yRot += Mth.clamp(headYaw, -50F, 50F) * Mth.DEG_TO_RAD;
        this.head.xRot += Mth.clamp(headPitch, -35F, 35F) * Mth.DEG_TO_RAD;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        lich.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart root() {
        return this.lich;
    }

    @Override
    public void translateToHand(HumanoidArm humanoidArm, PoseStack poseStack) {
        if (humanoidArm == HumanoidArm.RIGHT && floatingStaffPosition != null) {
            poseStack.translate(floatingStaffPosition.x, floatingStaffPosition.y, floatingStaffPosition.z);
            poseStack.mulPose(floatingStaffOrientation);
            poseStack.translate(1F / 16F, -8F / 16F, 0F);
            return;
        }
        this.lich.translateAndRotate(poseStack);
        this.body.translateAndRotate(poseStack);
        ModelPart arm = humanoidArm == HumanoidArm.LEFT ? this.left_arm : this.right_arm;
        arm.translateAndRotate(poseStack);
        poseStack.translate((humanoidArm == HumanoidArm.RIGHT ? 1F : -1F) / 16F, .5F / 16F, 0F);
        if (humanoidArm == HumanoidArm.RIGHT && staffRotation != 0F) {
            poseStack.translate(-1F / 16F, 8F / 16F, 0F);
            poseStack.mulPose(com.mojang.math.Axis.XP.rotation(staffRotation));
            poseStack.translate(1F / 16F, -8F / 16F, 0F);
        }
    }
}

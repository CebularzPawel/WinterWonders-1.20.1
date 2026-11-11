package net.cebularz.winterwonders.entity.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.entity.animations.AnimationUtil;
import net.cebularz.winterwonders.entity.animations.LichAnimations;
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
import org.joml.Vector3f;

public class LichModel<T extends LichEntity> extends HierarchicalModel<T> implements ArmedModel {
    public static final ModelLayerLocation LICH_LAYER = new ModelLayerLocation(new ResourceLocation(WinterWonders.MOD_ID, "lich"), "main");
    private final ModelPart lich;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart left_arm;
    private final ModelPart right_arm;
    private final Vector3f animationScratch = new Vector3f();

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
        float partial = Mth.clamp(ageInTicks - entity.tickCount, 0F, 1F);
        float casting = Mth.lerp(partial, entity.lastCastProgress, entity.castProgress);
        pose(ageInTicks, limbSwingAmount, netHeadYaw, headPitch, casting, entity.getMainArm());
    }

    private void pose(float ageInTicks, float movement, float yaw, float pitch, float casting, HumanoidArm mainArm) {
        this.lich.getAllParts().forEach(ModelPart::resetPose);
        long time = (long) (ageInTicks * 50F);
        float castWeight = AnimationUtil.smootherStep(casting);
        float glideWeight = Mth.clamp(movement * 3F, 0F, 1F) * (1F - castWeight * .75F);
        boolean left = mainArm == HumanoidArm.LEFT;
        KeyframeAnimations.animate(this, left ? LichAnimations.LEFT_IDLE : LichAnimations.IDLE, time, 1F, animationScratch);
        KeyframeAnimations.animate(this, left ? LichAnimations.LEFT_GLIDING : LichAnimations.GLIDING, time, glideWeight, animationScratch);
        KeyframeAnimations.animate(this, left ? LichAnimations.LEFT_CASTING : LichAnimations.CASTING, time, castWeight, animationScratch);
        this.head.yRot += Mth.clamp(yaw, -50F, 50F) * Mth.DEG_TO_RAD;
        this.head.xRot += Mth.clamp(pitch, -35F, 35F) * Mth.DEG_TO_RAD;
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
        this.lich.translateAndRotate(poseStack);
        this.body.translateAndRotate(poseStack);
        ModelPart arm = humanoidArm == HumanoidArm.LEFT ? this.left_arm : this.right_arm;
        arm.translateAndRotate(poseStack);
        poseStack.translate((humanoidArm == HumanoidArm.RIGHT ? 1F : -1F) / 16F, .5F / 16F, 0F);
    }
}

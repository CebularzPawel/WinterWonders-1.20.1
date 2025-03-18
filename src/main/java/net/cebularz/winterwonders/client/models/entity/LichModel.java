package net.cebularz.winterwonders.client.models.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.entity.custom.LichEntity;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class LichModel<T extends LichEntity> extends HierarchicalModel<T> implements ArmedModel {
    public static final ModelLayerLocation LICH_LAYER = new ModelLayerLocation(new ResourceLocation(WinterWonders.MOD_ID, "lich"), "main");
    private final ModelPart lich;
    private final ModelPart head;
    private final ModelPart head1;
    private final ModelPart head2;
    private final ModelPart body;
    private final ModelPart body1;
    private final ModelPart body2;
    private final ModelPart body3;
    private final ModelPart left_arm;
    private final ModelPart left_arm1;
    private final ModelPart left_arm2;
    private final ModelPart left_arm3;
    private final ModelPart right_arm;
    private final ModelPart right_arm1;
    private final ModelPart right_arm2;
    private final ModelPart right_arm3;
    private final ModelPart left_leg;
    private final ModelPart left_leg1;
    private final ModelPart left_leg2;
    private final ModelPart right_leg;
    private final ModelPart right_leg1;
    private final ModelPart right_leg2;

    public LichModel(ModelPart root) {
        this.lich = root.getChild("lich");
        this.head = this.lich.getChild("head");
        this.head1 = this.head.getChild("head1");
        this.head2 = this.head.getChild("head2");
        this.body = this.lich.getChild("body");
        this.body1 = this.body.getChild("body1");
        this.body2 = this.body.getChild("body2");
        this.body3 = this.body.getChild("body3");
        this.left_arm = this.lich.getChild("left_arm");
        this.left_arm1 = this.left_arm.getChild("left_arm1");
        this.left_arm2 = this.left_arm.getChild("left_arm2");
        this.left_arm3 = this.left_arm.getChild("left_arm3");
        this.right_arm = this.lich.getChild("right_arm");
        this.right_arm1 = this.right_arm.getChild("right_arm1");
        this.right_arm2 = this.right_arm.getChild("right_arm2");
        this.right_arm3 = this.right_arm.getChild("right_arm3");
        this.left_leg = this.lich.getChild("left_leg");
        this.left_leg1 = this.left_leg.getChild("left_leg1");
        this.left_leg2 = this.left_leg.getChild("left_leg2");
        this.right_leg = this.lich.getChild("right_leg");
        this.right_leg1 = this.right_leg.getChild("right_leg1");
        this.right_leg2 = this.right_leg.getChild("right_leg2");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition lich = partdefinition.addOrReplaceChild("lich", CubeListBuilder.create(), PartPose.offset(4.0F, -0.1F, 0.0F));

        PartDefinition head = lich.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(-4.0F, 0.1F, 0.0F));

        PartDefinition head1 = head.addOrReplaceChild("head1", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition head2 = head.addOrReplaceChild("head2", CubeListBuilder.create().texOffs(0, 32).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition body = lich.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(-6.0F, 12.1F, 0.1F));

        PartDefinition body1 = body.addOrReplaceChild("body1", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -12.0F, -0.1F));

        PartDefinition body2 = body.addOrReplaceChild("body2", CubeListBuilder.create().texOffs(16, 48).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(2.0F, -12.0F, -0.1F));

        PartDefinition body3 = body.addOrReplaceChild("body3", CubeListBuilder.create().texOffs(40, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(2.0F, -12.0F, -0.1F));

        PartDefinition left_arm = lich.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition left_arm1 = left_arm.addOrReplaceChild("left_arm1", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(1.0F, 2.1F, 0.0F));

        PartDefinition left_arm2 = left_arm.addOrReplaceChild("left_arm2", CubeListBuilder.create().texOffs(40, 48).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.15F)).mirror(false), PartPose.offset(1.0F, 2.1F, 0.0F));

        PartDefinition left_arm3 = left_arm.addOrReplaceChild("left_arm3", CubeListBuilder.create().texOffs(48, 16).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.4F)).mirror(false)
                .texOffs(54, 32).mirror().addBox(0.0F, -5.25F, 0.0F, 5.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(1.0F, 2.1F, 0.0F));

        PartDefinition right_arm = lich.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-8.0F, 0.1F, 0.1F));

        PartDefinition right_arm1 = right_arm.addOrReplaceChild("right_arm1", CubeListBuilder.create().texOffs(40, 16).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 2.0F, -0.1F));

        PartDefinition right_arm2 = right_arm.addOrReplaceChild("right_arm2", CubeListBuilder.create().texOffs(40, 48).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.15F)), PartPose.offset(-1.0F, 2.0F, -0.1F));

        PartDefinition right_arm3 = right_arm.addOrReplaceChild("right_arm3", CubeListBuilder.create().texOffs(48, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.4F))
                .texOffs(54, 32).addBox(-5.0F, -5.25F, 0.0F, 5.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 2.0F, -0.1F));

        PartDefinition left_leg = lich.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(-2.0F, 12.1F, 0.1F));

        PartDefinition left_leg1 = left_leg.addOrReplaceChild("left_leg1", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-1.0F, 0.0F, -1.1F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition left_leg2 = left_leg.addOrReplaceChild("left_leg2", CubeListBuilder.create().texOffs(0, 48).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)).mirror(false), PartPose.offset(-0.1F, 0.0F, 0.0F));

        PartDefinition right_leg = lich.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-6.0F, 12.1F, 0.1F));

        PartDefinition right_leg1 = right_leg.addOrReplaceChild("right_leg1", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, 0.0F, -1.1F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition right_leg2 = right_leg.addOrReplaceChild("right_leg2", CubeListBuilder.create().texOffs(0, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(0.1F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
        this.head.xRot = headPitch * ((float)Math.PI / 180F);

        float swing = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        float oppositeSwing = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;

        this.left_leg.xRot = swing;
        this.right_leg.xRot = oppositeSwing;

        this.left_arm.xRot = oppositeSwing * 0.5F;
        this.right_arm.xRot = swing * 0.5F;

        this.left_arm1.xRot = oppositeSwing * 0.5F;
        this.right_arm1.xRot = swing * 0.5F;

        this.right_arm2.xRot = 0.0f;
        this.left_arm2.xRot = 0.0f;

        this.right_arm3.xRot = 0.0f;
        this.left_arm3.xRot = 0.0f;

        this.left_arm.zRot = 0.0f;
        this.right_arm.zRot = 0.0f;

        this.left_arm1.zRot = 0.0f;
        this.right_arm1.zRot = 0.0f;

        this.right_arm2.zRot = 0.0f;
        this.left_arm2.zRot = 0.0f;

        this.right_arm3.zRot = 0.0f;
        this.left_arm3.zRot = 0.0f;

        this.body.yRot = 0.0F;
        this.body.xRot = Mth.cos(limbSwing * 0.6662F) * 0.05F * limbSwingAmount;

        if (entity.isCastingSpell()) {
            float baseXRot = -1.5f;
            float baseZRot = 0.4f;
            float amplitude = 0.35f;
            float castSpeed = 0.2f;
            float angle = ageInTicks * castSpeed;
            float offsetX = Mth.cos(angle) * amplitude;
            float offsetZ = Mth.sin(angle) * amplitude;

            this.right_arm.xRot = baseXRot + offsetX;
            this.right_arm.zRot = -baseZRot + offsetZ;
            this.left_arm.xRot = baseXRot - offsetX;
            this.left_arm.zRot = baseZRot - offsetZ;

            this.right_arm1.xRot = baseXRot + offsetX;
            this.right_arm1.zRot = -baseZRot + offsetZ;
            this.left_arm1.xRot = baseXRot - offsetX;
            this.left_arm1.zRot = baseZRot - offsetZ;

            this.right_arm2.xRot = baseXRot + offsetX;
            this.right_arm2.zRot = -baseZRot + offsetZ;
            this.left_arm2.xRot = baseXRot - offsetX;
            this.left_arm2.zRot = baseZRot -offsetZ;

            this.right_arm3.xRot = baseXRot + offsetX;
            this.right_arm3.zRot = -baseZRot + offsetZ;
            this.left_arm3.xRot = baseXRot - offsetX;
            this.left_arm3.zRot = baseZRot - offsetZ;
        }
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
        ModelPart armPart = humanoidArm == HumanoidArm.LEFT ? this.left_arm1 : this.right_arm1;
        armPart.translateAndRotate(poseStack);

        float xOffset = humanoidArm == HumanoidArm.LEFT ? 0.2F : -0.2F;
        poseStack.translate(xOffset, 0, 0);
    }
}

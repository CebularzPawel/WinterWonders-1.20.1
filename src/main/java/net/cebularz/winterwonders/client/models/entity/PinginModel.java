package net.cebularz.winterwonders.client.models.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
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
import net.cebularz.winterwonders.entity.animations.ModAnimationDefintions;
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

        if (pingin.isSliding()) {
            this.animate(pingin.slideAnimationState, ModAnimationDefintions.PINGIN_SLIDE, ageInTicks, 1f);
        } else if (pingin.isAdmiring()) {
            this.head.xRot = 0.25F;
            if (pingin.isLeftHanded()) {
                this.head.yRot = 0.5F;
                this.right_arm.yRot = 0.5F;
                this.right_arm.xRot = -0.9F;
                this.left_arm.zRot = -0.15F;
            } else {
                this.head.yRot = -0.5F;
                this.left_arm.yRot = -0.5F;
                this.left_arm.xRot = -0.9F;
                this.right_arm.zRot = 0.15F;
            }
        } else {
            this.applyHeadRotation(netHeadYaw, headPitch);
            if(!pingin.isAdmiring()) {
                this.animateWalk(ModAnimationDefintions.PINGIN_WALK, limbSwing, limbSwingAmount, 2f, 2.4f);
            }
            this.animate(pingin.idleAnimationState, ModAnimationDefintions.PINGIN_IDLE, ageInTicks, 1f);
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
        ModelPart armPart = humanoidArm == HumanoidArm.LEFT ? this.right_arm : this.left_arm;
        armPart.translateAndRotate(poseStack);
        poseStack.scale(0.65f, 0.65f, 0.65f);
        poseStack.translate(0.55, 2, 1.5);
        poseStack.mulPose(Axis.ZP.rotationDegrees(90));
    }
}

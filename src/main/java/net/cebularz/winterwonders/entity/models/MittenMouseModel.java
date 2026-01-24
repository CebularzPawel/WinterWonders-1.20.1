package net.cebularz.winterwonders.entity.models;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.entity.animations.MittenMouseAnimations;
import net.cebularz.winterwonders.entity.animations.PinginAnimations;
import net.cebularz.winterwonders.entity.custom.MittenMouseEntity;
import net.cebularz.winterwonders.entity.custom.PinginEntity;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;

public class MittenMouseModel<T extends Entity> extends HierarchicalModel<T> implements ArmedModel {
    public static final ModelLayerLocation MOUSE_LAYER = new ModelLayerLocation(new ResourceLocation(WinterWonders.MOD_ID, "mitten_mouse"), "main");
    private final ModelPart mitten_mouse;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart right_arm;
    private final ModelPart left_arm;
    private final ModelPart right_hind_leg;
    private final ModelPart left_hind_leg;
    private final ModelPart tail;

    public MittenMouseModel(ModelPart root) {
        this.mitten_mouse = root.getChild("mitten_mouse");
        this.head = this.mitten_mouse.getChild("head");
        this.body = this.mitten_mouse.getChild("body");
        this.right_arm = this.body.getChild("right_arm");
        this.left_arm = this.body.getChild("left_arm");
        this.right_hind_leg = this.body.getChild("right_hind_leg");
        this.left_hind_leg = this.body.getChild("left_hind_leg");
        this.tail = this.body.getChild("tail");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition mitten_mouse = partdefinition.addOrReplaceChild("mitten_mouse", CubeListBuilder.create(), PartPose.offset(-0.5F, 22.5F, 0.25F));

        PartDefinition head = mitten_mouse.addOrReplaceChild("head", CubeListBuilder.create().texOffs(12, 1).addBox(-4.5F, -4.0F, -0.5F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(12, 1).mirror().addBox(0.5F, -4.0F, -0.5F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(0, 0).addBox(-1.5F, -1.0F, -3.0F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.5F, -1.25F));

        PartDefinition body = mitten_mouse.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 5).addBox(-1.5F, -1.5F, -2.0F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.75F));

        PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 12).addBox(-1.0F, -0.5F, -2.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, 1.0F, -1.0F));

        PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 12).addBox(0.0F, -0.5F, -2.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, 1.0F, -1.0F));

        PartDefinition right_hind_leg = body.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(14, 8).addBox(-1.0F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(7, 13).addBox(-1.0F, 0.0F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, 0.5F, 1.0F));

        PartDefinition left_hind_leg = body.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(14, 8).addBox(0.0F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(7, 13).addBox(0.0F, 0.0F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, 0.5F, 1.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(20, -3).addBox(0.0F, -3.5F, 0.0F, 0.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 2.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        MittenMouseEntity mittenMouse = (MittenMouseEntity) entity;

        float finalLimbSwing = limbSwing;
        float finalLimbSwingAmount = limbSwingAmount;

        if (mittenMouse.isBaby()) {
            finalLimbSwing = limbSwing * 0.66f;
            finalLimbSwingAmount = limbSwingAmount * 0.66f;
        }

        this.applyHeadRotation(netHeadYaw, headPitch);
        this.animateWalk(MittenMouseAnimations.MOUSE_WALKING, finalLimbSwing, finalLimbSwingAmount, 2f, 2.4f);
        this.animate(mittenMouse.idleAnimationState, MittenMouseAnimations.MOUSE_IDLE, ageInTicks, 1f);
    }

    private void applyHeadRotation(float pNetHeadYaw, float pHeadPitch) {
        pNetHeadYaw = Mth.clamp(pNetHeadYaw, -30.0F, 30.0F);
        pHeadPitch = Mth.clamp(pHeadPitch, -25.0F, 45.0F);

        this.head.yRot = pNetHeadYaw * ((float)Math.PI / 180F);
        this.head.xRot = pHeadPitch * ((float)Math.PI / 180F);
    }
    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        mitten_mouse.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart root() {
        return mitten_mouse;
    }

    @Override
    public void translateToHand(HumanoidArm humanoidArm, PoseStack poseStack) {
        this.mitten_mouse.translateAndRotate(poseStack);
        this.body.translateAndRotate(poseStack);
    }
}

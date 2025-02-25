package net.cebularz.winterwonders.client.models.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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
import net.cebularz.winterwonders.entity.custom.BriskEntity;

public class BriskModel<T extends Entity> extends HierarchicalModel<T> {
    public static final ModelLayerLocation BRISK_LAYER = new ModelLayerLocation(new ResourceLocation(WinterWonders.MOD_ID, "brisk"), "main");
    private final ModelPart brisk;
    private final ModelPart head;
    private final ModelPart middleSpikes;
    private final ModelPart iceSpike1;
    private final ModelPart iceSpike2;
    private final ModelPart iceSpike3;
    private final ModelPart iceSpike4;
    private final ModelPart iceSpike5;

    public BriskModel(ModelPart root) {
        this.brisk = root.getChild("brisk");
        this.head = this.brisk.getChild("head");
        this.middleSpikes = this.brisk.getChild("middleSpikes");
        this.iceSpike1 = this.middleSpikes.getChild("iceSpike1");
        this.iceSpike2 = this.middleSpikes.getChild("iceSpike2");
        this.iceSpike3 = this.middleSpikes.getChild("iceSpike3");
        this.iceSpike4 = this.middleSpikes.getChild("iceSpike4");
        this.iceSpike5 = this.brisk.getChild("iceSpike5");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition brisk = partdefinition.addOrReplaceChild("brisk", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition head = brisk.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -2.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition middleSpikes = brisk.addOrReplaceChild("middleSpikes", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition iceSpike1 = middleSpikes.addOrReplaceChild("iceSpike1", CubeListBuilder.create(), PartPose.offset(-6.0F, -6.0F, 0.0F));

        PartDefinition face2_r1 = iceSpike1.addOrReplaceChild("face2_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, -4.0F, 0.0F, 4.0F, 10.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

        PartDefinition face1_r1 = iceSpike1.addOrReplaceChild("face1_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, -4.0F, 0.0F, 4.0F, 10.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        PartDefinition iceSpike2 = middleSpikes.addOrReplaceChild("iceSpike2", CubeListBuilder.create(), PartPose.offset(0.0F, -6.0F, -6.0F));

        PartDefinition face2_r2 = iceSpike2.addOrReplaceChild("face2_r2", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, -4.0F, 0.0F, 4.0F, 10.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        PartDefinition face1_r2 = iceSpike2.addOrReplaceChild("face1_r2", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, -4.0F, 0.0F, 4.0F, 10.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

        PartDefinition iceSpike3 = middleSpikes.addOrReplaceChild("iceSpike3", CubeListBuilder.create(), PartPose.offset(0.0F, -6.0F, 6.0F));

        PartDefinition face2_r3 = iceSpike3.addOrReplaceChild("face2_r3", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, -4.0F, 0.0F, 4.0F, 10.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        PartDefinition face1_r3 = iceSpike3.addOrReplaceChild("face1_r3", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, -4.0F, 0.0F, 4.0F, 10.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

        PartDefinition iceSpike4 = middleSpikes.addOrReplaceChild("iceSpike4", CubeListBuilder.create(), PartPose.offset(6.0F, -6.0F, 0.0F));

        PartDefinition face2_r4 = iceSpike4.addOrReplaceChild("face2_r4", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, -4.0F, 0.0F, 4.0F, 10.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

        PartDefinition face1_r4 = iceSpike4.addOrReplaceChild("face1_r4", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, -4.0F, 0.0F, 4.0F, 10.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        PartDefinition iceSpike5 = brisk.addOrReplaceChild("iceSpike5", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition face2_r5 = iceSpike5.addOrReplaceChild("face2_r5", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, -4.0F, 0.0F, 4.0F, 10.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

        PartDefinition face1_r5 = iceSpike5.addOrReplaceChild("face1_r5", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, -4.0F, 0.0F, 4.0F, 10.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        BriskEntity brisk = (BriskEntity) entity;
        this.applyHeadRotation(netHeadYaw, headPitch, ageInTicks);
        this.animate(brisk.idleAnimationState, ModAnimationDefintions.BRISK_IDLE, ageInTicks, 1f);
    }

    private void applyHeadRotation(float pNetHeadYaw, float pHeadPitch, float pAgeInTicks) {
        pNetHeadYaw = Mth.clamp(pNetHeadYaw, -30.0F, 30.0F);
        pHeadPitch = Mth.clamp(pHeadPitch, -25.0F, 45.0F);

        this.head.yRot = pNetHeadYaw * ((float)Math.PI / 180F);
        this.head.xRot = pHeadPitch * ((float)Math.PI / 180F);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        poseStack.pushPose();
        poseStack.translate(0.0, -0.25, 0.0);
        brisk.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        poseStack.popPose();
    }

    @Override
    public ModelPart root() {
        return brisk;
    }
}

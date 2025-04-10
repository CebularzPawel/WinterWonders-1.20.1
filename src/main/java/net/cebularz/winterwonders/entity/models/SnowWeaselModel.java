package net.cebularz.winterwonders.entity.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.entity.animations.SnowWeaselAnimations;
import net.cebularz.winterwonders.entity.custom.SnowWeaselEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class SnowWeaselModel<T extends Entity> extends HierarchicalModel<T> {
    public static final ModelLayerLocation SNOW_WEASEL_LAYER = new ModelLayerLocation(new ResourceLocation(WinterWonders.MOD_ID, "snow_weasel"), "main");
    private final ModelPart weasel;
    private final ModelPart chest;
    private final ModelPart hands;
    private final ModelPart head;
    private final ModelPart posterior;
    private final ModelPart feet;
    private final ModelPart tail;

    public SnowWeaselModel(ModelPart root) {
        this.weasel = root.getChild("weasel");
        this.chest = this.weasel.getChild("chest");
        this.hands = this.chest.getChild("hands");
        this.head = this.chest.getChild("head");
        this.posterior = this.weasel.getChild("posterior");
        this.feet = this.posterior.getChild("feet");
        this.tail = this.posterior.getChild("tail");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition weasel = partdefinition.addOrReplaceChild("weasel", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition chest = weasel.addOrReplaceChild("chest", CubeListBuilder.create().texOffs(0, 6).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, -2.0F));

        PartDefinition hands = chest.addOrReplaceChild("hands", CubeListBuilder.create().texOffs(12, 23).addBox(1.0F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(12, 23).addBox(-2.0F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.0F, -1.0F));

        PartDefinition head = chest.addOrReplaceChild("head", CubeListBuilder.create().texOffs(1, 14).addBox(-2.0F, -2.25F, -5.0F, 4.0F, 4.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(22, 20).addBox(-1.5F, -0.25F, -6.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(1, 24).addBox(1.0F, -3.25F, -2.0F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(7, 24).addBox(-3.0F, -3.25F, -2.0F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.25F, -2.0F));

        PartDefinition posterior = weasel.addOrReplaceChild("posterior", CubeListBuilder.create().texOffs(16, 3).addBox(-2.5F, -2.5F, 0.0F, 5.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.5F, 0.0F));

        PartDefinition feet = posterior.addOrReplaceChild("feet", CubeListBuilder.create().texOffs(14, 23).addBox(0.5F, 0.0F, -2.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(14, 23).mirror().addBox(-2.5F, 0.0F, -2.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 2.5F, 6.0F));

        PartDefinition tail = posterior.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offset(0.0F, -2.5179F, 5.2189F));

        PartDefinition tailtuft_r1 = tail.addOrReplaceChild("tailtuft_r1", CubeListBuilder.create().texOffs(42, 2).addBox(-1.0F, 0.0413F, 5.8246F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(38, 6).addBox(-1.0F, 0.0413F, -0.1754F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.4363F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        SnowWeaselEntity snowWeasel = (SnowWeaselEntity) entity;

        this.applyHeadRotation(netHeadYaw, headPitch);

        float finalLimbSwing = limbSwing;
        float finalLimbSwingAmount = limbSwingAmount;

        if (snowWeasel.isBaby()) {
            finalLimbSwing = limbSwing * 0.66f;
            finalLimbSwingAmount = limbSwingAmount * 0.66f;
        }

        this.animateWalk(SnowWeaselAnimations.WALKING, finalLimbSwing, finalLimbSwingAmount, 1.25f, 2.4f);
        this.animate(snowWeasel.idleAnimationState, SnowWeaselAnimations.IDLE, ageInTicks, 1f);
    }

    private void applyHeadRotation(float pNetHeadYaw, float pHeadPitch) {
        pNetHeadYaw = Mth.clamp(pNetHeadYaw, -30.0F, 30.0F);
        pHeadPitch = Mth.clamp(pHeadPitch, -25.0F, 45.0F);

        this.head.yRot = pNetHeadYaw * ((float)Math.PI / 180F);
        this.head.xRot = pHeadPitch * ((float)Math.PI / 180F);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        weasel.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart root() {
        return weasel;
    }
}

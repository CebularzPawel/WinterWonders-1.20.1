package net.cebularz.winterwonders.entity.models;

import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.entity.custom.PinginEntity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

public class PinginBabyModel<T extends PinginEntity> extends PinginModel<T> {
    public static final ModelLayerLocation BABY_LAYER = new ModelLayerLocation(new ResourceLocation(WinterWonders.MOD_ID, "pingin_baby"), "main");

    public PinginBabyModel(ModelPart root) { super(root); }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition pingin = partdefinition.addOrReplaceChild("pingin", CubeListBuilder.create(), PartPose.offset(0.0F, 19.0F, 0.0F));

        PartDefinition head = pingin.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 11).addBox(-3.0F, -3.0F, -2.0F, 6.0F, 3.0F, 5.0F, new CubeDeformation(0.0F))
        .texOffs(0, 19).addBox(-1.5F, -2.0F, -3.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.5F, 0.0F));

        PartDefinition body = pingin.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 1).addBox(-3.0F, -1.875F, -2.375F, 6.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.375F, 0.375F));

        PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(22, 9).addBox(0.0F, 0.0F, -2.5F, 1.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, -1.875F, 0.125F));

        PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(22, 9).mirror().addBox(-1.0F, 0.0F, -2.5F, 1.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-3.0F, -1.875F, 0.125F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offset(0.0F, 2.125F, 2.625F));

        PartDefinition tail_r1 = tail.addOrReplaceChild("tail_r1", CubeListBuilder.create().texOffs(15, 2).addBox(-2.0F, 0.0F, -2.0F, 5.0F, 0.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -0.5F, 1.75F, 0.2618F, 0.0F, 0.0F));

        PartDefinition left_foot = pingin.addOrReplaceChild("left_foot", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -1.5F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 4.5F, 0.5F));
        PartDefinition left_toes = left_foot.addOrReplaceChild("left_toes", CubeListBuilder.create().texOffs(4, 19).addBox(-1.5F, 0.0F, -2.5F, 3.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.5F, 0.0F));

        PartDefinition right_foot = pingin.addOrReplaceChild("right_foot", CubeListBuilder.create().texOffs(0, 3).addBox(-0.5F, -1.5F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 4.5F, 0.5F));
        PartDefinition right_toes = right_foot.addOrReplaceChild("right_toes", CubeListBuilder.create().texOffs(4, 19).mirror().addBox(-1.5F, 0.0F, -2.5F, 3.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 0.5F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

}

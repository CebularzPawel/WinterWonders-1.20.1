package net.cebularz.winterwonders.entity.models.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.cebularz.winterwonders.WinterWonders;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.WardenModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class OrnamentModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation ORNAMENT_LAYER = new ModelLayerLocation(new ResourceLocation(WinterWonders.MOD_ID, "ornament"), "main");
    private final ModelPart ornament;

    public OrnamentModel(ModelPart root) {
        this.ornament = root.getChild("ornament");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partdefinition = meshDefinition.getRoot();

        PartDefinition ornament = partdefinition.addOrReplaceChild("ornament", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -5.0F, -2.0F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(20, 6).addBox(-2.0F, -6.0F, -1.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 0.0F, -0.5F));

        PartDefinition hanger2_r1 = ornament.addOrReplaceChild("hanger2_r1", CubeListBuilder.create().texOffs(23, 1).addBox(0.0F, -1.0F, -1.5F, 0.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -7.0F, 0.5F, 0.0F, -2.3562F, 0.0F));
        PartDefinition hanger1_r1 = ornament.addOrReplaceChild("hanger1_r1", CubeListBuilder.create().texOffs(23, 1).addBox(0.0F, -1.0F, -1.5F, 0.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -7.0F, 0.5F, 0.0F, -0.7854F, 0.0F));

        return LayerDefinition.create(meshDefinition, 32, 32);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        ornament.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void setupAnim(T t, float v, float v1, float v2, float v3, float v4) {

    }
}

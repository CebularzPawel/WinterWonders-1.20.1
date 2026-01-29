package net.cebularz.winterwonders.entity.models.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.cebularz.winterwonders.WinterWonders;

public class IcicleProjectileModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation ICICLE_LAYER = new ModelLayerLocation(new ResourceLocation(WinterWonders.MOD_ID, "icicle"), "main");
    private final ModelPart icicle;

    public IcicleProjectileModel(ModelPart root) {
        this.icicle = root.getChild("icicle");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition icicle = partdefinition.addOrReplaceChild("icicle", CubeListBuilder.create().texOffs(8, 8).addBox(-2.0F, -10.0F, 5.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition face2_r1 = icicle.addOrReplaceChild("face2_r1", CubeListBuilder.create().texOffs(0, -8).addBox(0.0F, -2.0F, -6.0F, 0.0F, 4.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -8.0F, 0.0F, -3.1416F, 0.0F, 3.1416F));
        PartDefinition face1_r1 = icicle.addOrReplaceChild("face1_r1", CubeListBuilder.create().texOffs(0, -12).addBox(0.0F, -2.0F, -6.0F, 0.0F, 4.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -8.0F, 0.0F, -3.1416F, 0.0F, -1.5708F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        icicle.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void setupAnim(T t, float v, float v1, float v2, float v3, float v4) {

    }
}

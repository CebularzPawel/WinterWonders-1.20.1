package net.turtleboi.winterwonders.client.models.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.turtleboi.winterwonders.WinterWonders;
import net.turtleboi.winterwonders.entity.animations.ModAnimationDefintions;
import net.turtleboi.winterwonders.entity.custom.SnowWispEntity;

public class SnowWispModel<T extends Entity> extends HierarchicalModel<T> {
    public static final ModelLayerLocation SNOW_WISP_LAYER = new ModelLayerLocation(new ResourceLocation(WinterWonders.MOD_ID, "snow_wisp"), "main");
    private final ModelPart bb_main;
    private final ModelPart wing1;
    private final ModelPart wing2;

    private float r = 1.0F;
    private float g = 1.0F;
    private float b = 1.0F;

    public SnowWispModel(ModelPart root) {
        super(RenderType::entityTranslucent);
        bb_main = root.getChild("bb_main");
        wing1 = bb_main.getChild("wing1");
        wing2 = bb_main.getChild("wing2");
    }

    @Override
    public ModelPart root() {
        return bb_main;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bb_main = partdefinition.addOrReplaceChild(
                "bb_main",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-3.0F, -6.0F, -3.0F, 6.0F, 6.0F, 6.0F,
                                new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 24.0F, 0.0F)
        );

        PartDefinition wing1 = bb_main.addOrReplaceChild(
                "wing1",
                CubeListBuilder.create()
                        .texOffs(12, 6)
                        .addBox(0.0F, -1.0F, -1.0F, 0.0F, 4.0F, 6.0F,
                                new CubeDeformation(0.0F)),
                PartPose.offset(-1.0F, -4.0F, 4.0F)
        );

        PartDefinition wing2 = bb_main.addOrReplaceChild(
                "wing2",
                CubeListBuilder.create()
                        .texOffs(0, 6)
                        .addBox(0.0F, -1.0F, -1.0F, 0.0F, 4.0F, 6.0F,
                                new CubeDeformation(0.0F)),
                PartPose.offset(1.0F, -4.0F, 4.0F)
        );

        return LayerDefinition.create(meshdefinition, 32, 32);
    }


    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        this.animate(((SnowWispEntity) entity).idleAnimationState, ModAnimationDefintions.WING_FLAP_IDLE, ageInTicks, 1f);
    }

    public void setColor(int color) {
        this.r = (color >> 16 & 255) / 255.0F;
        this.g = (color >> 8 & 255) / 255.0F;
        this.b = (color & 255) / 255.0F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        bb_main.render(poseStack, vertexConsumer, LightTexture.FULL_BRIGHT, packedOverlay, this.r, this.g, this.b, alpha);
    }
}

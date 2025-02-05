package net.turtleboi.winterwonders.client.models.blocks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.turtleboi.winterwonders.WinterWonders;

import java.util.function.Function;

public class IcyVinesModel extends Model {
    public static final ModelLayerLocation ICY_VINES_LAYER = new ModelLayerLocation(new ResourceLocation(WinterWonders.MOD_ID, "icy_vines"), "main");

    private final ModelPart root;

    public IcyVinesModel(Function<ResourceLocation, RenderType> pRenderType, ModelPart root) {
        super(pRenderType);
        this.root = root;
    }


    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int light, int overlay,
                               float red, float green, float blue, float alpha) {
        root.render(poseStack, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition rootPart = meshDefinition.getRoot();
        rootPart.addOrReplaceChild("root",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-8.0F, -8.0F, -0.5F, 16.0F, 16.0F, 1.0F),
                PartPose.offset(0.0F, 16.0F, 0.0F)
        );

        return LayerDefinition.create(meshDefinition, 32, 32);
    }
}

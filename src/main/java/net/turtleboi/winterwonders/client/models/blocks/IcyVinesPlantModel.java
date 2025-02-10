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

public class IcyVinesPlantModel extends Model {
    public static final ModelLayerLocation ICY_VINES_PLANT_LAYER = new ModelLayerLocation(new ResourceLocation(WinterWonders.MOD_ID, "icy_vines_plant"), "main");
    private final ModelPart part1;
    private final ModelPart part2;

    public IcyVinesPlantModel(ModelPart root) {
        super(RenderType::entityCutout);
        this.part1 = root.getChild("part1");
        this.part2 = root.getChild("part2");
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int light, int overlay,
                               float red, float green, float blue, float alpha) {
        part1.render(poseStack, vertexConsumer, light, overlay, red, green, blue, alpha);
        part2.render(poseStack, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        PartDefinition part1 = partDefinition.addOrReplaceChild("part1",
                CubeListBuilder.create()
                        .texOffs(0, -20)
                        .addBox(0.0F, -8.0F, -10F, 0.0F, 16.0F, 20.0F,
                                new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-8.0F, -8.0F, 8.0F, 0.0F, 0.7854F, 0.0F));
        PartDefinition part2 = partDefinition.addOrReplaceChild("part2",
                CubeListBuilder.create()
                        .texOffs(0, -20)
                        .addBox(0.0F, -8.0F, -10F, 0.0F, 16.0F, 20.0F,
                                new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-8.0F, -8.0F, 8.0F, 0.0F, -0.7854F, 0.0F));

        return LayerDefinition.create(meshDefinition, 20, 16);
    }
}

package net.turtleboi.winterwonders.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.turtleboi.winterwonders.WinterWonders;

public class CursedPortalModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation CURSED_PORTAL_LAYER = new ModelLayerLocation(new ResourceLocation(WinterWonders.MOD_ID, "cursed_portal"), "main");
    private final ModelPart cursed_portal;

    public CursedPortalModel(ModelPart root) {
        this.cursed_portal = root.getChild("cursed_portal");
    }

    public static LayerDefinition createBodyLayer(){
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition cursed_portal = partdefinition.addOrReplaceChild(
                "cursed_portal",
                CubeListBuilder.create()
                        .texOffs(0,0)
                        .addBox(-11.0F, 0.0F, -0.5F, 22.0F, 42.0F, 1.0F,
                                new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -42.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        cursed_portal.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
    }
}

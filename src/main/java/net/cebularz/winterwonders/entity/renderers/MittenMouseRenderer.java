package net.cebularz.winterwonders.entity.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.entity.custom.MittenMouseEntity;
import net.cebularz.winterwonders.entity.models.MittenMouseModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class MittenMouseRenderer extends MobRenderer<MittenMouseEntity, MittenMouseModel<MittenMouseEntity>> {
    private static final ResourceLocation BROWN_MOUSE = new ResourceLocation(WinterWonders.MOD_ID, "textures/entity/mice/mitten_mouse_brown.png");
    private static final ResourceLocation WHITE_MOUSE = new ResourceLocation(WinterWonders.MOD_ID, "textures/entity/mice/mitten_mouse_white.png");
    private static final ResourceLocation BLACK_MOUSE = new ResourceLocation(WinterWonders.MOD_ID, "textures/entity/mice/mitten_mouse_black.png");
    public MittenMouseRenderer(EntityRendererProvider.Context context) {
        super(context, new MittenMouseModel<>(context.bakeLayer(MittenMouseModel.MOUSE_LAYER)), 0.25f);
        //add item render
    }

    @Override
    public ResourceLocation getTextureLocation(MittenMouseEntity mittenMouseEntity) {
        return switch (mittenMouseEntity.getVariant()) {
            case 1 -> WHITE_MOUSE;
            case 2 -> BLACK_MOUSE;
            default -> BROWN_MOUSE;
        };
    }

    @Override
    protected @Nullable RenderType getRenderType(MittenMouseEntity pLivingEntity, boolean pBodyVisible, boolean pTranslucent, boolean pGlowing) {
        return RenderType.entityCutout(getTextureLocation(pLivingEntity));
    }

    @Override
    public void render(MittenMouseEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        if (pEntity.isBaby()){
            pPoseStack.scale(0.75f, 0.75f, 0.75f);
        }
        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
    }
}

package net.cebularz.winterwonders.entity.renderers;

import com.mojang.blaze3d.vertex.PoseStack;

import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.entity.custom.PinginEntity;
import net.cebularz.winterwonders.entity.models.PinginBabyModel;
import net.cebularz.winterwonders.entity.models.PinginModel;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import org.joml.Vector3f;

public class PinginRenderer extends MobRenderer<PinginEntity, PinginModel<PinginEntity>> {
    private static final ResourceLocation ADULT_TEXTURE = new ResourceLocation(WinterWonders.MOD_ID, "textures/entity/pingin.png");
    private static final ResourceLocation BABY_TEXTURE = new ResourceLocation(WinterWonders.MOD_ID, "textures/entity/pingin_baby.png");
    private final PinginModel<PinginEntity> adultModel, babyModel;
    private static final int[] SHAKE_START = {0, 4, 6, 6, 8};
    private static final int[] SHAKE_END = {8, 14, 18, 18, 24};

    public PinginRenderer(EntityRendererProvider.Context context) {
        super(context, new PinginModel<>(context.bakeLayer(PinginModel.PINGIN_LAYER)), 0.5F);
        adultModel = model;
        babyModel = new PinginBabyModel<>(context.bakeLayer(PinginBabyModel.BABY_LAYER));
        addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(PinginEntity pingin) {
        return pingin.isBaby() ? BABY_TEXTURE : ADULT_TEXTURE;
    }

    @Override
    public void render(PinginEntity pingin, float yaw, float partial, PoseStack pose, MultiBufferSource buffer, int light) {
        model = pingin.isBaby() ? babyModel : adultModel;
        shadowRadius = pingin.isBaby() ? 0.35F : 0.5F;
        super.render(pingin, yaw, partial, pose, buffer, light);
        if (pingin.getSwimAction() == PinginEntity.SHAKE && pingin.lastShakeTick != pingin.tickCount) {
            pingin.lastShakeTick = pingin.tickCount;
            int tick = (int)(pingin.swimActionState.getAccumulatedTime() / 50L) - PinginEntity.LAND_TICKS;
            if (tick >= 0 && tick < PinginEntity.SHAKE_TICKS && tick % 2 == 0) {
                PoseStack waterShed = new PoseStack();
                float heading = Mth.rotLerp(partial, pingin.yBodyRotO, pingin.yBodyRot);
                setupRotations(pingin, waterShed, pingin.tickCount + partial, heading, partial);
                waterShed.scale(-1F, -1F, 1F);
                scale(pingin, waterShed, partial);
                waterShed.translate(0D, -1.501D, 0D);
                ModelPart root = model.root(), body = root.getChild("body");
                root.translateAndRotate(waterShed);
                ModelPart[] parts = {root.getChild("head"), body, body.getChild("left_arm"),
                        body.getChild("right_arm"), body.getChild("tail").getChild("tail_r1")};
                for (int index = 0; index < parts.length; index++) {
                    if (tick >= SHAKE_START[index] && tick < SHAKE_END[index]) {
                        waterShed.pushPose();
                        if (index > 1) {
                            body.translateAndRotate(waterShed);
                        }
                        if (index == 4) {
                            body.getChild("tail").translateAndRotate(waterShed);
                        }
                        ModelPart part = parts[index];
                        part.translateAndRotate(waterShed);
                        ModelPart.Cube cube = part.getRandomCube(pingin.getRandom());
                        float side = pingin.getRandom().nextBoolean() ? 1F : -1F;
                        Vector3f point = new Vector3f(side > 0F ? cube.maxX : cube.minX,
                                Mth.lerp(pingin.getRandom().nextFloat(), cube.minY, cube.maxY),
                                Mth.lerp(pingin.getRandom().nextFloat(), cube.minZ, cube.maxZ)).div(16F);
                        waterShed.last().pose().transformPosition(point);
                        Vector3f drift = waterShed.last().pose().transformDirection(new Vector3f(side * 0.06F, 0F, 0F));
                        pingin.level().addParticle(ParticleTypes.SPLASH,
                                Mth.lerp(partial, pingin.xo, pingin.getX()) + point.x,
                                Mth.lerp(partial, pingin.yo, pingin.getY()) + point.y,
                                Mth.lerp(partial, pingin.zo, pingin.getZ()) + point.z, drift.x, 0D, drift.z);
                        waterShed.popPose();
                    }
                }
            }
        }
    }
}

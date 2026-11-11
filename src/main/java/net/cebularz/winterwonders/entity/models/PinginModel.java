package net.cebularz.winterwonders.entity.models;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.entity.animations.PinginAnimations;
import net.cebularz.winterwonders.entity.animations.AnimationUtil;
import net.cebularz.winterwonders.entity.animations.ThrowingSpearAnimations;
import net.cebularz.winterwonders.entity.custom.PinginEntity;
import net.cebularz.winterwonders.entity.custom.PinginAttack;
import net.cebularz.winterwonders.item.custom.ThrowingSpearItem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class PinginModel<T extends PinginEntity> extends HierarchicalModel<T> implements ArmedModel {
    public static final ModelLayerLocation PINGIN_LAYER = new ModelLayerLocation(new ResourceLocation(WinterWonders.MOD_ID, "pingin"), "mainHand");
    private static final int MIN_X = 0, MIN_Y = 1, MIN_Z = 2, MAX_X = 3, MAX_Y = 4, MAX_Z = 5;
    private static final float FLOOR_Y = 24F;
    private static final float CANE_REACH = 8F;

    private record HoldProfile(float scale, float gripX, float gripY, float gripZ, float tilt, float turn,
                               float pitch, float drop, float flare, float back) {}
    private static final HoldProfile ITEM = new HoldProfile(0.7F, 0.5F, 6.5F, -4.5F, -0.15F, 0F, 1.2F, 3F, 0.44F, 1.25F);
    private static final HoldProfile BLOCK = new HoldProfile(0.8F, 0.5F, 8F, -3F, 0F, 0F, 1.2F, 3F, 0.44F, 0.5F);
    private static final HoldProfile HANDHELD = new HoldProfile(1F, 0.5F, 6.5F, -1.65F, -0.2F, 0.35F, 0.85F, 2.7F, 0.7F, 0.25F);
    private static final HoldProfile SPEAR = new HoldProfile(1F, 0.5F, 6.5F, -1.5F, -0.2F, 0F, 1.45F, 3F, 0F, 2.25F);

    private static final class HandPose {
        final ModelPart wing;
        final ModelPart.Cube shape;
        final int side;
        final Vector3f gripPivot = new Vector3f(), gripSlide = new Vector3f(), itemCenter = new Vector3f(), swingAxis = new Vector3f(), pointAxis = new Vector3f();
        final Vector3f caneAim = new Vector3f();
        final Quaternionf gripPlane = new Quaternionf();
        HoldProfile profile = ITEM;
        BakedModel model;
        boolean holdsFish;
        float itemYaw, weaponAngle, gripShift, aimWeight, aimHeight, aimTurn = 1F;
        float caneShift, caneWeight;
        HandPose(ModelPart wing, int side, ModelPart.Cube shape) {
            this.wing = wing;
            this.side = side;
            this.shape = shape;
        }

        float height() { return shape.maxY - shape.minY; }
        float depth() { return shape.maxZ - shape.minZ; }
        float gripX() {
            float inset = profile == SPEAR ? Math.max(0F, 8F - height()) / 30F : 0F;
            return side * (profile.gripX + inset);
        }
        float gripY() { return profile.gripY * height() / 8F; }
        float gripZ() {
            float inset = profile == SPEAR ? Math.max(0F, 8F - height()) * 0.15F : 0F;
            return profile.gripZ * depth() / 6F + inset;
        }
        float carryPitch(float pitchRadians) { return Math.min(1.52F, pitchRadians * 8F / height()); }
        Vector3f grip(Vector3f result) { return result.set(gripPivot).fma(gripShift, gripSlide); }
    }

    private final ModelPart rootPart, head, body, tail;
    private final ModelPart[] feet, toes;
    private final HandPose[] hands;
    private final RandomSource gripRandom = RandomSource.create(42L);
    private final Vector3f scratch = new Vector3f(), aim = new Vector3f(), up = new Vector3f();
    private final Quaternionf gripRotation = new Quaternionf();
    private final Matrix4f partTransform = new Matrix4f(), bodyTransform = new Matrix4f(), worldTransform = new Matrix4f();
    private final float[] headBounds = new float[6], partBounds = new float[6];
    private final Map<ModelPart, List<ModelPart.Cube>> geometry = new IdentityHashMap<>();
    private final float[] torso = new float[6];
    private final Vector3f motionScale = new Vector3f();
    private final AnimationDefinition idle, walk, slideClip, paddle, swimClip, landClip, shakeClip;
    private final float tailPitchBias;

    public PinginModel(ModelPart root) {
        rootPart = root.getChild("pingin");
        body = rootPart.getChild("body");
        head = rootPart.getChild("head");
        tail = body.getChild("tail");
        root.getAllParts().forEach(part -> geometry.put(part, ownCubes(part)));
        ModelPart leftWing = body.getChild("left_arm"), rightWing = body.getChild("right_arm");
        hands = new HandPose[]{new HandPose(leftWing, 1, geometry.get(leftWing).get(0)),
                new HandPose(rightWing, -1, geometry.get(rightWing).get(0))};
        feet = new ModelPart[]{rootPart.getChild("left_foot"), rootPart.getChild("right_foot")};
        toes = new ModelPart[]{feet[0].getChild("left_toes"), feet[1].getChild("right_toes")};
        bounds(body, partTransform.identity(), torso, false);
        motionScale.set((torso[MAX_X] - torso[MIN_X]) / 8F, (torso[MAX_Y] - torso[MIN_Y]) / 8F,
                (body.getInitialPose().z + torso[MIN_Z] - head.getInitialPose().z) / -3F);
        ModelPart.Cube web = geometry.get(toes[0]).get(0);
        Vector3f toeScale = new Vector3f((web.maxX - web.minX) / 3F, (web.maxZ - web.minZ) / 4F, (web.maxZ - web.minZ) / 4F);
        Map<String, Vector3f> positions = Map.of("left_toes", toeScale, "right_toes", toeScale);
        idle = AnimationUtil.retarget(PinginAnimations.IDLE, motionScale, positions);
        walk = AnimationUtil.retarget(PinginAnimations.WALKING, motionScale, positions);
        slideClip = AnimationUtil.retarget(PinginAnimations.SLIDE, motionScale, positions);
        paddle = AnimationUtil.retarget(PinginAnimations.PADDLING, motionScale, positions);
        swimClip = AnimationUtil.retarget(PinginAnimations.SWIMMING, motionScale, positions);
        landClip = AnimationUtil.retarget(PinginAnimations.LANDING, motionScale, positions);
        shakeClip = AnimationUtil.retarget(PinginAnimations.SHAKE, motionScale, positions);
        tailPitchBias = 0.6545F - tail.getInitialPose().xRot;
    }

    private static List<ModelPart.Cube> ownCubes(ModelPart part) {
        List<ModelPart.Cube> result = new ArrayList<>();
        part.visit(new PoseStack(), (pose, path, index, cube) -> {
            if (path.isEmpty()) {
                result.add(cube);
            }
        });
        return result;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition pingin = partdefinition.addOrReplaceChild("pingin", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition head = pingin.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 14).addBox(-4.0F, -6.0F, -3.0F, 8.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(8, 26).addBox(-2.0F, -2.5F, -4.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.5F, 0.0F));

        PartDefinition body = pingin.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -3.0F, 8.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.5F, 0.0F));

        PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(22, 20).addBox(0.0F, 0.0F, -3.0F, 1.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, -4.0F, 0.0F));

        PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 26).addBox(-1.0F, 0.0F, -3.0F, 1.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, -4.0F, 0.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offsetAndRotation(0.5F, 2.5F, 3.0F, 0.6545F, 0.0F, 0.0F));
        PartDefinition tail_r1 = tail.addOrReplaceChild("tail_r1", CubeListBuilder.create().texOffs(20, 6).addBox(-3.0F, 0.0F, -2.0F, 5.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.5F, 1.75F, 0.2618F, 0.0F, 0.0F));

        PartDefinition left_foot = pingin.addOrReplaceChild("left_foot", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -1.5F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(2.5F, -0.5F, 0.5F));
        PartDefinition left_toes = left_foot.addOrReplaceChild("left_toes", CubeListBuilder.create().texOffs(18, 14).addBox(-1.5F, 0.0F, -2.5F, 3.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.5F, 0.0F));

        PartDefinition right_foot = pingin.addOrReplaceChild("right_foot", CubeListBuilder.create().texOffs(0, 3).addBox(-0.5F, -1.5F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.5F, -0.5F, 0.5F));
        PartDefinition right_toes = right_foot.addOrReplaceChild("right_toes", CubeListBuilder.create().texOffs(18, 14).addBox(-1.5F, 0.0F, -2.5F, 3.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.5F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(T pingin, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        root().getAllParts().forEach(ModelPart::resetPose);
        young = false;

        head.x -= body.getInitialPose().x;
        head.y -= body.getInitialPose().y;
        head.z -= body.getInitialPose().z;
        HandPose mainHand = hands[pingin.isLeftHanded() ? 0 : 1];
        boolean isAttacking = !pingin.isBaby() && pingin.attackAnimationState.isStarted();
        // boolean chipping = pingin.getWork() == PinginEntity.CHIP || pingin.getWork() == PinginEntity.CHIP_BACK;
        PinginAttack attack = pingin.getAttackType();
        for (HandPose hand : hands) {
            ItemStack stack = hand == mainHand ? pingin.getMainHandItem() : pingin.getOffhandItem();
            BakedModel model = stack.isEmpty() ? null : Minecraft.getInstance().getItemRenderer().getModel(stack, pingin.level(), pingin, pingin.getId());
            configureGrip(stack, model, hand);
        }
        if (isAttacking && attack.spear) {
            mainHand.profile = SPEAR;
        }
        float partialTick = Minecraft.getInstance().getFrameTime();
        float swimBlend = AnimationUtil.smootherStep(Mth.lerp(partialTick, pingin.lastSwimProgress, pingin.swimProgress));
        float landBlend = AnimationUtil.smootherStep(Mth.lerp(partialTick, pingin.lastLandProgress, pingin.landProgress));
        float swimWeight = swimBlend * (1F - landBlend);
        float diveBlend = AnimationUtil.smootherStep(Mth.lerp(partialTick, pingin.lastDiveProgress, pingin.diveProgress));
        float slideWeight = AnimationUtil.smootherStep(Mth.lerp(partialTick, pingin.lastSlideProgress, pingin.slideProgress));
        // float movementWeight = Mth.clamp(limbSwingAmount * 2.4F, 0F, 1F) * (chipping ? 0F : isAttacking ? 0.35F : 1F);
        float movementWeight = Mth.clamp(limbSwingAmount * 2.4F, 0F, 1F) * (isAttacking ? 0.35F : 1F);
        float landWeight = (1F - slideWeight) * (1F - swimWeight), diveWeight = swimWeight * diveBlend;
        head.yRot = Mth.clamp(netHeadYaw, -30F, 30F) * Mth.DEG_TO_RAD * (1F - slideWeight) * (1F - swimWeight * 0.8F);
        head.xRot = Mth.clamp(headPitch, -25F, 45F) * Mth.DEG_TO_RAD * landWeight;
        KeyframeAnimations.animate(this, idle, (long)(ageInTicks * 50F), (1F - movementWeight) * landWeight, scratch);
        KeyframeAnimations.animate(this, walk, (long)(limbSwing * 100F * motionScale.y * motionScale.y), movementWeight * landWeight, scratch);
        float slideTime = -1F;
        if (pingin.slideAnimationState.isStarted()) {
            pingin.slideAnimationState.updateTime(ageInTicks, 1F);
            slideTime = pingin.slideAnimationState.getAccumulatedTime() / 1000F;
            KeyframeAnimations.animate(this, slideClip, pingin.slideAnimationState.getAccumulatedTime(), slideWeight, scratch);
            tail.xRot += tailPitchBias * slideWeight * Mth.clamp(rootPart.xRot / Mth.HALF_PI, 0F, 1F);
        }
        float swimPhase = 0F;
        if (pingin.swimActionState.isStarted()) {
            pingin.swimActionState.updateTime(ageInTicks, 1F);
            float swimTicks = pingin.getSwimAction() == PinginEntity.SHAKE ? PinginEntity.LAND_TICKS + PinginEntity.SHAKE_TICKS
                    : pingin.getSwimAction() == PinginEntity.CATCH ? 12F : 24F;
            swimPhase = Mth.clamp(pingin.swimActionState.getAccumulatedTime() / (swimTicks * 50F), 0F, 1F);
        }
        poseSwimming(ageInTicks, swimWeight, diveBlend, pingin.getSwimAction(), swimPhase, landBlend);
        poseHolding();
        float holdWeight = landWeight;
        if (isAttacking) {
            pingin.attackAnimationState.updateTime(ageInTicks, 1F);
            float time = pingin.attackAnimationState.getAccumulatedTime() / 1000F;
            float strike = AnimationUtil.smootherStep(AnimationUtil.progress(time, 0F, 0.15F))
                    * (1F - AnimationUtil.smootherStep(AnimationUtil.progress(time, attack.seconds() - 0.15F, attack.seconds())));
            holdWeight *= 1F - strike;
        }
        for (HandPose hand : hands) {
            if (hand.profile == HANDHELD) {
                hand.wing.xRot -= 0.4F * holdWeight;
                hand.wing.zRot += hand.side * 0.3F * holdWeight;
                hand.wing.z += 1.35F * holdWeight;
            }
            hand.weaponAngle = 0F;
            hand.caneAim.zero();
            hand.caneWeight = 0F;
            hand.aimWeight = 0F;
            hand.aimHeight = 0F;
            hand.aimTurn = 1F;
            hand.gripShift = hand.profile == SPEAR ? Mth.clamp((8F - hand.height()) / 3F, 0F, 1F) : 0F;
        }
        // if (pingin.workAnimationState.isStarted() && pingin.getWork() != PinginEntity.CHIP_WALK) {
            // pingin.workAnimationState.updateTime(ageInTicks, 1F);
            // workPose(pingin.getWork(), pingin.workAnimationState.getAccumulatedTime() / 1000F);
        // }
        if (isAttacking) {
            if (attack.spear) {
                float time = pingin.attackAnimationState.getAccumulatedTime() / 1000F;
                float hit = attack.hitTick / 20F;
                float load = hit * 0.6F;
                float phase = time < load ? 0.05F * AnimationUtil.progress(time, 0F, load)
                        : time < hit ? 0.05F + 0.15F * AnimationUtil.progress(time, load, hit)
                        : 0.2F + 0.8F * AnimationUtil.progress(time, hit, attack.seconds());
                if (attack == PinginAttack.THROW) {
                    throwSpear(mainHand, time, attack);
                } else {
                    jabSpear(mainHand, phase, pingin.getJabReach(), attack);
                }
            } else {
                animate(pingin.attackAnimationState, PinginAnimations.attack(pingin.getAttackType(), pingin.isLeftHanded()), ageInTicks);
                mainHand.weaponAngle = PinginAnimations.weaponAngle(pingin.getAttackType(), pingin.attackAnimationState.getAccumulatedTime() / 1000F, scratch);
            }
        }
        float admire = AnimationUtil.smootherStep(Mth.lerp(partialTick, pingin.lastAdmireProgress, pingin.admireProgress)) * landWeight;
        admireItem(mainHand, ageInTicks, admire);
        for (HandPose hand : hands) hand.itemYaw = hand.wing.yRot * 0.75F;
        finishPose(slideWeight, mainHand, admire, swimWeight);
        boolean cane = !pingin.isBaby() && !isAttacking && !pingin.isAdmiring() && pingin.onGround()
                && mainHand.profile == SPEAR && mainHand.model != null && landWeight > 0.99F;
        if (cane) {
            poseCane(mainHand, scratch.set(mainHand.side * 4.5F, FLOOR_Y, -6.2F), 1F);
        }
        if (cane && movementWeight > 0.02F) {
            walkWithSpear(pingin, mainHand, limbSwing * 0.1F / 1.04F,
                    AnimationUtil.smootherStep(movementWeight * 3F), partialTick, ageInTicks);
        } else {
            pingin.canePlant = null;
            pingin.canePhase = -1F;
        }
        float swimTilt = Mth.lerp(partialTick, pingin.lastSwimPitch, pingin.swimPitch) * Mth.DEG_TO_RAD;
        rootPart.xRot += swimTilt * diveWeight;
        head.xRot += swimTilt * (swimBlend - swimWeight) * diveBlend;
        rootPart.zRot -= Mth.clamp(Mth.wrapDegrees(pingin.yBodyRot - pingin.yBodyRotO) * 1.5F, -18F, 18F) * Mth.DEG_TO_RAD * diveWeight;
        if (slideTime >= 0F) {
            rootPart.y -= slideBounce(slideTime) * slideWeight * motionScale.y;
        }
        composeHeadPose();
    }

    @Override
    public ModelPart root() { return rootPart; }

    @Override
    public void renderToBuffer(PoseStack pose, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        rootPart.render(pose, vertices, light, overlay, red, green, blue, alpha);
    }

    private Quaternionf heldItemRotation(HandPose hand) {
        boolean weapon = hand.profile == HANDHELD || hand.profile == SPEAR;
        gripRotation.identity().rotateX(hand.profile.tilt).rotateY(weapon ? 0F : hand.itemYaw);
        if (weapon) {
            float turn = hand.profile.turn;
            if (hand.profile == SPEAR) {
                gripRotation.mul(hand.gripPlane);
                partTransform.identity().rotateZYX(rootPart.zRot, rootPart.yRot, rootPart.xRot)
                        .rotateZYX(body.zRot, body.yRot, body.xRot).rotateZYX(hand.wing.zRot, hand.wing.yRot, hand.wing.xRot)
                        .invert().transformDirection(aim.set(0F, 0F, -1F)).rotateX(-hand.profile.tilt);
                hand.gripPlane.transformInverse(aim);
                partTransform.transformDirection(up.set(0F, 1F, 0F)).rotateX(-hand.profile.tilt);
                hand.gripPlane.transformInverse(up).cross(hand.swingAxis);
                if (up.lengthSquared() > 0.000001F) {
                    if (up.dot(aim) < 0F) {
                        up.negate();
                    }
                } else {
                    up.set(aim).fma(-aim.dot(hand.swingAxis), hand.swingAxis);
                }
                partTransform.transformDirection(aim.set(0F, -1F, 0F)).rotateX(-hand.profile.tilt);
                hand.gripPlane.transformInverse(aim);
                aim.fma(-aim.dot(hand.swingAxis), hand.swingAxis);
                float rest = Mth.clamp((8F - hand.height()) / 3F, 0F, 1F);
                float pitch = Mth.lerp(rest, 0.325F - hand.side * 0.025F, 0.08F)
                        * (1F - hand.aimWeight) * Math.min(1F, aim.lengthSquared());
                float lift = Math.copySign(pitch, up.angleSigned(aim, hand.swingAxis));
                up.normalize().rotateAxis(lift, hand.swingAxis.x, hand.swingAxis.y, hand.swingAxis.z);
                turn = hand.pointAxis.angleSigned(up, hand.swingAxis);
                if (hand.caneWeight > 0F) {
                    aim.set(hand.caneAim).rotateX(-hand.profile.tilt);
                    hand.gripPlane.transformInverse(aim);
                    float angle = hand.pointAxis.angleSigned(aim, hand.swingAxis);
                    turn += Mth.wrapDegrees((angle - turn) * Mth.RAD_TO_DEG) * Mth.DEG_TO_RAD * hand.caneWeight;
                }
            }
            gripRotation.rotateAxis(turn + hand.weaponAngle,
                    hand.swingAxis.x, hand.swingAxis.y, hand.swingAxis.z);
        }
        return gripRotation;
    }

    @Override
    public void translateToHand(HumanoidArm arm, PoseStack pose) {
        HandPose hand = hands[arm == HumanoidArm.LEFT ? 0 : 1];
        rootPart.translateAndRotate(pose);
        if (hand.holdsFish) {
            head.translateAndRotate(pose);
            ModelPart.Cube beak = geometry.get(head).get(1);
            pose.translate(0D, (beak.minY + beak.maxY) / 32F, (beak.minZ - 0.65F) / 16F);
            pose.mulPose(Axis.YP.rotationDegrees(45F));
            pose.translate(-hand.itemCenter.x / 16F, -hand.itemCenter.y / 16F, -hand.itemCenter.z / 16F);
        } else {
            body.translateAndRotate(pose);
            hand.wing.translateAndRotate(pose);
            pose.translate(hand.gripX() / 16F, hand.gripY() / 16F, hand.gripZ() / 16F);
            pose.mulPose(heldItemRotation(hand));
            Vector3f grip = hand.grip(scratch);
            pose.translate(-grip.x / 16F, -grip.y / 16F, -grip.z / 16F);
        }
        pose.scale(hand.profile.scale, hand.profile.scale, hand.profile.scale);
    }

    private void poseHolding() {
        for (HandPose hand : hands) {
            if (hand.model != null && !hand.holdsFish) {
                HoldProfile profile = hand.profile;
                hand.wing.xRot -= hand.carryPitch(profile.pitch);
                hand.wing.y += profile.drop * hand.depth() / 6F;
                hand.wing.z += profile.back * hand.depth() / 6F;
                hand.wing.zRot -= hand.side * profile.flare;
                if (profile == SPEAR) {
                    hand.wing.zRot = 0F;
                }
            }
        }
    }

    // private void workPose(int work, float time) {
        // float weight = AnimationUtil.smootherStep(AnimationUtil.progress(time, 0F, 0.3F))
                // * (1F - AnimationUtil.smootherStep(AnimationUtil.progress(time, 2.6F, 3F)));
        // float flutter = Mth.sin(time * 13F);
        // if (work == PinginEntity.CHIP || work == PinginEntity.CHIP_BACK) {
            // head.xRot = 0F;
            // head.yRot = 0F;
            // AnimationDefinition clip = work == PinginEntity.CHIP ? PinginAnimations.CHIP_HOP : PinginAnimations.CHIP_BACK;
            // float clipTime = time, peckTime = PinginEntity.CHIP_PECK_TICKS / 20F;
            // if (work == PinginEntity.CHIP && time < peckTime) {
                // clip = PinginAnimations.CHIP_BEAK;
            // } else if (work == PinginEntity.CHIP) {
                // clipTime -= peckTime;
            // }
            // KeyframeAnimations.animate(this, clip, (long)(clipTime * 1000F), 1F, scratch);
            // if (work == PinginEntity.CHIP_BACK) {
                // float flap = Mth.sin(time * 42F) * 0.35F * AnimationUtil.smootherStep(AnimationUtil.progress(time, 0.1F, 0.2F))
                        // * (1F - AnimationUtil.smootherStep(AnimationUtil.progress(time, 0.5F, 0.7F)));
                // hands[0].wing.xRot += flap;
                // hands[1].wing.xRot += flap;
            // }
        // } else if (work == PinginEntity.PREEN) {
            // head.xRot += 0.35F * weight;
            // head.yRot -= 0.4F * weight;
            // head.zRot += (0.16F + flutter * 0.06F) * weight;
            // hands[0].wing.xRot -= (0.55F + flutter * 0.09F) * weight;
            // hands[0].wing.zRot -= 0.3F * weight;
            // tail.yRot += Mth.sin(time * 7F) * 0.12F * weight;
        // } else {
            // body.xRot += (0.16F + flutter * 0.025F) * weight;
            // head.xRot += 0.3F * weight;
            // for (HandPose hand : hands) {
                // hand.wing.xRot -= (0.65F + Mth.sin(time * 10F + hand.side) * 0.18F) * weight;
                // hand.wing.yRot -= hand.side * 0.2F * weight;
            // }
        // }
    // }

    private void walkWithSpear(PinginEntity pingin, HandPose hand, float step, float weight, float partialTick, float time) {
        float phase = Mth.positiveModulo(step, 1F);
        float yaw = Mth.rotLerp(partialTick, pingin.yBodyRotO, pingin.yBodyRot) * Mth.DEG_TO_RAD;
        float sin = Mth.sin(yaw), cos = Mth.cos(yaw);
        Vec3 pos = pingin.getPosition(partialTick);
        float side = hand.side * 4.3F / 16F, ahead = 7.5F / 16F, stride = 1.04F / 0.6F;
        if (pingin.canePlant == null || phase < pingin.canePhase || time - pingin.caneTime > 2F) {
            float start = ahead - phase * stride;
            pingin.canePlant = pos.add(cos * side - sin * start, 0D, sin * side + cos * start);
        }
        Vec3 foot = pingin.canePlant;
        if (phase > 0.24F) {
            float swing = (phase - 0.24F) / 0.76F;
            float recover = AnimationUtil.smoothStep(swing);
            float reach = ahead + (1F - phase) * stride;
            Vec3 next = pos.add(cos * side - sin * reach, 0D, sin * side + cos * reach);
            foot = foot.lerp(next, recover).add(0D, Mth.square(Mth.sin(Mth.PI * swing)) * 0.075D, 0D);
        }
        pingin.canePhase = phase;
        pingin.caneTime = time;
        double x = foot.x - pos.x, z = foot.z - pos.z;
        scratch.set((float)(cos * x + sin * z) * 16F, 24F + (float)(pos.y - foot.y) * 16F, (float)(sin * x - cos * z) * 16F);
        poseCane(hand, scratch, weight);
    }

    private void poseCane(HandPose hand, Vector3f foot, float weight) {
        PartPose shoulder = hand.wing.getInitialPose();
        float shoulderY = shoulder.y + hand.profile.drop;
        float gripX = hand.gripX(), gripY = hand.gripY(), gripZ = hand.gripZ();
        transform(rootPart, worldTransform).mul(transform(body, bodyTransform)).invert().transformPosition(foot, aim);
        aim.sub(shoulder.x, shoulderY, shoulder.z);
        float reach = aim.x * aim.x + aim.y * aim.y - Mth.square(gripX);
        if (weight > 0F && reach > 0F) {
            reach = Mth.sqrt(reach);
            float footLength = Mth.sqrt(reach * reach + aim.z * aim.z);
            float gripLength = Mth.sqrt(Mth.square(gripY) + Mth.square(gripZ));
            float shaftLength = CANE_REACH;
            if (footLength > Math.abs(gripLength - shaftLength) && footLength < gripLength + shaftLength) {
                float bend = (float)Math.acos(Mth.clamp((footLength * footLength + gripLength * gripLength - shaftLength * shaftLength)
                        / (2F * footLength * gripLength), -1F, 1F));
                float roll = (float)Math.atan2(gripX, reach) - (float)Math.atan2(aim.x, aim.y);
                float pitch = (float)Math.atan2(aim.z, reach) - bend - (float)Math.atan2(gripZ, gripY);
                float sin = Mth.sin(pitch), cos = Mth.cos(pitch);
                float y = gripY * cos - gripZ * sin - reach;
                float z = gripY * sin + gripZ * cos - aim.z;
                hand.gripShift = Mth.lerp(weight, hand.gripShift, hand.caneShift);
                hand.caneWeight = Mth.lerp(weight, hand.caneWeight, 1F);
                hand.wing.x = Mth.lerp(weight, hand.wing.x, shoulder.x);
                hand.wing.y = Mth.lerp(weight, hand.wing.y, shoulderY);
                hand.wing.z = Mth.lerp(weight, hand.wing.z, shoulder.z);
                hand.wing.xRot = Mth.lerp(weight, hand.wing.xRot, pitch);
                hand.wing.yRot *= 1F - weight;
                hand.wing.zRot = Mth.lerp(weight, hand.wing.zRot, roll);
                aim.set(0F, y * cos + z * sin, -y * sin + z * cos).normalize();
                hand.caneAim.lerp(aim, weight).normalize();
            }
        }
    }

    private void poseSwimming(float ageInTicks, float swimWeight, float diveBlend, int swimAction, float actionPhase, float landBlend) {
        long animationTime = (long)(ageInTicks * 50F);
        float diveWeight = swimWeight * diveBlend;
        KeyframeAnimations.animate(this, paddle, animationTime, swimWeight * (1F - diveBlend), scratch);
        KeyframeAnimations.animate(this, swimClip, animationTime, diveWeight, scratch);
        rootPart.y += (FLOOR_Y - rootPart.getInitialPose().y) * diveWeight;
        tail.xRot += (tailPitchBias - 2.35F) * diveWeight;
        float actionPulse = Mth.sin(Mth.PI * actionPhase);
        float leapWeight = AnimationUtil.smootherStep(AnimationUtil.progress(actionPhase, 0F, 0.1F))
                * (1F - AnimationUtil.smootherStep(AnimationUtil.progress(actionPhase, 0.8F, 1F))) * swimWeight;
        if (swimAction == PinginEntity.SHAKE) {
            actionPulse = 1F;
            leapWeight = (1F - AnimationUtil.smootherStep(actionPhase * (PinginEntity.LAND_TICKS + PinginEntity.SHAKE_TICKS)
                    / PinginEntity.LAND_TICKS)) * swimWeight;
        }
        float brakeWeight = swimAction == PinginEntity.LEAP ? landBlend : swimAction == PinginEntity.SHAKE
                ? 1F - AnimationUtil.smootherStep(actionPhase * (PinginEntity.LAND_TICKS + PinginEntity.SHAKE_TICKS)
                        / (PinginEntity.LAND_TICKS * 0.2F)) : 0F;
        float brakeFlap = Mth.sin(ageInTicks * 1.2F) * 0.7F * brakeWeight;
        float diveTuck = (1F - AnimationUtil.smootherStep(AnimationUtil.progress(actionPhase, 0.2F, 0.6F))) * swimWeight;
        for (HandPose hand : hands) {
            if (hand.model != null && !hand.holdsFish) {
                hand.wing.xRot *= 1F - swimWeight;
                hand.wing.yRot *= 1F - swimWeight;
                hand.wing.zRot *= 1F - swimWeight;
            } else {
                hand.wing.y += 0.8F * hand.depth() / 6F * swimWeight;
                if (swimAction == PinginEntity.DIVE) {
                    hand.wing.zRot = Mth.lerp(diveTuck, hand.wing.zRot, -hand.side * 0.25F);
                    hand.wing.xRot -= 0.3F * diveTuck;
                } else if (swimAction == PinginEntity.LEAP || swimAction == PinginEntity.SHAKE) {
                    hand.wing.zRot = Mth.lerp(leapWeight, hand.wing.zRot, -hand.side * (0.3F + actionPulse * 1.1F));
                    hand.wing.xRot -= 0.35F * (1F - actionPulse) * leapWeight;
                }
            }
            hand.wing.zRot -= hand.side * brakeFlap;
        }
        if (swimAction == PinginEntity.CATCH) {
            head.xRot += Mth.sin(actionPhase * Mth.TWO_PI) * 0.1F * actionPulse;
            body.z -= 0.4F * actionPulse * motionScale.y;
            tail.xRot += 0.1F * actionPulse;
        } else if (swimAction == PinginEntity.LEAP) {
            KeyframeAnimations.animate(this, landClip, 0L, landBlend, scratch);
        } else if (swimAction == PinginEntity.SHAKE) {
            float shoreTime = actionPhase * (PinginEntity.LAND_TICKS + PinginEntity.SHAKE_TICKS) * 50F;
            float landTime = PinginEntity.LAND_TICKS * 50F;
            KeyframeAnimations.animate(this, landClip, (long)Math.min(shoreTime, landTime), 1F - swimWeight, scratch);
            KeyframeAnimations.animate(this, shakeClip, (long)Math.max(0F, shoreTime - landTime), 1F - swimWeight, scratch);
        }
    }

    private void jabSpear(HandPose hand, float time, float reach, PinginAttack attack) {
        float windup = ThrowingSpearAnimations.windup(time), thrust = ThrowingSpearAnimations.thrust(time);
        float load = windup * attack.power, push = thrust * attack.power, turn = hand.side * attack.twist;
        float stroke = attack.sweep == 0F ? 1F : 0.3F;
        float recoil = AnimationUtil.inOutSine(AnimationUtil.progress(time, 0.35F, 0.55F))
                - AnimationUtil.inOutSine(AnimationUtil.progress(time, 0.55F, 0.85F));
        hand.aimWeight = AnimationUtil.inOutSine(AnimationUtil.progress(time, 0F, 0.05F))
                - AnimationUtil.inOutSine(AnimationUtil.progress(time, 0.85F, 1F));
        hand.aimTurn = attack.sweep == 0F ? 1F : 0F;
        float rise = attack.lift * windup + (attack.level - attack.lift) * thrust;
        hand.aimHeight = rootPart.getInitialPose().y + head.getInitialPose().y + (2F - rise) * motionScale.y;
        hand.weaponAngle = attack.slope * (windup + thrust) * 0.5F;
        hand.weaponAngle += attack.sweep * (0.35F * windup - 1.35F * thrust);
        float lunge = 0.75F * load - Mth.lerp(reach, 1.2F, 2.4F) * push * stroke;
        rootPart.z += lunge + 0.15F * recoil;
        rootPart.yRot += turn * (-0.30F * load + 0.46F * push);
        body.xRot += -0.14F * load + 0.26F * push - 0.05F * recoil;
        body.xRot += 0.025F * attack.lift * (thrust - windup);
        body.yRot += turn * (0.42F * load - 0.62F * push);
        body.yRot += hand.side * attack.sweep * (1.2F * windup - 2.4F * thrust);
        body.zRot += turn * (-0.05F * load + 0.09F * push);
        body.y -= 0.15F * load + 0.1F * push;
        body.z += 0.35F * load - 0.65F * push;
        float focus = AnimationUtil.inOutSine(AnimationUtil.progress(time, 0F, 0.025F))
                - AnimationUtil.inOutSine(AnimationUtil.progress(time, 0.85F, 1F));
        head.xRot = head.xRot * (1F - focus) - body.xRot * 0.5F;
        head.yRot = head.yRot * (1F - focus) - (rootPart.yRot + body.yRot) * 0.8F * hand.aimTurn;
        hand.wing.xRot += 0.4F * load - 0.5F * push;
        hand.wing.xRot += attack.lift * (-0.24F * windup + 0.12F * thrust);
        hand.wing.zRot *= 1F - hand.aimWeight;
        bounds(hand.wing, wingFrame(hand, hand.wing.xRot, 0F, partTransform), partBounds, false);
        float back = torso[MAX_Z] - partBounds[MAX_Z];
        float extension = (Mth.lerp(reach, 2.75F, 4.75F) - 0.2F * attack.lift) * Math.min(1F, attack.power);
        hand.wing.z = Mth.lerp(windup, hand.wing.z, back) - extension * thrust * stroke;
        hand.gripShift = -0.5F * (1F - reach) * hand.aimWeight + reach * thrust;
        if (attack.sweep != 0F) {
            hand.gripShift = Mth.lerp(reach, 0.35F, 0.7F) * hand.aimWeight;
        }
        int arm = hand.side == 1 ? 0 : 1, lead = attack.twist < 0F ? 1 - arm : arm, trail = 1 - lead;
        feet[lead].z += 0.4F * load - Mth.lerp(reach, 1.2F, 2F) * push;
        feet[lead].y -= 0.4F * windup * (1F - thrust);
        feet[trail].z += -0.2F * load + 0.35F * push - lunge;
        feet[lead].yRot += turn * (0.12F * load - 0.2F * push);
        HandPose other = hands[1 - arm];
        other.wing.xRot += -0.3F * load + 0.55F * push + 0.12F * recoil;
        other.wing.zRot += hand.side * (0.12F * load + 0.28F * push);
        other.wing.z += 0.6F * load - push;
        tail.xRot += 0.12F * load - 0.18F * push + 0.07F * recoil;
        tail.yRot += turn * (-0.18F * load + 0.3F * push + 0.18F * recoil);
    }

    private void throwSpear(HandPose hand, float time, PinginAttack attack) {
        float hit = attack.hitTick / 20F;
        float draw = AnimationUtil.inOutSine(AnimationUtil.progress(time, 0F, hit * 0.5F));
        float hip = AnimationUtil.inOutSine(AnimationUtil.progress(time, hit * 0.5F, hit * 0.82F));
        float drive = AnimationUtil.inQuad(AnimationUtil.progress(time, hit * 0.72F, hit));
        float cast = AnimationUtil.inQuad(AnimationUtil.progress(time, hit * 0.82F, hit));
        float recover = AnimationUtil.smootherStep(AnimationUtil.progress(time, hit + 0.12F, attack.seconds()));
        float follow = AnimationUtil.inOutSine(AnimationUtil.progress(time, hit, hit + 0.35F));
        float weight = 1F - recover, load = draw * weight, shift = hip * weight, push = drive * weight, settle = follow * weight;
        float focus = AnimationUtil.inOutSine(AnimationUtil.progress(time, 0F, hit * 0.25F)) * weight;
        float stride = AnimationUtil.progress(time, hit * 0.4F, hit * 0.7F);
        float step = AnimationUtil.inOutSine(stride) * weight, lift = Mth.sin(Mth.PI * stride) * weight;
        float shiftX = hand.side * (0.45F * load - 0.65F * shift), shiftZ = 1.2F * load - 4.2F * shift;
        hand.aimWeight = load;
        hand.aimTurn = 1F;
        hand.gripShift = 0F;
        rootPart.x += shiftX;
        rootPart.z += shiftZ;
        rootPart.yRot -= hand.side * (0.25F * load - 0.45F * shift);
        body.xRot += -0.28F * load + 0.62F * push + 0.15F * settle;
        body.yRot -= hand.side * (0.5F * load - 0.3F * push);
        body.zRot += hand.side * (-0.05F * load + 0.07F * push);
        body.y -= 0.4F * load + 0.3F * push + 0.15F * settle;
        body.z += 0.4F * load - 0.7F * push;
        head.xRot = head.xRot * (1F - focus) - body.xRot * 0.3F;
        head.yRot = head.yRot * (1F - focus) - (rootPart.yRot + body.yRot) * focus;
        hand.wing.xRot = Mth.lerp(load, hand.wing.xRot, Mth.lerp(cast, -3.85F, -3.8F) + 1.65F * follow);
        hand.weaponAngle = (-0.1F * draw - 0.2F * cast - 0.06F * (hip - drive)) * weight;
        hand.wing.y = Mth.lerp(load, hand.wing.y, hand.wing.getInitialPose().y + 0.1F * cast);
        hand.wing.z = Mth.lerp(load, hand.wing.z, hand.wing.getInitialPose().z - 1.25F - 0.7F * cast);
        hand.wing.zRot = hand.wing.zRot * (1F - load) + hand.side * 0.28F * load * (1F - cast);
        int arm = hand.side == 1 ? 0 : 1, lead = 1 - arm;
        feet[lead].x -= shiftX + hand.side * 0.35F * load;
        feet[arm].x += hand.side * 0.35F * load - shiftX;
        feet[lead].z -= shiftZ + 2.8F * step;
        feet[arm].z += 0.4F * load - shiftZ - 2F * settle;
        feet[lead].y -= 0.6F * lift;
        feet[arm].y -= 0.65F * Mth.sin(Mth.PI * follow) * weight;
        toes[lead].xRot += 0.16F * lift;
        feet[lead].yRot += hand.side * (0.2F * load - 0.3F * push);
        partTransform.identity().rotateY(-rootPart.yRot);
        for (ModelPart foot : feet) {
            partTransform.transformPosition(scratch.set(foot.x, foot.y, foot.z));
            foot.setPos(scratch.x, scratch.y, scratch.z);
            foot.yRot -= rootPart.yRot;
        }
        HandPose other = hands[lead];
        other.wing.xRot += -0.85F * load + 1.25F * push;
        other.wing.zRot += hand.side * (0.25F * load + 0.2F * push);
        other.wing.z += 0.2F * load - 0.5F * push;
        other.wing.y += 1.5F * load;
        tail.xRot += 0.2F * load - 0.3F * push + 0.1F * settle;
        tail.yRot += hand.side * (-0.25F * load + 0.3F * push);
    }

    private void configureGrip(ItemStack stack, BakedModel model, HandPose hand) {
        hand.holdsFish = stack.is(ItemTags.FISHES);
        if (stack.isEmpty()) {
            hand.model = null;
            hand.profile = ITEM;
        } else {
            boolean block = stack.getItem() instanceof BlockItem;
            if (model != hand.model || block != (hand.profile == BLOCK)) {
                ItemDisplayContext context = hand.side == 1 ? ItemDisplayContext.THIRD_PERSON_LEFT_HAND : ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
                ItemTransform display = model.applyTransform(context, new PoseStack(), hand.side == 1).getTransforms().getTransform(context);
                boolean handheld = Mth.equal(Math.abs(display.rotation.y()), 90F) && Mth.equal(Math.abs(display.rotation.z()), 55F);
                boolean spear = stack.getItem() instanceof ThrowingSpearItem;
                hand.profile = block ? BLOCK : spear ? SPEAR : handheld ? HANDHELD : ITEM;
                float mirror = -hand.side, toRadians = Mth.DEG_TO_RAD;
                partTransform.identity().rotateX(-Mth.HALF_PI).rotateY(Mth.PI).translate(mirror, 2F, -10F)
                        .translate(display.translation.x * 16F * mirror, display.translation.y * 16F, display.translation.z * 16F)
                        .rotateXYZ(display.rotation.x * toRadians, display.rotation.y * toRadians * mirror, display.rotation.z * toRadians * mirror).scale(display.scale);
                partTransform.transformDirection(hand.swingAxis.set(0F, 0F, 1F)).normalize();
                partTransform.transformDirection(hand.pointAxis.set(-1F, 1F, 0F)).normalize();
                hand.gripPlane.rotationTo(hand.swingAxis, aim.set(1F, 0F, 0F));
                hand.gripSlide.zero();
                if (block) {
                    findBlockCenter(model, partTransform, hand.gripPivot);
                    hand.gripPivot.mul(hand.profile.scale);
                    hand.itemCenter.set(hand.gripPivot);
                } else {
                    partTransform.transformPosition(hand.gripPivot.set(spear ? 2.5F : handheld ? -4.5F : 0F,
                            spear ? -2.5F : handheld ? -4.5F : 0F, 0F)).mul(hand.profile.scale);
                    partTransform.transformPosition(hand.itemCenter.zero()).mul(hand.profile.scale);
                }
                if (spear) {
                    partTransform.transformDirection(hand.gripSlide.set(3.5F, -3.5F, 0F));
                    partTransform.transformPosition(scratch.set(7.5F, -7.5F, 0F));
                    hand.caneShift = (hand.gripPivot.distance(scratch) - CANE_REACH) / hand.gripSlide.length();
                }
                hand.model = model;
            }
        }
    }

    private void findBlockCenter(BakedModel model, Matrix4f transform, Vector3f result) {
        for (int axis = 0; axis < 3; axis++) {
            partBounds[axis] = Float.POSITIVE_INFINITY;
            partBounds[axis + 3] = Float.NEGATIVE_INFINITY;
        }
        for (int face = -1; face < 6; face++) {
            gripRandom.setSeed(42L);
            for (BakedQuad quad : model.getQuads(null, face < 0 ? null : Direction.values()[face], gripRandom)) {
                int[] vertices = quad.getVertices();
                int stride = vertices.length / 4;
                for (int i = 0; i < vertices.length; i += stride) {
                    scratch.set(Float.intBitsToFloat(vertices[i]) * 16F - 8F,
                            Float.intBitsToFloat(vertices[i + 1]) * 16F - 8F, Float.intBitsToFloat(vertices[i + 2]) * 16F - 8F);
                    transform.transformPosition(scratch);
                    for (int axis = 0; axis < 3; axis++) {
                        partBounds[axis] = Math.min(partBounds[axis], scratch.get(axis));
                        partBounds[axis + 3] = Math.max(partBounds[axis + 3], scratch.get(axis));
                    }
                }
            }
        }
        if (Float.isFinite(partBounds[MIN_X])) {
            result.set((partBounds[MIN_X] + partBounds[MAX_X]) / 2F, (partBounds[MIN_Y] + partBounds[MAX_Y]) / 2F, (partBounds[MIN_Z] + partBounds[MAX_Z]) / 2F);
        } else {
            transform.transformPosition(result.zero());
        }
    }

    private void admireItem(HandPose mainHand, float time, float weight) {
        ModelPart wing = mainHand.wing;
        HoldProfile profile = mainHand.profile == HANDHELD ? ITEM : mainHand.profile;
        wing.xRot = Mth.lerp(weight, wing.xRot, -mainHand.carryPitch(profile.pitch) + Mth.sin(time * 0.12F) * 0.03F);
        wing.yRot *= 1F - weight;
        wing.zRot = Mth.lerp(weight, wing.zRot, -mainHand.side * mainHand.profile.flare);
        wing.y = Mth.lerp(weight, wing.y, wing.getInitialPose().y + profile.drop * mainHand.depth() / 6F);
        wing.z = Mth.lerp(weight, wing.z, wing.getInitialPose().z + profile.back * mainHand.depth() / 6F);
        HandPose otherHand = hands[mainHand.side == 1 ? 1 : 0];
        otherHand.wing.zRot = Mth.lerp(weight, otherHand.wing.zRot, -otherHand.side * 0.25F);
        body.zRot += mainHand.side * 0.035F * weight * motionScale.y * motionScale.y;
        head.xRot *= 1F - weight;
        head.zRot *= 1F - weight;
    }

    private void aimAtItem(HandPose hand, float admireWeight) {
        hand.grip(scratch).negate().add(hand.itemCenter);
        heldItemRotation(hand).transform(scratch);
        scratch.add(hand.gripX(), hand.gripY(), hand.gripZ());
        transform(hand.wing, partTransform).transformPosition(scratch);
        float itemOffsetX = scratch.x - head.x, itemOffsetZ = scratch.z - head.z;
        float lookYaw = Mth.clamp((float)Math.atan2(-itemOffsetX, -itemOffsetZ), -1.4F, 1.4F);

        head.yRot = Mth.lerp(admireWeight, head.yRot, lookYaw);
        head.zRot *= 1F - admireWeight;
    }

    private static Matrix4f transform(ModelPart part, Matrix4f result) {
        return result.identity().translate(part.x, part.y, part.z)
                .rotateZYX(part.zRot, part.yRot, part.xRot);
    }

    private void boxBounds(Matrix4f matrix, float x, float y, float z, float width, float height, float depth,
                           float[] bounds, boolean append) {
        if (!append) {
            for (int i = 0; i < 3; i++) {
                bounds[i] = Float.POSITIVE_INFINITY;
                bounds[i + 3] = Float.NEGATIVE_INFINITY;
            }
        }
        for (int i = 0; i < 8; i++) {
            scratch.set(x + ((i & 1) == 0 ? 0F : width), y + ((i & 2) == 0 ? 0F : height),
                    z + ((i & 4) == 0 ? 0F : depth));
            matrix.transformPosition(scratch);
            for (int axis = 0; axis < 3; axis++) {
                bounds[axis] = Math.min(bounds[axis], scratch.get(axis));
                bounds[axis + 3] = Math.max(bounds[axis + 3], scratch.get(axis));
            }
        }
    }

    private void bounds(ModelPart part, Matrix4f matrix, float[] result, boolean append) {
        for (ModelPart.Cube cube : geometry.get(part)) {
            boxBounds(matrix, cube.minX, cube.minY, cube.minZ, cube.maxX - cube.minX, cube.maxY - cube.minY, cube.maxZ - cube.minZ, result, append);
            append = true;
        }
    }

    private void placeHead(float pitchBlend) {
        ModelPart.Cube skull = geometry.get(head).get(0);
        float tilt = Mth.HALF_PI * AnimationUtil.smootherStep(Mth.clamp((rootPart.xRot - Mth.PI / 6F) / (Mth.PI / 3F), 0F, 1F));
        float center = (torso[MIN_Z] + torso[MAX_Z] + skull.minY + skull.maxY) * 0.5F;
        head.xRot -= Mth.lerp(pitchBlend, tilt, rootPart.xRot);
        head.y -= skull.maxZ * Mth.sin(tilt);
        head.y += 0.6F * motionScale.y * Mth.sin(2F * tilt);
        head.z += (center - head.getInitialPose().z + body.getInitialPose().z) * (1F - Mth.cos(tilt));
    }

    private void poseWing(HandPose hand) {
        ModelPart wing = hand.wing;
        float flare = -hand.side * wing.zRot;
        rotateWing(hand, wing.xRot, flare);
        if (hand.aimHeight > 0F && hand.aimWeight > 0F) {
            transform(rootPart, worldTransform).mul(transform(body, bodyTransform));
            float up = worldTransform.m11();
            worldTransform.mul(transform(wing, partTransform)).transformPosition(scratch.set(hand.gripX(), hand.gripY(), hand.gripZ()));
            wing.y += (hand.aimHeight - scratch.y) * hand.aimWeight / up;
        }
    }

    private void rotateWing(HandPose hand, float pitch, float flare) {
        ModelPart wing = hand.wing;
        Matrix4f frame = wingFrame(hand, pitch, flare, partTransform);
        wing.xRot = (float)Math.atan2(frame.m12(), frame.m22());
        wing.yRot = (float)Math.asin(Mth.clamp(-frame.m02(), -1F, 1F));
        wing.zRot = (float)Math.atan2(frame.m01(), frame.m00());
    }

    private Matrix4f wingFrame(HandPose hand, float pitch, float flare, Matrix4f result) {
        if (hand.profile == SPEAR && hand.aimWeight > 0F && hand.aimTurn > 0F && hand.aimHeight == 0F) {
            return result.identity().rotateZYX(rootPart.zRot, rootPart.yRot, rootPart.xRot)
                    .rotateZYX(body.zRot, body.yRot, body.xRot).invert()
                    .rotateZ(rootPart.zRot + body.zRot - hand.side * flare)
                    .rotateY((rootPart.yRot + body.yRot) * (1F - hand.aimTurn))
                    .rotateX(rootPart.xRot + body.xRot + pitch);
        } else {
            result.identity().rotateX(pitch).rotateZ(-hand.side * flare);
            if (hand.profile == SPEAR && hand.aimWeight > 0F) {
                float x = (float)Math.atan2(result.m12(), result.m22());
                float y = (float)Math.asin(Mth.clamp(-result.m02(), -1F, 1F));
                float z = (float)Math.atan2(result.m01(), result.m00());
                result.identity().rotateZYX(z, y - (rootPart.yRot + body.yRot) * hand.aimTurn, x);
            }
            return result;
        }
    }

    private void glideWing(HandPose hand, float weight) {
        ModelPart wing = hand.wing;
        if (hand.profile == SPEAR) {
            worldTransform.identity().rotateZYX(rootPart.zRot, rootPart.yRot, rootPart.xRot)
                    .rotateZYX(body.zRot, body.yRot, body.xRot).invert()
                    .rotateZ(-hand.side * Mth.HALF_PI * weight).rotateX(wing.xRot * (1F - weight));
            wing.xRot = (float)Math.atan2(worldTransform.m12(), worldTransform.m22());
            wing.yRot = (float)Math.asin(Mth.clamp(-worldTransform.m02(), -1F, 1F));
            wing.zRot = (float)Math.atan2(worldTransform.m01(), worldTransform.m00());
        } else {
            wing.xRot *= 1F - weight;
            wing.yRot = Mth.lerp(weight, wing.yRot, -hand.side * rootPart.xRot);
            wing.zRot = Mth.lerp(weight, wing.zRot, -hand.side * Mth.HALF_PI);
        }
        wing.y = Mth.lerp(weight, wing.y, wing.getInitialPose().y);
        wing.z = Mth.lerp(weight, wing.z, wing.getInitialPose().z);
        bounds(wing, transform(wing, partTransform), partBounds, false);
        wing.y += Math.max(0F, headBounds[MAX_Y] - partBounds[MIN_Y]) * weight;
    }

    private float glideWeight() {
        return AnimationUtil.smootherStep(Mth.clamp(rootPart.xRot / Mth.HALF_PI, 0F, 1F));
    }

    private void finishPose(float slideWeight, HandPose mainHand, float admire, float swimWeight) {
        placeHead(slideWeight > 0F ? swimWeight : 1F);
        float glide = slideWeight > 0F ? glideWeight() * (1F - swimWeight) : 0F;
        if (slideWeight > 0F || glide > 0F) {
            bounds(head, transform(head, partTransform), headBounds, false);
        }
        for (HandPose hand : hands) {
            poseWing(hand);
            float weight = hand.profile == SPEAR ? slideWeight : glide;
            if (weight > 0F) {
                glideWing(hand, weight);
            }
        }
        if (admire > 0F) {
            aimAtItem(mainHand, admire);
        }
        if (glide > 0F) {
            transform(rootPart, worldTransform).mul(transform(body, bodyTransform));
            bounds(body, worldTransform, partBounds, false);
            rootPart.y += (FLOOR_Y - partBounds[MAX_Y]) * glide;
        }
    }

    private void composeHeadPose() {
        transform(body, bodyTransform).mul(transform(head, partTransform));
        head.x = bodyTransform.m30();
        head.y = bodyTransform.m31();
        head.z = bodyTransform.m32();
        head.xRot = (float)Math.atan2(bodyTransform.m12(), bodyTransform.m22());
        head.yRot = (float)Math.asin(Mth.clamp(-bodyTransform.m02(), -1F, 1F));
        head.zRot = (float)Math.atan2(bodyTransform.m01(), bodyTransform.m00());
    }

    public static float slideBounce(float time) {
        float startTime, duration, height;
        if (time >= 1.82F && time < 2.14F) {
            startTime = 1.82F; duration = 0.32F; height = 2.3F;
        } else if (time >= 2.14F && time < 2.34F) {
            startTime = 2.14F; duration = 0.2F; height = 0.4F;
        } else {
            return 0F;
        }
        float progress = (time - startTime) / duration;
        return height * 4F * progress * (1F - progress);
    }

}

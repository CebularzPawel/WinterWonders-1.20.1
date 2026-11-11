package net.cebularz.winterwonders.entity.animations;

import static net.cebularz.winterwonders.entity.animations.AnimationUtil.easePos;
import static net.cebularz.winterwonders.entity.animations.AnimationUtil.easeRot;
import static net.cebularz.winterwonders.entity.animations.AnimationUtil.flowRot;
import static net.cebularz.winterwonders.entity.animations.AnimationUtil.loopPosition;
import static net.cebularz.winterwonders.entity.animations.AnimationUtil.loopRotation;
import static net.cebularz.winterwonders.entity.animations.AnimationUtil.mirror;
import static net.cebularz.winterwonders.entity.animations.AnimationUtil.pos;
import static net.cebularz.winterwonders.entity.animations.AnimationUtil.position;
import static net.cebularz.winterwonders.entity.animations.AnimationUtil.rot;
import static net.cebularz.winterwonders.entity.animations.AnimationUtil.rotation;
import static net.cebularz.winterwonders.entity.animations.AnimationUtil.scaleValues;
import static net.cebularz.winterwonders.entity.animations.AnimationUtil.flowTrack;

import java.util.EnumMap;
import java.util.Map;

import net.cebularz.winterwonders.entity.custom.PinginAttack;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;

import org.joml.Vector3f;

public final class PinginAnimations {
    private PinginAnimations() {}

    public static final AnimationDefinition IDLE = AnimationDefinition.Builder.withLength(3.2F).looping()
            .addAnimation("body", loopPosition(3.2F,
                    pos(0F, 0F, 0F, 0F),
                    pos(0.8F, 0F, 0.12F, 0F),
                    pos(1.6F, 0F, 0F, 0F),
                    pos(2.4F, 0F, -0.04F, 0F),
                    pos(3.2F, 0F, 0F, 0F)))
            .addAnimation("body", loopRotation(3.2F,
                    rot(0F, 0F, 0F, 0F),
                    rot(0.8F, 0F, 0F, 0.5F),
                    rot(1.6F, 0F, 0F, 0F),
                    rot(2.4F, 0F, 0F, -0.5F),
                    rot(3.2F, 0F, 0F, 0F)))
            .addAnimation("head", loopRotation(3.2F,
                    rot(0F, 0F, -1.5F, 0F),
                    rot(0.8F, 0F, 0F, 0F),
                    rot(1.6F, 0F, 1.5F, 0F),
                    rot(2.4F, 0F, 0F, 0F),
                    rot(3.2F, 0F, -1.5F, 0F)))
            .addAnimation("left_arm", loopRotation(3.2F,
                    rot(0F, 0F, 0F, -6F),
                    rot(0.8F, -0.5F, 0F, -8F),
                    rot(1.6F, 0F, 0F, -6F),
                    rot(2.4F, 0.5F, 0F, -5F),
                    rot(3.2F, 0F, 0F, -6F)))
            .addAnimation("right_arm", loopRotation(3.2F,
                    rot(0F, 0F, 0F, 6F),
                    rot(0.8F, -0.5F, 0F, 7F),
                    rot(1.6F, 0F, 0F, 6F),
                    rot(2.4F, 0.5F, 0F, 5F),
                    rot(3.2F, 0F, 0F, 6F)))
            .addAnimation("tail", loopRotation(3.2F,
                    rot(0F, -2F, -3F, 0F),
                    rot(0.8F, 1F, -1F, 0F),
                    rot(1.6F, 3F, 3F, 0F),
                    rot(2.4F, 0F, 1F, 0F),
                    rot(3.2F, -2F, -3F, 0F)))
            .addAnimation("left_foot", rotation(
                    rot(0F, 0F, -16F, 0F)))
            .addAnimation("right_foot", rotation(
                    rot(0F, 0F, 16F, 0F)))
            .build();

    public static final AnimationDefinition WALKING = AnimationDefinition.Builder.withLength(1.04F).looping()
            .addAnimation("body", loopPosition(1.04F,
                    pos(0F, 0.6F, 0.28F, 0F),
                    pos(0.26F, 0F, 0.08F, 0F),
                    pos(0.52F, -0.6F, 0.28F, 0F),
                    pos(0.78F, 0F, 0.08F, 0F),
                    pos(1.04F, 0.6F, 0.28F, 0F)))
            .addAnimation("body", loopRotation(1.04F,
                    rot(0F, 0F, -2F, 10F),
                    rot(0.26F, 0F, 0F, 0F),
                    rot(0.52F, 0F, 2F, -10F),
                    rot(0.78F, 0F, 0F, 0F),
                    rot(1.04F, 0F, -2F, 10F)))
            .addAnimation("head", loopRotation(1.04F,
                    rot(0F, 0F, 1F, -1.5F),
                    rot(0.26F, 0F, 0F, 0F),
                    rot(0.52F, 0F, -1F, 1.5F),
                    rot(0.78F, 0F, 0F, 0F),
                    rot(1.04F, 0F, 1F, -1.5F)))
            .addAnimation("left_foot", loopPosition(1.04F,
                    pos(0F, 0F, 0F, -0.45F),
                    pos(0.13F, 0F, 0F, -0.225F),
                    pos(0.26F, 0F, 0F, 0F),
                    pos(0.39F, 0F, 0F, 0.225F),
                    pos(0.52F, 0F, 0F, 0.45F),
                    pos(0.65F, 0.15F, 0.28F, 0.1F),
                    pos(0.78F, 0.2F, 0.38F, -0.4F),
                    pos(0.91F, 0.1F, 0.12F, -0.6F),
                    pos(1.04F, 0F, 0F, -0.45F)))
            .addAnimation("right_foot", loopPosition(1.04F,
                    pos(0F, 0F, 0F, 0.45F),
                    pos(0.13F, -0.15F, 0.28F, 0.1F),
                    pos(0.26F, -0.2F, 0.38F, -0.4F),
                    pos(0.39F, -0.1F, 0.12F, -0.6F),
                    pos(0.52F, 0F, 0F, -0.45F),
                    pos(0.65F, 0F, 0F, -0.225F),
                    pos(0.78F, 0F, 0F, 0F),
                    pos(0.91F, 0F, 0F, 0.225F),
                    pos(1.04F, 0F, 0F, 0.45F)))
            .addAnimation("left_foot", loopRotation(1.04F,
                    rot(0F, 0F, -17F, 0F),
                    rot(0.13F, 0F, -17F, 0F),
                    rot(0.26F, 0F, -17F, 0F),
                    rot(0.39F, 2F, -17F, 0F),
                    rot(0.52F, 4F, -17F, 0F),
                    rot(0.65F, -5F, -12F, 0F),
                    rot(0.78F, -4F, -14F, 0F),
                    rot(0.91F, -1F, -17F, 0F),
                    rot(1.04F, 0F, -17F, 0F)))
            .addAnimation("right_foot", loopRotation(1.04F,
                    rot(0F, 4F, 17F, 0F),
                    rot(0.13F, -5F, 12F, 0F),
                    rot(0.26F, -4F, 14F, 0F),
                    rot(0.39F, -1F, 17F, 0F),
                    rot(0.52F, 0F, 17F, 0F),
                    rot(0.65F, 0F, 17F, 0F),
                    rot(0.78F, 0F, 17F, 0F),
                    rot(0.91F, 2F, 17F, 0F),
                    rot(1.04F, 4F, 17F, 0F)))
            .addAnimation("left_arm", loopRotation(1.04F,
                    rot(0F, 0F, 0F, -10F),
                    rot(0.26F, -2F, 0F, -19F),
                    rot(0.52F, 0F, 0F, -24F),
                    rot(0.78F, 2F, 0F, -15F),
                    rot(1.04F, 0F, 0F, -10F)))
            .addAnimation("right_arm", loopRotation(1.04F,
                    rot(0F, 0F, 0F, 24F),
                    rot(0.26F, 2F, 0F, 15F),
                    rot(0.52F, 0F, 0F, 10F),
                    rot(0.78F, -2F, 0F, 19F),
                    rot(1.04F, 0F, 0F, 24F)))
            .addAnimation("tail", loopRotation(1.04F,
                    rot(0F, -3F, 12F, -2F),
                    rot(0.26F, 3F, 7F, -1F),
                    rot(0.52F, -3F, -12F, 2F),
                    rot(0.78F, 3F, -7F, 1F),
                    rot(1.04F, -3F, 12F, -2F)))
            .build();

    public static final AnimationDefinition SLIDE = AnimationDefinition.Builder.withLength(2.4F)
            .addAnimation("root", position(
                    easePos(0F, 0F, 0F, 0F),
                    easePos(0.18F, 0F, 0F, 0.2F),
                    easePos(0.4F, 0F, 1.9F, -0.4F),
                    easePos(0.56F, 0F, 3F, -0.6F),
                    easePos(1.4F, 0F, 3F, -0.6F),
                    easePos(1.65F, 0F, 1.8F, -0.2F),
                    easePos(1.82F, 0F, 0F, 0F),
                    easePos(1.98F, 0F, 0F, 0F),
                    easePos(2.14F, 0F, 0F, 0F),
                    easePos(2.26F, 0F, 0F, 0F),
                    easePos(2.4F, 0F, 0F, 0F)))
            .addAnimation("root", rotation(
                    easeRot(0F, 0F, 0F, 0F),
                    easeRot(0.18F, -8F, 0F, 0F),
                    easeRot(0.4F, 48F, 0F, 0F),
                    easeRot(0.56F, 90F, 0F, 0F),
                    easeRot(1.4F, 90F, 0F, 0F),
                    easeRot(1.65F, 42F, 0F, 0F),
                    easeRot(1.82F, -8F, 0F, 0F),
                    easeRot(1.98F, 2F, 0F, 0F),
                    easeRot(2.14F, 5F, 0F, 0F),
                    easeRot(2.26F, -2F, 0F, 0F),
                    easeRot(2.4F, 0F, 0F, 0F)))
            .addAnimation("body", position(
                    easePos(0F, 0F, 0F, 0F),
                    easePos(0.18F, 0F, -0.4F, 0F),
                    easePos(0.4F, 0F, 0F, 0F),
                    easePos(0.56F, 0F, 0F, 0F),
                    easePos(1.4F, 0F, 0F, 0F),
                    easePos(1.65F, 0F, -0.1F, 0F),
                    easePos(1.82F, 0F, -0.35F, 0F),
                    easePos(1.98F, 0F, 0.1F, 0F),
                    easePos(2.14F, 0F, -0.3F, 0F),
                    easePos(2.26F, 0F, 0F, 0F),
                    easePos(2.4F, 0F, 0F, 0F)))
            .addAnimation("left_arm", rotation(
                    easeRot(0F, 0F, 0F, -6F),
                    easeRot(0.18F, -8F, 0F, -20F),
                    easeRot(0.4F, 0F, 0F, -24F),
                    easeRot(0.56F, 0F, 0F, -14F),
                    easeRot(1.4F, 0F, 0F, -14F),
                    easeRot(1.65F, -5F, 0F, -25F),
                    easeRot(1.82F, 0F, 0F, -18F),
                    easeRot(1.98F, -4F, 0F, -30F),
                    easeRot(2.14F, 0F, 0F, -22F),
                    easeRot(2.26F, 0F, 0F, -12F),
                    easeRot(2.4F, 0F, 0F, -6F)))
            .addAnimation("right_arm", rotation(
                    easeRot(0F, 0F, 0F, 6F),
                    easeRot(0.18F, -8F, 0F, 20F),
                    easeRot(0.4F, 0F, 0F, 24F),
                    easeRot(0.56F, 0F, 0F, 14F),
                    easeRot(1.4F, 0F, 0F, 14F),
                    easeRot(1.65F, -5F, 0F, 25F),
                    easeRot(1.82F, 0F, 0F, 18F),
                    easeRot(1.98F, -4F, 0F, 30F),
                    easeRot(2.14F, 0F, 0F, 22F),
                    easeRot(2.26F, 0F, 0F, 12F),
                    easeRot(2.4F, 0F, 0F, 6F)))
            .addAnimation("tail", rotation(
                    easeRot(0F, 0F, 0F, 0F),
                    easeRot(0.18F, 8F, 0F, 0F),
                    easeRot(0.4F, -55F, 0F, 0F),
                    easeRot(0.56F, -132.5F, 0F, 0F),
                    easeRot(0.8F, -130.5F, 2F, 0F),
                    easeRot(1.02F, -134F, -2F, 0F),
                    easeRot(1.22F, -130.5F, 2F, 0F),
                    easeRot(1.4F, -132.5F, 0F, 0F),
                    easeRot(1.65F, -55F, 0F, 0F),
                    easeRot(1.82F, 10F, 0F, 0F),
                    easeRot(1.98F, -8F, 0F, 0F),
                    easeRot(2.14F, 12F, 0F, 0F),
                    easeRot(2.26F, -4F, 0F, 0F),
                    easeRot(2.4F, 0F, 0F, 0F)))
            .addAnimation("left_foot", position(
                    easePos(0F, 0F, 0F, 0F),
                    easePos(0.18F, 0F, 0F, 0F),
                    easePos(0.3F, 0F, 0.75F, 0F),
                    easePos(0.4F, 0F, 0.75F, 0F),
                    easePos(0.56F, 0F, 0F, 0F),
                    easePos(1.4F, 0F, 0F, 0F),
                    easePos(1.65F, 0F, 0.75F, 0F),
                    easePos(1.82F, 0F, 0F, 0F),
                    easePos(2.4F, 0F, 0F, 0F)))
            .addAnimation("right_foot", position(
                    easePos(0F, 0F, 0F, 0F),
                    easePos(0.18F, 0F, 0F, 0F),
                    easePos(0.3F, 0F, 0.75F, 0F),
                    easePos(0.4F, 0F, 0.75F, 0F),
                    easePos(0.56F, 0F, 0F, 0F),
                    easePos(1.4F, 0F, 0F, 0F),
                    easePos(1.65F, 0F, 0.75F, 0F),
                    easePos(1.82F, 0F, 0F, 0F),
                    easePos(2.4F, 0F, 0F, 0F)))
            .addAnimation("left_foot", rotation(
                    easeRot(0F, 0F, -16F, 0F),
                    easeRot(0.18F, 0F, -16F, 0F),
                    easeRot(0.4F, 0F, -5F, 0F),
                    easeRot(0.56F, 0F, 0F, 0F),
                    easeRot(1.4F, 0F, 0F, 0F),
                    easeRot(1.65F, 0F, -5F, 0F),
                    easeRot(1.82F, 0F, -16F, 0F),
                    easeRot(1.98F, -8F, -18F, 0F),
                    easeRot(2.14F, 0F, -16F, 0F),
                    easeRot(2.26F, 0F, -16F, 0F),
                    easeRot(2.4F, 0F, -16F, 0F)))
            .addAnimation("right_foot", rotation(
                    easeRot(0F, 0F, 16F, 0F),
                    easeRot(0.18F, 0F, 16F, 0F),
                    easeRot(0.4F, 0F, 5F, 0F),
                    easeRot(0.56F, 0F, 0F, 0F),
                    easeRot(1.4F, 0F, 0F, 0F),
                    easeRot(1.65F, 0F, 5F, 0F),
                    easeRot(1.82F, 0F, 16F, 0F),
                    easeRot(1.98F, -8F, 18F, 0F),
                    easeRot(2.14F, 0F, 16F, 0F),
                    easeRot(2.26F, 0F, 16F, 0F),
                    easeRot(2.4F, 0F, 16F, 0F)))
            .addAnimation("left_toes", rotation(
                    easeRot(0F, 0F, 0F, 0F),
                    easeRot(0.18F, 0F, 0F, 0F),
                    easeRot(0.4F, 48F, 0F, 0F),
                    easeRot(0.56F, 90F, 0F, 0F),
                    easeRot(1.4F, 90F, 0F, 0F),
                    easeRot(1.65F, 48F, 0F, 0F),
                    easeRot(1.82F, 0F, 0F, 0F),
                    easeRot(1.98F, 0F, 0F, 0F),
                    easeRot(2.14F, 0F, 0F, 0F),
                    easeRot(2.26F, 0F, 0F, 0F),
                    easeRot(2.4F, 0F, 0F, 0F)))
            .addAnimation("right_toes", rotation(
                    easeRot(0F, 0F, 0F, 0F),
                    easeRot(0.18F, 0F, 0F, 0F),
                    easeRot(0.4F, 48F, 0F, 0F),
                    easeRot(0.56F, 90F, 0F, 0F),
                    easeRot(1.4F, 90F, 0F, 0F),
                    easeRot(1.65F, 48F, 0F, 0F),
                    easeRot(1.82F, 0F, 0F, 0F),
                    easeRot(1.98F, 0F, 0F, 0F),
                    easeRot(2.14F, 0F, 0F, 0F),
                    easeRot(2.26F, 0F, 0F, 0F),
                    easeRot(2.4F, 0F, 0F, 0F)))
            .addAnimation("left_toes", position(
                    easePos(0F, 0F, 0F, 0F),
                    easePos(0.18F, 0F, 0F, 0F),
                    easePos(0.4F, 0F, -0.75F, 2.5F),
                    easePos(0.56F, 0F, -1.4F, 0F),
                    easePos(1.4F, 0F, -1.4F, 0F),
                    easePos(1.65F, 0F, -0.75F, 2.5F),
                    easePos(1.82F, 0F, 0F, 0F),
                    easePos(1.98F, 0F, 0F, 0F),
                    easePos(2.14F, 0F, 0F, 0F),
                    easePos(2.26F, 0F, 0F, 0F),
                    easePos(2.4F, 0F, 0F, 0F)))
            .addAnimation("right_toes", position(
                    easePos(0F, 0F, 0F, 0F),
                    easePos(0.18F, 0F, 0F, 0F),
                    easePos(0.4F, 0F, -0.75F, 2.5F),
                    easePos(0.56F, 0F, -1.4F, 0F),
                    easePos(1.4F, 0F, -1.4F, 0F),
                    easePos(1.65F, 0F, -0.75F, 2.5F),
                    easePos(1.82F, 0F, 0F, 0F),
                    easePos(1.98F, 0F, 0F, 0F),
                    easePos(2.14F, 0F, 0F, 0F),
                    easePos(2.26F, 0F, 0F, 0F),
                    easePos(2.4F, 0F, 0F, 0F)))
            .build();

    public static final AnimationDefinition PADDLING = AnimationDefinition.Builder.withLength(1.2F).looping()
            .addAnimation("root", rotation(rot(0F, 20F, 0F, 0F)))
            .addAnimation("body", loopRotation(1.2F,
                    rot(0F, 0F, 0F, -2F), rot(0.3F, 2F, 0F, 0F),
                    rot(0.6F, 0F, 0F, 2F), rot(0.9F, -2F, 0F, 0F), rot(1.2F, 0F, 0F, -2F)))
            .addAnimation("left_arm", loopRotation(1.2F,
                    rot(0F, -12F, 0F, -25F), rot(0.3F, 8F, 0F, -55F),
                    rot(0.6F, 15F, 0F, -35F), rot(0.9F, 0F, 0F, -20F), rot(1.2F, -12F, 0F, -25F)))
            .addAnimation("right_arm", loopRotation(1.2F,
                    rot(0F, 15F, 0F, 35F), rot(0.3F, 0F, 0F, 20F),
                    rot(0.6F, -12F, 0F, 25F), rot(0.9F, 8F, 0F, 55F), rot(1.2F, 15F, 0F, 35F)))
            .addAnimation("left_foot", loopRotation(1.2F,
                    rot(0F, -18F, 0F, 0F), rot(0.3F, 18F, 0F, 0F),
                    rot(0.6F, -18F, 0F, 0F), rot(0.9F, 18F, 0F, 0F), rot(1.2F, -18F, 0F, 0F)))
            .addAnimation("right_foot", loopRotation(1.2F,
                    rot(0F, 18F, 0F, 0F), rot(0.3F, -18F, 0F, 0F),
                    rot(0.6F, 18F, 0F, 0F), rot(0.9F, -18F, 0F, 0F), rot(1.2F, 18F, 0F, 0F)))
            .addAnimation("tail", loopRotation(1.2F,
                    rot(0F, -5F, -6F, 0F), rot(0.3F, 4F, 0F, 0F),
                    rot(0.6F, -5F, 6F, 0F), rot(0.9F, 4F, 0F, 0F), rot(1.2F, -5F, -6F, 0F)))
            .build();

    public static final AnimationDefinition SWIMMING = AnimationDefinition.Builder.withLength(1F).looping()
            .addAnimation("root", rotation(rot(0F, 90F, 0F, 0F)))
            .addAnimation("root", position(pos(0F, 0F, 6F, 0F)))
            .addAnimation("body", loopRotation(1F,
                    rot(0F, -2F, 0F, -2F), rot(0.25F, 2F, 0F, 0F),
                    rot(0.5F, -2F, 0F, 2F), rot(0.75F, 1F, 0F, 0F), rot(1F, -2F, 0F, -2F)))
            .addAnimation("left_arm", loopRotation(1F,
                    rot(0F, -8F, 0F, -45F), rot(0.25F, 6F, 0F, -85F),
                    rot(0.5F, 12F, 0F, -35F), rot(0.75F, 0F, 0F, -40F), rot(1F, -8F, 0F, -45F)))
            .addAnimation("right_arm", loopRotation(1F,
                    rot(0F, -8F, 0F, 45F), rot(0.25F, 6F, 0F, 85F),
                    rot(0.5F, 12F, 0F, 35F), rot(0.75F, 0F, 0F, 40F), rot(1F, -8F, 0F, 45F)))
            .addAnimation("left_foot", loopRotation(1F,
                    rot(0F, -8F, 0F, 0F), rot(0.25F, 10F, 0F, 0F),
                    rot(0.5F, -8F, 0F, 0F), rot(0.75F, 10F, 0F, 0F), rot(1F, -8F, 0F, 0F)))
            .addAnimation("right_foot", loopRotation(1F,
                    rot(0F, 10F, 0F, 0F), rot(0.25F, -8F, 0F, 0F),
                    rot(0.5F, 10F, 0F, 0F), rot(0.75F, -8F, 0F, 0F), rot(1F, 10F, 0F, 0F)))
            .addAnimation("left_toes", position(pos(0F, 0F, -1.4F, 0F)))
            .addAnimation("right_toes", position(pos(0F, 0F, -1.4F, 0F)))
            .addAnimation("left_toes", loopRotation(1F,
                    rot(0F, 82F, 0F, 0F), rot(0.125F, 80F, 0F, 0F),
                    rot(0.25F, 92F, 0F, 0F), rot(0.375F, 98F, 0F, 0F),
                    rot(0.5F, 82F, 0F, 0F), rot(0.625F, 80F, 0F, 0F),
                    rot(0.75F, 92F, 0F, 0F), rot(0.875F, 98F, 0F, 0F), rot(1F, 82F, 0F, 0F)))
            .addAnimation("right_toes", loopRotation(1F,
                    rot(0F, 92F, 0F, 0F), rot(0.125F, 98F, 0F, 0F),
                    rot(0.25F, 82F, 0F, 0F), rot(0.375F, 80F, 0F, 0F),
                    rot(0.5F, 92F, 0F, 0F), rot(0.625F, 98F, 0F, 0F),
                    rot(0.75F, 82F, 0F, 0F), rot(0.875F, 80F, 0F, 0F), rot(1F, 92F, 0F, 0F)))
            .addAnimation("tail", loopRotation(1F,
                    rot(0F, -4F, -8F, 0F), rot(0.25F, 5F, 0F, 0F),
                    rot(0.5F, -4F, 8F, 0F), rot(0.75F, 5F, 0F, 0F), rot(1F, -4F, -8F, 0F)))
            .build();

    public static final AnimationDefinition LANDING = AnimationDefinition.Builder.withLength(0.6F)
            .addAnimation("root", position(
                    easePos(0F, 0F, 0F, 0F), easePos(0.1F, 0F, 0F, 0F),
                    easePos(0.22F, 0F, 1.5F, 0F), easePos(0.32F, 0F, 0F, 0F),
                    easePos(0.42F, 0F, 0.35F, 0F), easePos(0.52F, 0F, 0F, 0F),
                    easePos(0.6F, 0F, 0F, 0F)))
            .addAnimation("body", position(
                    easePos(0F, 0F, 0F, 0F), easePos(0.1F, 0F, -1.6F, 0F),
                    easePos(0.22F, 0F, 0.25F, 0F), easePos(0.32F, 0F, -0.65F, 0F),
                    easePos(0.42F, 0F, 0.1F, 0F), easePos(0.6F, 0F, 0F, 0F)))
            .addAnimation("body", rotation(
                    flowRot(0F, -10F, 0F, 0F), flowRot(0.1F, 14F, 0F, 3F),
                    flowRot(0.22F, -7F, 0F, -2F), flowRot(0.32F, 7F, 0F, 2F),
                    flowRot(0.42F, -3F, 0F, -1F), flowRot(0.52F, 1F, 0F, 0.3F),
                    flowRot(0.6F, 0F, 0F, 0F)))
            .addAnimation("head", rotation(
                    flowRot(0F, 8F, 0F, 0F), flowRot(0.1F, -10F, 0F, -3F),
                    flowRot(0.22F, 7F, 0F, 2F), flowRot(0.32F, -5F, 0F, -2F),
                    flowRot(0.42F, 3F, 0F, 1F), flowRot(0.52F, -1F, 0F, -0.3F),
                    flowRot(0.6F, 0F, 0F, 0F)))
            .addAnimation("left_arm", rotation(
                    flowRot(0F, -6F, 0F, -60F), flowRot(0.1F, 14F, -4F, -38F),
                    flowRot(0.2F, -16F, 3F, -70F), flowRot(0.32F, 10F, -2F, -32F),
                    flowRot(0.42F, -6F, 1F, -42F), flowRot(0.52F, 2F, 0F, -10F),
                    flowRot(0.6F, 0F, 0F, 0F)))
            .addAnimation("right_arm", rotation(
                    flowRot(0F, 6F, 0F, 60F), flowRot(0.1F, -10F, -4F, 42F),
                    flowRot(0.2F, 8F, 3F, 58F), flowRot(0.32F, -12F, -2F, 42F),
                    flowRot(0.42F, 5F, 1F, 24F), flowRot(0.52F, -2F, 0F, 12F),
                    flowRot(0.6F, 0F, 0F, 0F)))
            .addAnimation("left_foot", position(
                    easePos(0F, 0.45F, 0F, -0.9F), easePos(0.12F, 0.45F, 0F, -0.7F),
                    easePos(0.32F, 0.3F, 0F, -0.4F), easePos(0.48F, 0.1F, 0F, -0.1F),
                    easePos(0.6F, 0F, 0F, 0F)))
            .addAnimation("right_foot", position(
                    easePos(0F, -0.45F, 0F, -0.9F), easePos(0.12F, -0.45F, 0F, -0.7F),
                    easePos(0.32F, -0.3F, 0F, -0.4F), easePos(0.48F, -0.1F, 0F, -0.1F),
                    easePos(0.6F, 0F, 0F, 0F)))
            .addAnimation("left_foot", rotation(
                    flowRot(0F, -12F, -10F, 0F), flowRot(0.12F, -8F, -10F, 0F),
                    flowRot(0.32F, -6F, -6F, 0F), flowRot(0.48F, -2F, -2F, 0F),
                    flowRot(0.6F, 0F, 0F, 0F)))
            .addAnimation("right_foot", rotation(
                    flowRot(0F, -12F, 10F, 0F), flowRot(0.12F, -8F, 10F, 0F),
                    flowRot(0.32F, -6F, 6F, 0F), flowRot(0.48F, -2F, 2F, 0F),
                    flowRot(0.6F, 0F, 0F, 0F)))
            .addAnimation("left_toes", rotation(
                    flowRot(0F, 12F, 0F, 0F), flowRot(0.12F, 8F, 0F, 0F),
                    flowRot(0.32F, 6F, 0F, 0F), flowRot(0.48F, 2F, 0F, 0F),
                    flowRot(0.6F, 0F, 0F, 0F)))
            .addAnimation("right_toes", rotation(
                    flowRot(0F, 12F, 0F, 0F), flowRot(0.12F, 8F, 0F, 0F),
                    flowRot(0.32F, 6F, 0F, 0F), flowRot(0.48F, 2F, 0F, 0F),
                    flowRot(0.6F, 0F, 0F, 0F)))
            .addAnimation("tail", rotation(
                    flowRot(0F, 8F, 0F, 0F), flowRot(0.12F, 18F, 0F, 0F),
                    flowRot(0.24F, -12F, 0F, 0F), flowRot(0.34F, 8F, 0F, 0F),
                    flowRot(0.45F, -4F, 0F, 0F), flowRot(0.6F, 0F, 0F, 0F)))
            .build();

    public static final AnimationDefinition SHAKE = AnimationDefinition.Builder.withLength(1.2F)
            .addAnimation("body", rotation(
                    flowRot(0F, 0F, 0F, 0F), flowRot(0.18F, 0F, 0F, 0F),
                    flowRot(0.29F, 2F, -12F, 1F), flowRot(0.43F, -2F, 24F, -2F),
                    flowRot(0.55F, 2.5F, -26F, 2.5F), flowRot(0.66F, -2F, 22F, -3F),
                    flowRot(0.76F, 1.5F, -15F, 2F), flowRot(0.85F, -1F, 8F, -1F),
                    flowRot(0.93F, 0.5F, -3F, 0.5F), flowRot(1.03F, 0F, 0F, 0F),
                    flowRot(1.2F, 0F, 0F, 0F)))
            .addAnimation("head", rotation(
                    flowRot(0F, 0F, 0F, 0F), flowRot(0.09F, 0F, -16F, 0F),
                    flowRot(0.18F, 0F, 2.5F, 0F), flowRot(0.26F, 0F, 27.8F, 0F),
                    flowRot(0.29F, 0F, 26.5F, 0F), flowRot(0.41F, 0F, -38F, 0F),
                    flowRot(0.43F, 0F, -38.4F, 0F), flowRot(0.55F, 0F, 38F, 0F),
                    flowRot(0.66F, 0F, -28.7F, 0F), flowRot(0.68F, 0F, -26.2F, 0F),
                    flowRot(0.76F, 0F, 16.6F, 0F), flowRot(0.8F, 0F, 10.4F, 0F),
                    flowRot(0.85F, 0F, -6F, 0F), flowRot(0.91F, 0F, -0.7F, 0F),
                    flowRot(0.93F, 0F, 1.4F, 0F), flowRot(0.98F, 0F, 1.5F, 0F),
                    flowRot(1.03F, 0F, 0F, 0F), flowRot(1.2F, 0F, 0F, 0F)))
            .addAnimation("left_arm", rotation(
                    flowRot(0F, 0F, 0F, 0F), flowRot(0.28F, 0F, 0F, 0F),
                    flowRot(0.38F, -4F, -4F, -9F), flowRot(0.49F, 7F, 8F, -18F),
                    flowRot(0.585F, -10F, -11F, -8F), flowRot(0.67F, 12F, 13F, -25F),
                    flowRot(0.745F, -10F, -12F, -9F), flowRot(0.815F, 8F, 10F, -21F),
                    flowRot(0.88F, -5F, -7F, -7F), flowRot(0.94F, 3F, 4F, -13F),
                    flowRot(1F, -1F, -2F, -4F), flowRot(1.08F, 0F, 0F, 0F),
                    flowRot(1.2F, 0F, 0F, 0F)))
            .addAnimation("right_arm", rotation(
                    flowRot(0F, 0F, 0F, 0F), flowRot(0.28F, 0F, 0F, 0F),
                    flowRot(0.38F, 4F, -4F, 9F), flowRot(0.49F, -7F, 8F, 18F),
                    flowRot(0.585F, 10F, -11F, 8F), flowRot(0.67F, -12F, 13F, 25F),
                    flowRot(0.745F, 10F, -12F, 9F), flowRot(0.815F, -8F, 10F, 21F),
                    flowRot(0.88F, 5F, -7F, 7F), flowRot(0.94F, -3F, 4F, 13F),
                    flowRot(1F, 1F, -2F, 4F), flowRot(1.08F, 0F, 0F, 0F),
                    flowRot(1.2F, 0F, 0F, 0F)))
            .addAnimation("tail", rotation(
                    flowRot(0F, 0F, 0F, 0F), flowRot(0.38F, 0F, 0F, 0F),
                    flowRot(0.47F, -1F, -10F, -1F), flowRot(0.55F, 2F, 23F, 2F),
                    flowRot(0.62F, -3F, -34F, -4F), flowRot(0.685F, 4F, 43F, 5F),
                    flowRot(0.745F, -4F, -40F, -5F), flowRot(0.8F, 3F, 34F, 4F),
                    flowRot(0.855F, -3F, -27F, -3F), flowRot(0.91F, 2F, 20F, 2F),
                    flowRot(0.965F, -1.5F, -14F, -1.5F), flowRot(1.02F, 1F, 9F, 1F),
                    flowRot(1.075F, -0.5F, -5F, -0.5F), flowRot(1.13F, 0.25F, 2F, 0.25F),
                    flowRot(1.2F, 0F, 0F, 0F)))
            .build();

    // public static final AnimationDefinition CHIP_BEAK = AnimationDefinition.Builder.withLength(1.2F)
            // .addAnimation("body", rotation(
                    // flowRot(0F, 0F, 0F, 0F), flowRot(0.18F, 14F, 0F, 0F), flowRot(0.35F, 60F, 0F, 0F),
                    // flowRot(0.5F, 80F, 0F, 0F), flowRot(0.62F, 65F, 0F, 0F), flowRot(0.85F, 80F, 0F, 0F),
                    // flowRot(0.98F, 60F, 0F, 0F), flowRot(1.2F, 0F, 0F, 0F)))
            // .addAnimation("body", position(
                    // pos(0F, 0F, 0F, 0F), easePos(0.18F, 0F, -0.5F, 0F), easePos(0.35F, 0F, -0.1F, -0.2F),
                    // easePos(0.5F, 0F, -1.2F, -0.3F), easePos(0.62F, 0F, 0F, 0F), easePos(0.85F, 0F, -1.2F, -0.3F),
                    // easePos(0.98F, 0F, -0.1F, 0F), easePos(1.2F, 0F, 0F, 0F)))
            // .addAnimation("head", rotation(
                    // flowRot(0F, 0F, 0F, 0F), flowRot(0.18F, 14F, 0F, 0F), flowRot(0.35F, 18F, 0F, 0F),
                    // flowRot(0.5F, 10F, 0F, 0F), flowRot(0.62F, 9F, 0F, 0F), flowRot(0.85F, 10F, 0F, 0F),
                    // flowRot(0.98F, 15F, 0F, 0F), flowRot(1.2F, 0F, 0F, 0F)))
            // .addAnimation("left_arm", rotation(
                    // flowRot(0F, 0F, 0F, 0F), flowRot(0.18F, -12F, 0F, -29F), flowRot(0.35F, -38F, 0F, -54F),
                    // flowRot(0.5F, -49F, 0F, -63F), flowRot(0.62F, -38F, 0F, -56F), flowRot(0.85F, -49F, 0F, -63F),
                    // flowRot(0.98F, -38F, 0F, -54F), flowRot(1.2F, 0F, 0F, 0F)))
            // .addAnimation("right_arm", rotation(
                    // flowRot(0F, 0F, 0F, 0F), flowRot(0.18F, -12F, 0F, 29F), flowRot(0.35F, -38F, 0F, 54F),
                    // flowRot(0.5F, -49F, 0F, 63F), flowRot(0.62F, -38F, 0F, 56F), flowRot(0.85F, -49F, 0F, 63F),
                    // flowRot(0.98F, -38F, 0F, 54F), flowRot(1.2F, 0F, 0F, 0F)))
            // .addAnimation("tail", rotation(
                    // flowRot(0F, 0F, 0F, 0F), flowRot(0.5F, -8F, -4F, 0F), flowRot(0.62F, -3F, 3F, 0F),
                    // flowRot(0.85F, -8F, -4F, 0F), flowRot(1.2F, 0F, 0F, 0F)))
            // .build();

    // public static final AnimationDefinition CHIP_HOP = AnimationDefinition.Builder.withLength(1.2F)
            // .addAnimation("body", rotation(
                    // flowRot(0F, 0F, 0F, 0F), flowRot(0.25F, -10F, 0F, 0F),
                    // flowRot(0.4F, 4F, 0F, 0F), flowRot(0.6F, 8F, 0F, 0F),
                    // flowRot(0.85F, 12F, 0F, 0F), flowRot(1.2F, 12F, 0F, 0F)))
            // .addAnimation("body", position(
                    // pos(0F, 0F, 0F, 0F), easePos(0.25F, 0F, -0.8F, 0F),
                    // easePos(0.4F, 0F, 0F, 0F), easePos(1.2F, 0F, 0F, 0F)))
            // .addAnimation("head", rotation(
                    // flowRot(0F, 0F, 0F, 0F), flowRot(0.25F, -5F, 0F, 0F),
                    // flowRot(0.55F, 8F, 0F, 0F), flowRot(0.85F, 5F, 0F, 0F), flowRot(1.2F, 5F, 0F, 0F)))
            // .addAnimation("left_arm", rotation(
                    // flowRot(0F, 0F, 0F, 0F), flowRot(0.25F, 20F, 0F, -20F),
                    // flowRot(0.45F, -30F, 0F, -48F), flowRot(0.75F, -12F, 0F, -24F), flowRot(1.2F, -12F, 0F, -24F)))
            // .addAnimation("right_arm", rotation(
                    // flowRot(0F, 0F, 0F, 0F), flowRot(0.25F, 20F, 0F, 20F),
                    // flowRot(0.45F, -30F, 0F, 48F), flowRot(0.75F, -12F, 0F, 24F), flowRot(1.2F, -12F, 0F, 24F)))
            // .addAnimation("left_foot", rotation(
                    // flowRot(0F, 0F, 0F, 0F), flowRot(0.45F, -18F, 0F, 0F),
                    // flowRot(0.75F, 0F, 0F, 0F), flowRot(1.2F, 0F, 0F, 0F)))
            // .addAnimation("right_foot", rotation(
                    // flowRot(0F, 0F, 0F, 0F), flowRot(0.45F, -18F, 0F, 0F),
                    // flowRot(0.75F, 0F, 0F, 0F), flowRot(1.2F, 0F, 0F, 0F)))
            // .build();

    // public static final AnimationDefinition CHIP_BACK = AnimationDefinition.Builder.withLength(1.2F)
            // .addAnimation("body", rotation(
                    // flowRot(0F, 12F, 0F, 0F), flowRot(0.1F, 16F, 0F, 0F),
                    // flowRot(0.2F, -13F, 0F, 0F), flowRot(0.5F, -8F, 0F, 0F),
                    // flowRot(0.65F, 8F, 0F, 0F), flowRot(0.78F, -4F, 0F, -3F), flowRot(0.9F, 0F, 0F, 0F), flowRot(1.2F, 0F, 0F, 0F)))
            // .addAnimation("body", position(
                    // pos(0F, 0F, 0F, 0F), easePos(0.1F, 0F, -1F, 0F),
                    // easePos(0.2F, 0F, 0F, 0F), easePos(0.65F, 0F, -0.4F, 0F),
                    // easePos(0.78F, 0F, 0.15F, 0F), easePos(0.9F, 0F, 0F, 0F)))
            // .addAnimation("head", rotation(
                    // flowRot(0F, 5F, 0F, 0F), flowRot(0.15F, -5F, 0F, 0F),
                    // flowRot(0.6F, 4F, 0F, 0F), flowRot(0.9F, 0F, 0F, 0F)))
            // .addAnimation("left_arm", rotation(
                    // flowRot(0F, -12F, 0F, -24F), flowRot(0.15F, -35F, 0F, -58F),
                    // flowRot(0.5F, -25F, 0F, -58F), flowRot(0.7F, 8F, 0F, -28F), flowRot(0.9F, 0F, 0F, 0F)))
            // .addAnimation("right_arm", rotation(
                    // flowRot(0F, -12F, 0F, 24F), flowRot(0.15F, -35F, 0F, 58F),
                    // flowRot(0.5F, -25F, 0F, 58F), flowRot(0.7F, 8F, 0F, 28F), flowRot(0.9F, 0F, 0F, 0F)))
            // .addAnimation("left_foot", rotation(
                    // flowRot(0F, 0F, 0F, 0F), flowRot(0.2F, 20F, 0F, 0F),
                    // flowRot(0.5F, 12F, 0F, 0F), flowRot(0.65F, 0F, 0F, 0F), flowRot(1.2F, 0F, 0F, 0F)))
            // .addAnimation("right_foot", rotation(
                    // flowRot(0F, 0F, 0F, 0F), flowRot(0.2F, 20F, 0F, 0F),
                    // flowRot(0.5F, 12F, 0F, 0F), flowRot(0.65F, 0F, 0F, 0F), flowRot(1.2F, 0F, 0F, 0F)))
            // .addAnimation("tail", rotation(
                    // flowRot(0F, 0F, 0F, 0F), flowRot(0.2F, 8F, -10F, 0F),
                    // flowRot(0.6F, -8F, 8F, 0F), flowRot(0.9F, 0F, 0F, 0F)))
            // .build();

    private record AttackClip(AnimationDefinition right, AnimationDefinition left, AnimationChannel pitch, AnimationChannel weapon) {}
    private static final Map<PinginAttack, AttackClip> ATTACKS = buildAttacks();

    private static Map<PinginAttack, AttackClip> buildAttacks() {
        Map<PinginAttack, AttackClip> clips = new EnumMap<>(PinginAttack.class);
        for (PinginAttack attack : PinginAttack.HANDHELD) {
            clips.put(attack, buildAttack(attack));
        }
        return clips;
    }

    public static AnimationDefinition attack(PinginAttack attack, boolean isLeftHanded) {
        AttackClip clip = ATTACKS.get(attack);
        return isLeftHanded ? clip.left() : clip.right();
    }

    private static float[] attackTimes(PinginAttack attack) {
        float hitTime = attack.hitTick / 20F, endTime = attack.seconds();
        return new float[]{0F, hitTime * 0.45F, hitTime * 0.7F, hitTime,
                hitTime + (endTime - hitTime) * 0.22F, hitTime + (endTime - hitTime) * 0.5F, hitTime + (endTime - hitTime) * 0.78F, endTime};
    }

    private static AttackClip buildAttack(PinginAttack attack) {
        float[] times = attackTimes(attack);
        float[] zeros = new float[times.length];
        float[] bodyYaw = {0F, attack.windupYaw * 0.85F, attack.windupYaw, attack.strikeYaw * 0.8F, attack.strikeYaw * 1.15F, attack.strikeYaw * 0.6F, -attack.windupYaw * 0.08F, 0F};
        float[] wingPitch = {0F, attack.windupPitch * 0.55F, attack.windupPitch, attack.strikePitch, attack.strikePitch * 1.15F, attack.strikePitch * 0.55F, -attack.strikePitch * 0.1F, 0F};
        float[] strength = {0F, 0.25F, 0.55F, 1F, 0.95F, 0.5F, 0.12F, 0F};
        float[] headYaw = {0F, -attack.windupYaw * 0.3F, -attack.windupYaw * 0.65F, -attack.strikeYaw * 0.3F,
                -attack.strikeYaw * 0.5F, -attack.strikeYaw * 0.45F, 0F, 0F};
        AnimationChannel pitch = flowTrack(AnimationChannel.Targets.ROTATION, times, wingPitch, zeros,
                scaleValues(attack.flare, 0F, -0.25F, -0.4F, 0.85F, 1F, 0.5F, -0.1F, 0F));
        AnimationDefinition clip = AnimationDefinition.Builder.withLength(attack.seconds())
                .addAnimation("root", flowTrack(AnimationChannel.Targets.POSITION, times,
                        scaleValues(attack.lunge * Math.signum(attack.strikeYaw), 0F, 0.12F, 0.2F, -0.45F, -0.55F, -0.2F, 0.05F, 0F),
                        scaleValues(attack.hop, 0F, 0F, 0.65F, 1F, 0.4F, 0F, 0F, 0F),
                        scaleValues(attack.lunge, 0F, 0.2F, 0.35F, -1F, -1.15F, -0.45F, 0.08F, 0F)))
                .addAnimation("root", flowTrack(AnimationChannel.Targets.ROTATION, times, zeros,
                        scaleValues(attack.turn, 0F, -0.06F, 0.5F, 1F, 1F, 1F, 1F, 1F), zeros))
                .addAnimation("body", flowTrack(AnimationChannel.Targets.ROTATION, times, zeros, bodyYaw,
                        scaleValues(Math.signum(attack.strikeYaw), 0F, -2F, -3F, 2F, 3F, 1F, -0.6F, 0F)))
                .addAnimation("head", flowTrack(AnimationChannel.Targets.ROTATION, times, zeros, headYaw, zeros))
                .addAnimation("right_arm", pitch)
                .addAnimation("right_arm", flowTrack(AnimationChannel.Targets.POSITION, times, zeros, zeros,
                        scaleValues(attack.lunge, 0F, 0.18F, 0.25F, -0.55F, -0.65F, -0.2F, 0.05F, 0F)))
                .addAnimation("left_arm", flowTrack(AnimationChannel.Targets.ROTATION, times,
                        scaleValues(8F, 0F, -0.25F, -0.5F, 1F, 0.8F, 0.25F, -0.1F, 0F), zeros, scaleValues(-24F, strength)))
                .addAnimation("right_foot", flowTrack(AnimationChannel.Targets.POSITION, times, zeros, scaleValues(0.06F, strength),
                        scaleValues(attack.lunge, 0F, 0.1F, 0.22F, -0.35F, -0.45F, -0.25F, 0F, 0F)))
                .addAnimation("left_foot", flowTrack(AnimationChannel.Targets.POSITION, times, zeros, scaleValues(0.04F, strength),
                        scaleValues(attack.lunge, 0F, 0.15F, 0.3F, 0.15F, -0.2F, -0.35F, -0.1F, 0F)))
                .addAnimation("right_foot", flowTrack(AnimationChannel.Targets.ROTATION, times, scaleValues(-6F, strength), scaleValues(-0.35F, bodyYaw), zeros))
                .addAnimation("right_toes", flowTrack(AnimationChannel.Targets.ROTATION, times, scaleValues(6F, strength), zeros, zeros))
                .addAnimation("left_foot", flowTrack(AnimationChannel.Targets.ROTATION, times, scaleValues(4F, strength), scaleValues(-0.25F, bodyYaw), zeros))
                .addAnimation("left_toes", flowTrack(AnimationChannel.Targets.ROTATION, times, scaleValues(-4F, strength), zeros, zeros))
                .addAnimation("tail", flowTrack(AnimationChannel.Targets.ROTATION, times, scaleValues(-0.2F, wingPitch),
                        new float[]{0F, -attack.windupYaw * 0.15F, -attack.windupYaw * 0.35F, -attack.windupYaw * 0.45F,
                                -attack.strikeYaw * 0.45F, -attack.strikeYaw * 0.25F, attack.windupYaw * 0.1F, 0F}, zeros))
                .build();

        float[] weaponTimes = times.clone();
        for (int index = 1; index < weaponTimes.length - 1; index++) weaponTimes[index] -= attack.weaponLead;
        AnimationChannel weapon = flowTrack(AnimationChannel.Targets.ROTATION, weaponTimes,
                scaleValues(attack.weaponAngle, 0F, -0.35F, -0.55F, 1F, 1F + 0.2F * attack.inertia, 0.45F, -0.08F, 0F), zeros, zeros);
        return new AttackClip(clip, mirror(clip), pitch, weapon);
    }

    public static float weaponAngle(PinginAttack attack, float time, Vector3f scratch) {
        float endTime = attack.seconds();
        if (time <= 0F || time >= endTime) {
            return 0F;
        } else {
            AttackClip clip = ATTACKS.get(attack);
            float wingPitch = AnimationUtil.sample(clip.pitch(), time, scratch).x();
            float weaponLag = (AnimationUtil.sample(clip.pitch(), Math.max(0F, time - 0.11F), scratch).x() - wingPitch) * 1.1F * attack.inertia;
            float recovery = Math.max(0F, Math.min(1F, (endTime - time) / (endTime * 0.3F)));
            recovery = recovery * recovery * (3F - 2F * recovery);
            return AnimationUtil.sample(clip.weapon(), time, scratch).x() + Math.max(-0.75F, Math.min(0.75F, weaponLag)) * recovery;
        }
    }
}

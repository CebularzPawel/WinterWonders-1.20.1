package net.cebularz.winterwonders.entity.animations;

import net.minecraft.client.animation.AnimationDefinition;

import static net.cebularz.winterwonders.entity.animations.AnimationUtil.*;

public final class LichAnimations {
    private LichAnimations() {}

    public static final AnimationDefinition IDLE = AnimationDefinition.Builder.withLength(4.8F).looping()
            .addAnimation("root", loopPosition(4.8F,
                    pos(0F, 0F, 3F, 0F), pos(1.2F, 0F, 3.7F, 0F), pos(2.4F, 0F, 3F, 0F),
                    pos(3.6F, 0F, 2.3F, 0F), pos(4.8F, 0F, 3F, 0F)))
            .addAnimation("body", loopRotation(4.8F,
                    rot(0F, -2F, 0F, -1.5F), rot(1.2F, -1F, 1.5F, 0F), rot(2.4F, -2F, 0F, 1.5F),
                    rot(3.6F, -3F, -1.5F, 0F), rot(4.8F, -2F, 0F, -1.5F)))
            .addAnimation("head", loopRotation(4.8F,
                    rot(0F, 2F, 0F, 1F), rot(1.2F, 1F, -1F, 0F), rot(2.4F, 2F, 0F, -1F),
                    rot(3.6F, 3F, 1F, 0F), rot(4.8F, 2F, 0F, 1F)))
            .addAnimation("right_arm", loopRotation(4.8F,
                    rot(0F, -12F, -6F, 8F), rot(1.2F, -15F, -7F, 9F), rot(2.4F, -12F, -6F, 10F),
                    rot(3.6F, -9F, -5F, 9F), rot(4.8F, -12F, -6F, 8F)))
            .addAnimation("left_arm", loopRotation(4.8F,
                    rot(0F, -4F, 0F, -9F), rot(1.2F, -2F, 2F, -11F), rot(2.4F, -4F, 0F, -13F),
                    rot(3.6F, -6F, -2F, -11F), rot(4.8F, -4F, 0F, -9F)))
            .addAnimation("left_leg", loopRotation(4.8F,
                    rot(0F, 7F, 0F, -3F), rot(1.2F, 4F, 0F, -2F), rot(2.4F, 7F, 0F, -3F),
                    rot(3.6F, 10F, 0F, -4F), rot(4.8F, 7F, 0F, -3F)))
            .addAnimation("right_leg", loopRotation(4.8F,
                    rot(0F, -3F, 0F, 3F), rot(1.2F, 0F, 0F, 4F), rot(2.4F, -3F, 0F, 3F),
                    rot(3.6F, -6F, 0F, 2F), rot(4.8F, -3F, 0F, 3F)))
            .build();

    public static final AnimationDefinition GLIDING = AnimationDefinition.Builder.withLength(4.8F).looping()
            .addAnimation("body", rotation(rot(0F, 9F, 0F, 0F)))
            .addAnimation("head", rotation(rot(0F, -7F, 0F, 0F)))
            .addAnimation("left_leg", loopRotation(4.8F,
                    rot(0F, 15F, 0F, -2F), rot(1.2F, 20F, 0F, -2F), rot(2.4F, 15F, 0F, -2F),
                    rot(3.6F, 10F, 0F, -2F), rot(4.8F, 15F, 0F, -2F)))
            .addAnimation("right_leg", loopRotation(4.8F,
                    rot(0F, 20F, 0F, 2F), rot(1.2F, 15F, 0F, 2F), rot(2.4F, 20F, 0F, 2F),
                    rot(3.6F, 25F, 0F, 2F), rot(4.8F, 20F, 0F, 2F)))
            .addAnimation("left_arm", rotation(rot(0F, 7F, 0F, -5F)))
            .addAnimation("right_arm", rotation(rot(0F, 5F, 0F, 3F)))
            .build();

    public static final AnimationDefinition CASTING = AnimationDefinition.Builder.withLength(2.4F).looping()
            .addAnimation("root", position(pos(0F, 0F, 1F, 0F)))
            .addAnimation("body", loopRotation(2.4F,
                    rot(0F, -4F, -5F, 0F), rot(.6F, -6F, -2F, 1F), rot(1.2F, -4F, 1F, 0F),
                    rot(1.8F, -2F, -2F, -1F), rot(2.4F, -4F, -5F, 0F)))
            .addAnimation("head", rotation(rot(0F, 4F, 2F, 0F)))
            .addAnimation("right_arm", loopRotation(2.4F,
                    rot(0F, -62F, -12F, 47F), rot(.6F, -68F, -9F, 51F), rot(1.2F, -62F, -6F, 47F),
                    rot(1.8F, -56F, -9F, 43F), rot(2.4F, -62F, -12F, 47F)))
            .addAnimation("left_arm", loopRotation(2.4F,
                    rot(0F, -91F, -16F, -26F), rot(.6F, -97F, -10F, -30F), rot(1.2F, -91F, -4F, -26F),
                    rot(1.8F, -85F, -10F, -22F), rot(2.4F, -91F, -16F, -26F)))
            .build();

    public static final AnimationDefinition LEFT_IDLE = mirror(IDLE);
    public static final AnimationDefinition LEFT_GLIDING = mirror(GLIDING);
    public static final AnimationDefinition LEFT_CASTING = mirror(CASTING);
}

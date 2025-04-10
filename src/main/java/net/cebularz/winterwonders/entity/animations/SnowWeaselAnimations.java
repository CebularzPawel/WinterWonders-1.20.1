package net.cebularz.winterwonders.entity.animations;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;

public class SnowWeaselAnimations {
    public static final AnimationDefinition IDLE = AnimationDefinition.Builder.withLength(4.0F).looping()
            .addAnimation("chest", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.92F, KeyframeAnimations.degreeVec(10.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("chest", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.92F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.5F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("hands", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.92F, KeyframeAnimations.degreeVec(-10.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("hands", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.92F, KeyframeAnimations.posVec(0.0F, 0.25F, -0.6953F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.92F, KeyframeAnimations.degreeVec(-10.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.92F, KeyframeAnimations.posVec(0.0F, -0.0695F, 0.3939F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("posterior", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.92F, KeyframeAnimations.degreeVec(-2.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("posterior", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.92F, KeyframeAnimations.posVec(0.0F, 0.25F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("feet", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.92F, KeyframeAnimations.degreeVec(2.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("feet", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.92F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("tail", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.92F, KeyframeAnimations.degreeVec(17.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("tail", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.92F, KeyframeAnimations.posVec(0.0F, -0.0218F, -0.4995F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .build();

    public static final AnimationDefinition WALKING = AnimationDefinition.Builder.withLength(0.6667F).looping()
            .addAnimation("weasel", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.6667F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("weasel", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.0741F, KeyframeAnimations.posVec(0.0F, -0.05F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.1235F, KeyframeAnimations.posVec(0.0F, 0.27F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.1481F, KeyframeAnimations.posVec(0.0F, 0.29F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.1728F, KeyframeAnimations.posVec(0.0F, 0.2F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2716F, KeyframeAnimations.posVec(0.0F, 3.44F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4198F, KeyframeAnimations.posVec(0.0F, 3.44F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5679F, KeyframeAnimations.posVec(0.0F, -0.99F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5926F, KeyframeAnimations.posVec(0.0F, -0.76F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.6667F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.1975F, KeyframeAnimations.degreeVec(15.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2716F, KeyframeAnimations.degreeVec(10.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.321F, KeyframeAnimations.degreeVec(2.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.3457F, KeyframeAnimations.degreeVec(-3.75F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5679F, KeyframeAnimations.degreeVec(-10.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.642F, KeyframeAnimations.degreeVec(9.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.6667F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.1728F, KeyframeAnimations.posVec(0.0F, 0.9956F, 1.1416F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2469F, KeyframeAnimations.posVec(0.0F, 0.648F, 0.876F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.3951F, KeyframeAnimations.posVec(0.0F, -0.1143F, 0.4058F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4938F, KeyframeAnimations.posVec(0.0F, -0.1861F, 0.4895F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5679F, KeyframeAnimations.posVec(0.0F, -0.3451F, 0.6202F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.642F, KeyframeAnimations.posVec(0.0F, 0.0279F, 0.3207F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.6667F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("tail", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.3457F, KeyframeAnimations.degreeVec(-47.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5679F, KeyframeAnimations.degreeVec(95.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.6667F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("tail", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.1728F, KeyframeAnimations.posVec(0.0F, 0.0207F, -0.8335F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.3457F, KeyframeAnimations.posVec(0.0F, -0.1F, 1.02F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4691F, KeyframeAnimations.posVec(0.0F, -0.04F, -0.4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4938F, KeyframeAnimations.posVec(0.0F, -0.03F, -0.74F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5185F, KeyframeAnimations.posVec(0.0F, -0.02F, -1.04F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5679F, KeyframeAnimations.posVec(0.0F, 0.0F, -1.35F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.6667F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("chest", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.0741F, KeyframeAnimations.degreeVec(-12.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.1728F, KeyframeAnimations.degreeVec(-27.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2716F, KeyframeAnimations.degreeVec(-12.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.3457F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4198F, KeyframeAnimations.degreeVec(10.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.6173F, KeyframeAnimations.degreeVec(10.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.6667F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("chest", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.0741F, KeyframeAnimations.posVec(0.0F, 1.8F, 0.58F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.1728F, KeyframeAnimations.posVec(0.0F, 4.45F, 1.7F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2716F, KeyframeAnimations.posVec(0.0F, 3.35F, -1.25F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4198F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.25F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5679F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.25F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5926F, KeyframeAnimations.posVec(0.0F, 0.55F, 0.25F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.6173F, KeyframeAnimations.posVec(0.0F, 0.5F, 0.25F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.642F, KeyframeAnimations.posVec(0.0F, 0.43F, 0.25F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.6667F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("posterior", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.0741F, KeyframeAnimations.degreeVec(-5.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.1728F, KeyframeAnimations.degreeVec(-45.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2716F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5679F, KeyframeAnimations.degreeVec(25.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.642F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.6667F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("posterior", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.0741F, KeyframeAnimations.posVec(0.0F, 0.5F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.1728F, KeyframeAnimations.posVec(0.0F, 3.2F, 0.5F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2716F, KeyframeAnimations.posVec(0.0F, 2.15F, -1.8F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4198F, KeyframeAnimations.posVec(0.0F, 0.18F, -1.25F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5679F, KeyframeAnimations.posVec(0.0F, 0.55F, -1.6F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.6667F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("feet", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.0741F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.1728F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2963F, KeyframeAnimations.degreeVec(164.1667F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4938F, KeyframeAnimations.degreeVec(194.1667F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5926F, KeyframeAnimations.degreeVec(77.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.6667F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("feet", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.1235F, KeyframeAnimations.posVec(0.0F, 0.1485F, -0.0374F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.1975F, KeyframeAnimations.posVec(0.0F, 0.2266F, -0.1059F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2963F, KeyframeAnimations.posVec(0.0F, 0.0654F, -0.0539F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.3457F, KeyframeAnimations.posVec(0.0F, 0.0202F, -0.0817F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.3951F, KeyframeAnimations.posVec(0.0F, -0.0189F, -0.0845F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.642F, KeyframeAnimations.posVec(0.0F, 0.8498F, 0.0087F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.6667F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("hands", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.1728F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.3704F, KeyframeAnimations.degreeVec(-42.7778F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5679F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5926F, KeyframeAnimations.degreeVec(0.8333F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.6173F, KeyframeAnimations.degreeVec(0.4167F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.642F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.6667F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("hands", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.1728F, KeyframeAnimations.posVec(0.0F, 0.2309F, 0.4435F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2222F, KeyframeAnimations.posVec(0.0F, 0.094F, -0.0342F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2963F, KeyframeAnimations.posVec(0.0F, 0.143F, -1.0484F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.321F, KeyframeAnimations.posVec(0.0F, 0.2397F, -1.0573F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.3457F, KeyframeAnimations.posVec(0.0F, 0.33F, -1.06F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.3951F, KeyframeAnimations.posVec(0.0F, 0.3693F, -1.0484F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5679F, KeyframeAnimations.posVec(0.0F, 0.3532F, -1.3396F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5926F, KeyframeAnimations.posVec(0.0F, 0.5154F, -1.1079F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.6173F, KeyframeAnimations.posVec(0.0F, 0.3185F, -1.1426F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.6667F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .build();
}

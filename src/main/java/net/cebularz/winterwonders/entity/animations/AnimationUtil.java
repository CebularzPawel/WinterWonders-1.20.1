package net.cebularz.winterwonders.entity.animations;

import java.util.Map;
import java.util.function.UnaryOperator;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.util.Mth;

import org.joml.Vector3f;

public final class AnimationUtil {
    private AnimationUtil() {}

    public static final AnimationChannel.Interpolation EASE = (result, progress, keys, from, to, weight) -> {
        float eased = smoothStep(progress);
        return AnimationChannel.Interpolations.LINEAR.apply(result, eased, keys, from, to, weight);
    };

    public static final AnimationChannel.Interpolation FLOW = (result, progress, keys, from, to, weight) -> {
        float interval = keys[to].timestamp() - keys[from].timestamp();
        float squared = progress * progress, cubed = squared * progress;
        for (int axis = 0; axis < 3; axis++) {
            float value = (2F * cubed - 3F * squared + 1F) * keys[from].target().get(axis)
                    + (cubed - 2F * squared + progress) * interval * flowTangent(keys, from, axis)
                    + (-2F * cubed + 3F * squared) * keys[to].target().get(axis)
                    + (cubed - squared) * interval * flowTangent(keys, to, axis);
            result.setComponent(axis, value * weight);
        }
        return result;
    };

    private static float flowTangent(Keyframe[] keys, int index, int axis) {
        if (index == 0 || index == keys.length - 1) {
            return 0F;
        } else {
            float beforeSpan = keys[index].timestamp() - keys[index - 1].timestamp();
            float afterSpan = keys[index + 1].timestamp() - keys[index].timestamp();
            float beforeSlope = (keys[index].target().get(axis) - keys[index - 1].target().get(axis)) / beforeSpan;
            float afterSlope = (keys[index + 1].target().get(axis) - keys[index].target().get(axis)) / afterSpan;
            if (beforeSlope * afterSlope <= 0F) {
                return 0F;
            } else {
                float beforeWeight = 2F * afterSpan + beforeSpan, afterWeight = afterSpan + 2F * beforeSpan;
                return (beforeWeight + afterWeight) / (beforeWeight / beforeSlope + afterWeight / afterSlope);
            }
        }
    }

    public static Keyframe rot(float time, float x, float y, float z) {
        return new Keyframe(time, KeyframeAnimations.degreeVec(x, y, z), AnimationChannel.Interpolations.CATMULLROM);
    }

    public static Keyframe pos(float time, float x, float y, float z) {
        return new Keyframe(time, KeyframeAnimations.posVec(x, y, z), AnimationChannel.Interpolations.CATMULLROM);
    }

    public static Keyframe easeRot(float time, float x, float y, float z) {
        return new Keyframe(time, KeyframeAnimations.degreeVec(x, y, z), EASE);
    }

    public static Keyframe easePos(float time, float x, float y, float z) {
        return new Keyframe(time, KeyframeAnimations.posVec(x, y, z), EASE);
    }

    public static Keyframe flowRot(float time, float x, float y, float z) {
        return new Keyframe(time, KeyframeAnimations.degreeVec(x, y, z), FLOW);
    }

    public static Keyframe flowPos(float time, float x, float y, float z) {
        return new Keyframe(time, KeyframeAnimations.posVec(x, y, z), FLOW);
    }

    public static AnimationChannel rotation(Keyframe... keys) {
        return new AnimationChannel(AnimationChannel.Targets.ROTATION, keys);
    }

    public static AnimationChannel position(Keyframe... keys) {
        return new AnimationChannel(AnimationChannel.Targets.POSITION, keys);
    }

    public static AnimationChannel loopRotation(float duration, Keyframe... keys) {
        return new AnimationChannel(AnimationChannel.Targets.ROTATION, loopKeys(duration, keys));
    }

    public static AnimationChannel loopPosition(float duration, Keyframe... keys) {
        return new AnimationChannel(AnimationChannel.Targets.POSITION, loopKeys(duration, keys));
    }

    private static Keyframe[] loopKeys(float duration, Keyframe[] keys) {
        if (keys.length == 1) {
            return keys;
        } else {
            Keyframe[] wrapped = new Keyframe[keys.length + 2];
            Keyframe beforeKey = keys[keys.length - 2];
            Keyframe afterKey = keys[1];
            wrapped[0] = new Keyframe(beforeKey.timestamp() - duration, beforeKey.target(), beforeKey.interpolation());
            System.arraycopy(keys, 0, wrapped, 1, keys.length);
            wrapped[wrapped.length - 1] = new Keyframe(afterKey.timestamp() + duration, afterKey.target(), afterKey.interpolation());
            return wrapped;
        }
    }

    public static AnimationChannel flowTrack(AnimationChannel.Target target, float[] times, float[] x, float[] y, float[] z) {
        Keyframe[] keys = new Keyframe[times.length];
        for (int index = 0; index < keys.length; index++) {
            Vector3f value;
            if (target == AnimationChannel.Targets.ROTATION) {
                value = KeyframeAnimations.degreeVec(x[index], y[index], z[index]);
            } else if (target == AnimationChannel.Targets.POSITION) {
                value = KeyframeAnimations.posVec(x[index], y[index], z[index]);
            } else if (target == AnimationChannel.Targets.SCALE) {
                value = KeyframeAnimations.scaleVec(x[index], y[index], z[index]);
            } else {
                value = new Vector3f(x[index], y[index], z[index]);
            }
            keys[index] = new Keyframe(times[index], value, FLOW);
        }
        return new AnimationChannel(target, keys);
    }

    public static float[] scaleValues(float factor, float... values) {
        float[] result = values.clone();
        for (int index = 0; index < result.length; index++) result[index] *= factor;
        return result;
    }

    public static AnimationDefinition retarget(AnimationDefinition source, Vector3f scale, Map<String, Vector3f> boneScales) {
        AnimationDefinition.Builder result = AnimationDefinition.Builder.withLength(source.lengthInSeconds());
        if (source.looping()) {
            result.looping();
        }
        source.boneAnimations().forEach((bone, channels) -> {
            for (AnimationChannel channel : channels) {
                Keyframe[] keys = channel.keyframes();
                if (channel.target() == AnimationChannel.Targets.POSITION) {
                    Keyframe[] scaled = new Keyframe[keys.length];
                    for (int index = 0; index < keys.length; index++) {
                        scaled[index] = new Keyframe(keys[index].timestamp(),
                                new Vector3f(keys[index].target()).mul(boneScales.getOrDefault(bone, scale)), keys[index].interpolation());
                    }
                    keys = scaled;
                }
                result.addAnimation(bone, new AnimationChannel(channel.target(), keys));
            }
        });
        return result.build();
    }

    public static AnimationDefinition retarget(AnimationDefinition source, Vector3f scale) {
        return retarget(source, scale, Map.of());
    }

    public static AnimationDefinition mirror(AnimationDefinition source) {
        return mirror(source, bone -> bone.startsWith("left_") ? "right_" + bone.substring(5)
                : bone.startsWith("right_") ? "left_" + bone.substring(6) : bone);
    }

    public static AnimationDefinition mirror(AnimationDefinition source, UnaryOperator<String> boneMapper) {
        AnimationDefinition.Builder result = AnimationDefinition.Builder.withLength(source.lengthInSeconds());
        if (source.looping()) {
            result.looping();
        }
        source.boneAnimations().forEach((bone, channels) -> {
            String targetBone = boneMapper.apply(bone);
            for (AnimationChannel channel : channels) {
                Keyframe[] keys = new Keyframe[channel.keyframes().length];
                for (int index = 0; index < keys.length; index++) {
                    Keyframe sourceKey = channel.keyframes()[index];
                    Vector3f value = new Vector3f(sourceKey.target());
                    if (channel.target() == AnimationChannel.Targets.POSITION) {
                        value.mul(-1F, 1F, 1F);
                    } else if (channel.target() == AnimationChannel.Targets.ROTATION) {
                        value.mul(1F, -1F, -1F);
                    }
                    keys[index] = new Keyframe(sourceKey.timestamp(), value, sourceKey.interpolation());
                }
                result.addAnimation(targetBone, new AnimationChannel(channel.target(), keys));
            }
        });
        return result.build();
    }

    public static float smoothStep(float value) {
        value = Mth.clamp(value, 0F, 1F);
        return value * value * (3F - 2F * value);
    }

    public static float progress(float time, float start, float end) {
        return Mth.clamp((time - start) / (end - start), 0F, 1F);
    }

    public static float inQuad(float value) {
        return value * value;
    }

    public static float inOutSine(float value) {
        return (1F - Mth.cos(value * Mth.PI)) * .5F;
    }

    public static float inOutExpo(float value) {
        if (value == 0F || value == 1F) {
            return value;
        } else {
            return value < .5F ? (float) Math.pow(2, 20 * value - 10) * .5F
                    : (2F - (float) Math.pow(2, -20 * value + 10)) * .5F;
        }
    }

    public static float outBack(float value) {
        float offset = value - 1F;
        return 1F + 2.70158F * offset * offset * offset + 1.70158F * offset * offset;
    }

    public static float smootherStep(float value) {
        value = Mth.clamp(value, 0F, 1F);
        return value * value * value * (value * (value * 6F - 15F) + 10F);
    }

    public static Vector3f sample(AnimationChannel channel, float time, Vector3f result) {
        Keyframe[] keys = channel.keyframes();
        if (keys.length == 1) {
            return result.set(keys[0].target());
        } else {
            int index = 0;
            while (index < keys.length - 2 && time > keys[index + 1].timestamp()) index++;
            float progress = Math.max(0F, Math.min(1F,
                    (time - keys[index].timestamp()) / (keys[index + 1].timestamp() - keys[index].timestamp())));
            return keys[index + 1].interpolation().apply(result, progress, keys, index, index + 1, 1F);
        }
    }
}

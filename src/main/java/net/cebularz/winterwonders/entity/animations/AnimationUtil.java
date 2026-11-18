package net.cebularz.winterwonders.entity.animations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.DoubleFunction;
import java.util.function.UnaryOperator;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class AnimationUtil {
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
        return new Keyframe(time, KeyframeAnimations.degreeVec(x, y, z), Interpolations.EASE);
    }

    public static Keyframe easePos(float time, float x, float y, float z) {
        return new Keyframe(time, KeyframeAnimations.posVec(x, y, z), Interpolations.EASE);
    }

    public static Keyframe flowRot(float time, float x, float y, float z) {
        return new Keyframe(time, KeyframeAnimations.degreeVec(x, y, z), Interpolations.FLOW);
    }

    public static Keyframe flowPos(float time, float x, float y, float z) {
        return new Keyframe(time, KeyframeAnimations.posVec(x, y, z), Interpolations.FLOW);
    }

    public static AnimationChannel rotation(Keyframe... keys) {
        return new AnimationChannel(AnimationChannel.Targets.ROTATION, keys);
    }

    public static AnimationChannel position(Keyframe... keys) {
        return new AnimationChannel(AnimationChannel.Targets.POSITION, keys);
    }

    public static final class BonePose {
        public float x, y, z, xRot, yRot, zRot;

        public BonePose(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public void set(BonePose source) {
            x = source.x;
            y = source.y;
            z = source.z;
            setRotation(source.xRot, source.yRot, source.zRot);
        }

        public void setRotation(float pitch, float yaw, float roll) {
            xRot = pitch;
            yRot = yaw;
            zRot = roll;
        }

        public void rotate(float pitch, float yaw, float roll) {
            setRotation(pitch * Mth.DEG_TO_RAD, yaw * Mth.DEG_TO_RAD, roll * Mth.DEG_TO_RAD);
        }

        public void rotateTo(BonePose from, float pitch, float yaw, float roll, float weight) {
            xRot = Mth.lerp(weight, from.xRot, pitch * Mth.DEG_TO_RAD);
            yRot = Mth.lerp(weight, from.yRot, yaw * Mth.DEG_TO_RAD);
            zRot = Mth.lerp(weight, from.zRot, roll * Mth.DEG_TO_RAD);
        }

        public void blend(BonePose target, float weight) {
            x = Mth.lerp(weight, x, target.x);
            y = Mth.lerp(weight, y, target.y);
            z = Mth.lerp(weight, z, target.z);
            xRot = Mth.lerp(weight, xRot, target.xRot);
            yRot = Mth.lerp(weight, yRot, target.yRot);
            zRot = Mth.lerp(weight, zRot, target.zRot);
        }

        public void addDifference(BonePose from, BonePose to, float weight) {
            x += (to.x - from.x) * weight;
            y += (to.y - from.y) * weight;
            z += (to.z - from.z) * weight;
            xRot += (to.xRot - from.xRot) * weight;
            yRot += (to.yRot - from.yRot) * weight;
            zRot += (to.zRot - from.zRot) * weight;
        }

        public void transform(Matrix4f transform) {
            transform.translate(x / 16, y / 16, z / 16).rotateZYX(zRot, yRot, xRot);
        }
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
            keys[index] = new Keyframe(times[index], value, Interpolations.FLOW);
        }
        return new AnimationChannel(target, keys);
    }

    public static float[] scaleValues(float factor, float... values) {
        float[] result = values.clone();
        for (int index = 0; index < result.length; index++) {
            result[index] *= factor;
        }
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
        return 1F + 2.702F * offset * offset * offset + 1.702F * offset * offset;
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
            while (index < keys.length - 2 && time > keys[index + 1].timestamp()) {
                index++;
            }
            float progress = Math.max(0F, Math.min(1F,
                    (time - keys[index].timestamp()) / (keys[index + 1].timestamp() - keys[index].timestamp())));
            return keys[index + 1].interpolation().apply(result, progress, keys, index, index + 1, 1F);
        }
    }

    public static float blendWeight(float time, float inStart, float inEnd, float outStart, float outEnd) {
        return smootherStep(time, inStart, inEnd) * (1 - smootherStep(time, outStart, outEnd));
    }

    public static float gather(float time, float start, float end) {
        double value = progress(time, start, end);
        return Mth.clamp((float) (value * value * value * value * (15 + value * (-24 + 10 * value))), 0, 1);
    }

    public static float settle(float time, float start, float end) {
        double value = progress(time, start, end);
        return Mth.clamp((float) (value * value * value * (56 + value * (-210 + value * (336 + value * (-280 + value * (120 - 21 * value)))))), 0, 1);
    }

    public static float turn(float progress) {
        double value = progress;
        return Mth.clamp((float) (value * value * value * value * (126 + value * (-504 + value * (840 + value * (-720 + value * (315 - 56 * value)))))), 0, 1);
    }

    public static float spring(float time, float frequency, float damping, float initial) {
        float decayRate = damping * frequency, oscillation = frequency * (float) Math.sqrt(1 - damping * damping);
        float decay = (float) Math.exp(-decayRate * time), angle = oscillation * time;
        return 1 - (1 - initial) * decay * ((float) Math.cos(angle) + decayRate / oscillation * (float) Math.sin(angle));
    }

    public static float springSpeed(float time, float frequency, float damping, float initial) {
        float decayRate = damping * frequency, oscillation = frequency * (float) Math.sqrt(1 - damping * damping);
        float decay = (float) Math.exp(-decayRate * time), angle = oscillation * time;
        return (1 - initial) * decay * frequency * frequency / oscillation * (float) Math.sin(angle);
    }

    public static float smootherStepRate(float time, float start, float end) {
        float value = progress(time, start, end);
        return 30 * value * value * (1 - value) * (1 - value) / (end - start);
    }

    public static float pulse(float time, float center, float width) {
        float distance = Math.abs(time - center) / width;
        return distance >= 1 ? 0 : .5F + .5F * (float) Math.cos(distance * Math.PI);
    }

    public static float catmullRom(float previous, float start, float end, float next, float progress) {
        return .5F * ((2 * start) + (-previous + end) * progress + (2 * previous - 5 * start + 4 * end - next) * progress * progress +
                (-previous + 3 * start - 3 * end + next) * progress * progress * progress);
    }

    public static float hermite(float progress, float start, float end, float startTangent, float endTangent) {
        float squared = progress * progress, cubed = squared * progress, fourth = cubed * progress, fifth = fourth * progress;
        return start + (end - start) * (10 * cubed - 15 * fourth + 6 * fifth)
                + startTangent * (progress - 6 * cubed + 8 * fourth - 3 * fifth) + endTangent * (-4 * cubed + 7 * fourth - 3 * fifth);
    }

    public static float hermite(float progress, float start, float end, float startTangent, float endTangent, float startAccel, float endAccel) {
        float remaining = 1 - progress;
        return hermite(progress, start, end, startTangent, endTangent) + .5F * startAccel * progress * progress * remaining * remaining * remaining +
                .5F * endAccel * progress * progress * progress * remaining * remaining;
    }

    public static Vector3f hermite(Vector3f start, Vector3f end, Vector3f startVelocity, Vector3f endVelocity, Vector3f startAccel, Vector3f endAccel,
                                   float progress, float duration) {
        float durationSquared = duration * duration;
        return new Vector3f(hermite(progress, start.x, end.x, startVelocity.x * duration, endVelocity.x * duration, startAccel.x * durationSquared,
                endAccel.x * durationSquared),
                hermite(progress, start.y, end.y, startVelocity.y * duration, endVelocity.y * duration, startAccel.y * durationSquared,
                        endAccel.y * durationSquared),
                hermite(progress, start.z, end.z, startVelocity.z * duration, endVelocity.z * duration, startAccel.z * durationSquared,
                        endAccel.z * durationSquared));
    }

    public static float smootherStep(float time, float start, float end) {
        return smootherStep(progress(time, start, end));
    }

    private static final class Interpolations {
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
    }

    public static final class ParticleTrail {
        private final double spacing;
        private double carriedDistance;
        private boolean started;

        public ParticleTrail(double spacing) {
            this.spacing = spacing;
        }

        public void reset() {
            carriedDistance = 0;
            started = false;
        }

        public List<Vec3> sample(DoubleFunction<Vec3> curve, double startTime, double endTime) {
            List<Vec3> path = new ArrayList<>(), particles = new ArrayList<>();
            Vec3 firstPoint = curve.apply(startTime);
            path.add(firstPoint);
            subdivide(path, curve, startTime, endTime, firstPoint, curve.apply(endTime), 0);
            if (!started) {
                particles.add(firstPoint);
                started = true;
            }
            for (int i = 1; i < path.size(); i++) {
                Vec3 fromPoint = path.get(i - 1), toPoint = path.get(i);
                double segmentLength = fromPoint.distanceTo(toPoint), traveled = 0;
                if (segmentLength < 1e-9) {
                    continue;
                }
                while (carriedDistance + segmentLength - traveled >= spacing) {
                    traveled += spacing - carriedDistance;
                    carriedDistance = 0;
                    particles.add(fromPoint.lerp(toPoint, Math.min(1, traveled / segmentLength)));
                    if (particles.size() >= 128) {
                        reset();
                        return particles;
                    }
                }
                carriedDistance += segmentLength - traveled;
            }
            return particles;
        }

        private void subdivide(List<Vec3> path, DoubleFunction<Vec3> curve, double startTime, double endTime, Vec3 startPoint, Vec3 endPoint, int depth) {
            double midTime = (startTime + endTime) * .5;
            Vec3 midPoint = curve.apply(midTime);
            double directDistance = startPoint.distanceTo(endPoint), curveDistance = startPoint.distanceTo(midPoint) + midPoint.distanceTo(endPoint);
            if (depth < 7 && (depth < 2 || directDistance > spacing * .5 || curveDistance - directDistance > 5e-4)) {
                subdivide(path, curve, startTime, midTime, startPoint, midPoint, depth + 1);
                subdivide(path, curve, midTime, endTime, midPoint, endPoint, depth + 1);
            } else {
                path.add(endPoint);
            }
        }
    }

}

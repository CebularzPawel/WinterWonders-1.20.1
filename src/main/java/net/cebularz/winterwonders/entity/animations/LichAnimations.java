package net.cebularz.winterwonders.entity.animations;

import java.util.Arrays;

import net.cebularz.winterwonders.entity.custom.LichEntity.CastState;
import net.cebularz.winterwonders.entity.custom.LichEntity.CastState.Kind;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static net.cebularz.winterwonders.entity.animations.AnimationUtil.*;

public final class LichAnimations {
    public static final class Pose {
        public final BonePose root = new BonePose(0, 0, 0);
        public final BonePose body = new BonePose(0, 0, 0);
        public final BonePose head = new BonePose(0, 0, 0);
        public final BonePose rightArm = new BonePose(-5, 2, 0);
        public final BonePose leftArm = new BonePose(5, 2, 0);
        public final BonePose leftLeg = new BonePose(2, 12, .1F);
        public final BonePose rightLeg = new BonePose(-2, 12, .1F);
        private final BonePose[] parts = {root, body, head, rightArm, leftArm, leftLeg, rightLeg};
        public float staffPitch;
        public Vector3f staffPosition;
        public Quaternionf staffOrientation;

        private Pose copy() {
            Pose copy = new Pose();
            copy.set(this);
            copy.staffPitch = staffPitch;
            copy.staffPosition = staffPosition == null ? null : new Vector3f(staffPosition);
            copy.staffOrientation = staffOrientation == null ? null : new Quaternionf(staffOrientation);
            return copy;
        }

        private void set(Pose source) {
            for (int part = 0; part < parts.length; part++) {
                parts[part].set(source.parts[part]);
            }
        }

        private void blend(Pose target, float weight) {
            for (int part = 0; part < parts.length; part++) {
                parts[part].blend(target.parts[part], weight);
            }
        }

        private void addDifference(Pose from, Pose to, float weight) {
            for (int part = 0; part < parts.length; part++) {
                parts[part].addDifference(from.parts[part], to.parts[part], weight);
            }
        }
    }

    private static final float[][] ROOT_IDLE = {{0, 3, 0}, {0, 3.7F, 0}, {0, 3, 0}, {0, 2.3F, 0}};
    private static final float[][] BODY_IDLE = {{-2, 0, -1.5F}, {-1, 1.5F, 0}, {-2, 0, 1.5F}, {-3, -1.5F, 0}};
    private static final float[][] HEAD_IDLE = {{2, 0, 1}, {1, -1, 0}, {2, 0, -1}, {3, 1, 0}};
    private static final float[][] RIGHT_ARM_IDLE = {{-12, -6, 8}, {-15, -7, 9}, {-12, -6, 10}, {-9, -5, 9}};
    private static final float[][] LEFT_ARM_IDLE = {{-4, 0, -9}, {-2, 2, -11}, {-4, 0, -13}, {-6, -2, -11}};
    private static final float[][] LEFT_LEG_IDLE = {{7, 0, -3}, {4, 0, -2}, {7, 0, -3}, {10, 0, -4}};
    private static final float[][] RIGHT_LEG_IDLE = {{-3, 0, 3}, {0, 0, 4}, {-3, 0, 3}, {-6, 0, 2}};

    private static Pose idle(float seconds) {
        Pose pose = new Pose();
        float phase = ((seconds % 4.8F) + 4.8F) % 4.8F / 1.2F;
        int frame = (int) phase;
        float frameProgress = phase - frame;
        pose.root.x += idleValue(ROOT_IDLE, frame, frameProgress, 0);
        pose.root.y += -idleValue(ROOT_IDLE, frame, frameProgress, 1);
        pose.root.z += idleValue(ROOT_IDLE, frame, frameProgress, 2);
        idleRotation(pose.body, BODY_IDLE, frame, frameProgress);
        idleRotation(pose.head, HEAD_IDLE, frame, frameProgress);
        idleRotation(pose.rightArm, RIGHT_ARM_IDLE, frame, frameProgress);
        idleRotation(pose.leftArm, LEFT_ARM_IDLE, frame, frameProgress);
        idleRotation(pose.leftLeg, LEFT_LEG_IDLE, frame, frameProgress);
        idleRotation(pose.rightLeg, RIGHT_LEG_IDLE, frame, frameProgress);
        return pose;
    }

    private static float idleValue(float[][] keys, int frame, float progress, int axis) {
        return catmullRom(keys[(frame + 3) % 4][axis], keys[frame][axis],
                keys[(frame + 1) % 4][axis], keys[(frame + 2) % 4][axis], progress);
    }

    private static void idleRotation(BonePose part, float[][] keys, int frame, float progress) {
        part.rotate(idleValue(keys, frame, progress, 0), idleValue(keys, frame, progress, 1), idleValue(keys, frame, progress, 2));
    }

    private static float reverseSource(float time) {
        float sweepTime = Mth.lerp(smootherStep(time, .75F, 1.02F), 1.7F, .75F);
        return time > 1.8F ? Mth.lerp(smootherStep(time, 1.8F, 2.6F), .75F, .15F) : sweepTime;
    }

    private static float cubeFirstSendDuration(CastState cast) {
        int[] releases = Arrays.stream(cast.releases()).sorted().distinct().toArray();
        return releases.length > 1 ? Math.min(.24F, (releases[1] - releases[0]) * .019F) : .24F;
    }

    private static float cubeClock(CastState cast, float time) {
        float releaseTime = CastState.CUBE_RELEASE_TICKS / 20F;
        if (time < releaseTime) {
            return hermite(progress(time, 0, releaseTime), 0, .9F, releaseTime, releaseTime);
        }
        float settleDuration = cubeFirstSendDuration(cast);
        return hermite(progress(time, releaseTime, releaseTime + settleDuration), .9F, .9F + settleDuration * .5F, settleDuration, 0);
    }

    private static float cubeGuidePhase(CastState cast, float time) {
        int[] releases = Arrays.stream(cast.releases()).sorted().distinct().toArray();
        if (releases.length == 0 || time <= releases[0] * .05F) {
            return 0;
        }
        int nextIndex = 0;
        while (nextIndex < releases.length && time >= releases[nextIndex] * .05F) {
            nextIndex++;
        }
        int sendIndex = nextIndex - 1;
        float releaseTime = releases[sendIndex] * .05F;
        float nextTime = nextIndex < releases.length ? releases[nextIndex] * .05F : Float.POSITIVE_INFINITY;
        float sendEnd = releaseTime + Math.min(.24F, (nextTime - releaseTime) * .38F);
        if (time < sendEnd) {
            return (sendIndex + .5F * smootherStep(time, releaseTime, sendEnd)) * Mth.TWO_PI;
        }
        if (nextIndex == releases.length) {
            return (sendIndex + .5F) * Mth.TWO_PI;
        }
        float windUp = Math.min(.55F, (nextTime - sendEnd) * .7F);
        return (sendIndex + .5F + .5F * smootherStep(time, nextTime - windUp, nextTime)) * Mth.TWO_PI;
    }

    private static float cubeSendAccent(float phase) {
        float arc = Math.max(0, (float) Math.sin(phase));
        return arc * arc;
    }

    private static float cubeArmRest(CastState cast, float time) {
        int[] releases = cast.releases();
        float lastRelease = -1, nextRelease = Float.POSITIVE_INFINITY;
        for (int tick : releases) {
            float releaseTime = tick * .05F;
            if (releaseTime <= time) {
                lastRelease = Math.max(lastRelease, releaseTime);
            } else {
                nextRelease = Math.min(nextRelease, releaseTime);
            }
        }
        if (lastRelease < 0) {
            return 0;
        }
        float impactTime = -1;
        for (int slot = 0; slot < releases.length; slot++) {
            if (releases[slot] * .05F == lastRelease) {
                if (cast.cubeImpactTick(slot) < 0) {
                    return 0;
                }
                impactTime = Math.max(impactTime, cast.cubeImpactTick(slot) * .05F);
            }
        }
        float sendEnd = lastRelease + Math.min(.24F, (nextRelease - lastRelease) * .38F);
        float restTime = Math.max(impactTime, sendEnd), restDuration = .95F;
        float windUp = Float.isFinite(nextRelease) ? Math.min(.55F, (nextRelease - sendEnd) * .7F) : 0;
        if (Float.isFinite(nextRelease)) {
            restDuration = Math.min(restDuration, Math.max(.7F, (nextRelease - windUp - restTime) * .9F));
        }
        float restWeight = gather(time, restTime, restTime + restDuration);
        if (Float.isFinite(nextRelease)) {
            restWeight *= 1 - smootherStep(time, nextRelease - windUp, nextRelease);
        }
        return restWeight;
    }

    private static void animateCast(Pose pose, CastState cast, float castTime, float cubePhase) {
        boolean reverse = cast.kind() == Kind.SPIKES && cast.reverse();
        float time = reverse ? reverseSource(castTime) : castTime;
        float releaseTime = switch (cast.kind()) {
            case SNOWBALL, ICICLE -> .8F;
            case CUBES -> 1.4F;
            case SPIKES, WHIRLWIND -> .9F;
            case BLIZZARD -> 1.1F;
            case SUMMON -> 1;
            default -> 0;
        };
        float gather, launch;
        if (cast.kind() == Kind.CUBES) {
            float firstRelease = CastState.CUBE_RELEASE_TICKS * .05F;
            gather = smootherStep(time, .15F, firstRelease);
            launch = smootherStep(time, firstRelease, firstRelease + cubeFirstSendDuration(cast));
        } else {
            gather = cast.kind() == Kind.WHIRLWIND ? gather(time, .15F, releaseTime - .12F)
                    : smootherStep(time, .15F, cast.kind() == Kind.BLIZZARD ? releaseTime : releaseTime - .12F);
            launch = smootherStep(time, releaseTime - .15F, releaseTime + .12F);
        }
        float recoveryTime = cast.recoverySeconds();
        float recovery = 1 - smootherStep(time, recoveryTime, recoveryTime + (cast.kind() == Kind.SUMMON ? 1 : .8F));
        float offhandRecovery = recovery;
        if (cast.kind() == Kind.WHIRLWIND) {
            recovery = 1 - settle(time, 1.65F, 2.65F);
            offhandRecovery = 1 - settle(time, 1.7F, 2.75F);
        }
        float bodyPitch = 0, bodyYaw = 0, bodyRoll = 0;
        float rightPitch = 0, rightYaw = 0, rightRoll = 0;
        float leftPitch = 0, leftYaw = 0, leftRoll = 0, hover = 0, turn = 0;
        switch (cast.kind()) {
            case SNOWBALL, ICICLE -> {
                bodyPitch = Mth.lerp(launch, -7, 7) * gather * recovery;
                bodyYaw = Mth.lerp(launch, 20, -9) * gather * recovery;
                bodyRoll = Mth.lerp(launch, -4, 3) * gather * recovery;
                hover = Mth.lerp(launch, .7F, .8F) * gather * recovery;
                float volley = 0;
                for (int shot = 0; shot < cast.releaseCount(); shot++) {
                    volley += pulse(time, cast.releaseSeconds(shot) + .07F, cast.enraged() ? .12F : .18F);
                }
                bodyYaw -= volley * 4;
            }
            case CUBES -> {
                bodyPitch = Mth.lerp(launch, -5, 3) * gather * recovery;
                bodyYaw = Mth.lerp(launch, -8, 5) * gather * recovery;
                bodyRoll = Mth.lerp(launch, -2, 2) * gather * recovery;
                hover = Mth.lerp(launch, 1.5F, 1) * gather * recovery;
            }
            case SPIKES -> {
                bodyPitch = Mth.lerp(launch, -8, 11) * gather * recovery;
                bodyYaw = Mth.lerp(launch, -15, 13) * gather * recovery;
                bodyRoll = Mth.lerp(launch, -4, 4) * gather * recovery;
                rightPitch = Mth.lerp(launch, -102, 14) * gather * recovery;
                rightYaw = Mth.lerp(launch, 12, -18) * gather * recovery;
                rightRoll = Mth.lerp(launch, 55, 24) * gather * recovery;
                leftPitch = Mth.lerp(launch, -36, -72) * gather * recovery;
                leftYaw = Mth.lerp(launch, -5, -32) * gather * recovery;
                leftRoll = Mth.lerp(launch, -25, -34) * gather * recovery;
                hover = Mth.lerp(launch, .8F, -.7F) * gather * recovery;
                float rake = smootherStep(time, .85F, 1.7F) * recovery;
                float offhandRake = cast.reverse() ? rake : smootherStep(time, .75F, 1.02F) * recovery;
                leftYaw += offhandRake * 50;
                bodyYaw += rake * 8;
            }
            case BLIZZARD -> {
                bodyPitch = -7 * gather * recovery;
                bodyYaw = 4 * gather * recovery;
                bodyRoll = 2 * gather * recovery;
                hover = 2 * gather * recovery;
                float draw = smootherStep(time, .15F, .6F), open = smootherStep(time, .6F, 1.1F), settle = smootherStep(time, 1.1F, 1.65F);
                leftPitch = Mth.lerp(open, -62 * draw, Mth.lerp(settle, -167, -155)) * recovery;
                leftYaw = Mth.lerp(open, 30 * draw, -8 + 3 * settle) * recovery;
                leftRoll = Mth.lerp(open, 25 * draw, Mth.lerp(settle, 52, 42)) * recovery;
                hover += .65F * smootherStep(time, .65F, 1.1F) * (1 - settle);
            }
            case WHIRLWIND -> {
                bodyPitch = Mth.lerp(launch, -3, -2) * gather * recovery;
                bodyYaw = Mth.lerp(launch, -30, 27) * gather * recovery;
                bodyRoll = Mth.lerp(launch, -3, 0) * gather * recovery;
                leftPitch = Mth.lerp(launch, -62, -70) * gather * offhandRecovery;
                leftYaw = Mth.lerp(launch, -16, 22) * gather * offhandRecovery;
                leftRoll = Mth.lerp(launch, -30, -55) * gather * offhandRecovery;
                hover = Mth.lerp(launch, .9F, 1.3F) * gather * recovery;
                turn = 360 * turn(progress(time, .75F, 2.05F));
            }
            case SUMMON -> {
                bodyPitch = Mth.lerp(launch, 5, -7) * gather * recovery;
                bodyYaw = Mth.lerp(launch, -4, 4) * gather * recovery;
                bodyRoll = Mth.lerp(launch, 1, -1) * gather * recovery;
                leftPitch = Mth.lerp(launch, -20, -92) * gather * recovery;
                leftYaw = Mth.lerp(launch, -8, -12) * gather * recovery;
                leftRoll = Mth.lerp(launch, -35, -38) * gather * recovery;
                hover = Mth.lerp(launch, .3F, 1.5F) * gather * recovery;
            }
            default -> {
            }
        }
        if (reverse) {
            float gatherWeight = smootherStep(castTime, .15F, .78F);
            bodyPitch *= gatherWeight;
            bodyYaw *= gatherWeight;
            bodyRoll *= gatherWeight;
            rightPitch *= gatherWeight;
            rightYaw *= gatherWeight;
            rightRoll *= gatherWeight;
            leftPitch *= gatherWeight;
            leftYaw *= gatherWeight;
            leftRoll *= gatherWeight;
            hover *= gatherWeight;
            turn *= gatherWeight;
        }
        float cubePitch = 0, cubeYaw = 0, cubeRoll = 0, cubeHover = 0;
        if (cast.kind() == Kind.CUBES) {
            float guide = .5F - .5F * (float) Math.cos(cubePhase), send = cubeSendAccent(cubePhase);
            float weight = blendWeight(time, .15F, CastState.CUBE_RELEASE_TICKS / 20F, cast.recoverySeconds(), cast.recoverySeconds() + .8F);
            cubePitch = (-8 + 21 * guide + 3 * send) * weight;
            cubeYaw = (-10 + 23 * guide) * weight;
            cubeRoll = (4 * (float) Math.sin(cubePhase) - 2 * send) * weight;
            cubeHover = (1 - 2.5F * guide - .35F * send) * weight;
        }
        pose.body.xRot += (bodyPitch + cubePitch) * Mth.DEG_TO_RAD;
        pose.body.yRot += (bodyYaw + cubeYaw) * Mth.DEG_TO_RAD;
        pose.body.zRot += (bodyRoll + cubeRoll) * Mth.DEG_TO_RAD;
        pose.head.xRot -= .55F * bodyPitch * Mth.DEG_TO_RAD;
        pose.head.yRot -= .55F * bodyYaw * Mth.DEG_TO_RAD;
        pose.head.zRot -= .55F * bodyRoll * Mth.DEG_TO_RAD;
        pose.rightArm.xRot += rightPitch * Mth.DEG_TO_RAD;
        pose.rightArm.yRot += rightYaw * Mth.DEG_TO_RAD;
        pose.rightArm.zRot += rightRoll * Mth.DEG_TO_RAD;
        pose.leftArm.xRot += leftPitch * Mth.DEG_TO_RAD;
        pose.leftArm.yRot += leftYaw * Mth.DEG_TO_RAD;
        pose.leftArm.zRot += leftRoll * Mth.DEG_TO_RAD;
        pose.root.y -= hover + cubeHover;
        pose.root.yRot += turn * Mth.DEG_TO_RAD;
        float lean = bodyPitch + cubePitch;
        pose.leftLeg.xRot -= .4F * lean * Mth.DEG_TO_RAD;
        pose.rightLeg.xRot -= .25F * lean * Mth.DEG_TO_RAD;
    }

    public static Pose sample(CastState cast, float seconds, float ageSeconds) {
        float time = Math.max(0, seconds);
        if (cast.kind() == Kind.NONE) {
            return idle(ageSeconds);
        }
        if (cast.ice()) {
            return ice(cast, time, ageSeconds);
        }
        float clock = cast.kind() == Kind.CUBES ? cubeClock(cast, time) : time;
        float castWeight = 1 - smootherStep(time, cast.recoverySeconds(), cast.recoverySeconds() + .8F);
        Pose pose = idle(clock);
        if (cast.kind() == Kind.CUBES) {
            Pose idlePose = idle(ageSeconds);
            pose.blend(idlePose, 1 - castWeight);
            pose.root.y = idlePose.root.y;
        }
        Pose basePose = pose.copy();
        float cubePhase = cast.kind() == Kind.CUBES ? cubeGuidePhase(cast, time) : 0;
        animateCast(pose, cast, time, cubePhase);
        switch (cast.kind()) {
            case SNOWBALL, ICICLE -> {
                float hold = smootherStep(time, .15F, .75F) * castWeight;
                pose.rightArm.rotateTo(basePose.rightArm, -59, -17, 41, hold);
                pose.staffPitch = 55.6F * Mth.DEG_TO_RAD * hold;
                float recoil = 0, recoilDuration = cast.enraged() ? .22F : .28F;
                for (int shot = 0; shot < cast.releaseCount(); shot++) {
                    float shotTime = cast.releaseSeconds(shot);
                    recoil += blendWeight(time, shotTime, shotTime + .05F, shotTime + .05F, shotTime + recoilDuration);
                }
                pose.rightArm.z += (cast.enraged() ? 2.4F : 2) * recoil;
                pose.leftArm.setRotation(basePose.leftArm.xRot, basePose.leftArm.yRot, basePose.leftArm.zRot);
                pose.leftArm.xRot += 18 * Mth.DEG_TO_RAD * hold;
            }
            case SUMMON -> {
                float hold = blendWeight(time, .15F, 1.15F, cast.recoverySeconds(), cast.recoverySeconds() + 1);
                pose.rightArm.rotateTo(basePose.rightArm, -19, 27.1F, 54.2F, hold);
                pose.staffPitch = 52.5F * Mth.DEG_TO_RAD * hold;
            }
            case BLIZZARD -> {
                float hold = smootherStep(time, .15F, 1.1F) * castWeight;
                pose.rightArm.rotateTo(basePose.rightArm, -124.9F, 68.6F, 19.7F, hold);
                pose.staffPitch = 54.6F * Mth.DEG_TO_RAD * hold;
            }
            case WHIRLWIND -> {
                float hold = gather(time, .15F, .9F) * (1 - settle(time, 1.8F, 2.85F));
                pose.rightArm.rotateTo(basePose.rightArm, 34.3F, 14.7F, 34.3F, hold);
                pose.staffPitch = 49.4F * Mth.DEG_TO_RAD * hold;
            }
            case CUBES -> {
                float raiseTime = CastState.CUBE_POINT_START_TICKS / 20F, pointTime = CastState.CUBE_POINT_END_TICKS / 20F;
                pose.rightArm.xRot = Mth.lerp(castWeight, basePose.rightArm.xRot, cubeRaise(time, -.209F, -2.998F, -19, 0, true));
                pose.rightArm.yRot = Mth.lerp(castWeight, basePose.rightArm.yRot, cubeRaise(time, -.105F, -.285F, 27.1F, 25 * Mth.DEG_TO_RAD, false));
                pose.rightArm.zRot = Mth.lerp(castWeight, basePose.rightArm.zRot, cubeRaise(time, .14F, -.15F, 54.2F, 35 * Mth.DEG_TO_RAD, false));
                pose.staffPitch = (time < raiseTime ? hermite(progress(time, .15F, raiseTime), 0, -4 * Mth.DEG_TO_RAD, 0, 18 * Mth.DEG_TO_RAD * (raiseTime - .15F))
                        : hermite(progress(time, raiseTime, pointTime), -4 * Mth.DEG_TO_RAD, 52.5F * Mth.DEG_TO_RAD, 18 * Mth.DEG_TO_RAD * (pointTime - raiseTime), 0)) * castWeight;

                float phase = cubePhase, guide = .5F - .5F * (float) Math.cos(phase);
                float arc = (float) Math.sin(phase), lift = smootherStep(time, .15F, CastState.CUBE_RELEASE_TICKS * .05F) * castWeight;
                float send = cubeSendAccent(phase);
                pose.root.x += .6F * arc * lift;
                pose.root.z -= 1.5F * guide * lift;
                pose.leftArm.xRot = basePose.leftArm.xRot + (Mth.lerp(guide, -145, -92) + 6 * send) * Mth.DEG_TO_RAD * lift;
                pose.leftArm.yRot = basePose.leftArm.yRot + (Mth.lerp(guide, 5, -12) + (16 * arc - 3 * send)) * Mth.DEG_TO_RAD * lift;
                pose.leftArm.zRot = basePose.leftArm.zRot + (Mth.lerp(guide, 26, -38) + (-12 * arc - 4 * send)) * Mth.DEG_TO_RAD * lift;
                float restWeight = cubeArmRest(cast, time);
                pose.leftArm.xRot = Mth.lerp(restWeight, pose.leftArm.xRot, basePose.leftArm.xRot + 18 * Mth.DEG_TO_RAD * castWeight);
                pose.leftArm.yRot = Mth.lerp(restWeight, pose.leftArm.yRot, basePose.leftArm.yRot - 6 * Mth.DEG_TO_RAD * castWeight);
                pose.leftArm.zRot = Mth.lerp(restWeight, pose.leftArm.zRot, basePose.leftArm.zRot - 10 * Mth.DEG_TO_RAD * castWeight);

                float restArc = (float) Math.sin(Math.PI * Mth.clamp(restWeight, 0, 1)) * castWeight;
                pose.leftArm.yRot -= 18 * Mth.DEG_TO_RAD * restArc;
                pose.leftArm.zRot += 14 * Mth.DEG_TO_RAD * restArc;
            }
            case SPIKES -> {
                float sweepTime = cast.reverse() ? reverseSource(time) : time;
                pose.staffPitch = (-55 + 100 * smootherStep(sweepTime, .72F, .98F)) * Mth.DEG_TO_RAD * smootherStep(sweepTime, .15F, .7F) *
                        (1 - smootherStep(sweepTime, 1.8F, 2.6F));
                if (cast.reverse()) {
                    pose.staffPitch *= smootherStep(time, .15F, .78F);
                    pose.staffPitch += 18 * Mth.DEG_TO_RAD * smootherStep(time, .15F, .78F) * (1 - smootherStep(time, .75F, 1.02F));
                }
            }
            default -> {
            }
        }
        float idleWeight =
                (1 - smootherStep(time, 0, .15F)) + (cast.kind() == Kind.CUBES ? 0 : smootherStep(time, cast.durationSeconds() - .15F, cast.durationSeconds()));
        if (idleWeight > 0) {
            Pose idlePose = idle(ageSeconds);
            pose.addDifference(basePose, idlePose, idleWeight);
        }
        if (cast.kind() == Kind.SPIKES) {
            float sweepTime = cast.reverse() ? reverseSource(time) : time;
            float pitchLimit = Mth.lerp(smootherStep(sweepTime, .4F, .7F), -40, -48) * Mth.DEG_TO_RAD, pitchMargin = 5 * Mth.DEG_TO_RAD;
            float totalPitch = pose.rightArm.xRot + pose.staffPitch;
            if (totalPitch < pitchLimit + pitchMargin) {
                pose.staffPitch = pitchLimit + pitchMargin * (float) Math.exp((totalPitch - pitchLimit - pitchMargin) / pitchMargin) - pose.rightArm.xRot;
            }
        }
        return pose;
    }

    private static float cubeRaise(float time, float rest, float raised, float target, float speed, boolean pitch) {
        float raiseTime = CastState.CUBE_POINT_START_TICKS / 20F, pointTime = CastState.CUBE_POINT_END_TICKS / 20F;
        float duration = time < raiseTime ? raiseTime - .15F : pointTime - raiseTime;
        float acceleration = pitch ? 480 * Mth.DEG_TO_RAD * duration * duration : 0;
        return time < raiseTime ? hermite(progress(time, .15F, raiseTime), rest, raised, 0, speed * duration, 0, acceleration)
                : hermite(progress(time, raiseTime, pointTime), raised, target * Mth.DEG_TO_RAD, speed * duration, 0, acceleration, 0);
    }

    private static final Pose ICE_HOLD = iceHold();
    private static final Pose ICE_BREAK = iceBreak();

    private static Pose iceHold() {
        Pose pose = new Pose();
        pose.root.y = -.332F;
        pose.body.setRotation(-.228F, .025F, -.04F);
        pose.head.setRotation(-.128F, -.017F, .023F);
        pose.rightArm.setRotation(-.885F, 1.208F, .694F);
        pose.leftArm.setRotation(-.281F, .208F, -1.861F);
        pose.leftLeg.y = 12.138F;
        pose.leftLeg.setRotation(.401F, 0, -.28F);
        pose.rightLeg.setRotation(.189F, 0, .278F);
        pose.staffPitch = 5 * Mth.DEG_TO_RAD;
        return pose;
    }

    private static Pose iceBreak() {
        Pose pose = new Pose();
        pose.root.y = -6;
        pose.root.z = .9F;
        pose.body.rotate(-21, 8, -3);
        pose.head.rotate(-12, -6, 0);
        pose.rightArm.rotate(45, 60, 145);
        pose.leftArm.rotate(-75, -15, -80);
        pose.leftLeg.rotate(32, 0, -23);
        pose.rightLeg.rotate(-2, 0, 23);
        return pose;
    }

    private static Pose ice(CastState cast, float time, float ageSeconds) {
        Pose pose = idle(ageSeconds);
        boolean entering = cast.kind() == Kind.ICE_BLOCK;
        float dockTime = CastState.ICE_STAFF_RETURN_TICKS / 20F;
        float freezeWeight = entering ? smootherStep(time, .15F, CastState.ICE_BLOCK_SETTLE_TICKS / 20F)
                : 1 - smootherStep(time, CastState.ICE_SHATTER_TICKS * .05F + .4F, CastState.ICE_THAW_END_TICKS * .05F);
        if (freezeWeight == 1) {
            pose.set(ICE_HOLD);
        } else {
            pose.blend(ICE_HOLD, freezeWeight);
        }
        if (!entering && time >= dockTime && time < CastState.ICE_PUSH_START_TICKS * .05F) {
            float shiverProgress = progress(time, dockTime, CastState.ICE_PUSH_START_TICKS * .05F);
            float shiverWeight = 16 * shiverProgress * shiverProgress * (1 - shiverProgress) * (1 - shiverProgress);
            float shiver = (float) Math.sin(shiverProgress * Math.PI * 4) * shiverWeight;
            pose.root.x += shiver;
            pose.root.z += .35F * (float) Math.sin(shiverProgress * Math.PI * 6) * shiverWeight;
            pose.root.yRot += .8F * Mth.DEG_TO_RAD * shiver;
        }
        if (!entering) {
            iceBreakMotion(pose, time);
        }
        pose.staffPitch = ICE_HOLD.staffPitch * freezeWeight;
        float departTime = CastState.ICE_STAFF_DEPART_TICKS / 20F;
        if (entering && time > departTime || !entering && time < dockTime) {
            Matrix4f gripTransform = staffPalm(pose);
            Vector3f gripPosition = gripTransform.getTranslation(new Vector3f());
            Quaternionf gripRotation = gripTransform.getUnnormalizedRotation(new Quaternionf()).normalize();
            StaffFrame staffPose = entering ? iceDeparture(gripPosition, gripRotation, time)
                    : iceReturn(gripPosition, gripRotation, time, cast.releaseCount() > 0 ? cast.releaseSeconds(0) : 30);
            pose.staffPosition = staffPose.position;
            pose.staffOrientation = staffPose.orientation;
        }
        return pose;
    }

    private static void iceBreakMotion(Pose pose, float time) {
        float pushTime = CastState.ICE_PUSH_START_TICKS * .05F, thawTime = CastState.ICE_THAW_END_TICKS * .05F;
        if (time <= pushTime || time >= thawTime) {
            return;
        }
        float shatterTime = CastState.ICE_SHATTER_TICKS * .05F;
        float rootFrequency = (float) Math.PI / ((CastState.ICE_PUSH_PEAK_TICKS * .05F - shatterTime) * (float) Math.sqrt(1 - .62F * .62F));
        float rootFollow = icePush(pose.root, ICE_BREAK.root, time, 0, rootFrequency, .62F, .48F, 0);
        float bodyFollow = icePush(pose.body, ICE_BREAK.body, time, .08F, 13, .64F, .42F, -.02F);
        icePush(pose.head, ICE_BREAK.head, time, .04F, 15, .72F, .38F, -.1F);
        float rightFollow = icePush(pose.rightArm, ICE_BREAK.rightArm, time, .13F, 16, .55F, .36F, -.05F);
        float leftFollow = icePush(pose.leftArm, ICE_BREAK.leftArm, time, .17F, 19, .5F, .3F, -.1F);
        float leftLegFollow = icePush(pose.leftLeg, ICE_BREAK.leftLeg, time, .04F, 14, .54F, .42F, -.02F);
        float rightLegFollow = icePush(pose.rightLeg, ICE_BREAK.rightLeg, time, .035F, 13, .58F, .46F, 0);
        pose.root.z -= .6F * rootFollow;
        pose.body.yRot -= 4 * Mth.DEG_TO_RAD * bodyFollow;
        pose.body.zRot -= 2 * Mth.DEG_TO_RAD * bodyFollow;
        pose.rightArm.xRot -= 4 * rightFollow * Mth.DEG_TO_RAD;
        pose.rightArm.yRot += 4 * rightFollow * Mth.DEG_TO_RAD;
        pose.rightArm.zRot -= 6 * rightFollow * Mth.DEG_TO_RAD;
        pose.leftArm.yRot -= 12 * leftFollow * Mth.DEG_TO_RAD;
        pose.leftArm.zRot -= 8 * leftFollow * Mth.DEG_TO_RAD;
        pose.leftLeg.xRot += 3 * leftLegFollow * Mth.DEG_TO_RAD;
        pose.rightLeg.xRot -= 3 * rightLegFollow * Mth.DEG_TO_RAD;
        if (time < shatterTime) {
            float strainProgress = progress(time, pushTime, shatterTime);
            float tremble = 16 * strainProgress * strainProgress * (1 - strainProgress) * (1 - strainProgress) * (float) Math.sin(strainProgress * Math.PI * 6);
            pose.rightArm.xRot += .65F * Mth.DEG_TO_RAD * tremble;
            pose.leftArm.xRot += -.65F * Mth.DEG_TO_RAD * tremble;
        }
    }

    private static float icePush(BonePose part, BonePose target, float time, float strain, float frequency, float damping, float relax, float finish) {
        float pushTime = CastState.ICE_PUSH_START_TICKS * .05F, shatterTime = CastState.ICE_SHATTER_TICKS * .05F;
        float extension, pushSpeed = 0;
        if (time < shatterTime) {
            extension = strain * settle(time, pushTime, shatterTime);
        } else {
            float elapsed = time - shatterTime;
            extension = spring(elapsed, frequency, damping, strain);
            pushSpeed = springSpeed(elapsed, frequency, damping, strain);
        }
        float relaxTime = shatterTime + relax, restTime = CastState.ICE_THAW_END_TICKS * .05F + finish;
        float recovery = smootherStep(time, relaxTime, restTime), recoverSpeed = smootherStepRate(time, relaxTime, restTime);
        float pushWeight = extension * (1 - recovery);
        float followThrough = Mth.clamp((pushSpeed * (1 - recovery) - extension * recoverSpeed) / 6F, -1, 1)
                * smootherStep(time, shatterTime, shatterTime + .08F);
        part.blend(target, pushWeight);
        return followThrough;
    }

    private static final float STAFF_UPRIGHT = -81.055F * Mth.DEG_TO_RAD;
    private static final float ICE_HAND_YAW = iceHandYaw();

    private record StaffFrame(Vector3f position, Quaternionf orientation) {}

    private static StaffFrame iceDeparture(Vector3f gripPosition, Quaternionf gripRotation, float time) {
        float elapsed = time - CastState.ICE_STAFF_DEPART_TICKS * .05F;
        float duration = (CastState.ICE_STAFF_RISE_TICKS - CastState.ICE_STAFF_DEPART_TICKS) * .05F;
        if (elapsed >= duration) {
            return hover(time);
        }
        float turnDuration = duration - .18F;
        float turnAngle = hermite(progress(elapsed, .18F, duration), 0, Mth.TWO_PI, 0, 65 * Mth.DEG_TO_RAD * turnDuration, 0,
                -37.5F * Mth.DEG_TO_RAD * turnDuration * turnDuration);
        float orbitWeight = smootherStep(elapsed, 0, .7F), orbitFade = 1 - smootherStep(elapsed, 2.35F, duration);
        float orbitAngle = (float) Math.atan2(gripPosition.z, gripPosition.x), radius =
                (float) Math.sqrt(gripPosition.x * gripPosition.x + gripPosition.z * gripPosition.z);
        radius = (Mth.lerp(orbitWeight, radius, 1.25F) + .16F * (float) Math.sin(turnAngle * .65F) * orbitWeight) * orbitFade;
        Vector3f position = new Vector3f(radius * (float) Math.cos(orbitAngle - turnAngle),
                Mth.lerp(smootherStep(elapsed, 0, duration), gripPosition.y, -30F / 16F) + .16F * (float) Math.sin(turnAngle * 1.25F) * orbitWeight * orbitFade,
                radius * (float) Math.sin(orbitAngle - turnAngle));
        Quaternionf relativeGrip = new Quaternionf().rotationY(-ICE_HAND_YAW).mul(gripRotation);
        Quaternionf tilt = relativeGrip.slerp(new Quaternionf().rotationX(STAFF_UPRIGHT), smootherStep(elapsed, .1F, .85F));
        float bankAngle = 72 * Mth.DEG_TO_RAD * smootherStep(elapsed, .25F, .85F) * (1 - smootherStep(elapsed, 2.3F, duration));
        float tangentAngle = -Mth.HALF_PI - orbitAngle - ICE_HAND_YAW;
        tangentAngle = (float) Math.atan2(Math.sin(tangentAngle), Math.cos(tangentAngle));
        float heading = ICE_HAND_YAW + turnAngle + tangentAngle * smootherStep(elapsed, .2F, .85F) * (1 - smootherStep(elapsed, 2.3F, duration));
        Quaternionf orientation = new Quaternionf().rotationY(heading).rotateZ(-bankAngle).mul(tilt).normalize();
        return new StaffFrame(position, orientation);
    }

    private static StaffFrame iceReturn(Vector3f gripPosition, Quaternionf gripRotation, float time, float hoverTime) {
        float duration = CastState.ICE_STAFF_RETURN_TICKS * .05F, returnProgress = progress(time, 0, duration), dockWeight = smootherStep(time, 0, duration);
        StaffFrame hoverPose = hover(hoverTime);
        float sampleStep = .005F;
        Vector3f prevPosition = hover(hoverTime - sampleStep).position, nextPosition = hover(hoverTime + sampleStep).position;
        Vector3f velocity = new Vector3f(nextPosition).sub(prevPosition).div(2 * sampleStep);
        Vector3f acceleration = new Vector3f(nextPosition).add(prevPosition).sub(new Vector3f(hoverPose.position).mul(2)).div(sampleStep * sampleStep);
        Vector3f position = hermite(hoverPose.position, gripPosition, velocity, new Vector3f(), acceleration, new Vector3f(), returnProgress, duration);
        float startYaw = hoverAngle(hoverTime), turnSpeed = hoverSpeed(hoverTime), targetYaw = (float) Math.ceil(startYaw / Mth.TWO_PI) * Mth.TWO_PI;
        if (targetYaw - startYaw < Mth.PI) {
            targetYaw += Mth.TWO_PI;
        }
        float yaw = hermite(progress(time, 0, duration), startYaw, targetYaw, turnSpeed * duration, 0);
        float orbitAngle = (float) Math.atan2(gripPosition.z, gripPosition.x) + targetYaw - yaw;
        float arcWeight = 64 * returnProgress * returnProgress * returnProgress * (1 - returnProgress) * (1 - returnProgress) * (1 - returnProgress);
        float radius = (float) Math.sqrt(gripPosition.x * gripPosition.x + gripPosition.z * gripPosition.z) * dockWeight + arcWeight;
        position.add(radius * (float) Math.cos(orbitAngle) - gripPosition.x * dockWeight,
                .12F * (float) Math.sin(targetYaw - yaw) * arcWeight, radius * (float) Math.sin(orbitAngle) - gripPosition.z * dockWeight);
        Quaternionf relativeGrip = new Quaternionf().rotationY(-ICE_HAND_YAW).mul(gripRotation);
        Quaternionf tilt = new Quaternionf().rotationX(STAFF_UPRIGHT).slerp(relativeGrip, smootherStep(time, .45F, duration));
        float bankAngle = 72 * Mth.DEG_TO_RAD * smootherStep(time, .15F, .85F) * (1 - smootherStep(time, 1.2F, duration - .25F));
        Quaternionf orientation = new Quaternionf().rotationY(ICE_HAND_YAW + yaw).rotateZ(-bankAngle).mul(tilt).normalize();
        return new StaffFrame(position, orientation);
    }

    private static float iceHandYaw() {
        Pose pose = new Pose();
        pose.set(ICE_HOLD);
        pose.staffPitch = ICE_HOLD.staffPitch;
        Quaternionf gripRotation = staffPalm(pose).getUnnormalizedRotation(new Quaternionf()).normalize()
                .mul(new Quaternionf().rotationX(-STAFF_UPRIGHT));
        return 2 * (float) Math.atan2(gripRotation.y, gripRotation.w);
    }

    private static float hoverAngle(float time) {
        float hoverTime = Math.max(0, time - CastState.ICE_STAFF_RISE_TICKS / 20F);
        return (35 * hoverTime + 24 * (1 - (float) Math.exp(-hoverTime / .8F))) * Mth.DEG_TO_RAD;
    }

    private static float hoverSpeed(float time) {
        float hoverTime = Math.max(0, time - CastState.ICE_STAFF_RISE_TICKS / 20F);
        return (35 + 30 * (float) Math.exp(-hoverTime / .8F)) * Mth.DEG_TO_RAD;
    }

    private static StaffFrame hover(float time) {
        float hoverTime = Math.max(0, time - CastState.ICE_STAFF_RISE_TICKS / 20F), bobWeight = smootherStep(hoverTime, 0, .6F);
        float bobHeight = .9F + .6F * (float) Math.exp(-hoverTime / .8F);
        Vector3f position = new Vector3f(.05F * (float) Math.sin(hoverTime * 1.8) * bobWeight,
                (-30 - bobHeight * (float) Math.sin(hoverTime * Math.PI / 1.2) * bobWeight) / 16F,
                .035F * ((float) Math.cos(hoverTime * 1.8) - 1) * bobWeight);
        return new StaffFrame(position, new Quaternionf().rotationY(ICE_HAND_YAW + hoverAngle(time)).rotateX(STAFF_UPRIGHT));
    }

    private static Matrix4f staffPalm(Pose pose) {
        Matrix4f transform = new Matrix4f();
        pose.root.transform(transform);
        pose.body.transform(transform);
        pose.rightArm.transform(transform);

        return transform.translate(0, 8.5F / 16F, 0).rotateX(pose.staffPitch);
    }

    public static Vec3 staffHead(Pose pose, Vec3 entityPosition, float bodyYaw) {
        Matrix4f transform = new Matrix4f().rotateY((180 - bodyYaw) * Mth.DEG_TO_RAD).scale(-1, -1, 1).translate(0, -1.501F, 0);
        if (pose.staffPosition != null) {
            transform.translate(pose.staffPosition).rotate(pose.staffOrientation);
        } else {
            transform.mul(staffPalm(pose));
        }
        Vector3f staffTip = new Vector3f(0, -1.736F, -9.09F).div(16);
        transform.transformPosition(staffTip);
        return entityPosition.add(staffTip.x, staffTip.y, staffTip.z);
    }

    public static final class Locomotion {
        public static final AnimationDefinition IDLE = idleAnimation();

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

        private static AnimationDefinition idleAnimation() {
            return AnimationDefinition.Builder.withLength(4.8F).looping()
                    .addAnimation("root", idleChannel(ROOT_IDLE, true))
                    .addAnimation("body", idleChannel(BODY_IDLE, false))
                    .addAnimation("head", idleChannel(HEAD_IDLE, false))
                    .addAnimation("right_arm", idleChannel(RIGHT_ARM_IDLE, false))
                    .addAnimation("left_arm", idleChannel(LEFT_ARM_IDLE, false))
                    .addAnimation("left_leg", idleChannel(LEFT_LEG_IDLE, false))
                    .addAnimation("right_leg", idleChannel(RIGHT_LEG_IDLE, false))
                    .build();
        }

        private static AnimationChannel idleChannel(float[][] values, boolean position) {
            float[] times = {0, 1.2F, 2.4F, 3.6F, 4.8F};
            Keyframe[] keys = new Keyframe[times.length];
            for (int frame = 0; frame < keys.length; frame++) {
                float[] value = values[frame % 4];
                keys[frame] = position ? pos(times[frame], value[0], value[1], value[2]) : rot(times[frame], value[0], value[1], value[2]);
            }
            return position ? loopPosition(4.8F, keys) : loopRotation(4.8F, keys);
        }
    }
}

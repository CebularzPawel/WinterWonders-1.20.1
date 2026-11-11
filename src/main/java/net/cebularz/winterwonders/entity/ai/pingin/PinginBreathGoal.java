package net.cebularz.winterwonders.entity.ai.pingin;

import java.util.EnumSet;

import net.cebularz.winterwonders.entity.custom.PinginEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.util.Mth;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.phys.Vec3;

public class PinginBreathGoal extends Goal {
    private final PinginEntity pingin;
    private Vec3 airTarget, lastAir;
    private int nextPathTick;

    public PinginBreathGoal(PinginEntity pingin) {
        this.pingin = pingin;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!pingin.isUnderWater() && pingin.level().getFluidState(BlockPos.containing(pingin.getEyePosition())).isEmpty()) {
            lastAir = pingin.position();
        }
        if (pingin.isAlive() && pingin.isInWater()) {
            BlockPos surface = pingin.blockPosition();
            while (surface.getY() < pingin.level().getMaxBuildHeight() && pingin.level().getFluidState(surface).is(FluidTags.WATER)) {
                surface = surface.above();
            }
            double distance = lastAir == null ? Math.max(0D, surface.getY() - pingin.getY()) : lastAir.distanceTo(pingin.position());
            int reserve = Mth.clamp(160 + (int)(distance * 8D), 400, pingin.getMaxAirSupply() - 40);
            return pingin.getAirSupply() < reserve;
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return pingin.isAlive() && pingin.getAirSupply() < pingin.getMaxAirSupply()
                && (pingin.isInWater() || !pingin.onGround());
    }

    @Override
    public void start() {
        airTarget = null;
        nextPathTick = 0;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (pingin.tickCount >= nextPathTick) {
            if (airTarget == null || pingin.level().getBlockCollisions(pingin,
                    pingin.getBoundingBox().move(airTarget.subtract(pingin.position()))).iterator().hasNext()
                    || !pingin.level().getFluidState(BlockPos.containing(airTarget.add(0D, pingin.getEyeHeight(), 0D))).isEmpty()
                    || (pingin.getNavigation().isDone() && pingin.distanceToSqr(airTarget) > 4D)) {
                airTarget = PinginSwimGoal.findSurface(pingin, lastAir);
            }
            int range = Math.max(24, Mth.ceil(airTarget.distanceTo(pingin.position())) + 8);
            pingin.getNavigation().moveTo(pingin.getNavigation().createPath(BlockPos.containing(airTarget), 0, range), 1.5D);
            nextPathTick = pingin.tickCount + 20;
        }
        Vec3 gap = airTarget.subtract(pingin.position());
        if (pingin.getNavigation().isDone() && pingin.isInWater()) {
            Vec3 drive = gap;
            if (gap.y > 0D && pingin.level().getBlockCollisions(pingin,
                    pingin.getBoundingBox().expandTowards(0D, Math.min(0.6D, gap.y), 0D)).iterator().hasNext()) {
                drive = gap.multiply(1D, 0D, 1D);
            }
            double force = Math.min(0.08D, 0.045D / Math.max(0.001D, drive.length()));
            pingin.setDeltaMovement(pingin.getDeltaMovement().scale(0.7D).add(drive.scale(force)));
            if (drive.horizontalDistanceSqr() > 0.04D) {
                float yaw = (float)(Mth.atan2(-drive.x, drive.z) * Mth.RAD_TO_DEG);
                pingin.setYRot(Mth.approachDegrees(pingin.getYRot(), yaw, 8F));
                pingin.yBodyRot = pingin.getYRot();
                pingin.setYHeadRot(pingin.getYRot());
            }
        }
        if (!pingin.isUnderWater() && pingin.level().getFluidState(BlockPos.containing(pingin.getEyePosition())).isEmpty()) {
            lastAir = pingin.position();
        }
    }

    @Override
    public void stop() {
        pingin.getNavigation().stop();
    }
}

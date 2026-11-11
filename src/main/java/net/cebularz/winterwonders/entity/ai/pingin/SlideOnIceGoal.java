package net.cebularz.winterwonders.entity.ai.pingin;

import java.util.EnumSet;

import net.cebularz.winterwonders.entity.custom.PinginEntity;

import net.minecraft.world.entity.ai.goal.Goal;

public class SlideOnIceGoal extends Goal {
    private final PinginEntity pingin;
    private static final int MAX_SLIDE_TIME = 48;
    private final double speedMultiplier;

    public SlideOnIceGoal(PinginEntity pingin, double speed) {
        this.pingin = pingin;
        this.speedMultiplier = speed;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return canSlide() && pingin.getRandom().nextFloat() < 0.02F;
    }

    @Override
    public boolean canContinueToUse() {
        return pingin.getSlideTicks() > 0 && canSlide();
    }

    private boolean canSlide() {
        return pingin.isOnIce() && pingin.onGround() && !pingin.isInWater()
                && !pingin.isAdmiring() && pingin.getTarget() == null && pingin.getHealth() > pingin.getMaxHealth() * 0.5F;
    }

    @Override
    public void start() {
        pingin.getNavigation().stop();
        pingin.startSliding(MAX_SLIDE_TIME);
    }

    @Override
    public void stop() {
        pingin.stopSliding();
        pingin.setDeltaMovement(pingin.getDeltaMovement().multiply(0.35D, 1D, 0.35D));
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {

        int ticksLeft = pingin.getSlideTicks() - 1;
        if (ticksLeft > 0) {

            double elapsed = MAX_SLIDE_TIME - ticksLeft;
            double push = Math.max(0D, Math.min(1D, (elapsed - 4D) / 7D));
            double brake = Math.max(0D, Math.min(1D, (34D - elapsed) / 9D));
            double speed = 0.15D * speedMultiplier * push * brake;
            double yawRad = Math.toRadians(this.pingin.getYRot());
            double xSpeed = -Math.sin(yawRad) * speed;
            double zSpeed = Math.cos(yawRad) * speed;
            this.pingin.setDeltaMovement(xSpeed, this.pingin.getDeltaMovement().y, zSpeed);
        } else if (ticksLeft == 0) {
            this.pingin.setDeltaMovement(0, this.pingin.getDeltaMovement().y, 0);
        }
    }
}

package net.cebularz.winterwonders.entity.ai;

import net.minecraft.world.entity.ai.goal.Goal;
import net.cebularz.winterwonders.entity.custom.PinginEntity;

import java.util.EnumSet;

public class SlideOnIceGoal extends Goal {
    private final PinginEntity pingin;
    private final double speed;
    private int slideTime;
    private static final int MAX_SLIDE_TIME = 40;

    public SlideOnIceGoal(PinginEntity pingin, double speed) {
        this.pingin = pingin;
        this.speed = speed;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!pingin.isOnIce()) {
            return false;
        }
        if (pingin.getRandom().nextFloat() < 0.02F) {
            return true;
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return slideTime > 0 && pingin.isOnIce();
    }

    @Override
    public void start() {
        slideTime = MAX_SLIDE_TIME;
        pingin.startSliding(MAX_SLIDE_TIME);
    }

    @Override
    public void stop() {
        slideTime = 0;
    }

    @Override
    public void tick() {
        slideTime--;
        if (this.slideTime > 0) {
            double speed = 0.15;
            double yawRad = Math.toRadians(this.pingin.getYRot());
            double xSpeed = -Math.sin(yawRad) * speed;
            double zSpeed = Math.cos(yawRad) * speed;
            this.pingin.setDeltaMovement(xSpeed, this.pingin.getDeltaMovement().y, zSpeed);
        } else if (this.slideTime == 0) {
            this.pingin.setDeltaMovement(0, this.pingin.getDeltaMovement().y, 0);
        }
    }
}

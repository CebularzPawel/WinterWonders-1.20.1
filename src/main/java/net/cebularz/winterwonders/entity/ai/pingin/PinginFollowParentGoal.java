package net.cebularz.winterwonders.entity.ai.pingin;

import java.util.Comparator;
import java.util.EnumSet;

import net.cebularz.winterwonders.entity.custom.PinginEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class PinginFollowParentGoal extends Goal {
    private final PinginEntity pingin;
    private PinginEntity parent;
    private int nextPathTick;

    public PinginFollowParentGoal(PinginEntity pingin) {
        this.pingin = pingin;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (pingin.isBaby() && pingin.getTarget() == null) {
            parent = pingin.getParent();
            if (parent == null) {
                parent = pingin.level().getEntitiesOfClass(PinginEntity.class, pingin.getBoundingBox().inflate(8D, 4D, 8D),
                        adult -> adult.isAlive() && !adult.isBaby()).stream()
                        .min(Comparator.comparingDouble(pingin::distanceToSqr)).orElse(null);
            }
            return parent != null && pingin.distanceToSqr(parent) >= 9D;
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return pingin.isBaby() && parent != null && parent.isAlive()
                && pingin.distanceToSqr(parent) >= 9D && pingin.distanceToSqr(parent) <= 256D;
    }

    @Override
    public void start() { nextPathTick = 0; }

    @Override
    public void tick() {
        if (pingin.tickCount >= nextPathTick) {
            pingin.getNavigation().moveTo(parent, 1.1D);
            nextPathTick = pingin.tickCount + 10;
        }
    }

    @Override
    public void stop() { parent = null; }
}

package net.cebularz.winterwonders.entity.ai.pingin;

import java.util.EnumSet;

import net.cebularz.winterwonders.entity.custom.PinginEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class PinginFollowPlayerGoal extends Goal {
    private final PinginEntity pingin;
    private Player player;
    private int nextPathTick;

    public PinginFollowPlayerGoal(PinginEntity pingin) {
        this.pingin = pingin;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (pingin.isAlive() && pingin.getRecruiterId() != null && pingin.getTarget() == null && !pingin.isAdmiring()) {
            player = pingin.getRecruiter();
            // if (!pingin.isRecruited() && pingin.getHomePos() == null && pingin.getColonyPos() != null) {
                // pingin.setHomePos(pingin.getColonyPos().south());
            // }
            if (pingin.isRecruited()) {
                return player != null && player.isAlive() && !player.isSpectator() && pingin.distanceToSqr(player) > 9D;
            } else if (pingin.getHomePos() != null) {
                return true;
            } else {
                pingin.finishRecruitment();
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return pingin.isAlive() && pingin.getRecruiterId() != null && pingin.getTarget() == null && !pingin.isAdmiring()
                && (pingin.isRecruited() ? player != null && player == pingin.getRecruiter() && player.isAlive()
                && !player.isSpectator() && pingin.distanceToSqr(player) > 4D : pingin.getHomePos() != null);
    }

    @Override
    public void start() { nextPathTick = 0; }

    @Override
    public void tick() {
        if (pingin.tickCount >= nextPathTick) {
            if (pingin.isRecruited() && player != null) {
                pingin.getLookControl().setLookAt(player, 30F, 30F);
                pingin.getNavigation().moveTo(player, 1.15D);
            } else {
                BlockPos homePos = pingin.getHomePos();
                if (homePos != null) {
                    Vec3 home = Vec3.atBottomCenterOf(homePos);
                    if (pingin.distanceToSqr(home) <= 9D) {
                        pingin.finishRecruitment();
                        pingin.getNavigation().stop();
                    } else {
                        Vec3 destination = pingin.distanceToSqr(home) > 256D
                                ? DefaultRandomPos.getPosTowards(pingin, 16, 7, home, Math.PI / 2D) : home;
                        if (destination != null) {
                            pingin.getNavigation().moveTo(destination.x, destination.y, destination.z, 1D);
                        }
                    }
                }
            }
            nextPathTick = pingin.tickCount + 20;
        }
    }

    @Override
    public void stop() {
        pingin.getNavigation().stop();
        player = null;
    }
}

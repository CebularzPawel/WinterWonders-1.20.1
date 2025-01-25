package net.turtleboi.winterwonders.ai;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

public class FishFollowPlayerGoal extends Goal {
    private final AbstractFish abstractFish;
    private Player targetPlayer;
    private final double speed;
    private final double followDistance;

    public FishFollowPlayerGoal(AbstractFish abstractFish, Player player, double speed, double followDistance) {
        this.abstractFish = abstractFish;
        this.targetPlayer = player;
        this.speed = speed;
        this.followDistance = followDistance;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return this.targetPlayer != null && !this.targetPlayer.isDeadOrDying() && this.abstractFish.distanceToSqr(this.targetPlayer) < this.followDistance * this.followDistance;
    }

    @Override
    public boolean canContinueToUse() {
        return this.targetPlayer != null && !this.targetPlayer.isDeadOrDying() && this.abstractFish.distanceToSqr(this.targetPlayer) < this.followDistance * this.followDistance;
    }

    @Override
    public void start() {
        if (this.targetPlayer != null) {
            this.abstractFish.getNavigation().moveTo(this.targetPlayer, this.speed);
        }
    }

    @Override
    public void tick() {
        if (this.targetPlayer != null && this.abstractFish.distanceTo(this.targetPlayer) > 2.0D) {
            this.abstractFish.getNavigation().moveTo(this.targetPlayer, this.speed);
        }
    }

    @Override
    public void stop() {
        if (this.targetPlayer == null || this.targetPlayer.isDeadOrDying()) {
            this.targetPlayer = null;
        }
        this.abstractFish.getNavigation().stop();
    }
}

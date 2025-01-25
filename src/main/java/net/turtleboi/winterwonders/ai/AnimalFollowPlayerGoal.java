package net.turtleboi.winterwonders.ai;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.animal.Animal;

import java.util.EnumSet;

public class AnimalFollowPlayerGoal extends Goal {
    private final Animal animal;
    private Player targetPlayer;
    private final double speed;
    private final double followDistance;

    public AnimalFollowPlayerGoal(Animal animal, Player player, double speed, double followDistance) {
        this.animal = animal;
        this.targetPlayer = player;
        this.speed = speed;
        this.followDistance = followDistance;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return this.targetPlayer != null && !this.targetPlayer.isDeadOrDying() && this.animal.distanceToSqr(this.targetPlayer) < this.followDistance * this.followDistance;
    }

    @Override
    public boolean canContinueToUse() {
        return this.targetPlayer != null && !this.targetPlayer.isDeadOrDying() && this.animal.distanceToSqr(this.targetPlayer) < this.followDistance * this.followDistance;
    }

    @Override
    public void start() {
        if (this.targetPlayer != null) {
            this.animal.getNavigation().moveTo(this.targetPlayer, this.speed);
        }
    }

    @Override
    public void tick() {
        if (this.targetPlayer != null && this.animal.distanceTo(this.targetPlayer) > 2.0D) {
            this.animal.getNavigation().moveTo(this.targetPlayer, this.speed);
        }
    }

    @Override
    public void stop() {
        if (this.targetPlayer == null || this.targetPlayer.isDeadOrDying()) {
            this.targetPlayer = null;
        }
        this.animal.getNavigation().stop();
    }
}

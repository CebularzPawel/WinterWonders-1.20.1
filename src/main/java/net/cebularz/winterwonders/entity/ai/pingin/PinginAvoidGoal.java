package net.cebularz.winterwonders.entity.ai.pingin;

import net.cebularz.winterwonders.entity.custom.PinginEntity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.phys.Vec3;

public class PinginAvoidGoal extends AvoidEntityGoal<LivingEntity> {
    private final PinginEntity pingin;
    private int nextCheck;

    public PinginAvoidGoal(PinginEntity pingin) {
        super(pingin, LivingEntity.class, threat -> !pingin.isAlliedTo(threat)
                && (threat instanceof Creeper creeper && (creeper.getSwellDir() > 0 || creeper.isIgnited())
                || !pingin.canFight() && (threat instanceof Monster || threat == pingin.getLastHurtByMob()
                && pingin.tickCount - pingin.getLastHurtByMobTimestamp() < 100)),
                8F, 1.3D, 1.6D, EntitySelector.NO_CREATIVE_OR_SPECTATOR::test);
        this.pingin = pingin;
    }

    @Override
    public boolean canUse() {
        if (pingin.tickCount >= nextCheck && !pingin.isOnFire()) {
            nextCheck = pingin.tickCount + 10;
            if (super.canUse() && path.canReach()) {
                return true;
            } else if (toAvoid != null) {
                for (int attempt = 0; attempt < 4; attempt++) {
                    Vec3 pos = DefaultRandomPos.getPosAway(pingin, 12, 4, toAvoid.position());
                    if (pos != null && toAvoid.distanceToSqr(pos) > pingin.distanceToSqr(toAvoid)) {
                        path = pathNav.createPath(pos.x, pos.y, pos.z, 0);
                        if (path != null && path.canReach()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return toAvoid != null && toAvoid.isAlive() && !pingin.isOnFire()
                && pingin.distanceToSqr(toAvoid) < 144D && super.canContinueToUse();
    }

    @Override
    public void start() {
        pingin.setTarget(null);
        pingin.setAdmiring(false);
        pingin.stopSliding();
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
        pingin.getNavigation().stop();
    }
}

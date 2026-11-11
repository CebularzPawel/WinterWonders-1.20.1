// package net.cebularz.winterwonders.entity.ai.pingin;

// import java.util.Comparator;
// import java.util.EnumSet;
// import java.util.List;
// import java.util.UUID;

// import net.cebularz.winterwonders.block.entity.custom.FishRackBlockEntity;
// import net.cebularz.winterwonders.entity.custom.PinginEntity;
// import net.cebularz.winterwonders.entity.custom.PinginRole;
// import net.minecraft.core.BlockPos;
// import net.minecraft.server.level.ServerLevel;
// import net.minecraft.tags.ItemTags;
// import net.minecraft.world.entity.Entity;
// import net.minecraft.world.entity.LivingEntity;
// import net.minecraft.world.entity.ai.goal.Goal;
// import net.minecraft.world.entity.monster.Monster;
// import net.minecraft.world.phys.Vec3;

// public class PinginColonyGoal extends Goal {
    // private final PinginEntity pingin;
    // private PinginEntity leader;
    // private UUID leaderId;
    // private BlockPos destination;
    // private int nextCheck, nextPathTick, departureTick, endTick;
    // private boolean sheltering;

    // public PinginColonyGoal(PinginEntity pingin) {
        // this.pingin = pingin;
        // setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    // }

    // @Override
    // public boolean canUse() {
        // if (pingin.tickCount >= nextCheck && pingin.isAlive() && pingin.getRecruiterId() == null && pingin.getTarget() == null
                // && !pingin.isAdmiring() && !pingin.getMainHandItem().is(ItemTags.FISHES)) {
            // nextCheck = pingin.tickCount + 40;
            // FishRackBlockEntity rack = pingin.getFishRack();
            // if (rack != null) {
                // ServerLevel level = (ServerLevel)pingin.level();
                // boolean canFight = pingin.canFight();
                // UUID threatId = rack.getThreat();
                // Entity threat = threatId == null ? null : level.getEntity(threatId);
                // if (canFight && threat == null) {
                    // threat = level.getEntitiesOfClass(Monster.class, pingin.getBoundingBox().inflate(12D),
                            // mob -> mob.isAlive() && pingin.getSensing().hasLineOfSight(mob)).stream()
                            // .min(Comparator.comparingDouble(pingin::distanceToSqr)).orElse(null);
                    // if (threat instanceof LivingEntity attacker) {
                        // rack.alarm(attacker);
                    // }
                // }
                // leader = null;
                // sheltering = false;
                // UUID trip = null;
                // if (threat instanceof LivingEntity attacker && attacker.isAlive()) {
                    // sheltering = true;
                    // if (canFight) {
                        // pingin.setTarget(attacker);
                    // } else {
                        // destination = pingin.getNestPos() == null ? pingin.getHomePos() : pingin.getNestPos();
                        // return destination != null;
                    // }
                // } else if (!pingin.isBaby() && pingin.getColonyRole() == PinginRole.FISHER && !pingin.hasWeapon()
                        // && pingin.getWork() < PinginEntity.CHIP) {
                    // trip = rack.joinTrip(pingin);
                // } else if (canFight) {
                    // if (!rack.canStartTrip()) {
                        // List<PinginEntity> adults = level.getEntitiesOfClass(PinginEntity.class, pingin.getBoundingBox().inflate(24D),
                                // adult -> adult.isAlive() && !adult.isBaby() && rack.getBlockPos().equals(adult.getColonyPos()));
                        // long guards = adults.stream().filter(adult -> adult.canFight() && adult.getRecruiterId() == null
                                // && !adult.getUUID().equals(rack.getEscort()) && !adult.isInWater()).count();
                        // boolean escorting = pingin.getUUID().equals(rack.getEscort());
                        // int needed = Math.max(1, adults.size() / 4);
                        // if (guards >= needed + (escorting ? 0 : 1)) {
                            // trip = rack.joinEscort(pingin);
                        // } else if (escorting) {
                            // rack.leaveEscort(pingin);
                        // }
                    // }
                    // if (trip == null) {
                        // if (pingin.getHomePos() != null && (pingin.isInWater()
                                // || pingin.distanceToSqr(Vec3.atBottomCenterOf(pingin.getHomePos())) > 144D)) {
                            // destination = pingin.getHomePos();
                            // return true;
                        // } else if (pingin.onGround() && !pingin.isInWater()) {
                            // nextCheck = pingin.tickCount + 200 + pingin.getRandom().nextInt(200);
                            // destination = rack.getBlockPos().offset(pingin.getRandom().nextInt(13) - 6, 0,
                                    // pingin.getRandom().nextInt(13) - 6);
                            // return pingin.level().getFluidState(destination).isEmpty();
                        // }
                    // }
                // }
                // if (trip != null) {
                    // if (!trip.equals(leaderId)) {
                        // leaderId = trip;
                        // departureTick = pingin.tickCount + rack.departureDelay(pingin);
                    // }
                    // Entity entity = level.getEntity(trip);
                    // if (entity instanceof PinginEntity fisher && fisher.isAlive() && fisher.getTarget() == null
                            // && (canFight || !fisher.getMainHandItem().is(ItemTags.FISHES)) && pingin.tickCount >= departureTick
                            // && pingin.distanceToSqr(fisher) > (pingin.isInWater() ? 36D : 9D)) {
                        // leader = fisher;
                        // return true;
                    // }
                // }
            // }
        // }
        // return false;
    // }

    // @Override
    // public boolean canContinueToUse() {
        // FishRackBlockEntity rack = pingin.getFishRack();
        // return pingin.isAlive() && pingin.getRecruiterId() == null && pingin.getTarget() == null && !pingin.isAdmiring()
                // && !pingin.getMainHandItem().is(ItemTags.FISHES) && pingin.tickCount < endTick
                // && pingin.getAirSupply() >= 400 && rack != null
                // && (leader == null ? sheltering || !pingin.getNavigation().isDone()
                // : leader.isAlive() && rack.getThreat() == null
                // && (pingin.canFight() ? pingin.getUUID().equals(rack.getEscort()) : !leader.getMainHandItem().is(ItemTags.FISHES))
                // && pingin.distanceToSqr(leader) > (pingin.isInWater() ? 36D : 4D));
    // }

    // @Override
    // public void start() {
        // nextPathTick = 0;
        // endTick = pingin.tickCount + (leader == null ? 120 : 600);
        // tick();
    // }

    // @Override
    // public void tick() {
        // if (pingin.tickCount >= nextPathTick) {
            // if (leader != null) {
                // pingin.getNavigation().moveTo(leader, 1.15D);
                // pingin.getLookControl().setLookAt(leader, 30F, 30F);
            // } else {
                // pingin.getNavigation().moveTo(destination.getX() + 0.5D, destination.getY(), destination.getZ() + 0.5D, 1D);
            // }
            // nextPathTick = pingin.tickCount + 20;
        // }
    // }

    // @Override
    // public void stop() {
        // pingin.getNavigation().stop();
        // leader = null;
    // }
// }

package net.cebularz.winterwonders.entity.ai.pingin;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.entity.custom.PinginEntity;
// import net.cebularz.winterwonders.entity.custom.PinginRole;
// import net.cebularz.winterwonders.block.entity.custom.FishRackBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PinginSwimGoal extends Goal {
    private static final TagKey<EntityType<?>> PREY = TagKey.create(Registries.ENTITY_TYPE,
            new ResourceLocation(WinterWonders.MOD_ID, "pingin_prey"));
    private final PinginEntity pingin;
    private Vec3 destination;
    private AbstractFish preyFish;
    private int elapsedTicks, tripDuration;
    private int nextTripTick, nextHuntTick, nextPathTick, nextBiteTick;
    private boolean returning;

    public PinginSwimGoal(PinginEntity pingin) {
        this.pingin = pingin;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        if (pingin.isAlive() && !pingin.isAdmiring() && pingin.getTarget() == null) {
            if (pingin.isInWater()) {
                return true;
            } else if (!pingin.isBaby() && pingin.getRecruiterId() == null && pingin.onGround() && !pingin.getMainHandItem().is(ItemTags.FISHES)
                    && pingin.tickCount >= nextTripTick) {
                nextTripTick = pingin.tickCount + 200 + pingin.getRandom().nextInt(200);
                // FishRackBlockEntity rack = pingin.getFishRack();
                // if (rack == null || pingin.getColonyRole() == PinginRole.FISHER && !pingin.hasWeapon()
                        // && rack.canStartTrip() && rack.fishCount() < 32 && rack.getThreat() == null) {
                    // destination = findWater();
                    // return destination != null;
                // }
                destination = findWater();
                return destination != null;
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return pingin.isAlive() && !pingin.isAdmiring() && pingin.getTarget() == null && elapsedTicks < 1200
                && (pingin.isInWater() || (!pingin.onGround() && (returning || pingin.getSwimAction() != 0))
                || (!returning && elapsedTicks < 160 && destination != null));
    }

    @Override
    public void start() {
        // FishRackBlockEntity rack = pingin.getFishRack();
        // if (rack != null && !pingin.isInWater() && !pingin.getMainHandItem().is(ItemTags.FISHES)) {
            // rack.startTrip(pingin);
        // }
        if (pingin.getHomePos() == null && pingin.onGround() && !pingin.isInWater()) {
            pingin.setHomePos(pingin.blockPosition());
        }
        elapsedTicks = 0;
        tripDuration = 400 + pingin.getRandom().nextInt(400);
        returning = pingin.getMainHandItem().is(ItemTags.FISHES);
        nextPathTick = 0;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        elapsedTicks++;
        int action = pingin.getSwimAction();
        boolean jumping = action == PinginEntity.DIVE || action == PinginEntity.LEAP;
        if (jumping && pingin.onGround() && !pingin.isInWater()) {
            destination = null;
            pingin.getNavigation().stop();
        } else if (jumping && (!pingin.isInWater() || pingin.getDeltaMovement().y > 0D)) {
            pingin.getNavigation().stop();
        } else if (pingin.isInWater()) {
            swim();
        } else if (destination != null) {
            move(1.1D);
            Vec3 gap = destination.subtract(pingin.position());
            if (returning && !pingin.onGround() && gap.lengthSqr() < 7D
                    && pingin.level().getFluidState(BlockPos.containing(destination)).isEmpty()) {
                pingin.startWaterJump(destination, PinginEntity.LEAP);
            }
        }
    }

    private void swim() {
        boolean needsAir = pingin.getAirSupply() < 400;
        if (!needsAir && !returning && (elapsedTicks > tripDuration || pingin.getMainHandItem().is(ItemTags.FISHES))) {
            returning = true;
            destination = null;
        }

        if (needsAir) {
            preyFish = null;
            destination = findSurface(pingin, null);
            move(1.5D);
        } else if (returning) {
            if (destination == null || (pingin.tickCount >= nextPathTick && pingin.getNavigation().isDone())) {
                BlockPos homePos = pingin.getHomePos();
                Vec3 homeTarget = homePos == null ? null : Vec3.atBottomCenterOf(homePos);
                Path homePath = homePos == null ? null : pingin.getNavigation().createPath(homePos, 0);
                if (homeTarget != null && (pingin.distanceToSqr(homeTarget) > 144D || homePath != null && homePath.canReach())) {
                    destination = homeTarget;
                } else {
                    destination = findShore();
                }
                nextPathTick = pingin.tickCount + 40;
            }
            if (destination == null) {
                destination = findSurface(pingin, null);
                move(1.2D);
            } else {
                move(1.3D);
                if (pingin.position().distanceToSqr(destination) < 7D && !pingin.isUnderWater()
                        && pingin.level().getFluidState(BlockPos.containing(destination)).isEmpty()) {
                    pingin.startWaterJump(destination, PinginEntity.LEAP);
                }
            }
        } else if (!pingin.isBaby() && pingin.getMainHandItem().isEmpty() && pingin.tickCount >= nextHuntTick) {
            hunt();
        } else {
            destination = wander();
            move(1.3D);
        }

        if (!needsAir && !returning && preyFish == null && pingin.getSwimAction() == 0 && !pingin.isUnderWater()
                && pingin.getRandom().nextInt(240) == 0) {
            Vec3 forward = Vec3.directionFromRotation(0F, pingin.getYRot());
            Vec3 landing = pingin.position().add(forward.scale(2.5D));
            if (water(BlockPos.containing(landing)) && water(BlockPos.containing(landing).below())) {
                pingin.startWaterJump(landing, PinginEntity.LEAP);
            }
        }
    }

    private void hunt() {
        if (preyFish == null || !canHuntFish(preyFish)) {
            preyFish = pingin.level().getEntitiesOfClass(AbstractFish.class, pingin.getBoundingBox().inflate(10D), this::canHuntFish).stream()
                    .min(Comparator.comparingDouble(pingin::distanceToSqr)).orElse(null);
        }

        boolean caught = false;
        if (preyFish != null) {
            destination = preyFish.position().add(0D, -0.25D, 0D);
            pingin.getLookControl().setLookAt(preyFish, 30F, 30F);
            if (pingin.tickCount >= nextPathTick) {
                move(1.8D);
                nextPathTick = pingin.tickCount + 4;
            }
            Vec3 gap = preyFish.position().subtract(pingin.position());
            if (gap.lengthSqr() < 1D && gap.normalize().dot(pingin.getViewVector(1F)) > 0.25D
                    && pingin.tickCount >= nextBiteTick && pingin.getSensing().hasLineOfSight(preyFish)) {
                nextBiteTick = pingin.tickCount + 12;
                pingin.startSwimAction(PinginEntity.CATCH);
                if (preyFish.hurt(pingin.damageSources().mobAttack(pingin), 3F) && !preyFish.isAlive()) {
                    List<ItemEntity> fishDrops = pingin.level().getEntitiesOfClass(ItemEntity.class,
                            preyFish.getBoundingBox().inflate(0.8D), drop -> drop.getItem().is(ItemTags.FISHES));
                    if (pingin.getMainHandItem().isEmpty() && !fishDrops.isEmpty()) {
                        ItemEntity fishDrop = fishDrops.get(0);
                        pingin.setItemInHand(InteractionHand.MAIN_HAND, fishDrop.getItem().split(1));
                        pingin.setDropChance(EquipmentSlot.MAINHAND, 1F);
                        if (fishDrop.getItem().isEmpty()) {
                            fishDrop.discard();
                        }
                    }
                    preyFish = null;
                    caught = true;
                }
            }
        } else {
            nextHuntTick = pingin.tickCount + 40;
            if (elapsedTicks >= 100 && pingin.isUnderWater() && pingin.level().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)
                    && pingin.getRandom().nextInt(40) == 0) {
                pingin.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(pingin.getRandom().nextBoolean() ? Items.COD : Items.SALMON));
                pingin.setDropChance(EquipmentSlot.MAINHAND, 1F);
                pingin.startSwimAction(PinginEntity.CATCH);
                caught = true;
            } else {
                destination = wander();
                move(1.3D);
            }
        }

        if (caught) {
            nextHuntTick = pingin.tickCount + 400 + pingin.getRandom().nextInt(400);
            destination = findSurface(pingin, null);
            move(1.3D);
        }
    }

    private boolean canHuntFish(AbstractFish fish) {
        return fish.isAlive() && fish.isInWater() && fish.getType().is(PREY)
                && !fish.hasCustomName() && !fish.fromBucket() && pingin.distanceToSqr(fish) < 144D;
    }

    private void move(double speed) {
        pingin.getNavigation().moveTo(destination.x, destination.y, destination.z, speed);
    }

    private Vec3 findWater() {
        for (BlockPos pos : BlockPos.withinManhattan(pingin.blockPosition(), 12, 4, 12)) {
            if (water(pos) && water(pos.below()) && pingin.level().isEmptyBlock(pos.above()) && pingin.level().isEmptyBlock(pos.above(2))) {
                Path path = pingin.getNavigation().createPath(pos, 0);
                if (path != null && path.canReach()) {
                    Vec3 landing = Vec3.atCenterOf(pos);
                    Vec3 forward = landing.subtract(pingin.position()).multiply(1D, 0D, 1D).normalize();
                    Vec3 deeper = landing.add(forward.scale(1.25D));
                    BlockPos target = BlockPos.containing(deeper);
                    if (water(target) && water(target.below()) && pingin.level().noCollision(pingin,
                            pingin.getBoundingBox().move(deeper.subtract(pingin.position())))) {
                        landing = deeper;
                    }
                    if (pingin.level().noCollision(pingin, pingin.getBoundingBox().move(landing.subtract(pingin.position())))) {
                        return landing;
                    }
                }
            }
        }
        return null;
    }

    private Vec3 findShore() {
        pingin.getNavigation().stop();
        BlockPos homePos = pingin.getHomePos();
        Vec3 homeTarget = homePos == null ? pingin.position() : Vec3.atBottomCenterOf(homePos);
        Vec3 shoreTarget = null;
        double bestDistance = Double.MAX_VALUE;
        for (BlockPos pos : BlockPos.withinManhattan(pingin.blockPosition(), 12, 6, 12)) {
            VoxelShape floor = pingin.level().getBlockState(pos.below()).getCollisionShape(pingin.level(), pos.below());
            BlockState landingState = pingin.level().getBlockState(pos);
            if (!floor.isEmpty() && landingState.getFluidState().isEmpty()
                    && landingState.isPathfindable(pingin.level(), pos, PathComputationType.LAND)) {
                Vec3 shore = Vec3.atBottomCenterOf(pos).add(0D, floor.max(Direction.Axis.Y) - 1D, 0D);
                boolean waterEdge = false;
                for (Direction side : Direction.Plane.HORIZONTAL) {
                    waterEdge |= water(BlockPos.containing(shore).below().relative(side));
                }
                double distance = shore.distanceToSqr(homeTarget);
                if (waterEdge && distance < bestDistance && pingin.level().noCollision(pingin,
                        pingin.getBoundingBox().move(shore.subtract(pingin.position())))) {
                    Path path = pingin.getNavigation().createPath(pos, 0);
                    if (path != null && path.canReach()) {
                        shoreTarget = shore;
                        bestDistance = distance;
                    }
                }
            }
        }
        return shoreTarget;
    }

    static Vec3 findSurface(PinginEntity pingin, Vec3 lastAir) {
        pingin.getNavigation().stop();
        BlockPos pos = pingin.blockPosition();
        while (pos.getY() < pingin.level().getMaxBuildHeight() && pingin.level().getFluidState(pos).is(FluidTags.WATER)) {
            pos = pos.above();
        }
        int range = lastAir == null ? 24 : 12;
        for (BlockPos air : BlockPos.withinManhattan(pos, range, 4, range)) {
            Vec3 airTarget = Vec3.atBottomCenterOf(air).add(0D, 0.1D - pingin.getEyeHeight(), 0D);
            AABB bounds = pingin.getBoundingBox().move(airTarget.subtract(pingin.position()));
            if (pingin.level().isEmptyBlock(air) && pingin.level().getFluidState(air.below()).is(FluidTags.WATER)
                    && !pingin.level().getBlockCollisions(pingin, bounds).iterator().hasNext()) {
                int reach = Math.max(24, Mth.ceil(airTarget.distanceTo(pingin.position())) + 8);
                Path path = pingin.getNavigation().createPath(air.below(), 0, reach);
                Vec3 sideGap = airTarget.subtract(pingin.position()).multiply(1D, 0D, 1D);
                boolean clear = !pingin.level().getBlockCollisions(pingin, pingin.getBoundingBox().expandTowards(sideGap)).iterator().hasNext();
                if ((path != null && (path.canReach() || path.getNodeCount() > 1)) || clear) {
                    return airTarget;
                }
            }
        }
        if (lastAir != null && pingin.level().getFluidState(BlockPos.containing(lastAir.add(0D, pingin.getEyeHeight(), 0D))).isEmpty()
                && !pingin.level().getBlockCollisions(pingin,
                pingin.getBoundingBox().move(lastAir.subtract(pingin.position()))).iterator().hasNext()) {
            int reach = Math.max(24, Mth.ceil(lastAir.distanceTo(pingin.position())) + 8);
            Path path = pingin.getNavigation().createPath(BlockPos.containing(lastAir), 0, reach);
            if (path != null && (path.canReach() || path.getNodeCount() > 1)) {
                return lastAir;
            }
        }
        return pingin.position().add(0D, 1D, 0D);
    }

    private Vec3 wander() {
        if (destination != null && pingin.distanceToSqr(destination) > 1D
                && (pingin.tickCount < nextPathTick || !pingin.getNavigation().isDone())) {
            return destination;
        } else {
            nextPathTick = pingin.tickCount + 60;
            for (int attempt = 0; attempt < 12; attempt++) {
                BlockPos pos = pingin.blockPosition().offset(pingin.getRandom().nextInt(13) - 6,
                        pingin.getRandom().nextInt(5) - 2, pingin.getRandom().nextInt(13) - 6);
                if (water(pos) && water(pos.above()) && pingin.level().noCollision(pingin,
                        pingin.getBoundingBox().move(Vec3.atCenterOf(pos).subtract(pingin.position())))) {
                    return Vec3.atCenterOf(pos);
                }
            }
            return findSurface(pingin, null);
        }
    }

    private boolean water(BlockPos pos) {
        return pingin.level().hasChunkAt(pos) && pingin.level().getFluidState(pos).is(FluidTags.WATER);
    }

    @Override
    public void stop() {
        pingin.getNavigation().stop();
        preyFish = null;
        destination = null;
        nextTripTick = pingin.tickCount + 400;
    }
}

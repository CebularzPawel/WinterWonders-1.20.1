// package net.cebularz.winterwonders.entity.ai.pingin;

// import java.util.EnumSet;
// import java.util.List;
// import java.util.Comparator;
// import java.util.ArrayList;

// import net.cebularz.winterwonders.block.ModBlocks;
// import net.cebularz.winterwonders.block.entity.custom.FishRackBlockEntity;
// import net.cebularz.winterwonders.entity.custom.PinginEntity;
// import net.cebularz.winterwonders.entity.custom.PinginRole;
// import net.cebularz.winterwonders.item.ModItems;
// import net.cebularz.winterwonders.init.ModPoiTypes;
// import net.minecraft.core.BlockPos;
// import net.minecraft.core.Direction;
// import net.minecraft.core.particles.BlockParticleOption;
// import net.minecraft.core.particles.ParticleTypes;
// import net.minecraft.util.Mth;
// import net.minecraft.tags.FluidTags;
// import net.minecraft.server.level.ServerLevel;
// import net.minecraft.world.InteractionHand;
// import net.minecraft.world.entity.EquipmentSlot;
// import net.minecraft.world.entity.ai.goal.Goal;
// import net.minecraft.world.entity.ai.village.poi.PoiManager;
// import net.minecraft.world.entity.ai.village.poi.PoiRecord;
// import net.minecraft.world.item.Item;
// import net.minecraft.world.item.ItemStack;
// import net.minecraft.world.item.Items;
// import net.minecraft.world.level.GameRules;
// import net.minecraft.world.level.block.Blocks;
// import net.minecraft.world.level.block.Block;
// import net.minecraft.sounds.SoundEvents;
// import net.minecraftforge.event.ForgeEventFactory;
// import net.minecraft.world.level.pathfinder.Path;
// import net.minecraft.world.phys.Vec3;

// public class PinginWorkGoal extends Goal {
    // private final PinginEntity pingin;
    // private FishRackBlockEntity rack;
    // private BlockPos target;
    // private Item material;
    // private int work, workTicks, elapsedTicks, nextCheck, nextPathTick;
    // private final List<BlockPos> ice = new ArrayList<>();
    // private BlockPos shorePos;

    // public PinginWorkGoal(PinginEntity pingin) {
        // this.pingin = pingin;
        // setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    // }

    // @Override
    // public boolean canUse() {
        // if (pingin.tickCount >= nextCheck && pingin.isAlive() && !pingin.isBaby() && pingin.getRecruiterId() == null && pingin.onGround()
                // && !pingin.isInWaterOrBubble() && !pingin.isAdmiring() && pingin.getTarget() == null
                // && pingin.getMainHandItem().isEmpty()) {
            // nextCheck = pingin.tickCount + 80 + pingin.getRandom().nextInt(80);
            // rack = pingin.getFishRack();
            // if (rack != null && rack.getThreat() == null && rack.claimWork(pingin)) {
                // ice.clear();
                // shorePos = null;
                // if (pingin.getColonyRole() == PinginRole.FISHER && rack.fishCount() < 32
                        // && ForgeEventFactory.getMobGriefingEvent(pingin.level(), pingin) && findIce()) {
                    // work = PinginEntity.CHIP;
                    // return true;
                // }
                // List<PinginEntity> adults = pingin.level().getEntitiesOfClass(PinginEntity.class,
                        // pingin.getBoundingBox().inflate(24D), adult -> adult.isAlive() && !adult.isBaby()
                                // && rack.getBlockPos().equals(adult.getColonyPos()));
                // long guards = adults.stream().filter(adult -> adult.getRecruiterId() == null
                        // && (adult.getColonyRole() == PinginRole.GUARD || adult.hasWeapon())).count();
                // int flint = rack.count(Items.FLINT), sticks = rack.count(Items.STICK), feathers = rack.count(ModItems.PINGIN_FEATHER.get());
                // if ((pingin.getColonyRole() == PinginRole.GUARD || guards < Math.max(1, adults.size() / 4)) && flint >= 1
                        // && sticks >= 2 && feathers >= 1) {
                    // work = PinginEntity.CRAFT;
                    // target = pingin.getHomePos() == null ? rack.getBlockPos().south() : pingin.getHomePos();
                    // return true;
                // }
                // List<BlockPos> nests = ((ServerLevel)pingin.level()).getPoiManager().getInRange(
                        // pointOfInterest -> pointOfInterest.is(ModPoiTypes.PINGIN_NEST.getKey()), rack.getBlockPos(), 16, PoiManager.Occupancy.ANY)
                        // .map(PoiRecord::getPos).toList();
                // if (nests.size() > 0) {
                    // pingin.setNestPos(nests.stream().min(Comparator.comparingDouble(pos -> pingin.distanceToSqr(Vec3.atBottomCenterOf(pos)))).orElseThrow());
                // }
                // if (nests.size() < Math.max(1, (adults.size() + 1) / 2) && sticks >= 4 && feathers >= 1
                        // && pingin.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                    // for (int attempt = 0; attempt < 32; attempt++) {
                        // BlockPos pos = rack.getBlockPos().offset(pingin.getRandom().nextInt(13) - 6, 0,
                                // pingin.getRandom().nextInt(13) - 6);
                        // if (pos.distSqr(rack.getBlockPos()) >= 4D && pingin.level().isEmptyBlock(pos)
                                // && ModBlocks.PINGIN_NEST.get().defaultBlockState().canSurvive(pingin.level(), pos)) {
                            // Path path = pingin.getNavigation().createPath(pos, 0);
                            // if (path != null && path.canReach()) {
                                // work = PinginEntity.BUILD;
                                // target = pos;
                                // return true;
                            // }
                        // }
                    // }
                // }
                // if (pingin.level().getGameTime() >= pingin.getNextPreenTime() && feathers < 8) {
                    // work = PinginEntity.PREEN;
                    // target = pingin.blockPosition();
                    // return true;
                // }
                // boolean needsFlint = flint < 8, needsSticks = sticks < 16;
                // for (BlockPos pos : BlockPos.withinManhattan(rack.getBlockPos(), 8, 2, 8)) {
                    // boolean gravel = needsFlint && pingin.level().getBlockState(pos).is(Blocks.GRAVEL);
                    // boolean shore = false;
                    // if (needsSticks && !gravel) {
                        // for (Direction side : Direction.Plane.HORIZONTAL) {
                            // if (pingin.level().getFluidState(pos.relative(side)).is(FluidTags.WATER)) {
                                // shore = true;
                                // break;
                            // }
                        // }
                    // }
                    // if ((gravel || shore) && pingin.level().isEmptyBlock(pos.above())
                            // && !pingin.level().getBlockState(pos).getCollisionShape(pingin.level(), pos).isEmpty()) {
                        // Path path = pingin.getNavigation().createPath(pos.above(), 0);
                        // if (path != null && path.canReach()) {
                            // material = gravel ? Items.FLINT : Items.STICK;
                            // target = pos.above();
                            // work = PinginEntity.RUMMAGE;
                            // return true;
                        // }
                    // }
                // }
                // rack.finishWork(pingin);
            // }
        // }
        // return false;
    // }

    // @Override
    // public boolean canContinueToUse() {
        // return pingin.isAlive() && pingin.getRecruiterId() == null && pingin.getTarget() == null && !pingin.isAdmiring()
                // && !pingin.isInWaterOrBubble() && rack == pingin.getFishRack() && rack.getThreat() == null
                // && rack.claimWork(pingin) && (work >= PinginEntity.CHIP ? elapsedTicks < 6000 && !ice.isEmpty()
                // : elapsedTicks < 220 && workTicks < 60);
    // }

    // @Override
    // public void start() {
        // elapsedTicks = workTicks = nextPathTick = 0;
        // if (work == PinginEntity.CHIP) {
            // pingin.getNavigation().stop();
            // pingin.setWork(PinginEntity.CHIP_WALK);
        // }
    // }

    // @Override
    // public boolean requiresUpdateEveryTick() { return true; }

    // @Override
    // public void tick() {
        // elapsedTicks++;
        // if (work >= PinginEntity.CHIP) {
            // if (ice.isEmpty()) {
                // pingin.setWork(0);
            // } else {
                // chipIce();
            // }
        // } else if (pingin.distanceToSqr(Vec3.atBottomCenterOf(target)) <= 2.25D) {
            // pingin.getNavigation().stop();
            // pingin.setWork(work);
            // workTicks++;
            // if (workTicks == 60) {
                // if (work == PinginEntity.PREEN) {
                    // pingin.setNextPreenTime(pingin.level().getGameTime() + 2400 + pingin.getRandom().nextInt(2400));
                    // pingin.spawnAtLocation(rack.store(new ItemStack(ModItems.PINGIN_FEATHER.get())));
                // } else if (work == PinginEntity.RUMMAGE) {
                    // pingin.spawnAtLocation(rack.store(new ItemStack(material)));
                // } else if (work == PinginEntity.CRAFT && rack.count(Items.FLINT) >= 1
                        // && rack.count(Items.STICK) >= 2 && rack.count(ModItems.PINGIN_FEATHER.get()) >= 1) {
                    // rack.take(Items.FLINT, 1);
                    // rack.take(Items.STICK, 2);
                    // rack.take(ModItems.PINGIN_FEATHER.get(), 1);
                    // pingin.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ModItems.FLINT_SPEAR.get()));
                    // pingin.setDropChance(EquipmentSlot.MAINHAND, 1F);
                    // pingin.setColonyRole(PinginRole.GUARD);
                // } else if (work == PinginEntity.BUILD && pingin.level().isEmptyBlock(target)
                        // && pingin.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)
                        // && ModBlocks.PINGIN_NEST.get().defaultBlockState().canSurvive(pingin.level(), target)
                        // && rack.count(Items.STICK) >= 4 && rack.count(ModItems.PINGIN_FEATHER.get()) >= 1) {
                    // rack.take(Items.STICK, 4);
                    // rack.take(ModItems.PINGIN_FEATHER.get(), 1);
                    // pingin.level().setBlock(target, ModBlocks.PINGIN_NEST.get().defaultBlockState(), 3);
                    // pingin.setNestPos(target);
                // }
            // }
        // } else if (pingin.tickCount >= nextPathTick) {
            // pingin.getNavigation().moveTo(target.getX() + 0.5D, target.getY(), target.getZ() + 0.5D, 1D);
            // nextPathTick = pingin.tickCount + 20;
        // }
    // }

    // private boolean thinIce(BlockPos pos) {
        // return pingin.level().hasChunkAt(pos) && pingin.level().getBlockState(pos).is(Blocks.ICE)
                // && pingin.level().getFluidState(pos.below()).is(FluidTags.WATER)
                // && pingin.level().getFluidState(pos.below(2)).is(FluidTags.WATER)
                // && (pingin.level().isEmptyBlock(pos.above()) || pingin.level().getBlockState(pos.above()).is(Blocks.SNOW))
                // && pingin.level().isEmptyBlock(pos.above(2)) && pingin.level().canSeeSky(pos.above(2));
    // }

    // private boolean findIce() {
        // for (BlockPos pos : BlockPos.withinManhattan(pingin.blockPosition(), 12, 3, 12)) {
            // if (pingin.level().getFluidState(pos).is(FluidTags.WATER)
                    // && pingin.level().getFluidState(pos.below()).is(FluidTags.WATER)
                    // && pingin.level().isEmptyBlock(pos.above()) && pingin.level().isEmptyBlock(pos.above(2))) {
                // Path path = pingin.getNavigation().createPath(pos, 0);
                // if (path != null && path.canReach()) {
                    // return false;
                // }
            // }
        // }
        // for (BlockPos pos : BlockPos.withinManhattan(pingin.blockPosition().below(), 8, 2, 8)) {
            // if (thinIce(pos)) {
                // BlockPos center = pos.immutable();
                // float phase = pingin.getRandom().nextFloat() * (float)Math.PI * 2F;
                // List<BlockPos> patch = new ArrayList<>();
                // for (int x = -3; x <= 3; x++) {
                    // for (int z = -3; z <= 3; z++) {
                        // double angle = Math.atan2(z, x);
                        // double radius = 3.4D + 0.2D * Math.sin(angle * 3D + phase) + 0.15D * Math.cos(angle * 5D - phase);
                        // BlockPos block = center.offset(x, 0, z);
                        // if (x * x + z * z <= radius * radius && thinIce(block)) {
                            // patch.add(block);
                        // }
                    // }
                // }
                // ice.add(center);
                // for (int i = 0; i < ice.size(); i++) {
                    // for (Direction side : Direction.Plane.HORIZONTAL) {
                        // BlockPos next = ice.get(i).relative(side);
                        // if (patch.contains(next) && !ice.contains(next)) {
                            // ice.add(next);
                        // }
                    // }
                // }
                // ice.sort(Comparator.comparingDouble(center::distSqr));
                // target = ice.get(0);
                // return true;
            // }
        // }
        // return false;
    // }

    // private void chipIce() {
        // boolean finished = false;
        // if (shorePos == null) {
            // target = ice.get(0);
            // int bestLength = Integer.MAX_VALUE;
            // for (Direction side : Direction.Plane.HORIZONTAL) {
                // BlockPos pos = target.relative(side).above();
                // if (pingin.level().getFluidState(pos).isEmpty() && pingin.level().getFluidState(pos.below()).isEmpty()
                        // && pingin.level().getBlockState(pos.below()).isFaceSturdy(pingin.level(), pos.below(), Direction.UP)
                        // && (pingin.level().isEmptyBlock(pos) || pingin.level().getBlockState(pos).is(Blocks.SNOW))
                        // && pingin.level().isEmptyBlock(pos.above())) {
                    // Path path = pingin.getNavigation().createPath(pos, 0);
                    // if (path != null && path.canReach() && path.getNodeCount() < bestLength) {
                        // shorePos = pos;
                        // bestLength = path.getNodeCount();
                    // }
                // }
            // }
            // if (shorePos == null) {
                // finished = true;
            // }
        // } else if (workTicks == 0 && pingin.getWork() == PinginEntity.CHIP_WALK) {
            // Vec3 stance = Vec3.atBottomCenterOf(shorePos).lerp(Vec3.atBottomCenterOf(target.above()), 0.25D);
            // if (pingin.onGround() && pingin.distanceToSqr(stance) < 0.0064D) {
                // pingin.getNavigation().stop();
                // pingin.setDeltaMovement(0D, 0D, 0D);
                // pingin.setWork(PinginEntity.CHIP);
                // workTicks = 1;
            // } else if (pingin.tickCount >= nextPathTick) {
                // pingin.getNavigation().moveTo(shorePos.getX() + 0.5D, shorePos.getY(), shorePos.getZ() + 0.5D, 1D);
                // nextPathTick = pingin.tickCount + 10;
            // }
            // if (workTicks == 0 && pingin.getNavigation().isDone() && pingin.distanceToSqr(Vec3.atBottomCenterOf(shorePos)) < 2.25D
                    // && (pingin.blockPosition().below().equals(target) || pingin.blockPosition().below().equals(shorePos.below()))) {
                // pingin.getMoveControl().setWantedPosition(stance.x, stance.y, stance.z, 1D);
            // }
        // } else {
            // pingin.getNavigation().stop();
            // pingin.getMoveControl().setWantedPosition(pingin.getX(), pingin.getY(), pingin.getZ(), 0D);
            // pingin.getLookControl().setLookAt(target.getX() + 0.5D, pingin.getEyeY(), target.getZ() + 0.5D, 45F, 30F);
            // workTicks++;
            // if (work == PinginEntity.CHIP) {
                // int hopTick = PinginEntity.CHIP_PECK_TICKS + 8;
                // if (workTicks < hopTick) {
                    // pingin.setDeltaMovement(0D, 0D, 0D);
                    // float yaw = (float)(Math.atan2(pingin.getX() - target.getX() - 0.5D,
                            // target.getZ() + 0.5D - pingin.getZ()) * Mth.RAD_TO_DEG);
                    // pingin.setYRot(Mth.rotLerp(0.4F, pingin.getYRot(), yaw));
                    // pingin.yBodyRot = pingin.getYRot();
                    // pingin.setYHeadRot(pingin.getYRot());
                    // if ((workTicks == 11 || workTicks == 18) && thinIce(target)
                            // && ForgeEventFactory.getMobGriefingEvent(pingin.level(), pingin)) {
                        // Vec3 point = pingin.position().add(pingin.getViewVector(1F).multiply(1D, 0D, 1D).normalize().scale(0.36D));
                        // pingin.level().destroyBlockProgress(pingin.getId(), target, workTicks == 11 ? 4 : 8);
                        // ((ServerLevel)pingin.level()).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.ICE.defaultBlockState()),
                                // point.x, target.getY() + 1.02D, point.z, 4, 0.035D, 0.02D, 0.035D, 0.03D);
                        // pingin.playSound(SoundEvents.GLASS_HIT, 0.3F, 1.3F);
                    // }
                // } else if (workTicks == hopTick) {
                    // if (thinIce(target)) {
                        // jumpTo(target.above());
                    // } else {
                        // finished = true;
                    // }
                // } else if (workTicks > hopTick + 2 && pingin.onGround()) {
                    // if (pingin.blockPosition().below().equals(target) && thinIce(target)) {
                        // work = PinginEntity.CHIP_BACK;
                        // workTicks = 0;
                        // pingin.setDeltaMovement(0D, 0D, 0D);
                        // pingin.setWork(PinginEntity.CHIP_BACK);
                        // pingin.playSound(SoundEvents.PLAYER_SMALL_FALL, 0.4F, 1.4F);
                    // } else {
                        // finished = true;
                    // }
                // }
            // } else if (workTicks == 2) {
                // pingin.level().destroyBlockProgress(pingin.getId(), target, -1);
                // if (thinIce(target) && pingin.onGround() && pingin.blockPosition().below().equals(target)
                        // && ForgeEventFactory.getMobGriefingEvent(pingin.level(), pingin)
                        // && pingin.level().getFluidState(shorePos.below()).isEmpty()
                        // && pingin.level().getBlockState(shorePos.below()).isFaceSturdy(pingin.level(), shorePos.below(), Direction.UP)) {
                    // if (pingin.level().getBlockState(target.above()).is(Blocks.SNOW)) {
                        // pingin.level().destroyBlock(target.above(), false);
                    // }
                    // pingin.level().levelEvent(2001, target, Block.getId(Blocks.ICE.defaultBlockState()));
                    // pingin.level().setBlock(target, Blocks.WATER.defaultBlockState(), 3);
                    // pingin.playSound(SoundEvents.PARROT_FLY, 0.4F, 1.5F);
                // }
                // jumpTo(shorePos);
            // } else if (workTicks >= 18 && pingin.onGround()) {
                // pingin.setDeltaMovement(0D, 0D, 0D);
                // finished = true;
            // }
        // }
        // if (finished) {
            // if (pingin.getWork() == PinginEntity.CHIP) {
                // pingin.level().destroyBlockProgress(pingin.getId(), target, -1);
            // }
            // ice.remove(0);
            // shorePos = null;
            // workTicks = 0;
            // work = PinginEntity.CHIP;
            // pingin.setWork(PinginEntity.CHIP_WALK);
        // }
    // }

    // private void jumpTo(BlockPos pos) {
        // Vec3 gap = Vec3.atBottomCenterOf(pos).subtract(pingin.position());
        // double flightTicks = (0.36D + Math.sqrt(Math.max(0D, 0.1296D - 0.16D * gap.y))) / 0.08D;
        // double dragSum = (1D - Math.pow(0.91D, flightTicks)) / 0.09D;
        // pingin.setOnGround(false);
        // if (work == PinginEntity.CHIP) {
            // float yaw = (float)(Math.atan2(-gap.x, gap.z) * 180D / Math.PI);
            // pingin.setYRot(yaw);
            // pingin.yBodyRot = yaw;
            // pingin.setYHeadRot(yaw);
        // }
        // pingin.setDeltaMovement(gap.x / dragSum, 0.36D, gap.z / dragSum);
        // pingin.hasImpulse = true;
    // }

    // @Override
    // public void stop() {
        // if (work >= PinginEntity.CHIP && target != null) {
            // pingin.level().destroyBlockProgress(pingin.getId(), target, -1);
        // }
        // pingin.setWork(0);
        // pingin.getNavigation().stop();
        // if (rack != null) {
            // rack.finishWork(pingin);
        // }
        // rack = null;
        // ice.clear();
        // shorePos = null;
    // }
// }

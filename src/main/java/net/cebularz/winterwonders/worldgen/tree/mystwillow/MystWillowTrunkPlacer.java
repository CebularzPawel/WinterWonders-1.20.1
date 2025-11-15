package net.cebularz.winterwonders.worldgen.tree.mystwillow;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.*;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.cebularz.winterwonders.worldgen.tree.ModTrunkPlacers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

public class MystWillowTrunkPlacer extends TrunkPlacer {
    public static final Codec<MystWillowTrunkPlacer> CODEC = RecordCodecBuilder.create(instance ->
            trunkPlacerParts(instance).apply(instance, MystWillowTrunkPlacer::new));

    public MystWillowTrunkPlacer(int baseHeight, int heightRandA, int heightRandB) {
        super(baseHeight, heightRandA, heightRandB);
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return ModTrunkPlacers.MYST_WILLOW_TRUNK_PLACER.get();
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader level, BiConsumer<BlockPos,
            BlockState> blockSetter, RandomSource random, int freeTreeHeight, BlockPos basePos, TreeConfiguration treeConfig) {
        int height = Math.max(3, freeTreeHeight
                + random.nextInt(this.heightRandA + 1)
                + random.nextInt(this.heightRandB + 1)
        );

        for (int dx = -1; dx <= 2; dx++) {
            for (int dz = -1; dz <= 2; dz++) {
                if (!validLogSpace(level, basePos.offset(dx, 0, dz))) {
                    return ImmutableList.of();
                }
            }
        }
        for (int dx = 0; dx <= 1; dx++) {
            for (int dz = 0; dz <= 1; dz++) {
                if (!validLogSpace(level, basePos.offset(dx, 1, dz))) {
                    return ImmutableList.of();
                }
            }
        }

        BlockPos below = basePos.below();
        setDirtAt(level, blockSetter, random, below, treeConfig);
        setDirtAt(level, blockSetter, random, below.east(), treeConfig);
        setDirtAt(level, blockSetter, random, below.south(), treeConfig);
        setDirtAt(level, blockSetter, random, below.south().east(), treeConfig);

        List<FoliagePlacer.FoliageAttachment> attachments = new ArrayList<>();

        placeBaseLogs(level, blockSetter, random, basePos, treeConfig);

        BlockPos[] rootCandidates = new BlockPos[] {
                basePos.offset(-1, 0,  0), basePos.offset( 2, 0,  0),
                basePos.offset( 0, 0, -1), basePos.offset( 1, 0, -1),
                basePos.offset( 0, 0,  2), basePos.offset( 1, 0,  2),
                basePos.offset(-1, 0, -1), basePos.offset( 2, 0, -1),
                basePos.offset(-1, 0,  2), basePos.offset( 2, 0,  2)
        };

        float rootProbability = 0.55f;
        int minimumRoots = 4;
        fisherYatesShuffle(rootCandidates, random);

        List<BlockPos> chosenRoots = new ArrayList<>(8);
        for (BlockPos rootPos : rootCandidates) {
            if (random.nextFloat() <= rootProbability && validLogSpace(level, rootPos) && respectsSpacing(rootPos, chosenRoots)) {
                chosenRoots.add(rootPos);
            }
        }
        if (chosenRoots.size() < minimumRoots) {
            for (BlockPos rootPos : rootCandidates) {
                if (!containsBlockPos(chosenRoots, rootPos) && validLogSpace(level, rootPos) && respectsSpacing(rootPos, chosenRoots)) {
                    chosenRoots.add(rootPos);
                    if (chosenRoots.size() >= minimumRoots) {
                        break;
                    }
                }
            }
        }
        if (chosenRoots.size() < minimumRoots) {
            return ImmutableList.of();
        }

        for (BlockPos rootPos : chosenRoots) {
            setDirtAt(level, blockSetter, random, rootPos.below(), treeConfig);
            this.placeLog(level, blockSetter, random, rootPos, treeConfig);
        }

        placeBaseLogs(level, blockSetter, random, basePos.above(1), treeConfig);

        int centerOffsetX = random.nextBoolean() ? 0 : 1;
        int centerOffsetZ = random.nextBoolean() ? 0 : 1;
        int bendSignX = (centerOffsetX == 0) ? -1 : 1;
        int bendSignZ = (centerOffsetZ == 0) ? -1 : 1;

        List<BlockPos> candidateOffsets = new ArrayList<>(Arrays.asList(
                new BlockPos(bendSignX, 0, 0),
                new BlockPos(0, 0, bendSignZ),
                new BlockPos(bendSignX, 0, bendSignZ),
                new BlockPos(-bendSignX, 0, 0),
                new BlockPos(0, 0, -bendSignZ),
                new BlockPos(-bendSignX, 0, bendSignZ),
                new BlockPos(bendSignX, 0, -bendSignZ),
                new BlockPos(-bendSignX, 0, -bendSignZ)
        ));
        softShuffle(candidateOffsets, random);

        List<BlockPos> activeOffsets = new ArrayList<>();
        BlockPos lastCenter = basePos.above(1);

        for (int y = 2; y < height; y++) {
            BlockPos center = basePos.offset(centerOffsetX, y, centerOffsetZ);
            if (validLogSpace(level, center)) {
                this.placeLog(level, blockSetter, random, center, treeConfig);
            }
            lastCenter = center;

            for (BlockPos relativeOffset : activeOffsets) {
                BlockPos columnPos = center.offset(relativeOffset.getX(), 0, relativeOffset.getZ());
                if (validLogSpace(level, columnPos)) {
                    this.placeLog(level, blockSetter, random, columnPos, treeConfig);
                }
            }

            if (y >= 3) {
                int requiredExtras = Mth.clamp(y - 2, 1, 8);
                int additionalNeeded = requiredExtras - activeOffsets.size();
                if (additionalNeeded > 0) {
                    for (BlockPos optionOffset : candidateOffsets) {
                        if (additionalNeeded <= 0) {
                            break;
                        }
                        if (!containsRelative(activeOffsets, optionOffset)) {
                            BlockPos target = center.offset(optionOffset.getX(), 0, optionOffset.getZ());
                            if (validLogSpace(level, target)) {
                                this.placeLog(level, blockSetter, random, target, treeConfig);
                                activeOffsets.add(optionOffset);
                                additionalNeeded--;
                            }
                        }
                    }
                }
            }
        }

        placeBranches(level, blockSetter, random, treeConfig, lastCenter, 3);
        attachments.add(new FoliagePlacer.FoliageAttachment(lastCenter, 0, true));
        return ImmutableList.copyOf(attachments);
    }

    private void placeBranches(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter,
                               RandomSource random, TreeConfiguration treeConfig, BlockPos centerPos, int branches) {
        int clampedBranches = Math.max(0, Math.min(branches, 4));
        if (clampedBranches == 0) {
            return;
        }

        Direction[] directions = new Direction[] {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
        for (int i = 0; i < directions.length; i++) {
            if (random.nextFloat() < 0.5f) {
                int j = random.nextInt(directions.length);
                Direction temp = directions[i];
                directions[i] = directions[j];
                directions[j] = temp;
            }
        }

        int branchesPlaced = 0;
        for (Direction direction : directions) {
            if (branchesPlaced >= clampedBranches) {
                break;
            }

            int outwardLength = 2;
            int perpendicularLength = 1;

            Axis axis = (direction == Direction.EAST || direction == Direction.WEST) ? Axis.X : Axis.Z;
            BlockState outwardState = treeConfig.trunkProvider.getState(random, centerPos);
            if (outwardState.hasProperty(RotatedPillarBlock.AXIS)) {
                outwardState = outwardState.setValue(RotatedPillarBlock.AXIS, axis);
            }

            BlockPos[] startCandidates = new BlockPos[] {
                    centerPos.relative(direction, 1),
                    centerPos.relative(direction, 2)
            };

            List<BlockPos> outwardPositions = null;

            for (BlockPos startPos : startCandidates) {
                List<BlockPos> tempPositions = new ArrayList<>();
                BlockPos currentPos = startPos;
                boolean valid = true;

                for (int i = 0; i < outwardLength; i++) {
                    if (!(TreeFeature.validTreePos(level, currentPos) || TreeFeature.isAirOrLeaves(level, currentPos))) {
                        valid = false;
                        break;
                    }
                    tempPositions.add(currentPos.immutable());
                    currentPos = currentPos.relative(direction);
                }

                if (valid) {
                    outwardPositions = tempPositions;
                    break;
                }
            }

            if (outwardPositions == null || outwardPositions.isEmpty()) {
                continue;
            }

            for (BlockPos position : outwardPositions) {
                blockSetter.accept(position, outwardState);
            }

            BlockPos branchEnd = outwardPositions.get(outwardPositions.size() - 1);
            Direction leftDirection = direction.getCounterClockWise();
            Direction rightDirection = direction.getClockWise();

            Direction primaryPerpendicular = random.nextBoolean() ? leftDirection : rightDirection;
            Direction secondaryPerpendicular = (primaryPerpendicular == leftDirection) ? rightDirection : leftDirection;

            Axis perpendicularAxis = (axis == Axis.X) ? Axis.Z : Axis.X;
            BlockState perpendicularState = outwardState.setValue(RotatedPillarBlock.AXIS, perpendicularAxis);

            int perpendicularPlaced = 0;

            BlockPos currentPos = branchEnd.relative(primaryPerpendicular);
            for (int i = 0; i < perpendicularLength; i++) {
                if (!(TreeFeature.validTreePos(level, currentPos) || TreeFeature.isAirOrLeaves(level, currentPos))) {
                    break;
                }
                blockSetter.accept(currentPos, perpendicularState);
                perpendicularPlaced++;
                currentPos = currentPos.relative(primaryPerpendicular);
            }

            if (perpendicularPlaced == 0) {
                currentPos = branchEnd.relative(secondaryPerpendicular);
                for (int i = 0; i < perpendicularLength; i++) {
                    if (!(TreeFeature.validTreePos(level, currentPos) || TreeFeature.isAirOrLeaves(level, currentPos))) {
                        break;
                    }
                    blockSetter.accept(currentPos, perpendicularState);
                    perpendicularPlaced++;
                    currentPos = currentPos.relative(secondaryPerpendicular);
                }
            }
            branchesPlaced++;
        }
    }

    private void placeBaseLogs(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter,
                               RandomSource random, BlockPos originPos, TreeConfiguration treeConfig) {
        this.placeLog(level, blockSetter, random, originPos, treeConfig);
        this.placeLog(level, blockSetter, random, originPos.east(), treeConfig);
        this.placeLog(level, blockSetter, random, originPos.south(), treeConfig);
        this.placeLog(level, blockSetter, random, originPos.east().south(), treeConfig);
    }

    private static boolean validLogSpace(LevelSimulatedReader level, BlockPos blockPos) {
        return TreeFeature.isAirOrLeaves(level, blockPos) || TreeFeature.validTreePos(level, blockPos);
    }

    private static boolean respectsSpacing(BlockPos candidatePos, List<BlockPos> chosenPositions) {
        for (BlockPos existingPos : chosenPositions) {
            int dx = Math.abs(candidatePos.getX() - existingPos.getX());
            int dz = Math.abs(candidatePos.getZ() - existingPos.getZ());
            if (dx <= 1 && dz <= 1) {
                return false;
            }
        }
        return true;
    }

    private static boolean containsBlockPos(List<BlockPos> blockPosList, BlockPos blockPos) {
        for (BlockPos existingPos : blockPosList) {
            if (existingPos.equals(blockPos)) {
                return true;
            }
        }
        return false;
    }

    private static boolean containsRelative(List<BlockPos> blockPosList, BlockPos blockPos) {
        for (BlockPos existingPos : blockPosList) {
            if (existingPos.getX() == blockPos.getX() && existingPos.getZ() == blockPos.getZ()) {
                return true;
            }
        }
        return false;
    }

    private static void fisherYatesShuffle(BlockPos[] positions, RandomSource random) {
        for (int i = positions.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            BlockPos temp = positions[i];
            positions[i] = positions[j];
            positions[j] = temp;
        }
    }

    private static void softShuffle(List<BlockPos> positions, RandomSource random) {
        for (int i = 0; i < positions.size(); i++) {
            if (random.nextFloat() < 0.35f) {
                int j = random.nextInt(positions.size());
                BlockPos a = positions.get(i);
                positions.set(i, positions.get(j));
                positions.set(j, a);
            }
        }
    }
}
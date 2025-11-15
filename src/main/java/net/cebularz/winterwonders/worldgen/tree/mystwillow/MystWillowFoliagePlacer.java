package net.cebularz.winterwonders.worldgen.tree.mystwillow;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.cebularz.winterwonders.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.cebularz.winterwonders.worldgen.tree.ModFoliagePlacers;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class MystWillowFoliagePlacer extends FoliagePlacer {
    public static final Codec<MystWillowFoliagePlacer> CODEC = RecordCodecBuilder.create(instance ->
            foliagePlacerParts(instance)
                    .and(Codec.intRange(0, 16).fieldOf("height").forGetter(placer -> placer.height))
                    .apply(instance, MystWillowFoliagePlacer::new));

    private final int height;

    public MystWillowFoliagePlacer(IntProvider radiusProvider, IntProvider offsetProvider, int height) {
        super(radiusProvider, offsetProvider);
        this.height = height;
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return ModFoliagePlacers.MYST_WILLOW_FOLIAGE_PLACER.get();
    }

    @Override
    protected void createFoliage(LevelSimulatedReader level, FoliageSetter foliageSetter, RandomSource randomSource,
                                 TreeConfiguration configuration, int maxFreeTreeHeight, FoliageAttachment attachment,
                                 int foliageHeight, int foliageRadius, int offset) {
        if (!attachment.doubleTrunk()) {
            return;
        }

        BlockPos centerPos = attachment.pos();
        BlockState leafState = configuration.foliageProvider.getState(randomSource, centerPos);

        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                int radiusSq = dx * dx + dz * dz;
                if (radiusSq > 9) {
                    continue;
                }

                BlockPos leafPos = centerPos.offset(dx, 0, dz);
                if (canPlaceLeaf(level, leafPos)) {
                    foliageSetter.set(leafPos, leafState);
                }
            }
        }

        BlockPos topCenterPos = centerPos.above();
        Set<Long> topFootprint = new HashSet<>();

        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                if (Math.abs(dx) == 2 && Math.abs(dz) == 2) {
                    continue;
                }

                BlockPos leafPos = topCenterPos.offset(dx, 0, dz);
                if (canPlaceLeaf(level, leafPos)) {
                    foliageSetter.set(leafPos, leafState);
                    topFootprint.add(encodeOffset(dx, dz));
                }
            }
        }

        for (int dx = -5; dx <= 5; dx++) {
            for (int dz = -5; dz <= 5; dz++) {
                BlockPos logPos = centerPos.offset(dx, 0, dz);
                if (isLog(level, logPos)) {
                    BlockPos coverPos = logPos.above();
                    if (canPlaceLeaf(level, coverPos)) {
                        foliageSetter.set(coverPos, leafState);
                        topFootprint.add(encodeOffset(dx, dz));
                    }
                }
            }
        }

        for (int dx = -5; dx <= 5; dx++) {
            for (int dz = -5; dz <= 5; dz++) {
                BlockPos logPos = centerPos.offset(dx, 0, dz);
                if (!isLog(level, logPos)) {
                    continue;
                }

                for (Direction direction : new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST}) {
                    BlockPos sidePos = logPos.relative(direction);
                    if (canPlaceLeaf(level, sidePos)) {
                        foliageSetter.set(sidePos, leafState);
                    }
                }
            }
        }

        BlockPos bottomCenterPos = centerPos.below();
        Set<Long> outlineFootprint = getOutlineFootprint(topFootprint);

        for (long key : outlineFootprint) {
            int dx = decodeOffsetX(key);
            int dz = decodeOffsetZ(key);
            BlockPos outlinePos = bottomCenterPos.offset(dx, 0, dz);
            if (canPlaceLeaf(level, outlinePos)) {
                foliageSetter.set(outlinePos, leafState);
            }
        }

        for (long key : outlineFootprint) {
            int dx = decodeOffsetX(key);
            int dz = decodeOffsetZ(key);

            BlockPos rimLeafPos = bottomCenterPos.offset(dx, 0, dz);
            BlockPos vineHeadPos = rimLeafPos.below();

            if (randomSource.nextFloat() >= 0.4f) {
                continue;
            }

            if (!canPlaceVine(level, vineHeadPos)) {
                continue;
            }

            BlockPos currentHeadPos = vineHeadPos;
            foliageSetter.set(currentHeadPos, ModBlocks.ICY_VINES.get().defaultBlockState());

            int vineSegments = 1;
            while (vineSegments < 3 && randomSource.nextFloat() > 0.65f) {
                BlockPos nextHeadPos = currentHeadPos.below();
                if (!canPlaceVine(level, nextHeadPos)) {
                    break;
                }

                foliageSetter.set(currentHeadPos, ModBlocks.ICY_VINES_PLANT.get().defaultBlockState());
                foliageSetter.set(nextHeadPos, ModBlocks.ICY_VINES.get().defaultBlockState());

                currentHeadPos = nextHeadPos;
                vineSegments++;
            }
        }
    }

    private static @NotNull Set<Long> getOutlineFootprint(Set<Long> topFootprint) {
        Set<Long> outlineFootprint = new HashSet<>();

        for (long key : topFootprint) {
            int baseDx = decodeOffsetX(key);
            int baseDz = decodeOffsetZ(key);

            for (int neighborDx = -1; neighborDx <= 1; neighborDx++) {
                for (int neighborDz = -1; neighborDz <= 1; neighborDz++) {
                    if (neighborDx == 0 && neighborDz == 0) {
                        continue;
                    }

                    int outlineDx = baseDx + neighborDx;
                    int outlineDz = baseDz + neighborDz;
                    long outlineKey = encodeOffset(outlineDx, outlineDz);

                    if (!topFootprint.contains(outlineKey)) {
                        outlineFootprint.add(outlineKey);
                    }
                }
            }
        }
        return outlineFootprint;
    }

    private static boolean canPlaceVine(LevelSimulatedReader level, BlockPos blockPos) {
        return level.isStateAtPosition(blockPos, blockState ->
                blockState.isAir() || blockState.is(BlockTags.REPLACEABLE_BY_TREES)
        );
    }

    private static boolean canPlaceLeaf(LevelSimulatedReader level, BlockPos blockPos) {
        return TreeFeature.isAirOrLeaves(level, blockPos) || TreeFeature.validTreePos(level, blockPos);
    }

    private static boolean isLog(LevelSimulatedReader level, BlockPos blockPos) {
        return level.isStateAtPosition(blockPos, state -> state.is(BlockTags.LOGS));
    }

    private static long encodeOffset(int dx, int dz) {
        return (((long) (dx + 64)) << 8) | (long) (dz + 64);
    }

    private static int decodeOffsetX(long key) {
        return (int) ((key >> 8) & 0x7FL) - 64;
    }

    private static int decodeOffsetZ(long key) {
        return (int) (key & 0xFFL) - 64;
    }

    @Override
    public int foliageHeight(RandomSource randomSource, int trunkHeight, TreeConfiguration treeConfiguration) {
        return this.height;
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource pRandom, int pLocalX, int pLocalY, int pLocalZ, int pRange, boolean pLarge) {
        return false;
    }
}

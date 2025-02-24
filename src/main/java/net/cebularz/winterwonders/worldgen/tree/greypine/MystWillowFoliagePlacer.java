package net.cebularz.winterwonders.worldgen.tree.greypine;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.cebularz.winterwonders.block.custom.TreeMushroomWallBlock;
import net.cebularz.winterwonders.init.ModBlocks;
import net.cebularz.winterwonders.worldgen.tree.ModFoliagePlacers;

import java.util.Comparator;
import java.util.List;

public class MystWillowFoliagePlacer extends FoliagePlacer {
    public static final Codec<MystWillowFoliagePlacer> CODEC = RecordCodecBuilder.create(greyPineFoliagePlacerInstance
            -> foliagePlacerParts(greyPineFoliagePlacerInstance).and(Codec.intRange(0,16).fieldOf("height")
            .forGetter(fp -> fp.height)).apply(greyPineFoliagePlacerInstance, MystWillowFoliagePlacer::new));

    private final int height;

    public MystWillowFoliagePlacer(IntProvider pRadius, IntProvider pOffset, int height) {
        super(pRadius, pOffset);
        this.height = height;
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return ModFoliagePlacers.GREYPINE_FOLIAGE_PLACER.get();
    }

    @Override
    protected void createFoliage(LevelSimulatedReader pLevel, FoliageSetter pBlockSetter, RandomSource pRandom,
                                 TreeConfiguration pConfig, int pMaxFreeTreeHeight, FoliageAttachment pAttachment,
                                 int pFoliageHeight, int pFoliageRadius, int pOffset) {
        BlockPos originPos = pAttachment.pos();
        int treeVariant = pRandom.nextInt(1, 10);

        List<int[]> rows = getFoliageRows(treeVariant);

        for (int[] row : rows) {
            int radius = row[0];
            int vOffset = row[1];
            placeLeavesRow(pLevel, pBlockSetter, pRandom, pConfig,
                    originPos.above(0),
                    radius,
                    vOffset,
                    pAttachment.doubleTrunk());
        }

        int lowestFoliage = rows.stream().mapToInt(row -> row[1]).min().orElse(0);
        for (int y = lowestFoliage - 1; y >= lowestFoliage - (pMaxFreeTreeHeight - (pFoliageHeight - 4)); y--) {
            for (Direction dir : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST}) {
                if (pRandom.nextFloat() > 0.85f) {
                    BlockPos mushroomPos = originPos.above(y).relative(dir, 1);
                    if (TreeFeature.validTreePos(pLevel, mushroomPos)) {
                        pBlockSetter.set(mushroomPos,
                                ModBlocks.WONDER_TREE_SHROOM_WALL.get().defaultBlockState()
                                        .setValue(TreeMushroomWallBlock.FACING, dir));
                    }
                }
            }
        }

        List<int[]> vineRows = rows.stream()
                .sorted(Comparator.comparingInt(a -> a[1]))
                .limit(3)
                .toList();

        for (int[] row : vineRows) {
            int radius = row[0];
            int vOffset = row[1];
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (Math.max(Math.abs(x), Math.abs(z)) == radius) {
                        if (pRandom.nextFloat() > 0.8f) {
                            BlockPos leafPos = originPos.offset(x, vOffset, z);
                            BlockPos vinePos = leafPos.below();
                            if (pLevel.isStateAtPosition(leafPos, (p_284924_) -> p_284924_.is(BlockTags.LEAVES)) &&
                                    pLevel.isStateAtPosition(vinePos, BlockBehaviour.BlockStateBase::isAir)) {
                                pBlockSetter.set(vinePos, ModBlocks.ICY_VINES.get().defaultBlockState());
                                if (pRandom.nextFloat() > 0.65f) {
                                    BlockPos vine2Pos = vinePos.below();
                                    pBlockSetter.set(vinePos, ModBlocks.ICY_VINES_PLANT.get().defaultBlockState());
                                    pBlockSetter.set(vine2Pos, ModBlocks.ICY_VINES.get().defaultBlockState());
                                    if (pRandom.nextFloat() > 0.65f) {
                                        BlockPos vine3Pos = vine2Pos.below();
                                        pBlockSetter.set(vinePos, ModBlocks.ICY_VINES_PLANT.get().defaultBlockState());
                                        pBlockSetter.set(vine2Pos, ModBlocks.ICY_VINES_PLANT.get().defaultBlockState());
                                        pBlockSetter.set(vine3Pos, ModBlocks.ICY_VINES.get().defaultBlockState());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    private List<int[]> getFoliageRows(int treeVariant) {
        if (treeVariant >= 9){
            return List.of(
                    new int[]{0, 4},
                    new int[]{1, 3},
                    new int[]{1, 2},
                    new int[]{2, 1},
                    new int[]{2, 0},
                    new int[]{3, -1},
                    new int[]{3, -2},
                    new int[]{2, -3},
                    new int[]{3, -4},
                    new int[]{2, -5},
                    new int[]{3, -6},
                    new int[]{2, -7},
                    new int[]{3, -8},
                    new int[]{1, -9},
                    new int[]{2, -10}
            );
        } else if (treeVariant >= 4) {
            return List.of(
                    new int[]{0, 4},
                    new int[]{1, 3},
                    new int[]{1, 2},
                    new int[]{2, 1},
                    new int[]{2, 0},
                    new int[]{3, -1},
                    new int[]{3, -2},
                    new int[]{2, -3},
                    new int[]{3, -4},
                    new int[]{2, -5},
                    new int[]{3, -6},
                    new int[]{1, -7},
                    new int[]{2, -8}
            );
        } else if (treeVariant > 2) {
            return List.of(
                    new int[]{0,4},
                    new int[]{1,3},
                    new int[]{1,2},
                    new int[]{2,1},
                    new int[]{2, 0},
                    new int[]{3, -1},
                    new int[]{3, -2},
                    new int[]{2, -3},
                    new int[]{3, -4},
                    new int[]{1, -5},
                    new int[]{2, -6}
            );
        } else {
            return List.of(
                    new int[]{0, 4},
                    new int[]{1, 3},
                    new int[]{1, 2},
                    new int[]{2, 1},
                    new int[]{2, 0},
                    new int[]{3, -1},
                    new int[]{3, -2},
                    new int[]{1, -3},
                    new int[]{2, -4}
            );
        }
    }


    @Override
    public int foliageHeight(RandomSource randomSource, int i, TreeConfiguration treeConfiguration) {
        return this.height;
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource pRandom, int pLocalX, int pLocalY, int pLocalZ, int pRange, boolean pLarge) {
        if (pLocalY >= -1) {
            return pLocalX * pLocalX + pLocalZ * pLocalZ > pRange * pRange;
        } else {
            return pLocalX == pRange && pLocalZ == pRange && pRange > 0;
        }
    }
}
package net.turtleboi.winterwonders.worldgen.tree.greypine;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.turtleboi.winterwonders.worldgen.tree.ModFoliagePlacers;

import java.util.List;

public class GreypineFoliagePlacer extends FoliagePlacer {
    public static final Codec<GreypineFoliagePlacer> CODEC = RecordCodecBuilder.create(greyPineFoliagePlacerInstance
            -> foliagePlacerParts(greyPineFoliagePlacerInstance).and(Codec.intRange(0,16).fieldOf("height")
            .forGetter(fp -> fp.height)).apply(greyPineFoliagePlacerInstance, GreypineFoliagePlacer::new));

    private final int height;

    public GreypineFoliagePlacer(IntProvider pRadius, IntProvider pOffset, int height) {
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

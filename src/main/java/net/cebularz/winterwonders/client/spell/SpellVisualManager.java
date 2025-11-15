package net.cebularz.winterwonders.client.spell;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.turtleboi.turtlecore.client.data.SpikeData;
import net.turtleboi.turtlecore.client.renderer.IceSpikeRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SpellVisualManager {
    private static final Random random = new Random();
    private record IceSpikes(BlockPos blockPos, long spawnTime, SpikeData spikeData, float yawDegrees) {}
    private static final List<IceSpikes> activeIceSpikes = new ArrayList<>();
    private static final long spikeLifetime = 3; //seconds
    private static final SpikeData[] premadeSpikes = SpikeData.createPremadeSpikes(random);

    public static void addSpike(BlockPos blockPos, float yawDegrees) {
        long currentTime = System.currentTimeMillis();
        SpikeData spikeData = premadeSpikes[random.nextInt(premadeSpikes.length)];
        activeIceSpikes.add(new IceSpikes(blockPos, currentTime, spikeData, yawDegrees));
    }

    public static void renderIceSpikes(PoseStack poseStack, MultiBufferSource multiBufferSource, Camera clientCamera) {
        if (activeIceSpikes.isEmpty()) return;
        long currentTime = System.currentTimeMillis();
        double camX = clientCamera.getPosition().x;
        double camY = clientCamera.getPosition().y;
        double camZ = clientCamera.getPosition().z;

        activeIceSpikes.removeIf(iceSpike -> currentTime - iceSpike.spawnTime() > getSpikeLifetime());
        for (IceSpikes iceSpike : activeIceSpikes) {
            SpikeData spikeData = iceSpike.spikeData();
            float age = (currentTime - iceSpike.spawnTime()) / getSpikeLifetime();
            age = Mth.clamp(age, 0.0F, 1.0F);

            float fadeStart = 0.9F;
            int alpha;
            if (age < fadeStart) {
                alpha = 255;
            } else {
                float t = (age - fadeStart) / (1.0F - fadeStart);
                t = Mth.clamp(t, 0.0F, 1.0F);
                alpha = (int)(255 * (1.0F - t));
            }

            double x = iceSpike.blockPos().getX() + 0.5 - camX;
            double y = iceSpike.blockPos().getY() - camY;
            double z = iceSpike.blockPos().getZ() + 0.5 - camZ;
            float baseYaw = iceSpike.yawDegrees();
            float yAngle = baseYaw + spikeData.yAngleOffset;

            poseStack.pushPose();
            poseStack.translate(x, y, z);

            float scaleDivider = 16f;
            float minSize = 1/scaleDivider;
            float maxSize = 3/scaleDivider;
            float growEnd = 0.0125F;
            float shrinkStart = 0.5F;

            float size;
            if (age < growEnd) {
                float t = age / growEnd;
                size = Mth.lerp(t, minSize, maxSize);
            } else if (age < shrinkStart) {
                size = maxSize;
            } else {
                float t = (age - shrinkStart) / (1.0F - shrinkStart);
                size = maxSize - (maxSize - (maxSize * 0.8f)) * t;
            }

            poseStack.scale(size, size, size);
            IceSpikeRenderer.renderSpike(
                    poseStack,
                    multiBufferSource,
                    spikeData,
                    1,
                    spikeData.zTranslationOffset,
                    spikeData.xAngleOffset,
                    yAngle,
                    spikeData.zAngleOffset,
                    alpha
            );
            poseStack.popPose();
        }
    }

    public static float getSpikeLifetime() {
        return spikeLifetime * (20 * 50L);
    }
}

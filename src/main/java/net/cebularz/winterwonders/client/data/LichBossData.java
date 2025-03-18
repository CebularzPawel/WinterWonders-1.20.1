package net.cebularz.winterwonders.client.data;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LichBossData {
    private static final Map<Integer, Double> lichHealthMap = new ConcurrentHashMap<>();

    public static double getBossHealth(int entityId) {
        return lichHealthMap.getOrDefault(entityId, 0.0);
    }

    public static void setBossHealth(int entityId, double health) {
        if (health <= 0.0) {
            lichHealthMap.remove(entityId);
        } else {
            lichHealthMap.put(entityId, Math.min(health, 1.0));
        }
    }

    public static void removeFarAwayBosses() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) return;

        Level level = minecraft.level;
        lichHealthMap.entrySet().removeIf(entry -> {
            int entityId = entry.getKey();
            LivingEntity boss = (LivingEntity) level.getEntity(entityId);
            double renderDistance = Minecraft.getInstance().options.renderDistance().get() * 16;
            return boss == null || boss.distanceToSqr(minecraft.player) > (renderDistance * renderDistance);
        });
    }

    public static Map<Integer, Double> getAllActiveBosses() {
        return lichHealthMap;
    }
}

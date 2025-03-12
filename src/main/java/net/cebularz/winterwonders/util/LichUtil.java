package net.cebularz.winterwonders.util;

import net.cebularz.winterwonders.entity.custom.LichEntity;
import net.cebularz.winterwonders.entity.custom.RevenantEntity;
import net.cebularz.winterwonders.init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;

public class LichUtil
{
    private static final double terrainAttackRange = 16.0D;
    public static boolean isTargetedPlayerInRange(LichEntity entity)
    {
        if(entity.getTarget() instanceof Player player)
        {
            List<Player> players = entity.level().getNearbyPlayers(TargetingConditions.forCombat(),player,entity.getBoundingBox().inflate(16D));
            return players.stream().anyMatch(targeted -> targeted == player);
        }
        return false;
    }

    public static boolean isGroundValidForTerrainAttack (LichEntity entity) {
        BlockPos lichPos = entity.blockPosition();
        Level level = entity.level();
        int groundY = lichPos.getY() - 1;

        for (double x = -terrainAttackRange; x <= terrainAttackRange; x++) {
            for (double z = -terrainAttackRange; z <= terrainAttackRange; z++) {
                BlockPos checkPos = new BlockPos((int) (lichPos.getX() + x), groundY, (int) (lichPos.getZ() + z));
                if (level.getBlockState(checkPos).isSolid()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void summonMobsForProtection(LichEntity entity) {
        ServerLevel level = (ServerLevel) entity.level();
        RandomSource random = entity.getRandom();

        int mobCount = 2 + random.nextInt(10);
        for (int i = 0; i < mobCount; i++) {
            try {
                RevenantEntity protector = ModEntities.REVENANT.get().create(level);
                if(protector==null)return;
                double offsetX = (random.nextDouble() - 0.5) * 4;
                double offsetZ = (random.nextDouble() - 0.5) * 4;
                protector.setPos(
                        entity.getX() + offsetX,
                        entity.getY(),
                        entity.getZ() + offsetZ
                );

                level.addFreshEntity(protector);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static double getTerrainAttackRange() {
        return terrainAttackRange;
    }
}

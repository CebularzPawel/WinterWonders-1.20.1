package net.cebularz.winterwonders.client.renderer;

import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.client.data.LichBossData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.Map;

public class LichBossBar {
    private static final ResourceLocation LICH_BOSS_BAR =
            new ResourceLocation(WinterWonders.MOD_ID, "textures/gui/lich_boss_bar.png");

    public static void render(GuiGraphics guiGraphics, int x, int y, Minecraft minecraft) {
        if (minecraft.player == null) return;

        Map<Integer, Double> activeBosses = LichBossData.getAllActiveBosses();
        if (activeBosses.isEmpty()) return;

        int barWidth = 192;
        int barHeight = 20;
        int barSpacing = 25;

        int index = 0;
        for (Map.Entry<Integer, Double> entry : activeBosses.entrySet()) {
            int entityId = entry.getKey();
            double bossHealth = entry.getValue();

            if (bossHealth <= 0.0F) continue;

            int yOffset = y + (index * barSpacing);
            int bossHealthWidth = getBossHealthWidth(bossHealth, barWidth);

            if (bossHealth > 0.5F) {
                guiGraphics.blit(LICH_BOSS_BAR, x, yOffset, 0, 0, barWidth, barHeight, barWidth, barHeight * 3);
            } else {
                guiGraphics.blit(LICH_BOSS_BAR, x, yOffset, 0, 20, barWidth, barHeight, barWidth, barHeight * 3);
            }

            if (bossHealthWidth > 0) {
                guiGraphics.blit(LICH_BOSS_BAR, x + 6, yOffset, 6, 40, bossHealthWidth, barHeight, barWidth, barHeight * 3);
            }

            Component displayText = getBossDisplayName();
            int titleWidth = minecraft.font.width(displayText);
            guiGraphics.drawString(minecraft.font, displayText, x + (barWidth / 2) - (titleWidth / 2), yOffset - 4,0xFFFFFF, true);

            index++;
        }
    }

    private static int getBossHealthWidth(double health, int barWidth) {
        return Mth.ceil(health * (barWidth - (6 * 2)));
    }

    private static Component getBossDisplayName() {
        return Component.translatable("boss.winterwonders.lich");
    }
}

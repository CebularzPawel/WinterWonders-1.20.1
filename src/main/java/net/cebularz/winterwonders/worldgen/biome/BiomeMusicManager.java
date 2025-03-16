package net.cebularz.winterwonders.worldgen.biome;

import net.cebularz.winterwonders.init.ModSongs;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BiomeMusicManager {
    private final Map<UUID, ResourceKey<Biome>> playerBiomes = new HashMap<>();
    private final Map<ResourceKey<Biome>, SoundEvent> biomeSounds = new HashMap<>();
    private int checkInterval = 20;

    public BiomeMusicManager() {
        biomeSounds.put(WinterFrostBiome.WINTER_FROST, ModSongs.BIOME_MUSIC.get());
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        if (event.getServer().getTickCount() % checkInterval != 0) return;

        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            checkAndUpdatePlayerBiome(player);
        }
    }

    private void checkAndUpdatePlayerBiome(ServerPlayer player) {
        ResourceKey<Biome> currentBiome = player.level().getBiome(player.blockPosition()).unwrapKey().orElse(null);
        if (currentBiome == null) return;

        UUID playerId = player.getUUID();

        if (!currentBiome.equals(playerBiomes.get(playerId))) {
            playerBiomes.put(playerId, currentBiome);

            if (biomeSounds.containsKey(currentBiome)) {
                player.connection.send(new ClientboundStopSoundPacket(null, SoundSource.MUSIC));

                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        biomeSounds.get(currentBiome), SoundSource.MUSIC,
                        1.0F, 1.0F);
            }
        }
    }
}
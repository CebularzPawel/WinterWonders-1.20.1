package net.cebularz.winterwonders.network;

import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.network.packets.IceSpikeVisualS2C;
import net.cebularz.winterwonders.network.packets.LichBossDataS2C;
import net.cebularz.winterwonders.network.packets.ThrowingSpearAttackC2S;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.turtleboi.turtlecore.network.packet.effects.FrozenDataC2S;
import net.turtleboi.turtlecore.network.packet.effects.FrozenDataS2C;
import net.turtleboi.turtlecore.network.packet.util.SendParticlesS2C;

public class ModNetworking {
    private static SimpleChannel INSTANCE;
    private static int packetId = 0;
    private static int id() {
        return packetId++;
    }
    public static void register () {
        SimpleChannel channel = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(WinterWonders.MOD_ID, "networking"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(version -> true)
                .serverAcceptedVersions(version -> true)
                .simpleChannel();

        INSTANCE = channel;

        channel.messageBuilder(LichBossDataS2C.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(LichBossDataS2C::new)
                .encoder(LichBossDataS2C::toBytes)
                .consumerMainThread(LichBossDataS2C::handle)
                .add();

        channel.messageBuilder(IceSpikeVisualS2C.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(IceSpikeVisualS2C::new)
                .encoder(IceSpikeVisualS2C::toBytes)
                .consumerMainThread(IceSpikeVisualS2C::handle)
                .add();

        channel.messageBuilder(ThrowingSpearAttackC2S.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(ThrowingSpearAttackC2S::new).encoder(ThrowingSpearAttackC2S::toBytes)
                .consumerMainThread(ThrowingSpearAttackC2S::handle).add();
    }

    public static <MSG> void sendToPlayer (MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <MSG> void sendToPlayers(MSG message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }

    public static <MSG> void sendNear(MSG message, LivingEntity livingEntity) {
        if (!(livingEntity.level() instanceof ServerLevel level)) return;
        double x = livingEntity.getX();
        double y = livingEntity.getY();
        double z = livingEntity.getZ();
        double range = level.getServer().getPlayerList().getViewDistance() * 16D;
        INSTANCE.send(PacketDistributor.NEAR.with(() ->
                new PacketDistributor.TargetPoint(x, y, z, range, livingEntity.level().dimension())),
                message);
    }

    public static <MSG> void sendToServer (MSG message) {
        INSTANCE.sendToServer(message);
    }
}

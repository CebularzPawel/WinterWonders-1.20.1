package net.cebularz.winterwonders.network;

import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.network.packets.IceSpikeVisualS2C;
import net.cebularz.winterwonders.network.packets.LichBossDataS2C;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
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
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(WinterWonders.MOD_ID, "networking"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(LichBossDataS2C.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(LichBossDataS2C::new)
                .encoder(LichBossDataS2C::toBytes)
                .consumerMainThread(LichBossDataS2C::handle)
                .add();

        net.messageBuilder(IceSpikeVisualS2C.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(IceSpikeVisualS2C::new)
                .encoder(IceSpikeVisualS2C::toBytes)
                .consumerMainThread(IceSpikeVisualS2C::handle)
                .add();
    }

    public static <MSG> void sendToPlayer (MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <MSG> void sendToPlayers(MSG message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }

    public static <MSG> void sendNear(MSG message, LivingEntity livingEntity) {
        double x = livingEntity.getX();
        double y = livingEntity.getY();
        double z = livingEntity.getZ();
        double r2 = Minecraft.getInstance().options.renderDistance().get() * 16;
        INSTANCE.send(PacketDistributor.NEAR.with(() ->
                new PacketDistributor.TargetPoint(x, y, z, r2, livingEntity.level().dimension())),
                message);
    }

    public static <MSG> void sendToServer (MSG message) {
        INSTANCE.sendToServer(message);
    }
}

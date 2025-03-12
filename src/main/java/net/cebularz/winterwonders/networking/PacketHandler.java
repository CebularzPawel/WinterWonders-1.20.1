package net.cebularz.winterwonders.networking;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.lwjgl.system.windows.MSG;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class PacketHandler
{
    public static final SimpleChannel NETWORK_WRAPPER;
    private static final String PROTOCOL_VERSION = Integer.toString(1);

    public static int packetsRegistered = 0;

    public static void setup()
    {
        NETWORK_WRAPPER.registerMessage(packetsRegistered++, ClientboundBlizzardPacket.class,ClientboundBlizzardPacket::encode,ClientboundBlizzardPacket::new,ClientboundBlizzardPacket::handle);
    }

    static {
        NetworkRegistry.ChannelBuilder channel = NetworkRegistry.ChannelBuilder.named(new ResourceLocation("winterwonders", "main_channel"));
        String version = PROTOCOL_VERSION;
        version.getClass();
        channel = channel.clientAcceptedVersions(version::equals);
        version = PROTOCOL_VERSION;
        version.getClass();
        NETWORK_WRAPPER = channel.serverAcceptedVersions(version::equals).networkProtocolVersion(() -> PROTOCOL_VERSION).simpleChannel();
    }
    public static <MSG> BiConsumer<MSG , Supplier<NetworkEvent.Context>> handle(BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer) {
        return (msg, ctx) -> {
            ctx.get().enqueueWork(() ->
                    DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER, () -> () -> messageConsumer.accept(msg, ctx))
            );
            ctx.get().enqueueWork(() ->
                    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> messageConsumer.accept(msg, ctx))
            );
            ctx.get().setPacketHandled(true);
        };
    }


    public static BiConsumer<MSG, Supplier<NetworkEvent.Context>> handleServer(BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer) {
        return (msg, ctx) -> {
            ctx.get().enqueueWork(() ->
                    DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER, () -> () -> messageConsumer.accept(msg, ctx))
            );
            ctx.get().setPacketHandled(true);
        };
    }

    public static BiConsumer<MSG, Supplier<NetworkEvent.Context>> handleClient(BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer) {
        return (msg, ctx) -> {
            ctx.get().enqueueWork(() ->
                    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> messageConsumer.accept(msg, ctx))
            );
            ctx.get().setPacketHandled(true);
        };
    }

    public static <MSG> void sendMSGToServer(MSG message) {
        NETWORK_WRAPPER.sendToServer(message);
    }

    public static <MSG> void sendMSGToAll(MSG message) {
        for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            sendNonLocal(message, player);
        }
    }
    public static <MSG> void sendNonLocal(MSG msg, ServerPlayer player) {
        NETWORK_WRAPPER.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }
    public static <MSG> void sendMSGToPlayer(MSG message, ServerPlayer player) {
        NETWORK_WRAPPER.sendTo(message, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }
}

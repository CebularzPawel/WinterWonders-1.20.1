package net.cebularz.winterwonders.network.packets;

import net.cebularz.winterwonders.network.ModNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class FrozenDataC2S {
    private final int entityId;
    private final boolean frozen;

    public FrozenDataC2S(int entityId, boolean frozen) {
        this.entityId = entityId;
        this.frozen = frozen;
    }

    public FrozenDataC2S(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
        this.frozen = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeBoolean(frozen);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                ModNetworking.sendToAllPlayers(new FrozenDataS2C(entityId, frozen));
            }
        });
        context.setPacketHandled(true);
        return true;
    }
}

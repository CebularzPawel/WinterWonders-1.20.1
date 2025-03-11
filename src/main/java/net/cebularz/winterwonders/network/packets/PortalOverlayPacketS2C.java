package net.cebularz.winterwonders.network.packets;

import net.cebularz.winterwonders.client.data.FrozenStatusCache;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PortalOverlayPacketS2C {
    //private final int entityId;
    //private final boolean frozen;

    public PortalOverlayPacketS2C(int entityId, boolean frozen) {
        //this.entityId = entityId;
        //this.frozen = frozen;
    }

    public PortalOverlayPacketS2C(FriendlyByteBuf buf) {
        //this.portalAlpha = buf.readFloat();
    }

    public void toBytes(FriendlyByteBuf buf) {
        //buf.writeFloat(portalAlpha);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            //FrozenStatusCache.setStatus(entityId, frozen);
        });
        context.setPacketHandled(true);
        return true;
    }
}

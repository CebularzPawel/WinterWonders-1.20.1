package net.turtleboi.winterwonders.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.turtleboi.winterwonders.client.PlayerClientData;

import java.util.function.Supplier;

public class CameraShakeS2C {
    private final float intensity;
    private final int duration;

    public CameraShakeS2C(float intensity, int duration) {
        this.intensity = intensity;
        this.duration = duration;
    }

    public CameraShakeS2C(FriendlyByteBuf buf) {
        this.intensity = buf.readFloat();
        this.duration = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeFloat(intensity);
        buf.writeInt(duration);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {

        });
        context.setPacketHandled(true);
        return true;
    }
}

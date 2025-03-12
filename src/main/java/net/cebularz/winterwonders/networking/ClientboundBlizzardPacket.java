package net.cebularz.winterwonders.networking;

import net.cebularz.winterwonders.client.shaders.blizzard.BlizzardRenderer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundBlizzardPacket
{
    private final double x;
    private final double y;
    private final double z;
    private final int duration;
    private final float intensity;

    public ClientboundBlizzardPacket(double x, double y, double z, int duration, float intensity) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.duration = duration;
        this.intensity = intensity;
    }

    public ClientboundBlizzardPacket(FriendlyByteBuf buf)
    {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.duration = buf.readInt();
        this.intensity = buf.readFloat();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeInt(duration);
        buf.writeFloat(intensity);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                BlizzardRenderer.getInstance().startBlizzard(
                        new Vec3(x, y, z), duration, intensity
                );
            });
        });
        return true;
    }
}

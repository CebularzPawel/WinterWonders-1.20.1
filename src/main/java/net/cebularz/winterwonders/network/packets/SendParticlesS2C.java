package net.cebularz.winterwonders.network.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class SendParticlesS2C {
    private final double x, y, z;
    private final double dx, dy, dz;
    private final ParticleOptions particleType;

    public SendParticlesS2C(ParticleOptions particleType, double x, double y, double z, double dx, double dy, double dz) {
        this.particleType = particleType;
        this.x = x;
        this.y = y;
        this.z = z;
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
    }

    public SendParticlesS2C(FriendlyByteBuf buf) {
        ParticleType<ParticleOptions> type = buf.readRegistryId();
        this.particleType = type.getDeserializer().fromNetwork(type, buf);
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.dx = buf.readDouble();
        this.dy = buf.readDouble();
        this.dz = buf.readDouble();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeRegistryId(ForgeRegistries.PARTICLE_TYPES, this.particleType.getType());
        this.particleType.writeToNetwork(buf);
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeDouble(dx);
        buf.writeDouble(dy);
        buf.writeDouble(dz);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            //System.out.println(Component.literal("Level: " + Minecraft.getInstance().level)); //debug code
            if (Minecraft.getInstance().level != null) {
                Minecraft.getInstance().level.addParticle(
                        particleType,
                        x,
                        y,
                        z,
                        dx,
                        dy,
                        dz);
                //System.out.println(Component.literal("Sending particles to the client at: " + x + ", " + y + ", " + z)); //debug code
            }
        });
        context.setPacketHandled(true);
        return true;
    }
}

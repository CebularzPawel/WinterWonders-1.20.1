package net.cebularz.winterwonders.network.packets;

import net.cebularz.winterwonders.client.data.FrozenStatusCache;
import net.cebularz.winterwonders.client.data.LichBossData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class LichBossDataS2C {
    private final int entityId;
    private final double health;

    public LichBossDataS2C(int entityId, double health) {
        this.entityId = entityId;
        this.health = health;
    }

    public LichBossDataS2C(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
        this.health = buf.readDouble();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeDouble(health);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().level != null) {
                LivingEntity entity = (LivingEntity) Minecraft.getInstance().level.getEntity(entityId);
                if (entity != null) {
                    LichBossData.setBossHealth(entityId, health);
                }
            }
        });
        context.setPacketHandled(true);
        return true;
    }
}

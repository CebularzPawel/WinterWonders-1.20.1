package net.cebularz.winterwonders.network.packets;

import net.cebularz.winterwonders.client.data.FrozenStatusCache;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class FrozenDataS2C {
    private final int entityId;
    private final boolean frozen;

    public FrozenDataS2C(int entityId, boolean frozen) {
        this.entityId = entityId;
        this.frozen = frozen;
    }

    public FrozenDataS2C(FriendlyByteBuf buf) {
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
            if (Minecraft.getInstance().level != null) {
                LivingEntity entity = (LivingEntity) Minecraft.getInstance().level.getEntity(entityId);
                if (entity != null){
                    FrozenStatusCache.setStatus(entityId, frozen);
                }
            }
        });
        context.setPacketHandled(true);
        return true;
    }
}

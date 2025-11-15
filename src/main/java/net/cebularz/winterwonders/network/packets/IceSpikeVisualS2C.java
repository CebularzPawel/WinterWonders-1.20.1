package net.cebularz.winterwonders.network.packets;

import net.cebularz.winterwonders.client.data.LichBossData;
import net.cebularz.winterwonders.client.spell.SpellVisualManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class IceSpikeVisualS2C {
    private final BlockPos blockPos;
    private final float yawDegrees;

    public IceSpikeVisualS2C(BlockPos blockPos, float yawDegrees) {
        this.blockPos = blockPos;
        this.yawDegrees = yawDegrees;
    }

    public IceSpikeVisualS2C(FriendlyByteBuf buf) {
        this.blockPos = buf.readBlockPos();
        this.yawDegrees = buf.readFloat();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(blockPos);
        buf.writeFloat(yawDegrees);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().level != null) {
                SpellVisualManager.addSpike(blockPos, yawDegrees);
            }
        });
        context.setPacketHandled(true);
        return true;
    }
}

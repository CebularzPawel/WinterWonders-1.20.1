package net.cebularz.winterwonders.network.packets;

import net.cebularz.winterwonders.item.custom.ThrowingSpearItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ThrowingSpearAttackC2S() {
    public ThrowingSpearAttackC2S(FriendlyByteBuf buffer) {
        this();
    }

    public void toBytes(FriendlyByteBuf buffer) {}

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        var player = context.getSender();
        if (player != null && player.getMainHandItem().getItem() instanceof ThrowingSpearItem spear) {
            if (spear.canJab(player)) {
                spear.jab(player, EquipmentSlot.MAINHAND);
            }
        }
        context.setPacketHandled(true);
    }
}

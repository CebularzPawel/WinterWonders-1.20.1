package net.cebularz.winterwonders.mixin;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.cebularz.winterwonders.item.custom.ThrowingSpearItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemInHandRenderer.class)
public abstract class ThrowingSpearEquipMixin {
    @Redirect(method = "tick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F"))
    private float throwingSpearEquip(LocalPlayer player, float partial) {
        if (player.getMainHandItem().getItem() instanceof ThrowingSpearItem) {
            return 1;
        } else {
            return player.getAttackStrengthScale(partial);
        }
    }
}

package net.cebularz.winterwonders.mixin;

import net.cebularz.winterwonders.item.custom.ThrowingSpearItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class ThrowingSpearMobMixin {
    @Inject(method = "doHurtTarget", at = @At("HEAD"), cancellable = true)
    private void throwingSpearAttack(Entity target, CallbackInfoReturnable<Boolean> result) {
        Mob mob = (Mob) (Object) this;
        if (mob.getMainHandItem().getItem() instanceof ThrowingSpearItem spear) {
            spear.jab(mob, net.minecraft.world.entity.EquipmentSlot.MAINHAND);
            result.setReturnValue(true);
        }
    }
}

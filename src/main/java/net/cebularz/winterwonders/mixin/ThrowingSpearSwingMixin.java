package net.cebularz.winterwonders.mixin;

import net.cebularz.winterwonders.item.custom.ThrowingSpearItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class ThrowingSpearSwingMixin {
    @Unique private static final TagKey<DamageType> NO_KNOCKBACK = TagKey.create(Registries.DAMAGE_TYPE,
            new ResourceLocation("minecraft", "no_knockback"));

    @Inject(method = "getCurrentSwingDuration", at = @At("HEAD"), cancellable = true)
    private void throwingSpearSwing(CallbackInfoReturnable<Integer> result) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.swingingArm != null && entity.getItemInHand(entity.swingingArm).getItem() instanceof ThrowingSpearItem spear) {
            result.setReturnValue(spear.getSwingTicks());
        }
    }

    @Redirect(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;knockback(DDD)V"))
    private void damageKnockback(LivingEntity entity, double strength, double x, double z, DamageSource source, float amount) {
        if (!source.is(NO_KNOCKBACK)) {
            entity.knockback(strength, x, z);
        }
    }

}

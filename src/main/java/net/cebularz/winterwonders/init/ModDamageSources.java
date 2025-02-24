package net.cebularz.winterwonders.init;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.cebularz.winterwonders.WinterWonders;
import org.jetbrains.annotations.Nullable;

public class ModDamageSources {
    public static final ResourceLocation COLD_DAMAGE_ID = new ResourceLocation(WinterWonders.MOD_ID, "cold");

    public static DamageSource coldDamage(Level level, @Nullable Entity attacker, @Nullable Entity directCause) {
        Holder<DamageType> holder = coldDamageType(level);
        return new DamageSource(holder, attacker, directCause, attacker != null ? attacker.position() : null);
    }

    public static Holder<DamageType> coldDamageType(Level level) {
        return level.registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, COLD_DAMAGE_ID));
    }

    public static void hurtWithColdDamage(LivingEntity target, @Nullable Entity attacker, float percent) {
        float damageAmount = target.getHealth() * percent;
        double resistanceModifier = 0;
        if (target.getAttribute(ModAttributes.COLD_RESISTANCE.get()) != null &&
                target.getAttributeValue(ModAttributes.COLD_RESISTANCE.get()) > 0){
            resistanceModifier = target.getAttributeValue(ModAttributes.COLD_RESISTANCE.get());
        }

        DamageSource source = coldDamage(target.level(), attacker, null);
        target.hurt(source, Math.max(1.0F, (float) (damageAmount * (1 - resistanceModifier))));

    }
}

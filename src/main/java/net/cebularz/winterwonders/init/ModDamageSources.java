package net.cebularz.winterwonders.init;

import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.level.Level;
import net.cebularz.winterwonders.WinterWonders;
import org.jetbrains.annotations.Nullable;

public class ModDamageSources {
    public static final ResourceLocation FROST_DAMAGE_ID = new ResourceLocation(WinterWonders.MOD_ID, "frost");

    public static DamageSource frostDamage(Level level, @Nullable Entity attacker, @Nullable Entity directCause) {
        Holder<DamageType> holder = frostDamageType(level);
        return new DamageSource(holder, attacker, directCause, attacker != null ? attacker.position() : null);
    }

    public static Holder<DamageType> frostDamageType(Level level) {
        return level.registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, FROST_DAMAGE_ID));
    }

    public static void hurtWithFrostDamage(LivingEntity target, @Nullable Entity attacker, float percent) {
        float damageAmount = target.getMaxHealth() * percent;
        double resistanceModifier = 1.0;

        AttributeInstance damageResistanceAttribute = target.getAttribute(ModAttributes.FROST_RESISTANCE.get());
        if (damageResistanceAttribute != null && damageResistanceAttribute.getValue() > 0) {
            resistanceModifier = (100 - damageResistanceAttribute.getValue()) / 100.0;
            //System.out.println("Current Frost Resistance: " + damageResistanceAttribute.getValue() + "%");
            //System.out.println("Taking " + (resistanceModifier * 100) + "% of the original damage");
        }

        float reducedDamage = Math.max(1, (float) (damageAmount * resistanceModifier));

        //System.out.println("Reduced damage from " + damageAmount + " to " + reducedDamage);

        if (target.getServer() != null) {
            spawnSnowParticles(target.getServer().getLevel(target.level().dimension()), target.getX(), target.getY(), target.getZ());
        }

        DamageSource damageSource = frostDamage(target.level(), attacker, null);
        target.hurt(damageSource, reducedDamage);
    }

    private static void spawnSnowParticles(ServerLevel serverLevel, double x, double y, double z) {
        int count = 10;
        for (int i = 0; i < count; i++) {
            double offsetX = (serverLevel.random.nextDouble() - 0.5);
            double offsetY = serverLevel.random.nextDouble() * 1.95;
            double offsetZ = (serverLevel.random.nextDouble() - 0.5);

            serverLevel.sendParticles(
                    ParticleTypes.SNOWFLAKE,
                    x + offsetX,
                    y + offsetY,
                    z + offsetZ,
                    1,
                    0, 0, 0,
                    0.01
            );
        }
    }
}

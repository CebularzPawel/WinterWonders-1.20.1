package net.cebularz.winterwonders.entity.custom.projectile;

import net.cebularz.winterwonders.init.ModDamageSources;
import net.cebularz.winterwonders.effect.ModEffects;
import net.cebularz.winterwonders.entity.ModEntities;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class ChillingSnowballEntity extends Snowball {
    private final int chillAmplifier;
    private final boolean dealsDamage;
    private final float damagePercent;
    public ChillingSnowballEntity(EntityType<? extends Snowball> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.chillAmplifier = 0;
        this.dealsDamage = false;
        this.damagePercent = 0.0f;
    }

    public ChillingSnowballEntity(Level pLevel){
        super(ModEntities.CHILLING_SNOWBALL.get(), pLevel);
        this.chillAmplifier = 0;
        this.dealsDamage = false;
        this.damagePercent = 0.0f;
    }

    public ChillingSnowballEntity(Level pLevel, LivingEntity pLivingEntity) {
        super(pLevel, pLivingEntity);
        this.chillAmplifier = 0;
        this.dealsDamage = false;
        this.damagePercent = 0.0f;
    }

    public ChillingSnowballEntity(Level pLevel, LivingEntity pLivingEntity, int chillAmplifier, boolean dealsDamage, float damagePercent){
        super(pLevel, pLivingEntity);
        this.chillAmplifier = chillAmplifier;
        this.dealsDamage = dealsDamage;
        this.damagePercent = damagePercent;
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        Entity hitEntity = pResult.getEntity();
        Entity ownerEntity = getOwner();
        if (hitEntity != ownerEntity) {
            if (hitEntity instanceof LivingEntity livingEntity) {
                if (livingEntity.hasEffect(ModEffects.CHILLED.get())) {
                    int currentAmplifier = livingEntity.getEffect(ModEffects.CHILLED.get()).getAmplifier();
                    livingEntity.addEffect(new MobEffectInstance(ModEffects.CHILLED.get(), 400, currentAmplifier + this.chillAmplifier));
                } else {
                    livingEntity.addEffect(new MobEffectInstance(ModEffects.CHILLED.get(), 400, this.chillAmplifier));
                }

                if (this.dealsDamage) {
                    ModDamageSources.hurtWithFrostDamage(livingEntity, ownerEntity, this.damagePercent/100);
                }

                this.level().playSound(
                        this,
                        this.blockPosition(),
                        SoundEvents.PLAYER_HURT_FREEZE,
                        SoundSource.NEUTRAL,
                        0.5f,
                        0.4f / (level().getRandom().nextFloat() * 0.4f + 0.8f)
                );
            }
            discard();
        }
    }
}

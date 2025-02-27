package net.cebularz.winterwonders.entity.custom.projectile;

import net.cebularz.winterwonders.init.ModEffects;
import net.cebularz.winterwonders.init.ModEntities;
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
    public ChillingSnowballEntity(EntityType<? extends Snowball> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public ChillingSnowballEntity(Level pLevel){
        super(ModEntities.CHILLING_SNOWBALL.get(), pLevel);
    }

    public ChillingSnowballEntity(Level pLevel, LivingEntity pLivingEntity){
        super(pLevel, pLivingEntity);
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        Entity hitEntity = pResult.getEntity();
        Entity ownerEntity = getOwner();
        if (hitEntity != ownerEntity) {
            if (hitEntity instanceof LivingEntity livingEntity) {
                if (livingEntity.hasEffect(ModEffects.CHILLED.get())) {
                    livingEntity.addEffect(new MobEffectInstance(ModEffects.CHILLED.get(), 400,
                            livingEntity.getEffect(ModEffects.CHILLED.get()).getAmplifier() + 1));
                } else {
                    livingEntity.addEffect(new MobEffectInstance(ModEffects.CHILLED.get(), 400, 0));
                }
            }

            this.level().playSound(
                    this,
                    this.blockPosition(),
                    SoundEvents.PLAYER_HURT_FREEZE,
                    SoundSource.NEUTRAL,
                    0.5f,
                    0.4f / (level().getRandom().nextFloat() * 0.4f + 0.8f)
            );
            discard();
        }
    }
}

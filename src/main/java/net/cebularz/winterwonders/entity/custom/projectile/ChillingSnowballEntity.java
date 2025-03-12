package net.cebularz.winterwonders.entity.custom.projectile;

import net.cebularz.winterwonders.init.ModEffects;
import net.cebularz.winterwonders.init.ModEntities;
import net.cebularz.winterwonders.util.IStackProjectile;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class ChillingSnowballEntity extends Snowball implements IStackProjectile {
    private int stackValue = 0;
    private static final int STACK_THRESHOLD = 50;
    private static final int STACK_INCREMENT = 25;
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


                if (stackValue < 100) {
                    stackValue += STACK_INCREMENT;
                }

                if (stackValue >= STACK_THRESHOLD) {
                    handleStackAction(level(), livingEntity);
                    stackValue = 0;
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

    @Override
    public int getStackValue() {
        return stackValue;
    }

    @Override
    public void setStackValue(int value) {
        stackValue = value;
    }

    @Override
    public boolean handleStackAction(Level level, LivingEntity wompWomp) {
        if(level.isClientSide) return false;

        wompWomp.addEffect(new MobEffectInstance(ModEffects.CHILLED.get(),1000));
        return true;
    }

    public static void setBaseDamageValeu(int value){

    }
}

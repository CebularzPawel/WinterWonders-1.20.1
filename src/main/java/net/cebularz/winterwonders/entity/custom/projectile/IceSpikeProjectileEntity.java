package net.cebularz.winterwonders.entity.custom.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.cebularz.winterwonders.init.ModDamageSources;
import net.cebularz.winterwonders.init.ModEffects;
import net.cebularz.winterwonders.init.ModEntities;

public class IceSpikeProjectileEntity extends AbstractArrow {
    public IceSpikeProjectileEntity(EntityType<? extends AbstractArrow> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public IceSpikeProjectileEntity(Level pLevel){
        super(ModEntities.ICE_SPIKE.get(), pLevel);
    }

    public IceSpikeProjectileEntity(Level pLevel, LivingEntity pLivingEntity){
        super(ModEntities.ICE_SPIKE.get(), pLivingEntity, pLevel);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide){
            for(int particles = 0; particles < 1; ++particles) {
                this.level().addParticle(
                        ParticleTypes.SNOWFLAKE,
                        this.getRandomX(0.25F),
                        this.getY(),
                        this.getRandomZ(0.25F),
                        0.0F,
                        0.0F,
                        0.0F);
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        Entity hitEntity = pResult.getEntity();
        Entity ownerEntity = getOwner();
        if (hitEntity != ownerEntity) {
            if (hitEntity instanceof LivingEntity livingEntity) {
                if (livingEntity.hasEffect(ModEffects.CHILLED.get())) {
                    int chilledAmplifier = livingEntity.getEffect(ModEffects.CHILLED.get()).getAmplifier() + 1;
                    //System.out.println("Hit target with " + chilledAmplifier + "Chilled");
                    ModDamageSources.hurtWithFrostDamage(livingEntity, getOwner(), 0.05f * chilledAmplifier);
                } else {
                    ModDamageSources.hurtWithFrostDamage(livingEntity, getOwner(), 0.05f);
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

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        this.level().playSound(
                this,
                this.blockPosition(),
                SoundEvents.GLASS_BREAK,
                SoundSource.NEUTRAL,
                1f,
                1f
        );

        Vec3 vec3 = pResult.getLocation().subtract(this.getX(), this.getY(), this.getZ());
        this.setDeltaMovement(vec3);
        Vec3 vec31 = vec3.normalize().scale((double)0.05F);
        this.setPosRaw(this.getX() - vec31.x, this.getY() - vec31.y, this.getZ() - vec31.z);
        this.inGround = true;
        this.shakeTime = 7;
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

    @Override
    protected ItemStack getPickupItem() {
        return null;
    }
}

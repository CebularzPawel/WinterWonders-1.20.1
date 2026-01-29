package net.cebularz.winterwonders.entity.custom.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
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
import net.cebularz.winterwonders.entity.ModEntities;
import net.turtleboi.turtlecore.effect.CoreEffects;

public class IcicleProjectileEntity extends AbstractArrow {
    private final float damageAmount;
    private final boolean homing;
    private LivingEntity homingTarget;
    public IcicleProjectileEntity(EntityType<? extends AbstractArrow> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.damageAmount = 0.05f;
        this.homing = false;
    }

    public IcicleProjectileEntity(Level pLevel){
        super(ModEntities.ICICLE.get(), pLevel);
        this.damageAmount = 0.05f;
        this.homing = false;
    }

    public IcicleProjectileEntity(Level pLevel, LivingEntity pLivingEntity){
        super(ModEntities.ICICLE.get(), pLivingEntity, pLevel);
        this.damageAmount = 0.05f;
        this.homing = false;
    }

    public IcicleProjectileEntity(Level pLevel, LivingEntity pLivingEntity, float damageAmount, boolean homing) {
        super(ModEntities.ICICLE.get(), pLivingEntity, pLevel);
        this.damageAmount = damageAmount;
        this.homing = homing;
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

        if (homing && homingTarget != null && !homingTarget.isRemoved()) {
            Vec3 currentPos = this.position();
            Vec3 targetPos = homingTarget.position().add(0, homingTarget.getBbHeight() / 2.0, 0);
            Vec3 desiredDir = targetPos.subtract(currentPos).normalize();
            Vec3 currentMotion = this.getDeltaMovement();
            Vec3 homingMotion = currentMotion.scale(0.65).add(desiredDir.scale(0.35));
            this.setDeltaMovement(homingMotion.normalize().scale(currentMotion.length()));
            this.hurtMarked = true;
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        Entity hitEntity = pResult.getEntity();
        Entity ownerEntity = getOwner();

        if (hitEntity != ownerEntity) {
            if (hitEntity instanceof LivingEntity livingEntity) {
                int chilledLevel;
                float multiplier = 1.0f;
                MobEffectInstance chilled = livingEntity.getEffect(CoreEffects.CHILLED.get());
                if (chilled != null) {
                    chilledLevel = chilled.getAmplifier() + 1;
                    multiplier = chilledLevel;
                    //System.out.println("[Icicle] Target has CHILLED level=" + chilledLevel);
                }

                float finalDamage = damageAmount * multiplier;
                //System.out.println(
                //        "[Icicle] base=" + damageAmount +
                //                " mult=" + multiplier +
                //                " final=" + finalDamage +
                //                " target=" + livingEntity.getName().getString() +
                //                " owner=" + (ownerEntity != null ? ownerEntity.getName().getString() : "null")
                //);
                ModDamageSources.hurtWithFrostDamage(livingEntity, ownerEntity, finalDamage);
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
        Vec3 vec31 = vec3.normalize().scale(0.05F);
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

    public void setHomingTarget(LivingEntity target) {
        this.homingTarget = target;
    }
}

package net.cebularz.winterwonders.entity.custom.projectile;

import net.cebularz.winterwonders.entity.ModEntities;
import net.cebularz.winterwonders.mixin.ThrownTridentAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class ThrowingSpearProjectile extends ThrownTrident implements ItemSupplier {
    private static final EntityDataAccessor<ItemStack> ITEM = SynchedEntityData.defineId(ThrowingSpearProjectile.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Float> CHARGE = SynchedEntityData.defineId(ThrowingSpearProjectile.class, EntityDataSerializers.FLOAT);

    public ThrowingSpearProjectile(EntityType<? extends ThrowingSpearProjectile> type, Level level) {
        super(type, level);
    }

    public ThrowingSpearProjectile(Level level, LivingEntity owner, ItemStack stack) {
        this(ModEntities.THROWING_SPEAR.get(), level);
        ((ThrownTridentAccessor) this).setTridentItem(stack.copy());
        entityData.set(ITEM, stack.copy());
        entityData.set(ThrownTridentAccessor.loyaltyData(), (byte) EnchantmentHelper.getLoyalty(stack));
        setOwner(owner);
        setPos(owner.getX(), owner.getEyeY() - .1, owner.getZ());
        pickup = Pickup.ALLOWED;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(ITEM, ItemStack.EMPTY);
        entityData.define(CHARGE, 0F);
    }

    public void setThrowCharge(float charge) {
        entityData.set(CHARGE, Mth.clamp(charge, 0, 1));
    }

    @Override
    public ItemStack getItem() {
        return entityData.get(ITEM);
    }

    public boolean isRetrievable() { return (inGround || isNoPhysics()) && shakeTime <= 0 && pickup == Pickup.ALLOWED; }

    @Override
    protected boolean canHitEntity(Entity target) {
        Entity owner = getOwner();
        return super.canHitEntity(target) && (owner == null || !owner.isAlliedTo(target));
    }

    @Override
    public boolean isFoil() {
        return getItem().hasFoil();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity target = result.getEntity();
        Entity owner = getOwner();
        float damage = Mth.lerp(entityData.get(CHARGE), 3, 12);
        if (target instanceof LivingEntity living) {
            damage += EnchantmentHelper.getDamageBonus(getItem(), living.getMobType());
        }
        ((ThrownTridentAccessor) this).setDealtDamage(true);
        boolean hurt = target.hurt(damageSources().trident(this, owner == null ? this : owner), damage);
        if (!hurt || target.getType() != EntityType.ENDERMAN) {
            if (hurt && target instanceof LivingEntity living) {
                if (owner instanceof LivingEntity attacker) {
                    EnchantmentHelper.doPostHurtEffects(living, attacker);
                    EnchantmentHelper.doPostDamageEffects(attacker, living);
                }
                doPostHurtEffects(living);
            }
            setDeltaMovement(getDeltaMovement().multiply(.02, .2, .02));
            playSound(SoundEvents.TRIDENT_HIT, 1, 1);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("ThrowCharge", entityData.get(CHARGE));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(ITEM, ((ThrownTridentAccessor) this).getTridentItem().copy());
        setThrowCharge(tag.getFloat("ThrowCharge"));
    }
}

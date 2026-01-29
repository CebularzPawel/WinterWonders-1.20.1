package net.cebularz.winterwonders.entity.custom.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.turtleboi.turtlecore.effect.CoreEffects;

import javax.annotation.Nullable;
import java.util.UUID;

public class IceCubeEntity extends ThrowableProjectile {
    private int windupTicks = 15;
    private double homingSpeed = 0.55;
    private double hitRadius = 1.1;
    private int freezeTicks = 120;
    private boolean canAffectAllies = false;

    private boolean released = false;
    private int postReleaseWindup = 0;
    private int arcIndex = 0;
    private int arcTotal = 1;

    private int ageTicks = 0;
    private @Nullable UUID targetUuid;
    private @Nullable LivingEntity cachedTarget;

    private static final EntityDataAccessor<Float> CUBE_HEALTH =
            SynchedEntityData.defineId(IceCubeEntity.class, EntityDataSerializers.FLOAT);

    private float maxCubeHealth = 10.0F;

    public IceCubeEntity(EntityType<? extends ThrowableProjectile> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public IceCubeEntity(EntityType<? extends ThrowableProjectile> type, Level level, LivingEntity owner) {
        this(type, level);
        this.setOwner(owner);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(CUBE_HEALTH, 10.0F);
    }

    public void configureStats(float hp, int windupTicks, double homingSpeed, double hitRadius, int freezeTicks, boolean canAffectAllies) {
        this.maxCubeHealth = Math.max(1.0F, hp);
        this.entityData.set(CUBE_HEALTH, this.maxCubeHealth);

        this.windupTicks = Math.max(0, windupTicks);
        this.homingSpeed = Math.max(0.05, homingSpeed);
        this.hitRadius = Math.max(0.5, hitRadius);
        this.freezeTicks = Math.max(20, freezeTicks);
        this.canAffectAllies = canAffectAllies;
    }

    public void setTarget(@Nullable LivingEntity target) {
        this.cachedTarget = target;
        this.targetUuid = (target != null) ? target.getUUID() : null;
    }

    @Nullable
    private LivingEntity getTargetResolved() {
        if (cachedTarget != null && cachedTarget.isAlive() && !cachedTarget.isRemoved()) return cachedTarget;
        cachedTarget = null;

        if (targetUuid == null) return null;
        if (!(level() instanceof ServerLevel serverLevel)) return null;

        Entity targetEntity = serverLevel.getEntity(targetUuid);
        if (targetEntity instanceof LivingEntity livingEntity && livingEntity.isAlive() && !livingEntity.isRemoved()) {
            cachedTarget = livingEntity;
            return livingEntity;
        }
        return null;
    }

    @Override
    protected float getGravity() {
        return 0.0F;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public float getPickRadius() {
        return 1.0F;
    }

    @Override
    public void tick() {
        super.tick();
        ageTicks++;

        this.setNoGravity(true);
        this.noPhysics = true;
        if (level().isClientSide) {
            for (int i = 0; i < 2; i++) {
                level().addParticle(
                        ParticleTypes.SNOWFLAKE,
                        getX() + (random.nextDouble() - 0.5) * 0.4,
                        getY() + 0.5 + ((random.nextDouble() - 0.5) * 0.4),
                        getZ() + (random.nextDouble() - 0.5) * 0.4,
                        0, 0, 0
                );
            }
            return;
        }

        LivingEntity owner = (getOwner() instanceof LivingEntity le) ? le : null;
        LivingEntity target = getTargetResolved();

        if (owner == null || !owner.isAlive() || target == null || !target.isAlive()) {
            discard();
            return;
        }

        if (!canAffectAllies && target.isAlliedTo(owner)) {
            discard();
            return;
        }

        final double hoverLerp = 0.35;
        final double homingLerp = 0.25;
        final double maxStep = homingSpeed;
        int chaseTicks = Math.max(0, ageTicks - windupTicks);
        if (!released) {
            Vec3 hoverPos = computeArcHoverPos(owner, arcIndex, arcTotal);
            Vec3 cur = position();
            Vec3 desiredStep = hoverPos.subtract(cur).scale(hoverLerp);

            double length = desiredStep.length();
            if (length > maxStep) desiredStep = desiredStep.scale(maxStep / length);

            setDeltaMovement(desiredStep);
            this.hasImpulse = true;
            move(MoverType.SELF, desiredStep);
            return;
        }

        if (postReleaseWindup-- > 0) {
            return;
        }

        Vec3 targetPos = target.position().add(0, target.getBbHeight() * 0.5, 0);
        Vec3 toTarget = targetPos.subtract(position());

        if (this.getBoundingBox().intersects(target.getBoundingBox())) {
            impactTarget(target);
            return;
        }

        double baseSpeed = homingSpeed;
        double rampRate = 0.01;
        double maxSpeed = homingSpeed * 4.0;

        double rampedSpeed = baseSpeed * Math.exp(rampRate * chaseTicks);
        double stepSpeed = Math.min(rampedSpeed, maxSpeed);
        Vec3 desiredSpeed = toTarget.normalize().scale(stepSpeed);
        Vec3 currentSpeed = getDeltaMovement();
        Vec3 newSpeed = currentSpeed.add(desiredSpeed.subtract(currentSpeed).scale(homingLerp));

        double newLength = newSpeed.length();
        if (newLength > maxSpeed) {
            newSpeed = newSpeed.scale(maxSpeed / newLength);
        }

        setDeltaMovement(newSpeed);
        this.hasImpulse = true;
        move(MoverType.SELF, newSpeed);

        if (ageTicks > windupTicks + 300) {
            breakCube();
        }
    }

    private Vec3 computeArcHoverPos(LivingEntity owner, int index, int total) {
        Vec3 basePos = owner.position().add(0.0, owner.getBbHeight() + 1.25, 0.0);
        if (total <= 1) {
            return basePos;
        }

        double radius = 2.0;
        double arcDegrees = 145.0;

        double totalCubes = index / (double)(total - 1);
        double span = Math.toRadians(arcDegrees);
        double start = (Math.PI - span) * 0.5;
        double angle = start + span * totalCubes;

        double localX = Math.cos(angle) * radius;
        double localY = Math.sin(angle) * radius;

        double yawRad = Math.toRadians(owner.getYRot());
        double cos = Math.cos(yawRad);
        double sin = Math.sin(yawRad);

        double levelX = localX * cos;
        double levelZ = localX * sin;

        return basePos.add(levelX, localY, levelZ);
    }

    public void setArcSlot(int index, int total) {
        this.arcIndex = Math.max(0, index);
        this.arcTotal = Math.max(1, total);
    }

    public void releaseNow() {
        this.released = true;
        this.postReleaseWindup = this.windupTicks;
    }

    private void impactTarget(LivingEntity target) {
        target.addEffect(new MobEffectInstance(CoreEffects.FROZEN.get(), freezeTicks, 0));
        level().playSound(
                null,
                target.blockPosition(),
                SoundEvents.PLAYER_HURT_FREEZE,
                SoundSource.HOSTILE,
                1.0F,
                0.8F
        );
        breakCube();
    }

    public int getCrackStage() {
        float cubeHealth = this.entityData.get(CUBE_HEALTH);
        float maxCubeHealth = Math.max(1.0F, this.maxCubeHealth);
        float damagePercent = 1.0F - (cubeHealth / maxCubeHealth);
        int stage = (int)(damagePercent * 9.0F);
        return Mth.clamp(stage, 0, 9);
    }


    private void breakCube() {
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.ITEM_SNOWBALL,
                    getX(), getY(), getZ(),
                    25, 0.4, 0.4, 0.4, 0.08);
            serverLevel.playSound(
                    null,
                    blockPosition(),
                    SoundEvents.GLASS_BREAK,
                    SoundSource.HOSTILE,
                    1.0F,
                    1.0F);
        }
        discard();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (level().isClientSide) return true;
        Entity attacker = source.getEntity();
        Entity owner = getOwner();
        if (attacker != null && owner != null && attacker.getUUID().equals(owner.getUUID())) {
            return false;
        }

        float cubeHealth = entityData.get(CUBE_HEALTH);
        cubeHealth -= Math.max(0.0F, amount);
        entityData.set(CUBE_HEALTH, cubeHealth);

        if (cubeHealth <= 0.0F) {
            breakCube();
        } else {
            level().playSound(
                    null,
                    blockPosition(),
                    SoundEvents.GLASS_HIT,
                    SoundSource.HOSTILE,
                    0.6F,
                    1.2F);
        }
        return true;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        breakCube();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putFloat("HP", entityData.get(CUBE_HEALTH));
        tag.putFloat("MaxHP", maxCubeHealth);
        tag.putInt("Windup", windupTicks);
        tag.putDouble("Speed", homingSpeed);
        tag.putDouble("HitRadius", hitRadius);
        tag.putInt("FreezeTicks", freezeTicks);
        tag.putBoolean("CanAffectAllies", canAffectAllies);
        tag.putInt("AgeTicks", ageTicks);
        if (targetUuid != null) tag.putUUID("Target", targetUuid);
        tag.putBoolean("Released", released);
        tag.putInt("ArcIndex", arcIndex);
        tag.putInt("ArcTotal", arcTotal);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("HP")) entityData.set(CUBE_HEALTH, tag.getFloat("HP"));
        if (tag.contains("MaxHP")) maxCubeHealth = tag.getFloat("MaxHP");
        if (tag.contains("Windup")) windupTicks = tag.getInt("Windup");
        if (tag.contains("Speed")) homingSpeed = tag.getDouble("Speed");
        if (tag.contains("HitRadius")) hitRadius = tag.getDouble("HitRadius");
        if (tag.contains("FreezeTicks")) freezeTicks = tag.getInt("FreezeTicks");
        if (tag.contains("CanAffectAllies")) canAffectAllies = tag.getBoolean("CanAffectAllies");
        if (tag.contains("AgeTicks")) ageTicks = tag.getInt("AgeTicks");
        if (tag.hasUUID("Target")) targetUuid = tag.getUUID("Target");
        if (tag.contains("Released")) released = tag.getBoolean("Released");
        if (tag.contains("ArcIndex")) arcIndex = tag.getInt("ArcIndex");
        if (tag.contains("ArcTotal")) arcTotal = tag.getInt("ArcTotal");
    }
}

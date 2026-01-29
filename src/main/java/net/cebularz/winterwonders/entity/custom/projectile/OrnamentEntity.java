package net.cebularz.winterwonders.entity.custom.projectile;

import net.cebularz.winterwonders.effect.ModEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class OrnamentEntity extends ThrowableProjectile {
    private static final EntityDataAccessor<Integer> VARIANT =
            SynchedEntityData.defineId(OrnamentEntity.class, EntityDataSerializers.INT);
    public OrnamentEntity(EntityType<? extends ThrowableProjectile > type, Level level) {
        super(type, level);
    }

    public OrnamentVariant getVariant() {
        return OrnamentVariant.fromId(this.entityData.get(VARIANT));
    }

    public void setVariant(OrnamentVariant variant) {
        this.entityData.set(VARIANT, variant.variantId);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(VARIANT, OrnamentVariant.RED.variantId);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide() && this.tickCount > 200) {
            this.discard();
        }
    }

    @Override
    protected float getGravity() {
        return 0.01F;
    }

    @Override
    protected void onHitEntity(EntityHitResult hit) {
        super.onHitEntity(hit);

        if (level().isClientSide()) return;
        Entity target = hit.getEntity();
        Entity owner = this.getOwner();
        Vec3 hitLocation = hit.getLocation();

        DamageSource damageSource = damageSources().thrown(this, owner);
        target.hurt(damageSource, 4.0F);

        if (target instanceof LivingEntity livingEntity) {
            switch (getVariant()) {
                case RED -> {
                    level().explode(this, hitLocation.x, hitLocation.y, hitLocation.z, 1.6F, Level.ExplosionInteraction.NONE);
                    AABB explosionRadius = new AABB(hitLocation, hitLocation).inflate(3.0);
                    for (LivingEntity explodedEntity : level().getEntitiesOfClass(LivingEntity.class, explosionRadius, LivingEntity::isAlive)) {
                        if (owner != null && explodedEntity == owner) continue;

                        double dist = explodedEntity.distanceToSqr(hitLocation.x, hitLocation.y, hitLocation.z);
                        if (dist > 9.0) continue;

                        explodedEntity.hurt(damageSource, 6.0F);
                        explodedEntity.setRemainingFireTicks(20 * 4);
                    }
                    igniteGround(BlockPos.containing(hitLocation), 3);
                    burstSnowParticles(hitLocation, 24);
                }
                case BLUE -> {
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20 * 5, 2));
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 20 * 5, 1));
                    freezeGround(BlockPos.containing(hitLocation), 3);
                    burstSnowParticles(hitLocation, 18);
                }
                case BLACK -> {
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20 * 6, 1));
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20 * 3, 0));
                    livingEntity.addEffect(new MobEffectInstance(ModEffects.SUNDERED.get(), 20 * 6, 0));
                    burstSnowParticles(hitLocation, 16);
                }
                case GOLD -> {
                    DamageSource magicDamageSource;
                    try {
                        magicDamageSource = damageSources().indirectMagic(this, owner);
                    } catch (Throwable ignored) {
                        magicDamageSource = damageSources().magic();
                    }

                    livingEntity.hurt(magicDamageSource, 8.0F);
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 20 * 6, 0));
                    Vec3 knockback = this.getDeltaMovement().normalize().scale(0.4);
                    livingEntity.push(knockback.x, 0.12, knockback.z);
                    burstBrightParticles(hitLocation, 48);
                }
            }
        }

        doImpactEffects();
        this.discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult hit) {
        super.onHitBlock(hit);
        if (level().isClientSide()) return;
        doImpactEffects();
        this.discard();
    }

    private void doImpactEffects() {
        RandomSource random = level().getRandom();
        float volume = 1.0f + (random.nextFloat() - random.nextFloat()) * 0.15f;
        float pitch = 1.0f + (random.nextFloat() - random.nextFloat()) * 0.2f;
        level().playSound(
                null,
                blockPosition(),
                SoundEvents.AMETHYST_BLOCK_BREAK,
                SoundSource.PLAYERS,
                volume,
                pitch);

        if (level() instanceof ServerLevel server) {
            server.sendParticles(
                    ParticleTypes.SNOWFLAKE,
                    getX(), getY(), getZ(),
                    36,
                    0.33, 0.33, 0.33,
                    0.15
            );
        }
    }

    private void burstSnowParticles(Vec3 hitPos, int particleCount) {
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.SNOWFLAKE,
                    hitPos.x, hitPos.y, hitPos.z,
                    particleCount,
                    0.25, 0.25, 0.25,
                    0.08);
        }
    }

    private void burstFireParticles(Vec3 hitPos, int particleCount) {
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.FLAME,
                    hitPos.x, hitPos.y, hitPos.z,
                    particleCount,
                    0.25, 0.25, 0.25,
                    0.08);

            serverLevel.sendParticles(
                    ParticleTypes.SMOKE,
                    hitPos.x, hitPos.y, hitPos.z,
                    particleCount,
                    0.25, 0.25, 0.25,
                    0.08);
        }
    }

    private void burstBrightParticles(Vec3 hitPos, int particleCount) {
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.END_ROD,
                    hitPos.x, hitPos.y, hitPos.z,
                    particleCount,
                    0.35, 0.35, 0.35,
                    0.08);
            serverLevel.sendParticles(
                    ParticleTypes.FIREWORK,
                    hitPos.x, hitPos.y, hitPos.z,
                    particleCount / 2,
                    0.35, 0.35, 0.35,
                    0.12);
        }
    }

    private void freezeGround(BlockPos blockPos, int radius) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz > radius * radius) continue;
                BlockPos groundPos = blockPos.offset(dx, 0, dz);
                for (int i = 0; i < 4; i++) {
                    if (!level().getBlockState(groundPos).isAir()) break;
                    groundPos = groundPos.below();
                }

                BlockPos placePos = groundPos.above();
                if (!level().getBlockState(placePos).isAir()) continue;

                var belowState = level().getBlockState(groundPos);
                if (belowState.isAir()) continue;
                if (!belowState.isFaceSturdy(level(), groundPos, Direction.UP)) continue;
                if (!belowState.isCollisionShapeFullBlock(level(), groundPos)) continue;

                int snowLayers = 1 + level().random.nextInt(8);
                var existingState = level().getBlockState(placePos);
                if (existingState.is(Blocks.SNOW)) {
                    int existingLayers = existingState.getValue(SnowLayerBlock.LAYERS);
                    int newLayers = Math.min(8, existingLayers + level().random.nextInt(3) + 1);
                    level().setBlock(placePos, existingState.setValue(SnowLayerBlock.LAYERS, newLayers), 3);
                } else {
                    level().setBlock(placePos, Blocks.SNOW.defaultBlockState().setValue(SnowLayerBlock.LAYERS, snowLayers), 3);
                }

                double x = placePos.getX() + 0.5;
                double y = placePos.getY() + 0.2;
                double z = placePos.getZ() + 0.5;
                Vec3 effectPos = new Vec3(x, y, z);
                burstSnowParticles(effectPos, 60);

                RandomSource random = level().getRandom();
                float volume = 1.0f + (random.nextFloat() - random.nextFloat()) * 0.15f;
                float pitch = 1.0f + (random.nextFloat() - random.nextFloat()) * 0.2f;
                level().playSound(
                        null,
                        placePos,
                        SoundEvents.POWDER_SNOW_PLACE,
                        SoundSource.PLAYERS,
                        volume,
                        pitch);
            }
        }
    }

    private void igniteGround(BlockPos blockPos, int radius) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz > radius * radius) continue;
                if (level().random.nextFloat() > 0.30f) continue;

                BlockPos groundPos = blockPos.offset(dx, 0, dz);
                for (int i = 0; i < 4; i++) {
                    if (!level().getBlockState(groundPos).isAir()) break;
                    groundPos = groundPos.below();
                }

                BlockPos placePos = groundPos.above();
                if (!level().getBlockState(placePos).isAir()) continue;

                var belowState = level().getBlockState(groundPos);
                if (belowState.isAir()) continue;
                if (!belowState.isFaceSturdy(level(), groundPos, Direction.UP)) continue;
                if (!belowState.isCollisionShapeFullBlock(level(), groundPos)) continue;

                BlockState fireState = BaseFireBlock.getState(level(), placePos);
                if (fireState.isAir()) continue;
                level().setBlock(placePos, fireState, 3);

                double x = placePos.getX() + 0.5;
                double y = placePos.getY() + 0.2;
                double z = placePos.getZ() + 0.5;
                Vec3 effectPos = new Vec3(x, y, z);
                burstFireParticles(effectPos, 60);

                RandomSource random = level().getRandom();
                float volume = 1.0f + (random.nextFloat() - random.nextFloat()) * 0.15f;
                float pitch = 1.0f + (random.nextFloat() - random.nextFloat()) * 0.2f;
                level().playSound(
                        null,
                        placePos,
                        SoundEvents.FIRECHARGE_USE,
                        SoundSource.PLAYERS,
                        volume,
                        pitch);
            }
        }
    }

    public enum OrnamentVariant {
        RED(0), BLUE(1), GOLD(2), BLACK(3);

        private final int variantId;
        OrnamentVariant(int variantId) { this.variantId = variantId; }

        public int getVariantId() {
            return variantId;
        }

        public static OrnamentVariant fromId(int id) {
            int clamped = Math.floorMod(id, 4);
            return switch (clamped) {
                case 0 -> RED;
                case 1 -> BLUE;
                case 2 -> GOLD;
                default -> BLACK;
            };
        }

        public static OrnamentVariant getRandomVariant(RandomSource randomSource) {
            return fromId(randomSource.nextInt(4));
        }
    }
}

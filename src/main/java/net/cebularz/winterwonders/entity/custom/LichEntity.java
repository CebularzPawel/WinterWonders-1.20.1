package net.cebularz.winterwonders.entity.custom;

import net.cebularz.winterwonders.entity.ai.lich.LichAttackGoal;
import net.cebularz.winterwonders.item.ModItems;
import net.cebularz.winterwonders.network.ModNetworking;
import net.cebularz.winterwonders.network.packets.LichBossDataS2C;
import net.cebularz.winterwonders.spell.lich.LichSpellbook;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.turtleboi.turtlecore.network.CoreNetworking;
import net.turtleboi.turtlecore.network.packet.effects.FrozenDataS2C;
import net.turtleboi.turtlecore.network.packet.util.SendParticlesS2C;
import net.turtleboi.turtlecore.spell.SpellScheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LichEntity extends Monster {
    private static final float minAttackDist = 0.0f;
    private static final float maxAttackDist = 64.0f;

    private static final EntityDataAccessor<Integer> CASTING_TICKS =
            SynchedEntityData.defineId(LichEntity.class, EntityDataSerializers.INT);

    public float castProgress;
    public float lastCastProgress;

    private int attackCooldown = 0;

    private boolean hasCastIceBlock = false;
    private boolean iceBlockActive = false;

    private static final int ICE_BLOCK_SECONDS = 30;
    private int iceBlockTickCounter = ICE_BLOCK_SECONDS * 20;

    public final List<Mob> spawnedMinions = new ArrayList<>();

    private boolean hasPhased = false;

    public LichEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CASTING_TICKS, 0);
    }

    public int getCastingSpellTicks() {
        return this.entityData.get(CASTING_TICKS);
    }

    public void setCastingSpellTicks(int ticks) {
        this.entityData.set(CASTING_TICKS, ticks);
    }

    public boolean isCastingSpell() {
        return getCastingSpellTicks() > 0;
    }

    public static AttributeSupplier.Builder createAttribute() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.328D)
                .add(Attributes.ATTACK_KNOCKBACK, 1.2D)
                .add(Attributes.ATTACK_SPEED, 0.89D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.MAX_HEALTH, 180.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 6.0D)
                .add(Attributes.ARMOR, 16.0D)
                .add(Attributes.FOLLOW_RANGE, maxAttackDist * 2);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new LichAttackGoal(this, 1.0D, 40, 60, maxAttackDist));

        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected @NotNull PathNavigation createNavigation(Level level) {
        GroundPathNavigation navigation = new GroundPathNavigation(this, level);
        navigation.canOpenDoors();
        return navigation;
    }

    @Override
    public void tick() {
        super.tick();
        tickCooldowns();
        tickRegen();

        if (!hasCastIceBlock && !iceBlockActive && isBelowHalfHealth()) {
            triggerIceBlock();
        }

        if (iceBlockActive) {
            tickIceBlockPhase();
        }

        syncBossHud();
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.level().isClientSide) {
            float target = this.isCastingSpell() ? 1.0F : 0.0F;
            float speed = 0.125f;

            this.lastCastProgress = this.castProgress;
            this.castProgress = Mth.lerp(speed, this.castProgress, target);
            this.castProgress = Mth.clamp(this.castProgress, 0.0F, 1.0F);
        }
    }

    public boolean isOnCooldown() {
        return attackCooldown > 0;
    }

    public void setAttackCooldown(int cooldown) {
        this.attackCooldown = cooldown;
    }

    public boolean isTargetInRangedAttackRange(LivingEntity target) {
        if (target == null) return false;

        double distanceSq = this.distanceToSqr(target);
        return distanceSq >= (minAttackDist * minAttackDist) &&
                distanceSq <= (maxAttackDist * maxAttackDist);
    }

    public boolean hasPhased() {
        return hasPhased;
    }

    private void markPhased() {
        this.hasPhased = true;
    }

    private boolean isBelowHalfHealth() {
        return this.getHealth() < (this.getMaxHealth() * 0.5F);
    }

    private void tickCooldowns() {
        if (attackCooldown > 0) attackCooldown--;

        int casting = getCastingSpellTicks();
        if (casting > 0) setCastingSpellTicks(casting - 1);
    }

    private void tickRegen() {
        if (this.tickCount % 20 != 0) return;

        if (!this.isOnFire() && this.getHealth() < this.getMaxHealth()) {
            this.heal(1.0F);
        }
    }

    private void syncBossHud() {
        if (this.level().isClientSide) return;

        float healthPercentage = this.getHealth() / this.getMaxHealth();
        ModNetworking.sendNear(new LichBossDataS2C(this.getId(), healthPercentage), this);
    }

    private void freezeInPlace() {
        this.setDeltaMovement(Vec3.ZERO);
        this.getNavigation().stop();
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason,
                                                  @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        spawnData = super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.BLIZZARD_STAFF.get()));
        return spawnData;
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (iceBlockActive) return false;

        LivingEntity attacker = (source.getEntity() instanceof LivingEntity livingEntity) ? livingEntity : null;

        if (attacker != null && attacker.isAlive() && this.level() instanceof ServerLevel serverLevel) {
            SpellScheduler.schedule(serverLevel, 20, () -> {
                if (!this.isAlive() || attacker.isRemoved() || !attacker.isAlive()) return;

                RandomSource random = serverLevel.getRandom();
                double distSq = this.distanceToSqr(attacker);

                final double CLOSE_SQ = 16.0;
                final float SUMMON_CHANCE = 0.18f;
                if (this.hasPhased() && random.nextFloat() < SUMMON_CHANCE) {
                    executeSpell(attacker, LichAttackGoal.AttackType.SUMMON_MINIONS);
                    setAttackCooldown(0);
                    return;
                }

                LichAttackGoal.AttackType chosenType;
                if (distSq < CLOSE_SQ) {
                    chosenType = (random.nextFloat() < 0.75f)
                            ? LichAttackGoal.AttackType.ICE_SPIKES
                            : LichAttackGoal.AttackType.WHIRLWIND;
                } else {
                    float roll = random.nextFloat();
                    if (roll < 0.50f) {
                        chosenType = LichAttackGoal.AttackType.BASIC_PROJECTILE;
                    } else if (roll < 0.78f) {
                        chosenType = LichAttackGoal.AttackType.SPECIAL_ATTACK;
                    } else {
                        chosenType = LichAttackGoal.AttackType.BLIZZARD;
                    }
                }

                executeSpell(attacker, chosenType);
                setAttackCooldown(0);
            });
        }

        return super.hurt(source, amount);
    }

    public void executeSpell(LivingEntity target, LichAttackGoal.AttackType attackType) {
        if (level() instanceof ServerLevel serverLevel) {
            this.level().playSound(
                    null,
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    SoundEvents.EVOKER_CAST_SPELL,
                    SoundSource.HOSTILE,
                    1.25F,
                    0.4f / (this.level().getRandom().nextFloat() * 0.4f + 0.8f)
            );
            LichSpellbook.castForAttackType(attackType, serverLevel, this, target);
            setCastingSpellTicks(
                    switch (attackType) {
                        case BASIC_PROJECTILE, FREEZING_CUBE -> 60;
                        case ICE_SPIKES, BLIZZARD, WHIRLWIND, SUMMON_MINIONS, SPECIAL_ATTACK -> 20;
                    });
        }
    }

    private void triggerIceBlock() {
        if (!(this.level() instanceof ServerLevel serverLevel)) return;

        iceBlockActive = true;
        iceBlockTickCounter = ICE_BLOCK_SECONDS * 20;

        int spawnIntervalTicks = 200;
        int totalDurationTicks = ICE_BLOCK_SECONDS * 20;

        for (int delay = 0; delay < totalDurationTicks; delay += spawnIntervalTicks) {
            SpellScheduler.schedule(serverLevel, delay,
                    () -> executeSpell(this.getTarget(), LichAttackGoal.AttackType.SUMMON_MINIONS)
            );
        }
    }

    private void tickIceBlockPhase() {
        freezeInPlace();

        pruneMinions();
        retargetMinions();

        iceBlockTickCounter--;
        setCastingSpellTicks(20);

        CoreNetworking.sendToAllPlayers(new FrozenDataS2C(this.getId(), true));

        if (iceBlockTickCounter <= 0) {
            for (Mob minion : spawnedMinions) {
                if (minion != null && minion.isAlive()) {
                    minion.setGlowingTag(true);
                }
            }

            if (spawnedMinions.isEmpty()) {
                endIceBlockPhase();
            }
        }
    }

    private void endIceBlockPhase() {
        setCastingSpellTicks(0);
        iceBlockActive = false;
        hasCastIceBlock = true;
        markPhased();

        CoreNetworking.sendToAllPlayers(new FrozenDataS2C(this.getId(), false));
        this.level().playSound(
                null,
                this.getX(),
                this.getY(),
                this.getZ(),
                SoundEvents.GLASS_BREAK,
                SoundSource.AMBIENT,
                1.25F,
                0.4f / (this.level().getRandom().nextFloat() * 0.4f + 0.8f)
        );

        spawnIceShatterParticles();
    }

    private void spawnIceShatterParticles() {
        double entitySize = this.getBbHeight() * this.getBbWidth();
        RandomSource random = this.level().getRandom();
        int count = (int) (entitySize * 60);

        for (int i = 0; i < count; i++) {
            double offX = (random.nextDouble() - 0.5) * 0.5;
            double offY = this.getBbHeight() / 2;
            double offZ = (random.nextDouble() - 0.5) * 0.5;

            double theta = random.nextDouble() * Math.PI;
            double phi = random.nextDouble() * 2 * Math.PI;
            double speed = 0.5 + random.nextDouble() * 0.5;

            double xSpeed = speed * Math.sin(theta) * Math.cos(phi);
            double ySpeed = speed * Math.cos(theta);
            double zSpeed = speed * Math.sin(theta) * Math.sin(phi);

            ParticleOptions particle = new BlockParticleOption(ParticleTypes.BLOCK, Blocks.ICE.defaultBlockState());

            CoreNetworking.sendToNear(new SendParticlesS2C(
                    particle,
                    this.getX() + offX,
                    this.getY() + offY,
                    this.getZ() + offZ,
                    xSpeed, ySpeed, zSpeed
            ), this);
        }
    }

    private void pruneMinions() {
        spawnedMinions.removeIf(m -> m == null || m.isRemoved() || !m.isAlive());
    }

    private void retargetMinions() {
        LivingEntity target = this.getTarget();
        if (target == null) return;

        for (Mob minion : spawnedMinions) {
            if (minion != null && minion.isAlive()) {
                minion.setTarget(target);
            }
        }
    }

    public boolean isMinion(LivingEntity entity) {
        if (entity == null) return false;
        return this.spawnedMinions.stream().anyMatch(mob ->
                mob != null && mob.isAlive() && mob.getUUID().equals(entity.getUUID())
        );
    }
}
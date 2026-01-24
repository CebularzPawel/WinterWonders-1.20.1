package net.cebularz.winterwonders.entity.custom;

import net.cebularz.winterwonders.entity.ai.lich.LichAttackGoal;
import net.cebularz.winterwonders.entity.ModEntities;
import net.cebularz.winterwonders.item.ModItems;
import net.cebularz.winterwonders.item.custom.LichBlizzardStaffItem;
import net.cebularz.winterwonders.network.ModNetworking;
import net.cebularz.winterwonders.network.packets.LichBossDataS2C;
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
import net.minecraft.world.InteractionHand;
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
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.turtleboi.turtlecore.effect.CoreEffects;
import net.turtleboi.turtlecore.network.CoreNetworking;
import net.turtleboi.turtlecore.network.packet.effects.FrozenDataS2C;
import net.turtleboi.turtlecore.network.packet.util.SendParticlesS2C;
import net.turtleboi.turtlecore.spells.SpellScheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LichEntity extends Monster implements RangedAttackMob {
    private static final double MIN_RANGED_ATTACK_DISTANCE = 5.0D;
    private static final double MAX_RANGED_ATTACK_DISTANCE = 32.0D;
    private static final EntityDataAccessor<Integer> CASTING_TICKS =
            SynchedEntityData.defineId(LichEntity.class, EntityDataSerializers.INT);

    public float castProgress;
    public float lastCastProgress;
    private int attackCooldown = 0;
    private boolean hasCastIceBlock = false;
    private boolean iceBlockActive = false;
    private final int iceBlockSeconds = 30;
    private int iceBlockTickCounter = iceBlockSeconds * 20;
    public final List<Mob> spawnedMinions = new ArrayList<>();

    public LichEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CASTING_TICKS, 0);
    }

    public static AttributeSupplier.Builder createAttribute() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.328D)
                .add(Attributes.ATTACK_KNOCKBACK, 1.2D)
                .add(Attributes.ATTACK_SPEED, 0.89D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.MAX_HEALTH, 120.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 1.5D)
                .add(Attributes.ARMOR, 1.2D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.0D, 120, 140, 16.0F));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public @NotNull ItemStack getMainHandItem() {
        return this.getItemBySlot(EquipmentSlot.MAINHAND);
    }

    @Override
    protected @NotNull PathNavigation createNavigation(Level pLevel) {
        GroundPathNavigation navigation = new GroundPathNavigation(this, pLevel);
        navigation.canOpenDoors();
        return navigation;
    }

    @Override
    public void tick() {
        super.tick();

        if (attackCooldown > 0) {
            attackCooldown--;
        }

        if (getCastingSpellTicks() > 0) {
            setCastingSpellTicks(getCastingSpellTicks() - 1);
        }

        if (!hasCastIceBlock && !iceBlockActive && this.getHealth() < (this.getMaxHealth() / 2)) {
            triggerIceBlock();
        }

        if (this.tickCount % 20 == 0){
            if (this.getHealth() < this.getMaxHealth()) {
                heal(1);
            }
        }

        if (iceBlockActive) {
            this.setDeltaMovement(Vec3.ZERO);
            this.getNavigation().stop();

            spawnedMinions.removeIf(entity -> !entity.isAlive());
            iceBlockTickCounter--;
            setCastingSpellTicks(20);
            CoreNetworking.sendToAllPlayers(new FrozenDataS2C(this.getId(), true));

            Iterator<Mob> iterator = spawnedMinions.iterator();
            while (iterator.hasNext()) {
                Mob minion = iterator.next();
                if (minion == null || minion.isRemoved()) {
                    iterator.remove();
                } else {
                    minion.setTarget(this.getTarget());
                }
            }

            if (iceBlockTickCounter <= 0) {
                for (Mob minion : spawnedMinions.stream().toList()) {
                    minion.setGlowingTag(true);
                }

                if (spawnedMinions.isEmpty()) {
                    setCastingSpellTicks(0);
                    iceBlockActive = false;
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
                    double entitySize = this.getBbHeight() * this.getBbWidth();
                    //System.out.println("Spawning " + (entitySize * 60) + " particles for " + this.getName());
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
                                xSpeed, ySpeed, zSpeed), this);
                    }

                    hasCastIceBlock = true;
                }
            }

            if (hasCastIceBlock) {
                if (this.tickCount % 200 == 0){
                    spawnMinions();
                }
            }
        }

        if (!this.level().isClientSide) {
            float healthPercentage = this.getHealth() / this.getMaxHealth();
            ModNetworking.sendNear(new LichBossDataS2C(this.getId(), healthPercentage),this);
        }
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

    public boolean isCastingSpell(){
        return getCastingSpellTicks() > 0;
    }

    public int getCastingSpellTicks() {
        return this.entityData.get(CASTING_TICKS);
    }

    public void setCastingSpellTicks(int ticks) {
        this.entityData.set(CASTING_TICKS, ticks);
    }

    public boolean isTargetInRangedAttackRange(LivingEntity target) {
        if (target == null) return false;

        double distanceSq = this.distanceToSqr(target);
        return distanceSq >= (MIN_RANGED_ATTACK_DISTANCE * MIN_RANGED_ATTACK_DISTANCE) &&
                distanceSq <= (MAX_RANGED_ATTACK_DISTANCE * MAX_RANGED_ATTACK_DISTANCE);
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
                                                  MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        pSpawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        ItemStack staffStack = new ItemStack(ModItems.LICH_BLIZZARD_STAFF.get());
        this.setItemSlot(EquipmentSlot.MAINHAND, staffStack);
        //System.out.println("Lich spawned with staff: " + !this.getMainHandItem().isEmpty());
        return pSpawnData;
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        castSpell(target);
        if (this.getHealth() / this.getMaxHealth() <= 0.5) {
            castSpell(target);
        }
    }

    private void castSpell(LivingEntity target){
        Item staffItem = this.getMainHandItem().getItem();
        if (staffItem instanceof LichBlizzardStaffItem lichStaff) {
            lichStaff.setCaster(this);

            double distance = this.distanceToSqr(target);
            float healthPercentage = this.getHealth() / this.getMaxHealth() * 100f;

            LichAttackGoal.AttackType attackType = selectAttackType(distance, healthPercentage);
            executeAttack(lichStaff, target, attackType);
        }
    }

    private LichAttackGoal.AttackType selectAttackType(double distance, float healthPercentage) {
        RandomSource random = level().getRandom();

        if (distance < 36.0) {
            return random.nextBoolean() ?
                    LichAttackGoal.AttackType.WHIRLWIND : LichAttackGoal.AttackType.ICE_SPIKES;
        } else {
            float attackChance = random.nextFloat();
            if (attackChance < 0.5f) {
                return LichAttackGoal.AttackType.BASIC_PROJECTILE;
            } else {
                return random.nextBoolean() ?
                        LichAttackGoal.AttackType.FREEZING_CUBE:
                        LichAttackGoal.AttackType.BLIZZARD;
            }
        }
    }

    private void executeAttack(LichBlizzardStaffItem staffItem, LivingEntity target,
                               LichAttackGoal.AttackType attackType) {
        switch (attackType) {
            case BASIC_PROJECTILE:
                float projectileTypeChance = random.nextFloat();
                if (projectileTypeChance < 0.5f || target.hasEffect(CoreEffects.FROZEN.get())) {
                    staffItem.executeIceSpikeVolley(target);
                    setCastingSpellTicks(60);
                } else {
                    staffItem.executeSnowballVolley(target);
                    setCastingSpellTicks(60);
                }
                break;
            case ICE_SPIKES:
                staffItem.executeTerrainAttack(target, true);
                setCastingSpellTicks(20);
                break;
            case FREEZING_CUBE:
                staffItem.executeTerrainAttack(target, false);
                setCastingSpellTicks(20);
                break;
            case BLIZZARD:
                staffItem.executeBlizzardAttack(target);
                setCastingSpellTicks(20);
                break;
            case WHIRLWIND:
                staffItem.executeWhirlwindAttack();
                setCastingSpellTicks(20);
                break;
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (iceBlockActive) {
            return false;
        }

        if (source.getEntity() instanceof LivingEntity attacker) {
            if (attacker.level() instanceof ServerLevel serverLevel) {
                SpellScheduler.schedule(serverLevel,20, () -> {
                    castSpell(attacker);
                    setAttackCooldown(0);
                });
            }
        }

        return super.hurt(source, amount);
    }

    private void triggerIceBlock() {
        iceBlockActive = true;
        iceBlockTickCounter = iceBlockSeconds * 20;

        int spawnIntervalTicks = 200;
        int totalDurationTicks = iceBlockSeconds * 20;
        int numberOfSpawns = totalDurationTicks / spawnIntervalTicks;
        for (int i = 0; i < numberOfSpawns; i++) {
            int delay = i * spawnIntervalTicks;
            if (this.level() instanceof ServerLevel serverLevel) {
                SpellScheduler.schedule(serverLevel, delay, this::spawnMinions);
            }
        }
    }

    private void spawnMinions() {
        Level level = this.level();
        if (!(level instanceof ServerLevel serverLevel)) return;

        int minionsToSpawn = 2 + random.nextInt(3);

        for (int i = 0; i < minionsToSpawn; i++) {
            Mob minion;
            if (random.nextBoolean()) {
                minion = new Stray(EntityType.STRAY, serverLevel);
                minion.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BOW));
                minion.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModItems.COLDSTEEL_HELMET.get()));
            } else {
                minion = new RevenantEntity(ModEntities.REVENANT.get(), serverLevel);
                RevenantEntity.equipRandomColdsteelArmor((RevenantEntity) minion);
                RevenantEntity.equipRandomColdsteelWeapon((RevenantEntity) minion, 0);
            }
            double offsetX = (random.nextDouble() - 0.5) * 2.0;
            double offsetZ = (random.nextDouble() - 0.5) * 2.0;
            minion.moveTo(this.getX() + offsetX, this.getY(), this.getZ() + offsetZ, this.getYRot(), 0.0F);
            serverLevel.addFreshEntity(minion);
            spawnedMinions.add(minion);

            serverLevel.playSound(
                    minion,
                    minion.blockPosition(),
                    SoundEvents.PLAYER_HURT_FREEZE,
                    SoundSource.HOSTILE,
                    1f,
                    1f
            );

            for (int particle = 0; particle < 40; particle++) {
                double particleOffsetX = (serverLevel.random.nextDouble() - 0.5);
                double particleOffsetY = serverLevel.random.nextDouble() * 1.95;
                double particleOffsetZ = (serverLevel.random.nextDouble() - 0.5);

                serverLevel.sendParticles(
                        ParticleTypes.SNOWFLAKE,
                        minion.getX() + particleOffsetX,
                        minion.getY() + particleOffsetY,
                        minion.getZ() + particleOffsetZ,
                        1,
                        0, 0, 0,
                        0.01
                );
            }
        }
    }

    public boolean isMinion(LichEntity lichEntity, LivingEntity minionEntity) {
        return lichEntity.spawnedMinions.stream().anyMatch(minion -> minion.getUUID().equals(minionEntity.getUUID()));
    }
}
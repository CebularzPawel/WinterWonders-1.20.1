package net.cebularz.winterwonders.entity.custom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.cebularz.winterwonders.entity.ai.lich.LichAttackGoal;
import net.cebularz.winterwonders.entity.animations.AnimationUtil.ParticleTrail;
import net.cebularz.winterwonders.entity.animations.LichAnimations;
import net.cebularz.winterwonders.entity.custom.LichEntity.CastState.Kind;
import net.cebularz.winterwonders.item.ModItems;
import net.cebularz.winterwonders.network.ModNetworking;
import net.cebularz.winterwonders.network.packets.LichBossDataS2C;
import net.cebularz.winterwonders.spell.lich.LichSpellbook;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.DustParticleOptions;
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
import net.turtleboi.turtlecore.particle.CoreParticles;
import net.turtleboi.turtlecore.spell.SpellScheduler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class LichEntity extends Monster {
    private static final float minAttackDist = 0.0f;
    private static final float maxAttackDist = 64.0f;

    private static final EntityDataAccessor<CompoundTag> CAST_STATE =
            SynchedEntityData.defineId(LichEntity.class, EntityDataSerializers.COMPOUND_TAG);
    private CastState castState = CastState.NONE;
    private int castSerial;
    private int releasedCastSerial = -1;
    private int shatteredIceSerial = -1;
    private LivingEntity castTarget;
    private Vec3[] clientFootOffsets;
    private final ParticleTrail sweepTrail = new ParticleTrail(.065);
    private int sweepCastSerial = -1;
    private float sweepTrailTime = -1;

    private int attackCooldown = 0;

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
        this.entityData.define(CAST_STATE, new CompoundTag());
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (CAST_STATE.equals(key)) {
            castState = CastState.fromTag(entityData.get(CAST_STATE));
        }
    }

    public CastState getCastState() {
        return castState == null ? CastState.NONE : castState;
    }

    public float getCastSeconds(float partialTick) {
        return Math.max(0F, (level().getGameTime() - getCastState().started() + partialTick) * .05F);
    }

    public boolean isCastAnimating() {
        CastState cast = getCastState();
        return cast.kind() != Kind.NONE && level().getGameTime() - cast.started() < cast.durationTicks();
    }

    private void setCastState(CastState cast) {
        entityData.set(CAST_STATE, cast.toTag());
    }

    public void onCubeImpact(int serial, long started, int slot) {
        CastState cast = getCastState();
        if (level().isClientSide || cast.kind() != Kind.CUBES || cast.serial() != serial || cast.started() != started
                || slot < 0 || slot >= cast.releaseCount() || cast.cubeImpactTick(slot) >= 0) {
            return;
        }
        int elapsed = (int) (level().getGameTime() - cast.started());
        if (elapsed < cast.releases()[slot]) {
            return;
        }
        setCastState(cast.withCubeImpact(slot, elapsed));
    }

    public Vec3 getStaffCastOrigin() {
        return LichAnimations.staffHead(LichAnimations.sample(getCastState(), getCastSeconds(0), tickCount * .05F),
                position(), yBodyRot);
    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
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
        if (this.level().isClientSide) {
            tickStaffFlightParticles();
            tickStaffSweepParticles();
            tickFootFrostParticles();
            return;
        }
        tickCooldowns();
        tickRegen();

        if (!hasPhased && !iceBlockActive && isBelowHalfHealth()) {
            triggerIceBlock();
        }

        if (iceBlockActive) {
            tickIceBlockPhase();
        }

        tickCast();

        syncBossHud();
    }

    private void tickStaffFlightParticles() {
        CastState cast = getCastState();
        float time = getCastSeconds(0);
        boolean departing = cast.kind() == Kind.ICE_BLOCK && time > CastState.ICE_STAFF_DEPART_TICKS * .05F
                && time < CastState.ICE_STAFF_RISE_TICKS * .05F;
        boolean returning = cast.kind() == Kind.ICE_BLOCK_RELEASE && time < CastState.ICE_STAFF_RETURN_TICKS * .05F;
        if (!departing && !returning) {
            return;
        }
        Vec3 staffHead = getStaffCastOrigin();
        Vec3 previousHead = LichAnimations.staffHead(LichAnimations.sample(cast, Math.max(0, time - .05F), (tickCount - 1) * .05F), position(), yBodyRot);
        DustParticleOptions frost = new DustParticleOptions(new Vector3f(.45F, .85F, 1F), .65F);
        for (int i = 0; i < 3; i++) {
            Vec3 point = previousHead.lerp(staffHead, (i + 1) / 3D);
            level().addParticle(frost, point.x, point.y, point.z, 0, .008, 0);
            if (i != 1) {
                level().addParticle(CoreParticles.CHILLED_PARTICLES.get(), point.x, point.y, point.z, 0, .012, 0);
            }
        }
        if (tickCount % 3 == 0) {
            level().addParticle(ParticleTypes.END_ROD, staffHead.x, staffHead.y, staffHead.z, 0, .012, 0);
        }
    }

    private void tickStaffSweepParticles() {
        CastState cast = getCastState();
        float time = getCastSeconds(0);
        if (cast.kind() != Kind.SPIKES || !isCastAnimating()) {
            sweepCastSerial = -1;
            sweepTrailTime = -1;
            sweepTrail.reset();
            return;
        }
        float releaseTime = cast.releaseCount() > 0 ? cast.releaseSeconds(0) : .9F, trailStart = Math.max(0, releaseTime - .3F);
        if (time < trailStart || time >= cast.recoverySeconds()) {
            return;
        }
        if (position().distanceToSqr(new Vec3(xo, yo, zo)) > 16) {
            sweepTrail.reset();
            sweepCastSerial = cast.serial();
            sweepTrailTime = time;
            Vec3 staffHead = getStaffCastOrigin();
            level().addParticle(CoreParticles.CHILLED_PARTICLES.get(), staffHead.x, staffHead.y, staffHead.z, 0, .01, 0);
            return;
        }
        if (sweepCastSerial != cast.serial() || time - sweepTrailTime > .15F) {
            sweepTrail.reset();
            sweepCastSerial = cast.serial();
            sweepTrailTime = Math.max(trailStart, time - .05F);
        }
        if (time <= sweepTrailTime) {
            return;
        }
        float previousTime = sweepTrailTime;
        List<Vec3> particles = sweepTrail.sample(sampleTime -> {
            float partialTick = Mth.clamp(((float) sampleTime - (time - .05F)) / .05F, 0, 1);
            Vec3 origin = new Vec3(Mth.lerp(partialTick, xo, getX()), Mth.lerp(partialTick, yo, getY()), Mth.lerp(partialTick, zo, getZ()));
            return LichAnimations.staffHead(LichAnimations.sample(cast, (float) sampleTime, (tickCount - 1 + partialTick) * .05F),
                    origin, Mth.rotLerp(partialTick, yBodyRotO, yBodyRot));
        }, previousTime, time);
        sweepTrailTime = time;
        if (particles.isEmpty() && tickCount % 2 == 0) {
            particles.add(getStaffCastOrigin());
        }
        for (Vec3 staffHead : particles) {
            level().addParticle(CoreParticles.CHILLED_PARTICLES.get(), staffHead.x, staffHead.y, staffHead.z, 0, .01, 0);
        }
    }

    public void updateClientFootOffsets(Vec3 left, Vec3 right) {
        if (level().isClientSide) {
            clientFootOffsets = new Vec3[]{left, right};
        }
    }

    private void tickFootFrostParticles() {
        if (clientFootOffsets == null || !isAlive() || isEncasedInIce()) {
            return;
        }
        int interval = getDeltaMovement().lengthSqr() > .005 ? 2 : 3;
        if (tickCount % interval != 0) {
            return;
        }
        for (Vec3 offset : clientFootOffsets) {
            Vec3 foot = new Vec3(-offset.x, 1.501 - offset.y, offset.z)
                    .yRot((180 - yBodyRot) * Mth.DEG_TO_RAD).add(position());
            if (onGround() && foot.y < getY() + .045) {
                continue;
            }
            level().addParticle(CoreParticles.CHILLED_PARTICLES.get(),
                    foot.x + (random.nextDouble() - .5) * .035, foot.y - .015,
                    foot.z + (random.nextDouble() - .5) * .035, 0, -.015, 0);
        }
    }

    public boolean isOnCooldown() {
        return attackCooldown > 0 || isCastAnimating() || iceBlockActive;
    }

    public void setAttackCooldown(int cooldown) {
        this.attackCooldown = cooldown;
    }

    public boolean isTargetInRangedAttackRange(LivingEntity target) {
        if (target == null) {
            return false;
        }

        double distanceSq = this.distanceToSqr(target);
        return distanceSq >= (minAttackDist * minAttackDist) &&
                distanceSq <= (maxAttackDist * maxAttackDist);
    }

    public boolean hasPhased() {
        return hasPhased;
    }

    private boolean isBelowHalfHealth() {
        return this.getHealth() < (this.getMaxHealth() * 0.5F);
    }

    private void tickCooldowns() {
        if (attackCooldown > 0) {
            attackCooldown--;
        }

    }

    private void tickRegen() {
        if (this.tickCount % 20 != 0) {
            return;
        }

        if (!this.isOnFire() && this.getHealth() < this.getMaxHealth()) {
            this.heal(1.0F);
        }
    }

    private void syncBossHud() {
        if (this.level().isClientSide) {
            return;
        }

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
        if (iceBlockActive || isEncasedInIce()) {
            return false;
        }

        LivingEntity attacker = (source.getEntity() instanceof LivingEntity livingEntity) ? livingEntity : null;

        if (attacker != null && attacker.isAlive() && this.level() instanceof ServerLevel serverLevel) {
            SpellScheduler.schedule(serverLevel, 20, () -> {
                if (!this.isAlive() || attacker.isRemoved() || !attacker.isAlive()) {
                    return;
                }

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
        if (target == null || !target.isAlive()) {
            return;
        }
        castSpell(target, LichSpellbook.animationForAttack(attackType, this, target), hasPhased() ? 2 : 1,
                attackType == LichAttackGoal.AttackType.ICE_SPIKES && getRandom().nextBoolean());
    }

    public boolean castSpell(LivingEntity target, Kind kind, int phase, boolean reverse) {
        if (!(level() instanceof ServerLevel) || !isAlive() || iceBlockActive || isCastAnimating()
                || kind == Kind.NONE || kind == Kind.ICE_BLOCK || kind == Kind.ICE_BLOCK_RELEASE) {
            return false;
        }
        if (kind != Kind.SUMMON && (target == null || !target.isAlive() || target.isRemoved() || target.level() != level())) {
            return false;
        }
        castTarget = target;
        CastState cast = LichSpellbook.prepare(kind, level().getGameTime(), ++castSerial, phase, reverse, getRandom());
        setCastState(cast);
        level().playSound(null, getX(), getY(), getZ(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.HOSTILE,
                1.25F, .4F / (getRandom().nextFloat() * .4F + .8F));
        return true;
    }

    private void tickCast() {
        CastState cast = getCastState();
        if (cast.kind() == Kind.NONE || cast.kind() == Kind.ICE_BLOCK) {
            return;
        }
        long elapsed = level().getGameTime() - cast.started();
        if (cast.kind() == Kind.ICE_BLOCK_RELEASE) {
            freezeInPlace();
            if (elapsed < CastState.ICE_SHATTER_TICKS) {
                CoreNetworking.sendToAllPlayers(new FrozenDataS2C(this.getId(), true));
            }
            if (elapsed >= CastState.ICE_SHATTER_TICKS && shatteredIceSerial != cast.serial()) {
                shatteredIceSerial = cast.serial();
                shatterIce();
            }
        }
        if (elapsed >= cast.durationTicks()) {
            setCastState(CastState.NONE);
            castTarget = null;
        } else if (cast.kind() != Kind.ICE_BLOCK_RELEASE && releasedCastSerial != cast.serial()
                && elapsed >= LichSpellbook.windupTicks(cast.kind())) {
            releasedCastSerial = cast.serial();
            if (cast.kind() == Kind.SUMMON || castTarget != null && castTarget.isAlive()
                    && !castTarget.isRemoved() && castTarget.level() == level()) {
                LichSpellbook.release(cast, (ServerLevel) level(), this, castTarget);
            }
        }
    }

    private void triggerIceBlock() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        iceBlockActive = true;
        iceBlockTickCounter = ICE_BLOCK_SECONDS * 20;
        castTarget = null;
        setCastState(CastState.create(Kind.ICE_BLOCK, level().getGameTime(), ++castSerial, 1, false, new int[0]));

        int spawnIntervalTicks = 200;
        int totalDurationTicks = ICE_BLOCK_SECONDS * 20;

        for (int delay = CastState.ICE_BLOCK_SUMMON_START_TICKS; delay < totalDurationTicks; delay += spawnIntervalTicks) {
            SpellScheduler.schedule(serverLevel, delay,
                    () -> {
                        if (isAlive() && !isRemoved() && iceBlockActive) {
                            LichSpellbook.SUMMON_MINIONS.cast(serverLevel, this, getTarget(), getRandom());
                        }
                    }
            );
        }
    }

    private void tickIceBlockPhase() {
        freezeInPlace();

        pruneMinions();
        retargetMinions();

        iceBlockTickCounter--;

        boolean settled = level().getGameTime() - getCastState().started() >= CastState.ICE_BLOCK_SETTLE_TICKS;
        CoreNetworking.sendToAllPlayers(new FrozenDataS2C(this.getId(), settled));

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
        iceBlockActive = false;
        hasPhased = true;
        int phaseTicks = (int) (level().getGameTime() - getCastState().started());
        setCastState(CastState.create(Kind.ICE_BLOCK_RELEASE, level().getGameTime(), ++castSerial, 2, false, new int[]{phaseTicks}));
    }

    public boolean isEncasedInIce() {
        CastState cast = getCastState();
        long elapsed = level().getGameTime() - cast.started();
        return cast.kind() == Kind.ICE_BLOCK && elapsed >= CastState.ICE_BLOCK_SETTLE_TICKS
                || cast.kind() == Kind.ICE_BLOCK_RELEASE && elapsed < CastState.ICE_SHATTER_TICKS;
    }

    private void shatterIce() {
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
        if (target == null) {
            return;
        }

        for (Mob minion : spawnedMinions) {
            if (minion != null && minion.isAlive()) {
                minion.setTarget(target);
            }
        }
    }

    public boolean isMinion(LivingEntity entity) {
        if (entity == null) {
            return false;
        }
        return this.spawnedMinions.stream().anyMatch(mob ->
                mob != null && mob.isAlive() && mob.getUUID().equals(entity.getUUID())
        );
    }

    public record CastState(Kind kind, long started, int serial, int phase, boolean reverse,
                            int[] releases, int[] cubeImpacts, int recoveryTick, int durationTicks) {
        public static final int CUBE_POINT_START_TICKS = 14;
        public static final int CUBE_POINT_END_TICKS = 25;
        public static final int CUBE_RELEASE_TICKS = 27;
        public static final int ICE_BLOCK_SETTLE_TICKS = 20;
        public static final int ICE_STAFF_DEPART_TICKS = ICE_BLOCK_SETTLE_TICKS + 10;
        public static final int ICE_STAFF_RISE_TICKS = ICE_STAFF_DEPART_TICKS + 70;
        public static final int ICE_BLOCK_SUMMON_START_TICKS = ICE_STAFF_RISE_TICKS + 20;
        public static final int ICE_STAFF_RETURN_TICKS = 52;
        public static final int ICE_PUSH_START_TICKS = ICE_STAFF_RETURN_TICKS + 5;
        public static final int ICE_SHATTER_TICKS = ICE_PUSH_START_TICKS + 8;
        public static final int ICE_PUSH_PEAK_TICKS = ICE_SHATTER_TICKS + 7;
        public static final int ICE_THAW_END_TICKS = ICE_PUSH_START_TICKS + 32;
        public static final int ICE_BLOCK_RECOVERY_TICKS = ICE_THAW_END_TICKS + 40;

        public enum Kind {NONE, SNOWBALL, ICICLE, CUBES, SPIKES, BLIZZARD, WHIRLWIND, SUMMON, ICE_BLOCK, ICE_BLOCK_RELEASE}

        public static final CastState NONE = new CastState(Kind.NONE, 0, 0, 1, false, new int[0], new int[0], 0, 0);

        public CastState {
            releases = releases.clone();
            cubeImpacts = cubeImpacts.clone();
            phase = phase >= 2 ? 2 : 1;
        }

        @Override
        public int[] releases() {
            return releases.clone();
        }

        @Override
        public int[] cubeImpacts() {
            return cubeImpacts.clone();
        }

        public int cubeImpactTick(int slot) {
            return slot >= 0 && slot < cubeImpacts.length ? cubeImpacts[slot] : -1;
        }

        public CastState withCubeImpact(int slot, int tick) {
            int[] impactTicks = cubeImpacts();
            impactTicks[slot] = tick;
            return new CastState(kind, started, serial, phase, reverse, releases, impactTicks, recoveryTick, durationTicks);
        }

        public int releaseCount() {
            return releases.length;
        }

        public float releaseSeconds(int index) {
            return releases[index] * .05F;
        }

        public boolean enraged() {
            return phase == 2;
        }

        public float recoverySeconds() {
            return recoveryTick * .05F;
        }

        public float durationSeconds() {
            return durationTicks * .05F;
        }

        public boolean ice() {
            return kind == Kind.ICE_BLOCK || kind == Kind.ICE_BLOCK_RELEASE;
        }

        public static CastState create(Kind kind, long started, int serial, int phase, boolean reverse, int[] releases) {
            int lastRelease = Arrays.stream(releases).max().orElse(0);
            int recoveryTick = switch (kind) {
                case SNOWBALL, ICICLE -> phase >= 2 ? 38 : 54;
                case SPIKES -> 36;
                case BLIZZARD -> 62;
                case WHIRLWIND -> 33;
                case SUMMON -> lastRelease + 10;
                case CUBES -> Math.max((phase >= 2 ? 58 : 48) + CUBE_RELEASE_TICKS - 18, lastRelease + (phase >= 2 ? 10 : 20));
                default -> 0;
            };
            int durationTicks = switch (kind) {
                case NONE -> 0;
                case WHIRLWIND -> 57;
                case SUMMON -> recoveryTick + 20;
                case ICE_BLOCK -> Integer.MAX_VALUE;
                case ICE_BLOCK_RELEASE -> ICE_BLOCK_RECOVERY_TICKS;
                default -> recoveryTick + 16;
            };
            int[] impactTicks = new int[releases.length];
            Arrays.fill(impactTicks, -1);
            return new CastState(kind, started, serial, phase, reverse, releases, impactTicks, recoveryTick, durationTicks);
        }

        public CompoundTag toTag() {
            CompoundTag tag = new CompoundTag();
            tag.putInt("Kind", kind.ordinal());
            tag.putLong("Started", started);
            tag.putInt("Serial", serial);
            tag.putInt("Phase", phase);
            tag.putBoolean("Reverse", reverse);
            tag.putIntArray("Releases", releases);
            tag.putInt("Recovery", recoveryTick);
            tag.putInt("Duration", durationTicks);
            tag.putIntArray("CubeImpacts", cubeImpacts);
            return tag;
        }

        public static CastState fromTag(CompoundTag tag) {
            int kindId = tag.getInt("Kind");
            if (kindId <= 0 || kindId >= Kind.values().length) {
                return NONE;
            }
            int[] releases = tag.getIntArray("Releases"), impactTicks = tag.getIntArray("CubeImpacts");
            if (impactTicks.length != releases.length) {
                impactTicks = new int[releases.length];
                Arrays.fill(impactTicks, -1);
            }
            return new CastState(Kind.values()[kindId], tag.getLong("Started"), tag.getInt("Serial"),
                    tag.getInt("Phase"), tag.getBoolean("Reverse"), releases,
                    impactTicks, tag.getInt("Recovery"), tag.getInt("Duration"));
        }
    }
}

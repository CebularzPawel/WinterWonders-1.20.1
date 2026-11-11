package net.cebularz.winterwonders.entity.custom;

import java.util.EnumSet;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.mojang.serialization.Dynamic;

import net.cebularz.winterwonders.entity.ModEntities;
import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.entity.ai.pingin.PinginMeleeAttackGoal;
import net.cebularz.winterwonders.entity.ai.pingin.PinginFoodGoal;
import net.cebularz.winterwonders.entity.ai.pingin.PinginBreathGoal;
import net.cebularz.winterwonders.entity.ai.pingin.PinginSwimGoal;
import net.cebularz.winterwonders.entity.ai.pingin.PinginTradeWithPlayerGoal;
import net.cebularz.winterwonders.entity.ai.pingin.SlideOnIceGoal;
// import net.cebularz.winterwonders.entity.ai.pingin.PinginWorkGoal;
// import net.cebularz.winterwonders.entity.ai.pingin.PinginColonyGoal;
import net.cebularz.winterwonders.entity.ai.pingin.PinginFollowParentGoal;
import net.cebularz.winterwonders.entity.ai.pingin.PinginFollowPlayerGoal;
import net.cebularz.winterwonders.entity.ai.pingin.PinginRetrieveSpearGoal;
import net.cebularz.winterwonders.entity.ai.pingin.PinginAvoidGoal;
// import net.cebularz.winterwonders.block.entity.custom.FishRackBlockEntity;
// import net.cebularz.winterwonders.block.ModBlocks;
// import net.cebularz.winterwonders.init.ModPoiTypes;
import net.cebularz.winterwonders.item.ModItems;
import net.cebularz.winterwonders.item.custom.ThrowingSpearItem;

import net.minecraft.core.BlockPos;
// import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.gossip.GossipContainer;
import net.minecraft.world.entity.ai.gossip.GossipType;
// import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;
// import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.pathfinder.AmphibiousNodeEvaluator;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

public class PinginEntity extends Animal {
    private static final EntityDataAccessor<Boolean> ADMIRING = SynchedEntityData.defineId(PinginEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SLIDING = SynchedEntityData.defineId(PinginEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> JAB_REACH = SynchedEntityData.defineId(PinginEntity.class, EntityDataSerializers.FLOAT);
    // private static final EntityDataAccessor<Integer> WORK = SynchedEntityData.defineId(PinginEntity.class, EntityDataSerializers.INT);
    private static final byte ATTACK_EVENT = 100;
    private static final byte SWIM_EVENT = 80;
    private static final byte SWIM_STOP_EVENT = 84;
    public static final int DIVE = 1, LEAP = 2, CATCH = 3, SHAKE = 4;
    public static final int LAND_TICKS = 12, SHAKE_TICKS = 24;
    // public static final int CHIP_PECK_TICKS = 24;
    // public static final int PREEN = 1, RUMMAGE = 2, BUILD = 3, CRAFT = 4, CHIP = 5, CHIP_BACK = 6, CHIP_WALK = 7;
    public static final int RECRUIT_TICKS = 6000;
    private static final PinginAttack[] ATTACKS = PinginAttack.values();
    private static final TagKey<Item> WEAPONS = TagKey.create(Registries.ITEM, new ResourceLocation(WinterWonders.MOD_ID, "pingin_weapons"));

    public final AnimationState slideAnimationState = new AnimationState();
    public final AnimationState attackAnimationState = new AnimationState();
    public final AnimationState swimActionState = new AnimationState();
    // public final AnimationState workAnimationState = new AnimationState();
    public float admireProgress, lastAdmireProgress;
    public float slideProgress, lastSlideProgress;
    public float swimProgress, lastSwimProgress, diveProgress, lastDiveProgress;
    public float swimPitch, lastSwimPitch;
    public float landProgress, lastLandProgress;
    public int lastShakeTick = -1;
    public Vec3 canePlant;
    public float canePhase = -1F, caneTime;
    private final MoveControl landMove, waterMove;
    private int swimAction, swimEnd;
    private boolean wet;
    private BlockPos homePos;
    // private BlockPos colonyPos, nestPos;
    private UUID parentId, otherParentId;
    private UUID recruitId;
    private long recruitEnd, nextGossipDecay;
    private boolean recruitWarned;
    private final GossipContainer gossip = new GossipContainer();
    // private PinginRole colonyRole;
    // private long nextPreenTime;
    private long nextMealTime;
    private final SimpleContainer foodInventory = new SimpleContainer(9);
    private PinginAttack attackType = PinginAttack.SLASH;
    private int attackAnimationEndTick;
    private int slideTicks;
    private final SimpleContainer tradeContainer = new SimpleContainer(20);

    public PinginEntity(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        landMove = moveControl;
        waterMove = new SmoothSwimmingMoveControl(this, 85, 20, 0.16F, 1F, false) {
            private float turn;

            @Override
            public void tick() {
                float yaw = getYRot();
                boolean vertical = Math.abs(wantedY - getY()) > 0.5D && Math.hypot(wantedX - getX(), wantedZ - getZ()) < 0.25D;
                super.tick();
                if (isInWater()) {
                    boolean urgent = getAirSupply() < 400;
                    float change = Mth.wrapDegrees(getYRot() - yaw);
                    if (vertical || getNavigation().isDone()) {
                        turn = 0F;
                    } else if (urgent) {
                        turn = change;
                    } else {
                        turn = Mth.lerp(0.25F, turn, Mth.clamp(change, -12F, 12F));
                        turn = Mth.clamp(turn, -Math.abs(change), Math.abs(change));
                    }
                    setYRot(yaw + turn);
                    yBodyRot = getYRot();
                    setYHeadRot(getYRot());
                    if (!vertical && !urgent) {
                        float heading = (float)(Mth.atan2(getWantedX() - getX(), getWantedZ() - getZ()) * -Mth.RAD_TO_DEG);
                        float error = Mth.wrapDegrees(heading - getYRot()) * Mth.DEG_TO_RAD;
                        float pace = Mth.clamp(Mth.cos(error), 0.25F, 1F);
                        setZza(zza * pace);
                        setYya(yya * pace);
                    }
                }
            }
        };
        lookControl = new LookControl(this) {
            @Override
            protected boolean resetXRotOnTick() {
                return !isInWater();
            }

            @Override
            protected Optional<Float> getXRotD() {
                return isInWater() ? Optional.empty() : super.getXRotD();
            }
        };
        setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0F);
        setAirSupply(getMaxAirSupply());
        List<Item> rewards = List.of(Items.ENCHANTED_BOOK, Items.APPLE, Items.GOLD_BLOCK);
        for (int i = 0; i < rewards.size(); i++) {
            tradeContainer.setItem(i, new ItemStack(rewards.get(i)));
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new PinginBreathGoal(this));
        Goal shake = new Goal() {
            @Override
            public boolean canUse() {
                return swimAction == SHAKE && onGround() && !isInWaterOrBubble();
            }

            @Override
            public void start() {
                getNavigation().stop();
            }
        };
        shake.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
        this.goalSelector.addGoal(3, shake);
        this.goalSelector.addGoal(1, new PinginAvoidGoal(this));
        this.goalSelector.addGoal(2, new PanicGoal(this, 2.0D) {
            @Override
            public boolean canUse() {
                return (isOnFire() || !canFight()) && super.canUse();
            }
        });
        this.goalSelector.addGoal(2, new PinginRetrieveSpearGoal(this));
        this.goalSelector.addGoal(2, new PinginMeleeAttackGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this) {
            @Override
            public boolean canUse() {
                return canFight() && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return canFight() && super.canContinueToUse();
            }
        });

        this.goalSelector.addGoal(3, new PinginFoodGoal(this));
        this.goalSelector.addGoal(3, new PinginFollowPlayerGoal(this));
        // this.goalSelector.addGoal(3, new PinginColonyGoal(this));

        this.goalSelector.addGoal(4, new PinginFollowParentGoal(this));
        this.goalSelector.addGoal(2, new PinginTradeWithPlayerGoal(this,
                List.of(Items.ICE, Items.PACKED_ICE, Items.BLUE_ICE, Items.SNOWBALL, Items.WOODEN_SWORD, ModItems.FLINT_SPEAR.get())));
        // this.goalSelector.addGoal(5, new PinginWorkGoal(this));
        this.goalSelector.addGoal(5, new PinginSwimGoal(this));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.75D, 0F));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 3f));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.goalSelector.addGoal(8, new SlideOnIceGoal(this, 1.2D));
    }

    @Override
    protected void updateWalkAnimation(float movement) {
        float speed = getPose() == Pose.STANDING ? Math.min(movement * 6F, 1F) : 0F;
        walkAnimation.update(speed, 0.2F);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ADMIRING, false);
        this.entityData.define(SLIDING, false);
        this.entityData.define(JAB_REACH, 0.5F);
        // this.entityData.define(WORK, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("admiring", this.isAdmiring());
        if (homePos != null) {
            pCompound.put("Home", NbtUtils.writeBlockPos(homePos));
        }
        pCompound.put("Food", foodInventory.createTag());
        pCompound.putLong("NextMeal", nextMealTime);
        // pCompound.putString("ColonyRole", getColonyRole().name());
        // pCompound.putLong("NextPreen", nextPreenTime);
        // if (colonyPos != null) {
            // pCompound.put("Colony", NbtUtils.writeBlockPos(colonyPos));
        // }
        // if (nestPos != null) {
            // pCompound.put("Nest", NbtUtils.writeBlockPos(nestPos));
        // }
        if (parentId != null) {
            pCompound.putUUID("Parent", parentId);
        }
        if (otherParentId != null) {
            pCompound.putUUID("OtherParent", otherParentId);
        }
        if (recruitId != null) {
            pCompound.putUUID("Recruiter", recruitId);
        }
        pCompound.putLong("RecruitEnd", recruitEnd);
        pCompound.putBoolean("RecruitWarned", recruitWarned);
        pCompound.putLong("NextGossipDecay", nextGossipDecay);
        pCompound.put("Gossips", gossip.store(NbtOps.INSTANCE));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setAdmiring(pCompound.getBoolean("admiring"));
        if (pCompound.contains("Home", Tag.TAG_COMPOUND)) {
            homePos = NbtUtils.readBlockPos(pCompound.getCompound("Home"));
        }
        foodInventory.fromTag(pCompound.getList("Food", Tag.TAG_COMPOUND));
        nextMealTime = pCompound.getLong("NextMeal");
        // for (PinginRole role : PinginRole.values()) {
            // if (role.name().equals(pCompound.getString("ColonyRole"))) {
                // colonyRole = role;
            // }
        // }
        // nextPreenTime = pCompound.getLong("NextPreen");
        // colonyPos = pCompound.contains("Colony", Tag.TAG_COMPOUND) ? NbtUtils.readBlockPos(pCompound.getCompound("Colony")) : null;
        // nestPos = pCompound.contains("Nest", Tag.TAG_COMPOUND) ? NbtUtils.readBlockPos(pCompound.getCompound("Nest")) : null;
        parentId = pCompound.hasUUID("Parent") ? pCompound.getUUID("Parent") : null;
        otherParentId = pCompound.hasUUID("OtherParent") ? pCompound.getUUID("OtherParent") : null;
        recruitId = pCompound.hasUUID("Recruiter") ? pCompound.getUUID("Recruiter") : null;
        recruitEnd = pCompound.getLong("RecruitEnd");
        recruitWarned = pCompound.getBoolean("RecruitWarned");
        nextGossipDecay = pCompound.getLong("NextGossipDecay");
        gossip.update(new Dynamic<>(NbtOps.INSTANCE, pCompound.getList("Gossips", Tag.TAG_COMPOUND)));
    }

    @Override
    public void aiStep() {
        MoveControl control = isInWater() ? waterMove : landMove;
        if (moveControl != control) {
            moveControl = control;
            moveControl.setWantedPosition(getX(), getY(), getZ(), 0D);
        }
        if (!this.level().isClientSide && this.slideTicks > 0 && --this.slideTicks == 0) {
            this.entityData.set(SLIDING, false);
        }
        super.aiStep();
        if (!level().isClientSide) {
            if (!isAttacking()) {
                ItemStack mainHand = getMainHandItem(), offHand = getOffhandItem();
                if (mainHand.isEmpty() && isWeapon(offHand)
                        || getTarget() == null && offHand.getItem() instanceof ThrowingSpearItem) {
                    setItemInHand(InteractionHand.MAIN_HAND, offHand);
                    setItemInHand(InteractionHand.OFF_HAND, mainHand);
                }
            }
            if (level().getGameTime() >= nextGossipDecay) {
                gossip.decay();
                nextGossipDecay = level().getGameTime() + 24000L;
            }
            if (recruitId != null && recruitEnd > 0L && level().getGameTime() >= recruitEnd) {
                recruitEnd = 0L;
                setTarget(null);
                getNavigation().stop();
            }
            if (isRecruited() && tickCount % 20 == 0) {
                Player player = getRecruiter();
                if (player != null) {
                    if (!likesPlayer(player)) {
                        recruitEnd = 0L;
                        setTarget(null);
                    } else {
                        if (!recruitWarned && recruitEnd - level().getGameTime() <= 1200L) {
                            recruitWarned = true;
                            getLookControl().setLookAt(player, 45F, 45F);
                            playSound(SoundEvents.PARROT_AMBIENT, 1F, 1.2F);
                            ((ServerLevel)level()).sendParticles(ParticleTypes.NOTE, getX(), getEyeY() + 0.25D, getZ(), 1, 0D, 0D, 0D, 0D);
                        }
                        if (canFight() && getTarget() == null && distanceToSqr(player) < 1024D) {
                            LivingEntity threat = player.getLastHurtByMob();
                            if (threat == null || !threat.isAlive() || player.tickCount - player.getLastHurtByMobTimestamp() > 100) {
                                threat = player.tickCount - player.getLastHurtMobTimestamp() <= 100 ? player.getLastHurtMob() : null;
                            }
                            if (threat == null || !threat.isAlive() || isAlliedTo(threat)) {
                                threat = level().getEntitiesOfClass(Monster.class, getBoundingBox().inflate(12D),
                                        monster -> monster.isAlive() && getSensing().hasLineOfSight(monster)).stream()
                                        .min(Comparator.comparingDouble(this::distanceToSqr)).orElse(null);
                            }
                            if (threat != null && threat.isAlive() && canAttack(threat) && !isAlliedTo(threat)) {
                                setTarget(threat);
                            }
                        }
                    }
                }
            }
            if (tickCount == 20 || (tickCount + getId() * 17) % 400 == 0) {
                List<PinginEntity> neighbors = level().getEntitiesOfClass(PinginEntity.class, getBoundingBox().inflate(6D),
                        pingin -> pingin != this && pingin.isAlive());
                if (!neighbors.isEmpty()) {
                    PinginEntity neighbor = neighbors.get(random.nextInt(neighbors.size()));
                    gossip.transferFrom(neighbor.gossip, random, 2);
                    neighbor.gossip.transferFrom(gossip, random, 2);
                }
                // if (recruitId == null && getFishRack() == null) {
                    // colonyPos = ((ServerLevel)level()).getPoiManager().findClosest(pointOfInterest -> pointOfInterest.is(ModPoiTypes.FISH_RACK.getKey()),
                            // pos -> level().hasChunkAt(pos) && level().getBlockEntity(pos) instanceof FishRackBlockEntity,
                            // blockPosition(), 32, PoiManager.Occupancy.ANY).orElse(null);
                    // if (colonyPos != null) {
                        // for (Direction side : Direction.Plane.HORIZONTAL) {
                            // BlockPos pos = colonyPos.relative(side);
                            // if (level().getBlockState(pos).getCollisionShape(level(), pos).isEmpty()
                                    // && level().getFluidState(pos).isEmpty()
                                    // && !level().getBlockState(pos.below()).getCollisionShape(level(), pos.below()).isEmpty()) {
                                // homePos = pos;
                                // break;
                            // }
                        // }
                        // if (hasWeapon()) {
                            // colonyRole = PinginRole.GUARD;
                        // } else if (colonyRole == null) {
                            // colonyRole = Math.floorMod(getUUID().hashCode(), 4) == 0 ? PinginRole.CARETAKER : PinginRole.FISHER;
                        // }
                    // }
                // }
                // if (getFishRack() != null) {
                    // if (nestPos == null || level().hasChunkAt(nestPos) && !level().getBlockState(nestPos).is(ModBlocks.PINGIN_NEST.get())) {
                        // nestPos = ((ServerLevel)level()).getPoiManager().findClosest(pointOfInterest -> pointOfInterest.is(ModPoiTypes.PINGIN_NEST.getKey()),
                                // blockPosition(), 16, PoiManager.Occupancy.ANY).orElse(null);
                    // }
                    // if (isBaby() && parentId == null) {
                        // level().getEntitiesOfClass(PinginEntity.class, getBoundingBox().inflate(6D),
                                // adult -> adult.isAlive() && !adult.isBaby() && colonyPos.equals(adult.colonyPos)).stream()
                                // .min(Comparator.comparingDouble(this::distanceToSqr)).ifPresent(adult -> parentId = adult.getUUID());
                    // }
                // }
            }
            if (homePos != null && tickCount % 20 == 0 && level().hasChunkAt(homePos)
                    && (!level().getFluidState(homePos).isEmpty()
                    || level().getBlockState(homePos.below()).getCollisionShape(level(), homePos.below()).isEmpty())) {
                homePos = null;
            }
            if (isInWaterOrBubble()) {
                wet = true;
            } else if (onGround() && wet) {
                wet = false;
                if (getTarget() == null && !isAdmiring()) {
                    startSwimAction(SHAKE);
                }
            }
            if (swimAction == SHAKE) {
                int elapsed = LAND_TICKS + SHAKE_TICKS - (swimEnd - tickCount);
                if (elapsed == LAND_TICKS) {
                    playSound(SoundEvents.WOLF_SHAKE, 0.6F, 1.6F);
                }
            }
        }
        if (!level().isClientSide && onGround() && (swimAction == DIVE || swimAction == LEAP)) {
            swimEnd = tickCount;
            level().broadcastEntityEvent(this, SWIM_STOP_EVENT);
        }
        if (tickCount >= swimEnd) {
            swimAction = 0;
            swimActionState.stop();
        }
        lastLandProgress = landProgress;
        boolean landing = swimAction == SHAKE && !isInWaterOrBubble();
        Vec3 motion = getDeltaMovement();
        if (swimAction == LEAP && !onGround() && !isUnderWater() && motion.y <= 0.34D) {
            landing = level().getBlockCollisions(this, getBoundingBox().expandTowards(motion.x * 9D,
                    Math.min(-0.1D, motion.y * 9D - 3.24D), motion.z * 9D)).iterator().hasNext();
        }
        landProgress = Mth.approach(landProgress, landing ? 1F : 0F, 0.2F);
        if (this.level().isClientSide) {
            setupAnimationStates();
        }
    }

    private void setupAnimationStates(){
        // if (getWork() > 0) {
            // workAnimationState.startIfStopped(tickCount);
            // if (getWork() == CHIP || getWork() == CHIP_BACK) {
                // yBodyRot = getYRot();
            // }
        // } else {
            // workAnimationState.stop();
        // }
        lastSwimProgress = swimProgress;
        lastDiveProgress = diveProgress;
        lastSwimPitch = swimPitch;
        boolean airborne = !onGround() && (swimAction == DIVE || swimAction == LEAP);
        swimProgress = Mth.approach(swimProgress, isInWater() || airborne ? 1F : 0F, 0.12F);
        diveProgress = Mth.approach(diveProgress, isUnderWater() || airborne ? 1F : 0F, 0.12F);
        Vec3 motion = getDeltaMovement();
        float pitch = motion.lengthSqr() > 0.0004D ? (float)Math.toDegrees(Math.atan2(-motion.y, motion.horizontalDistance())) : 0F;
        swimPitch = Mth.rotLerp(0.2F, swimPitch, Mth.clamp(pitch, -65F, 65F) * (1F - landProgress));
        this.lastSlideProgress = this.slideProgress;
        this.slideProgress = Mth.approach(this.slideProgress, this.isSliding() ? 1F : 0F, 0.2F);
        if (this.isSliding()) {
            this.slideAnimationState.startIfStopped(this.tickCount);
        } else if (this.slideProgress == 0F) {
            this.slideAnimationState.stop();
        }
        if (this.tickCount >= this.attackAnimationEndTick) {
            this.attackAnimationState.stop();
        }

        float target = this.isAdmiring() ? 1.0F : 0.0F;
        float speed = 0.2f;
        this.lastAdmireProgress = this.admireProgress;
        this.admireProgress = Mth.lerp(speed, this.admireProgress, target);
        this.admireProgress = Mth.clamp(this.admireProgress, 0.0F, 1.0F);
    }

    public static AttributeSupplier.Builder createAttributes(){
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 12D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 2D)
                .add(Attributes.FOLLOW_RANGE, 24D);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new AmphibiousPathNavigation(this, level) {
            private Vec3 destination;

            @Override
            protected PathFinder createPathFinder(int maxNodes) {
                // nodeEvaluator = new AmphibiousNodeEvaluator(false) {
                    // @Override
                    // public BlockPathTypes getBlockPathType(BlockGetter blocks, int x, int y, int z) {
                        // if (getWork() >= CHIP && blocks.getFluidState(new BlockPos(x, y, z)).is(FluidTags.WATER)) {
                            // return BlockPathTypes.BLOCKED;
                        // } else {
                            // return super.getBlockPathType(blocks, x, y, z);
                        // }
                    // }
                // };
                nodeEvaluator = new AmphibiousNodeEvaluator(false);
                nodeEvaluator.setCanPassDoors(true);
                return new PathFinder(nodeEvaluator, maxNodes);
            }

            @Override
            public boolean moveTo(Entity target, double speed) {
                return isInWater() ? moveTo(target.getX(), target.getY(), target.getZ(), speed) : super.moveTo(target, speed);
            }

            @Override
            public boolean moveTo(double x, double y, double z, double speed) {
                Vec3 target = new Vec3(x, y, z);
                Path route = createPath(x, y, z, 0);
                if (isInWater() && path != null && path.isDone() && route != null && route.sameAs(path)
                        && path.getNodeCount() > 0 && distanceToSqr(target) > 0.16D && canMoveDirectly(getTempMobPos(), target)) {
                    path.setNextNodeIndex(path.getNodeCount() - 1);
                }
                boolean moving = super.moveTo(route, speed);
                destination = moving ? target : null;
                return moving;
            }

            @Override
            public void stop() {
                super.stop();
                destination = null;
            }

            @Override
            public void tick() {
                super.tick();
                if (isInWater() && destination != null && !isDone() && distanceToSqr(destination) < 9D
                        && canMoveDirectly(getTempMobPos(), destination)) {
                    getMoveControl().setWantedPosition(destination.x, destination.y, destination.z, speedModifier);
                }
            }

            @Override
            protected void followThePath() {
                Vec3 position = getTempMobPos();
                if (isInWater() && destination != null && path != null && path.getNextNodeIndex() == path.getNodeCount() - 1
                        && distanceToSqr(destination) > 0.16D && distanceToSqr(destination) < 9D
                        && canMoveDirectly(position, destination)) {
                    doStuckDetection(position);
                } else {
                    super.followThePath();
                }
            }
        };
    }

    @Override
    public float getPathfindingMalus(BlockPathTypes type) {
        // if (getWork() >= CHIP && type == BlockPathTypes.WATER_BORDER) {
        //     return 0F;
        // } else
        if (type == BlockPathTypes.WATER) {
            return isInWater() ? 0F : 8F;
        } else if (type == BlockPathTypes.WALKABLE && !isInWater()) {
            return 0F;
        } else {
            return super.getPathfindingMalus(type);
        }
    }

    @Override
    public int getMaxAirSupply() {
        return 1200;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        Path path = getNavigation().getPath();
        if ((swimAction == DIVE || swimAction == LEAP) && (!isInWater() || getDeltaMovement().y > 0D)) {
            getNavigation().stop();
            moveControl.setWantedPosition(getX(), getY(), getZ(), 0D);
        } else if (onGround() && !isInWaterOrBubble() && swimAction == 0 && path != null && !path.isDone()) {
            int end = Math.min(path.getNodeCount(), path.getNextNodeIndex() + 3);
            for (int index = path.getNextNodeIndex(); index < end; index++) {
                BlockPos target = path.getNodePos(index);
                if (level().getFluidState(target).is(FluidTags.WATER)
                        && level().getFluidState(target.below()).is(FluidTags.WATER)) {
                    Vec3 landing = path.getEntityPosAtNode(this, index);
                    Vec3 forward = landing.subtract(position()).multiply(1D, 0D, 1D).normalize();
                    BlockPos edge = BlockPos.containing(position().add(forward.scale(0.9D)).add(0D, -0.5D, 0D));
                    while (edge.getY() > target.getY() && level().isEmptyBlock(edge)) {
                        edge = edge.below();
                    }
                    if (landing.y < getY() + 0.5D && level().getFluidState(edge).is(FluidTags.WATER)) {
                        startWaterJump(landing, DIVE);
                        if (swimAction == DIVE) {
                            moveControl.setWantedPosition(getX(), getY(), getZ(), 0D);
                        }
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void travel(Vec3 input) {
        if (isEffectiveAi() && isInWater()) {
            moveRelative(getSpeed(), input);
            move(MoverType.SELF, getDeltaMovement());
            setDeltaMovement(getDeltaMovement().scale(0.9D));
            Path path = getNavigation().getPath();
            if (horizontalCollision && !isUnderWater() && swimAction == 0 && path != null && !path.isDone()
                    && level().getFluidState(path.getTarget()).isEmpty()) {
                Vec3 shoreTarget = path.getNextEntityPos(this);
                BlockPos shorePos = BlockPos.containing(shoreTarget);
                if (shoreTarget.y >= getY() && shoreTarget.distanceToSqr(position()) < 16D
                        && level().getFluidState(shorePos).isEmpty()
                        && !level().getBlockState(shorePos.below()).getCollisionShape(level(), shorePos.below()).isEmpty()) {
                    startWaterJump(shoreTarget, LEAP);
                }
            }
            if (getNavigation().isDone() && swimAction == 0 && isUnderWater()) {
                setDeltaMovement(getDeltaMovement().add(0D, 0.004D, 0D));
            }
        } else {
            super.travel(input);
            if (swimAction == LEAP && landProgress >= 0.7F && !onGround() && !isInWaterOrBubble()) {
                Vec3 motion = getDeltaMovement();
                if (motion.y < 0D) {
                    setDeltaMovement(motion.multiply(1D, 1D - 0.25D * landProgress, 1D));
                }
            }
        }
    }

    public int getSwimAction() {
        return swimAction;
    }

    public void startWaterJump(Vec3 landing, int action) {
        Vec3 gap = landing.subtract(position());
        if (swimAction == 0) {
            boolean shoreExit = action == LEAP && level().getFluidState(BlockPos.containing(landing)).isEmpty();
            for (int attempt = 0; attempt < (shoreExit ? 3 : 1); attempt++) {
                double verticalSpeed = action == DIVE ? 0.22D : attempt < 2 ? 0.36D * Math.pow(0.5D, attempt)
                        : Mth.clamp(gap.y + 0.03D, 0.08D, 0.16D);
                double peak = verticalSpeed * verticalSpeed / 0.16D + verticalSpeed * 0.5D;
                double arcHeight = Math.max(0D, peak - gap.y * 0.5D);
                boolean clear = true;
                for (int step = 1; step <= 4; step++) {
                    double phase = step / 4D;
                    Vec3 offset = gap.scale(phase).add(0D, 4D * arcHeight * phase * (1D - phase), 0D);
                    clear &= level().noCollision(this, getBoundingBox().move(offset));
                }
                if (clear && (action == DIVE || gap.y <= verticalSpeed - 0.02D)) {
                    Vec3 forward = gap.multiply(1D, 0D, 1D).normalize();
                    double flightTicks = (verticalSpeed + Math.sqrt(Math.max(0D, verticalSpeed * verticalSpeed - 0.16D * gap.y))) / 0.08D;
                    double dragDistance = (1D - Math.pow(0.91D, flightTicks)) / 0.09D;
                    double speed = Math.min(0.38D, gap.horizontalDistance() / Math.max(1D, dragDistance));
                    float yaw = (float)(Mth.atan2(-forward.x, forward.z) * Mth.RAD_TO_DEG);
                    setYRot(yaw);
                    yBodyRot = yaw;
                    setYHeadRot(yaw);
                    getNavigation().stop();
                    setOnGround(false);
                    setDeltaMovement(forward.x * speed, verticalSpeed, forward.z * speed);
                    hasImpulse = true;
                    startSwimAction(action);
                    playSound(SoundEvents.GENERIC_SPLASH, 0.7F, 1.3F);
                    break;
                }
            }
        }
    }

    public void startSwimAction(int action) {
        swimAction = action;
        swimEnd = tickCount + (action == SHAKE ? LAND_TICKS + SHAKE_TICKS : action == CATCH ? 12 : 24);
        if (level().isClientSide) {
            swimActionState.start(tickCount);
        } else {
            level().broadcastEntityEvent(this, (byte)(SWIM_EVENT + action - 1));
        }
    }

    public BlockPos getHomePos() { return homePos; }

    public void setHomePos(BlockPos homePos) { this.homePos = homePos.immutable(); }

    public SimpleContainer getFoodInventory() { return foodInventory; }

    // public FishRackBlockEntity getFishRack() {
        // return colonyPos != null && level().hasChunkAt(colonyPos) && level().getBlockEntity(colonyPos) instanceof FishRackBlockEntity rack ? rack : null;
    // }

    // public PinginRole getColonyRole() { return colonyRole == null ? PinginRole.FISHER : colonyRole; }

    // public void setColonyRole(PinginRole role) { colonyRole = role; }

    // public BlockPos getColonyPos() { return colonyPos; }

    // public void setColonyPos(BlockPos pos) { colonyPos = pos.immutable(); }

    // public BlockPos getNestPos() { return nestPos; }

    // public void setNestPos(BlockPos pos) { nestPos = pos.immutable(); }

    // public int getWork() { return entityData.get(WORK); }

    // public void setWork(int work) { entityData.set(WORK, work); }

    // @Override
    // public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
        // super.onSyncedDataUpdated(data);
        // if (WORK.equals(data)) {
            // if (getWork() > 0) {
                // workAnimationState.start(tickCount);
            // } else {
                // workAnimationState.stop();
            // }
        // }
    // }

    // public long getNextPreenTime() { return nextPreenTime; }

    // public void setNextPreenTime(long time) { nextPreenTime = time; }

    public boolean isParentOf(PinginEntity pinginBaby) {
        return getUUID().equals(pinginBaby.parentId) || getUUID().equals(pinginBaby.otherParentId);
    }

    public PinginEntity getParent() {
        if (level() instanceof ServerLevel server) {
            Entity parent = parentId == null ? null : server.getEntity(parentId);
            if (parent instanceof PinginEntity pingin && pingin.isAlive()) {
                return pingin;
            } else {
                Entity other = otherParentId == null ? null : server.getEntity(otherParentId);
                if (other instanceof PinginEntity pingin && pingin.isAlive()) {
                    return pingin;
                }
            }
        }
        return null;
    }

    @Override
    public boolean hurt(DamageSource damage, float amount) {
        boolean hurt = super.hurt(damage, amount);
        if (hurt && !level().isClientSide && damage.getEntity() instanceof LivingEntity attacker && !(attacker instanceof PinginEntity)) {
            if (attacker instanceof Player player) {
                gossip.add(player.getUUID(), GossipType.MAJOR_NEGATIVE, 25);
            }
            for (PinginEntity pingin : level().getEntitiesOfClass(PinginEntity.class, getBoundingBox().inflate(8D),
                    pingin -> pingin != this && pingin.isAlive() && pingin.getSensing().hasLineOfSight(this))) {
                if (attacker instanceof Player) {
                    pingin.gossip.transferFrom(gossip, random, 10);
                }
                LivingEntity target = pingin.getTarget();
                if (pingin.canFight() && pingin.canAttack(attacker) && !pingin.isAlliedTo(attacker)
                        && (target == null || pingin.distanceToSqr(attacker) < pingin.distanceToSqr(target))) {
                    pingin.setTarget(attacker);
                }
            }
            // FishRackBlockEntity rack = getFishRack();
            // if (rack != null) {
                // rack.alarm(attacker);
            // }
        }
        return hurt;
    }

    public boolean wantsFish() {
        return isBaby() ? level().getGameTime() >= nextMealTime : getHealth() < getMaxHealth();
    }

    public void eatFish() {
        if (onGround() && !isInWaterOrBubble() && wantsFish()) {
            ItemStack fish = ItemStack.EMPTY;
            if (getMainHandItem().is(ItemTags.FISHES)) {
                fish = getMainHandItem().split(1);
            } else if (hasWeapon()) {
                for (int slot = 0; slot < foodInventory.getContainerSize(); slot++) {
                    if (foodInventory.getItem(slot).is(ItemTags.FISHES)) {
                        fish = foodInventory.removeItem(slot, 1);
                        break;
                    }
                }
                // FishRackBlockEntity rack = getFishRack();
                // if (fish.isEmpty() && rack != null && homePos != null && distanceToSqr(Vec3.atBottomCenterOf(homePos)) <= 4D) {
                    // fish = rack.takeFish();
                // }
            }
            if (fish.is(ItemTags.FISHES)) {
                heal(2F);
                if (isBaby()) {
                    ageUp(AgeableMob.getSpeedUpSecondsWhenFeeding(-getAge()), true);
                    nextMealTime = level().getGameTime() + 1200L;
                }
            }
        }
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob mate) {
        PinginEntity pinginBaby = ModEntities.PINGIN.get().create(serverLevel);
        if (pinginBaby != null && homePos != null) {
            pinginBaby.setHomePos(homePos);
        }
        if (pinginBaby != null) {
            pinginBaby.parentId = getUUID();
            pinginBaby.otherParentId = mate.getUUID();
            // pinginBaby.colonyPos = colonyPos;
            // pinginBaby.nestPos = nestPos;
        }
        return pinginBaby;
    }

    @Override
    public boolean isFood(ItemStack pStack) {
        return false;
    }

    public UUID getRecruiterId() { return recruitId; }

    public boolean isRecruited() { return recruitId != null && level().getGameTime() < recruitEnd; }

    public Player getRecruiter() { return isRecruited() ? level().getPlayerByUUID(recruitId) : null; }

    public boolean likesPlayer(Player player) { return gossip.getReputation(player.getUUID(), type -> true) >= 0; }

    public void recruit(Player player) {
        recruitId = player.getUUID();
        recruitEnd = level().getGameTime() + RECRUIT_TICKS;
        recruitWarned = false;
        if (homePos == null) {
            homePos = blockPosition();
        }
        gossip.add(recruitId, GossipType.MINOR_POSITIVE, 5);
        heal(2F);
        setTarget(null);
        getNavigation().stop();
        playSound(SoundEvents.FOX_EAT, 0.6F, 1.3F);
    }

    public void finishRecruitment() {
        recruitId = null;
        recruitEnd = 0L;
        recruitWarned = false;
    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        // if (entity instanceof PinginEntity pingin && (colonyPos != null && colonyPos.equals(pingin.colonyPos)
                // || isRecruited() && pingin.isRecruited() && recruitId.equals(pingin.recruitId))) {
        if (entity instanceof PinginEntity pingin && isRecruited() && pingin.isRecruited()
                && recruitId.equals(pingin.recruitId)) {
            return true;
        } else if (isRecruited() && (recruitId.equals(entity.getUUID())
                || entity instanceof TamableAnimal animal && recruitId.equals(animal.getOwnerUUID()))) {
            return true;
        } else {
            return super.isAlliedTo(entity);
        }
    }

    public void startSliding(int duration) {
        this.slideTicks = Math.max(0, duration);
        this.entityData.set(SLIDING, this.slideTicks > 0);
    }

    public void stopSliding() {
        this.startSliding(0);
    }

    public int getSlideTicks() {
        return slideTicks;
    }

    public static boolean isWeapon(ItemStack stack) {
        return stack.getItem() instanceof SwordItem || stack.getItem() instanceof AxeItem
                || stack.getItem() instanceof ThrowingSpearItem || stack.is(WEAPONS);
    }

    public boolean hasWeapon() { return isWeapon(getMainHandItem()) || isWeapon(getOffhandItem()); }

    public boolean wantsWeapon(ItemStack stack) {
        if (!isBaby() && isWeapon(stack) && (getMainHandItem().isEmpty() || hasWeapon())) {
            boolean spear = stack.getItem() instanceof ThrowingSpearItem;
            ItemStack held = getMainHandItem().isEmpty() ? getOffhandItem() : getMainHandItem();
            boolean heldSpear = held.getItem() instanceof ThrowingSpearItem;
            return (getMainHandItem().isEmpty() || getOffhandItem().isEmpty()) && (held.isEmpty() || spear != heldSpear);
        } else {
            return false;
        }
    }

    public boolean equipWeapon(ItemStack stack) {
        if (wantsWeapon(stack)) {
            ItemStack held = getMainHandItem().isEmpty() ? getOffhandItem() : getMainHandItem();
            setItemInHand(InteractionHand.OFF_HAND, held);
            setItemInHand(InteractionHand.MAIN_HAND, stack.copyWithCount(1));
            setDropChance(EquipmentSlot.MAINHAND, 1F);
            setDropChance(EquipmentSlot.OFFHAND, 1F);
            // colonyRole = PinginRole.GUARD;
            return true;
        } else {
            return false;
        }
    }

    public boolean isAttacking() { return tickCount < attackAnimationEndTick + 2; }

    @Override
    public void knockback(double strength, double x, double z) {
        if (canFight() && isAttacking() && onGround() && !isInWater()) {
            strength *= 0.5D;
        }
        super.knockback(strength, x, z);
    }

    public boolean canFight() {
        return hasWeapon() && !isBaby() && getHealth() > getMaxHealth() * 0.5F;
    }

    public PinginAttack getAttackType() {
        return attackType;
    }

    public float getJabReach() {
        return this.entityData.get(JAB_REACH);
    }

    public PinginAttack startAttackAnimation() {
        List<PinginAttack> attacks = PinginAttack.HANDHELD;
        if (getMainHandItem().getItem() instanceof ThrowingSpearItem) {
            attacks = PinginAttack.SPEARS;
        }
        LivingEntity target = getTarget();
        if (target != null && (distanceToSqr(target) < 4D || target instanceof Mob mob && mob.getTarget() == this)) {
            attacks = attacks.stream().filter(attack -> attack.hitTick <= 9).toList();
        }
        int index = this.random.nextInt(attacks.size());
        if (attacks.get(index) == this.attackType) {
            index = (index + 1 + this.random.nextInt(attacks.size() - 1)) % attacks.size();
        }
        return startAttackAnimation(attacks.get(index));
    }

    public PinginAttack startAttackAnimation(PinginAttack attack) {
        if (attack.spear && attack != PinginAttack.THROW && getMainHandItem().getItem() instanceof ThrowingSpearItem spear) {
            LivingEntity target = getTarget();
            float reach = 0.5F;
            if (target != null) {
                double gap = target.position().subtract(position()).horizontalDistance() - target.getBbWidth() * 0.5D;
                reach = Mth.clamp((float)((gap - getBbWidth()) / (spear.getReach(this) - getBbWidth())), 0F, 1F);
            }
            this.entityData.set(JAB_REACH, reach);
        }
        this.attackType = attack;
        this.attackAnimationEndTick = this.tickCount + attack.duration;
        this.level().broadcastEntityEvent(this, (byte)(ATTACK_EVENT + this.attackType.ordinal()));
        return this.attackType;
    }

    @Override
    public void handleEntityEvent(byte event) {
        if (event >= ATTACK_EVENT && event < ATTACK_EVENT + ATTACKS.length) {
            this.attackType = ATTACKS[event - ATTACK_EVENT];
            this.attackAnimationState.start(this.tickCount);
            this.attackAnimationEndTick = this.tickCount + this.attackType.duration;
        } else if (event >= SWIM_EVENT && event < SWIM_EVENT + 4) {
            startSwimAction(event - SWIM_EVENT + 1);
        } else if (event == SWIM_STOP_EVENT) {
            swimEnd = tickCount;
        } else {
            super.handleEntityEvent(event);
        }
    }

    public static boolean canSpawn(EntityType<PinginEntity> entityType, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random){
        return checkMobSpawnRules(entityType, level, spawnType, pos, random);
    }

    public SimpleContainer getTradeContainer() {
        return tradeContainer;
    }

    public boolean isAdmiring() {
        return this.entityData.get(ADMIRING);
    }

    public void setAdmiring(boolean value) {
        this.entityData.set(ADMIRING, value);
    }

    public boolean isOnIce() {
        BlockState ground = level().getBlockState(blockPosition().below());
        return ground.is(Blocks.ICE) || ground.is(Blocks.PACKED_ICE) || ground.is(Blocks.BLUE_ICE);
    }

    public boolean isSliding() {
        return this.entityData.get(SLIDING);
    }
}

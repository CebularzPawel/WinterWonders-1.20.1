package net.cebularz.winterwonders.entity.custom;

import net.cebularz.winterwonders.entity.ai.pingin.PinginTradeWithPlayerGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.cebularz.winterwonders.entity.ai.pingin.SlideOnIceGoal;
import net.cebularz.winterwonders.entity.ModEntities;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PinginEntity extends Animal {
    private Player tradingPlayer;
    public float admireProgress;
    public float lastAdmireProgress;
    private final List<Item> resultItems = new ArrayList<>();
    private final SimpleContainer tradeContainer;
    private static final EntityDataAccessor<Boolean> ADMIRING = SynchedEntityData.defineId(PinginEntity.class,EntityDataSerializers.BOOLEAN);
    public PinginEntity(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.tradeContainer = new SimpleContainer(20);
        setResultItems();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 2.0D));

        this.goalSelector.addGoal(2, new BreedGoal(this, 1.15D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.15D, Ingredient.of(Items.SALMON), false));

        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(4, new PinginTradeWithPlayerGoal(this,
                List.of(Items.ICE, Items.PACKED_ICE, Items.BLUE_ICE, Items.SNOWBALL, Items.WOODEN_SWORD)));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 0.75D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 3f));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.goalSelector.addGoal(8, new SlideOnIceGoal(this, 1.2D));
    }

    public AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    public AnimationState slideAnimationState = new AnimationState();
    public int slideTimer = 0;
    public final int slideDuration = 40;

    @Override
    protected void updateWalkAnimation(float pPartialTick) {
        float f;
        if(this.getPose() == Pose.STANDING) {
            f = Math.min(pPartialTick * 6F, 1f);
        } else {
            f = 0f;
        }

        this.walkAnimation.update(f, 0.2f);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ADMIRING,false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("admiring",this.isAdmiring());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setAdmiring(pCompound.getBoolean("admiring"));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide){
            setupAnimationStates();
        }
    }

    private void setupAnimationStates(){
        if(this.idleAnimationTimeout <= 0){
            this.idleAnimationTimeout = 40;
            this.idleAnimationState.start(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }

        if (this.slideTimer <= 0 && this.isOnIce() && this.random.nextFloat() < 0.02F) {
            this.slideTimer = slideDuration;
            this.slideAnimationState.start(this.tickCount);
        }

        if (this.slideTimer > 0) {
            this.slideTimer--;
            if (this.slideTimer <= 0) {
                this.slideAnimationState.stop();
            }
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
                .add(Attributes.FOLLOW_RANGE, 24D);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return ModEntities.PINGIN.get().create(serverLevel);
    }

    @Override
    public boolean isFood(ItemStack pStack) {
        return pStack.is(Items.SALMON);
    }

    public void startSliding(int duration) {
        this.slideTimer = duration;
    }

    public static boolean canSpawn(EntityType<PinginEntity> entityType, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random){
        return checkMobSpawnRules(entityType, level, spawnType, pos, random);
    }

    public Player getTradingPlayer() {
        return tradingPlayer;
    }

    public void setTradingPlayer(Player tradingPlayer) {
        this.tradingPlayer = tradingPlayer;
    }

    public List<Item> getResultItems() {
        return Collections.unmodifiableList(resultItems);
    }

    public SimpleContainer getTradeContainer() {
        return tradeContainer;
    }

    public boolean isAdmiring() {
        return this.entityData.get(ADMIRING);
    }

    public void setAdmiring(boolean value) {
        this.entityData.set(ADMIRING,value);
    }

    private void setResultItems() {
        this.addItemsToList(Items.ENCHANTED_BOOK);
        this.addItemsToList(Items.APPLE);
        this.addItemsToList(Items.GOLD_BLOCK);

        for(int i=0; i<this.resultItems.size(); i++) {
            this.getTradeContainer().setItem(i, new ItemStack(this.resultItems.get(i)));
        }
    }

    private void addItemsToList(Item pItem)
    {
        this.resultItems.add(pItem);
    }

    public boolean isOnIce() {
        BlockPos pos = this.blockPosition().below();
        return this.level().getBlockState(pos).is(net.minecraft.world.level.block.Blocks.ICE)
                || this.level().getBlockState(pos).is(net.minecraft.world.level.block.Blocks.PACKED_ICE)
                || this.level().getBlockState(pos).is(net.minecraft.world.level.block.Blocks.BLUE_ICE);
    }

    public boolean isSliding() {
        return this.slideTimer > 0;
    }
}

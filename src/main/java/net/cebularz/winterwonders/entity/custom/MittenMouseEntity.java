package net.cebularz.winterwonders.entity.custom;

import net.cebularz.winterwonders.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MittenMouseEntity extends Animal {
    private static final EntityDataAccessor<Integer> VARIANT =
            SynchedEntityData.defineId(MittenMouseEntity.class, EntityDataSerializers.INT);
    public final AnimationState idleAnimationState = new AnimationState();

    private enum TreatStep {
        NONE, COOKIE_GIVEN
    }

    private final Map<UUID, TreatStep> treatSteps = new HashMap<>();
    private final Map<UUID, Integer> treatTimeouts = new HashMap<>();

    private static final int TREAT_TIMEOUT_TICKS = 20 * 10;

    private int giftCooldownTicks = 0;
    private static final int commonGiftPremiumChance = 35;
    private static final int uncommonGiftPremiumChance = 40;
    private static final int rareGiftPremiumChance = 20;
    private static final int epicGiftPremiumChance = 5;

    private static final int commonGiftChance = 70;
    private static final int uncommonGiftChance = 22;
    private static final int rareGiftChance = 7;
    private static final int epicGiftChance = 1;

    private static final Ingredient FOOD = Ingredient.of(
            Items.SWEET_BERRIES,
            ModItems.SUGAR_COOKIE.get(),
            Items.MILK_BUCKET
    );

    public MittenMouseEntity(EntityType<? extends Animal > type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.25D));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.1D, FOOD, false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 8.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.30D)
                .add(Attributes.FOLLOW_RANGE, 12.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(VARIANT, 0);
    }

    public int getVariant() {
        return this.entityData.get(VARIANT);
    }

    public void setVariant(int variant) {
        this.entityData.set(VARIANT, variant);
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType mobSpawnType,
            @Nullable SpawnGroupData spawnData, @Nullable CompoundTag nbtData) {
        SpawnGroupData spawnGroupData = super.finalizeSpawn(levelAccessor, difficulty, mobSpawnType, spawnData, nbtData);

        if (nbtData == null || !nbtData.contains("Variant")) {
            this.setVariant(levelAccessor.getRandom().nextInt(3));
        }

        return spawnGroupData;
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(@NotNull ServerLevel level, @NotNull AgeableMob otherParent) {
        return null;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            if (giftCooldownTicks > 0) giftCooldownTicks--;

            if (!treatTimeouts.isEmpty()) {
                treatTimeouts.replaceAll((uuid, countdown) -> countdown - 1);
                treatTimeouts.entrySet().removeIf(entity -> {
                    if (entity.getValue() <= 0) {
                        treatSteps.remove(entity.getKey());
                        return true;
                    }
                    return false;
                });
            }
        }

        if (this.level().isClientSide()) {
            this.setupIdleAnimation();
        }
    }

    private void setupIdleAnimation() {
        if (this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6D) {
            this.idleAnimationState.stop();
        } else if (!this.idleAnimationState.isStarted()) {
            this.idleAnimationState.start(this.tickCount);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Variant", this.getVariant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Variant")) {
            this.setVariant(tag.getInt("Variant"));
        }
    }

    @Override
    public boolean isFood(@NotNull ItemStack foodStack) {
        return FOOD.test(foodStack);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack inHand = player.getItemInHand(hand);
        if (this.level().isClientSide()) {
            if (inHand.is(Items.SWEET_BERRIES) || inHand.is(ModItems.SUGAR_COOKIE.get()) || inHand.is(Items.MILK_BUCKET)) {
                return InteractionResult.SUCCESS;
            }
            return super.mobInteract(player, hand);
        }

        if (giftCooldownTicks > 0) return InteractionResult.CONSUME;
        UUID playerUUID = player.getUUID();
        TreatStep treatStep = treatSteps.getOrDefault(playerUUID, TreatStep.NONE);

        if (inHand.is(Items.SWEET_BERRIES)) {
            if (!player.getAbilities().instabuild) inHand.shrink(1);

            //ItemStack gift = rollGift(false);
            //dropGiftNearby(gift, player);
            treatSteps.remove(playerUUID);
            treatTimeouts.remove(playerUUID);

            giftCooldownTicks = 40;
            return InteractionResult.CONSUME;
        }

        if (inHand.is(ModItems.SUGAR_COOKIE.get())) {
            if (treatStep == TreatStep.COOKIE_GIVEN) {
                player.displayClientMessage(Component.literal("The mouse is waiting for milk."), true);
                return InteractionResult.CONSUME;
            }

            if (!player.getAbilities().instabuild) inHand.shrink(1);
            treatSteps.put(playerUUID, TreatStep.COOKIE_GIVEN);
            treatTimeouts.put(playerUUID, TREAT_TIMEOUT_TICKS);

            player.displayClientMessage(Component.literal("The mouse accepts the cookie and looks expectantly for milk."), true);
            return InteractionResult.CONSUME;
        }

        if (inHand.is(Items.MILK_BUCKET)) {
            if (treatStep != TreatStep.COOKIE_GIVEN) {
                player.displayClientMessage(Component.literal("The mouse refuses. It wants a sugar cookie first."), true);
                return InteractionResult.CONSUME;
            }

            if (!player.getAbilities().instabuild) {
                inHand.shrink(1);
                player.getInventory().add(new ItemStack(Items.BUCKET));
            }

            //ItemStack gift = rollGift(true);
            //dropGiftNearby(gift, player);
            treatSteps.remove(playerUUID);
            treatTimeouts.remove(playerUUID);

            giftCooldownTicks = 40;
            return InteractionResult.CONSUME;
        }

        return super.mobInteract(player, hand);
    }

    //private ItemStack rollGift(boolean premium) {
    //    int commonGift = !premium ? commonGiftChance : commonGiftPremiumChance;
    //    int uncommonGift = !premium ? uncommonGiftChance : uncommonGiftPremiumChance;
    //    int rareGift = !premium ? rareGiftChance : rareGiftPremiumChance;
    //    int epicGift = !premium ? epicGiftChance : epicGiftPremiumChance;
//
    //    int totalWeight = commonGift + uncommonGift + rareGift + epicGift;
    //    int rolledWeight = this.random.nextInt(totalWeight);
//
    //    if ((rolledWeight -= commonGift) < 0) {
    //        return new ItemStack(ModItems.COMMON_GIFT.get());
    //    }
    //    if ((rolledWeight -= uncommonGift) < 0) {
    //        return new ItemStack(ModItems.UNCOMMON_GIFT.get());
    //    }
    //    if (rolledWeight - rareGift < 0) {
    //        return new ItemStack(ModItems.RARE_GIFT.get());
    //    }
    //    return new ItemStack(ModItems.EPIC_GIFT.get());
    //}

    private void dropGiftNearby(ItemStack itemStack, Player player) {
        if (this.level().isClientSide()) return;
        Vec3 from = this.position().add(0.0D, 0.25D, 0.0D);
        Vec3 to = player.position().add(0.0D, player.getBbHeight(), 0.0D);
        Vec3 direction = to.subtract(from).normalize();
        double speed = 0.15D;
        Vec3 vector = direction.scale(speed).add(0.0D, 0.02D, 0.0D);

        ItemEntity itemEntity = new ItemEntity(
                this.level(),
                from.x,
                from.y,
                from.z,
                itemStack.copy()
        );

        itemEntity.setDeltaMovement(vector);
        itemEntity.setPickUpDelay(10);
        this.level().addFreshEntity(itemEntity);
    }
}

package net.cebularz.winterwonders.entity.custom;

import net.cebularz.winterwonders.entity.ai.LichStaffAttackGoal;
import net.cebularz.winterwonders.entity.custom.base.IStaffHoldingMob;
import net.cebularz.winterwonders.init.ModItems;
import net.cebularz.winterwonders.item.custom.LichBlizzardStaffItem;
import net.cebularz.winterwonders.item.custom.impl.IStaffItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LichEntity extends Monster implements IStaffHoldingMob, RangedAttackMob {
    private static final double MIN_RANGED_ATTACK_DISTANCE = 5.0D;
    private static final double MAX_RANGED_ATTACK_DISTANCE = 32.0D;

    private int attackCooldown = 0;

    public LichEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier.Builder createAttribute() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.328D)
                .add(Attributes.ATTACK_KNOCKBACK, 1.2D)
                .add(Attributes.ATTACK_SPEED, 0.89D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.MAX_HEALTH, 80.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 1.5D)
                .add(Attributes.ARMOR, 1.2D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.0D, 20, 40, 16.0F));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public IStaffItem getStaffItem() {
        return (IStaffItem) ModItems.LICH_BLIZZARD_STAFF.get();
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
    }

    public boolean isOnAttackCooldown() {
        return attackCooldown > 0;
    }

    public void setAttackCooldown(int cooldown) {
        this.attackCooldown = cooldown;
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
        System.out.println("Lich spawned with staff: " + !this.getMainHandItem().isEmpty());
        return pSpawnData;
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        IStaffItem staffItem = this.getStaffItem();
        if (staffItem instanceof LichBlizzardStaffItem lichStaff) {
            lichStaff.setCaster(this);

            double distance = this.distanceToSqr(target);
            float healthPercentage = this.getHealth() / this.getMaxHealth() * 100f;

            LichStaffAttackGoal.AttackType attackType = selectAttackType(distance, healthPercentage);

            executeAttack(lichStaff, target, attackType);
        }
    }

    private LichStaffAttackGoal.AttackType selectAttackType(double distance, float healthPercentage) {
        RandomSource random = level().getRandom();

        if (healthPercentage <= 50) {
            if (distance < 36.0) {
                return random.nextBoolean() ?
                        LichStaffAttackGoal.AttackType.WHIRLWIND :
                        LichStaffAttackGoal.AttackType.BLIZZARD;
            } else {
                if (distance < 100.0) {
                    return LichStaffAttackGoal.AttackType.TERRAIN_ATTACK_CLOSE;
                } else {
                    return LichStaffAttackGoal.AttackType.TERRAIN_ATTACK_FAR;
                }
            }
        } else {
            if (distance < 36.0) {
                return LichStaffAttackGoal.AttackType.WHIRLWIND;
            } else if (distance < 100.0) {
                return random.nextBoolean() ?
                        LichStaffAttackGoal.AttackType.BASIC_PROJECTILE :
                        LichStaffAttackGoal.AttackType.SPECIAL_ATTACK;
            } else {
                return random.nextBoolean() ?
                        LichStaffAttackGoal.AttackType.BASIC_PROJECTILE :
                        LichStaffAttackGoal.AttackType.TERRAIN_ATTACK_FAR;
            }
        }
    }

    private void executeAttack(LichBlizzardStaffItem staffItem, LivingEntity target,
                               LichStaffAttackGoal.AttackType attackType) {
        switch (attackType) {
            case BASIC_PROJECTILE:
                staffItem.executeProjectileAttack(target, 0);
                break;
            case SPECIAL_ATTACK:
                staffItem.executeSpecialAttack(target);
                break;
            case TERRAIN_ATTACK_CLOSE:
                staffItem.executeTerrainAttack(target, true);
                break;
            case TERRAIN_ATTACK_FAR:
                staffItem.executeTerrainAttack(target, false);
                break;
            case BLIZZARD:
                staffItem.executeBlizzardAttack(target);
                break;
            case WHIRLWIND:
                staffItem.executeWhirlwindAttack(target);
                break;
        }
    }
}
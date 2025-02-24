package net.cebularz.winterwonders.entity.custom;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.cebularz.winterwonders.init.ModEffects;
import net.cebularz.winterwonders.init.ModEntities;
import net.cebularz.winterwonders.init.ModItems;

import javax.annotation.Nullable;

public class RevenantEntity extends Zombie {
    private int lootingLevelOnDeath = 0;
    public RevenantEntity(EntityType<? extends Zombie> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected boolean isSunSensitive() {
        return false;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean didAttack = super.doHurtTarget(target);

        if (didAttack && target instanceof LivingEntity livingEntity) {
            if (livingEntity.hasEffect(ModEffects.CHILLED.get())) {
                livingEntity.addEffect(new MobEffectInstance(ModEffects.CHILLED.get(), 100,
                        livingEntity.getEffect(ModEffects.CHILLED.get()).getAmplifier() + 1));
            } else {
                livingEntity.addEffect(new MobEffectInstance(ModEffects.CHILLED.get(), 100, 0));
            }
        }

        return didAttack;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Zombie.createAttributes();
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world,
                                        DifficultyInstance difficulty,
                                        MobSpawnType spawnReason,
                                        @Nullable SpawnGroupData livingdata,
                                        @Nullable CompoundTag dataTag) {
        livingdata = super.finalizeSpawn(world, difficulty, spawnReason, livingdata, dataTag);
        equipRandomColdsteelWeapon(this, 0.05F);
        equipRandomColdsteelArmor(this);
        return livingdata;
    }

    @Override
    public void die(DamageSource source) {
        if (!this.level().isClientSide()) {
            Entity entity = source.getEntity();
            if (entity instanceof LivingEntity living) {
                this.lootingLevelOnDeath = EnchantmentHelper.getMobLooting(living);
            }
        }
        super.die(source);
    }

    @Override
    public float getEquipmentDropChance(EquipmentSlot slot) {
        float base = super.getEquipmentDropChance(slot);
        float finalChance = base + 0.1F * this.lootingLevelOnDeath;
        return Math.min(finalChance, 1.0F);
    }

    public static void convertZombieToRevenant(Zombie zombieEntity) {
        Level level = zombieEntity.level();
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        RevenantEntity revenantEntity = ModEntities.REVENANT.get().create(serverLevel);
        if (revenantEntity == null) {
            return;
        }

        revenantEntity.moveTo(zombieEntity.getX(), zombieEntity.getY(), zombieEntity.getZ(), zombieEntity.getYRot(), zombieEntity.getXRot());
        revenantEntity.setNoAi(zombieEntity.isNoAi());
        revenantEntity.setBaby(zombieEntity.isBaby());

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack oldStack = zombieEntity.getItemBySlot(slot);
            if (!oldStack.isEmpty()) {
                revenantEntity.setItemSlot(slot, oldStack.copy());
            }
        }

        if (revenantEntity.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
            equipRandomColdsteelWeapon(revenantEntity, 0.05F);
        }

        equipRandomColdsteelArmor(revenantEntity);

        if (zombieEntity.hasCustomName()) {
            revenantEntity.setCustomName(zombieEntity.getCustomName());
            revenantEntity.setCustomNameVisible(zombieEntity.isCustomNameVisible());
        }

        spawnSnowParticles(serverLevel, zombieEntity.getX(), zombieEntity.getY(), zombieEntity.getZ());
        zombieEntity.discard();
        serverLevel.addFreshEntity(revenantEntity);

        serverLevel.playSound(
                revenantEntity,
                revenantEntity.blockPosition(),
                SoundEvents.PLAYER_HURT_FREEZE,
                SoundSource.HOSTILE,
                1f,
                1f
        );
    }

    private static void equipRandomColdsteelWeapon(RevenantEntity revenantEntity, float dropChance) {
            if (revenantEntity.getRandom().nextBoolean()) {
                revenantEntity.setItemSlot(
                        EquipmentSlot.MAINHAND,
                        new ItemStack(ModItems.COLDSTEEL_SWORD.get())
                );
            } else {
                revenantEntity.setItemSlot(
                        EquipmentSlot.MAINHAND,
                        new ItemStack(ModItems.COLDSTEEL_AXE.get())
                );
            }
            revenantEntity.setDropChance(EquipmentSlot.MAINHAND, dropChance);
    }

    private static void equipRandomColdsteelArmor(RevenantEntity revenantEntity) {
        double armorChance = revenantEntity.getRandom().nextDouble();
        if (armorChance < 0.05) {
            equipIfEmpty(revenantEntity, EquipmentSlot.HEAD, ModItems.COLDSTEEL_HELMET.get(), 0.01F);
            equipIfEmpty(revenantEntity, EquipmentSlot.CHEST, ModItems.COLDSTEEL_CHESTPLATE.get(), 0.01F);
            equipIfEmpty(revenantEntity, EquipmentSlot.LEGS, ModItems.COLDSTEEL_LEGGINGS.get(), 0.01F);
            equipIfEmpty(revenantEntity, EquipmentSlot.FEET, ModItems.COLDSTEEL_BOOTS.get(), 0.01F);
        } else if (armorChance < 0.95) {
            EquipmentSlot[] armorSlots = {
                    EquipmentSlot.HEAD,
                    EquipmentSlot.CHEST,
                    EquipmentSlot.LEGS,
                    EquipmentSlot.FEET
            };
            EquipmentSlot chosen = armorSlots[revenantEntity.getRandom().nextInt(armorSlots.length)];
            Item item = switch (chosen) {
                case HEAD -> ModItems.COLDSTEEL_HELMET.get();
                case CHEST -> ModItems.COLDSTEEL_CHESTPLATE.get();
                case LEGS -> ModItems.COLDSTEEL_LEGGINGS.get();
                case FEET -> ModItems.COLDSTEEL_BOOTS.get();
                default -> Items.AIR;
            };
            equipIfEmpty(revenantEntity, chosen, item, 0.02F);
        }
    }

    private static void equipIfEmpty(RevenantEntity revenantEntity, EquipmentSlot slot, Item item, float dropChance) {
        if (revenantEntity.getItemBySlot(slot).isEmpty()) {
            revenantEntity.setItemSlot(slot, new ItemStack(item));
            revenantEntity.setDropChance(slot, dropChance);
        }
    }

    private static void spawnSnowParticles(ServerLevel serverLevel, double x, double y, double z) {
        int count = 40;
        for (int i = 0; i < count; i++) {
            double offsetX = (serverLevel.random.nextDouble() - 0.5);
            double offsetY = serverLevel.random.nextDouble() * 1.95;
            double offsetZ = (serverLevel.random.nextDouble() - 0.5);

            serverLevel.sendParticles(
                    ParticleTypes.SNOWFLAKE,
                    x + offsetX,
                    y + offsetY,
                    z + offsetZ,
                    1,
                    0, 0, 0,
                    0.01
            );
        }
    }
}

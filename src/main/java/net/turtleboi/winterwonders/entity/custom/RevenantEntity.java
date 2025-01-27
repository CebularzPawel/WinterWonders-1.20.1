package net.turtleboi.winterwonders.entity.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.turtleboi.winterwonders.init.ModItems;

import javax.annotation.Nullable;

public class RevenantEntity extends Zombie {
    public RevenantEntity(EntityType<? extends Zombie> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean didAttack = super.doHurtTarget(target);

        if (didAttack && target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 0));
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
        if (this.random.nextBoolean()) {
            this.setItemSlot(EquipmentSlot.MAINHAND,
                    new ItemStack(ModItems.COLDSTEEL_SWORD.get()));
        } else {
            this.setItemSlot(EquipmentSlot.MAINHAND,
                    new ItemStack(ModItems.COLDSTEEL_AXE.get()));
        }
        this.setDropChance(EquipmentSlot.MAINHAND, 0.05F);

        double armorChance = this.random.nextDouble();
        if (armorChance < 0.05) {
            setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.DIAMOND_HELMET));
            setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.DIAMOND_CHESTPLATE));
            setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.DIAMOND_LEGGINGS));
            setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.DIAMOND_BOOTS));
            this.setDropChance(EquipmentSlot.HEAD, 0.01F);
            this.setDropChance(EquipmentSlot.CHEST, 0.01F);
            this.setDropChance(EquipmentSlot.LEGS, 0.01F);
            this.setDropChance(EquipmentSlot.FEET, 0.01F);

        } else if (armorChance < 0.95) {
            EquipmentSlot[] armorSlots = {
                    EquipmentSlot.HEAD,
                    EquipmentSlot.CHEST,
                    EquipmentSlot.LEGS,
                    EquipmentSlot.FEET
            };
            EquipmentSlot chosenSlot = armorSlots[this.random.nextInt(armorSlots.length)];
            this.setItemSlot(chosenSlot, pickArmor(chosenSlot));
            this.setDropChance(chosenSlot, 0.02F);

        } else {

        }

        return livingdata;
    }

    private ItemStack pickArmor(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> new ItemStack(Items.DIAMOND_HELMET);
            case CHEST -> new ItemStack(Items.DIAMOND_CHESTPLATE);
            case LEGS -> new ItemStack(Items.DIAMOND_LEGGINGS);
            case FEET -> new ItemStack(Items.DIAMOND_BOOTS);
            default -> ItemStack.EMPTY;
        };
    }

    private int lootingLevelOnDeath = 0;

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
}

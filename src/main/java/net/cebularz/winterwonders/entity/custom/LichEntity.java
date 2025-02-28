package net.cebularz.winterwonders.entity.custom;

import net.cebularz.winterwonders.init.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

public class LichEntity extends Monster {
    public LichEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
                                                  MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        pSpawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        this.setItemSlot(
                EquipmentSlot.MAINHAND,
                new ItemStack(ModItems.BLIZZARD_STAFF.get())
        );
        return pSpawnData;
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }
}

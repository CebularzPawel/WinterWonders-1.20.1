package net.cebularz.winterwonders.init;

import com.mojang.serialization.Codec;
import net.cebularz.winterwonders.WinterWonders;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;

public class ModMemories
{
    public static final DeferredRegister<MemoryModuleType<?>> MEMORY_MODULES = DeferredRegister.create(ForgeRegistries.MEMORY_MODULE_TYPES, WinterWonders.MOD_ID);

    public static final RegistryObject<MemoryModuleType<BlockPos>> ATTACK_TARGET_LOCATION = MEMORY_MODULES
            .register("attack_target_location",()-> new MemoryModuleType<>(Optional.of(BlockPos.CODEC)));

    public static final RegistryObject<MemoryModuleType<Boolean>> LICH_CAN_CAST = MEMORY_MODULES.register(
            "lich_can_cast", () -> new MemoryModuleType<>(Optional.empty()));
}

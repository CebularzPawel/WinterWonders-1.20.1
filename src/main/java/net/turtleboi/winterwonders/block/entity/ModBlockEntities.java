package net.turtleboi.winterwonders.block.entity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.turtleboi.winterwonders.WinterWonders;
import net.turtleboi.winterwonders.block.ModBlocks;

public class ModBlockEntities{
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, WinterWonders.MOD_ID);

    public static final RegistryObject<BlockEntityType<LapidaristTableBlockEntity>> LAPIDARIST_TABLE_BE =
            BLOCK_ENTITIES.register("lapidarist_table_be", () ->
                    BlockEntityType.Builder.of(LapidaristTableBlockEntity::new,
                            ModBlocks.LAPIDARIST_TABLE.get()).build(null));

    public static void register(IEventBus eventBus){
        BLOCK_ENTITIES.register(eventBus);
    }
}

package net.cebularz.winterwonders.block.entity;

import net.cebularz.winterwonders.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.block.entity.custom.IcyVinesBlockEntity;
import net.cebularz.winterwonders.block.entity.custom.IcyVinesPlantBlockEntity;

public class ModBlockEntities{
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, WinterWonders.MOD_ID);

    public static final RegistryObject<BlockEntityType<IcyVinesBlockEntity>> ICY_VINES_BE =
            BLOCK_ENTITIES.register("icy_vines_be", () ->
                    BlockEntityType.Builder.of(IcyVinesBlockEntity::new,
                            ModBlocks.ICY_VINES.get()).build(null));

    public static final RegistryObject<BlockEntityType<IcyVinesPlantBlockEntity>> ICY_VINES_PLANT_BE =
            BLOCK_ENTITIES.register("icy_vines_plant_be", () ->
                    BlockEntityType.Builder.of(IcyVinesPlantBlockEntity::new,
                            ModBlocks.ICY_VINES_PLANT.get()).build(null));

    public static void register(IEventBus eventBus){
        BLOCK_ENTITIES.register(eventBus);
    }
}

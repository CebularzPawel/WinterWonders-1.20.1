package net.turtleboi.winterwonders.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.turtleboi.winterwonders.init.ModBlockEntities;

public class IcyVinesPlantBlockEntity extends BlockEntity {
    public IcyVinesPlantBlockEntity(BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(ModBlockEntities.ICY_VINES_PLANT_BE.get(), pos, state);
    }
}

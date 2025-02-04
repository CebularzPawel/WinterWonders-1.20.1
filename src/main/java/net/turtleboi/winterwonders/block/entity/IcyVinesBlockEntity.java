package net.turtleboi.winterwonders.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.turtleboi.winterwonders.init.ModBlockEntities;
import net.turtleboi.winterwonders.init.ModBlocks;

public class IcyVinesBlockEntity extends BlockEntity {
    public IcyVinesBlockEntity(BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(ModBlockEntities.ICY_VINES_BE.get(), pos, state);
    }
}

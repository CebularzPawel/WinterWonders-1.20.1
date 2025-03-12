package net.cebularz.winterwonders.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public interface IStackProjectile
{
    int getStackValue();

    void setStackValue(int value);

    boolean handleStackAction(Level level, LivingEntity wompWomp);
}

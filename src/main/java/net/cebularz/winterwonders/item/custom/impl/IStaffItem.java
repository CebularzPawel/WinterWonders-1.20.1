package net.cebularz.winterwonders.item.custom.impl;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface IStaffItem
{
    int getChargeTime();

    void setChargeTime(int chargeTime);

    boolean execute();

    boolean isInterrupted();

    void onInterrupted();

    default LivingEntity getCaster() {
        return null;
    }

    StaffState getCurrentState();

    void setCurrentStaffState(StaffState state);

    /**
     * Called when the staff is being charged or used
     * @param level The current level
     * @param entity The entity using the staff
     * @param stack The ItemStack being used
     * @param timeLeft Time left in the use action
     */
    default void onChargeTick(Level level, LivingEntity entity, ItemStack stack, int timeLeft) {}

    /**
     * Called when the entity stops using the staff
     * @param level The current level
     * @param entity The entity that was using the staff
     * @param stack The ItemStack that was being used
     */
    default void onStopUsing(Level level, LivingEntity entity, ItemStack stack) {}

    /**
     * Gets the priority of this staff when multiple staves are active
     * Higher priority staves will execute first or override lower priority ones
     * @return The priority value
     */
    default int getPriority() {
        return 0;
    }

    enum StaffState{
        CHARGING,
        CHARGED,
        UNCHARGED,
        ZERO
    }
}
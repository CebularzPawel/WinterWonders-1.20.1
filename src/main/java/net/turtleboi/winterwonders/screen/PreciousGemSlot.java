package net.turtleboi.winterwonders.screen;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.turtleboi.winterwonders.block.entity.LapidaristTableBlockEntity;
import org.jetbrains.annotations.NotNull;

public class PreciousGemSlot extends SlotItemHandler {
    private final LapidaristTableBlockEntity blockEntity;

    public PreciousGemSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, LapidaristTableBlockEntity blockEntity) {
        super(itemHandler, index, xPosition, yPosition);
        this.blockEntity = blockEntity;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return isSocketableItemPresent() && super.mayPlace(stack)
                //&& stack.getItem() instanceof PreciousGemItem
                ;
    }

    @Override
    public boolean mayPickup(Player player) {
        return isSocketableItemPresent() && super.mayPickup(player);
    }

    @Override
    public void set(@NotNull ItemStack stack) {
        super.set(stack);
        onSlotChanged();
    }

    @Override
    public void onQuickCraft(ItemStack oldStack, ItemStack newStack) {
        super.onQuickCraft(oldStack, newStack);
        onSlotChanged();
    }

    @Override
    public void setChanged() {
        super.setChanged();
        onSlotChanged();
    }

    private void onSlotChanged() {
        blockEntity.onSlotChanged();
    }

    public boolean isSocketableItemPresent() {
        ItemStack itemStack = blockEntity.getInventory().getStackInSlot(LapidaristTableBlockEntity.socketableItemSlot);
        return !itemStack.isEmpty() && itemStack.hasTag() && itemStack.getTag().getBoolean("Socketable");
    }
}

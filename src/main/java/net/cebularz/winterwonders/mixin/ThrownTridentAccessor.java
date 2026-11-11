package net.cebularz.winterwonders.mixin;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ThrownTrident.class)
public interface ThrownTridentAccessor {
    @Accessor("tridentItem")
    void setTridentItem(ItemStack stack);

    @Accessor("tridentItem")
    ItemStack getTridentItem();

    @Accessor("dealtDamage")
    void setDealtDamage(boolean value);

    @Accessor("ID_LOYALTY")
    static EntityDataAccessor<Byte> loyaltyData() {
        throw new AssertionError();
    }
}

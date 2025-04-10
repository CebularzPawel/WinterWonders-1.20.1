package net.cebularz.winterwonders.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.effect.custom.FrostResistanceEffect;

public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, WinterWonders.MOD_ID);

    public static final RegistryObject<MobEffect> FROST_RESISTANCE = EFFECTS.register("frost_resistance",
            () -> new FrostResistanceEffect(MobEffectCategory.BENEFICIAL, 59903));

    public static void register(IEventBus eventBus) {EFFECTS.register(eventBus);}

}

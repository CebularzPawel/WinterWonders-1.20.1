package net.cebularz.winterwonders.init;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.effect.ChilledEffect;
import net.cebularz.winterwonders.effect.FrostResistanceEffect;
import net.cebularz.winterwonders.effect.FrozenEffect;

public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, WinterWonders.MOD_ID);

    public static final RegistryObject<MobEffect> CHILLED = EFFECTS.register("chilled",
            () -> new ChilledEffect(MobEffectCategory.HARMFUL, 59903));

    public static final RegistryObject<MobEffect> FROZEN = EFFECTS.register("frozen",
            () -> new FrozenEffect(MobEffectCategory.HARMFUL, 8752371));

    public static final RegistryObject<MobEffect> FROST_RESISTANCE = EFFECTS.register("frost_resistance",
            () -> new FrostResistanceEffect(MobEffectCategory.BENEFICIAL, 59903));

    public static void register(IEventBus eventBus) {EFFECTS.register(eventBus);}

}

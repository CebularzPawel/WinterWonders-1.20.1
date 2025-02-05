package net.turtleboi.winterwonders.init;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.turtleboi.winterwonders.WinterWonders;
import net.turtleboi.winterwonders.effect.ChilledEffect;
import net.turtleboi.winterwonders.effect.FrozenEffect;

public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, WinterWonders.MOD_ID);

    public static final RegistryObject<MobEffect> CHILLED = EFFECTS.register("chilled",
            () -> new ChilledEffect(MobEffectCategory.HARMFUL, 59903));

    public static final RegistryObject<MobEffect> FROZEN = EFFECTS.register("frozen",
            () -> new FrozenEffect(MobEffectCategory.HARMFUL, 8752371));

    public static void register(IEventBus eventBus) {EFFECTS.register(eventBus);}

}

package net.cebularz.winterwonders.init;

import net.cebularz.winterwonders.WinterWonders;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.eventbus.api.IEventBus;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> REGISTRAR = DeferredRegister
            .create(ForgeRegistries.SOUND_EVENTS, WinterWonders.MOD_ID);

    public static final RegistryObject<SoundEvent> BIOME_MUSIC = registerSoundEvents("biome_music");
    public static final RegistryObject<SoundEvent> THROWING_SPEAR_ATTACK = registerSoundEvents("item.throwing_spear.attack");
    public static final RegistryObject<SoundEvent> THROWING_SPEAR_HIT = registerSoundEvents("item.throwing_spear.hit");

    public static void register(IEventBus bus) {
        REGISTRAR.register(bus);
    }

    private static RegistryObject<SoundEvent> registerSoundEvents(String name) {
        ResourceLocation id = new ResourceLocation(WinterWonders.MOD_ID, name);
        return REGISTRAR.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }
}

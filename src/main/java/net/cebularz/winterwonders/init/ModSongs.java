package net.cebularz.winterwonders.init;

import net.cebularz.winterwonders.WinterWonders;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSongs
{
    public static final DeferredRegister<SoundEvent> REGISTRAR = DeferredRegister
            .create(ForgeRegistries.SOUND_EVENTS, WinterWonders.MOD_ID);

    public static final RegistryObject<SoundEvent> BIOME_MUSIC = registerSoundEvents("biome_music");

    private static RegistryObject<SoundEvent> registerSoundEvents(String name) {
        ResourceLocation id = new ResourceLocation(WinterWonders.MOD_ID, name);
        return REGISTRAR.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }
}

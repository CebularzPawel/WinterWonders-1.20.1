package net.cebularz.winterwonders.init;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.cebularz.winterwonders.WinterWonders;

public class ModParticles
{
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, WinterWonders.MOD_ID);

    public static final RegistryObject<SimpleParticleType> AURORA_PARTICLE =
            PARTICLES.register("aurora_particles", ()-> new SimpleParticleType(true));

    public static void register(IEventBus eventBus)
    {
        PARTICLES.register(eventBus);
    }
}

package net.turtleboi.winterwonders;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.turtleboi.winterwonders.block.ModBlocks;
import net.turtleboi.winterwonders.block.entity.ModBlockEntities;
import net.turtleboi.winterwonders.command.ClearCurseCommand;
import net.turtleboi.winterwonders.config.WinterWondersConfig;
import net.turtleboi.winterwonders.effect.ModEffects;
import net.turtleboi.winterwonders.entity.ModEntities;
import net.turtleboi.winterwonders.init.ModAttributes;
import net.turtleboi.winterwonders.item.ModCreativeModeTabs;
import net.turtleboi.winterwonders.item.ModItems;
import net.turtleboi.winterwonders.network.ModNetworking;
import net.turtleboi.winterwonders.particle.ModParticleTypes;
import net.turtleboi.winterwonders.screen.ModMenuTypes;
import net.turtleboi.winterwonders.sound.ModSounds;
import net.turtleboi.winterwonders.world.structures.ModStructures;
import org.slf4j.Logger;

@Mod(WinterWonders.MOD_ID)
public class WinterWonders {
    public static final String MOD_ID = "winterwonders";
    public static final Logger LOGGER = LogUtils.getLogger();

    public WinterWonders() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModParticleTypes.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);

        ModEntities.register(modEventBus);

        ModEffects.register(modEventBus);
        ModSounds.register(modEventBus);

        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);

        ModStructures.register(modEventBus);

        ModAttributes.REGISTRY.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, WinterWondersConfig.SPEC, "winterwonders-common.toml");

        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {

        });
        ModNetworking.register();
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        ClearCurseCommand.register(event.getServer().getCommands().getDispatcher());
    }
}

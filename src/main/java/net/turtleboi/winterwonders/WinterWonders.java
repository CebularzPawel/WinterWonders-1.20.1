package net.turtleboi.winterwonders;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.turtleboi.winterwonders.init.ModBlocks;
import net.turtleboi.winterwonders.init.ModCreativeModeTabs;
import net.turtleboi.winterwonders.init.ModItems;
import org.slf4j.Logger;

@Mod(WinterWonders.MOD_ID)
public class WinterWonders {
    public static final String MOD_ID = "winterwonders";
    public static final Logger LOGGER = LogUtils.getLogger();

    public WinterWonders() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);

        modEventBus.addListener(this::commonSetup);


        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

}

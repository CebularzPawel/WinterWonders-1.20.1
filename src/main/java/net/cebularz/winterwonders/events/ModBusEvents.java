package net.cebularz.winterwonders.events;

import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.entity.ModEntities;
import net.cebularz.winterwonders.entity.custom.BriskEntity;
import net.cebularz.winterwonders.entity.custom.LichEntity;
import net.cebularz.winterwonders.entity.custom.MittenMouseEntity;
import net.cebularz.winterwonders.entity.custom.PinginEntity;
import net.cebularz.winterwonders.entity.custom.RevenantEntity;
import net.cebularz.winterwonders.entity.custom.SnowWeaselEntity;
import net.cebularz.winterwonders.entity.custom.SnowWispEntity;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WinterWonders.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBusEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.SNOW_WISP.get(), SnowWispEntity.createAttributes().build());
        event.put(ModEntities.REVENANT.get(), RevenantEntity.createAttributes().build());
        event.put(ModEntities.PINGIN.get(), PinginEntity.createAttributes().build());
        event.put(ModEntities.MITTEN_MOUSE.get(), MittenMouseEntity.createAttributes().build());
        event.put(ModEntities.SNOW_WEASEL.get(), SnowWeaselEntity.createAttributes().build());
        event.put(ModEntities.BRISK.get(), BriskEntity.createAttributes().build());
        event.put(ModEntities.LICH.get(), LichEntity.createAttribute().build());
    }
}

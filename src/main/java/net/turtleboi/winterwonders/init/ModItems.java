package net.turtleboi.winterwonders.init;

import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.turtleboi.winterwonders.WinterWonders;
import net.turtleboi.winterwonders.item.custom.FrostbiteWandItem;

import java.util.function.Supplier;

public class ModItems {
    public static final Rarity LEGENDARY = Rarity.create("LEGENDARY", ChatFormatting.GOLD);

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, WinterWonders.MOD_ID);

    public static final RegistryObject<Item> FROST_ESSENCE = registerItem("frost_essence",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> TRUEICE_SHARD = registerItem("trueice_shard",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)));

    public static final RegistryObject<Item> FROZEN_HEART = registerItem("frozen_heart",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> COLDSTEEL_SCRAP = registerItem("coldsteel_scrap",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)));

    public static final RegistryObject<Item> COLDSTEEL_INGOT = registerItem("coldsteel_ingot",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)));

    public static final RegistryObject<Item> FROSTBITE_WAND = registerItem("frostbite_wand",
            () -> new FrostbiteWandItem(new Item.Properties().rarity(Rarity.RARE)));


    public static <I extends Item> RegistryObject<I> registerItem(String itemName, Supplier<I> itemSup)
    {
        return registerItem(itemName,itemSup,true);
    }

    public static <I extends Item> RegistryObject<I> registerItem(String itemName, Supplier<I> itemSup, boolean putInTab)
    {
        RegistryObject<I> regObj = ITEMS.register(itemName,itemSup);
        if(putInTab)
        {
            ModCreativeModeTabs.ITEM_LIST.add(regObj.get());
        }
        return regObj;
    }


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

package net.turtleboi.winterwonders.init;

import net.minecraft.ChatFormatting;
import net.minecraft.world.item.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.turtleboi.winterwonders.WinterWonders;
import net.turtleboi.winterwonders.item.ModArmorMaterials;
import net.turtleboi.winterwonders.item.ModToolTiers;
import net.turtleboi.winterwonders.item.custom.FrostbiteWandItem;

import java.util.function.Supplier;

public class ModItems {
    public static final Rarity LEGENDARY = Rarity.create("LEGENDARY", ChatFormatting.GOLD);

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, WinterWonders.MOD_ID);

    public static final RegistryObject<Item> FROST_ESSENCE = ITEMS.register("frost_essence",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> TRUEICE_SHARD = ITEMS.register("trueice_shard",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)));

    public static final RegistryObject<Item> FROZEN_HEART = ITEMS.register("frozen_heart",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> COLDSTEEL_SCRAP = ITEMS.register("coldsteel_scrap",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)));

    public static final RegistryObject<Item> COLDSTEEL_INGOT = ITEMS.register("coldsteel_ingot",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)));

    public static final RegistryObject<Item> COLDSTEEL_SWORD = ITEMS.register("coldsteel_sword",
            () -> new SwordItem(ModToolTiers.COLDSTEEL, 3, -2.4f, new Item.Properties().rarity(Rarity.RARE)));

    public static final RegistryObject<Item> COLDSTEEL_PICKAXE = ITEMS.register("coldsteel_pickaxe",
            () -> new PickaxeItem(ModToolTiers.COLDSTEEL, 1, -2.8f, new Item.Properties().rarity(Rarity.RARE)));

    public static final RegistryObject<Item> COLDSTEEL_AXE = ITEMS.register("coldsteel_axe",
            () -> new AxeItem(ModToolTiers.COLDSTEEL, 6, -3f, new Item.Properties().rarity(Rarity.RARE)));

    public static final RegistryObject<Item> COLDSTEEL_SHOVEL = ITEMS.register("coldsteel_shovel",
            () -> new ShovelItem(ModToolTiers.COLDSTEEL, 2, -3f, new Item.Properties().rarity(Rarity.RARE)));

    public static final RegistryObject<Item> COLDSTEEL_HOE = ITEMS.register("coldsteel_hoe",
            () -> new HoeItem(ModToolTiers.COLDSTEEL, -2, -0.5f, new Item.Properties().rarity(Rarity.RARE)));

    public static final RegistryObject<Item> COLDSTEEL_HELMET = ITEMS.register("coldsteel_helmet",
            () -> new ArmorItem(ModArmorMaterials.COLDSTEEL, ArmorItem.Type.HELMET, new Item.Properties().rarity(Rarity.RARE)));

    public static final RegistryObject<Item> COLDSTEEL_CHESTPLATE = ITEMS.register("coldsteel_chestplate",
            () -> new ArmorItem(ModArmorMaterials.COLDSTEEL, ArmorItem.Type.CHESTPLATE, new Item.Properties().rarity(Rarity.RARE)));

    public static final RegistryObject<Item> COLDSTEEL_LEGGINGS = ITEMS.register("coldsteel_leggings",
            () -> new ArmorItem(ModArmorMaterials.COLDSTEEL, ArmorItem.Type.LEGGINGS, new Item.Properties().rarity(Rarity.RARE)));

    public static final RegistryObject<Item> COLDSTEEL_BOOTS = ITEMS.register("coldsteel_boots",
            () -> new ArmorItem(ModArmorMaterials.COLDSTEEL, ArmorItem.Type.BOOTS, new Item.Properties().rarity(Rarity.RARE)));

    public static final RegistryObject<Item> FROSTBITE_WAND = ITEMS.register("frostbite_wand",
            () -> new FrostbiteWandItem(new Item.Properties().rarity(Rarity.RARE)));


    //public static <I extends Item> RegistryObject<I> registerItem(String itemName, Supplier<I> itemSup)
    //{
    //    return registerItem(itemName,itemSup,true);
    //}
//
    //public static <I extends Item> RegistryObject<I> registerItem(String itemName, Supplier<I> itemSup, boolean putInTab)
    //{
    //    RegistryObject<I> regObj = ITEMS.register(itemName,itemSup);
    //    if(putInTab)
    //        {
    //            ModCreativeModeTabs.ITEM_LIST.add(regObj.get());
    //        }
    //    return regObj;
    //}


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

package net.cebularz.winterwonders.item;

import net.cebularz.winterwonders.block.ModBlocks;
import net.cebularz.winterwonders.entity.ModEntities;
import net.cebularz.winterwonders.item.custom.BlizzardStaffItem;
import net.cebularz.winterwonders.item.custom.ColdsteelSwordItem;
import net.cebularz.winterwonders.item.custom.LichBlizzardStaffItem;
import net.cebularz.winterwonders.item.util.ModArmorItem;
import net.cebularz.winterwonders.item.util.ModArmorMaterials;
import net.cebularz.winterwonders.item.util.ModToolTiers;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.item.custom.FrostbiteWandItem;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, WinterWonders.MOD_ID);

    public static final RegistryObject<Item> FROST_ESSENCE = ITEMS.register("frost_essence",
            () -> new Item(new Item.Properties().rarity(Rarity.COMMON)));

    public static final RegistryObject<Item> BRISK_ROD = ITEMS.register("brisk_rod",
            () -> new Item(new Item.Properties().rarity(Rarity.COMMON)));

    public static final RegistryObject<Item> TRUEICE_SHARD = ITEMS.register("trueice_shard",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)));

    public static final RegistryObject<Item> FROZEN_HEART = ITEMS.register("frozen_heart",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> COLDSTEEL_SCRAP = ITEMS.register("coldsteel_scrap",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> COLDSTEEL_INGOT = ITEMS.register("coldsteel_ingot",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> COLDSTEEL_SWORD = ITEMS.register("coldsteel_sword",
            () -> new ColdsteelSwordItem(ModToolTiers.COLDSTEEL, 3, -2.4f, new Item.Properties().rarity(Rarity.RARE)));

    public static final RegistryObject<Item> COLDSTEEL_PICKAXE = ITEMS.register("coldsteel_pickaxe",
            () -> new PickaxeItem(ModToolTiers.COLDSTEEL, 1, -2.8f, new Item.Properties().rarity(Rarity.RARE)));

    public static final RegistryObject<Item> COLDSTEEL_AXE = ITEMS.register("coldsteel_axe",
            () -> new AxeItem(ModToolTiers.COLDSTEEL, 6, -3f, new Item.Properties().rarity(Rarity.RARE)));

    public static final RegistryObject<Item> COLDSTEEL_SHOVEL = ITEMS.register("coldsteel_shovel",
            () -> new ShovelItem(ModToolTiers.COLDSTEEL, 2, -3f, new Item.Properties().rarity(Rarity.RARE)));

    public static final RegistryObject<Item> COLDSTEEL_HOE = ITEMS.register("coldsteel_hoe",
            () -> new HoeItem(ModToolTiers.COLDSTEEL, -2, -0.5f, new Item.Properties().rarity(Rarity.RARE)));

    public static final RegistryObject<Item> COLDSTEEL_HELMET = ITEMS.register("coldsteel_helmet",
            () -> new ModArmorItem(ModArmorMaterials.COLDSTEEL, ArmorItem.Type.HELMET, new Item.Properties().rarity(Rarity.RARE)));

    public static final RegistryObject<Item> COLDSTEEL_CHESTPLATE = ITEMS.register("coldsteel_chestplate",
            () -> new ModArmorItem(ModArmorMaterials.COLDSTEEL, ArmorItem.Type.CHESTPLATE, new Item.Properties().rarity(Rarity.RARE)));

    public static final RegistryObject<Item> COLDSTEEL_LEGGINGS = ITEMS.register("coldsteel_leggings",
            () -> new ModArmorItem(ModArmorMaterials.COLDSTEEL, ArmorItem.Type.LEGGINGS, new Item.Properties().rarity(Rarity.RARE)));

    public static final RegistryObject<Item> COLDSTEEL_BOOTS = ITEMS.register("coldsteel_boots",
            () -> new ModArmorItem(ModArmorMaterials.COLDSTEEL, ArmorItem.Type.BOOTS, new Item.Properties().rarity(Rarity.RARE)));

    public static final RegistryObject<Item> FROSTBITE_WAND = ITEMS.register("frostbite_wand",
            () -> new FrostbiteWandItem(new Item.Properties().rarity(Rarity.RARE).stacksTo(1)));

    public static final RegistryObject<Item> BLIZZARD_STAFF = ITEMS.register("blizzard_staff",
            () -> new BlizzardStaffItem(new Item.Properties().rarity(Rarity.EPIC).stacksTo(1)));

    public static final RegistryObject<Item> LICH_BLIZZARD_STAFF = ITEMS.register("lich_blizzard_staff",
            () -> new LichBlizzardStaffItem(new Item.Properties().rarity(Rarity.EPIC).setNoRepair().fireResistant().stacksTo(1)));

    public static final RegistryObject<Item> SNOW_WISP_SPAWN_EGG = ITEMS.register("snow_wisp_spawn_egg",
            ()-> new ForgeSpawnEggItem(ModEntities.SNOW_WISP,0xffffff,0xc5ebeb,new Item.Properties()));

    public static final RegistryObject<Item> REVENANT_SPAWN_EGG = ITEMS.register("revenant_spawn_egg",
            ()-> new ForgeSpawnEggItem(ModEntities.REVENANT,0x6e6e6e,0x6c8696,new Item.Properties()));

    public static final RegistryObject<Item> BRISK_SPAWN_EGG = ITEMS.register("brisk_spawn_egg",
            ()-> new ForgeSpawnEggItem(ModEntities.BRISK,0xBFEEF2,0xFFFFFF,new Item.Properties()));

    public static final RegistryObject<Item> PINGIN_SPAWN_EGG = ITEMS.register("pingin_spawn_egg",
            ()-> new ForgeSpawnEggItem(ModEntities.PINGIN,0x374859,0xFF914A,new Item.Properties()));

    public static final RegistryObject<Item> SNOW_WEASEL_SPAWN_EGG = ITEMS.register("snow_weasel_spawn_egg",
            ()-> new ForgeSpawnEggItem(ModEntities.SNOW_WEASEL,0xFFFFFF,0x2F2F2F,new Item.Properties()));

    public static final RegistryObject<Item> LICH_SPAWN_EGG = ITEMS.register("lich_spawn_egg",
            ()-> new ForgeSpawnEggItem(ModEntities.LICH,0x393D44,0xF3B8FF,new Item.Properties()));

    public static final RegistryObject<Item> WUNDERSHROOM_TREE = ITEMS.register("wundershroom_tree",
            () -> new StandingAndWallBlockItem(ModBlocks.WUNDERSHROOM_TREE.get(), ModBlocks.WUNDERSHROOM_TREE_WALL.get(), new Item.Properties(), Direction.DOWN));

    public static final RegistryObject<Item> PUCKERBERRY = ITEMS.register("puckerberry",
            () -> new ItemNameBlockItem(ModBlocks.PUCKERBERRY_BUSH.get(), new Item.Properties().rarity(Rarity.COMMON)
                    .food(new FoodProperties.Builder().nutrition(2).saturationMod(0.1F).build())));

    public static final RegistryObject<Item> PINGIN_FEATHER = ITEMS.register("pingin_feather",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> PINGIN_MEAT = ITEMS.register("pingin_meat",
            () -> new Item(new Item.Properties().food(
                    new FoodProperties.Builder().nutrition(2).saturationMod(0.3F)
                            .effect(new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.3F).meat().build())));

    public static final RegistryObject<Item> COOKED_PINGIN_MEAT = ITEMS.register("cooked_pingin_meat",
            () -> new Item(new Item.Properties().food(
                    new FoodProperties.Builder().nutrition(6).saturationMod(0.6F).meat().build())));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

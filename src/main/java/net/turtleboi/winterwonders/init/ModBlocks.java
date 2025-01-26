package net.turtleboi.winterwonders.init;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.turtleboi.winterwonders.WinterWonders;

import java.util.function.Supplier;

public class ModBlocks 
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, WinterWonders.MOD_ID);




    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        return registerBlock(name,block,true);
    }

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block,boolean putInTab) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        ModItems.registerItem(name, () -> new BlockItem(toReturn.get(), new Item.Properties()), false);
        if (putInTab) {
            ModCreativeModeTabs.BLOCK_LIST.add(toReturn.get());
        }
        return toReturn;
    }



    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}

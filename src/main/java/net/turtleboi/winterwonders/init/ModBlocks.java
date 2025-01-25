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

    public static <I extends Block> RegistryObject<I> registerBlock(String blockName, Supplier<I> blockSup)
    {
        return registerBlock(blockName,blockSup,true);
    }

    public static <I extends Block> RegistryObject<I> registerBlock(String blockName, Supplier<I> blockSup, boolean putInTab)
    {
        RegistryObject<I> regObj = BLOCKS.register(blockName,blockSup);
        ModItems.registerItem(blockName,()->new BlockItem(regObj.get(),new Item.Properties()),false);
        if(putInTab)
        {
            ModCreativeModeTabs.BLOCK_LIST.add(regObj.get());
        }
        return regObj;
    }
    
    public static void register(IEventBus eventBus)
    {
        BLOCKS.register(eventBus);
    }
}

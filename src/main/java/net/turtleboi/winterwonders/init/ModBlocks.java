package net.turtleboi.winterwonders.init;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.turtleboi.winterwonders.WinterWonders;
import net.turtleboi.winterwonders.block.custom.*;
import net.turtleboi.winterwonders.worldgen.tree.greypine.GreypineTreeGrower;


import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, WinterWonders.MOD_ID);

    public static final RegistryObject<Block> COLDSTEEL_BLOCK = registerBlock("coldsteel_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion()));

    public static final RegistryObject<Block> GREYPINE_LOG = registerBlock("greypine_log",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.SPRUCE_LOG).strength(3f)));

    public static final RegistryObject<Block> GREYPINE_WOOD = registerBlock("greypine_wood",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.SPRUCE_LOG).strength(3f)));

    public static final RegistryObject<Block> STRIPPED_GREYPINE_LOG = registerBlock("stripped_greypine_log",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.SPRUCE_LOG).strength(3f)));

    public static final RegistryObject<Block> STRIPPED_GREYPINE_WOOD = registerBlock("stripped_greypine_wood",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.SPRUCE_LOG).strength(3f)));
    public static final RegistryObject<Block> WONDER_SHROOM = registerBlock("wonder_shroom",
            () -> new MushroomNoGrowableBlock(BlockBehaviour.Properties.copy(Blocks.BROWN_MUSHROOM)));

    public static final RegistryObject<Block> WONDER_TREE_SHROOM = BLOCKS.register("wonder_tree_shroom",
            () -> new TreeMushroomBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).noCollission().instabreak().sound(SoundType.WET_GRASS).pushReaction(PushReaction.DESTROY)));
    public static final RegistryObject<Block> WONDER_TREE_SHROOM_WALL = BLOCKS.register("wonder_tree_shroom_wall",
            () -> new TreeMushroomWallBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).forceSolidOn().instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().noCollission().instabreak().dropsLike(WONDER_SHROOM.get())));

    public static final RegistryObject<Block> ICY_VINES_PLANT = BLOCKS.register("icy_vines_plant",
            () -> new IcyVinesPlantBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).noCollission().instabreak().sound(SoundType.VINE).pushReaction(PushReaction.DESTROY).lightLevel((p_50870_) -> {
                return 4;
            })));
    public static final RegistryObject<Block> ICY_VINES = registerBlock("icy_vines",
            () -> new IcyVinesBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).noCollission().instabreak().randomTicks().sound(SoundType.VINE).pushReaction(PushReaction.DESTROY).lightLevel((p_50870_) -> {
                return 5;
            })));




    public static final RegistryObject<Block> GREYPINE_PLANKS = registerBlock("greypine_planks",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.SPRUCE_PLANKS)){
                @Override
                public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return true;
                }

                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 20;
                }

                @Override
                public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 5;
                }
            });


    public static final RegistryObject<Block> ICE_SLUSH = registerBlock("ice_slush",
            ()-> new IceSlushBlock(BlockBehaviour.Properties.copy(Blocks.ICE).mapColor(MapColor.ICE).replaceable().strength(0.2F).friction(0.98F)));

    public static final RegistryObject<Block> GREYPINE_LEAVES = registerBlock("greypine_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.copy(Blocks.SPRUCE_LEAVES)){
                @Override
                public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return true;
                }

                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 60;
                }

                @Override
                public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 30;
                }
            });

    public static final RegistryObject<Block> GREYPINE_SAPLING = registerBlock("greypine_sapling",
            () -> new SaplingBlock(new GreypineTreeGrower(), BlockBehaviour.Properties.copy(Blocks.SPRUCE_SAPLING)));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    private static <T extends Block> RegistryObject<T> registerBlockWithoutItem(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }


    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
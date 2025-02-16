package net.turtleboi.winterwonders.init;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffects;
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
import net.turtleboi.winterwonders.worldgen.tree.greypine.MystWillowTreeGrower;
import org.stringtemplate.v4.ST;


import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, WinterWonders.MOD_ID);

    public static final RegistryObject<Block> COLDSTEEL_BLOCK = registerBlock("coldsteel_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion()));
    public static final RegistryObject<Block> COBBLED_ICE_STONE = registerBlock("cobbled_ice_stone",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.COBBLESTONE)));
    public static final RegistryObject<Block> COBBLED_ICE_STONE_SLAB = registerBlock("cobbled_ice_stone_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.COBBLESTONE)));
    public static final RegistryObject<Block> COBBLED_ICE_STONE_STAIRS = registerBlock("cobbled_ice_stone_stairs",
            () -> new StairBlock(()-> ModBlocks.COBBLED_ICE_STONE.get().defaultBlockState(),BlockBehaviour.Properties.copy(Blocks.COBBLESTONE)));
    public static final RegistryObject<Block> COBBLED_ICE_STONE_WALL = registerBlock("cobbled_ice_stone_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(Blocks.COBBLESTONE)));

    public static final RegistryObject<Block> ICE_STONE_BRICKS = registerBlock("ice_stone_bricks",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS)));
    public static final RegistryObject<Block> ICE_STONE_BRICKS_SLAB = registerBlock("ice_stone_bricks_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS)));
    public static final RegistryObject<Block> ICE_STONE_BRICKS_STAIRS = registerBlock("ice_stone_bricks_stairs",
            () -> new StairBlock(()-> ModBlocks.ICE_STONE_BRICKS.get().defaultBlockState(),BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS)));
    public static final RegistryObject<Block> ICE_STONE_BRICKS_WALL = registerBlock("ice_stone_bricks_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS)));


    public static final RegistryObject<Block> ICE_STONE_TILES = registerBlock("ice_stone_tiles",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS)));
    public static final RegistryObject<Block> ICE_STONE_TILES_SLAB = registerBlock("ice_stone_tiles_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS)));
    public static final RegistryObject<Block> ICE_STONE_TILES_STAIRS = registerBlock("ice_stone_tiles_stairs",
            () -> new StairBlock(()-> ModBlocks.ICE_STONE_TILES.get().defaultBlockState(),BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS)));
    public static final RegistryObject<Block> ICE_STONE_TILES_WALL = registerBlock("ice_stone_tiles_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS)));

    public static final RegistryObject<Block> POLISHED_ICE_STONE = registerBlock("polished_ice_stone",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS)));
    public static final RegistryObject<Block> POLISHED_ICE_STONE_SLAB = registerBlock("polished_ice_stone_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS)));
    public static final RegistryObject<Block> POLISHED_ICE_STONE_STAIRS = registerBlock("polished_ice_stone_stairs",
            () -> new StairBlock(()-> ModBlocks.POLISHED_ICE_STONE.get().defaultBlockState(),BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS)));
    public static final RegistryObject<Block> POLISHED_ICE_STONE_WALL = registerBlock("polished_ice_stone_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS)));


    public static final RegistryObject<Block> GREYPINE_LOG = registerBlock("greypine_log",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.SPRUCE_LOG).strength(3f)));

    public static final RegistryObject<Block> GREYPINE_WOOD = registerBlock("greypine_wood",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.SPRUCE_LOG).strength(3f)));

    public static final RegistryObject<Block> STRIPPED_GREYPINE_LOG = registerBlock("stripped_greypine_log",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.SPRUCE_LOG).strength(3f)));

    public static final RegistryObject<Block> STRIPPED_GREYPINE_WOOD = registerBlock("stripped_greypine_wood",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.SPRUCE_LOG).strength(3f)));

    public static final RegistryObject<Block> MYST_WILLOW_LOG = registerBlock("myst_willow_log",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.SPRUCE_LOG).strength(3f)));

    public static final RegistryObject<Block> MYST_WILLOW_WOOD = registerBlock("myst_willow_wood",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.SPRUCE_LOG).strength(3f)));

    public static final RegistryObject<Block> STRIPPED_MYST_WILLOW_LOG = registerBlock("stripped_myst_willow_log",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.SPRUCE_LOG).strength(3f)));

    public static final RegistryObject<Block> STRIPPED_MYST_WILLOW_WOOD = registerBlock("stripped_myst_willow_wood",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.SPRUCE_LOG).strength(3f)));

    public static final RegistryObject<Block> WONDER_SHROOM = registerBlock("wonder_shroom",
            () -> new MushroomNoGrowableBlock(BlockBehaviour.Properties.copy(Blocks.BROWN_MUSHROOM)));

    public static final RegistryObject<Block> WONDER_TREE_SHROOM = BLOCKS.register("wonder_tree_shroom",
            () -> new TreeMushroomBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).noCollission().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.DESTROY)));

    public static final RegistryObject<Block> WONDER_TREE_SHROOM_WALL = BLOCKS.register("wonder_tree_shroom_wall",
            () -> new TreeMushroomWallBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).forceSolidOn().sound(SoundType.GRASS).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().noCollission().instabreak().dropsLike(WONDER_SHROOM.get())));

    public static final RegistryObject<Block> ICY_VINES_PLANT = BLOCKS.register("icy_vines_plant",
            () -> new IcyVinesPlantBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).noCollission().instabreak().sound(SoundType.VINE).pushReaction(PushReaction.DESTROY).lightLevel((p_50870_) -> {
                return 6;
            })));
    public static final RegistryObject<Block> ICY_VINES = registerBlock("icy_vines",
            () -> new IcyVinesBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).noCollission().instabreak().randomTicks().sound(SoundType.VINE).pushReaction(PushReaction.DESTROY).lightLevel((p_50870_) -> {
                return 8;
            })));


    public static final RegistryObject<Block> MAGICAL_ROSE = registerBlock("magical_rose",
            () -> new FlowerBlock(() -> MobEffects.NIGHT_VISION,5,
                    BlockBehaviour.Properties.copy(Blocks.ALLIUM).noOcclusion().noCollission()));

    public static final RegistryObject<Block> POTTED_MAGICAL_ROSE = BLOCKS.register("potted_magical_rose",
            () -> new FlowerPotBlock(() -> ((FlowerPotBlock) Blocks.FLOWER_POT),ModBlocks.MAGICAL_ROSE,
                    BlockBehaviour.Properties.copy(Blocks.POTTED_ALLIUM).noOcclusion()));

    public static final RegistryObject<Block> ICE_FLOWER = registerBlock("ice_flower",
            () -> new FlowerBlock(() -> ModEffects.COLD_RESIST.get(),5,
                    BlockBehaviour.Properties.copy(Blocks.ALLIUM).noOcclusion().noCollission()));

    public static final RegistryObject<Block> POTTED_ICE_FLOWER = BLOCKS.register("potted_ice_flower",
            () -> new FlowerPotBlock(() -> ((FlowerPotBlock) Blocks.FLOWER_POT),ModBlocks.ICE_FLOWER,
                    BlockBehaviour.Properties.copy(Blocks.POTTED_ALLIUM).noOcclusion()));

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

    public static final RegistryObject<Block> MYST_WILLOW_PLANKS = registerBlock("myst_willow_planks",
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
            () -> new GreypineLeavesBlock(BlockBehaviour.Properties.copy(Blocks.SPRUCE_LEAVES)){
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

    public static final RegistryObject<Block> MYST_WILLOW_LEAVES = registerBlock("myst_willow_leaves",
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

    public static final RegistryObject<Block> MYST_WILLOW_SAPLING = registerBlock("myst_willow_sapling",
            () -> new SaplingBlock(new MystWillowTreeGrower(), BlockBehaviour.Properties.copy(Blocks.SPRUCE_SAPLING)));

    public static final RegistryObject<Block> PUCKERBERRY_BUSH = BLOCKS.register("puckerberry_bush",
            () -> new PuckerberryBushBlock(BlockBehaviour.Properties.copy(Blocks.SWEET_BERRY_BUSH).noOcclusion().noCollission()));

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
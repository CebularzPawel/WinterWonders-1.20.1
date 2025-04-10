package net.cebularz.winterwonders.block;

import net.cebularz.winterwonders.effect.ModEffects;
import net.cebularz.winterwonders.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.block.custom.*;
import net.cebularz.winterwonders.worldgen.tree.greypine.GreypineTreeGrower;
import net.cebularz.winterwonders.worldgen.tree.greypine.MystWillowTreeGrower;


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

    public static final RegistryObject<Block> ICE_STONE_PILLAR = registerBlock("ice_stone_pillar",
            ()-> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS).requiresCorrectToolForDrops()));

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

    public static final RegistryObject<Block> WUNDERSHROOM = registerBlock("wundershroom",
            () -> new MushroomNoGrowableBlock(BlockBehaviour.Properties.copy(Blocks.BROWN_MUSHROOM)));

    public static final RegistryObject<Block> WUNDERSHROOM_TREE = BLOCKS.register("wundershroom_tree",
            () -> new TreeMushroomBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).noCollission().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.DESTROY)));

    public static final RegistryObject<Block> WUNDERSHROOM_TREE_WALL = BLOCKS.register("wundershroom_tree_wall",
            () -> new TreeMushroomWallBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).forceSolidOn().sound(SoundType.GRASS).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().noCollission().instabreak().dropsLike(WUNDERSHROOM.get())));

    public static final RegistryObject<Block> ICY_VINES_PLANT = BLOCKS.register("icy_vines_plant",
            () -> new IcyVinesPlantBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).noCollission().instabreak().sound(SoundType.VINE).pushReaction(PushReaction.DESTROY).lightLevel((p_50870_) -> {
                return 6;
            })));
    public static final RegistryObject<Block> ICY_VINES = registerBlock("icy_vines",
            () -> new IcyVinesBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).noCollission().instabreak().randomTicks().sound(SoundType.VINE).pushReaction(PushReaction.DESTROY).lightLevel((p_50870_) -> 8)));

    public static final RegistryObject<Block> FROSTPETAL = registerBlock("frostpetal",
            () -> new PinkPetalsBlock(BlockBehaviour.Properties.copy(Blocks.PINK_PETALS)));

    public static final RegistryObject<Block> ARCANILLUM = registerBlock("arcanillum",
            () -> new FlowerBlock(ModEffects.FROST_RESISTANCE,40,
                    BlockBehaviour.Properties.copy(Blocks.ALLIUM).noOcclusion().noCollission()));

    public static final RegistryObject<Block> POTTED_ARCANILLUM = BLOCKS.register("potted_arcanillum",
            () -> new FlowerPotBlock(() -> ((FlowerPotBlock) Blocks.FLOWER_POT),ModBlocks.ARCANILLUM,
                    BlockBehaviour.Properties.copy(Blocks.POTTED_ALLIUM).noOcclusion()));

    public static final RegistryObject<Block> RIMEBLOOM = registerBlock("rimebloom",
            () -> new FlowerBlock(ModEffects.FROZEN,5,
                    BlockBehaviour.Properties.copy(Blocks.ALLIUM).noOcclusion().noCollission()));
    public static final RegistryObject<Block> MUSCARI = registerBlock("muscari",
            () -> new FlowerBlock(() -> MobEffects.GLOWING,5,
                    BlockBehaviour.Properties.copy(Blocks.ALLIUM).noOcclusion().noCollission()));
    public static final RegistryObject<Block> POTTED_MUSCARI = BLOCKS.register("potted_muscari",
            () -> new FlowerPotBlock(() -> ((FlowerPotBlock) Blocks.FLOWER_POT),ModBlocks.MUSCARI,
                    BlockBehaviour.Properties.copy(Blocks.POTTED_ALLIUM).noOcclusion()));



    public static final RegistryObject<Block> POTTED_RIMEBLOOM = BLOCKS.register("potted_rimebloom",
            () -> new FlowerPotBlock(() -> ((FlowerPotBlock) Blocks.FLOWER_POT),ModBlocks.RIMEBLOOM,
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

    public static final RegistryObject<Block> GREYPINE_SLAB = registerBlock("greypine_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.SPRUCE_PLANKS)));

    public static final RegistryObject<Block> GREYPINE_STAIRS = registerBlock("greypine_stairs",
            () -> new StairBlock(() -> ModBlocks.GREYPINE_PLANKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.SPRUCE_PLANKS)));

    public static final RegistryObject<Block> GREYPINE_BUTTON = registerBlock("greypine_button",
            () -> new ButtonBlock(BlockBehaviour.Properties.copy(Blocks.SPRUCE_BUTTON),
                    BlockSetType.SPRUCE, 20, true));

    public static final RegistryObject<Block> GREYPINE_PRESSURE_PLATE = registerBlock("greypine_pressure_plate",
            () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, BlockBehaviour.Properties.copy(Blocks.SPRUCE_PLANKS),
                    BlockSetType.SPRUCE));

    public static final RegistryObject<Block> GREYPINE_FENCE = registerBlock("greypine_fence",
            () -> new FenceBlock(BlockBehaviour.Properties.copy(Blocks.SPRUCE_PLANKS)));

    public static final RegistryObject<Block> GREYPINE_FENCE_GATE = registerBlock("greypine_fence_gate",
            () -> new FenceGateBlock(BlockBehaviour.Properties.copy(Blocks.SPRUCE_PLANKS), SoundEvents.WOODEN_DOOR_OPEN, SoundEvents.WOODEN_DOOR_CLOSE));

    public static final RegistryObject<Block> GREYPINE_DOOR = registerBlock("greypine_door",
            () -> new DoorBlock(BlockBehaviour.Properties.copy(Blocks.SPRUCE_DOOR), BlockSetType.SPRUCE));

    public static final RegistryObject<Block> GREYPINE_TRAPDOOR = registerBlock("greypine_trapdoor",
            () -> new TrapDoorBlock(BlockBehaviour.Properties.copy(Blocks.SPRUCE_TRAPDOOR), BlockSetType.SPRUCE));

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
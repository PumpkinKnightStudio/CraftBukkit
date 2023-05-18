package org.bukkit.craftbukkit.block;

import com.google.common.base.Preconditions;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.entity.HangingSignBlockEntity;
import net.minecraft.world.level.block.entity.SculkCatalystBlockEntity;
import net.minecraft.world.level.block.entity.SculkSensorBlockEntity;
import net.minecraft.world.level.block.entity.SculkShriekerBlockEntity;
import net.minecraft.world.level.block.entity.SuspiciousSandBlockEntity;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityBanner;
import net.minecraft.world.level.block.entity.TileEntityBarrel;
import net.minecraft.world.level.block.entity.TileEntityBeacon;
import net.minecraft.world.level.block.entity.TileEntityBed;
import net.minecraft.world.level.block.entity.TileEntityBeehive;
import net.minecraft.world.level.block.entity.TileEntityBell;
import net.minecraft.world.level.block.entity.TileEntityBlastFurnace;
import net.minecraft.world.level.block.entity.TileEntityBrewingStand;
import net.minecraft.world.level.block.entity.TileEntityCampfire;
import net.minecraft.world.level.block.entity.TileEntityChest;
import net.minecraft.world.level.block.entity.TileEntityChestTrapped;
import net.minecraft.world.level.block.entity.TileEntityCommand;
import net.minecraft.world.level.block.entity.TileEntityComparator;
import net.minecraft.world.level.block.entity.TileEntityConduit;
import net.minecraft.world.level.block.entity.TileEntityDispenser;
import net.minecraft.world.level.block.entity.TileEntityDropper;
import net.minecraft.world.level.block.entity.TileEntityEnchantTable;
import net.minecraft.world.level.block.entity.TileEntityEndGateway;
import net.minecraft.world.level.block.entity.TileEntityEnderChest;
import net.minecraft.world.level.block.entity.TileEntityEnderPortal;
import net.minecraft.world.level.block.entity.TileEntityFurnaceFurnace;
import net.minecraft.world.level.block.entity.TileEntityHopper;
import net.minecraft.world.level.block.entity.TileEntityJigsaw;
import net.minecraft.world.level.block.entity.TileEntityJukeBox;
import net.minecraft.world.level.block.entity.TileEntityLectern;
import net.minecraft.world.level.block.entity.TileEntityLightDetector;
import net.minecraft.world.level.block.entity.TileEntityMobSpawner;
import net.minecraft.world.level.block.entity.TileEntityShulkerBox;
import net.minecraft.world.level.block.entity.TileEntitySign;
import net.minecraft.world.level.block.entity.TileEntitySkull;
import net.minecraft.world.level.block.entity.TileEntitySmoker;
import net.minecraft.world.level.block.entity.TileEntityStructure;
import net.minecraft.world.level.block.piston.TileEntityPiston;
import net.minecraft.world.level.block.state.IBlockData;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.BlockType;
import org.bukkit.craftbukkit.CraftWorld;

public final class CraftBlockStates {

    private abstract static class BlockStateFactory<B extends CraftBlockState> {

        public final Class<B> blockStateType;

        public BlockStateFactory(Class<B> blockStateType) {
            this.blockStateType = blockStateType;
        }

        // The given world can be null for unplaced BlockStates.
        // If the world is not null and the given block data is a tile entity, the given tile entity is expected to not be null.
        // Otherwise, the given tile entity may or may not be null.
        // If the given tile entity is not null, its position and block data are expected to match the given block position and block data.
        // In some situations, such as during chunk generation, the tile entity's world may be null, even if the given world is not null.
        // If the tile entity's world is not null, it is expected to match the given world.
        public abstract B createBlockState(World world, BlockPosition blockPosition, IBlockData blockData, TileEntity tileEntity);
    }

    private static class BlockEntityStateFactory<T extends TileEntity, B extends CraftBlockEntityState<T>> extends BlockStateFactory<B> {

        private final BiFunction<World, T, B> blockStateConstructor;
        private final BiFunction<BlockPosition, IBlockData, T> tileEntityConstructor;

        protected BlockEntityStateFactory(Class<B> blockStateType, BiFunction<World, T, B> blockStateConstructor, BiFunction<BlockPosition, IBlockData, T> tileEntityConstructor) {
            super(blockStateType);
            this.blockStateConstructor = blockStateConstructor;
            this.tileEntityConstructor = tileEntityConstructor;
        }

        @Override
        public final B createBlockState(World world, BlockPosition blockPosition, IBlockData blockData, TileEntity tileEntity) {
            if (world != null) {
                Preconditions.checkState(tileEntity != null, "Tile is null, asynchronous access? %s", CraftBlock.at(((CraftWorld) world).getHandle(), blockPosition));
            } else if (tileEntity == null) {
                tileEntity = this.createTileEntity(blockPosition, blockData);
            }
            return this.createBlockState(world, (T) tileEntity);
        }

        private T createTileEntity(BlockPosition blockPosition, IBlockData blockData) {
            return tileEntityConstructor.apply(blockPosition, blockData);
        }

        private B createBlockState(World world, T tileEntity) {
            return blockStateConstructor.apply(world, tileEntity);
        }
    }

    private static final Map<BlockType<?>, BlockStateFactory<?>> FACTORIES = new HashMap<>();
    private static final BlockStateFactory<?> DEFAULT_FACTORY = new BlockStateFactory<CraftBlockState>(CraftBlockState.class) {
        @Override
        public CraftBlockState createBlockState(World world, BlockPosition blockPosition, IBlockData blockData, TileEntity tileEntity) {
            // SPIGOT-6754, SPIGOT-6817: Restore previous behaviour for tile entities with removed blocks (loot generation post-destroy)
            if (tileEntity != null) {
                // block with unhandled TileEntity:
                return new CraftBlockEntityState<>(world, tileEntity);
            }
            Preconditions.checkState(tileEntity == null, "Unexpected BlockState for %s", CraftBlockType.minecraftToBukkit(blockData.getBlock()).getKey());
            return new CraftBlockState(world, blockPosition, blockData);
        }
    };

    static {
        register(
                Arrays.asList(
                        BlockType.ACACIA_SIGN,
                        BlockType.ACACIA_WALL_SIGN,
                        BlockType.BAMBOO_SIGN,
                        BlockType.BAMBOO_WALL_SIGN,
                        BlockType.BIRCH_SIGN,
                        BlockType.BIRCH_WALL_SIGN,
                        BlockType.CHERRY_SIGN,
                        BlockType.CHERRY_WALL_SIGN,
                        BlockType.CRIMSON_SIGN,
                        BlockType.CRIMSON_WALL_SIGN,
                        BlockType.DARK_OAK_SIGN,
                        BlockType.DARK_OAK_WALL_SIGN,
                        BlockType.JUNGLE_SIGN,
                        BlockType.JUNGLE_WALL_SIGN,
                        BlockType.MANGROVE_SIGN,
                        BlockType.MANGROVE_WALL_SIGN,
                        BlockType.OAK_SIGN,
                        BlockType.OAK_WALL_SIGN,
                        BlockType.SPRUCE_SIGN,
                        BlockType.SPRUCE_WALL_SIGN,
                        BlockType.WARPED_SIGN,
                        BlockType.WARPED_WALL_SIGN
                ), CraftSign.class, CraftSign::new, TileEntitySign::new
        );

        register(
                Arrays.asList(
                        BlockType.ACACIA_HANGING_SIGN,
                        BlockType.ACACIA_WALL_HANGING_SIGN,
                        BlockType.BAMBOO_HANGING_SIGN,
                        BlockType.BAMBOO_WALL_HANGING_SIGN,
                        BlockType.BIRCH_HANGING_SIGN,
                        BlockType.BIRCH_WALL_HANGING_SIGN,
                        BlockType.CHERRY_HANGING_SIGN,
                        BlockType.CHERRY_WALL_HANGING_SIGN,
                        BlockType.CRIMSON_HANGING_SIGN,
                        BlockType.CRIMSON_WALL_HANGING_SIGN,
                        BlockType.DARK_OAK_HANGING_SIGN,
                        BlockType.DARK_OAK_WALL_HANGING_SIGN,
                        BlockType.JUNGLE_HANGING_SIGN,
                        BlockType.JUNGLE_WALL_HANGING_SIGN,
                        BlockType.MANGROVE_HANGING_SIGN,
                        BlockType.MANGROVE_WALL_HANGING_SIGN,
                        BlockType.OAK_HANGING_SIGN,
                        BlockType.OAK_WALL_HANGING_SIGN,
                        BlockType.SPRUCE_HANGING_SIGN,
                        BlockType.SPRUCE_WALL_HANGING_SIGN,
                        BlockType.WARPED_HANGING_SIGN,
                        BlockType.WARPED_WALL_HANGING_SIGN
                ), CraftHangingSign.class, CraftHangingSign::new, HangingSignBlockEntity::new
        );

        register(
                Arrays.asList(
                        BlockType.CREEPER_HEAD,
                        BlockType.CREEPER_WALL_HEAD,
                        BlockType.DRAGON_HEAD,
                        BlockType.DRAGON_WALL_HEAD,
                        BlockType.PIGLIN_HEAD,
                        BlockType.PIGLIN_WALL_HEAD,
                        BlockType.PLAYER_HEAD,
                        BlockType.PLAYER_WALL_HEAD,
                        BlockType.SKELETON_SKULL,
                        BlockType.SKELETON_WALL_SKULL,
                        BlockType.WITHER_SKELETON_SKULL,
                        BlockType.WITHER_SKELETON_WALL_SKULL,
                        BlockType.ZOMBIE_HEAD,
                        BlockType.ZOMBIE_WALL_HEAD
                ), CraftSkull.class, CraftSkull::new, TileEntitySkull::new
        );

        register(
                Arrays.asList(
                        BlockType.COMMAND_BLOCK,
                        BlockType.REPEATING_COMMAND_BLOCK,
                        BlockType.CHAIN_COMMAND_BLOCK
                ), CraftCommandBlock.class, CraftCommandBlock::new, TileEntityCommand::new
        );

        register(
                Arrays.asList(
                        BlockType.BLACK_BANNER,
                        BlockType.BLACK_WALL_BANNER,
                        BlockType.BLUE_BANNER,
                        BlockType.BLUE_WALL_BANNER,
                        BlockType.BROWN_BANNER,
                        BlockType.BROWN_WALL_BANNER,
                        BlockType.CYAN_BANNER,
                        BlockType.CYAN_WALL_BANNER,
                        BlockType.GRAY_BANNER,
                        BlockType.GRAY_WALL_BANNER,
                        BlockType.GREEN_BANNER,
                        BlockType.GREEN_WALL_BANNER,
                        BlockType.LIGHT_BLUE_BANNER,
                        BlockType.LIGHT_BLUE_WALL_BANNER,
                        BlockType.LIGHT_GRAY_BANNER,
                        BlockType.LIGHT_GRAY_WALL_BANNER,
                        BlockType.LIME_BANNER,
                        BlockType.LIME_WALL_BANNER,
                        BlockType.MAGENTA_BANNER,
                        BlockType.MAGENTA_WALL_BANNER,
                        BlockType.ORANGE_BANNER,
                        BlockType.ORANGE_WALL_BANNER,
                        BlockType.PINK_BANNER,
                        BlockType.PINK_WALL_BANNER,
                        BlockType.PURPLE_BANNER,
                        BlockType.PURPLE_WALL_BANNER,
                        BlockType.RED_BANNER,
                        BlockType.RED_WALL_BANNER,
                        BlockType.WHITE_BANNER,
                        BlockType.WHITE_WALL_BANNER,
                        BlockType.YELLOW_BANNER,
                        BlockType.YELLOW_WALL_BANNER
                ), CraftBanner.class, CraftBanner::new, TileEntityBanner::new
        );

        register(
                Arrays.asList(
                        BlockType.SHULKER_BOX,
                        BlockType.WHITE_SHULKER_BOX,
                        BlockType.ORANGE_SHULKER_BOX,
                        BlockType.MAGENTA_SHULKER_BOX,
                        BlockType.LIGHT_BLUE_SHULKER_BOX,
                        BlockType.YELLOW_SHULKER_BOX,
                        BlockType.LIME_SHULKER_BOX,
                        BlockType.PINK_SHULKER_BOX,
                        BlockType.GRAY_SHULKER_BOX,
                        BlockType.LIGHT_GRAY_SHULKER_BOX,
                        BlockType.CYAN_SHULKER_BOX,
                        BlockType.PURPLE_SHULKER_BOX,
                        BlockType.BLUE_SHULKER_BOX,
                        BlockType.BROWN_SHULKER_BOX,
                        BlockType.GREEN_SHULKER_BOX,
                        BlockType.RED_SHULKER_BOX,
                        BlockType.BLACK_SHULKER_BOX
                ), CraftShulkerBox.class, CraftShulkerBox::new, TileEntityShulkerBox::new
        );

        register(
                Arrays.asList(
                        BlockType.BLACK_BED,
                        BlockType.BLUE_BED,
                        BlockType.BROWN_BED,
                        BlockType.CYAN_BED,
                        BlockType.GRAY_BED,
                        BlockType.GREEN_BED,
                        BlockType.LIGHT_BLUE_BED,
                        BlockType.LIGHT_GRAY_BED,
                        BlockType.LIME_BED,
                        BlockType.MAGENTA_BED,
                        BlockType.ORANGE_BED,
                        BlockType.PINK_BED,
                        BlockType.PURPLE_BED,
                        BlockType.RED_BED,
                        BlockType.WHITE_BED,
                        BlockType.YELLOW_BED
                ), CraftBed.class, CraftBed::new, TileEntityBed::new
        );

        register(
                Arrays.asList(
                        BlockType.BEEHIVE,
                        BlockType.BEE_NEST
                ), CraftBeehive.class, CraftBeehive::new, TileEntityBeehive::new
        );

        register(
                Arrays.asList(
                        BlockType.CAMPFIRE,
                        BlockType.SOUL_CAMPFIRE
                ), CraftCampfire.class, CraftCampfire::new, TileEntityCampfire::new
        );

        register(BlockType.BARREL, CraftBarrel.class, CraftBarrel::new, TileEntityBarrel::new);
        register(BlockType.BEACON, CraftBeacon.class, CraftBeacon::new, TileEntityBeacon::new);
        register(BlockType.BELL, CraftBell.class, CraftBell::new, TileEntityBell::new);
        register(BlockType.BLAST_FURNACE, CraftBlastFurnace.class, CraftBlastFurnace::new, TileEntityBlastFurnace::new);
        register(BlockType.BREWING_STAND, CraftBrewingStand.class, CraftBrewingStand::new, TileEntityBrewingStand::new);
        register(BlockType.CHEST, CraftChest.class, CraftChest::new, TileEntityChest::new);
        register(BlockType.CHISELED_BOOKSHELF, CraftChiseledBookshelf.class, CraftChiseledBookshelf::new, ChiseledBookShelfBlockEntity::new);
        register(BlockType.COMPARATOR, CraftComparator.class, CraftComparator::new, TileEntityComparator::new);
        register(BlockType.CONDUIT, CraftConduit.class, CraftConduit::new, TileEntityConduit::new);
        register(BlockType.DAYLIGHT_DETECTOR, CraftDaylightDetector.class, CraftDaylightDetector::new, TileEntityLightDetector::new);
        register(BlockType.DECORATED_POT, CraftDecoratedPot.class, CraftDecoratedPot::new, DecoratedPotBlockEntity::new);
        register(BlockType.DISPENSER, CraftDispenser.class, CraftDispenser::new, TileEntityDispenser::new);
        register(BlockType.DROPPER, CraftDropper.class, CraftDropper::new, TileEntityDropper::new);
        register(BlockType.ENCHANTING_TABLE, CraftEnchantingTable.class, CraftEnchantingTable::new, TileEntityEnchantTable::new);
        register(BlockType.ENDER_CHEST, CraftEnderChest.class, CraftEnderChest::new, TileEntityEnderChest::new);
        register(BlockType.END_GATEWAY, CraftEndGateway.class, CraftEndGateway::new, TileEntityEndGateway::new);
        register(BlockType.END_PORTAL, CraftEndPortal.class, CraftEndPortal::new, TileEntityEnderPortal::new);
        register(BlockType.FURNACE, CraftFurnaceFurnace.class, CraftFurnaceFurnace::new, TileEntityFurnaceFurnace::new);
        register(BlockType.HOPPER, CraftHopper.class, CraftHopper::new, TileEntityHopper::new);
        register(BlockType.JIGSAW, CraftJigsaw.class, CraftJigsaw::new, TileEntityJigsaw::new);
        register(BlockType.JUKEBOX, CraftJukebox.class, CraftJukebox::new, TileEntityJukeBox::new);
        register(BlockType.LECTERN, CraftLectern.class, CraftLectern::new, TileEntityLectern::new);
        register(BlockType.MOVING_PISTON, CraftMovingPiston.class, CraftMovingPiston::new, TileEntityPiston::new);
        register(BlockType.SCULK_CATALYST, CraftSculkCatalyst.class, CraftSculkCatalyst::new, SculkCatalystBlockEntity::new);
        register(BlockType.SCULK_SENSOR, CraftSculkSensor.class, CraftSculkSensor::new, SculkSensorBlockEntity::new);
        register(BlockType.SCULK_SHRIEKER, CraftSculkShrieker.class, CraftSculkShrieker::new, SculkShriekerBlockEntity::new);
        register(BlockType.SMOKER, CraftSmoker.class, CraftSmoker::new, TileEntitySmoker::new);
        register(BlockType.SPAWNER, CraftCreatureSpawner.class, CraftCreatureSpawner::new, TileEntityMobSpawner::new);
        register(BlockType.STRUCTURE_BLOCK, CraftStructureBlock.class, CraftStructureBlock::new, TileEntityStructure::new);
        register(BlockType.SUSPICIOUS_SAND, CraftSuspiciousSand.class, CraftSuspiciousSand::new, SuspiciousSandBlockEntity::new);
        register(BlockType.TRAPPED_CHEST, CraftChest.class, CraftChest::new, TileEntityChestTrapped::new);
    }

    private static void register(BlockType<?> blockType, BlockStateFactory<?> factory) {
        FACTORIES.put(blockType, factory);
    }

    private static <T extends TileEntity, B extends CraftBlockEntityState<T>> void register(
            BlockType<?> blockType,
            Class<B> blockStateType,
            BiFunction<World, T, B> blockStateConstructor,
            BiFunction<BlockPosition, IBlockData, T> tileEntityConstructor
    ) {
        register(Collections.singletonList(blockType), blockStateType, blockStateConstructor, tileEntityConstructor);
    }

    private static <T extends TileEntity, B extends CraftBlockEntityState<T>> void register(
            List<BlockType<?>> blockTypes,
            Class<B> blockStateType,
            BiFunction<World, T, B> blockStateConstructor,
            BiFunction<BlockPosition, IBlockData, T> tileEntityConstructor
    ) {
        BlockStateFactory<B> factory = new BlockEntityStateFactory<>(blockStateType, blockStateConstructor, tileEntityConstructor);
        for (BlockType<?> blockType : blockTypes) {
            register(blockType, factory);
        }
    }

    private static BlockStateFactory<?> getFactory(BlockType<?> blockType) {
        return FACTORIES.getOrDefault(blockType, DEFAULT_FACTORY);
    }

    public static Class<? extends CraftBlockState> getBlockStateType(BlockType<?> blockType) {
        Preconditions.checkNotNull(blockType, "blockType is null");
        return getFactory(blockType).blockStateType;
    }

    public static TileEntity createNewTileEntity(BlockType<?> blockType) {
        BlockStateFactory<?> factory = getFactory(blockType);

        if (factory instanceof BlockEntityStateFactory) {
            return ((BlockEntityStateFactory<?, ?>) factory).createTileEntity(BlockPosition.ZERO, ((CraftBlockType) blockType).getHandle().defaultBlockState());
        }

        return null;
    }

    public static BlockState getBlockState(Block block) {
        Preconditions.checkNotNull(block, "block is null");
        CraftBlock craftBlock = (CraftBlock) block;
        CraftWorld world = (CraftWorld) block.getWorld();
        BlockPosition blockPosition = craftBlock.getPosition();
        IBlockData blockData = craftBlock.getNMS();
        TileEntity tileEntity = craftBlock.getHandle().getBlockEntity(blockPosition);
        CraftBlockState blockState = getBlockState(world, blockPosition, blockData, tileEntity);
        blockState.setWorldHandle(craftBlock.getHandle()); // Inject the block's generator access
        return blockState;
    }

    public static BlockState getBlockState(BlockType<?> blockType, @Nullable NBTTagCompound blockEntityTag) {
        return getBlockState(BlockPosition.ZERO, blockType, blockEntityTag);
    }

    public static BlockState getBlockState(BlockPosition blockPosition, BlockType<?> blockType, @Nullable NBTTagCompound blockEntityTag) {
        Preconditions.checkNotNull(blockType, "block type is null");
        IBlockData blockData = ((CraftBlockType<?>) blockType).getHandle().defaultBlockState();
        return getBlockState(blockPosition, blockData, blockEntityTag);
    }

    public static BlockState getBlockState(IBlockData blockData, @Nullable NBTTagCompound blockEntityTag) {
        return getBlockState(BlockPosition.ZERO, blockData, blockEntityTag);
    }

    public static BlockState getBlockState(BlockPosition blockPosition, IBlockData blockData, @Nullable NBTTagCompound blockEntityTag) {
        Preconditions.checkNotNull(blockPosition, "blockPosition is null");
        Preconditions.checkNotNull(blockData, "blockData is null");
        TileEntity tileEntity = (blockEntityTag == null) ? null : TileEntity.loadStatic(blockPosition, blockData, blockEntityTag);
        return getBlockState(null, blockPosition, blockData, tileEntity);
    }

    // See BlockStateFactory#createBlockState(World, BlockPosition, IBlockData, TileEntity)
    private static CraftBlockState getBlockState(World world, BlockPosition blockPosition, IBlockData blockData, TileEntity tileEntity) {
        BlockType<?> blockType = CraftBlockType.minecraftToBukkit(blockData.getBlock());
        BlockStateFactory<?> factory;
        // For some types of TileEntity blocks (eg. moving pistons), Minecraft may in some situations (eg. when using Block#setType or the
        // setBlock command) not create a corresponding TileEntity in the world. We return a normal BlockState in this case.
        if (world != null && tileEntity == null && isTileEntityOptional(blockType)) {
            factory = DEFAULT_FACTORY;
        } else {
            factory = getFactory(blockType);
        }
        return factory.createBlockState(world, blockPosition, blockData, tileEntity);
    }

    public static boolean isTileEntityOptional(BlockType<?> blockType) {
        return blockType == BlockType.MOVING_PISTON;
    }

    // This ignores tile entity data.
    public static CraftBlockState getBlockState(GeneratorAccess world, BlockPosition pos) {
        return new CraftBlockState(CraftBlock.at(world, pos));
    }

    // This ignores tile entity data.
    public static CraftBlockState getBlockState(GeneratorAccess world, BlockPosition pos, int flag) {
        return new CraftBlockState(CraftBlock.at(world, pos), flag);
    }

    private CraftBlockStates() {
    }
}

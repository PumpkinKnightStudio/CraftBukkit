package org.bukkit.craftbukkit.legacy;

import com.google.common.base.Preconditions;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.CraftMaterial;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemType;
import org.bukkit.material.MaterialData;

@Deprecated
public class CraftLegacyMaterial implements Material {

    protected static Map<String, CraftLegacyMaterial> MATERIAL_MAP;
    private static boolean BLOCK_CREATING = false;

    public static CraftLegacyMaterial createLegacyMaterial(String name, int id, int maxStackSize, short maxDurability, Class<? extends MaterialData> materialData) {
        Preconditions.checkState(!BLOCK_CREATING, "Plugins cannot create Legacy Materials");
        if (MATERIAL_MAP == null) {
            MATERIAL_MAP = new LinkedHashMap<>();
        }

        return MATERIAL_MAP.computeIfAbsent(name.toUpperCase(), n -> new CraftLegacyMaterial(n, id, maxStackSize, maxDurability, materialData));
    }

    public static void blockCreating() {
        BLOCK_CREATING = true;
    }

    public static CraftLegacyMaterial getLegacyMaterial(String name) {
        return MATERIAL_MAP.get(name);
    }

    public static Collection<Material> getLegacyMaterials() {
        return new LinkedHashSet<>(MATERIAL_MAP.values());
    }

    private final int id;
    private final int maxStackSize;
    private final short maxDurability;
    private final String name;
    private final int ordinal;
    private final Class<?> data;
    private final Constructor<? extends MaterialData> ctor;

    public CraftLegacyMaterial(String name, int id, int maxStackSize, short maxDurability, Class<? extends MaterialData> materialData) {
        this.id = id;
        this.maxStackSize = maxStackSize;
        this.maxDurability = maxDurability;
        this.name = name;
        this.ordinal = CraftMaterial.getNextOrdinal();
        this.data = materialData;
        // try to cache the constructor for this material
        try {
            if (MaterialData.class.isAssignableFrom(materialData)) {
                this.ctor = (Constructor<? extends MaterialData>) materialData.getConstructor(Material.class, byte.class);
            } else {
                this.ctor = null;
            }
        } catch (NoSuchMethodException ex) {
            throw new AssertionError(ex);
        } catch (SecurityException ex) {
            throw new AssertionError(ex);
        }
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public boolean isLegacy() {
        return true;
    }

    @Override
    public int getMaxStackSize() {
        return maxStackSize;
    }

    @Override
    public short getMaxDurability() {
        return maxDurability;
    }

    @Override
    public BlockData createBlockData() {
        return Bukkit.createBlockData(this);
    }

    @Override
    public BlockData createBlockData(Consumer<BlockData> consumer) {
        return Bukkit.createBlockData(this, consumer);
    }

    @Override
    public BlockData createBlockData(String data) {
        return Bukkit.createBlockData(this, data);
    }

    @Override
    public Class<?> getBlockDataClass() {
        return data;
    }

    @Override
    public Class<? extends MaterialData> getData() {
        return ctor.getDeclaringClass();
    }

    @Override
    public MaterialData getNewData(byte raw) {
        try {
            return ctor.newInstance(this, raw);
        } catch (InstantiationException ex) {
            final Throwable t = ex.getCause();
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            }
            if (t instanceof Error) {
                throw (Error) t;
            }
            throw new AssertionError(t);
        } catch (Throwable t) {
            throw new AssertionError(t);
        }
    }

    @Override
    public boolean isBlock() {
        return 0 <= id && id < 256;
    }

    @Override
    public boolean isEdible() {
        switch (id) {
            case 297: // LEGACY_BREAD
            case 391: // LEGACY_CARROT_ITEM
            case 393: // LEGACY_BAKED_POTATO
            case 392: // LEGACY_POTATO_ITEM
            case 394: // LEGACY_POISONOUS_POTATO
            case 396: // LEGACY_GOLDEN_CARROT
            case 400: // LEGACY_PUMPKIN_PIE
            case 357: // LEGACY_COOKIE
            case 360: // LEGACY_MELON
            case 282: // LEGACY_MUSHROOM_SOUP
            case 365: // LEGACY_RAW_CHICKEN
            case 366: // LEGACY_COOKED_CHICKEN
            case 363: // LEGACY_RAW_BEEF
            case 364: // LEGACY_COOKED_BEEF
            case 349: // LEGACY_RAW_FISH
            case 350: // LEGACY_COOKED_FISH
            case 319: // LEGACY_PORK
            case 320: // LEGACY_GRILLED_PORK
            case 260: // LEGACY_APPLE
            case 322: // LEGACY_GOLDEN_APPLE
            case 367: // LEGACY_ROTTEN_FLESH
            case 375: // LEGACY_SPIDER_EYE
            case 411: // LEGACY_RABBIT
            case 412: // LEGACY_COOKED_RABBIT
            case 413: // LEGACY_RABBIT_STEW
            case 423: // LEGACY_MUTTON
            case 424: // LEGACY_COOKED_MUTTON
            case 434: // LEGACY_BEETROOT
            case 432: // LEGACY_CHORUS_FRUIT
            case 436: // LEGACY_BEETROOT_SOUP
                return true;
            default:
                return false;
        }

    }

    @Override
    public boolean isRecord() {
        return id >= LEGACY_GOLD_RECORD.getId() && id <= LEGACY_RECORD_12.getId();
    }

    @Override
    public boolean isSolid() {
        if (!isBlock() || id == 0) {
            return false;
        }

        switch (id) {
            //<editor-fold defaultstate="collapsed" desc="isSolid">
            case 1: // LEGACY_STONE
            case 2: // LEGACY_GRASS
            case 3: // LEGACY_DIRT
            case 4: // LEGACY_COBBLESTONE
            case 5: // LEGACY_WOOD
            case 7: // LEGACY_BEDROCK
            case 12: // LEGACY_SAND
            case 13: // LEGACY_GRAVEL
            case 14: // LEGACY_GOLD_ORE
            case 15: // LEGACY_IRON_ORE
            case 16: // LEGACY_COAL_ORE
            case 17: // LEGACY_LOG
            case 18: // LEGACY_LEAVES
            case 19: // LEGACY_SPONGE
            case 20: // LEGACY_GLASS
            case 21: // LEGACY_LAPIS_ORE
            case 22: // LEGACY_LAPIS_BLOCK
            case 23: // LEGACY_DISPENSER
            case 24: // LEGACY_SANDSTONE
            case 25: // LEGACY_NOTE_BLOCK
            case 26: // LEGACY_BED_BLOCK
            case 29: // LEGACY_PISTON_STICKY_BASE
            case 33: // LEGACY_PISTON_BASE
            case 34: // LEGACY_PISTON_EXTENSION
            case 35: // LEGACY_WOOL
            case 36: // LEGACY_PISTON_MOVING_PIECE
            case 41: // LEGACY_GOLD_BLOCK
            case 42: // LEGACY_IRON_BLOCK
            case 43: // LEGACY_DOUBLE_STEP
            case 44: // LEGACY_STEP
            case 45: // LEGACY_BRICK
            case 46: // LEGACY_TNT
            case 47: // LEGACY_BOOKSHELF
            case 48: // LEGACY_MOSSY_COBBLESTONE
            case 49: // LEGACY_OBSIDIAN
            case 52: // LEGACY_MOB_SPAWNER
            case 53: // LEGACY_WOOD_STAIRS
            case 54: // LEGACY_CHEST
            case 56: // LEGACY_DIAMOND_ORE
            case 57: // LEGACY_DIAMOND_BLOCK
            case 58: // LEGACY_WORKBENCH
            case 60: // LEGACY_SOIL
            case 61: // LEGACY_FURNACE
            case 62: // LEGACY_BURNING_FURNACE
            case 63: // LEGACY_SIGN_POST
            case 64: // LEGACY_WOODEN_DOOR
            case 67: // LEGACY_COBBLESTONE_STAIRS
            case 68: // LEGACY_WALL_SIGN
            case 70: // LEGACY_STONE_PLATE
            case 71: // LEGACY_IRON_DOOR_BLOCK
            case 72: // LEGACY_WOOD_PLATE
            case 73: // LEGACY_REDSTONE_ORE
            case 74: // LEGACY_GLOWING_REDSTONE_ORE
            case 79: // LEGACY_ICE
            case 80: // LEGACY_SNOW_BLOCK
            case 81: // LEGACY_CACTUS
            case 82: // LEGACY_CLAY
            case 84: // LEGACY_JUKEBOX
            case 85: // LEGACY_FENCE
            case 86: // LEGACY_PUMPKIN
            case 87: // LEGACY_NETHERRACK
            case 88: // LEGACY_SOUL_SAND
            case 89: // LEGACY_GLOWSTONE
            case 91: // LEGACY_JACK_O_LANTERN
            case 92: // LEGACY_CAKE_BLOCK
            case 95: // LEGACY_STAINED_GLASS
            case 96: // LEGACY_TRAP_DOOR
            case 97: // LEGACY_MONSTER_EGGS
            case 98: // LEGACY_SMOOTH_BRICK
            case 99: // LEGACY_HUGE_MUSHROOM_1
            case 100: // LEGACY_HUGE_MUSHROOM_2
            case 101: // LEGACY_IRON_FENCE
            case 102: // LEGACY_THIN_GLASS
            case 103: // LEGACY_MELON_BLOCK
            case 107: // LEGACY_FENCE_GATE
            case 108: // LEGACY_BRICK_STAIRS
            case 109: // LEGACY_SMOOTH_STAIRS
            case 110: // LEGACY_MYCEL
            case 112: // LEGACY_NETHER_BRICK
            case 113: // LEGACY_NETHER_FENCE
            case 114: // LEGACY_NETHER_BRICK_STAIRS
            case 116: // LEGACY_ENCHANTMENT_TABLE
            case 117: // LEGACY_BREWING_STAND
            case 118: // LEGACY_CAULDRON
            case 120: // LEGACY_ENDER_PORTAL_FRAME
            case 121: // LEGACY_ENDER_STONE
            case 122: // LEGACY_DRAGON_EGG
            case 123: // LEGACY_REDSTONE_LAMP_OFF
            case 124: // LEGACY_REDSTONE_LAMP_ON
            case 125: // LEGACY_WOOD_DOUBLE_STEP
            case 126: // LEGACY_WOOD_STEP
            case 128: // LEGACY_SANDSTONE_STAIRS
            case 129: // LEGACY_EMERALD_ORE
            case 130: // LEGACY_ENDER_CHEST
            case 133: // LEGACY_EMERALD_BLOCK
            case 134: // LEGACY_SPRUCE_WOOD_STAIRS
            case 135: // LEGACY_BIRCH_WOOD_STAIRS
            case 136: // LEGACY_JUNGLE_WOOD_STAIRS
            case 137: // LEGACY_COMMAND
            case 138: // LEGACY_BEACON
            case 139: // LEGACY_COBBLE_WALL
            case 145: // LEGACY_ANVIL
            case 146: // LEGACY_TRAPPED_CHEST
            case 147: // LEGACY_GOLD_PLATE
            case 148: // LEGACY_IRON_PLATE
            case 151: // LEGACY_DAYLIGHT_DETECTOR
            case 152: // LEGACY_REDSTONE_BLOCK
            case 153: // LEGACY_QUARTZ_ORE
            case 154: // LEGACY_HOPPER
            case 155: // LEGACY_QUARTZ_BLOCK
            case 156: // LEGACY_QUARTZ_STAIRS
            case 158: // LEGACY_DROPPER
            case 159: // LEGACY_STAINED_CLAY
            case 170: // LEGACY_HAY_BLOCK
            case 172: // LEGACY_HARD_CLAY
            case 173: // LEGACY_COAL_BLOCK
            case 160: // LEGACY_STAINED_GLASS_PANE
            case 161: // LEGACY_LEAVES_2
            case 162: // LEGACY_LOG_2
            case 163: // LEGACY_ACACIA_STAIRS
            case 164: // LEGACY_DARK_OAK_STAIRS
            case 174: // LEGACY_PACKED_ICE
            case 179: // LEGACY_RED_SANDSTONE
            case 165: // LEGACY_SLIME_BLOCK
            case 166: // LEGACY_BARRIER
            case 167: // LEGACY_IRON_TRAPDOOR
            case 168: // LEGACY_PRISMARINE
            case 169: // LEGACY_SEA_LANTERN
            case 181: // LEGACY_DOUBLE_STONE_SLAB2
            case 180: // LEGACY_RED_SANDSTONE_STAIRS
            case 182: // LEGACY_STONE_SLAB2
            case 183: // LEGACY_SPRUCE_FENCE_GATE
            case 184: // LEGACY_BIRCH_FENCE_GATE
            case 185: // LEGACY_JUNGLE_FENCE_GATE
            case 186: // LEGACY_DARK_OAK_FENCE_GATE
            case 187: // LEGACY_ACACIA_FENCE_GATE
            case 188: // LEGACY_SPRUCE_FENCE
            case 189: // LEGACY_BIRCH_FENCE
            case 190: // LEGACY_JUNGLE_FENCE
            case 191: // LEGACY_DARK_OAK_FENCE
            case 192: // LEGACY_ACACIA_FENCE
            case 176: // LEGACY_STANDING_BANNER
            case 177: // LEGACY_WALL_BANNER
            case 178: // LEGACY_DAYLIGHT_DETECTOR_INVERTED
            case 193: // LEGACY_SPRUCE_DOOR
            case 194: // LEGACY_BIRCH_DOOR
            case 195: // LEGACY_JUNGLE_DOOR
            case 196: // LEGACY_ACACIA_DOOR
            case 197: // LEGACY_DARK_OAK_DOOR
            case 201: // LEGACY_PURPUR_BLOCK
            case 202: // LEGACY_PURPUR_PILLAR
            case 203: // LEGACY_PURPUR_STAIRS
            case 204: // LEGACY_PURPUR_DOUBLE_SLAB
            case 205: // LEGACY_PURPUR_SLAB
            case 206: // LEGACY_END_BRICKS
            case 208: // LEGACY_GRASS_PATH
            case 255: // LEGACY_STRUCTURE_BLOCK
            case 210: // LEGACY_COMMAND_REPEATING
            case 211: // LEGACY_COMMAND_CHAIN
            case 212: // LEGACY_FROSTED_ICE
            case 213: // LEGACY_MAGMA
            case 214: // LEGACY_NETHER_WART_BLOCK
            case 215: // LEGACY_RED_NETHER_BRICK
            case 216: // LEGACY_BONE_BLOCK
            case 218: // LEGACY_OBSERVER
            case 219: // LEGACY_WHITE_SHULKER_BOX
            case 220: // LEGACY_ORANGE_SHULKER_BOX
            case 221: // LEGACY_MAGENTA_SHULKER_BOX
            case 222: // LEGACY_LIGHT_BLUE_SHULKER_BOX
            case 223: // LEGACY_YELLOW_SHULKER_BOX
            case 224: // LEGACY_LIME_SHULKER_BOX
            case 225: // LEGACY_PINK_SHULKER_BOX
            case 226: // LEGACY_GRAY_SHULKER_BOX
            case 227: // LEGACY_SILVER_SHULKER_BOX
            case 228: // LEGACY_CYAN_SHULKER_BOX
            case 229: // LEGACY_PURPLE_SHULKER_BOX
            case 230: // LEGACY_BLUE_SHULKER_BOX
            case 231: // LEGACY_BROWN_SHULKER_BOX
            case 232: // LEGACY_GREEN_SHULKER_BOX
            case 233: // LEGACY_RED_SHULKER_BOX
            case 234: // LEGACY_BLACK_SHULKER_BOX
            case 235: // LEGACY_WHITE_GLAZED_TERRACOTTA
            case 236: // LEGACY_ORANGE_GLAZED_TERRACOTTA
            case 237: // LEGACY_MAGENTA_GLAZED_TERRACOTTA
            case 238: // LEGACY_LIGHT_BLUE_GLAZED_TERRACOTTA
            case 239: // LEGACY_YELLOW_GLAZED_TERRACOTTA
            case 240: // LEGACY_LIME_GLAZED_TERRACOTTA
            case 241: // LEGACY_PINK_GLAZED_TERRACOTTA
            case 242: // LEGACY_GRAY_GLAZED_TERRACOTTA
            case 243: // LEGACY_SILVER_GLAZED_TERRACOTTA
            case 244: // LEGACY_CYAN_GLAZED_TERRACOTTA
            case 245: // LEGACY_PURPLE_GLAZED_TERRACOTTA
            case 246: // LEGACY_BLUE_GLAZED_TERRACOTTA
            case 247: // LEGACY_BROWN_GLAZED_TERRACOTTA
            case 248: // LEGACY_GREEN_GLAZED_TERRACOTTA
            case 249: // LEGACY_RED_GLAZED_TERRACOTTA
            case 250: // LEGACY_BLACK_GLAZED_TERRACOTTA
            case 251: // LEGACY_CONCRETE
            case 252: // LEGACY_CONCRETE_POWDER
            //</editor-fold>
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean isAir() {
        return this == LEGACY_AIR;
    }

    @Override
    public boolean isTransparent() {
        if (!isBlock()) {
            return false;
        }

        switch (id) {
            //<editor-fold defaultstate="collapsed" desc="isTransparent">
            case 0: // LEGACY_AIR
            case 6: // LEGACY_SAPLING
            case 27: // LEGACY_POWERED_RAIL
            case 28: // LEGACY_DETECTOR_RAIL
            case 31: // LEGACY_LONG_GRASS
            case 32: // LEGACY_DEAD_BUSH
            case 37: // LEGACY_YELLOW_FLOWER
            case 38: // LEGACY_RED_ROSE
            case 39: // LEGACY_BROWN_MUSHROOM
            case 40: // LEGACY_RED_MUSHROOM
            case 50: // LEGACY_TORCH
            case 51: // LEGACY_FIRE
            case 55: // LEGACY_REDSTONE_WIRE
            case 59: // LEGACY_CROPS
            case 65: // LEGACY_LADDER
            case 66: // LEGACY_RAILS
            case 69: // LEGACY_LEVER
            case 75: // LEGACY_REDSTONE_TORCH_OFF
            case 76: // LEGACY_REDSTONE_TORCH_ON
            case 77: // LEGACY_STONE_BUTTON
            case 78: // LEGACY_SNOW
            case 83: // LEGACY_SUGAR_CANE_BLOCK
            case 90: // LEGACY_PORTAL
            case 93: // LEGACY_DIODE_BLOCK_OFF
            case 94: // LEGACY_DIODE_BLOCK_ON
            case 104: // LEGACY_PUMPKIN_STEM
            case 105: // LEGACY_MELON_STEM
            case 106: // LEGACY_VINE
            case 111: // LEGACY_WATER_LILY
            case 115: // LEGACY_NETHER_WARTS
            case 119: // LEGACY_ENDER_PORTAL
            case 127: // LEGACY_COCOA
            case 131: // LEGACY_TRIPWIRE_HOOK
            case 132: // LEGACY_TRIPWIRE
            case 140: // LEGACY_FLOWER_POT
            case 141: // LEGACY_CARROT
            case 142: // LEGACY_POTATO
            case 143: // LEGACY_WOOD_BUTTON
            case 144: // LEGACY_SKULL
            case 149: // LEGACY_REDSTONE_COMPARATOR_OFF
            case 150: // LEGACY_REDSTONE_COMPARATOR_ON
            case 157: // LEGACY_ACTIVATOR_RAIL
            case 171: // LEGACY_CARPET
            case 175: // LEGACY_DOUBLE_PLANT
            case 198: // LEGACY_END_ROD
            case 199: // LEGACY_CHORUS_PLANT
            case 200: // LEGACY_CHORUS_FLOWER
            case 207: // LEGACY_BEETROOT_BLOCK
            case 209: // LEGACY_END_GATEWAY
            case 217: // LEGACY_STRUCTURE_VOID
            //</editor-fold>
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean isFlammable() {
        if (!isBlock()) {
            return false;
        }

        switch (id) {
            //<editor-fold defaultstate="collapsed" desc="isFlammable">
            case 5: // LEGACY_WOOD
            case 17: // LEGACY_LOG
            case 18: // LEGACY_LEAVES
            case 25: // LEGACY_NOTE_BLOCK
            case 26: // LEGACY_BED_BLOCK
            case 31: // LEGACY_LONG_GRASS
            case 32: // LEGACY_DEAD_BUSH
            case 35: // LEGACY_WOOL
            case 46: // LEGACY_TNT
            case 47: // LEGACY_BOOKSHELF
            case 53: // LEGACY_WOOD_STAIRS
            case 54: // LEGACY_CHEST
            case 58: // LEGACY_WORKBENCH
            case 63: // LEGACY_SIGN_POST
            case 64: // LEGACY_WOODEN_DOOR
            case 68: // LEGACY_WALL_SIGN
            case 72: // LEGACY_WOOD_PLATE
            case 84: // LEGACY_JUKEBOX
            case 85: // LEGACY_FENCE
            case 96: // LEGACY_TRAP_DOOR
            case 99: // LEGACY_HUGE_MUSHROOM_1
            case 100: // LEGACY_HUGE_MUSHROOM_2
            case 106: // LEGACY_VINE
            case 107: // LEGACY_FENCE_GATE
            case 125: // LEGACY_WOOD_DOUBLE_STEP
            case 126: // LEGACY_WOOD_STEP
            case 134: // LEGACY_SPRUCE_WOOD_STAIRS
            case 135: // LEGACY_BIRCH_WOOD_STAIRS
            case 136: // LEGACY_JUNGLE_WOOD_STAIRS
            case 146: // LEGACY_TRAPPED_CHEST
            case 151: // LEGACY_DAYLIGHT_DETECTOR
            case 171: // LEGACY_CARPET
            case 161: // LEGACY_LEAVES_2
            case 162: // LEGACY_LOG_2
            case 163: // LEGACY_ACACIA_STAIRS
            case 164: // LEGACY_DARK_OAK_STAIRS
            case 175: // LEGACY_DOUBLE_PLANT
            case 183: // LEGACY_SPRUCE_FENCE_GATE
            case 184: // LEGACY_BIRCH_FENCE_GATE
            case 185: // LEGACY_JUNGLE_FENCE_GATE
            case 186: // LEGACY_DARK_OAK_FENCE_GATE
            case 187: // LEGACY_ACACIA_FENCE_GATE
            case 188: // LEGACY_SPRUCE_FENCE
            case 189: // LEGACY_BIRCH_FENCE
            case 190: // LEGACY_JUNGLE_FENCE
            case 191: // LEGACY_DARK_OAK_FENCE
            case 192: // LEGACY_ACACIA_FENCE
            case 176: // LEGACY_STANDING_BANNER
            case 177: // LEGACY_WALL_BANNER
            case 178: // LEGACY_DAYLIGHT_DETECTOR_INVERTED
            case 193: // LEGACY_SPRUCE_DOOR
            case 194: // LEGACY_BIRCH_DOOR
            case 195: // LEGACY_JUNGLE_DOOR
            case 196: // LEGACY_ACACIA_DOOR
            case 197: // LEGACY_DARK_OAK_DOOR
            //</editor-fold>
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean isBurnable() {
        if (!isBlock()) {
            return false;
        }

        switch (id) {
            //<editor-fold defaultstate="collapsed" desc="isBurnable">
            case 5: // LEGACY_WOOD
            case 17: // LEGACY_LOG
            case 18: // LEGACY_LEAVES
            case 31: // LEGACY_LONG_GRASS
            case 35: // LEGACY_WOOL
            case 37: // LEGACY_YELLOW_FLOWER
            case 38: // LEGACY_RED_ROSE
            case 46: // LEGACY_TNT
            case 47: // LEGACY_BOOKSHELF
            case 53: // LEGACY_WOOD_STAIRS
            case 85: // LEGACY_FENCE
            case 86: // LEGACY_VINE
            case 125: // LEGACY_WOOD_DOUBLE_STEP
            case 126: // LEGACY_WOOD_STEP
            case 134: // LEGACY_SPRUCE_WOOD_STAIRS
            case 135: // LEGACY_BIRCH_WOOD_STAIRS
            case 136: // LEGACY_JUNGLE_WOOD_STAIRS
            case 170: // LEGACY_HAY_BLOCK
            case 173: // LEGACY_COAL_BLOCK
            case 161: // LEGACY_LEAVES_2
            case 162: // LEGACY_LOG_2
            case 171: // LEGACY_CARPET
            case 175: // LEGACY_DOUBLE_PLANT
            case 32: // LEGACY_DEAD_BUSH
            case 107: // LEGACY_FENCE_GATE
            case 183: // LEGACY_SPRUCE_FENCE_GATE
            case 184: // LEGACY_BIRCH_FENCE_GATE
            case 185: // LEGACY_JUNGLE_FENCE_GATE
            case 186: // LEGACY_DARK_OAK_FENCE_GATE
            case 187: // LEGACY_ACACIA_FENCE_GATE
            case 188: // LEGACY_SPRUCE_FENCE
            case 189: // LEGACY_BIRCH_FENCE
            case 190: // LEGACY_JUNGLE_FENCE
            case 191: // LEGACY_DARK_OAK_FENCE
            case 192: // LEGACY_ACACIA_FENCE
            case 163: // LEGACY_ACACIA_STAIRS
            case 164: // LEGACY_DARK_OAK_STAIRS
            //</editor-fold>
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean isFuel() {
        switch (id) {
            //<editor-fold defaultstate="collapsed" desc="isFuel">
            case 327: // LEGACY_LAVA_BUCKET
            case 173: // LEGACY_COAL_BLOCK
            case 369: // LEGACY_BLAZE_ROD
            case 263: // LEGACY_COAL
            case 333: // LEGACY_BOAT
            case 447: // LEGACY_BOAT_ACACIA
            case 445: // LEGACY_BOAT_BIRCH
            case 448: // LEGACY_BOAT_DARK_OAK
            case 446: // LEGACY_BOAT_JUNGLE
            case 444: // LEGACY_BOAT_SPRUCE
            case 17: // LEGACY_LOG
            case 162: // LEGACY_LOG_2
            case 5: // LEGACY_WOOD
            case 72: // LEGACY_WOOD_PLATE
            case 85: // LEGACY_FENCE
            case 192: // LEGACY_ACACIA_FENCE
            case 189: // LEGACY_BIRCH_FENCE
            case 191: // LEGACY_DARK_OAK_FENCE
            case 190: // LEGACY_JUNGLE_FENCE
            case 188: // LEGACY_SPRUCE_FENCE
            case 107: // LEGACY_FENCE_GATE
            case 187: // LEGACY_ACACIA_FENCE_GATE
            case 184: // LEGACY_BIRCH_FENCE_GATE
            case 186: // LEGACY_DARK_OAK_FENCE_GATE
            case 185: // LEGACY_JUNGLE_FENCE_GATE
            case 183: // LEGACY_SPRUCE_FENCE_GATE
            case 53: // LEGACY_WOOD_STAIRS
            case 163: // LEGACY_ACACIA_STAIRS
            case 135: // LEGACY_BIRCH_WOOD_STAIRS
            case 164: // LEGACY_DARK_OAK_STAIRS
            case 136: // LEGACY_JUNGLE_WOOD_STAIRS
            case 134: // LEGACY_SPRUCE_WOOD_STAIRS
            case 96: // LEGACY_TRAP_DOOR
            case 58: // LEGACY_WORKBENCH
            case 47: // LEGACY_BOOKSHELF
            case 54: // LEGACY_CHEST
            case 146: // LEGACY_TRAPPED_CHEST
            case 151: // LEGACY_DAYLIGHT_DETECTOR
            case 84: // LEGACY_JUKEBOX
            case 25: // LEGACY_NOTE_BLOCK
            case 425: // LEGACY_BANNER
            case 346: // LEGACY_FISHING_ROD
            case 65: // LEGACY_LADDER
            case 268: // LEGACY_WOOD_SWORD
            case 270: // LEGACY_WOOD_PICKAXE
            case 271: // LEGACY_WOOD_AXE
            case 269: // LEGACY_WOOD_SPADE
            case 290: // LEGACY_WOOD_HOE
            case 261: // LEGACY_BOW
            case 323: // LEGACY_SIGN
            case 324: // LEGACY_WOOD_DOOR
            case 430: // LEGACY_ACACIA_DOOR_ITEM
            case 428: // LEGACY_BIRCH_DOOR_ITEM
            case 431: // LEGACY_DARK_OAK_DOOR_ITEM
            case 429: // LEGACY_JUNGLE_DOOR_ITEM
            case 427: // LEGACY_SPRUCE_DOOR_ITEM
            case 126: // LEGACY_WOOD_STEP
            case 6: // LEGACY_SAPLING
            case 280: // LEGACY_STICK
            case 143: // LEGACY_WOOD_BUTTON
            case 35: // LEGACY_WOOL
            case 171: // LEGACY_CARPET
            case 281: // LEGACY_BOWL
            //</editor-fold>
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean isOccluding() {
        if (!isBlock()) {
            return false;
        }

        switch (id) {
            //<editor-fold defaultstate="collapsed" desc="isOccluding">
            case 1: // LEGACY_STONE
            case 2: // LEGACY_GRASS
            case 3: // LEGACY_DIRT
            case 4: // LEGACY_COBBLESTONE
            case 5: // LEGACY_WOOD
            case 7: // LEGACY_BEDROCK
            case 12: // LEGACY_SAND
            case 13: // LEGACY_GRAVEL
            case 14: // LEGACY_GOLD_ORE
            case 15: // LEGACY_IRON_ORE
            case 16: // LEGACY_COAL_ORE
            case 17: // LEGACY_LOG
            case 19: // LEGACY_SPONGE
            case 21: // LEGACY_LAPIS_ORE
            case 22: // LEGACY_LAPIS_BLOCK
            case 23: // LEGACY_DISPENSER
            case 24: // LEGACY_SANDSTONE
            case 25: // LEGACY_NOTE_BLOCK
            case 35: // LEGACY_WOOL
            case 41: // LEGACY_GOLD_BLOCK
            case 42: // LEGACY_IRON_BLOCK
            case 43: // LEGACY_DOUBLE_STEP
            case 45: // LEGACY_BRICK
            case 47: // LEGACY_BOOKSHELF
            case 48: // LEGACY_MOSSY_COBBLESTONE
            case 49: // LEGACY_OBSIDIAN
            case 52: // LEGACY_MOB_SPAWNER
            case 56: // LEGACY_DIAMOND_ORE
            case 57: // LEGACY_DIAMOND_BLOCK
            case 58: // LEGACY_WORKBENCH
            case 61: // LEGACY_FURNACE
            case 62: // LEGACY_BURNING_FURNACE
            case 73: // LEGACY_REDSTONE_ORE
            case 74: // LEGACY_GLOWING_REDSTONE_ORE
            case 80: // LEGACY_SNOW_BLOCK
            case 82: // LEGACY_CLAY
            case 84: // LEGACY_JUKEBOX
            case 86: // LEGACY_PUMPKIN
            case 87: // LEGACY_NETHERRACK
            case 88: // LEGACY_SOUL_SAND
            case 91: // LEGACY_JACK_O_LANTERN
            case 97: // LEGACY_MONSTER_EGGS
            case 98: // LEGACY_SMOOTH_BRICK
            case 99: // LEGACY_HUGE_MUSHROOM_1
            case 100: // LEGACY_HUGE_MUSHROOM_2
            case 103: // LEGACY_MELON_BLOCK
            case 110: // LEGACY_MYCEL
            case 112: // LEGACY_NETHER_BRICK
            case 121: // LEGACY_ENDER_STONE
            case 123: // LEGACY_REDSTONE_LAMP_OFF
            case 124: // LEGACY_REDSTONE_LAMP_ON
            case 125: // LEGACY_WOOD_DOUBLE_STEP
            case 129: // LEGACY_EMERALD_ORE
            case 133: // LEGACY_EMERALD_BLOCK
            case 137: // LEGACY_COMMAND
            case 153: // LEGACY_QUARTZ_ORE
            case 155: // LEGACY_QUARTZ_BLOCK
            case 158: // LEGACY_DROPPER
            case 159: // LEGACY_STAINED_CLAY
            case 170: // LEGACY_HAY_BLOCK
            case 172: // LEGACY_HARD_CLAY
            case 173: // LEGACY_COAL_BLOCK
            case 162: // LEGACY_LOG_2
            case 174: // LEGACY_PACKED_ICE
            case 165: // LEGACY_SLIME_BLOCK
            case 166: // LEGACY_BARRIER
            case 168: // LEGACY_PRISMARINE
            case 179: // LEGACY_RED_SANDSTONE
            case 181: // LEGACY_DOUBLE_STONE_SLAB2
            case 201: // LEGACY_PURPUR_BLOCK
            case 202: // LEGACY_PURPUR_PILLAR
            case 204: // LEGACY_PURPUR_DOUBLE_SLAB
            case 206: // LEGACY_END_BRICKS
            case 255: // LEGACY_STRUCTURE_BLOCK
            case 210: // LEGACY_COMMAND_REPEATING
            case 211: // LEGACY_COMMAND_CHAIN
            case 213: // LEGACY_MAGMA
            case 214: // LEGACY_NETHER_WART_BLOCK
            case 215: // LEGACY_RED_NETHER_BRICK
            case 216: // LEGACY_BONE_BLOCK
            case 235: // LEGACY_WHITE_GLAZED_TERRACOTTA
            case 236: // LEGACY_ORANGE_GLAZED_TERRACOTTA
            case 237: // LEGACY_MAGENTA_GLAZED_TERRACOTTA
            case 238: // LEGACY_LIGHT_BLUE_GLAZED_TERRACOTTA
            case 239: // LEGACY_YELLOW_GLAZED_TERRACOTTA
            case 240: // LEGACY_LIME_GLAZED_TERRACOTTA
            case 241: // LEGACY_PINK_GLAZED_TERRACOTTA
            case 242: // LEGACY_GRAY_GLAZED_TERRACOTTA
            case 243: // LEGACY_SILVER_GLAZED_TERRACOTTA
            case 244: // LEGACY_CYAN_GLAZED_TERRACOTTA
            case 245: // LEGACY_PURPLE_GLAZED_TERRACOTTA
            case 246: // LEGACY_BLUE_GLAZED_TERRACOTTA
            case 247: // LEGACY_BROWN_GLAZED_TERRACOTTA
            case 248: // LEGACY_GREEN_GLAZED_TERRACOTTA
            case 249: // LEGACY_RED_GLAZED_TERRACOTTA
            case 250: // LEGACY_BLACK_GLAZED_TERRACOTTA
            case 251: // LEGACY_CONCRETE
            case 252: // LEGACY_CONCRETE_POWDER
            //</editor-fold>
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean hasGravity() {
        if (!isBlock()) {
            return false;
        }

        switch (id) {
            //<editor-fold defaultstate="collapsed" desc="hasGravity">
            case 12: // LEGACY_SAND
            case 13: // LEGACY_GRAVEL
            case 145: // LEGACY_ANVIL
            case 252: // LEGACY_CONCRETE_POWDER
                //</editor-fold>
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean isItem() {
        switch (id) {
            //<editor-fold defaultstate="collapsed" desc="isItem">
            case 196: // LEGACY_ACACIA_DOOR
            case 26: // LEGACY_BED_BLOCK
            case 207: // LEGACY_BEETROOT_BLOCK
            case 194: // LEGACY_BIRCH_DOOR
            case 117: // LEGACY_BREWING_STAND
            case 62: // LEGACY_BURNING_FURNACE
            case 92: // LEGACY_CAKE_BLOCK
            case 141: // LEGACY_CARROT
            case 118: // LEGACY_CAULDRON
            case 127: // LEGACY_COCOA
            case 59: // LEGACY_CROPS
            case 197: // LEGACY_DARK_OAK_DOOR
            case 178: // LEGACY_DAYLIGHT_DETECTOR_INVERTED
            case 93: // LEGACY_DIODE_BLOCK_OFF
            case 94: // LEGACY_DIODE_BLOCK_ON
            case 43: // LEGACY_DOUBLE_STEP
            case 181: // LEGACY_DOUBLE_STONE_SLAB2
            case 119: // LEGACY_ENDER_PORTAL
            case 209: // LEGACY_END_GATEWAY
            case 51: // LEGACY_FIRE
            case 140: // LEGACY_FLOWER_POT
            case 212: // LEGACY_FROSTED_ICE
            case 74: // LEGACY_GLOWING_REDSTONE_ORE
            case 71: // LEGACY_IRON_DOOR_BLOCK
            case 195: // LEGACY_JUNGLE_DOOR
            case 10: // LEGACY_LAVA
            case 105: // LEGACY_MELON_STEM
            case 115: // LEGACY_NETHER_WARTS
            case 34: // LEGACY_PISTON_EXTENSION
            case 36: // LEGACY_PISTON_MOVING_PIECE
            case 90: // LEGACY_PORTAL
            case 142: // LEGACY_POTATO
            case 104: // LEGACY_PUMPKIN_STEM
            case 204: // LEGACY_PURPUR_DOUBLE_SLAB
            case 149: // LEGACY_REDSTONE_COMPARATOR_OFF
            case 150: // LEGACY_REDSTONE_COMPARATOR_ON
            case 124: // LEGACY_REDSTONE_LAMP_ON
            case 75: // LEGACY_REDSTONE_TORCH_OFF
            case 55: // LEGACY_REDSTONE_WIRE
            case 63: // LEGACY_SIGN_POST
            case 144: // LEGACY_SKULL
            case 193: // LEGACY_SPRUCE_DOOR
            case 176: // LEGACY_STANDING_BANNER
            case 11: // LEGACY_STATIONARY_LAVA
            case 9: // LEGACY_STATIONARY_WATER
            case 83: // LEGACY_SUGAR_CANE_BLOCK
            case 132: // LEGACY_TRIPWIRE
            case 177: // LEGACY_WALL_BANNER
            case 68: // LEGACY_WALL_SIGN
            case 8: // LEGACY_WATER
            case 64: // LEGACY_WOODEN_DOOR
            case 125: // LEGACY_WOOD_DOUBLE_STEP
            // </editor-fold>
                return false;
            default:
                return true;
        }
    }

    @Override
    public ItemType asItemType() {
        throw new IllegalArgumentException("Cannot get ItemType of Legacy material");
    }

    @Override
    public boolean isInteractable() {
        return false;
    }

    @Override
    public float getHardness() {
        Preconditions.checkArgument(isBlock(), "The Material is not a block!");
        return 0F;
    }

    @Override
    public float getBlastResistance() {
        Preconditions.checkArgument(isBlock(), "The Material is not a block!");
        return 0F;
    }

    @Override
    public float getSlipperiness() {
        Preconditions.checkArgument(isBlock(), "The Material is not a block!");
        return 0.6F;
    }

    @Override
    public Material getCraftingRemainingItem() {
        Preconditions.checkArgument(isItem(), "The Material is not an item!");
        return null;
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        Preconditions.checkArgument(isItem(), "The Material is not an item!");
        return EquipmentSlot.HAND;
    }

    @Override
    public int compareTo(Material material) {
        return ordinal - material.ordinal();
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int ordinal() {
        return ordinal;
    }

    @Override
    public BlockType<?> asBlockType() {
        throw new IllegalArgumentException("Cannot get ItemType of Legacy material");
    }

    @Override
    public NamespacedKey getKey() {
        throw new IllegalArgumentException("Cannot get key of Legacy Material");
    }

    @Override
    public String toString() {
        // For backwards compatibility
        return name();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof CraftLegacyMaterial)) {
            return false;
        }

        return id == ((CraftLegacyMaterial) other).id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}

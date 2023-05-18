package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.block.CraftBanner;
import org.bukkit.craftbukkit.block.CraftBlockEntityState;
import org.bukkit.craftbukkit.block.CraftBlockStates;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.BlockStateMeta;

@DelegateDeserialization(CraftMetaItem.SerializableMeta.class)
public class CraftMetaBlockState extends CraftMetaItem implements BlockStateMeta {

    private static final Set<ItemType> SHULKER_BOX_ITEM_TYPES = Sets.newHashSet(
            ItemType.SHULKER_BOX,
            ItemType.WHITE_SHULKER_BOX,
            ItemType.ORANGE_SHULKER_BOX,
            ItemType.MAGENTA_SHULKER_BOX,
            ItemType.LIGHT_BLUE_SHULKER_BOX,
            ItemType.YELLOW_SHULKER_BOX,
            ItemType.LIME_SHULKER_BOX,
            ItemType.PINK_SHULKER_BOX,
            ItemType.GRAY_SHULKER_BOX,
            ItemType.LIGHT_GRAY_SHULKER_BOX,
            ItemType.CYAN_SHULKER_BOX,
            ItemType.PURPLE_SHULKER_BOX,
            ItemType.BLUE_SHULKER_BOX,
            ItemType.BROWN_SHULKER_BOX,
            ItemType.GREEN_SHULKER_BOX,
            ItemType.RED_SHULKER_BOX,
            ItemType.BLACK_SHULKER_BOX
    );

    private static final Set<ItemType> BLOCK_STATE_ITEM_TYPES = Sets.newHashSet(
            ItemType.FURNACE,
            ItemType.CHEST,
            ItemType.TRAPPED_CHEST,
            ItemType.JUKEBOX,
            ItemType.DISPENSER,
            ItemType.DROPPER,
            ItemType.ACACIA_HANGING_SIGN,
            ItemType.ACACIA_SIGN,
            ItemType.BAMBOO_HANGING_SIGN,
            ItemType.BAMBOO_SIGN,
            ItemType.BIRCH_HANGING_SIGN,
            ItemType.BIRCH_SIGN,
            ItemType.CHERRY_HANGING_SIGN,
            ItemType.CHERRY_SIGN,
            ItemType.CRIMSON_HANGING_SIGN,
            ItemType.CRIMSON_SIGN,
            ItemType.DARK_OAK_HANGING_SIGN,
            ItemType.DARK_OAK_SIGN,
            ItemType.JUNGLE_HANGING_SIGN,
            ItemType.JUNGLE_SIGN,
            ItemType.MANGROVE_HANGING_SIGN,
            ItemType.MANGROVE_SIGN,
            ItemType.OAK_HANGING_SIGN,
            ItemType.OAK_SIGN,
            ItemType.SPRUCE_HANGING_SIGN,
            ItemType.SPRUCE_SIGN,
            ItemType.WARPED_HANGING_SIGN,
            ItemType.WARPED_SIGN,
            ItemType.SPAWNER,
            ItemType.BREWING_STAND,
            ItemType.ENCHANTING_TABLE,
            ItemType.COMMAND_BLOCK,
            ItemType.REPEATING_COMMAND_BLOCK,
            ItemType.CHAIN_COMMAND_BLOCK,
            ItemType.BEACON,
            ItemType.DAYLIGHT_DETECTOR,
            ItemType.HOPPER,
            ItemType.COMPARATOR,
            ItemType.SHIELD,
            ItemType.STRUCTURE_BLOCK,
            ItemType.ENDER_CHEST,
            ItemType.BARREL,
            ItemType.BELL,
            ItemType.BLAST_FURNACE,
            ItemType.CAMPFIRE,
            ItemType.SOUL_CAMPFIRE,
            ItemType.JIGSAW,
            ItemType.LECTERN,
            ItemType.SMOKER,
            ItemType.BEEHIVE,
            ItemType.BEE_NEST,
            ItemType.SCULK_CATALYST,
            ItemType.SCULK_SHRIEKER,
            ItemType.SCULK_SENSOR,
            ItemType.CHISELED_BOOKSHELF,
            ItemType.DECORATED_POT,
            ItemType.SUSPICIOUS_SAND
    );

    static {
        // Add shulker boxes to the list of block state materials too
        BLOCK_STATE_ITEM_TYPES.addAll(SHULKER_BOX_ITEM_TYPES);
    }

    @ItemMetaKey.Specific(ItemMetaKey.Specific.To.NBT)
    static final ItemMetaKey BLOCK_ENTITY_TAG = new ItemMetaKey("BlockEntityTag");

    final ItemType itemType;
    NBTTagCompound blockEntityTag;

    CraftMetaBlockState(CraftMetaItem meta, ItemType itemType) {
        super(meta);
        this.itemType = itemType;

        if (!(meta instanceof CraftMetaBlockState)
                || ((CraftMetaBlockState) meta).itemType != itemType) {
            blockEntityTag = null;
            return;
        }

        CraftMetaBlockState te = (CraftMetaBlockState) meta;
        this.blockEntityTag = te.blockEntityTag;
    }

    CraftMetaBlockState(NBTTagCompound tag, ItemType itemType) {
        super(tag);
        this.itemType = itemType;

        if (tag.contains(BLOCK_ENTITY_TAG.NBT, CraftMagicNumbers.NBT.TAG_COMPOUND)) {
            blockEntityTag = tag.getCompound(BLOCK_ENTITY_TAG.NBT).copy();
        } else {
            blockEntityTag = null;
        }
    }

    CraftMetaBlockState(Map<String, Object> map) {
        super(map);
        String matName = SerializableMeta.getString(map, "blockMaterial", true);
        Material m = Material.getMaterial(matName);
        if (m != null) {
            itemType = m.asItemType();
        } else {
            ItemType type = Registry.ITEM_TYPE.get(NamespacedKey.fromString(matName));
            if (type != null) {
                itemType = type;
            } else {
                itemType = ItemType.AIR;
            }
        }
    }

    @Override
    void applyToItem(NBTTagCompound tag) {
        super.applyToItem(tag);

        if (blockEntityTag != null) {
            tag.put(BLOCK_ENTITY_TAG.NBT, blockEntityTag);
        }
    }

    @Override
    void deserializeInternal(NBTTagCompound tag, Object context) {
        super.deserializeInternal(tag, context);

        if (tag.contains(BLOCK_ENTITY_TAG.NBT, CraftMagicNumbers.NBT.TAG_COMPOUND)) {
            blockEntityTag = tag.getCompound(BLOCK_ENTITY_TAG.NBT);
        }
    }

    @Override
    void serializeInternal(final Map<String, NBTBase> internalTags) {
        if (blockEntityTag != null) {
            internalTags.put(BLOCK_ENTITY_TAG.NBT, blockEntityTag);
        }
    }

    @Override
    ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
        super.serialize(builder);
        builder.put("blockMaterial", itemType.getKey());
        return builder;
    }

    @Override
    int applyHash() {
        final int original;
        int hash = original = super.applyHash();
        if (blockEntityTag != null) {
            hash = 61 * hash + this.blockEntityTag.hashCode();
        }
        return original != hash ? CraftMetaBlockState.class.hashCode() ^ hash : hash;
    }

    @Override
    public boolean equalsCommon(CraftMetaItem meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }
        if (meta instanceof CraftMetaBlockState) {
            CraftMetaBlockState that = (CraftMetaBlockState) meta;

            return Objects.equal(this.blockEntityTag, that.blockEntityTag);
        }
        return true;
    }

    @Override
    boolean notUncommon(CraftMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof CraftMetaBlockState || blockEntityTag == null);
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && blockEntityTag == null;
    }

    @Override
    boolean applicableTo(ItemType type) {
        return BLOCK_STATE_ITEM_TYPES.contains(type);
    }

    @Override
    public CraftMetaBlockState clone() {
        CraftMetaBlockState meta = (CraftMetaBlockState) super.clone();
        if (blockEntityTag != null) {
            meta.blockEntityTag = blockEntityTag.copy();
        }
        return meta;
    }

    @Override
    public boolean hasBlockState() {
        return blockEntityTag != null;
    }

    @Override
    public BlockState getBlockState() {
        ItemType stateType = (itemType != ItemType.SHIELD) ? itemType : shieldToBannerHack(blockEntityTag); // Only actually used for jigsaws
        if (blockEntityTag != null) {
            if (itemType == ItemType.SHIELD) {
                blockEntityTag.putString("id", "minecraft:banner");
            } else if (itemType == ItemType.BEE_NEST || itemType == ItemType.BEEHIVE) {
                blockEntityTag.putString("id", "minecraft:beehive");
            } else if (SHULKER_BOX_ITEM_TYPES.contains(itemType)) {
                blockEntityTag.putString("id", "minecraft:shulker_box");
            }
        }

        // This is expected to always return a CraftBlockEntityState for the passed material:
        return CraftBlockStates.getBlockState(stateType.getBlockType(), blockEntityTag);
    }

    @Override
    public void setBlockState(BlockState blockState) {
        Validate.notNull(blockState, "blockState must not be null");

        ItemType stateType = (itemType != ItemType.SHIELD) ? itemType : shieldToBannerHack(blockEntityTag);
        Class<?> blockStateType = CraftBlockStates.getBlockStateType(stateType.getBlockType());
        Validate.isTrue(blockStateType == blockState.getClass() && blockState instanceof CraftBlockEntityState, "Invalid blockState for " + itemType.getKey());

        blockEntityTag = ((CraftBlockEntityState) blockState).getSnapshotNBT();
        // Set shield base
        if (itemType == ItemType.SHIELD) {
            blockEntityTag.putInt(CraftMetaBanner.BASE.NBT, ((CraftBanner) blockState).getBaseColor().getWoolData());
        }
    }

    private static ItemType shieldToBannerHack(NBTTagCompound tag) {
        if (tag == null || !tag.contains(CraftMetaBanner.BASE.NBT, CraftMagicNumbers.NBT.TAG_INT)) {
            return ItemType.WHITE_BANNER;
        }

        switch (tag.getInt(CraftMetaBanner.BASE.NBT)) {
            case 0:
                return ItemType.WHITE_BANNER;
            case 1:
                return ItemType.ORANGE_BANNER;
            case 2:
                return ItemType.MAGENTA_BANNER;
            case 3:
                return ItemType.LIGHT_BLUE_BANNER;
            case 4:
                return ItemType.YELLOW_BANNER;
            case 5:
                return ItemType.LIME_BANNER;
            case 6:
                return ItemType.PINK_BANNER;
            case 7:
                return ItemType.GRAY_BANNER;
            case 8:
                return ItemType.LIGHT_GRAY_BANNER;
            case 9:
                return ItemType.CYAN_BANNER;
            case 10:
                return ItemType.PURPLE_BANNER;
            case 11:
                return ItemType.BLUE_BANNER;
            case 12:
                return ItemType.BROWN_BANNER;
            case 13:
                return ItemType.GREEN_BANNER;
            case 14:
                return ItemType.RED_BANNER;
            case 15:
                return ItemType.BLACK_BANNER;
            default:
                throw new IllegalArgumentException("Unknown banner colour");
        }
    }
}

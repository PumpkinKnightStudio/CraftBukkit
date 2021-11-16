package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.block.CraftBanner;
import org.bukkit.craftbukkit.block.CraftBlockEntityState;
import org.bukkit.craftbukkit.block.CraftBlockStates;
import org.bukkit.craftbukkit.inventory.CraftMetaItem.ItemMetaKey;
import org.bukkit.craftbukkit.inventory.CraftMetaItem.SerializableMeta;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.inventory.meta.BlockStateMeta;

@DelegateDeserialization(CraftMetaItem.SerializableMeta.class)
public class CraftMetaBlockState extends CraftMetaItem implements BlockStateMeta {

    @ItemMetaKey.Specific(ItemMetaKey.Specific.To.NBT)
    static final ItemMetaKey BLOCK_ENTITY_TAG = new ItemMetaKey("BlockEntityTag");

    final Material material;
    NBTTagCompound blockEntityTag;

    CraftMetaBlockState(CraftMetaItem meta, Material material) {
        super(meta);
        this.material = material;

        if (!(meta instanceof CraftMetaBlockState)
                || ((CraftMetaBlockState) meta).material != material) {
            blockEntityTag = null;
            return;
        }

        CraftMetaBlockState te = (CraftMetaBlockState) meta;
        this.blockEntityTag = te.blockEntityTag;
    }

    CraftMetaBlockState(NBTTagCompound tag, Material material) {
        super(tag);
        this.material = material;

        if (tag.hasKeyOfType(BLOCK_ENTITY_TAG.NBT, CraftMagicNumbers.NBT.TAG_COMPOUND)) {
            blockEntityTag = tag.getCompound(BLOCK_ENTITY_TAG.NBT);
        } else {
            blockEntityTag = null;
        }
    }

    CraftMetaBlockState(Map<String, Object> map) {
        super(map);
        String matName = SerializableMeta.getString(map, "blockMaterial", true);
        Material m = Material.getMaterial(matName);
        if (m != null) {
            material = m;
        } else {
            material = Material.AIR;
        }
    }

    @Override
    void applyToItem(NBTTagCompound tag) {
        super.applyToItem(tag);

        if (blockEntityTag != null) {
            tag.set(BLOCK_ENTITY_TAG.NBT, blockEntityTag);
        }
    }

    @Override
    void deserializeInternal(NBTTagCompound tag, Object context) {
        super.deserializeInternal(tag, context);

        if (tag.hasKeyOfType(BLOCK_ENTITY_TAG.NBT, CraftMagicNumbers.NBT.TAG_COMPOUND)) {
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
        builder.put("blockMaterial", material.name());
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
    boolean applicableTo(Material type) {
        if (type == Material.FURNACE || type == Material.CHEST
                || type == Material.TRAPPED_CHEST || type == Material.JUKEBOX
                || type == Material.DISPENSER || type == Material.DROPPER
                || type == Material.ACACIA_SIGN || type == Material.ACACIA_WALL_SIGN
                || type == Material.BIRCH_SIGN || type == Material.BIRCH_WALL_SIGN
                || type == Material.CRIMSON_SIGN || type == Material.CRIMSON_WALL_SIGN
                || type == Material.DARK_OAK_SIGN || type == Material.DARK_OAK_WALL_SIGN
                || type == Material.JUNGLE_SIGN || type == Material.JUNGLE_WALL_SIGN
                || type == Material.OAK_SIGN || type == Material.OAK_WALL_SIGN
                || type == Material.SPRUCE_SIGN || type == Material.SPRUCE_WALL_SIGN
                || type == Material.WARPED_SIGN || type == Material.WARPED_WALL_SIGN
                || type == Material.SPAWNER || type == Material.BREWING_STAND
                || type == Material.ENCHANTING_TABLE || type == Material.COMMAND_BLOCK
                || type == Material.REPEATING_COMMAND_BLOCK || type == Material.CHAIN_COMMAND_BLOCK
                || type == Material.BEACON || type == Material.DAYLIGHT_DETECTOR
                || type == Material.HOPPER || type == Material.COMPARATOR
                || type == Material.SHIELD || type == Material.STRUCTURE_BLOCK
                || type == Material.SHULKER_BOX || type == Material.WHITE_SHULKER_BOX
                || type == Material.ORANGE_SHULKER_BOX || type == Material.MAGENTA_SHULKER_BOX
                || type == Material.LIGHT_BLUE_SHULKER_BOX || type == Material.YELLOW_SHULKER_BOX
                || type == Material.LIME_SHULKER_BOX || type == Material.PINK_SHULKER_BOX
                || type == Material.GRAY_SHULKER_BOX || type == Material.LIGHT_GRAY_SHULKER_BOX
                || type == Material.CYAN_SHULKER_BOX || type == Material.PURPLE_SHULKER_BOX
                || type == Material.BLUE_SHULKER_BOX || type == Material.BROWN_SHULKER_BOX
                || type == Material.GREEN_SHULKER_BOX || type == Material.RED_SHULKER_BOX
                || type == Material.BLACK_SHULKER_BOX || type == Material.ENDER_CHEST
                || type == Material.BARREL || type == Material.BELL
                || type == Material.BLAST_FURNACE || type == Material.CAMPFIRE
                || type == Material.SOUL_CAMPFIRE || type == Material.JIGSAW
                || type == Material.LECTERN || type == Material.SMOKER
                || type == Material.BEEHIVE || type == Material.BEE_NEST
                || type == Material.SCULK_SENSOR) {
                return true;
        }

        return false;
    }

    @Override
    public CraftMetaBlockState clone() {
        CraftMetaBlockState meta = (CraftMetaBlockState) super.clone();
        if (blockEntityTag != null) {
            meta.blockEntityTag = blockEntityTag.clone();
        }
        return meta;
    }

    @Override
    public boolean hasBlockState() {
        return blockEntityTag != null;
    }

    @Override
    public BlockState getBlockState() {
        Material stateMaterial = (material != Material.SHIELD) ? material : shieldToBannerHack(blockEntityTag); // Only actually used for jigsaws
        if (blockEntityTag != null) {
            if (material == Material.SHIELD) {
                blockEntityTag.setString("id", "banner");
            } else if (material == Material.SHULKER_BOX || material == Material.WHITE_SHULKER_BOX
                    || material == Material.ORANGE_SHULKER_BOX || material == Material.MAGENTA_SHULKER_BOX
                    || material == Material.LIGHT_BLUE_SHULKER_BOX || material == Material.YELLOW_SHULKER_BOX
                    || material == Material.LIME_SHULKER_BOX || material == Material.PINK_SHULKER_BOX
                    || material == Material.GRAY_SHULKER_BOX || material == Material.LIGHT_GRAY_SHULKER_BOX
                    || material == Material.CYAN_SHULKER_BOX || material == Material.PURPLE_SHULKER_BOX
                    || material == Material.BLUE_SHULKER_BOX || material == Material.BROWN_SHULKER_BOX
                    || material == Material.GREEN_SHULKER_BOX || material == Material.RED_SHULKER_BOX
                    || material == Material.BLACK_SHULKER_BOX) {
                blockEntityTag.setString("id", "shulker_box");
            } else if (material == Material.BEE_NEST || material == Material.BEEHIVE) {
                blockEntityTag.setString("id", "beehive");
            }
        }

        // This is expected to always return a CraftBlockEntityState for the passed material:
        return CraftBlockStates.getBlockState(stateMaterial, blockEntityTag);
    }

    @Override
    public void setBlockState(BlockState blockState) {
        Validate.notNull(blockState, "blockState must not be null");

        Material stateMaterial = (material != Material.SHIELD) ? material : shieldToBannerHack(blockEntityTag);
        Class<?> blockStateType = CraftBlockStates.getBlockStateType(stateMaterial);
        Validate.isTrue(blockStateType == blockState.getClass() && blockState instanceof CraftBlockEntityState, "Invalid blockState for " + material);

        blockEntityTag = ((CraftBlockEntityState) blockState).getSnapshotNBT();
        // Set shield base
        if (material == Material.SHIELD) {
            blockEntityTag.setInt(CraftMetaBanner.BASE.NBT, ((CraftBanner) blockState).getBaseColor().getWoolData());
        }
    }

    private static Material shieldToBannerHack(NBTTagCompound tag) {
        if (tag == null || !tag.hasKeyOfType(CraftMetaBanner.BASE.NBT, CraftMagicNumbers.NBT.TAG_INT)) {
            return Material.WHITE_BANNER;
        }

        switch (tag.getInt(CraftMetaBanner.BASE.NBT)) {
            case 0:
                return Material.WHITE_BANNER;
            case 1:
                return Material.ORANGE_BANNER;
            case 2:
                return Material.MAGENTA_BANNER;
            case 3:
                return Material.LIGHT_BLUE_BANNER;
            case 4:
                return Material.YELLOW_BANNER;
            case 5:
                return Material.LIME_BANNER;
            case 6:
                return Material.PINK_BANNER;
            case 7:
                return Material.GRAY_BANNER;
            case 8:
                return Material.LIGHT_GRAY_BANNER;
            case 9:
                return Material.CYAN_BANNER;
            case 10:
                return Material.PURPLE_BANNER;
            case 11:
                return Material.BLUE_BANNER;
            case 12:
                return Material.BROWN_BANNER;
            case 13:
                return Material.GREEN_BANNER;
            case 14:
                return Material.RED_BANNER;
            case 15:
                return Material.BLACK_BANNER;
            default:
                throw new IllegalArgumentException("Unknown banner colour");
        }
    }
}

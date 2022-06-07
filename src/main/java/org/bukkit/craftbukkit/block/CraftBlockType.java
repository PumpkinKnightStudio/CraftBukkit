package org.bukkit.craftbukkit.block;

import com.google.common.collect.Multimap;
import java.util.function.Consumer;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.BlockAccessAir;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockFalling;
import net.minecraft.world.level.block.BlockFire;
import net.minecraft.world.level.block.Blocks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.CraftMaterial;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.inventory.CreativeCategory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemType;
import org.bukkit.material.MaterialData;

public class CraftBlockType<B extends BlockData> implements BlockType<B> {

    private final NamespacedKey key;
    private final Block block;
    private final Class<B> blockDataClass;
    private final String name;
    private final int ordinal;
    private final boolean interactable;

    public CraftBlockType(NamespacedKey key, Block block) {
        this.key = key;
        this.block = block;
        this.blockDataClass = (Class<B>) CraftBlockData.fromData(block.defaultBlockState()).getClass().getInterfaces()[0];
        // For backwards compatibility, minecraft values will stile return the uppercase name without the namespace,
        // in case plugins use for example the name as key in a config file to receive material specific values.
        // Custom materials will return the key with namespace. For a plugin this should look than like a new material
        // (which can always be added in new minecraft versions and the plugin should therefore handle it accordingly).
        if (NamespacedKey.MINECRAFT.equals(key.getNamespace())) {
            this.name = key.getKey().toUpperCase();
        } else {
            this.name = key.toString();
        }
        this.ordinal = CraftMaterial.getNextOrdinal();
        this.interactable = CraftMaterial.isInteractable(block);
    }

    @Override
    public boolean isLegacy() {
        return false;
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public short getMaxDurability() {
        return 0;
    }

    @Override
    public Class<B> getBlockDataClass() {
        return blockDataClass;
    }

    @Override
    public B createBlockData() {
        return (B) Bukkit.createBlockData(this);
    }

    @Override
    public B createBlockData(Consumer<BlockData> consumer) {
        return (B) Bukkit.createBlockData(this, consumer);
    }

    @Override
    public B createBlockData(String data) {
        return (B) Bukkit.createBlockData(this, data);
    }

    @Override
    public boolean isBlock() {
        return true;
    }

    @Override
    public boolean isEdible() {
        return false;
    }

    @Override
    public boolean isRecord() {
        return false;
    }

    @Override
    public boolean isSolid() {
        return block.defaultBlockState().getMaterial().blocksMotion();
    }

    @Override
    public boolean isAir() {
        return block.defaultBlockState().isAir();
    }

    @Override
    public boolean isTransparent() {
        return block.defaultBlockState().getMaterial().isSolidBlocking();
    }

    @Override
    public boolean isFlammable() {
        return block.defaultBlockState().getMaterial().isFlammable();
    }

    @Override
    public boolean isBurnable() {
        return ((BlockFire) Blocks.FIRE).igniteOdds.getOrDefault(block, 0) > 0;
    }

    @Override
    public boolean isFuel() {
        return false;
    }

    @Override
    public boolean isOccluding() {
        return block.defaultBlockState().isRedstoneConductor(BlockAccessAir.INSTANCE, BlockPosition.ZERO);
    }

    @Override
    public boolean hasGravity() {
        return block instanceof BlockFalling;
    }

    @Override
    public boolean isItem() {
        return false;
    }

    @Override
    public ItemType asItemType() {
        throw new UnsupportedOperationException("Material " + this + " is not an item");
    }

    @Override
    public boolean isInteractable() {
        return interactable;
    }

    @Override
    public float getHardness() {
        return block.defaultBlockState().destroySpeed;
    }

    @Override
    public float getBlastResistance() {
        return block.getExplosionResistance();
    }

    @Override
    public float getSlipperiness() {
        return block.getFriction();
    }

    @Override
    public Material getCraftingRemainingItem() {
        throw new IllegalArgumentException("The Material is not an item!");
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        throw new IllegalArgumentException("The Material is not an item!");
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        throw new IllegalArgumentException("The Material is not an item!");
    }

    @Override
    public CreativeCategory getCreativeCategory() {
        throw new IllegalArgumentException("The Material is not an item!");
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
    public BlockType<B> asBlockType() {
        return this;
    }

    @Override
    public NamespacedKey getKey() {
        return key;
    }

    @Override
    public int getId() {
        throw new IllegalArgumentException("Cannot get ID of Modern Material");
    }

    @Override
    public Class<? extends MaterialData> getData() {
        throw new IllegalArgumentException("Cannot get data class of Modern Material");
    }

    @Override
    public MaterialData getNewData(byte b) {
        throw new IllegalArgumentException("Cannot get new data of Modern Material");
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

        if (!(other instanceof CraftBlockType)) {
            return false;
        }

        return getKey().equals(((CraftBlockType) other).getKey());
    }

    @Override
    public int hashCode() {
        return getKey().hashCode();
    }
}

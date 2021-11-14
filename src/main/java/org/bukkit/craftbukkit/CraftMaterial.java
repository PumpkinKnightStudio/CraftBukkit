package org.bukkit.craftbukkit;

import java.util.function.Consumer;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.EnumHand;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemRecord;
import net.minecraft.world.level.BlockAccessAir;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockFalling;
import net.minecraft.world.level.block.BlockFire;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntityFurnace;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import org.bukkit.Art;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.material.MaterialData;

public class CraftMaterial<B extends BlockData> implements BlockType<B>, ItemType {

    private static int count = 0;

    public static int getNextOrdinal() {
        return count++;
    }

    private final NamespacedKey key;
    private final Block block;
    private final Item item;
    private final String name;
    private final int ordinal;

    public CraftMaterial(NamespacedKey key, Block block, Item item) {
        this.key = key;
        this.block = block;
        this.item = item;
        // For backwards compatibility, minecraft values will stile return the uppercase name without the namespace,
        // in case plugins use for example the name as key in a config file to receive material specific values.
        // Custom materials will return the key with namespace. For a plugin this should look than like a new material
        // (which can always be added in new minecraft versions and the plugin should therefore handle it accordingly).
        if (NamespacedKey.MINECRAFT.equals(key.getNamespace())) {
            this.name = key.getKey().toUpperCase();
        } else {
            this.name = key.toString();
        }
        this.ordinal = getNextOrdinal();
    }

    @Override
    public boolean isLegacy() {
        return false;
    }

    @Override
    public int getMaxStackSize() {
        return item.getMaxStackSize();
    }

    @Override
    public short getMaxDurability() {
        return (short) item.getMaxDurability();
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
    public boolean isBlock() {
        return true;
    }

    @Override
    public boolean isEdible() {
        return item.isFood();
    }

    @Override
    public boolean isRecord() {
        return item instanceof ItemRecord;
    }

    @Override
    public boolean isSolid() {
        return block.getBlockData().getMaterial().isSolid();
    }

    @Override
    public boolean isAir() {
        return block.getBlockData().isAir();
    }

    @Override
    public boolean isTransparent() {
        return block.getBlockData().getMaterial().f();
    }

    @Override
    public boolean isFlammable() {
        return block.getBlockData().getMaterial().isBurnable();
    }

    @Override
    public boolean isBurnable() {
        return ((BlockFire) Blocks.FIRE).flameOdds.containsKey(block) && ((BlockFire) Blocks.FIRE).flameOdds.get(block) > 0;
    }

    @Override
    public boolean isFuel() {
        return TileEntityFurnace.isFuel(new net.minecraft.world.item.ItemStack(item));
    }

    @Override
    public boolean isOccluding() {
        return block.getBlockData().isOccluding(BlockAccessAir.INSTANCE, BlockPosition.ZERO);
    }

    @Override
    public boolean hasGravity() {
        return block instanceof BlockFalling;
    }

    @Override
    public boolean isItem() {
        return true;
    }

    @Override
    public ItemType asItemType() {
        return this;
    }

    @Override
    public boolean isInteractable() {
        try {
            return !block.getClass()
                    .getMethod("interact", IBlockData.class, net.minecraft.world.level.World.class, BlockPosition.class, EntityHuman.class, EnumHand.class, MovingObjectPositionBlock.class)
                    .getDeclaringClass().equals(BlockBase.class);
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    @Override
    public float getHardness() {
        return block.getBlockData().destroySpeed;
    }

    @Override
    public float getBlastResistance() {
        return block.getDurability();
    }

    @Override
    public float getSlipperiness() {
        return block.getFrictionFactor();
    }

    @Override
    public Material getCraftingRemainingItem() {
        Item expectedItem = item.getCraftingRemainingItem();
        return expectedItem == null ? null : CraftMagicNumbers.getMaterial(expectedItem);
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return CraftEquipmentSlot.getSlot(EntityInsentient.getEquipmentSlotForItem(CraftItemStack.asNMSCopy(new ItemStack(this))));
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

        if (!(other instanceof CraftMaterial)) {
            return false;
        }

        return getKey().equals(((CraftMaterial) other).getKey());
    }

    @Override
    public int hashCode() {
        return getKey().hashCode();
    }
}

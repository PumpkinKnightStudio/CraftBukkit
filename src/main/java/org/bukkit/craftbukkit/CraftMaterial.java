package org.bukkit.craftbukkit;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
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
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.block.CraftBlockType;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftItemType;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.material.MaterialData;

public class CraftMaterial<B extends BlockData> implements BlockType<B>, ItemType {

    private static int count = 0;

    public static int getNextOrdinal() {
        return count++;
    }

    public static boolean isInteractable(Block block) {
        try {
            return !block.getClass()
                    .getMethod("use", IBlockData.class, net.minecraft.world.level.World.class, BlockPosition.class, EntityHuman.class, EnumHand.class, MovingObjectPositionBlock.class)
                    .getDeclaringClass().equals(BlockBase.class);
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    private final NamespacedKey key;
    private final Block block;
    private final Item item;
    private final Class<B> blockDataClass;
    private final String name;
    private final int ordinal;
    private final boolean interactable;

    public CraftMaterial(NamespacedKey key, Block block, Item item) {
        this.key = key;
        this.block = block;
        this.blockDataClass = (Class<B>) CraftBlockData.fromData(block.defaultBlockState()).getClass().getInterfaces()[0];
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
        this.interactable = isInteractable(block);
    }

    @Override
    public boolean isLegacy() {
        return false;
    }

    @Override
    public int getMaxStackSize() {
        if (this == Material.AIR) {
            return 0;
        }

        return item.getMaxStackSize();
    }

    @Override
    public short getMaxDurability() {
        return (short) item.getMaxDamage();
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
        return item.isEdible();
    }

    @Override
    public boolean isRecord() {
        return item instanceof ItemRecord;
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
        if (this == Material.AIR) {
            return true;
        }

        return block.defaultBlockState().getMaterial().isSolidBlocking();
    }

    @Override
    public boolean isFlammable() {
        return block.defaultBlockState().getMaterial().isFlammable();
    }

    @Override
    public boolean isBurnable() {
        return ((BlockFire) Blocks.FIRE).flameOdds.getOrDefault(block, 0) > 0;
    }

    @Override
    public boolean isFuel() {
        return TileEntityFurnace.isFuel(new net.minecraft.world.item.ItemStack(item));
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
        return true;
    }

    @Override
    public ItemType asItemType() {
        return this;
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

    public static class CraftMaterialRegistry implements Registry<Material> {
        private final Map<NamespacedKey, Material> cache = new HashMap<>();
        private final IRegistry<Block> blockRegistry;
        private final IRegistry<Item> itemRegistry;

        public CraftMaterialRegistry(IRegistry<Block> blockRegistry, IRegistry<Item> itemRegistry) {
            this.blockRegistry = blockRegistry;
            this.itemRegistry = itemRegistry;
        }

        @Override
        public Material get(NamespacedKey namespacedKey) {
            Material cached = cache.get(namespacedKey);
            if (cached != null) {
                return cached;
            }

            MinecraftKey minecraftKey = CraftNamespacedKey.toMinecraft(namespacedKey);
            Block block = blockRegistry.getOptional(minecraftKey).orElse(null);
            Item item = itemRegistry.getOptional(minecraftKey).orElse(null);

            if (block != null && item != null) {
                Material bukkit = new CraftMaterial<>(namespacedKey, block, item);
                cache.put(namespacedKey, bukkit);
                return bukkit;
            }

            if (block != null) {
                Material bukkit = new CraftBlockType<>(namespacedKey, block);
                cache.put(namespacedKey, bukkit);
                return bukkit;
            }

            if (item != null) {
                Material bukkit = new CraftItemType(namespacedKey, item);
                cache.put(namespacedKey, bukkit);
                return bukkit;
            }

            return null;
        }

        @Override
        public Iterator<Material> iterator() {
            Set<MinecraftKey> keySet = new LinkedHashSet<>(itemRegistry.keySet());
            keySet.addAll(blockRegistry.keySet());
            return keySet.stream().map(minecraftKey -> get(CraftNamespacedKey.fromMinecraft(minecraftKey))).iterator();
        }
    }
}

package org.bukkit.craftbukkit.block;

import com.google.common.base.Preconditions;
import java.util.function.Consumer;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.EnumHand;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockAccessAir;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockFalling;
import net.minecraft.world.level.block.BlockFire;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.inventory.CraftItemType;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;

public class CraftBlockType<B extends BlockData> implements BlockType<B> {

    private final NamespacedKey key;
    private final Block block;
    private final Class<B> blockDataClass;
    private final boolean interactable;

    public static BlockType<?> minecraftToBukkit(Block minecraft) {
        Preconditions.checkArgument(minecraft != null);

        IRegistry<Block> registry = CraftRegistry.getMinecraftRegistry().registryOrThrow(Registries.BLOCK);
        BlockType<?> bukkit = Registry.BLOCK.get(CraftNamespacedKey.fromMinecraft(registry.getKey(minecraft)));

        Preconditions.checkArgument(bukkit != null);

        return bukkit;
    }

    private static boolean isInteractable(Block block) {
        try {
            return !block.getClass()
                    .getMethod("use", IBlockData.class, net.minecraft.world.level.World.class, BlockPosition.class, EntityHuman.class, EnumHand.class, MovingObjectPositionBlock.class)
                    .getDeclaringClass().equals(BlockBase.class);
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    public CraftBlockType(NamespacedKey key, Block block) {
        this.key = key;
        this.block = block;
        this.blockDataClass = (Class<B>) CraftBlockData.fromData(block.defaultBlockState()).getClass().getInterfaces()[0];
        this.interactable = isInteractable(block);
    }

    public Block getHandle() {
        return block;
    }

    @Override
    public boolean hasItemType() {
        if (this == AIR) {
            return true;
        }

        return block.asItem() != Items.AIR;
    }

    @NotNull
    @Override
    public ItemType getItemType() {
        if (this == AIR) {
            return ItemType.AIR;
        }

        Item item = block.asItem();
        Preconditions.checkArgument(item != Items.AIR, "The block type %s has no corresponding item type", getKey());
        return CraftItemType.minecraftToBukkit(item);
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
    public B createBlockData(Consumer<B> consumer) {
        return (B) Bukkit.createBlockData(this, consumer);
    }

    @Override
    public B createBlockData(String data) {
        return (B) Bukkit.createBlockData(this, data);
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
    public boolean isFlammable() {
        return block.defaultBlockState().getMaterial().isFlammable();
    }

    @Override
    public boolean isBurnable() {
        return ((BlockFire) Blocks.FIRE).igniteOdds.getOrDefault(block, 0) > 0;
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

    @NotNull
    @Override
    public String getTranslationKey() {
        return block.getDescriptionId();
    }

    @Override
    public NamespacedKey getKey() {
        return key;
    }
}

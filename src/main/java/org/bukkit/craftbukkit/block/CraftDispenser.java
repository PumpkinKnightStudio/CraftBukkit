package org.bukkit.craftbukkit.block;

import java.util.function.Function;
import com.google.common.base.Preconditions;
import net.minecraft.server.BlockDispenser;
import net.minecraft.server.Blocks;
import net.minecraft.server.DispenseBehaviorItem;
import net.minecraft.server.IDispenseBehavior;
import net.minecraft.server.ItemStack;
import net.minecraft.server.SourceBlock;
import net.minecraft.server.TileEntityDispenser;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.projectiles.CraftBlockProjectileSource;
import org.bukkit.inventory.Inventory;
import org.bukkit.projectiles.BlockProjectileSource;

public class CraftDispenser extends CraftLootable<TileEntityDispenser> implements Dispenser {

    private static final Function<ItemStack, IDispenseBehavior> DISPENSE_BEHAVIOR = (item) -> BlockDispenser.REGISTRY.get(item.getItem());
    private static final Function<ItemStack, IDispenseBehavior> DROP_BEHAVIOR = (item) -> new DispenseBehaviorItem();

    public CraftDispenser(final Block block) {
        super(block, TileEntityDispenser.class);
    }

    public CraftDispenser(final Material material, final TileEntityDispenser te) {
        super(material, te);
    }

    @Override
    public Inventory getSnapshotInventory() {
        return new CraftInventory(this.getSnapshot());
    }

    @Override
    public Inventory getInventory() {
        if (!this.isPlaced()) {
            return this.getSnapshotInventory();
        }

        return new CraftInventory(this.getTileEntity());
    }

    @Override
    public BlockProjectileSource getBlockProjectileSource() {
        Block block = getBlock();

        if (block.getType() != Material.DISPENSER) {
            return null;
        }

        return new CraftBlockProjectileSource((TileEntityDispenser) this.getTileEntityFromWorld());
    }

    @Override
    public boolean dispense() {
        Block block = getBlock();

        if (block.getType() == Material.DISPENSER) {
            CraftWorld world = (CraftWorld) this.getWorld();
            BlockDispenser dispense = (BlockDispenser) Blocks.DISPENSER;

            dispense.dispense(world.getHandle(), this.getPosition());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean dispenseItem(org.bukkit.inventory.ItemStack item) {
        return dispenseItem(item, DISPENSE_BEHAVIOR);
    }

    @Override
    public boolean dropItem(org.bukkit.inventory.ItemStack item) {
        return dispenseItem(item, DROP_BEHAVIOR);
    }

    private boolean dispenseItem(org.bukkit.inventory.ItemStack item, Function<ItemStack, IDispenseBehavior> behaviorFunction) {
        Preconditions.checkArgument(item != null, "item");
        Block block = getBlock();
        if (block.getType() != Material.DISPENSER) {
            return false;
        }

        ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        if (!nmsItem.isEmpty()) {
            CraftWorld world = (CraftWorld) this.getWorld();
            IDispenseBehavior dispenseBehavior = behaviorFunction.apply(nmsItem);
            dispenseBehavior.dispense(new SourceBlock(world.getHandle(), this.getPosition()), nmsItem);
        }
        return true;
    }
}

package org.bukkit.craftbukkit.inventory;

import net.minecraft.world.IInventory;
import net.minecraft.world.ITileInventory;
import net.minecraft.world.level.block.entity.TileEntityLectern;
import org.bukkit.block.Lectern;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.LecternInventory;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public class CraftInventoryLectern extends CraftInventory implements LecternInventory {

    public ITileInventory tile;

    public CraftInventoryLectern(IInventory inventory) {
        super(inventory);
        if (inventory instanceof TileEntityLectern.LecternInventory) {
            this.tile = ((TileEntityLectern.LecternInventory) inventory).getLectern();
        }
    }

    @Nullable
    @Override
    public ItemStack getBook() {
        return getItem(0);
    }

    @Override
    public void setBook(@Nullable final ItemStack itemStack) {
        setItem(0, itemStack);
    }

    @Nullable
    @Override
    public MenuType<?> getMenuType() {
        return MenuType.LECTERN;
    }

    @Override
    public Lectern getHolder() {
        return (Lectern) inventory.getOwner();
    }
}

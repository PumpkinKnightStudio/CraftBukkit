package org.bukkit.craftbukkit.inventory;

import net.minecraft.world.IInventory;
import net.minecraft.world.ITileInventory;
import net.minecraft.world.level.block.entity.TileEntityLectern;
import org.bukkit.block.Lectern;
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
    public MenuType<?> getMenuType() {
        return null;
    }

    @Override
    public Lectern getHolder() {
        return (Lectern) inventory.getOwner();
    }
}

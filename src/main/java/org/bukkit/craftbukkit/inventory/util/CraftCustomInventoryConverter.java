package org.bukkit.craftbukkit.inventory.util;

import org.bukkit.craftbukkit.inventory.CraftInventoryCustom;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class CraftCustomInventoryConverter implements CraftInventoryCreator.InventoryConverter {

    @Override
    @Deprecated
    public Inventory createInventory(InventoryHolder holder, InventoryType type) {
        return new CraftInventoryCustom(holder, type);
    }

    @Override
    public Inventory createInventory(InventoryType type) {
        return new CraftInventoryCustom(null, type);
    }

    @Override
    @Deprecated
    public Inventory createInventory(InventoryHolder owner, InventoryType type, String title) {
        return new CraftInventoryCustom(owner, type, title);
    }

    @Override
    public Inventory createInventory(InventoryType type, String title) {
        return new CraftInventoryCustom(null, type, title);
    }

    @Deprecated
    public Inventory createInventory(InventoryHolder owner, int size) {
        return new CraftInventoryCustom(owner, size);
    }

    @Deprecated
    public Inventory createInventory(InventoryHolder owner, int size, String title) {
        return new CraftInventoryCustom(owner, size, title);
    }

    public Inventory createInventory(int size) {
        return new CraftInventoryCustom(null, size);
    }

    public Inventory createInventory(int size, String title) {
        return new CraftInventoryCustom(null, size, title);
    }
}

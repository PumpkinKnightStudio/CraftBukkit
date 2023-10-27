package org.bukkit.craftbukkit.inventory;

import net.minecraft.world.IInventory;
import org.bukkit.inventory.CartographyInventory;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public class CraftInventoryCartography extends CraftResultInventory implements CartographyInventory {

    public CraftInventoryCartography(IInventory inventory, IInventory resultInventory) {
        super(inventory, resultInventory);
    }

    @Nullable
    @Override
    public MenuType<?> getMenuType() {
        return MenuType.CARTOGRAPHY_TABLE;
    }
}

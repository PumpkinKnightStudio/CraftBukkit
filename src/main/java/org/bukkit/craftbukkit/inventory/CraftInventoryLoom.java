package org.bukkit.craftbukkit.inventory;

import net.minecraft.world.IInventory;
import org.bukkit.inventory.LoomInventory;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public class CraftInventoryLoom extends CraftResultInventory implements LoomInventory {

    public CraftInventoryLoom(IInventory inventory, IInventory resultInventory) {
        super(inventory, resultInventory);
    }

    @Nullable
    @Override
    public MenuType<?> getMenuType() {
        return MenuType.LOOM;
    }
}

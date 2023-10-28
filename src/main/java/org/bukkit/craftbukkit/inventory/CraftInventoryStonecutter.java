package org.bukkit.craftbukkit.inventory;

import net.minecraft.world.IInventory;
import org.bukkit.inventory.MenuType;
import org.bukkit.inventory.StonecutterInventory;
import org.jetbrains.annotations.Nullable;

public class CraftInventoryStonecutter extends CraftResultInventory implements StonecutterInventory {

    public CraftInventoryStonecutter(IInventory inventory, IInventory resultInventory) {
        super(inventory, resultInventory);
    }

    @Nullable
    @Override
    public MenuType<?> getMenuType() {
        return MenuType.STONECUTTER;
    }
}

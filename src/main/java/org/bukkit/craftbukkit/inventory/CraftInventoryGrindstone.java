package org.bukkit.craftbukkit.inventory;

import net.minecraft.world.IInventory;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public class CraftInventoryGrindstone extends CraftResultInventory implements GrindstoneInventory {

    public CraftInventoryGrindstone(IInventory inventory, IInventory resultInventory) {
        super(inventory, resultInventory);
    }

    @Nullable
    @Override
    public MenuType<?> getMenuType() {
        return MenuType.GRINDSTONE;
    }
}

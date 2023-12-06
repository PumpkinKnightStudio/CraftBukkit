package org.bukkit.craftbukkit.inventory;

import net.minecraft.world.IInventory;
import org.bukkit.inventory.CrafterInventory;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public class CraftInventoryCrafter extends CraftResultInventory implements CrafterInventory {

    public CraftInventoryCrafter(IInventory inventory, IInventory resultInventory) {
        super(inventory, resultInventory);
    }

    @Nullable
    @Override
    public MenuType<?> getMenuType() {
        return MenuType.CRAFTER_3X3;
    }
}

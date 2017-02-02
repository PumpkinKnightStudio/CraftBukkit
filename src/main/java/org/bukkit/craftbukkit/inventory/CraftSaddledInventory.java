package org.bukkit.craftbukkit.inventory;

import net.minecraft.server.IInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SaddledHorseInventory;

public class CraftSaddledInventory extends CraftInventory implements SaddledHorseInventory {

    public CraftSaddledInventory(IInventory inventory) {
        super(inventory);
    }

    public ItemStack getSaddle() {
        return getItem(0);
    }

    public void setSaddle(ItemStack stack) {
        setItem(0, stack);
    }
}

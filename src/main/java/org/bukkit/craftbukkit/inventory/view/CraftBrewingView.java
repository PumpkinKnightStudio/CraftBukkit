package org.bukkit.craftbukkit.inventory.view;

import net.minecraft.world.inventory.ContainerBrewingStand;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.view.BrewingView;

public class CraftBrewingView extends CraftInventoryView<ContainerBrewingStand> implements BrewingView {

    public CraftBrewingView(HumanEntity player, Inventory viewing, ContainerBrewingStand container) {
        super(player, viewing, container);
    }

    @Override
    public int getFuelLevel() {
        return container.getFuel();
    }

    @Override
    public int getBrewingTicks() {
        return container.getBrewingTicks();
    }
}

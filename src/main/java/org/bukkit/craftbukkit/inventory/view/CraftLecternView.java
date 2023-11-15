package org.bukkit.craftbukkit.inventory.view;

import net.minecraft.world.inventory.ContainerLectern;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.view.LecternView;

public class CraftLecternView extends CraftInventoryView<ContainerLectern> implements LecternView {

    public CraftLecternView(final HumanEntity player, final Inventory viewing, final ContainerLectern container) {
        super(player, viewing, container);
    }

    @Override
    public int getPage() {
        return container.getPage();
    }
}
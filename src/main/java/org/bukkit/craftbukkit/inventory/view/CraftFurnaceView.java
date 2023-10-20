package org.bukkit.craftbukkit.inventory.view;

import net.minecraft.world.inventory.ContainerFurnace;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.view.FurnaceView;

public class CraftFurnaceView extends CraftInventoryView<ContainerFurnace> implements FurnaceView {


    public CraftFurnaceView(HumanEntity player, Inventory viewing, ContainerFurnace container) {
        super(player, viewing, container);
    }

    @Override
    public boolean isBurning() {
        return container.isLit();
    }
}

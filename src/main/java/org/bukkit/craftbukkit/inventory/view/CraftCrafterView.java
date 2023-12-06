package org.bukkit.craftbukkit.inventory.view;

import net.minecraft.world.inventory.CrafterMenu;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.view.CrafterView;

public class CraftCrafterView extends CraftInventoryView<CrafterMenu> implements CrafterView {

    public CraftCrafterView(final HumanEntity player, final Inventory viewing, final CrafterMenu container) {
        super(player, viewing, container);
    }

    @Override
    public boolean isSlotDisabled(final int i) {
        return container.isSlotDisabled(i);
    }

    @Override
    public void setSlotStatus(final int i, final boolean enabled) {
        container.setSlotState(i, enabled);
    }
}

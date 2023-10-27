package org.bukkit.craftbukkit.inventory.view;

import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.inventory.ContainerAnvil;
import net.minecraft.world.inventory.Slot;
import org.bukkit.craftbukkit.inventory.CraftInventoryAnvil;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.view.AnvilView;

public class CraftAnvilView extends CraftInventoryView<ContainerAnvil> implements AnvilView {

    public CraftAnvilView(HumanEntity player, Inventory viewing, ContainerAnvil container) {
        super(player, viewing, container);
    }

    @Override
    public String getRenameText() {
        return container.itemName;
    }

    @Override
    public int getRepairItemCost() {
        return container.repairItemCountCost;
    }

    @Override
    public int getRepairCost() {
        return container.cost.get();
    }

    @Override
    public int getMaximumRepairCost() {
        return container.maximumRepairCost;
    }

    @Override
    public void setRenameText(String s) {
        container.itemName = s;
        // left slot dictates renameText
        final Slot slot = container.getSlot(0);
        if (slot.hasItem()) {
            slot.getItem().setHoverName(IChatBaseComponent.literal(s));
        }
    }

    @Override
    public void setRepairItemCost(int i) {
        container.repairItemCountCost = i;
    }

    @Override
    public void setRepairCost(int i) {
        container.cost.set(i);
    }

    @Override
    public void setMaximumRepairCost(int i) {
        container.maximumRepairCost = i;
    }

    public void updateFromLegacy(CraftInventoryAnvil legacy) {
        if (legacy.isRepairCostSet()) {
            setRepairCost(legacy.getRepairCost());
        }

        if (legacy.isRepairCostAmountSet()) {
            setRepairItemCost(legacy.getRepairCostAmount());
        }

        if (legacy.isMaximumRepairCostSet()) {
            setMaximumRepairCost(legacy.getMaximumRepairCost());
        }
    }
}

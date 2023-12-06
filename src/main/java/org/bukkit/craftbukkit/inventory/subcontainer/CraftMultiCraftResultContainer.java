package org.bukkit.craftbukkit.inventory.subcontainer;

import net.minecraft.world.IInventory;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.inventory.InventoryCraftResult;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class CraftMultiCraftResultContainer extends InventoryCraftResult {

    private Map<UUID, Consumer<IInventory>> changeMap;

    public void addInstance(EntityHuman player, Consumer<IInventory> slotChange) {
        changeMap.put(player.getUUID(), slotChange);
    }

    @Override
    public void onClose(final CraftHumanEntity who) {
        super.onClose(who);
        changeMap.remove(who.getUniqueId());
    }

    @Override
    public void setChanged() {
        super.setChanged();
        changeMap.forEach((k, consumer) -> consumer.accept(this));
    }

}

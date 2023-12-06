package org.bukkit.craftbukkit.inventory.subcontainer;

import net.minecraft.world.IInventory;
import net.minecraft.world.InventorySubcontainer;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.inventory.ContainerAccess;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class CraftMultiInventorySubContainer extends InventorySubcontainer {

    private Map<UUID, Consumer<IInventory>> changeMap;
    private ContainerAccess access;
    private boolean isSet;

    public CraftMultiInventorySubContainer(int i, InventoryHolder holder) {
        super(i, holder);
        changeMap = new HashMap<>();
    }

    public void addInstance(final EntityHuman player, final Consumer<IInventory> slotsChanged) {
        changeMap.put(player.getUUID(), slotsChanged);
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

    @Override
    public Location getLocation() {
        return null;
    }

}

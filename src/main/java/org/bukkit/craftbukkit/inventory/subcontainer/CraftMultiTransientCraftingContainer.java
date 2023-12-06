package org.bukkit.craftbukkit.inventory.subcontainer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.world.ContainerUtil;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;

public class CraftMultiTransientCraftingContainer extends TransientCraftingContainer {

    private Map<UUID, Container> openInstances;

    public CraftMultiTransientCraftingContainer(final int i, final int j) {
        super(null, i, j, (EntityHuman) null);
        openInstances = new HashMap<>();
    }

    public void openInstance(EntityHuman human, Container container) {
        openInstances.put(human.getUUID(), container);
    }

    @Override
    public void onClose(final CraftHumanEntity who) {
        super.onClose(who);
        openInstances.remove(who.getUniqueId());
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public void setItem(final int i, final ItemStack itemstack) {
        this.items.set(i, itemstack);
        this.openInstances.forEach((k, c) -> c.slotsChanged(this));
    }

    @Override
    public ItemStack removeItem(final int i, final int j) {
        ItemStack itemstack = ContainerUtil.removeItem(this.items, i, j);

        if (!itemstack.isEmpty()) {
            this.openInstances.forEach((k, c) -> c.slotsChanged(this));
        }

        return itemstack;
    }
}

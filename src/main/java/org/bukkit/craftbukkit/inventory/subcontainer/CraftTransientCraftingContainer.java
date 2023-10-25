package org.bukkit.craftbukkit.inventory.subcontainer;

import com.google.common.base.Preconditions;
import net.minecraft.world.ContainerUtil;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Location;

public class CraftTransientCraftingContainer extends TransientCraftingContainer {

    private boolean isSet;

    public CraftTransientCraftingContainer(final int i, final int j) {
        super(null, i, j, (EntityHuman) null);
    }

    public void setContainer(Container container, EntityHuman human) {
        Preconditions.checkArgument(!isSet, "The container is already set for this inventory and it can not be set again");
        super.menu = container;
        super.owner = human;
        isSet = true;
    }

    @Override
    public Location getLocation() {
        return isSet ? super.getLocation() : null;
    }

    @Override
    public ItemStack removeItem(final int i, final int j) {
        if (isSet) {
            super.removeItem(i, j);
        }
        ItemStack itemstack = ContainerUtil.removeItem(super.items, i, j);
        return itemstack.isEmpty() ? ItemStack.EMPTY : itemstack;
    }

    @Override
    public void setItem(final int i, final ItemStack itemstack) {
        if (isSet) {
            super.setItem(i, itemstack);
        }
        this.items.set(i, itemstack);
    }
}

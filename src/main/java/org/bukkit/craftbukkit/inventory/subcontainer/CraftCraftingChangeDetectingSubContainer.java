package org.bukkit.craftbukkit.inventory.subcontainer;

import com.google.common.base.Preconditions;
import java.util.function.Consumer;
import net.minecraft.world.IInventory;
import net.minecraft.world.inventory.ContainerAccess;
import net.minecraft.world.inventory.InventoryCraftResult;
import org.bukkit.Location;

public class CraftCraftingChangeDetectingSubContainer extends InventoryCraftResult {

    private Consumer<IInventory> slotsChanged = (iinventory) -> {
    };
    private ContainerAccess access;
    private boolean isSet;

    @Override
    public void setChanged() {
        super.setChanged();
        slotsChanged.accept(this);
    }

    @Override
    public Location getLocation() {
        return this.access == null ? null : this.access.getLocation();
    }

    public void setContainer(Consumer<IInventory> slotsChanged, ContainerAccess access) {
        Preconditions.checkArgument(!isSet, "You must not set the container again if it is already set");
        this.slotsChanged = slotsChanged;
        this.access = access;
    }

}

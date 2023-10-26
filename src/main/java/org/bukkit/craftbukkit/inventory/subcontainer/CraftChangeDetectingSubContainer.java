package org.bukkit.craftbukkit.inventory.subcontainer;

import com.google.common.base.Preconditions;
import java.util.function.Consumer;
import net.minecraft.world.IInventory;
import net.minecraft.world.InventorySubcontainer;
import net.minecraft.world.inventory.ContainerAccess;
import org.bukkit.Location;
import org.bukkit.inventory.InventoryHolder;


public class CraftChangeDetectingSubContainer extends InventorySubcontainer {

    private Consumer<IInventory> slotsChanged = (iinventory) -> {
    };
    private ContainerAccess access;
    private boolean isSet;

    public CraftChangeDetectingSubContainer(int i, InventoryHolder holder) {
        super(i, holder);
    }

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

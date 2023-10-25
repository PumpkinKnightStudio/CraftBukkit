package org.bukkit.craftbukkit.inventory.subcontainer;

import com.google.common.base.Preconditions;
import java.util.function.Consumer;
import net.minecraft.world.IInventory;
import net.minecraft.world.InventorySubcontainer;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class CraftInventoryAnvilSubContainer extends InventorySubcontainer {

    private Consumer<IInventory> slotsChanged = (iinventory) -> {
    };
    private boolean isSet;

    public CraftInventoryAnvilSubContainer(int i, InventoryHolder holder) {
        super(i, holder);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        slotsChanged.accept(this);
    }

    public void setSlotsChanged(@NotNull final Consumer<IInventory> slotsChanged) {
        Preconditions.checkArgument(!isSet, "You must not set the slotChanged function if it is already set");
        this.slotsChanged = slotsChanged;
        isSet = true;
    }
}

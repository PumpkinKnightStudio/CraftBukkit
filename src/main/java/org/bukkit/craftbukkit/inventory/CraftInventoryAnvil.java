package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Preconditions;
import java.util.function.Consumer;
import net.minecraft.world.IInventory;
import org.bukkit.Location;
import org.bukkit.craftbukkit.inventory.view.CraftAnvilView;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public class CraftInventoryAnvil extends CraftResultInventory implements AnvilInventory {

    private static final int DEFAULT_REPAIR_COST = 0;
    private static final int DEFAULT_REPAIR_COST_AMOUNT = 0;
    private static final int DEFAULT_MAXIMUM_REPAIR_COST = 40;

    private final Location location;
    private String renameText;
    private int costAmount;
    private int repairCost;
    private int maximumRepairCost;

    public CraftInventoryAnvil(Location location, IInventory inventory, IInventory resultInventory) {
        super(inventory, resultInventory);
        this.location = location;
        this.renameText = null;
        this.costAmount = DEFAULT_REPAIR_COST_AMOUNT;
        this.repairCost = DEFAULT_REPAIR_COST;
        this.maximumRepairCost = DEFAULT_MAXIMUM_REPAIR_COST;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    @Deprecated
    public String getRenameText() {
        onViewers((cav) -> this.renameText = cav.getRenameText());
        return this.renameText;
    }

    @Override
    @Deprecated
    public int getRepairCostAmount() {
        onViewers((cav) -> this.costAmount = cav.getRepairItemCost());
        return this.costAmount;
    }

    @Override
    @Deprecated
    public void setRepairCostAmount(int amount) {
        this.repairCost = amount;
        onViewers((cav) -> cav.setRepairItemCost(amount));
    }

    @Override
    @Deprecated
    public int getRepairCost() {
        onViewers((cav) -> this.repairCost = cav.getRepairCost());
        return this.repairCost;
    }

    @Override
    @Deprecated
    public void setRepairCost(int i) {
        this.repairCost = i;
        onViewers((cav) -> cav.setRepairCost(i));
    }

    @Override
    @Deprecated
    public int getMaximumRepairCost() {
        onViewers((cav) -> this.maximumRepairCost = cav.getMaximumRepairCost());
        return this.maximumRepairCost;
    }

    @Nullable
    @Override
    public MenuType<?> getMenuType() {
        return MenuType.ANVIL;
    }

    @Override
    @Deprecated
    public void setMaximumRepairCost(int levels) {
        Preconditions.checkArgument(levels >= 0, "Maximum repair cost must be positive (or 0)");
        this.maximumRepairCost = levels;
        onViewers((cav) -> cav.setMaximumRepairCost(levels));
    }

    public boolean isRepairCostSet() {
        return this.repairCost != DEFAULT_REPAIR_COST;
    }

    public boolean isRepairCostAmountSet() {
        return this.costAmount != DEFAULT_REPAIR_COST_AMOUNT;
    }

    public boolean isMaximumRepairCostSet() {
        return this.maximumRepairCost != DEFAULT_MAXIMUM_REPAIR_COST;
    }

    // used to lazily update and apply values from the view to the inventory
    private void onViewers(Consumer<CraftAnvilView> consumer) {
        for (HumanEntity viewer : getViewers()) {
            if (viewer.getOpenInventory() instanceof CraftAnvilView cav) {
                consumer.accept(cav);
            }
        }
    }
}

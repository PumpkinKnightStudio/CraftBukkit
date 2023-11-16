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
        syncWithArbitraryViewValue((cav) -> this.renameText = cav.getRenameText());
        return this.renameText;
    }

    @Override
    @Deprecated
    public int getRepairCostAmount() {
        syncWithArbitraryViewValue((cav) -> this.costAmount = cav.getRepairItemCost());
        return this.costAmount;
    }

    @Override
    @Deprecated
    public void setRepairCostAmount(int amount) {
        this.repairCost = amount;
        syncViews((cav) -> cav.setRepairItemCost(amount));
    }

    @Override
    @Deprecated
    public int getRepairCost() {
        syncWithArbitraryViewValue((cav) -> this.repairCost = cav.getRepairCost());
        return this.repairCost;
    }

    @Override
    @Deprecated
    public void setRepairCost(int i) {
        this.repairCost = i;
        syncViews((cav) -> cav.setRepairCost(i));
    }

    @Override
    @Deprecated
    public int getMaximumRepairCost() {
        syncWithArbitraryViewValue((cav) -> this.maximumRepairCost = cav.getMaximumRepairCost());
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
        syncViews((cav) -> cav.setMaximumRepairCost(levels));
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
    private void syncViews(Consumer<CraftAnvilView> consumer) {
        for (HumanEntity viewer : getViewers()) {
            if (viewer.getOpenInventory() instanceof CraftAnvilView cav) {
                consumer.accept(cav);
            }
        }
    }

    /*
     * This method provides the best effort guess on whatever the value could be
     * It is possible these values are wrong given there are more than 1 views of this inventory,
     * however it is a limitation seeing as these anvil values are supposed to be in the Container
     * not the inventory.
     */
    private void syncWithArbitraryViewValue(Consumer<CraftAnvilView> consumer) {
        final HumanEntity entity = getViewers().get(0);
        if (entity != null && entity.getOpenInventory() instanceof CraftAnvilView cav) {
            consumer.accept(cav);
        }
    }
}

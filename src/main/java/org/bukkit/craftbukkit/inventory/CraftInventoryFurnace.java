package org.bukkit.craftbukkit.inventory;

import net.minecraft.world.level.block.entity.TileEntityBlastFurnace;
import net.minecraft.world.level.block.entity.TileEntityFurnace;
import net.minecraft.world.level.block.entity.TileEntityFurnaceFurnace;
import net.minecraft.world.level.block.entity.TileEntitySmoker;
import org.bukkit.block.Furnace;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public class CraftInventoryFurnace extends CraftInventory implements FurnaceInventory {
    public CraftInventoryFurnace(TileEntityFurnace inventory) {
        super(inventory);
    }

    @Override
    public ItemStack getResult() {
        return getItem(2);
    }

    @Override
    public ItemStack getFuel() {
        return getItem(1);
    }

    @Override
    public ItemStack getSmelting() {
        return getItem(0);
    }

    @Override
    public void setFuel(ItemStack stack) {
        setItem(1, stack);
    }

    @Override
    public void setResult(ItemStack stack) {
        setItem(2, stack);
    }

    @Override
    public void setSmelting(ItemStack stack) {
        setItem(0, stack);
    }

    @Override
    public Furnace getHolder() {
        return (Furnace) inventory.getOwner();
    }

    @Nullable
    @Override
    public MenuType<?> getMenuType() {
        if (inventory instanceof TileEntityFurnaceFurnace) {
            return MenuType.FURNACE;
        } else if (inventory instanceof TileEntitySmoker) {
            return MenuType.SMOKER;
        } else if (inventory instanceof TileEntityBlastFurnace) {
            return MenuType.BLAST_FURNACE;
        } else {
            throw new IllegalStateException("Unable to fetch the menu type associated with this inventory");
        }
    }
}

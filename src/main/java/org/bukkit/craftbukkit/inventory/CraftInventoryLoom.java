package org.bukkit.craftbukkit.inventory;

import net.minecraft.world.IInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.LoomInventory;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public class CraftInventoryLoom extends CraftResultInventory implements LoomInventory {

    public CraftInventoryLoom(IInventory inventory, IInventory resultInventory) {
        super(inventory, resultInventory);
    }

    @Nullable
    @Override
    public ItemStack getBanner() {
        return getItem(0);
    }

    @Override
    public void setBanner(@Nullable final ItemStack itemStack) {
        setItem(0, itemStack);
    }

    @Nullable
    @Override
    public ItemStack getDye() {
        return getItem(1);
    }

    @Override
    public void setDye(@Nullable final ItemStack itemStack) {
        setItem(1, itemStack);
    }

    @Nullable
    @Override
    public ItemStack getBannerPattern() {
        return getItem(2);
    }

    @Override
    public void setBannerPattern(@Nullable final ItemStack itemStack) {
        setItem(2, itemStack);
    }

    @Nullable
    @Override
    public ItemStack getResult() {
        return getItem(3);
    }

    @Override
    public void setResult(@Nullable final ItemStack itemStack) {
        setItem(3, itemStack);
    }

    @Nullable
    @Override
    public MenuType<?> getMenuType() {
        return MenuType.LOOM;
    }
}

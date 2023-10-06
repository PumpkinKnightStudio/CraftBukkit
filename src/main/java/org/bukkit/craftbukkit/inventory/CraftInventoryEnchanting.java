package org.bukkit.craftbukkit.inventory;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.IInventory;
import net.minecraft.world.inventory.ContainerEnchantTable;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CraftInventoryEnchanting extends CraftInventory implements EnchantingInventory {
    private final ContainerEnchantTable container;
    public CraftInventoryEnchanting(IInventory inventory, ContainerEnchantTable container) {
        super(inventory);
        this.container = container;
    }

    @Override
    public void setItem(ItemStack item) {
        setItem(0, item);
    }

    @Override
    public ItemStack getItem() {
        return getItem(0);
    }

    @Override
    public void setSecondary(ItemStack item) {
        setItem(1, item);
    }

    @Override
    public ItemStack getSecondary() {
        return getItem(1);
    }

    @Override
    public int[] getEnchantmentCosts() {
        return container.costs;
    }

    @Override
    public List<Enchantment> getEnchantmentClues() {
        List<Enchantment> enchantmentClues = new ArrayList<>();

        for (int i=0; i<3; i++) {
            if (container.levelClue[i]!=-1)
                enchantmentClues.add(Enchantment.
                        getByKey(CraftNamespacedKey
                                .fromMinecraft(BuiltInRegistries.ENCHANTMENT
                                        .getKey(net.minecraft.world.item.enchantment.Enchantment.byId(container.levelClue[i])))));
            else enchantmentClues.add(null);
        }

        return enchantmentClues;
    }

    @Override
    public int[] getLevelClues() {
        return container.levelClue;
    }
}

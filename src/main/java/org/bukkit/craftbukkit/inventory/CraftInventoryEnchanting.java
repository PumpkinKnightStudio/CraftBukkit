package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Preconditions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.IInventory;
import net.minecraft.world.inventory.ContainerEnchantTable;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;

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
    public void setEnchantmentCosts(int[] costs) {
        Preconditions.checkArgument(costs.length == 3, "There must be 3 slots for the enchantment costs. Leave slot as null if needed.");
        System.arraycopy(costs, 0, container.costs, 0, 3);
    }

    @Override
    public int[] getEnchantmentCosts() {
        return container.costs;
    }

    @Override
    public void setEnchantmentClues(Enchantment[] enchantments) {
        Preconditions.checkArgument(enchantments.length == 3, "There must be 3 slots for the enchantments. Leave slot as null if needed.");
        for (int i=0; i<3; i++) {
            if (enchantments[i] != null)
                container.enchantClue[i] = BuiltInRegistries.ENCHANTMENT.getId(BuiltInRegistries.ENCHANTMENT.get(CraftNamespacedKey.toMinecraft(enchantments[i].getKey())));
        }
    }

    @Override
    public Enchantment[] getEnchantmentClues() {
        Enchantment[] enchantmentClues = new Enchantment[3];

        for (int i=0; i<3; i++) {
            enchantmentClues[i] = container.levelClue[i]==-1 ? null
            : Enchantment.getByKey(CraftNamespacedKey.fromMinecraft(BuiltInRegistries.ENCHANTMENT.getKey(net.minecraft.world.item.enchantment.Enchantment.byId(container.levelClue[i]))));
        }

        return enchantmentClues;
    }

    @Override
    public void setLevelClues(int[] levels) {
        Preconditions.checkArgument(levels.length == 3, "There must be 3 slots for the enchantment levels. Leave slot as -1 if want to omit.");
        for (int i=0; i<3; i++) {
            if (levels[i] != -1) container.levelClue[i] = levels[i];
        }
    }

    @Override
    public int[] getLevelClues() {
        return container.levelClue;
    }
}

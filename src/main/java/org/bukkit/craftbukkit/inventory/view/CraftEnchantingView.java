package org.bukkit.craftbukkit.inventory.view;

import com.google.common.base.Preconditions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.ContainerEnchantTable;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.view.EnchantingView;

public class CraftEnchantingView extends CraftInventoryView<ContainerEnchantTable> implements EnchantingView {

    public CraftEnchantingView(HumanEntity player, Inventory viewing, ContainerEnchantTable container) {
        super(player, viewing, container);
    }

    @Override
    public int getEnchantmentSeed() {
        return container.getEnchantmentSeed();
    }

    @Override
    public Enchantment[] getEnchantmentHints() {
        Enchantment[] hints = new Enchantment[3];
        for (int i = 0; i < hints.length; i++) {
            hints[i] = container.levelClue[i] == 1 ? null : Registry.ENCHANTMENT.get(CraftNamespacedKey.fromMinecraft(BuiltInRegistries.ENCHANTMENT.getKey(net.minecraft.world.item.enchantment.Enchantment.byId(container.levelClue[i]))));
        }
        return hints;
    }

    @Override
    public int[] getLevelClues() {
        final int[] levelClues = new int[container.levelClue.length];
        System.arraycopy(container.levelClue, 0, levelClues, 0, container.levelClue.length);
        return levelClues;
    }

    @Override
    public void setEnchantmentHints(Enchantment[] enchantments) {
        Preconditions.checkArgument(enchantments.length == 3, "There must be 3 slots for the enchantments.");
        for (int i = 0; i < enchantments.length; i++) {
            if (enchantments[i] == null) {
                continue;
            }
            container.enchantClue[i] = BuiltInRegistries.ENCHANTMENT.getId(((CraftEnchantment) Registry.ENCHANTMENT.get(enchantments[i].getKey())).getHandle());
        }
    }

    @Override
    public void setLevelClues(int[] ints) {
        Preconditions.checkArgument(ints.length == 3, "There must be 3 enchantment levels provided. Use -1 to omit.");
        for (int i = 0; i < ints.length; i++) {
            if (ints[i] == -1) {
                continue;
            }
            container.levelClue[i] = ints[i];
        }
    }
}

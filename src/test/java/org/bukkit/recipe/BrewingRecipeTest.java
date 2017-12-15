package org.bukkit.recipe;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.recipe.BrewingRecipe;
import org.bukkit.support.AbstractTestingBase;
import org.junit.Test;

import static org.junit.Assert.*;

public class BrewingRecipeTest extends AbstractTestingBase {
    @Test
    public void testConstructorValid() {
        ItemStack result = new ItemStack(Material.ENDER_CHEST);
        ItemStack input = new ItemStack(Material.POTION);
        ItemStack reagent = new ItemStack(Material.SULPHUR);

        BrewingRecipe recipe = new BrewingRecipe(input, reagent, result);
        recipe.key(NamespacedKey.minecraft("test_key"));

        assertTrue(recipe.getInput().equals(input));
        assertTrue(recipe.getReagent().equals(reagent));
        assertTrue(recipe.getResult().equals(result));
        assertTrue(recipe.getKey().equals(NamespacedKey.minecraft("test_key")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorInvalid() {
        ItemStack result = new ItemStack(Material.ENDER_CHEST);
        ItemStack input = new ItemStack(Material.POTION);
        ItemStack reagent = new ItemStack(Material.SULPHUR);
        NamespacedKey key = NamespacedKey.minecraft("test_key");

        new BrewingRecipe((ItemStack)null, reagent, result);
        new BrewingRecipe(input,(ItemStack)null, result);
        new BrewingRecipe(input, reagent, (ItemStack)null);
        new BrewingRecipe(new ItemStack(Material.COBBLESTONE), reagent, result);
        new BrewingRecipe(input, new ItemStack(Material.AIR), result);

        new BrewingRecipe(input, reagent, result).key(null);
        new BrewingRecipe(input, reagent, result).key(NamespacedKey.minecraft(""));
    }
}

package org.bukkit.recipe;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.support.AbstractTestingBase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ShapelessRecipeTest extends AbstractTestingBase {

    @Test
    public void testConstructorValid() {
        new ShapelessRecipe(new ItemStack(Material.STONE));

        new ShapelessRecipe(NamespacedKey.minecraft("test_key"), new ItemStack(Material.STONE));

        ShapelessRecipe recipe = new ShapelessRecipe(NamespacedKey.minecraft("test_key"), new ItemStack(Material.STONE)).group("test-group");

        assertEquals(recipe.getResult(), new ItemStack(Material.STONE));
        assertEquals(recipe.getKey(), NamespacedKey.minecraft("test_key"));
        assertTrue(recipe.getGroup().equals("test-group"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorInvalid() {
        new ShapelessRecipe(null);
        new ShapelessRecipe(new ItemStack(Material.AIR));

        new ShapelessRecipe(null, new ItemStack(Material.STONE));
        new ShapelessRecipe(NamespacedKey.minecraft(null), new ItemStack(Material.STONE));

        new ShapelessRecipe(NamespacedKey.minecraft("test_key"), new ItemStack(Material.STONE)).group(null);
    }

    @Test
    public void testIngredientsValid() {
        ShapelessRecipe recipe = new ShapelessRecipe(NamespacedKey.minecraft("test_key"), new ItemStack(Material.STONE));

        for(int i = 1; i < 9; i++) {
            recipe.addIngredient(i, new ItemStack(Material.GOLD_BLOCK));
            recipe.removeIngredient(i, new ItemStack(Material.GOLD_BLOCK));
        }

        assertTrue(recipe.getIngredientList().size() <= 9);
        assertTrue(recipe.getGroup().equals(""));
        assertTrue(recipe.getKey().equals(NamespacedKey.minecraft("test_key")));
        assertEquals(recipe.getResult(), new ItemStack(Material.STONE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIngredientsInvalid() {
        ShapelessRecipe recipe = new ShapelessRecipe(NamespacedKey.minecraft("test_key"), new ItemStack(Material.STONE));

        recipe.addIngredient(-1, new ItemStack(Material.STONE));
        recipe.addIngredient(100, new ItemStack(Material.STONE));
        recipe.addIngredient(2, (ItemStack)null);
        recipe.addIngredient(2, new ItemStack(Material.AIR));

        recipe.removeIngredient(-1, new ItemStack(Material.GOLD_BLOCK));
        recipe.removeIngredient(10, new ItemStack(Material.GOLD_BLOCK));
        recipe.removeIngredient(3, (ItemStack)null);
    }
}

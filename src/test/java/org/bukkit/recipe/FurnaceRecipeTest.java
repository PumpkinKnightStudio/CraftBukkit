package org.bukkit.recipe;

import org.bukkit.Material;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.support.AbstractTestingBase;
import org.junit.Test;

import static org.junit.Assert.*;

public class FurnaceRecipeTest extends AbstractTestingBase {
    @Test
    public void testConstructor() {
        ItemStack result = new ItemStack(Material.GOLD_BLOCK);
        ItemStack input =  new ItemStack(Material.GOLD_CHESTPLATE);
        float xp = 3.0f;

        new FurnaceRecipe(result, input, xp);
        FurnaceRecipe recipe = new FurnaceRecipe(result, input, xp);
        recipe.setInput(input);

        assertTrue(recipe.getResult().equals(result));
        assertTrue(recipe.getInput().equals(input));
        assertTrue(recipe.getExperience() == xp);
        assertTrue(!recipe.isExactMatch());

        recipe.setExactMatch(true);
        assertTrue(recipe.isExactMatch());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorInvalid() {
        ItemStack result = new ItemStack(Material.GOLD_BLOCK);
        ItemStack input =  new ItemStack(Material.GOLD_CHESTPLATE);
        float xp = 3.0f;

        new FurnaceRecipe((ItemStack)null, input, xp);
        new FurnaceRecipe(result, (ItemStack)null, xp);
        new FurnaceRecipe(new ItemStack(Material.AIR), input, xp);
        new FurnaceRecipe(result, new ItemStack(Material.AIR), xp);

        new FurnaceRecipe(result, input, xp).setInput((ItemStack)null);
        new FurnaceRecipe(result, input, xp).setInput(new ItemStack(Material.AIR));
    }
}

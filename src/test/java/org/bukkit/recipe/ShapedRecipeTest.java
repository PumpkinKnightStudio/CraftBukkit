package org.bukkit.recipe;

import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.support.AbstractTestingBase;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ShapedRecipeTest extends AbstractTestingBase {

    @Test
    public void testConstructorValid() {
        ItemStack validResult = new ItemStack(Material.STONE);
        NamespacedKey key = NamespacedKey.minecraft("test_key");

        ShapedRecipe recipe = new ShapedRecipe(key, validResult);

        assertEquals(recipe.getResult(), validResult);
        assertEquals(recipe.getKey(), key);
        assertArrayEquals(recipe.getShape(), new String[0]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorInvalid() {
        ItemStack invalidStack = new ItemStack(Material.AIR);

        new ShapedRecipe(null);
        new ShapedRecipe(invalidStack);

        new ShapedRecipe(null, null);
        new ShapedRecipe(null, invalidStack);
        new ShapedRecipe(new NamespacedKey(NamespacedKey.BUKKIT, null), invalidStack);
    }

    @Test
    public void testShapeValid() {
        ShapedRecipe recipe = new ShapedRecipe(NamespacedKey.minecraft("test_key"), new ItemStack(Material.STONE));
        List<String[]> list = Lists.newArrayList(
                new String[]{"###"},
                new String[]{"##"},
                new String[]{"#"}, // end one row
                new String[]{"###", "###"},
                new String[]{"##", "##"},
                new String[]{"#", "#"}, // end two rows
                new String[]{"###", "###", "###"},
                new String[]{"##", "##", "##"},
                new String[]{"#", "#", "#"} // end three rows
        );

        for(String[] shape : list) {
            recipe.shape(shape);
            assertArrayEquals(recipe.getShape(), shape);
        }

        assertNotNull(recipe.getShape());
        assertTrue(recipe.getShape().length > 0 && recipe.getShape().length < 4);
        int lastLen = -1;
        for(String row : recipe.getShape()) {
            assertTrue(row.length() > 0 && row.length() < 4);
            assertTrue(lastLen == -1 || lastLen == row.length());
            lastLen = row.length();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testShapeInvalid() {
        ShapedRecipe recipe = new ShapedRecipe(NamespacedKey.minecraft("test_key"), new ItemStack(Material.STONE));
        List<String[]> list = Lists.newArrayList(
                (String[])null,
                new String[0],
                new String[]{"#", "#", "#", "#"},
                new String[]{"####", "###", "###"},
                new String[]{"##", "##", null},
                new String[]{"####", "####", "#####"}
        );

        for(String[] shape : list) {
            recipe.shape(shape);
        }
    }

    @Test
    public void testIngredientsValid() {
        ItemStack result = new ItemStack(Material.STONE);
        NamespacedKey key = NamespacedKey.minecraft("test_key");
        ShapedRecipe recipe = new ShapedRecipe(key, result);

        List<ItemStack> asterick = Lists.newArrayList(new ItemStack(Material.WOOD, 1, (short) 0), new ItemStack(Material.WOOD, 1, (short) 1),
                                                         new ItemStack(Material.WOOD, 2, (short) 0),new ItemStack(Material.WOOD, 1, (short) -100),
                                                         new ItemStack(Material.WOOD, 4, (short) -1),new ItemStack(Material.WOOD, 1, (short) Short.MAX_VALUE));
        List<ItemStack> xList = Lists.newArrayList(new ItemStack(Material.SOUL_SAND));
        List<ItemStack> exclamation = Lists.newArrayList(asterick);

        String[] shape = new String[] {" * ", "#X#", "! !"};
        recipe.shape(shape);

        recipe.setIngredient('*', asterick.toArray(new ItemStack[]{}));
        recipe.setIngredient('#', new ItemStack(Material.NETHER_STAR));
        recipe.setIngredient('X', xList.toArray(new ItemStack[]{}));
        recipe.setIngredient('!', exclamation.toArray(new ItemStack[]{}));

        assertNotNull(recipe.getIngredientMap());
        assertTrue(!recipe.getIngredientMap().isEmpty());
        assertEquals(recipe.getIngredientMap().get('*'), asterick);
        assertEquals(recipe.getIngredientMap().get('#').get(0), new ItemStack(Material.NETHER_STAR));
        assertEquals(recipe.getIngredientMap().get('X'), xList);
        assertEquals(recipe.getIngredientMap().get('!'), exclamation);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIngredientsInvalid() {
        ItemStack result = new ItemStack(Material.NETHER_STAR);
        NamespacedKey key = NamespacedKey.minecraft("test_key");

        ShapedRecipe recipe = new ShapedRecipe(key, result);
        String[] shape = new String[] {"###", "###", "###"};
        recipe.shape(shape);

        recipe.setIngredient(' ', new ItemStack(Material.GOLD_BLOCK));
        recipe.setIngredient('!', new ItemStack(Material.AIR));
        recipe.setIngredient('#', (ItemStack)null);

        assertNotNull(recipe.getIngredientMap());
        assertTrue(recipe.getIngredientMap().isEmpty());
    }
}

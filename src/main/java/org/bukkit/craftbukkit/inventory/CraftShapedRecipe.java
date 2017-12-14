package org.bukkit.craftbukkit.inventory;

import com.google.common.collect.ListMultimap;
import net.minecraft.server.CraftingManager;
import net.minecraft.server.NonNullList;
import net.minecraft.server.RecipeItemStack;
import net.minecraft.server.ShapedRecipes;

import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;
import java.util.List;

public class CraftShapedRecipe extends ShapedRecipe implements CraftRecipe {
    // TODO: Could eventually use this to add a matches() method or some such
    private ShapedRecipes handle;

    public CraftShapedRecipe(NamespacedKey key, ItemStack result) {
        super(key, result);
    }

    public CraftShapedRecipe(ItemStack result, ShapedRecipes recipe) {
        this(CraftNamespacedKey.fromMinecraft(recipe.key), result);
        this.handle = recipe;
    }

    public static CraftShapedRecipe fromBukkitRecipe(ShapedRecipe recipe) {
        if (recipe instanceof CraftShapedRecipe) {
            return (CraftShapedRecipe) recipe;
        }
        CraftShapedRecipe ret = new CraftShapedRecipe(recipe.getKey(), recipe.getResult());
        String[] shape = recipe.getShape();
        ret.shape(shape);
        ret.group(recipe.getGroup());
        ret.hidden(recipe.isHidden());
        ret.setIngredientMap(recipe.getIngredientMap());
        ret.setExactMatch(recipe.getExactMatch());
        return ret;
    }

    public void addToCraftingManager() {
        String[] shape = this.getShape();
        ListMultimap<Character, ItemStack> ingred = this.getIngredientMap();
        int width = shape[0].length();
        NonNullList<RecipeItemStack> data = NonNullList.a(shape.length * width, RecipeItemStack.a);

        for(int i = 0; i < shape.length; i++) {
            String row = shape[i];
            for(int j = 0; j < row.length(); j++) {

                List<ItemStack> bukkitStacks = ingred.get(row.charAt(j));
                List<net.minecraft.server.ItemStack> choices = new ArrayList<>();
                for(ItemStack item : bukkitStacks) {
                    net.minecraft.server.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
                    choices.add(nmsStack);
                }
                data.set(i * width + j, RecipeItemStack.a(choices.toArray(new net.minecraft.server.ItemStack[choices.size()])));
            }
        }
        ShapedRecipes recipe = new ShapedRecipes(getGroup(), width, shape.length, data, CraftItemStack.asNMSCopy(this.getResult()), shape);
        recipe.hidden = this.isHidden();
        recipe.setExactMatch(this.getExactMatch());
        CraftingManager.a(CraftNamespacedKey.toMinecraft(this.getKey()), recipe);
    }
}

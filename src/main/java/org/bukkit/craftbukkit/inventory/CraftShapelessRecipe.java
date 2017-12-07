package org.bukkit.craftbukkit.inventory;

import java.util.List;

import net.minecraft.server.CraftingManager;
import net.minecraft.server.NonNullList;
import net.minecraft.server.RecipeItemStack;
import net.minecraft.server.ShapelessRecipes;

import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

public class CraftShapelessRecipe extends ShapelessRecipe implements CraftRecipe {
    // TODO: Could eventually use this to add a matches() method or some such
    private ShapelessRecipes recipe;

    public CraftShapelessRecipe(NamespacedKey key, ItemStack result) {
        super(key, result);
    }

    public CraftShapelessRecipe(ItemStack result, ShapelessRecipes recipe) {
        this(CraftNamespacedKey.fromMinecraft(recipe.key), result);
        this.recipe = recipe;
    }

    public static CraftShapelessRecipe fromBukkitRecipe(ShapelessRecipe recipe) {
        if (recipe instanceof CraftShapelessRecipe) {
            return (CraftShapelessRecipe) recipe;
        }
        CraftShapelessRecipe ret = new CraftShapelessRecipe(recipe.getKey(), recipe.getResult());
        for (ItemStack ingred : recipe.getIngredientList()) {
            ret.addIngredient(1, ingred);
        }
        ret.setExactMatch(recipe.getExactMatch());
        ret.hidden(recipe.isHidden());
        recipe.group(recipe.getGroup());
        return ret;
    }

    public void addToCraftingManager() {
        List<ItemStack> ingredientList = this.getIngredientList();
        NonNullList<RecipeItemStack> data = NonNullList.a(ingredientList.size(), RecipeItemStack.a);
        for (int i = 0; i < ingredientList.size(); i++) {
            data.set(i, RecipeItemStack.a(new net.minecraft.server.ItemStack[]{CraftItemStack.asNMSCopy(ingredientList.get(i))}));
        }
        ShapelessRecipes recipe = new ShapelessRecipes(this.getGroup(), CraftItemStack.asNMSCopy(this.getResult()), data);
        recipe.setExactMatch(this.getExactMatch());
        recipe.hidden = this.isHidden();
        CraftingManager.a(CraftNamespacedKey.toMinecraft(this.getKey()), recipe);
    }
}

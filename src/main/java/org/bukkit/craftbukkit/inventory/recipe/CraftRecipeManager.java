package org.bukkit.craftbukkit.inventory.recipe;

import com.google.common.collect.Lists;
import net.minecraft.server.CraftingManager;
import net.minecraft.server.IRecipe;
import net.minecraft.server.RecipesFurnace;
import net.minecraft.server.RegistryMaterials;
import org.apache.commons.lang3.Validate;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftFurnaceRecipe;
import org.bukkit.craftbukkit.inventory.CraftRecipe;
import org.bukkit.craftbukkit.inventory.CraftShapedRecipe;
import org.bukkit.craftbukkit.inventory.CraftShapelessRecipe;
import org.bukkit.craftbukkit.inventory.RecipeIterator;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.recipe.BrewingRecipe;
import org.bukkit.inventory.recipe.CraftingRecipe;
import org.bukkit.inventory.recipe.RecipeManager;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

public class CraftRecipeManager implements RecipeManager {
    private final CraftFurnaceManager furnaceManager = CraftFurnaceManager.getInstance();

    public CraftRecipeManager() { }

    @Nullable
    public CraftingRecipe getCraftingRecipe(NamespacedKey recipeName) {
        return getRecipe(recipeName, CraftingRecipe.class);
    }

    @Nullable
    public ShapedRecipe getShapedRecipe(NamespacedKey recipeName) {
        return getRecipe(recipeName, ShapedRecipe.class);
    }

    @Nullable
    public ShapelessRecipe getShapelessRecipe(NamespacedKey recipeName) {
        return getRecipe(recipeName, ShapelessRecipe.class);
    }

    @Nullable
    private <T extends Recipe> T getRecipe(NamespacedKey key, Class<T> clazz) {
        IRecipe result = CraftingManager.a(CraftNamespacedKey.toMinecraft(key));
        Recipe recipe = result == null ? null : result.toBukkitRecipe();
        return recipe == null ? null : clazz.cast(recipe);
    }

    public boolean addRecipe(Recipe recipe) {
        Validate.notNull(recipe, "Recipe cannot be null");
        if(recipe instanceof BrewingRecipe) {
            return false;
        }
        CraftRecipe toAdd;
        if (recipe instanceof CraftRecipe) {
            toAdd = (CraftRecipe) recipe;
        } else {
            if (recipe instanceof ShapedRecipe) {
                toAdd = CraftShapedRecipe.fromBukkitRecipe((ShapedRecipe) recipe);
            } else if (recipe instanceof ShapelessRecipe) {
                toAdd = CraftShapelessRecipe.fromBukkitRecipe((ShapelessRecipe) recipe);
            } else if (recipe instanceof FurnaceRecipe) {
                return furnaceManager.addRecipe((FurnaceRecipe)recipe);
            } else {
                return false;
            }
        }
        if(toAdd !=  null) {
            toAdd.addToCraftingManager();
            return true;
        }
        return false;
    }

    public List<Recipe> getRecipesFor(ItemStack result) {
        Validate.notNull(result, "Result cannot be null");

        List<Recipe> results = Lists.newArrayList();
        Iterator<Recipe> iter = recipeIterator();
        while (iter.hasNext()) {
            Recipe recipe = iter.next();
            ItemStack stack = recipe.getResult();
            if (stack.getType() != result.getType()) {
                continue;
            }
            if(recipe instanceof FurnaceRecipe) {
                FurnaceRecipe fr = (FurnaceRecipe)recipe;
                if(CraftFurnaceManager.stacksMatch(stack, fr.getResult(), fr.isExactMatch())){
                    results.add(recipe);
                    continue;
                }
            }
            if(stack.getDurability() == result.getDurability() || result.getDurability() == Short.MAX_VALUE || result.getDurability()  < 0)  {
                results.add(recipe);
            }
        }
        return results;
    }

    public void clearRecipes() {
        CraftingManager.recipes = new RegistryMaterials<>();
        CraftFurnaceManager.getInstance().clearRecipes();
    }

    public void resetRecipes() {
        CraftingManager.recipes = new RegistryMaterials<>();
        CraftingManager.init();
        CraftFurnaceManager.getInstance().resetRecipes();
    }

    public Iterator<Recipe> recipeIterator() {
        return new RecipeIterator();
    }
}

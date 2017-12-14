package org.bukkit.craftbukkit.inventory;

import java.util.Iterator;

import org.bukkit.craftbukkit.inventory.recipe.CraftBrewingManager;
import org.bukkit.craftbukkit.inventory.recipe.CraftFurnaceManager;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Recipe;

import net.minecraft.server.CraftingManager;
import net.minecraft.server.IRecipe;
import org.bukkit.inventory.recipe.BrewingRecipe;

public class RecipeIterator implements Iterator<Recipe> {
    private final Iterator<IRecipe> recipes;
    private final Iterator<FurnaceRecipe> smelting;
    private final Iterator<BrewingRecipe> brewing;
    private Iterator<?> removeFrom = null;

    public RecipeIterator() {
        this.recipes = CraftingManager.recipes.iterator();
        this.smelting = CraftFurnaceManager.recipes.iterator();
        this.brewing = CraftBrewingManager.recipes.values().iterator();
    }

    public boolean hasNext() {
        return recipes.hasNext() || smelting.hasNext() || brewing.hasNext();
    }

    public Recipe next() {
        if (recipes.hasNext()) {
            removeFrom = recipes;
            return recipes.next().toBukkitRecipe();
        } else if(smelting.hasNext()){
            removeFrom = smelting;
            return smelting.next();
        } else {
            removeFrom = brewing;
            return brewing.next();
        }
    }

    public void remove() {
        if (removeFrom == null) {
            throw new IllegalStateException();
        }
        removeFrom.remove();
    }
}

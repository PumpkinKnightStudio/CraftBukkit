package org.bukkit.craftbukkit.inventory;


import org.bukkit.craftbukkit.inventory.recipe.CraftFurnaceManager;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;

public class CraftFurnaceRecipe extends FurnaceRecipe implements CraftRecipe {
    public CraftFurnaceRecipe(ItemStack result, ItemStack source, float experience) {
        super(result, source, experience);
    }

    @Deprecated
    public static CraftFurnaceRecipe fromBukkitRecipe(FurnaceRecipe recipe) {
        if (recipe instanceof CraftFurnaceRecipe) {
            return (CraftFurnaceRecipe) recipe;
        }
        CraftFurnaceRecipe craftRecipe = new CraftFurnaceRecipe(recipe.getResult(), recipe.getInput(), recipe.getExperience());
        recipe.setExactMatch(recipe.isExactMatch());
        return craftRecipe;
    }

    @Override
    public void addToCraftingManager() {

    }

    @Override
    public boolean equals(Object other) {
        if(other == null || !(other instanceof FurnaceRecipe) || !(other instanceof CraftFurnaceRecipe)) {
            return false;
        }
        FurnaceRecipe that = (FurnaceRecipe)other;
        return CraftFurnaceManager.stacksMatch(that.getResult(), this.getResult(), this.isExactMatch()) &&
                CraftFurnaceManager.stacksMatch(that.getInput(), this.getInput(), this.isExactMatch())  &&
                that.getExperience() == this.getExperience() && that.isExactMatch() == this.isExactMatch();
    }
}

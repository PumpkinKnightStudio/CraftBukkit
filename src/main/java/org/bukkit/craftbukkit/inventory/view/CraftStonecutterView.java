package org.bukkit.craftbukkit.inventory.view;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.inventory.ContainerStonecutter;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeStonecutting;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.StonecuttingRecipe;
import org.bukkit.inventory.view.StonecutterView;

public class CraftStonecutterView extends CraftInventoryView<ContainerStonecutter> implements StonecutterView {

    public CraftStonecutterView(HumanEntity player, Inventory viewing, ContainerStonecutter container) {
        super(player, viewing, container);
    }

    @Override
    public int getSelectedRecipeIndex() {
        return container.getSelectedRecipeIndex();
    }

    @Override
    public List<StonecuttingRecipe> getRecipes() {
        final List<StonecuttingRecipe> bukkitRecipes = new ArrayList<>();
        for (RecipeHolder<RecipeStonecutting> containerRecipe : container.getRecipes()) {
            // safe cast
            bukkitRecipes.add((StonecuttingRecipe) containerRecipe.toBukkitRecipe());
        }
        return bukkitRecipes;
    }

    @Override
    public int getRecipeAmount() {
        return container.getNumRecipes();
    }

    @Override
    public boolean hasInputItem() {
        return container.hasInputItem();
    }

}

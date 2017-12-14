package org.bukkit.craftbukkit.inventory.recipe;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.server.RecipesFurnace;
import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class CraftFurnaceManager {
    private static final CraftFurnaceManager INSTANCE = new CraftFurnaceManager();
    public static final Set<FurnaceRecipe> recipes = Sets.newHashSet();

    public static CraftFurnaceManager getInstance() {
        return INSTANCE;
    }

    private CraftFurnaceManager() {}

    public boolean addRecipe(FurnaceRecipe recipe) {
        for(FurnaceRecipe fr : recipes) {
            if(fr.equals(recipe)) {
                recipes.remove(fr);
            }
        }
        return recipes.add(recipe);
    }

    // get all recipes that have a certain result
    public Collection<FurnaceRecipe> getRecipesForResult(ItemStack result) {
        List<FurnaceRecipe> results = Lists.newArrayList();
        for(FurnaceRecipe fr : recipes) {
            if(stacksMatch(fr.getResult(), result)) {
                results.add(fr);
            }
        }
        return results;
    }

    public Collection<FurnaceRecipe> getRecipesForInput(ItemStack input) {
        List<FurnaceRecipe> results = Lists.newArrayList();
        for(FurnaceRecipe fr : recipes) {
            if(stacksMatch(fr.getInput(), input)) {

            }
        }
        return results;
    }

    public void clearRecipes() {
        recipes.clear();
        RecipesFurnace.getInstance().recipes.clear();
        RecipesFurnace.getInstance().experience.clear();
    }

    public void resetRecipes() {
        clearRecipes();
        RecipesFurnace rf = new RecipesFurnace();
        RecipesFurnace.getInstance().recipes = rf.recipes;
        RecipesFurnace.getInstance().experience = rf.experience;
    }

    //
    // all static methods below here for organizational purposes
    //

    // correct invalid data(i.e. -1 -> Short.MAX_VALUE)
    public static ItemStack validateData(ItemStack stack) {
        if(stack.getDurability() < 0) { // check for all negative values
            stack.setDurability(Short.MAX_VALUE);
        }
        return stack;
    }

    public static boolean stacksMatch(ItemStack result, ItemStack against) {
        return stacksMatch(result, against, false);
    }

    public static boolean stacksMatch(ItemStack result, ItemStack against, boolean matchNBT) {
        if(result == null || against == null || result.getType() == Material.AIR || against.getType() == Material.AIR) {
            return false;
        }
        result = validateData(result);
        against = validateData(against);
        return !matchNBT ? similar(result, against) : similar(result, against) && net.minecraft.server.ItemStack.equals(convert(result), convert(against));
    }

    private static boolean similar(ItemStack first, ItemStack second) {
        return first.getType() == second.getType() && (first.getDurability() == second.getDurability() || first.getDurability() == Short.MAX_VALUE || second.getDurability() == Short.MAX_VALUE);
    }

    public static org.bukkit.inventory.ItemStack convert(net.minecraft.server.ItemStack nms) {
        return CraftItemStack.asCraftMirror(nms);
    }

    public static net.minecraft.server.ItemStack convert(org.bukkit.inventory.ItemStack itemStack) {
        return CraftItemStack.asNMSCopy(itemStack);
    }

    public static class CraftFurnaceDelegate {
        public static void addDefaultRecipe(net.minecraft.server.ItemStack input, net.minecraft.server.ItemStack result, float experience) {
            recipes.add(new FurnaceRecipe(convert(input), convert(result), experience));
        }

        public static net.minecraft.server.ItemStack getResult(net.minecraft.server.ItemStack input) {
            for(FurnaceRecipe fr : recipes) {
                if(stacksMatch(fr.getInput(), convert(input), fr.isExactMatch())) {
                    return convert(fr.getResult());
                }
            }
            return net.minecraft.server.ItemStack.a;
        }

        public static float getExperience(net.minecraft.server.ItemStack result) {
            for(FurnaceRecipe fr : recipes) {
                if(stacksMatch(fr.getResult(), convert(result), fr.isExactMatch())) {
                    return fr.getExperience();
                }
            }
            return 0.0f;
        }
    }
}

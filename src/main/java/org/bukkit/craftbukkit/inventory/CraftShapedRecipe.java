package org.bukkit.craftbukkit.inventory;

import com.google.common.collect.ArrayListMultimap;
import joptsimple.internal.Strings;
import net.minecraft.server.CraftingManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.NonNullList;
import net.minecraft.server.RecipeItemStack;
import net.minecraft.server.ShapedRecipes;

import org.bukkit.Bukkit;
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
        ret.setHidden(recipe.isHidden());
        ret.setIngredientMap(recipe.getIngredientMap());
        return ret;
    }

    public void addToCraftingManager() {
        String[] shape = this.getShape();
        ArrayListMultimap<Character, ItemStack> ingred = this.getIngredientMap();
        int width = shape[0].length();
        NonNullList<RecipeItemStack> data = NonNullList.a(shape.length * width, RecipeItemStack.a);

        int idx = 0;
        for(int i = 0; i < shape.length; i++) {
            String row = shape[i];
            for(int j = 0; j < row.length(); j++) {

                List<ItemStack> bukkitStacks = ingred.get(row.charAt(j));
                List<net.minecraft.server.ItemStack> choices = new ArrayList<>();
                idx = 0;
                for(ItemStack item : bukkitStacks) {
                    net.minecraft.server.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
                    choices.add(nmsStack);
                }
                data.set(i * width + j, RecipeItemStack.a(choices.toArray(new net.minecraft.server.ItemStack[choices.size()])));
            }
        }
        /*
        for (int i = 0; i < shape.length; i++) {
            String row = shape[i];
            for (int j = 0; j < row.length(); j++) {
                for(ItemStack item : ingred.get(row.charAt(j))) {
                    net.minecraft.server.ItemStack nms = CraftItemStack.asNMSCopy(item);
                    //data.set(i * width +j, RecipeItemStack.a(new net.minecraft.server.ItemStack[]{nms}));
                }
                //data.set(i * width + j, RecipeItemStack.a(new net.minecraft.server.ItemStack[]{CraftItemStack.asNMSCopy(ingred.get(row.charAt(j)))}));
            }
        }
        */
        ShapedRecipes recipe = new ShapedRecipes(getGroup(), width, shape.length, data, CraftItemStack.asNMSCopy(this.getResult()), shape);
        recipe.setHidden(this.isHidden());
        CraftingManager.a(CraftNamespacedKey.toMinecraft(this.getKey()), recipe);
    }
}

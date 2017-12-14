package org.bukkit.craftbukkit.inventory.recipe;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.server.Item;
import net.minecraft.server.Items;
import net.minecraft.server.PotionBrewer;
import net.minecraft.server.PotionRegistry;
import net.minecraft.server.PotionUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.recipe.BrewingManager;
import org.bukkit.inventory.recipe.BrewingRecipe;

import java.util.List;
import java.util.Map;

public class CraftBrewingManager implements BrewingManager {
    public static final Map<NamespacedKey, BrewingRecipe> recipes = Maps.newHashMap(); // default size of all loaded vanilla recipes

    static {
        loadRecipes();
    }

    @Override
    public boolean addBrewingRecipe(BrewingRecipe recipe) {
        if (recipes.containsKey(recipe.getKey()) || CraftBrewingDelegate.canBrew(convert(recipe.getInput()), convert(recipe.getReagent()))) {
            recipes.remove(recipe.getKey());
        }
        recipes.put(recipe.getKey(), recipe);
        return recipes.get(recipe.getKey()).equals(recipe); // make sure it's the recipe we just inserted
    }

    @Override
    public BrewingRecipe getRecipe(NamespacedKey key) {
        return recipes.get(key);
    }

    @Override
    public void clearRecipes() {
        recipes.clear();
        PotionBrewer.a.clear(); // PAIL typeConversions
        PotionBrewer.b.clear(); // PAIL itemConversions
        PotionBrewer.c.clear(); // PAIL potionItems
    }

    @Override
    public void resetRecipes() {
        clearRecipes();
        loadRecipes();
    }

    @Override
    public Map<NamespacedKey, BrewingRecipe> getRecipes() {
        return ImmutableBiMap.copyOf(recipes);
    }

    private static void loadRecipes() {
        recipes.clear();
        if(PotionBrewer.a.isEmpty() || PotionBrewer.b.isEmpty() || PotionBrewer.c.isEmpty()) {
            PotionBrewer.a(); // PAIL init
        }
        // load item conversions
        // these are recipes to convert potions from one type to another
        // i.e. potion + gunpowder = splash_potion
        // i.e. splash_potion + dragon_breath = lingering_potion
        for (PotionBrewer.PredicatedCombination<Item> predicate : PotionBrewer.b) {
            net.minecraft.server.ItemStack nmsInput = new net.minecraft.server.ItemStack(predicate.a); // PAIL input
            net.minecraft.server.ItemStack nmsResult = new net.minecraft.server.ItemStack(predicate.c); // PAIL result
            net.minecraft.server.ItemStack nmsReagent = predicate.b.choices[0]; // PAIL reagent

            BrewingRecipe recipe = new BrewingRecipe(convert(nmsInput), convert(nmsReagent), convert(nmsResult));
            recipe.key(NamespacedKey.minecraft(predicate.c.getName())); // the key is the resulting item's name
            recipes.put(recipe.getKey(), recipe);
        }

        /*
         We have to create recipes for each applicable Material because PotionBrewer does not actually hold ItemStacks.
         PotionBrewer holds the input PotionType, the reagent,and the resulting PotionType.
         The resulting ItemStack is gotten from the TileEntityBrewingStand, and passed into the PotionBrewer class to apply the conversion
         */
        List<Item> itemTypes = Lists.newArrayList(Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION);
        // load actual potion recipes for normal potions
        // i.e. making speed potions longer, more potent, etc.
        for (PotionBrewer.PredicatedCombination<PotionRegistry> predicate : PotionBrewer.a) {
            for(Item item : itemTypes) {
                net.minecraft.server.ItemStack nmsInput = PotionUtil.a(new net.minecraft.server.ItemStack(item), predicate.a);
                net.minecraft.server.ItemStack nmsReagent = predicate.b.choices[0]; // PAIL reagent
                net.minecraft.server.ItemStack nmsResult = PotionUtil.a(new net.minecraft.server.ItemStack(item), predicate.c);

                BrewingRecipe recipe = new BrewingRecipe(convert(nmsInput), convert(nmsReagent), convert(nmsResult));
                // because we're adding multiple of the same PredicatedCombination, we have to differentiate the recipes by telling which item the recipe is for
                recipe.key(NamespacedKey.minecraft(item.getName() + "_" + PotionRegistry.a.b(predicate.c).getKey())); // potion_long_strength; splash_potion_long_strength, etc.
                recipes.put(recipe.getKey(), recipe);
            }
        }
    }

    private static org.bukkit.inventory.ItemStack convert(net.minecraft.server.ItemStack nms) {
        return CraftItemStack.asCraftMirror(nms);
    }

    private static net.minecraft.server.ItemStack convert(org.bukkit.inventory.ItemStack itemStack) {
        return CraftItemStack.asNMSCopy(itemStack);
    }


    public final static class CraftBrewingDelegate {

        // is the item a reagent in any recipe
        public static boolean isReagent(net.minecraft.server.ItemStack reagent) {
            for(BrewingRecipe recipe : recipes.values()) {
                if(reagent.doMaterialsMatch(convert(recipe.getReagent()))) {
                    return true;
                }
            }
            return false;
        }

        // can the input and reagent be used in a reaction
        public static boolean canBrew(net.minecraft.server.ItemStack input, net.minecraft.server.ItemStack reagent) {
            if(!PotionBrewer.d.apply(input)) { // is the input a potion item?
                return false;
            }
            for(BrewingRecipe recipe : recipes.values()) {
                if(net.minecraft.server.ItemStack.equals(convert(recipe.getInput()), input) && reagent.doMaterialsMatch(convert(recipe.getReagent()))) {
                    return true;
                }
            }
            return false;
        }

        // perform a reaction with a given reagent and input
        public static net.minecraft.server.ItemStack doReaction(net.minecraft.server.ItemStack reagent, net.minecraft.server.ItemStack inputItem) {
            if(inputItem.isEmpty()) {
                return inputItem;
            }
            for(BrewingRecipe recipe : recipes.values()) {
                if(net.minecraft.server.ItemStack.equals(convert(recipe.getInput()), inputItem) && reagent.doMaterialsMatch(convert(recipe.getReagent()))) {
                    return convert(recipe.getResult());
                }
            }
            return inputItem;
        }
    }
}

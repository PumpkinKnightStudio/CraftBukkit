package org.bukkit.craftbukkit.inventory;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.food.FoodInfo;
import net.minecraft.world.item.Item;
import org.bukkit.craftbukkit.potion.CraftPotionUtil;
import org.bukkit.inventory.ItemNutrition;
import org.bukkit.potion.PotionEffect;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class CraftItemNutrition implements ItemNutrition {
    private final static HashMap<Item, ItemNutrition> nutritions = new HashMap<>();
    private final int nutrition;
    private final float saturationModifier;
    private final boolean isMeat;
    private final boolean canAlwaysEat;
    private final boolean fastFood;
    private final List<Map.Entry<PotionEffect, Float>> effects;

    static ItemNutrition getNutrition(Item item) {
        if (!nutritions.containsKey(item) && item.getFoodInfo() != null) {
            nutritions.put(item, new CraftItemNutrition(item.getFoodInfo()));
        }

        return nutritions.get(item);
    }

    private CraftItemNutrition(FoodInfo foodInfo) {
        this.nutrition = foodInfo.getNutrition();
        this.saturationModifier = foodInfo.getSaturationModifier();
        this.isMeat = foodInfo.c(); // PAIL rename isMeat
        this.canAlwaysEat = foodInfo.d(); // PAIL rename canAlwaysEat
        this.fastFood = foodInfo.e(); // PAIL rename isFastFood
        List<Map.Entry<PotionEffect, Float>> effects = new ArrayList<>();
        for (Pair<MobEffect, Float> pair : foodInfo.f()) {
            effects.add(new AbstractMap.SimpleEntry<>(CraftPotionUtil.toBukkit(pair.getFirst()), pair.getSecond()));
        }

        this.effects = ImmutableList.copyOf(effects);
    }

    @Override
    public int getNutrition() {
        return nutrition;
    }

    @Override
    public float getSaturationModifier() {
        return saturationModifier;
    }

    @Override
    public boolean isMeat() {
        return isMeat;
    }

    @Override
    public boolean isCanAlwaysEat() {
        return canAlwaysEat;
    }

    @Override
    public boolean isFastFood() {
        return fastFood;
    }

    @Override
    public List<Map.Entry<PotionEffect, Float>> getEffects() {
        return effects;
    }
}

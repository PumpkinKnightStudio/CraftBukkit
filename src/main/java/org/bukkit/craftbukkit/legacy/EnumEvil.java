package org.bukkit.craftbukkit.legacy;

import com.google.common.collect.Lists;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Art;
import org.bukkit.Fluid;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

/**
 * @deprecated only for legacy use, do not use
 */
@Deprecated
public class EnumEvil {

    private static final Map<Class<?>, Registry<?>> REGISTRIES = new HashMap<>();

    static {
        REGISTRIES.put(Biome.class, Registry.BIOME);
        REGISTRIES.put(Art.class, Registry.ART);
        REGISTRIES.put(Fluid.class, Registry.FLUID);
        REGISTRIES.put(EntityType.class, Registry.ENTITY_TYPE);
        REGISTRIES.put(Statistic.class, Registry.STATISTIC);
        REGISTRIES.put(Sound.class, Registry.SOUNDS);
        REGISTRIES.put(Material.class, Registry.MATERIAL);
        REGISTRIES.put(Attribute.class, Registry.ATTRIBUTE);
        REGISTRIES.put(Villager.Type.class, Registry.VILLAGER_TYPE);
        REGISTRIES.put(Villager.Profession.class, Registry.VILLAGER_PROFESSION);
    }

    public static Object getEnumConstants(Class<?> clazz) {
        if (clazz.isEnum()) {
            return clazz.getEnumConstants();
        }

        Registry<?> registry = REGISTRIES.get(clazz);

        if (registry == null) {
            return clazz.getEnumConstants();
        }

        // Need to do this in such away to avoid ClassCastException
        List<?> values = Lists.newArrayList(registry);
        Object array = Array.newInstance(clazz, values.size());

        for (int i = 0; i < values.size(); i++) {
            Array.set(array, i, values.get(i));
        }

        return array;
    }
}

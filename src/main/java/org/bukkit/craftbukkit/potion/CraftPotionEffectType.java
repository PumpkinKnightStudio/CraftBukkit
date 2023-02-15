package org.bukkit.craftbukkit.potion;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import net.minecraft.core.IRegistry;
import net.minecraft.world.effect.MobEffectList;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.potion.PotionEffectType;

public class CraftPotionEffectType extends PotionEffectType {

    public static PotionEffectType minecraftToBukkit(MobEffectList minecraft) {
        if (minecraft == null) {
            return null;
        }

        return Registry.POTION_EFFECT_TYPE.get(CraftNamespacedKey.fromMinecraft(BuiltInRegistries.MOB_EFFECT.getKey(minecraft)));
    }

    public static MobEffectList bukkitToMinecraft(PotionEffectType bukkit) {
        if (bukkit == null) {
            return null;
        }

        return ((CraftPotionEffectType) bukkit).getHandle();
    }

    private final NamespacedKey key;
    private final MobEffectList handle;
    private final String name;
    private final int id;

    public CraftPotionEffectType(NamespacedKey key, MobEffectList handle) {
        this.key = key;
        this.handle = handle;
        // For backwards compatibility, minecraft values will stile return the uppercase name without the namespace,
        // in case plugins use for example the name as key in a config file to receive potion effect type specific values.
        // Custom potion effect types will return the key with namespace. For a plugin this should look than like a new potion effect type
        // (which can always be added in new minecraft versions and the plugin should therefore handle it accordingly).
        if (NamespacedKey.MINECRAFT.equals(key.getNamespace())) {
            this.name = key.getKey().toUpperCase();
        } else {
            this.name = key.toString();
        }
        this.id = MobEffectList.getId(handle);
        ID_MAP.put(id, this);
    }

    @Override
    public double getDurationModifier() {
        return 1.0D;
    }

    public MobEffectList getHandle() {
        return handle;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isInstant() {
        return handle.isInstantenous();
    }

    @Override
    public Color getColor() {
        return Color.fromRGB(handle.getColor());
    }

    @Override
    public NamespacedKey getKey() {
        return key;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof CraftPotionEffectType)) {
            return false;
        }

        return getKey().equals(((PotionEffectType) other).getKey());
    }

    @Override
    public int hashCode() {
        return getKey().hashCode();
    }

    @Override
    public String toString() {
        return "CraftPotionEffectType[" + getKey() + "]";
    }

    public static class CraftPotionEffectTypeRegistry extends CraftRegistry<PotionEffectType, MobEffectList> {
        private static final Map<NamespacedKey, NamespacedKey> NAME_MAP = new HashMap<>();

        private static void add(String oldName, String newName) {
            NAME_MAP.put(NamespacedKey.fromString(oldName), NamespacedKey.fromString(newName));
        }
        static {
            // Add legacy names
            add("slow", "slowness");
            add("fast_digging", "haste");
            add("slow_digging", "mining_fatigue");
            add("increase_damage", "strength");
            add("heal", "instant_health");
            add("harm", "instant_damage");
            add("jump", "jump_boost");
            add("confusion", "nausea");
            add("damage_resistance", "resistance");
        }

        public CraftPotionEffectTypeRegistry(IRegistry<MobEffectList> minecraftRegistry, BiFunction<NamespacedKey, MobEffectList, PotionEffectType> minecraftToBukkit) {
            super(minecraftRegistry, minecraftToBukkit);
        }

        @Override
        public PotionEffectType createBukkit(NamespacedKey namespacedKey, MobEffectList mobEffectList) {
            // convert legacy names to new one
            if (NAME_MAP.containsKey(namespacedKey)) {
                return get(NAME_MAP.get(namespacedKey));
            }

            if (mobEffectList == null) {
                return null;
            }

            return super.createBukkit(namespacedKey, mobEffectList);
        }
    }
}

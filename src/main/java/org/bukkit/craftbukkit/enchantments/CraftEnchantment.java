package org.bukkit.craftbukkit.enchantments;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import net.minecraft.core.IRegistry;
import net.minecraft.world.item.enchantment.EnchantmentBinding;
import net.minecraft.world.item.enchantment.EnchantmentVanishing;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemStack;

public class CraftEnchantment extends Enchantment {
    private final NamespacedKey key;
    private final net.minecraft.world.item.enchantment.Enchantment target;
    private final String name;

    public CraftEnchantment(NamespacedKey key, net.minecraft.world.item.enchantment.Enchantment target) {
        this.key = key;
        this.target = target;
        // For backwards compatibility, minecraft values will stile return the uppercase name without the namespace,
        // in case plugins use for example the name as key in a config file to receive enchantment specific values.
        // Custom enchantments will return the key with namespace. For a plugin this should look than like a new enchantment
        // (which can always be added in new minecraft versions and the plugin should therefore handle it accordingly).
        if (NamespacedKey.MINECRAFT.equals(key.getNamespace())) {
            this.name = key.getKey().toUpperCase();
        } else {
            this.name = key.toString();
        }
    }

    @Override
    public int getMaxLevel() {
        return target.getMaxLevel();
    }

    @Override
    public int getStartLevel() {
        return target.getMinLevel();
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        switch (target.category) {
        case ARMOR:
            return EnchantmentTarget.ARMOR;
        case ARMOR_FEET:
            return EnchantmentTarget.ARMOR_FEET;
        case ARMOR_HEAD:
            return EnchantmentTarget.ARMOR_HEAD;
        case ARMOR_LEGS:
            return EnchantmentTarget.ARMOR_LEGS;
        case ARMOR_CHEST:
            return EnchantmentTarget.ARMOR_TORSO;
        case DIGGER:
            return EnchantmentTarget.TOOL;
        case WEAPON:
            return EnchantmentTarget.WEAPON;
        case BOW:
            return EnchantmentTarget.BOW;
        case FISHING_ROD:
            return EnchantmentTarget.FISHING_ROD;
        case BREAKABLE:
            return EnchantmentTarget.BREAKABLE;
        case WEARABLE:
            return EnchantmentTarget.WEARABLE;
        case TRIDENT:
            return EnchantmentTarget.TRIDENT;
        case CROSSBOW:
            return EnchantmentTarget.CROSSBOW;
        case VANISHABLE:
            return EnchantmentTarget.VANISHABLE;
        default:
            return null;
        }
    }

    @Override
    public boolean isTreasure() {
        return target.isTreasureOnly();
    }

    @Override
    public boolean isCursed() {
        return target instanceof EnchantmentBinding || target instanceof EnchantmentVanishing;
    }

    @Override
    public boolean canEnchantItem(ItemStack item) {
        return target.canEnchant(CraftItemStack.asNMSCopy(item));
    }

    @Override
    public String getName() {
        return name;
    }

    public static net.minecraft.world.item.enchantment.Enchantment getRaw(Enchantment enchantment) {
        if (enchantment instanceof EnchantmentWrapper) {
            enchantment = ((EnchantmentWrapper) enchantment).getEnchantment();
        }

        if (enchantment instanceof CraftEnchantment) {
            return ((CraftEnchantment) enchantment).target;
        }

        return null;
    }

    @Override
    public boolean conflictsWith(Enchantment other) {
        if (other instanceof EnchantmentWrapper) {
            other = ((EnchantmentWrapper) other).getEnchantment();
        }
        if (!(other instanceof CraftEnchantment)) {
            return false;
        }
        CraftEnchantment ench = (CraftEnchantment) other;
        return !target.isCompatibleWith(ench.target);
    }

    public net.minecraft.world.item.enchantment.Enchantment getHandle() {
        return target;
    }

    @Override
    public NamespacedKey getKey() {
        return key;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof CraftEnchantment)) {
            return false;
        }

        return getKey().equals(((Enchantment) other).getKey());
    }

    @Override
    public int hashCode() {
        return getKey().hashCode();
    }

    @Override
    public String toString() {
        return "CraftEnchantment[" + getKey() + "]";
    }

    public static class CraftEnchantmentRegistry extends CraftRegistry<Enchantment, net.minecraft.world.item.enchantment.Enchantment> {
        private static final Map<NamespacedKey, NamespacedKey> NAME_MAP = new HashMap<>();

        private static void add(String oldName, String newName) {
            NAME_MAP.put(NamespacedKey.fromString(oldName), NamespacedKey.fromString(newName));
        }
        static {
            // Add legacy names
            add("protection_environmental", "protection");
            add("protection_fire", "fire_protection");
            add("protection_fall", "feather_falling");
            add("protection_explosions", "blast_protection");
            add("protection_projectile", "projectile_protection");
            add("oxygen", "respiration");
            add("water_worker", "aqua_affinity");
            add("damage_all", "sharpness");
            add("damage_undead", "smite");
            add("damage_arthropods", "bane_of_arthropods");
            add("loot_bonus_mobs", "looting");
            add("sweeping_edge", "sweeping");
            add("dig_speed", "efficiency");
            add("durability", "unbreaking");
            add("loot_bonus_blocks", "fortune");
            add("arrow_damage", "power");
            add("arrow_knockback", "punch");
            add("arrow_fire", "flame");
            add("arrow_infinite", "infinity");
            add("luck", "luck_of_the_sea");
        }

        public CraftEnchantmentRegistry(IRegistry<net.minecraft.world.item.enchantment.Enchantment> minecraftRegistry, BiFunction<NamespacedKey, net.minecraft.world.item.enchantment.Enchantment, Enchantment> minecraftToBukkit) {
            super(minecraftRegistry, minecraftToBukkit);
        }

        @Override
        public Enchantment createBukkit(NamespacedKey namespacedKey, net.minecraft.world.item.enchantment.Enchantment enchantment) {
            // convert legacy names to new one
            if (NAME_MAP.containsKey(namespacedKey)) {
                return get(NAME_MAP.get(namespacedKey));
            }

            if (enchantment == null) {
                return null;
            }

            return super.createBukkit(namespacedKey, enchantment);
        }
    }
}

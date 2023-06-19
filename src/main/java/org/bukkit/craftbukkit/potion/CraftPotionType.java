package org.bukkit.craftbukkit.potion;

import com.google.common.base.Preconditions;
import java.util.List;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.alchemy.PotionRegistry;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CraftPotionType extends PotionType {
    private static int count = 0;

    public static PotionType minecraftToBukkit(PotionRegistry minecraft) {
        Preconditions.checkArgument(minecraft != null);

        IRegistry<PotionRegistry> registry = CraftRegistry.getMinecraftRegistry().registryOrThrow(Registries.POTION);
        PotionType bukkit = Registry.POTION.get(CraftNamespacedKey.fromMinecraft(registry.getKey(minecraft)));

        Preconditions.checkArgument(bukkit != null);

        return bukkit;
    }

    public static PotionRegistry bukkitToMinecraft(PotionType bukkit) {
        Preconditions.checkArgument(bukkit != null);

        return ((CraftPotionType) bukkit).getHandle();
    }

    private static PotionEffect toBukkit(MobEffect effect) {
        // Copy here from CraftPotionUtil, because of class loader order
        PotionEffectType type = CraftPotionEffectType.minecraftToBukkit(effect.getEffect());
        int amp = effect.getAmplifier();
        int duration = effect.getDuration();
        boolean ambient = effect.isAmbient();
        boolean particles = effect.isVisible();
        return new PotionEffect(type, duration, amp, ambient, particles);
    }

    private final NamespacedKey key;
    private final PotionRegistry potion;
    private final List<PotionEffect> potionEffects;
    private final boolean upgradeable;
    private final boolean extendable;
    private final int maxLevel;
    private final String name;
    private final int ordinal;

    public CraftPotionType(NamespacedKey key, PotionRegistry potion) {
        this.key = key;
        this.potion = potion;
        // For backwards compatibility, minecraft values will stile return the uppercase name without the namespace,
        // in case plugins use for example the name as key in a config file to receive potion specific values.
        // Custom potions will return the key with namespace. For a plugin this should look than like a new potion
        // (which can always be added in new minecraft versions and the plugin should therefore handle it accordingly).
        if (NamespacedKey.MINECRAFT.equals(key.getNamespace())) {
            this.name = key.getKey().toUpperCase();
        } else {
            this.name = key.toString();
        }
        this.ordinal = count++;

        this.potionEffects = potion.getEffects().stream().map(CraftPotionType::toBukkit).toList();
        this.upgradeable = Registry.POTION.get(new NamespacedKey(key.getNamespace(), "strong_" + key.getKey())) != null;
        this.extendable = Registry.POTION.get(new NamespacedKey(key.getNamespace(), "long_" + key.getKey())) != null;
        this.maxLevel = upgradeable ? 2 : 1;
    }

    public PotionRegistry getHandle() {
        return potion;
    }

    @Override
    public NamespacedKey getKey() {
        return key;
    }

    @Override
    public int compareTo(PotionType potionType) {
        return ordinal - potionType.ordinal();
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int ordinal() {
        return ordinal;
    }

    @Override
    public String toString() {
        // For backwards compatibility
        return name();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof CraftPotionType)) {
            return false;
        }

        return getKey().equals(((PotionType) other).getKey());
    }

    @Nullable
    @Override
    public PotionEffectType getEffectType() {
        return potionEffects.isEmpty() ? null : potionEffects.get(0).getType();
    }

    @NotNull
    @Override
    public List<PotionEffect> getPotionEffects() {
        return potionEffects;
    }

    @Override
    public boolean isInstant() {
        return potion.hasInstantEffects();
    }

    @Override
    public boolean isUpgradeable() {
        return upgradeable;
    }

    @Override
    public boolean isExtendable() {
        return extendable;
    }

    @Override
    public int getMaxLevel() {
        return maxLevel;
    }
}

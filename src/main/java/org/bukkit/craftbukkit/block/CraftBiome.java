package org.bukkit.craftbukkit.block;

import com.google.common.base.Preconditions;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.BiomeBase;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;

public class CraftBiome extends Biome {
    private static int count = 0;

    public static Biome minecraftToBukkit(Holder<BiomeBase> minecraft) {
        return minecraftToBukkit(minecraft.value());
    }

    public static Biome minecraftToBukkit(BiomeBase minecraft) {
        Preconditions.checkArgument(minecraft != null);

        IRegistry<BiomeBase> registry = CraftRegistry.getMinecraftRegistry().registryOrThrow(Registries.BIOME);
        Biome bukkit = Registry.BIOME.get(CraftNamespacedKey.fromMinecraft(registry.getKey(minecraft)));

        Preconditions.checkArgument(bukkit != null);

        return bukkit;
    }

    public static Holder<BiomeBase> bukkitToMinecraft(Biome bukkit) {
        Preconditions.checkArgument(bukkit != null);

        IRegistry<BiomeBase> registry = CraftRegistry.getMinecraftRegistry().registryOrThrow(Registries.BIOME);

        return registry.wrapAsHolder(((CraftBiome) bukkit).getHandle());
    }

    private final NamespacedKey key;
    private final BiomeBase biome;
    private final String name;
    private final int ordinal;

    public CraftBiome(NamespacedKey key, BiomeBase biome) {
        this.key = key;
        this.biome = biome;
        // For backwards compatibility, minecraft values will stile return the uppercase name without the namespace,
        // in case plugins use for example the name as key in a config file to receive biome specific values.
        // Custom biomes will return the key with namespace. For a plugin this should look than like a new biome
        // (which can always be added in new minecraft versions and the plugin should therefore handle it accordingly).
        if (NamespacedKey.MINECRAFT.equals(key.getNamespace())) {
            this.name = key.getKey().toUpperCase();
        } else {
            this.name = key.toString();
        }
        this.ordinal = count++;
    }

    public BiomeBase getHandle() {
        return biome;
    }

    @Override
    public int compareTo(Biome biome) {
        return ordinal - biome.ordinal();
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
    public NamespacedKey getKey() {
        return key;
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

        if (!(other instanceof CraftBiome)) {
            return false;
        }

        return getKey().equals(((Biome) other).getKey());
    }

    @Override
    public int hashCode() {
        return getKey().hashCode();
    }
}

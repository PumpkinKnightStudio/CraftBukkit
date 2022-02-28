package org.bukkit.craftbukkit.block;

import java.util.function.BiFunction;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.BiomeBase;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;

public class CraftBiome extends Biome {
    private static int count = 0;

    public static Biome minecraftToBukkit(IRegistry<BiomeBase> registry, Holder<BiomeBase> minecraft) {
        return minecraftToBukkit(registry, minecraft.value());
    }

    public static Biome minecraftToBukkit(IRegistry<BiomeBase> registry, BiomeBase minecraft) {
        if (minecraft == null) {
            return null;
        }

        return Registry.BIOME.get(CraftNamespacedKey.fromMinecraft(registry.getKey(minecraft)));
    }

    public static Holder<BiomeBase> bukkitToMinecraft(IRegistry<BiomeBase> registry, Biome bukkit) {
        if (bukkit == null || bukkit == Biome.CUSTOM) {
            return null;
        }

        return registry.getHolderOrThrow(ResourceKey.create(IRegistry.BIOME_REGISTRY, CraftNamespacedKey.toMinecraft(bukkit.getKey())));
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

    public static class CraftBiomeRegistry extends CraftRegistry<Biome, BiomeBase> {
        private static final NamespacedKey CUSTOM = NamespacedKey.minecraft("custom");

        public CraftBiomeRegistry(IRegistry<BiomeBase> minecraftRegistry, BiFunction<NamespacedKey, BiomeBase, Biome> minecraftToBukkit) {
            super(minecraftRegistry, minecraftToBukkit);
        }

        @Override
        public Biome createBukkit(NamespacedKey namespacedKey, BiomeBase minecraft) {
            // For backwards compatibility
            if (CUSTOM.equals(namespacedKey)) {
                return new CraftBiome(namespacedKey, minecraft);
            }

            return super.createBukkit(namespacedKey, minecraft);
        }

        @Override
        public Stream<Biome> values() {
            return Stream.concat(super.values(), Stream.of(get(CUSTOM)));
        }
    }
}

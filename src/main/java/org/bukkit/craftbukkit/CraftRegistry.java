package org.bukkit.craftbukkit;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.block.CraftBiome;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;

public class CraftRegistry<BUKKIT extends Keyed, MINECRAFT> implements Registry<BUKKIT> {

    public static <BUKKIT extends Keyed> Registry<?> createRegistry(Class<BUKKIT> bukkitClass, IRegistryCustom registryHolder) {
        if (bukkitClass == Biome.class) {
            return new CraftRegistry<>(registryHolder.b(IRegistry.BIOME_REGISTRY), CraftBiome::new, true);
        }

        return null;
    }

    private static final NamespacedKey CUSTOM = NamespacedKey.minecraft("custom");

    private final Map<NamespacedKey, BUKKIT> cache = new HashMap<>();
    private final IRegistry<MINECRAFT> minecraftRegistry;
    private final BiFunction<NamespacedKey, MINECRAFT, BUKKIT> minecraftToBukkit;
    private final boolean hasCustom;

    public CraftRegistry(IRegistry<MINECRAFT> minecraftRegistry, BiFunction<NamespacedKey, MINECRAFT, BUKKIT> minecraftToBukkit) {
        this(minecraftRegistry, minecraftToBukkit, false);
    }

    public CraftRegistry(IRegistry<MINECRAFT> minecraftRegistry, BiFunction<NamespacedKey, MINECRAFT, BUKKIT> minecraftToBukkit, boolean hasCustom) {
        this.minecraftRegistry = minecraftRegistry;
        this.minecraftToBukkit = minecraftToBukkit;
        this.hasCustom = hasCustom;
    }

    @Override
    public BUKKIT get(NamespacedKey namespacedKey) {
        BUKKIT cached = cache.get(namespacedKey);
        if (cached != null) {
            return cached;
        }

        MINECRAFT minecraft;
        // special case for custom biome field
        if (hasCustom && CUSTOM.equals(namespacedKey)) {
            minecraft = null;
        } else {
            minecraft = minecraftRegistry.get(CraftNamespacedKey.toMinecraft(namespacedKey));

            if (minecraft == null) {
                return null;
            }
        }

        BUKKIT bukkit = minecraftToBukkit.apply(namespacedKey, minecraft);
        cache.put(namespacedKey, bukkit);

        return bukkit;
    }

    @Override
    public Iterator<BUKKIT> iterator() {
        if (hasCustom) {
            return Stream.concat(minecraftRegistry.keySet().stream().map(minecraftKey -> get(CraftNamespacedKey.fromMinecraft(minecraftKey))), Stream.of(get(CUSTOM))).iterator();
        } else {
            return minecraftRegistry.keySet().stream().map(minecraftKey -> get(CraftNamespacedKey.fromMinecraft(minecraftKey))).iterator();
        }
    }
}

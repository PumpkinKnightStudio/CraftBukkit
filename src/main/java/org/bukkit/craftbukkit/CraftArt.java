package org.bukkit.craftbukkit;

import com.google.common.base.Preconditions;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.decoration.PaintingVariant;
import org.bukkit.Art;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.block.CraftBiome;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;

public class CraftArt extends Art {
    private static final int UNIT_MULTIPLIER = 16;
    private static int count = 0;

    public static Art minecraftToBukkit(Holder<PaintingVariant> minecraft) {
        Preconditions.checkArgument(minecraft != null);

        IRegistry<PaintingVariant> registry = CraftRegistry.getMinecraftRegistry().registryOrThrow(Registries.PAINTING_VARIANT);
        Art bukkit = Registry.ART.get(CraftNamespacedKey.fromMinecraft(registry.getKey(minecraft.value())));

        Preconditions.checkArgument(bukkit != null);

        return bukkit;
    }

    public static Holder<PaintingVariant> bukkitToMinecraft(Art bukkit) {
        Preconditions.checkArgument(bukkit != null);
        IRegistry<PaintingVariant> registry = CraftRegistry.getMinecraftRegistry().registryOrThrow(Registries.PAINTING_VARIANT);

        return registry.wrapAsHolder(((CraftArt) bukkit).getHandle());
    }

    private final NamespacedKey key;
    private final PaintingVariant painting;
    private final String name;
    private final int ordinal;

    public CraftArt(NamespacedKey key, PaintingVariant painting) {
        this.key = key;
        this.painting = painting;
        // For backwards compatibility, minecraft values will stile return the uppercase name without the namespace,
        // in case plugins use for example the name as key in a config file to receive art specific values.
        // Custom arts will return the key with namespace. For a plugin this should look than like a new art
        // (which can always be added in new minecraft versions and the plugin should therefore handle it accordingly).
        if (NamespacedKey.MINECRAFT.equals(key.getNamespace())) {
            this.name = key.getKey().toUpperCase();
        } else {
            this.name = key.toString();
        }
        this.ordinal = count++;
    }

    public PaintingVariant getHandle() {
        return painting;
    }

    @Override
    public int getBlockWidth() {
        return painting.getWidth() / UNIT_MULTIPLIER;
    }

    @Override
    public int getBlockHeight() {
        return painting.getHeight() / UNIT_MULTIPLIER;
    }

    @Override
    public NamespacedKey getKey() {
        return key;
    }

    @Override
    public int getId() {
        return ordinal;
    }

    @Override
    public int compareTo(Art art) {
        return ordinal - art.ordinal();
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

        if (!(other instanceof CraftArt)) {
            return false;
        }

        return getKey().equals(((Art) other).getKey());
    }

    @Override
    public int hashCode() {
        return getKey().hashCode();
    }
}

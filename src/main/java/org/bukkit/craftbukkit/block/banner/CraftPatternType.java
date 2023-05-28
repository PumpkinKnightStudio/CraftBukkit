package org.bukkit.craftbukkit.block.banner;

import com.google.common.base.Preconditions;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.EnumBannerPatternType;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.banner.PatternType;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.jetbrains.annotations.NotNull;

public class CraftPatternType extends PatternType {

    private static int count = 0;

    public static PatternType minecraftToBukkit(EnumBannerPatternType minecraft) {
        Preconditions.checkArgument(minecraft != null);

        IRegistry<EnumBannerPatternType> registry = CraftRegistry.getMinecraftRegistry().registryOrThrow(Registries.BANNER_PATTERN);
        PatternType bukkit = Registry.BANNER_PATTERN.get(CraftNamespacedKey.fromMinecraft(registry.getKey(minecraft)));

        Preconditions.checkArgument(bukkit != null);

        return bukkit;
    }

    public static EnumBannerPatternType bukkitToMinecraft(PatternType bukkit) {
        Preconditions.checkArgument(bukkit != null);

        return ((CraftPatternType) bukkit).getHandle();
    }

    private final NamespacedKey key;
    private final EnumBannerPatternType bannerPatternType;
    private final String name;
    private final int ordinal;

    public CraftPatternType(NamespacedKey key, EnumBannerPatternType bannerPatternType) {
        this.key = key;
        this.bannerPatternType = bannerPatternType;
        // For backwards compatibility, minecraft values will stile return the uppercase name without the namespace,
        // in case plugins use for example the name as key in a config file to receive pattern type specific values.
        // Custom pattern types will return the key with namespace. For a plugin this should look than like a new pattern type
        // (which can always be added in new minecraft versions and the plugin should therefore handle it accordingly).
        if (NamespacedKey.MINECRAFT.equals(key.getNamespace())) {
            this.name = key.getKey().toUpperCase();
        } else {
            this.name = key.toString();
        }
        this.ordinal = count++;
    }

    public EnumBannerPatternType getHandle() {
        return bannerPatternType;
    }

    @Override
    public NamespacedKey getKey() {
        return key;
    }

    @Override
    public int compareTo(PatternType patternType) {
        return ordinal - patternType.ordinal();
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

        if (!(other instanceof CraftPatternType)) {
            return false;
        }

        return getKey().equals(((PatternType) other).getKey());
    }

    @Override
    public int hashCode() {
        return getKey().hashCode();
    }

    @NotNull
    @Override
    public String getIdentifier() {
        return bannerPatternType.getHashname();
    }
}

package org.bukkit.craftbukkit.attribute;

import com.google.common.base.Preconditions;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.AttributeBase;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;

public class CraftAttribute extends Attribute {
    private static int count = 0;

    public static Attribute minecraftToBukkit(AttributeBase minecraft) {
        Preconditions.checkArgument(minecraft != null);

        IRegistry<AttributeBase> registry = CraftRegistry.getMinecraftRegistry().registryOrThrow(Registries.ATTRIBUTE);
        Attribute bukkit = Registry.ATTRIBUTE.get(CraftNamespacedKey.fromMinecraft(registry.getKey(minecraft)));

        Preconditions.checkArgument(bukkit != null);

        return bukkit;
    }

    public static AttributeBase bukkitToMinecraft(Attribute bukkit) {
        Preconditions.checkArgument(bukkit != null);

        return ((CraftAttribute) bukkit).getHandle();
    }

    private final NamespacedKey key;
    private final AttributeBase attributeBase;
    private final String name;
    private final int ordinal;

    public CraftAttribute(NamespacedKey key, AttributeBase attributeBase) {
        this.key = key;
        this.attributeBase = attributeBase;
        // For backwards compatibility, minecraft values will stile return the uppercase name without the namespace,
        // in case plugins use for example the name as key in a config file to receive attribute specific values.
        // Custom attributes will return the key with namespace. For a plugin this should look than like a new attribute
        // (which can always be added in new minecraft versions and the plugin should therefore handle it accordingly).
        if (NamespacedKey.MINECRAFT.equals(key.getNamespace())) {
            this.name = key.getKey().toUpperCase().replace(".", "_");
        } else {
            this.name = key.toString();
        }
        this.ordinal = count++;
    }

    public AttributeBase getHandle() {
        return attributeBase;
    }

    @Override
    public NamespacedKey getKey() {
        return key;
    }

    @Override
    public int compareTo(Attribute attribute) {
        return ordinal - attribute.ordinal();
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

        if (!(other instanceof CraftAttribute)) {
            return false;
        }

        return getKey().equals(((Attribute) other).getKey());
    }

    @Override
    public int hashCode() {
        return getKey().hashCode();
    }
}

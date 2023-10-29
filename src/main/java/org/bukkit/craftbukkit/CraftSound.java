package org.bukkit.craftbukkit;

import com.google.common.base.Preconditions;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEffect;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;

public class CraftSound implements Sound {
    private static int count = 0;

    public static Sound minecraftToBukkit(SoundEffect minecraft) {
        Preconditions.checkArgument(minecraft != null);

        IRegistry<SoundEffect> registry = CraftRegistry.getMinecraftRegistry(Registries.SOUND_EVENT);
        Sound bukkit = Registry.SOUNDS.get(CraftNamespacedKey.fromMinecraft(registry.getResourceKey(minecraft).orElseThrow().location()));

        Preconditions.checkArgument(bukkit != null);

        return bukkit;
    }

    public static SoundEffect bukkitToMinecraft(Sound bukkit) {
        Preconditions.checkArgument(bukkit != null);

        return ((CraftSound) bukkit).getHandle();
    }

    public static Holder<SoundEffect> bukkitToMinecraftHolder(Sound bukkit) {
        Preconditions.checkArgument(bukkit != null);

        IRegistry<SoundEffect> registry = CraftRegistry.getMinecraftRegistry(Registries.SOUND_EVENT);

        if (registry.wrapAsHolder(bukkitToMinecraft(bukkit)) instanceof Holder.c<SoundEffect> holder) {
            return holder;
        }

        throw new IllegalArgumentException("No Reference holder found for " + bukkit
                + ", this can happen if a plugin creates its own sound effect with out properly registering it.");
    }

    private final NamespacedKey key;
    private final SoundEffect soundEffect;
    private final String name;
    private final int ordinal;

    public CraftSound(NamespacedKey key, SoundEffect soundEffect) {
        this.key = key;
        this.soundEffect = soundEffect;
        // For backwards compatibility, minecraft values will stile return the uppercase name without the namespace,
        // in case plugins use for example the name as key in a config file to receive sound specific values.
        // Custom sounds will return the key with namespace. For a plugin this should look than like a new sound
        // (which can always be added in new minecraft versions and the plugin should therefore handle it accordingly).
        if (NamespacedKey.MINECRAFT.equals(key.getNamespace())) {
            this.name = key.getKey().toUpperCase().replace(".", "_");
        } else {
            this.name = key.toString();
        }
        this.ordinal = count++;
    }

    public SoundEffect getHandle() {
        return soundEffect;
    }

    @Override
    public NamespacedKey getKey() {
        return key;
    }

    @Override
    public int compareTo(Sound sound) {
        return ordinal - sound.ordinal();
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

        if (!(other instanceof CraftSound)) {
            return false;
        }

        return getKey().equals(((Sound) other).getKey());
    }

    @Override
    public int hashCode() {
        return getKey().hashCode();
    }
}

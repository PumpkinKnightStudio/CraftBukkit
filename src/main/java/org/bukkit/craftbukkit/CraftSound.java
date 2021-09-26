package org.bukkit.craftbukkit;

import com.google.common.base.Preconditions;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.sounds.SoundEffect;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;

public class CraftSound extends Sound {
    private static int count = 0;

    public static SoundEffect getSoundEffect(String s) {
        SoundEffect effect = IRegistry.SOUND_EVENT.get(new MinecraftKey(s));
        Preconditions.checkArgument(effect != null, "Sound effect %s does not exist", s);

        return effect;
    }

    public static SoundEffect getSoundEffect(Sound s) {
        SoundEffect effect = IRegistry.SOUND_EVENT.get(CraftNamespacedKey.toMinecraft(s.getKey()));
        Preconditions.checkArgument(effect != null, "Sound effect %s does not exist", s);

        return effect;
    }

    public static Sound getBukkit(SoundEffect soundEffect) {
        return Registry.SOUNDS.get(CraftNamespacedKey.fromMinecraft(IRegistry.SOUND_EVENT.getKey(soundEffect)));
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

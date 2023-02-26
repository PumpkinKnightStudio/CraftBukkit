package org.bukkit.craftbukkit;

import java.util.HashMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.SoundEffectType;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundGroup;

public class CraftSoundGroup implements SoundGroup {

    private final net.minecraft.world.level.block.SoundEffectType handle;
    private static final HashMap<SoundEffectType, CraftSoundGroup> SOUND_GROUPS = new HashMap<>();

    public static SoundGroup getSoundGroup(SoundEffectType soundEffectType) {
        return SOUND_GROUPS.computeIfAbsent(soundEffectType, CraftSoundGroup::new);
    }

    private CraftSoundGroup(net.minecraft.world.level.block.SoundEffectType soundEffectType) {
        this.handle = soundEffectType;
    }

    public net.minecraft.world.level.block.SoundEffectType getHandle() {
        return handle;
    }

    @Override
    public float getVolume() {
        return getHandle().getVolume();
    }

    @Override
    public float getPitch() {
        return getHandle().getPitch();
    }

    @Override
    public Sound getBreakSound() {
        return CraftSound.getBukkit(((CraftServer) Bukkit.getServer()).getServer().registryAccess().registryOrThrow(Registries.SOUND_EVENT), getHandle().breakSound);
    }

    @Override
    public Sound getStepSound() {
        return CraftSound.getBukkit(((CraftServer) Bukkit.getServer()).getServer().registryAccess().registryOrThrow(Registries.SOUND_EVENT), getHandle().getStepSound());
    }

    @Override
    public Sound getPlaceSound() {
        return CraftSound.getBukkit(((CraftServer) Bukkit.getServer()).getServer().registryAccess().registryOrThrow(Registries.SOUND_EVENT), getHandle().getPlaceSound());
    }

    @Override
    public Sound getHitSound() {
        return CraftSound.getBukkit(((CraftServer) Bukkit.getServer()).getServer().registryAccess().registryOrThrow(Registries.SOUND_EVENT), getHandle().hitSound);
    }

    @Override
    public Sound getFallSound() {
        return CraftSound.getBukkit(((CraftServer) Bukkit.getServer()).getServer().registryAccess().registryOrThrow(Registries.SOUND_EVENT), getHandle().getFallSound());
    }
}

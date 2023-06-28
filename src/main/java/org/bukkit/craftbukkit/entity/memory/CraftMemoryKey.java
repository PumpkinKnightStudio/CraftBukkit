package org.bukkit.craftbukkit.entity.memory;

import net.minecraft.core.IRegistry;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.memory.MemoryKey;

public final class CraftMemoryKey {

    private CraftMemoryKey() {}

    public static <T, U> MemoryModuleType<U> fromMemoryKey(IRegistry<MemoryModuleType<?>> registry, MemoryKey<T> memoryKey) {
        return (MemoryModuleType<U>) registry.get(CraftNamespacedKey.toMinecraft(memoryKey.getKey()));
    }

    public static <T, U> MemoryKey<U> toMemoryKey(IRegistry<MemoryModuleType<?>> registry, MemoryModuleType<T> memoryModuleType) {
        return (MemoryKey<U>) MemoryKey.getByKey(CraftNamespacedKey.fromMinecraft(registry.getKey(memoryModuleType)));
    }
}

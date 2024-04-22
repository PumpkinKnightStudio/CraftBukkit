package org.bukkit.craftbukkit.tag;

import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.bukkit.Keyed;
import org.bukkit.Tag;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;

@FunctionalInterface
public interface TagMapper<B extends Keyed, M> {

    public Tag<B> map(IRegistry<M> minecraftRegistry, TagKey<M> tagKey);

    public static <B extends Keyed, M> TagMapper<B, M> standard(CraftRegistry<B, M> bukkitRegistry) {
        return (minecraftRegistry, tagKey) -> new CraftSimpleTag<>(minecraftRegistry, tagKey,
            bukkit -> toMinecraftHolder(minecraftRegistry, bukkit),
            nms -> toBukkit(minecraftRegistry, bukkitRegistry, nms)
        );
    }

    public static <B extends Keyed, M> TagMapper<B, M> none() {
        return (ignore, ignore2) -> null;
    }

    private static <B extends Keyed, M> B toBukkit(IRegistry<M> registry, CraftRegistry<B, M> bukkitRegistry, M nms) {
        return bukkitRegistry.get(CraftNamespacedKey.fromMinecraft(registry.getKey(nms)));
    }

    private static <B extends Keyed, M> Holder<M> toMinecraftHolder(IRegistry<M> minecraftRegistry, B bukkit) {
        return minecraftRegistry.getHolderOrThrow(ResourceKey.create(minecraftRegistry.key(), CraftNamespacedKey.toMinecraft(bukkit.getKey())));
    }

}

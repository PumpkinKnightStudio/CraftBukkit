package org.bukkit.craftbukkit.tag;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.tags.TagKey;
import org.bukkit.Keyed;
import org.jetbrains.annotations.NotNull;

public class CraftSimpleTag<N, B extends Keyed> extends CraftTag<N, B> {

    private final Function<B, Holder<N>> bukkitToHolder;
    private final Function<N, B> minecraftToBukkit;

    public CraftSimpleTag(IRegistry<N> registry, TagKey<N> tag, Function<B, Holder<N>> bukkitToHolder, Function<N, B> minecraftToBukkit) {
        super(registry, tag);

        this.bukkitToHolder = bukkitToHolder;
        this.minecraftToBukkit = minecraftToBukkit;
    }

    @Override
    public boolean isTagged(B item) {
        return bukkitToHolder.apply(item).is(tag);
    }

    @Override
    @NotNull
    public Set<B> getValues() {
        return getHandle().stream().map(holder -> minecraftToBukkit.apply(holder.value())).collect(Collectors.toUnmodifiableSet());
    }

}

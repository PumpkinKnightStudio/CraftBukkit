package org.bukkit.craftbukkit;

import org.bukkit.Keyed;
import org.bukkit.Registry;

// Exists as a way to invalidate a Registry implementation
public interface CraftRegistryInternal<B extends Keyed> extends Registry<B> {

    public void invalidate();

}

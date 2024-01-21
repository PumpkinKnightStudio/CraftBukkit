package org.bukkit.craftbukkit.scoreboard.format;

import net.minecraft.network.chat.numbers.NumberFormatType;
import org.bukkit.NamespacedKey;

public final class CraftNumberFormatType implements org.bukkit.scoreboard.format.NumberFormatType {

    private final NamespacedKey key;
    private final NumberFormatType<?> handle;

    public CraftNumberFormatType(NamespacedKey key, NumberFormatType<?> handle) {
        this.key = key;
        this.handle = handle;
    }

    @Override
    public NamespacedKey getKey() {
        return key;
    }

    public NumberFormatType<?> getHandle() {
        return handle;
    }

}

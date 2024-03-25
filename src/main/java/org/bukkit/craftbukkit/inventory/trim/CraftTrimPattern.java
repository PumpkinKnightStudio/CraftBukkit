package org.bukkit.craftbukkit.inventory.trim;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.Item;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.inventory.CraftItemType;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.craftbukkit.util.Handleable;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;

public class CraftTrimPattern implements TrimPattern, Handleable<net.minecraft.world.item.armortrim.TrimPattern> {

    public static TrimPattern minecraftToBukkit(net.minecraft.world.item.armortrim.TrimPattern minecraft) {
        return CraftRegistry.minecraftToBukkit(minecraft, Registries.TRIM_PATTERN, Registry.TRIM_PATTERN);
    }

    public static net.minecraft.world.item.armortrim.TrimPattern bukkitToMinecraft(TrimPattern bukkit) {
        return CraftRegistry.bukkitToMinecraft(bukkit);
    }

    private final NamespacedKey key;
    private final net.minecraft.world.item.armortrim.TrimPattern handle;

    public CraftTrimPattern(NamespacedKey key, net.minecraft.world.item.armortrim.TrimPattern handle) {
        this.key = key;
        this.handle = handle;
    }

    @Override
    public net.minecraft.world.item.armortrim.TrimPattern getHandle() {
        return handle;
    }

    @Override
    @NotNull
    public NamespacedKey getKey() {
        return key;
    }

    @NotNull
    @Override
    public String getTranslationKey() {
        return ((TranslatableContents) handle.description().getContents()).getKey();
    }

    @Override
    public Material getItemType() {
        Item item = this.handle.templateItem().value();
        return CraftItemType.minecraftToBukkit(item);
    }
}

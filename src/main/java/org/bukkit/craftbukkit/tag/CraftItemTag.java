package org.bukkit.craftbukkit.tag;

import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.bukkit.craftbukkit.inventory.CraftItemType;
import org.bukkit.inventory.ItemType;

public class CraftItemTag extends CraftTag<Item, ItemType> {

    public CraftItemTag(IRegistry<Item> registry, TagKey<Item> tag) {
        super(registry, tag);
    }

    @Override
    public boolean isTagged(ItemType item) {
        return CraftItemType.bukkitToMinecraft(item).builtInRegistryHolder().is(tag);
    }

    @Override
    public Set<ItemType> getValues() {
        return getHandle().stream().map(Holder::value).map(CraftItemType::minecraftToBukkit).collect(Collectors.toUnmodifiableSet());
    }
}

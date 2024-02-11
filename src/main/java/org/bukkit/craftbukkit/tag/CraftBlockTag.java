package org.bukkit.craftbukkit.tag;

import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.bukkit.block.BlockType;
import org.bukkit.craftbukkit.block.CraftBlockType;

public class CraftBlockTag extends CraftTag<Block, BlockType<?>> {

    public CraftBlockTag(IRegistry<Block> registry, TagKey<Block> tag) {
        super(registry, tag);
    }

    @Override
    public boolean isTagged(BlockType<?> item) {
        return CraftBlockType.bukkitToMinecraft(item).builtInRegistryHolder().is(tag);
    }

    @Override
    public Set<BlockType<?>> getValues() {
        return getHandle().stream().map(Holder::value).map(CraftBlockType::minecraftToBukkit).collect(Collectors.toUnmodifiableSet());
    }
}

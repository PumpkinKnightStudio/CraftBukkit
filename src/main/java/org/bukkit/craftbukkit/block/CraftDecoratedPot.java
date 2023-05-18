package org.bukkit.craftbukkit.block;

import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import org.bukkit.World;
import org.bukkit.block.DecoratedPot;
import org.bukkit.craftbukkit.inventory.CraftItemType;
import org.bukkit.inventory.ItemType;

public class CraftDecoratedPot extends CraftBlockEntityState<DecoratedPotBlockEntity> implements DecoratedPot {

    public CraftDecoratedPot(World world, DecoratedPotBlockEntity tileEntity) {
        super(world, tileEntity);
    }

    @Override
    public List<ItemType> getShards() {
        return getSnapshot().getShards().stream().map(CraftItemType::minecraftToBukkit).collect(Collectors.toUnmodifiableList());
    }

    @Override
    public void addShard(ItemType itemType) {
        getSnapshot().getShards().add(((CraftItemType) itemType).getHandle());
    }

    @Override
    public void setShards(List<ItemType> shard) {
        getSnapshot().getShards().clear();

        for (ItemType itemType : shard) {
            addShard(itemType);
        }
    }
}

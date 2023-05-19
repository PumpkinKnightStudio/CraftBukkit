package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.world.entity.monster.piglin.EntityPiglin;
import net.minecraft.world.item.Item;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.inventory.CraftItemType;
import org.bukkit.entity.Piglin;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemType;

public class CraftPiglin extends CraftPiglinAbstract implements Piglin {

    public CraftPiglin(CraftServer server, EntityPiglin entity) {
        super(server, entity);
    }

    @Override
    public boolean isAbleToHunt() {
        return getHandle().cannotHunt;
    }

    @Override
    public void setIsAbleToHunt(boolean flag) {
        getHandle().cannotHunt = flag;
    }

    @Override
    public boolean addBarterItem(ItemType itemType) {
        Preconditions.checkArgument(itemType != null, "itemType cannot be null");

        Item item = ((CraftItemType) itemType).getHandle();
        return getHandle().allowedBarterItems.add(item);
    }

    @Override
    public boolean removeBarterItem(ItemType itemType) {
        Preconditions.checkArgument(itemType != null, "itemType cannot be null");

        Item item = ((CraftItemType) itemType).getHandle();
        return getHandle().allowedBarterItems.remove(item);
    }

    @Override
    public boolean addItemOfInterest(ItemType itemType) {
        Preconditions.checkArgument(itemType != null, "itemType cannot be null");

        Item item = ((CraftItemType) itemType).getHandle();
        return getHandle().interestItems.add(item);
    }

    @Override
    public boolean removeItemOfInterest(ItemType itemType) {
        Preconditions.checkArgument(itemType != null, "itemType cannot be null");

        Item item = ((CraftItemType) itemType).getHandle();
        return getHandle().interestItems.remove(item);
    }

    @Override
    public Set<ItemType> getInterestList() {
        return Collections.unmodifiableSet(getHandle().interestItems.stream().map(CraftItemType::minecraftToBukkit).collect(Collectors.toSet()));
    }

    @Override
    public Set<ItemType> getBarterList() {
        return Collections.unmodifiableSet(getHandle().allowedBarterItems.stream().map(CraftItemType::minecraftToBukkit).collect(Collectors.toSet()));
    }

    @Override
    public Inventory getInventory() {
        return new CraftInventory(getHandle().inventory);
    }

    @Override
    public EntityPiglin getHandle() {
        return (EntityPiglin) super.getHandle();
    }

    @Override
    public String toString() {
        return "CraftPiglin";
    }
}

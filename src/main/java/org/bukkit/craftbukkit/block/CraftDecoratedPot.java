package org.bukkit.craftbukkit.block;

import com.google.common.base.Preconditions;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.DecoratedPot;
import org.bukkit.craftbukkit.inventory.CraftItemType;
import org.bukkit.inventory.ItemType;

public class CraftDecoratedPot extends CraftBlockEntityState<DecoratedPotBlockEntity> implements DecoratedPot {

    public CraftDecoratedPot(World world, DecoratedPotBlockEntity tileEntity) {
        super(world, tileEntity);
    }

    @Override
    public void setSherd(Side face, ItemType sherd) {
        Preconditions.checkArgument(face != null, "face must not be null");
        Preconditions.checkArgument(sherd == null || sherd == ItemType.BRICK || Tag.ITEMS_DECORATED_POT_SHERDS.isTagged(sherd), "sherd is not a valid sherd item type: %s", sherd);

        Item sherdItem = (sherd != null) ? CraftItemType.bukkitToMinecraft(sherd) : Items.BRICK;
        DecoratedPotBlockEntity.a decorations = getSnapshot().getDecorations(); // PAIL rename Decorations

        switch (face) {
            case BACK -> getSnapshot().decorations = new DecoratedPotBlockEntity.a(sherdItem, decorations.left(), decorations.right(), decorations.front());
            case LEFT -> getSnapshot().decorations = new DecoratedPotBlockEntity.a(decorations.back(), sherdItem, decorations.right(), decorations.front());
            case RIGHT -> getSnapshot().decorations = new DecoratedPotBlockEntity.a(decorations.back(), decorations.left(), sherdItem, decorations.front());
            case FRONT -> getSnapshot().decorations = new DecoratedPotBlockEntity.a(decorations.back(), decorations.left(), decorations.right(), sherdItem);
            default -> throw new IllegalArgumentException("Unexpected value: " + face);
        }
    }

    @Override
    public ItemType getSherd(Side face) {
        Preconditions.checkArgument(face != null, "face must not be null");

        DecoratedPotBlockEntity.a decorations = getSnapshot().getDecorations(); // PAIL rename Decorations
        Item sherdItem = switch (face) {
            case BACK -> decorations.back();
            case LEFT -> decorations.left();
            case RIGHT -> decorations.right();
            case FRONT -> decorations.front();
            default -> throw new IllegalArgumentException("Unexpected value: " + face);
        };

        return CraftItemType.minecraftToBukkit(sherdItem);
    }

    @Override
    public Map<Side, ItemType> getSherds() {
        DecoratedPotBlockEntity.a decorations = getSnapshot().getDecorations(); // PAIL rename Decorations

        Map<Side, ItemType> sherds = new EnumMap<>(Side.class);
        sherds.put(Side.BACK, CraftItemType.minecraftToBukkit(decorations.back()));
        sherds.put(Side.LEFT, CraftItemType.minecraftToBukkit(decorations.left()));
        sherds.put(Side.RIGHT, CraftItemType.minecraftToBukkit(decorations.right()));
        sherds.put(Side.FRONT, CraftItemType.minecraftToBukkit(decorations.front()));
        return sherds;
    }

    @Override
    public List<ItemType> getShards() {
        return getSnapshot().getDecorations().sorted().map(CraftItemType::minecraftToBukkit).collect(Collectors.toUnmodifiableList());
    }
}

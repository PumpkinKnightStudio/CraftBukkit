package org.bukkit.craftbukkit.inventory.view;

import net.minecraft.world.inventory.ContainerBeacon;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.bukkit.craftbukkit.potion.CraftPotionEffectType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.view.BeaconView;
import org.bukkit.potion.PotionEffectType;

public class CraftBeaconView extends CraftInventoryView<ContainerBeacon> implements BeaconView {

    public CraftBeaconView(HumanEntity player, Inventory viewing, ContainerBeacon container) {
        super(player, viewing, container);
    }

    @Override
    public int getLevel() {
        return container.getLevels();
    }

    @Override
    public PotionEffectType getPrimaryEffect() {
        return container.getPrimaryEffect() != null ? CraftPotionEffectType.minecraftToBukkit(container.getPrimaryEffect()) : null;
    }

    @Override
    public PotionEffectType getSecondaryEffect() {
        return container.getSecondaryEffect() != null ? CraftPotionEffectType.minecraftToBukkit(container.getSecondaryEffect()) : null;
    }
}

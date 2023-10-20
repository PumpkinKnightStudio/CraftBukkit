package org.bukkit.craftbukkit.inventory.view;

import net.minecraft.world.inventory.ContainerMerchant;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.view.MerchantView;

public class CraftMerchantView extends CraftInventoryView<ContainerMerchant> implements MerchantView {


    public CraftMerchantView(HumanEntity player, Inventory viewing, ContainerMerchant container) {
        super(player, viewing, container);
    }

    @Override
    public int getTraderXp() {
        return container.getTraderXp();
    }

    @Override
    public int getTraderLevel() {
        return container.getTraderLevel();
    }

    @Override
    public boolean canRestock() {
        return container.canRestock();
    }

    @Override
    public boolean showProgressBar() {
        return container.showProgressBar();
    }

    @Override
    public void setTraderXp(int i) {
        container.setXp(i);
    }

    @Override
    public void setTraderLevel(int i) {
        container.setMerchantLevel(i);
    }

    @Override
    public void setCanRestock(boolean b) {
        container.setCanRestock(b);
    }

    @Override
    public void setShowProgressBar(boolean b) {
        container.setShowProgressBar(b);
    }
}

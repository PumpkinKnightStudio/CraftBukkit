package org.bukkit.craftbukkit.inventory.view;

import com.google.common.base.Preconditions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.ContainerEnchantTable;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.view.EnchantingView;
import org.jetbrains.annotations.NotNull;

public class CraftEnchantingView extends CraftInventoryView<ContainerEnchantTable> implements EnchantingView {

    private EnchantmentOffer[] cache;

    public CraftEnchantingView(HumanEntity player, Inventory viewing, ContainerEnchantTable container) {
        super(player, viewing, container);
    }

    @Override
    public int getEnchantmentSeed() {
        return container.getEnchantmentSeed();
    }

    @NotNull
    @Override
    public EnchantmentOffer[] getOffers() {
        if (cache != null) {
            return cache;
        }
        loadOffers();
        return cache.clone();
    }

    @Override
    public void setOffers(@NotNull EnchantmentOffer[] offers) {
        Preconditions.checkArgument(offers.length <= 3, "There must be no more than 3 offers.");
        if (cache == null) {
            loadOffers();
        }
        for (int i = 0; i < offers.length; i++) {
            final EnchantmentOffer offer = offers[i];
            container.costs[i] = offer.getCost();
            container.enchantClue[i] = BuiltInRegistries.ENCHANTMENT.getId(BuiltInRegistries.ENCHANTMENT.get(CraftNamespacedKey.toMinecraft(offer.getEnchantment().getKey())));
            container.levelClue[i] = offer.getEnchantmentLevel();
        }
        System.arraycopy(offers, 0, cache, 0, offers.length);
    }

    private void loadOffers() {
        final EnchantmentOffer[] offers = new EnchantmentOffer[3];
        for (int i = 0; i < offers.length; i++) {
            Enchantment enchantment = (container.enchantClue[i] >= 0) ? org.bukkit.enchantments.Enchantment.getByKey(CraftNamespacedKey.fromMinecraft(BuiltInRegistries.ENCHANTMENT.getKey(BuiltInRegistries.ENCHANTMENT.byId(container.enchantClue[i])))) : null;
            offers[i] = (enchantment != null) ? new EnchantmentOffer(enchantment, container.levelClue[i], container.costs[i]) : null;
        }
        this.cache = offers;
    }

}

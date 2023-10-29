package org.bukkit.craftbukkit.inventory.view;

import net.minecraft.core.Holder;
import net.minecraft.world.inventory.ContainerLoom;
import net.minecraft.world.level.block.entity.EnumBannerPatternType;
import org.bukkit.block.banner.PatternType;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.view.LoomView;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CraftLoomView extends CraftInventoryView<ContainerLoom> implements LoomView {

    public CraftLoomView(final HumanEntity player, final Inventory viewing, final ContainerLoom container) {
        super(player, viewing, container);
    }

    @NotNull
    @Override
    public List<PatternType> getSelectablePatterns() {
        final List<PatternType> types = new ArrayList<>();
        for (final Holder<EnumBannerPatternType> selectablePattern : container.getSelectablePatterns()) {
            final PatternType bukkit = PatternType.getByIdentifier(selectablePattern.value().getHashname());
            types.add(bukkit);
        }
        return types;
    }

    @Override
    public int getSelectedBannerPatternIndex() {
        return container.getSelectedBannerPatternIndex();
    }
}

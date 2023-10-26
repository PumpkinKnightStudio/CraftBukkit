package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Preconditions;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.Containers;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityBlastFurnace;
import net.minecraft.world.level.block.entity.TileEntityContainer;
import net.minecraft.world.level.block.entity.TileEntityDispenser;
import net.minecraft.world.level.block.entity.TileEntityFurnaceFurnace;
import net.minecraft.world.level.block.entity.TileEntitySmoker;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.inventory.util.CraftContainerCreator;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

import java.awt.Menu;

public class CraftMenuType<T extends InventoryView> implements MenuType<T> {

    private final NamespacedKey key;
    private final Containers<?> handle;

    public CraftMenuType(NamespacedKey key, final Containers<?> handle) {
        this.key = key;
        this.handle = handle;
    }

    @NotNull
    @Override
    public T create(@NotNull HumanEntity humanEntity, @NotNull String title) {
        Preconditions.checkArgument(humanEntity instanceof CraftHumanEntity, "This human entity must be a CraftHumanEntity");
        Preconditions.checkArgument(((CraftHumanEntity) humanEntity).getHandle() instanceof EntityPlayer);
        final EntityPlayer player = (EntityPlayer) ((CraftHumanEntity) humanEntity).getHandle();
        final Container container = CraftContainerCreator.INSTANCE.create(this, player.nextContainerCounter(), player.getInventory(), title);
        container.checkReachable = false;
        return (T) container.getBukkitView();
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return key;
    }

    public Containers<?> getHandle() {
        return handle;
    }

    public static MenuType<?> getMenuType(Inventory inventory) {
        final CraftInventory craft = (CraftInventory) inventory;
        if (craft instanceof CraftInventoryAnvil) {
            return MenuType.ANVIL;
        } else if (craft instanceof CraftInventoryBeacon) {
            return MenuType.BEACON;
        } else if (craft instanceof CraftInventoryFurnace) {
            final TileEntityContainer tile = (TileEntityContainer) craft.getInventory();
            if (tile instanceof TileEntityFurnaceFurnace) {
                return MenuType.FURNACE;
            } else if (tile instanceof TileEntityBlastFurnace) {
                return MenuType.BLAST_FURNACE;
            } else if (tile instanceof TileEntitySmoker) {
                return MenuType.SMOKER;
            }
        } else if (craft instanceof CraftInventoryBrewer) {
            return MenuType.BREWING_STAND;
        } else if (craft instanceof CraftInventoryCrafting) {
            return MenuType.CRAFTING;
        } else if (craft instanceof CraftInventoryEnchanting) {
            return MenuType.ENCHANTMENT;
        } else if (craft instanceof CraftInventoryGrindstone) {
            return MenuType.GRINDSTONE;
        }

        if (craft.getInventory() instanceof TileEntityDispenser) {
            return MenuType.GENERIC_3x3;
        }
        switch (inventory.getSize()) {
            case 5 -> {
                return MenuType.HOPPER;
            }
            case 9 -> {
                return MenuType.GENERIC_9x1;
            }
            case 18 -> {
                return MenuType.GENERIC_9x2;
            }
            case 27 -> {
                return MenuType.GENERIC_9x3;
            }
            case 36 -> {
                return MenuType.GENERIC_9x4;
            }
            case 45 -> {
                return MenuType.GENERIC_9x5;
            }
            case 54 -> {
                return MenuType.GENERIC_9x6;
            }
            default -> {
                throw new IllegalArgumentException("Unable to open a Craft Inventory with this size");
            }
        }
    }

    public static MenuType<?> minecraftToBukkit(Containers<?> container) {
        Preconditions.checkNotNull(container);
        IRegistry<Containers<?>> registry = CraftRegistry.getMinecraftRegistry().registryOrThrow(Registries.MENU);
        MenuType<?> bukkit = Registry.MENU.get(CraftNamespacedKey.fromMinecraft(registry.getKey(container)));
        Preconditions.checkNotNull(bukkit);
        return bukkit;
    }

    public static Containers<?> bukkitToMinecraft(MenuType<?> bukkit) {
        Preconditions.checkNotNull(bukkit);
        return ((CraftMenuType<?>) bukkit).handle;
    }

}

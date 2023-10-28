package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Preconditions;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.Containers;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.inventory.util.CraftContainerBuilder;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        final Container container = CraftContainerBuilder.INSTANCE.create(this, player.nextContainerCounter(), player.getInventory(), title);
        container.checkReachable = false;
        return (T) container.getBukkitView();
    }

    @NotNull
    @Override
    public T create(@NotNull final HumanEntity humanEntity, @NotNull final Location location, @NotNull final String title) {
        Preconditions.checkArgument(humanEntity instanceof CraftHumanEntity, "This human entity must be a CraftHumanEntity");
        Preconditions.checkArgument(((CraftHumanEntity) humanEntity).getHandle() instanceof EntityPlayer);
        final EntityPlayer player = (EntityPlayer) ((CraftHumanEntity) humanEntity).getHandle();
        final Container container = CraftContainerBuilder.INSTANCE.create(this, player.nextContainerCounter(), player.getInventory(), CraftLocation.toBlockPosition(location), title);
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

    @Nullable
    public static String getDefaultTitle(MenuType<?> type) {
        String translationKey = null;
        if (type == MenuType.GENERIC_9x1 || type == MenuType.GENERIC_9x2 || type == MenuType.GENERIC_9x3) {
            translationKey = "container.chest";
        } else if (type == MenuType.GENERIC_9x4 || type == MenuType.GENERIC_9x5 || type == MenuType.GENERIC_9x6) {
            translationKey = "container.chestDouble";
        } else if (type == MenuType.GENERIC_3x3) {
            translationKey = "container.dispenser";
        } else if (type == MenuType.ANVIL) {
            translationKey = "container.repair";
        } else if (type == MenuType.BEACON) {
            translationKey = "container.beacon";
        } else if (type == MenuType.BLAST_FURNACE) {
            translationKey = "container.blast_furnace";
        } else if (type == MenuType.BREWING_STAND) {
            translationKey = "container.brewing";
        } else if (type == MenuType.CRAFTING) {
            translationKey = "container.crafting";
        } else if (type == MenuType.ENCHANTMENT) {
            translationKey = "container.enchant";
        } else if (type == MenuType.FURNACE) {
            translationKey = "container.furnace";
        } else if (type == MenuType.GRINDSTONE) {
            translationKey = "container.grindstone_title";
        } else if (type == MenuType.HOPPER) {
            translationKey = "container.hopper";
        } else if (type == MenuType.LECTERN) {
            translationKey = "container.lectern";
        } else if (type == MenuType.LOOM) {
            translationKey = "container.loom";
        } else if (type == MenuType.SHULKER_BOX) {
            translationKey = "container.shulkerBox";
        } else if (type == MenuType.SMITHING) {
            translationKey = "container.upgrade";
        } else if (type == MenuType.SMOKER) {
            translationKey = "container.smoker";
        } else if (type == MenuType.CARTOGRAPHY_TABLE) {
            translationKey = "container.cartography_table";
        } else if (type == MenuType.STONECUTTER) {
            translationKey = "container.stonecutter";
        }
        return translationKey == null ? null : CraftChatMessage.fromComponent(IChatBaseComponent.translatable(translationKey));
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

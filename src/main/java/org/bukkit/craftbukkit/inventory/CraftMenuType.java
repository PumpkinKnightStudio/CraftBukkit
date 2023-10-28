package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.Containers;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.inventory.util.CraftContainerCreator;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class CraftMenuType<T extends InventoryView> implements MenuType<T> {

    private static final Map<MenuType<?>, String> titleTranslationKeys;

    static {
        Map<MenuType<?>, String> temp = new HashMap<>();
        temp.put(MenuType.GENERIC_9x1, "container.chest");
        temp.put(MenuType.GENERIC_9x2, "container.chest");
        temp.put(MenuType.GENERIC_9x3, "container.chest");
        temp.put(MenuType.GENERIC_9x4, "container.chestDouble");
        temp.put(MenuType.GENERIC_9x5, "container.chestDouble");
        temp.put(MenuType.GENERIC_9x6, "container.chestDouble");
        temp.put(MenuType.GENERIC_3x3, "container.dispenser");
        temp.put(MenuType.ANVIL, "container.repair");
        temp.put(MenuType.BEACON, "container.beacon");
        temp.put(MenuType.BLAST_FURNACE, "container.blast_furnace");
        temp.put(MenuType.BREWING_STAND, "container.brewing");
        temp.put(MenuType.CRAFTING, "container.crafting");
        temp.put(MenuType.ENCHANTMENT, "container.enchant");
        temp.put(MenuType.FURNACE, "container.furnace");
        temp.put(MenuType.GRINDSTONE, "container.grindstone_title");
        temp.put(MenuType.HOPPER, "container.hopper");
        temp.put(MenuType.LECTERN, "container.lectern");
        temp.put(MenuType.LOOM, "container.loom");
        temp.put(MenuType.SHULKER_BOX, "container.shulkerBox");
        temp.put(MenuType.SMITHING, "container.upgrade");
        temp.put(MenuType.SMOKER, "container.smoker");
        temp.put(MenuType.CARTOGRAPHY_TABLE, "container.cartography_table");
        temp.put(MenuType.STONECUTTER, "container.stonecutter");
        titleTranslationKeys = ImmutableMap.copyOf(temp);
    }

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

    @Nullable
    public static String getDefaultTitle(MenuType<?> type) {
        final String translationKey = titleTranslationKeys.get(type);
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

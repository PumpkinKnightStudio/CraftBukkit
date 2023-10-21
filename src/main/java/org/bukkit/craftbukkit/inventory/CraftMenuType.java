package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Preconditions;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.inventory.Containers;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

public class CraftMenuType<T extends InventoryView> implements MenuType<T> {

    private final NamespacedKey key;
    private Containers<?> handle;

    public CraftMenuType(NamespacedKey key, final Containers<?> handle) {
        this.key = key;
        this.handle = handle;
    }

    @NotNull
    @Override
    public T create(@NotNull HumanEntity humanEntity) {
        Preconditions.checkArgument(humanEntity instanceof CraftHumanEntity, "This human entity must be a CraftHumanEntity");
        Preconditions.checkArgument(((CraftHumanEntity) humanEntity).getHandle() instanceof EntityPlayer);
        final EntityPlayer player = (EntityPlayer) ((CraftHumanEntity) humanEntity).getHandle();
        return (T) handle.create(player.nextContainerCounter(), player.getInventory()).getBukkitView();
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return key;
    }

    public Containers<?> getHandle() {
        return handle;
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

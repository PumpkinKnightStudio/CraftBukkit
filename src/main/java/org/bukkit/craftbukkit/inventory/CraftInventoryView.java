package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Preconditions;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.network.protocol.game.PacketPlayOutOpenWindow;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.Containers;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class CraftInventoryView extends InventoryView {
    private final Container container;
    private final CraftHumanEntity player;
    private final CraftInventory viewing;
    private final BaseComponent originalTitle;
    private BaseComponent title;
    private final CraftComponents components;

    public CraftInventoryView(HumanEntity player, Inventory viewing, Container container) {
        // TODO: Should we make sure it really IS a CraftHumanEntity first? And a CraftInventory?
        this.player = (CraftHumanEntity) player;
        this.viewing = (CraftInventory) viewing;
        this.container = container;
        this.originalTitle = CraftChatMessage.toBungee(container.getTitle());
        this.title = originalTitle.duplicate();
        this.components = new CraftComponents();
    }

    @Override
    public Inventory getTopInventory() {
        return viewing;
    }

    @Override
    public Inventory getBottomInventory() {
        return player.getInventory();
    }

    @Override
    public HumanEntity getPlayer() {
        return player;
    }

    @Override
    public InventoryType getType() {
        InventoryType type = viewing.getType();
        if (type == InventoryType.CRAFTING && player.getGameMode() == GameMode.CREATIVE) {
            return InventoryType.CREATIVE;
        }
        return type;
    }

    @Override
    public void setItem(int slot, ItemStack item) {
        net.minecraft.world.item.ItemStack stack = CraftItemStack.asNMSCopy(item);
        if (slot >= 0) {
            container.getSlot(slot).set(stack);
        } else {
            player.getHandle().drop(stack, false);
        }
    }

    @Override
    public ItemStack getItem(int slot) {
        if (slot < 0) {
            return null;
        }
        return CraftItemStack.asCraftMirror(container.getSlot(slot).getItem());
    }

    @Override
    public String getTitle() {
        return BaseComponent.toLegacyText(title);
    }

    @Override
    public String getOriginalTitle() {
        return BaseComponent.toLegacyText(originalTitle);
    }

    @Override
    public void setTitle(String title) {
        this.components.setTitle(TextComponent.fromLegacy(title));
    }

    public boolean isInTop(int rawSlot) {
        return rawSlot < viewing.getSize();
    }

    private final class CraftComponents implements InventoryView.Components {

        @Override
        public BaseComponent getTitle() {
            return title.duplicate();
        }

        @Override
        public BaseComponent getOriginalTitle() {
            return originalTitle.duplicate();
        }

        @Override
        public void setTitle(BaseComponent title) {
            sendInventoryTitleChange(CraftInventoryView.this, title);
            CraftInventoryView.this.title = title.duplicate();
        }
    }

    @Override
    public Components components() {
        return components;
    }

    public Container getHandle() {
        return container;
    }

    public static void sendInventoryTitleChange(InventoryView view, BaseComponent title) {
        Preconditions.checkArgument(view != null, "InventoryView cannot be null");
        Preconditions.checkArgument(title != null, "Title cannot be null");
        Preconditions.checkArgument(view.getPlayer() instanceof Player, "NPCs are not currently supported for this function");
        Preconditions.checkArgument(view.getTopInventory().getType().isCreatable(), "Only creatable inventories can have their title changed");

        final EntityPlayer entityPlayer = (EntityPlayer) ((CraftHumanEntity) view.getPlayer()).getHandle();
        final int containerId = entityPlayer.containerMenu.containerId;
        final Containers<?> windowType = CraftContainer.getNotchInventoryType(view.getTopInventory());
        final net.minecraft.network.chat.IChatBaseComponent nmsTitle = CraftChatMessage.fromBungee(title);
        entityPlayer.connection.send(new PacketPlayOutOpenWindow(containerId, windowType, nmsTitle));
        ((Player) view.getPlayer()).updateInventory();
    }
}

package org.bukkit.craftbukkit.inventory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.world.IInventory;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.MenuType;

public class CraftInventoryGeneral extends CraftInventory {

    public CraftInventoryGeneral(InventoryHolder holder, MenuType<?> type) {
        super(new MinecraftInventory(holder, type));
    }

    static class MinecraftInventory implements IInventory {
        private final NonNullList<ItemStack> items;
        private int maxStack = MAX_STACK;
        private final List<HumanEntity> viewers;
        private final MenuType<?> type;
        private final InventoryHolder owner;

        public MinecraftInventory(InventoryHolder owner, MenuType<?> type) {
            this.items = NonNullList.withSize(getMenuSize(type), ItemStack.EMPTY);
            this.viewers = new ArrayList<HumanEntity>();
            this.owner = owner;
            this.type = type;
        }

        @Override
        public int getContainerSize() {
            return items.size();
        }

        @Override
        public ItemStack getItem(int i) {
            return items.get(i);
        }

        @Override
        public ItemStack removeItem(int i, int j) {
            ItemStack stack = this.getItem(i);
            ItemStack result;
            if (stack == ItemStack.EMPTY) return stack;
            if (stack.getCount() <= j) {
                this.setItem(i, ItemStack.EMPTY);
                result = stack;
            } else {
                result = CraftItemStack.copyNMSStack(stack, j);
                stack.shrink(j);
            }
            this.setChanged();
            return result;
        }

        @Override
        public ItemStack removeItemNoUpdate(int i) {
            ItemStack stack = this.getItem(i);
            ItemStack result;
            if (stack == ItemStack.EMPTY) return stack;
            if (stack.getCount() <= 1) {
                this.setItem(i, null);
                result = stack;
            } else {
                result = CraftItemStack.copyNMSStack(stack, 1);
                stack.shrink(1);
            }
            return result;
        }

        @Override
        public void setItem(int i, ItemStack itemstack) {
            items.set(i, itemstack);
            if (itemstack != ItemStack.EMPTY && this.getMaxStackSize() > 0 && itemstack.getCount() > this.getMaxStackSize()) {
                itemstack.setCount(this.getMaxStackSize());
            }
        }

        @Override
        public int getMaxStackSize() {
            return maxStack;
        }

        @Override
        public void setMaxStackSize(int size) {
            maxStack = size;
        }

        @Override
        public void setChanged() {
        }

        @Override
        public boolean stillValid(EntityHuman entityhuman) {
            return true;
        }

        @Override
        public List<ItemStack> getContents() {
            return items;
        }

        @Override
        public void onOpen(CraftHumanEntity who) {
            viewers.add(who);
        }

        @Override
        public void onClose(CraftHumanEntity who) {
            viewers.remove(who);
        }

        @Override
        public List<HumanEntity> getViewers() {
            return viewers;
        }

        public MenuType<?> getType() {
            return type;
        }

        @Override
        public InventoryHolder getOwner() {
            return owner;
        }

        @Override
        public boolean canPlaceItem(int i, ItemStack itemstack) {
            return true;
        }

        @Override
        public void startOpen(EntityHuman entityHuman) {

        }

        @Override
        public void stopOpen(EntityHuman entityHuman) {

        }

        @Override
        public void clearContent() {
            items.clear();
        }

        @Override
        public Location getLocation() {
            return null;
        }

        @Override
        public boolean isEmpty() {
            Iterator iterator = this.items.iterator();

            ItemStack itemstack;

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                itemstack = (ItemStack) iterator.next();
            } while (itemstack.isEmpty());

            return false;
        }

        private static int getMenuSize(MenuType<?> type) {
            if (type == MenuType.GENERIC_9x1) {
                return 9;
            } else if (type == MenuType.GENERIC_9x2) {
                return 18;
            } else if (type == MenuType.GENERIC_9x3) {
                return 27;
            } else if (type == MenuType.GENERIC_9x4) {
                return 36;
            } else if (type == MenuType.GENERIC_9x5) {
                return 45;
            } else if (type == MenuType.GENERIC_9x6) {
                return 54;
            } else if (type == MenuType.GENERIC_3x3) {
                return 9;
            } else if (type == MenuType.HOPPER) {
                return 5;
            } else if (type == MenuType.MERCHANT) {
                return 3;
            } else {
                throw new IllegalArgumentException("Unable to obtain size of the provided MenuType " + type.getKey());
            }
        }
    }
}

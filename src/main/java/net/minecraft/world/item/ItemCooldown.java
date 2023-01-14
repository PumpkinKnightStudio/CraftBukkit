package net.minecraft.world.item;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.player.EntityHuman;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemCooldownChangeEvent;

public class ItemCooldown {

    public final Map<Item, ItemCooldown.Info> cooldowns = Maps.newHashMap();
    public int tickCount;

    public ItemCooldown() {}

    public boolean isOnCooldown(Item item) {
        return this.getCooldownPercent(item, 0.0F) > 0.0F;
    }

    public float getCooldownPercent(Item item, float f) {
        ItemCooldown.Info itemcooldown_info = (ItemCooldown.Info) this.cooldowns.get(item);

        if (itemcooldown_info != null) {
            float f1 = (float) (itemcooldown_info.endTime - itemcooldown_info.startTime);
            float f2 = (float) itemcooldown_info.endTime - ((float) this.tickCount + f);

            return MathHelper.clamp(f2 / f1, 0.0F, 1.0F);
        } else {
            return 0.0F;
        }
    }

    public void tick() {
        ++this.tickCount;
        if (!this.cooldowns.isEmpty()) {
            Iterator iterator = this.cooldowns.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry<Item, ItemCooldown.Info> entry = (Entry) iterator.next();

                if (((ItemCooldown.Info) entry.getValue()).endTime <= this.tickCount) {
                    iterator.remove();
                    this.onCooldownEnded((Item) entry.getKey());
                }
            }
        }

    }

    // CraftBukkit - Call PlayerItemCooldownChangeEvent
    public void addCooldown(Item item, int i, EntityHuman player, PlayerItemCooldownChangeEvent.Reason reason) {
        PlayerItemCooldownChangeEvent playeritemcooldownevent = new PlayerItemCooldownChangeEvent(player.getBukkitEntity(), CraftItemStack.asBukkitCopy(new ItemStack(item)), i, reason);
        player.getServer().server.getPluginManager().callEvent(playeritemcooldownevent);
        if (!playeritemcooldownevent.isCancelled()) {
            addCooldown0(item, playeritemcooldownevent.getCooldown());
        }
    }

    private void addCooldown0(Item item, int i) { // Pail: public -> private, addCooldown -> addCooldown0
        this.cooldowns.put(item, new ItemCooldown.Info(this.tickCount, this.tickCount + i));
        this.onCooldownStarted(item, i);
    }

    public void removeCooldown(Item item) {
        this.cooldowns.remove(item);
        this.onCooldownEnded(item);
    }

    protected void onCooldownStarted(Item item, int i) {}

    protected void onCooldownEnded(Item item) {}

    public static class Info {

        final int startTime;
        public final int endTime;

        Info(int i, int j) {
            this.startTime = i;
            this.endTime = j;
        }
    }
}

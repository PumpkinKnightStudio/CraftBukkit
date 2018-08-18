package org.bukkit.craftbukkit.entity;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.EntityProjectile;

import net.minecraft.server.EnumHand;
import net.minecraft.server.EnumInteractionResult;
import net.minecraft.server.InteractionResultWrapper;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;
import net.minecraft.server.StatisticList;
import net.minecraft.server.World;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.projectiles.ProjectileSource;

public abstract class CraftProjectile extends AbstractProjectile implements Projectile {
    public CraftProjectile(CraftServer server, net.minecraft.server.Entity entity) {
        super(server, entity);
    }

    public ProjectileSource getShooter() {
        return getHandle().projectileSource;
    }

    public void setShooter(ProjectileSource shooter) {
        if (shooter instanceof CraftLivingEntity) {
            getHandle().shooter = (EntityLiving) ((CraftLivingEntity) shooter).entity;
            getHandle().shooterId = ((CraftLivingEntity) shooter).getUniqueId();
        } else {
            getHandle().shooter = null;
            getHandle().shooterId = null;
        }
        getHandle().projectileSource = shooter;
    }

    @Override
    public EntityProjectile getHandle() {
        return (EntityProjectile) entity;
    }

    @Override
    public String toString() {
        return "CraftProjectile";
    }

    /**
     * This handles the basic right click for Items that can create projectiles.
     */
    public static InteractionResultWrapper<ItemStack> handleItemRightClick(Entity launched, ItemStack itemstack, World world, EntityHuman human, EnumHand hand) {
        ProjectileLaunchEvent event = CraftEventFactory.callProjectileLaunchEvent(launched, itemstack, hand);
        if (event.isCancelled()) {
            return new InteractionResultWrapper<ItemStack>(EnumInteractionResult.FAIL, itemstack);
        }
        if (world.addEntity(launched, CreatureSpawnEvent.SpawnReason.THROWN)) {
            if (!human.abilities.canInstantlyBuild && event.shouldConsumeItem()) {
                itemstack = event.getItemStack() == null || event.getItemStack().getType() == org.bukkit.Material.AIR ? ItemStack.a : CraftItemStack.asNMSCopy(event.getItemStack());
                if (!itemstack.isEmpty()) itemstack.subtract(1);
            }
            if (!itemstack.isEmpty()) human.b(StatisticList.ITEM_USED.b(itemstack.getItem()));
            return new InteractionResultWrapper<ItemStack>(EnumInteractionResult.SUCCESS, itemstack);
        } else {
            if (human instanceof EntityPlayer) {
                // update inventory in case the client thinks the projectile was launched
                ((EntityPlayer)human).getBukkitEntity().updateInventory();
            }
        }
        return new InteractionResultWrapper<ItemStack>(EnumInteractionResult.SUCCESS, itemstack);
    }
}

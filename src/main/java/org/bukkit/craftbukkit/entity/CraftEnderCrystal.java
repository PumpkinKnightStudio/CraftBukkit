package org.bukkit.craftbukkit.entity;

import net.minecraft.server.BlockPosition;
import net.minecraft.server.DamageSource;
import net.minecraft.server.EntityEnderCrystal;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.NumberConversions;

public class CraftEnderCrystal extends CraftEntity implements EnderCrystal {
    public CraftEnderCrystal(CraftServer server, EntityEnderCrystal entity) {
        super(server, entity);
    }

    @Override
    public boolean isShowingBottom() {
        return getHandle().k(); // PAIL: Rename isShowingBottom
    }

    @Override
    public void setShowingBottom(boolean showing) {
        getHandle().a(showing); // PAIL: Rename setShowingBottom
    }

    @Override
    public Location getBeamTarget() {
        BlockPosition pos = getHandle().j(); // PAIL: Rename getBeamTarget
        return pos == null ? null : new Location(getWorld(), pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public void setBeamTarget(Location location) {
        if (location == null) {
            getHandle().a((BlockPosition) null); // PAIL: Rename setBeamTarget
        } else if (location.getWorld() != getWorld()) {
            throw new IllegalArgumentException("Cannot set beam target location to different world");
        } else {
            getHandle().a(new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
        }
    }

    @Override
    public EntityEnderCrystal getHandle() {
        return (EntityEnderCrystal) entity;
    }

    @Override
    public String toString() {
        return "CraftEnderCrystal";
    }

    public EntityType getType() {
        return EntityType.ENDER_CRYSTAL;
    }

    @Override
    public void damage(double amount) {
        damage(amount, null);
    }

    @Override
    public void damage(double amount, Entity source) {
        DamageSource reason = DamageSource.GENERIC;

        if (source instanceof HumanEntity) {
            reason = DamageSource.playerAttack(((CraftHumanEntity) source).getHandle());
        } else if (source instanceof LivingEntity) {
            reason = DamageSource.mobAttack(((CraftLivingEntity) source).getHandle());
        }
        getHandle().damageEntity(reason, (float) amount);
    }

    @Override
    public double getHealth() {
        return isDead() ? 0 : 1;
    }

    @Override
    public void setHealth(double health) {
        if (health == 0) {
            getHandle().damageEntity(null, 1);
        }
    }

    @Override
    public double getMaxHealth() {
        return 1;
    }

    @Override
    public void setMaxHealth(double health) {
        // Can't change max health of EnderCrystal.
    }

    @Override
    public void resetMaxHealth() {
        // Can't change max health of EnderCrystal.
    }

    @Deprecated
    @Override
    public void _INVALID_damage(int amount) {
        damage(amount);
    }

    @Deprecated
    @Override
    public void _INVALID_damage(int amount, Entity source) {
        damage(amount, source);
    }

    @Deprecated
    @Override
    public int _INVALID_getHealth() {
        return NumberConversions.ceil(getHealth());
    }
    
    @Deprecated
    @Override
    public void _INVALID_setHealth(int health) {
        setHealth(health);
    }

    @Deprecated
    @Override
    public int _INVALID_getMaxHealth() {
        return NumberConversions.ceil(getMaxHealth());
    }
    
    @Deprecated
    @Override
    public void _INVALID_setMaxHealth(int health) {
        setMaxHealth(health);
    }
}

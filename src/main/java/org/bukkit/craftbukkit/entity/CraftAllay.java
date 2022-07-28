package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import java.util.Optional;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.allay.Allay;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class CraftAllay extends CraftCreature implements org.bukkit.entity.Allay {

    public CraftAllay(CraftServer server, Allay entity) {
        super(server, entity);
    }

    @Override
    public Allay getHandle() {
        return (Allay) entity;
    }

    @Override
    public String toString() {
        return "CraftAllay";
    }

    @Override
    public EntityType getType() {
        return EntityType.ALLAY;
    }

    @Override
    public Inventory getInventory() {
        return new CraftInventory(getHandle().getInventory());
    }

    @Override
    public boolean canDuplicate() {
        return getHandle().canDuplicate();
    }

    @Override
    public long getDuplicationCooldown() {
        return getHandle().duplicationCooldown;
    }

    @Override
    public void setDuplicationCooldown(long l) {
        getHandle().duplicationCooldown = l;
    }

    @Override
    public void resetDuplicationCooldown() {
        getHandle().resetDuplicationCooldown();
    }

    @Override
    public boolean isDancing() {
        return getHandle().isDancing();
    }

    @Override
    public void startDancing(@NotNull Location location) {
        Preconditions.checkArgument(location != null, "Location cannot be null");
        Preconditions.checkArgument(location.getBlock().getType().equals(Material.JUKEBOX), "The Block in the Location need to be a JukeBox");
        getHandle().setJukeboxPlaying(new BlockPosition(location.getX(), location.getY(), location.getZ()), true);
    }

    @Override
    public void startDancing() {
        getHandle().forceDancing = true;
        getHandle().setDancing(true);
    }

    @Override
    public void stopDancing() {
        getHandle().forceDancing = false;
        getHandle().jukeboxPos = null;
        getHandle().setJukeboxPlaying(null, false);
    }

    public org.bukkit.entity.Allay duplicateAllay() {
        return (org.bukkit.entity.Allay) Optional.ofNullable(getHandle().duplicateAllay()).map(Entity::getBukkitEntity).orElse(null);
    }
}

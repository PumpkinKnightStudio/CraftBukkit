package org.bukkit.craftbukkit.entity;

import net.minecraft.server.EntityDolphin;
import net.minecraft.server.EntityHuman;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Dolphin;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;

public class CraftDolphin extends CraftWaterMob implements Dolphin {

    public CraftDolphin(CraftServer server, EntityDolphin entity) {
        super(server, entity);
    }

    @Override
    public EntityDolphin getHandle() {
        return (EntityDolphin) super.getHandle();
    }

    @Override
    public String toString() {
        return "CraftDolphin";
    }

    @Override
    public EntityType getType() {
        return EntityType.DOLPHIN;
    }

    /**
     * Gets the human entity that the dolphin should be following
     *
     * @return The {@link HumanEntity} being followed by the dolphin
     */
    @Override
    public HumanEntity getFollowing() {
        return ((EntityDolphin) entity).following.getBukkitEntity();
    }

    /**
     * Sets the provided {@link HumanEntity} to be followed by the dolphin.
     * Note that it may unfollow if the provided entity fails internal criteria such as needing to be swimming
     *
     * @param toFollow The {@link HumanEntity} to follow
     */
    @Override
    public void setFollowing(HumanEntity toFollow) {
        ((EntityDolphin) entity).following = (EntityHuman) ((CraftEntity) toFollow).getHandle();
    }
}

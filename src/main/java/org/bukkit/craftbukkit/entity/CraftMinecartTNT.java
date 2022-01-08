package org.bukkit.craftbukkit.entity;

import net.minecraft.world.entity.vehicle.EntityMinecartTNT;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.minecart.ExplosiveMinecart;

final class CraftMinecartTNT extends CraftMinecart implements ExplosiveMinecart {
    CraftMinecartTNT(CraftServer server, EntityMinecartTNT entity) {
        super(server, entity);
    }

    @Override
    public String toString() {
        return "CraftMinecartTNT";
    }
}

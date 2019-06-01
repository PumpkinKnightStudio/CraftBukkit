package org.bukkit.craftbukkit;

import net.minecraft.server.BlockPosition;
import net.minecraft.server.DimensionManager;
import net.minecraft.server.EnumDirection;
import net.minecraft.server.PortalTravelAgent;
import net.minecraft.server.ShapeDetector;
import net.minecraft.server.Vec3D;
import net.minecraft.server.WorldServer;

import org.bukkit.Location;
import org.bukkit.TravelAgent;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class CraftTravelAgent extends PortalTravelAgent implements TravelAgent {

    public static TravelAgent DEFAULT = null;

    private int searchRadius = 128;
    private int creationRadius = 16;
    private boolean canCreatePortal = true;

    public CraftTravelAgent(WorldServer worldserver) {
        super(worldserver);
        if (DEFAULT == null && worldserver.worldProvider.isOverworld()) {
            DEFAULT = this;
        }
    }

    @Override
    public Location findOrCreate(Location target) {
        return findOrCreate(null, target);
    }

    @Override
    public Location findOrCreate(Entity entity, Location target) {
        Location found = this.findPortal(entity, target);
        if (found == null && this.getCanCreatePortal() && this.createPortal(entity, target)) {
            found = this.findPortal(entity, target);
        }
        if (found == null) {
            found = target; // fallback to original if unable to find or create
        }

        return found;
    }

    @Override
    public Location findPortal(Location location) {
        return findPortal(null, location);
    }

    @Override
    public Location findPortal(Entity entity, Location location) {
        PortalTravelAgent pta = ((CraftWorld) location.getWorld()).getHandle().getTravelAgent();
        Vector direction = location.getDirection();
        Vec3D portalOffset = entity != null ? ((CraftEntity) entity).getHandle().getPortalOffset() : new Vec3D(0, 0, 0);
        EnumDirection portalDirection = entity != null ? ((CraftEntity) entity).getHandle().getPortalDirection() : EnumDirection.fromAngle(location.getYaw());
        ShapeDetector.c portalShape = pta.findPortal( // PAIL: rename
                new BlockPosition(location.getX(), location.getY(), location.getZ()),
                new Vec3D(direction.getX(), direction.getY(), direction.getZ()),
                portalDirection, portalOffset.x, portalOffset.y, canCreatePortal, this.getSearchRadius());
        return portalShape != null ? new Location(location.getWorld(), portalShape.a.getX(), portalShape.a.getY(), portalShape.a.getZ(), location.getYaw() + (float) portalShape.c, location.getPitch()) : null;  // PAIL: rename
    }

    @Override
    public boolean createPortal(Location location) {
        return createPortal(null, location);
    }

    @Override
    public boolean createPortal(Entity entity, Location location) {
        PortalTravelAgent pta = ((CraftWorld) location.getWorld()).getHandle().getTravelAgent();
        return pta.createPortal(entity, location.getX(), location.getY(), location.getZ(), this.getCreationRadius());
    }

    @Override
    public TravelAgent setSearchRadius(int radius) {
        this.searchRadius = radius;
        return this;
    }

    @Override
    public int getSearchRadius() {
        return this.searchRadius;
    }

    @Override
    public TravelAgent setCreationRadius(int radius) {
        this.creationRadius = radius < 2 ? 0 : radius;
        return this;
    }

    @Override
    public int getCreationRadius() {
        return this.creationRadius;
    }

    @Override
    public boolean getCanCreatePortal() {
        return this.canCreatePortal;
    }

    @Override
    public void setCanCreatePortal(boolean create) {
        this.canCreatePortal = create;
    }
}

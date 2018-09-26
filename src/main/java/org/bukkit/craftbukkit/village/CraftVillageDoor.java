package org.bukkit.craftbukkit.village;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.village.Village;
import org.bukkit.village.VillageDoor;

public class CraftVillageDoor implements VillageDoor {

    private net.minecraft.server.VillageDoor handle;
    public CraftVillageDoor(net.minecraft.server.VillageDoor handle) {
        this.handle = handle;
        if (handle.world == null && handle.village != null) {
            handle.world = handle.village.a;
        }
    }

    public net.minecraft.server.VillageDoor getHandle() {
        return handle;
    }

    public World getBukkitWorld() {
        return getHandle().world == null ? (getHandle().village == null ? null : getHandle().village.a.getWorld()) : getHandle().world.getWorld();
    }

    @Override
    public Village getVillage() {
        return getHandle().village == null ? null : getHandle().village.bukkitVillage;
    }

    @Override
    public Location getDoorLocation() {
        return new Location(getBukkitWorld(), getHandle().a.getX(), getHandle().a.getY(), getHandle().a.getZ()); // PAIL rename doorBlockPosition
    }

    @Override
    public Location getInsideLocation() {
        return new Location(getBukkitWorld(), getHandle().b.getX(), getHandle().b.getY(), getHandle().b.getZ()); // PAIL rename insideBlockPosition
    }

    @Override
    public BlockFace getInsideFacing() {
        return CraftBlock.notchToBlockFace(getHandle().j()); // PAIL rename getInsideDirection
    }

    @Override
    public boolean isDetachedFromVillage() {
        return getHandle().i(); // PAIL rename isDetachedFromVillage
    }

    @Override
    public void setIsDetachedFromVillage(boolean detached) {
        getHandle().a(detached); // PAIL rename setIsDetachedFromVillage
    }
}

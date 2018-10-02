package org.bukkit.craftbukkit.village;

import com.google.common.collect.ImmutableList;
import net.minecraft.server.BlockPosition;
import net.minecraft.server.PersistentVillage;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.util.Vector;
import org.bukkit.village.Village;
import org.bukkit.village.VillageCollection;
import org.bukkit.village.VillageDoor;

import java.util.Collection;
import java.util.List;

public class CraftVillageCollection implements VillageCollection {

    private PersistentVillage handle;
    public CraftVillageCollection(PersistentVillage handle) {
        this.handle = handle;
    }

    public PersistentVillage getHandle() {
        return handle;
    }

    @Override
    public World getWorld() {
        return getHandle().world.getWorld();
    }

    @Override
    public Collection<Village> getVillages() {
        ImmutableList.Builder<Village> bukkitVillages = new ImmutableList.Builder<>();
        getHandle().getVillages().forEach((v) -> bukkitVillages.add(v.bukkitVillage));
        List<Village> result = bukkitVillages.build();
        return result.isEmpty() ? null : result;
    }

    @Override
    public Village getClosestVillage(Location location, int radius) {
        net.minecraft.server.Village nms = getHandle().getClosestVillage(new BlockPosition(location.getX(), location.getY(), location.getZ()), radius);
        return nms == null ? null : nms.bukkitVillage;
    }

    @Override
    public void addDoorsAroundLocation(Location location, Vector radius) {
        getHandle().addDoorsAround(new BlockPosition(location.getX(), location.getY(), location.getZ()), radius.getBlockX(), radius.getBlockY(), radius.getBlockZ());
        getHandle().h(); // PAIL rename addNewDoors
    }

    @Override
    public VillageDoor getDoorAtLocation(Location location) {
        net.minecraft.server.VillageDoor nms = getHandle().c(new BlockPosition(location.getX(), location.getY(), location.getZ())); // PAIL rename getDoorAtPosition
        return nms == null ? null : nms.bukkitVillageDoor;
    }

    @Override
    public boolean isValidDoor(Location location) {
        return getHandle().a(getHandle().world.getType(new BlockPosition(location.getX(), location.getY(), location.getZ()))); // PAIL rename isValidDoor
    }

    @Override
    public boolean isValidDoor(BlockData data) {
        return getHandle().a(((CraftBlockData)data).getState());
    }
}

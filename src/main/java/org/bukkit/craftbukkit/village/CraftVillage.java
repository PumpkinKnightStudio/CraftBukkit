package org.bukkit.craftbukkit.village;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.BlockPosition;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.village.Village;
import org.bukkit.village.VillageDoor;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CraftVillage implements Village {

    public net.minecraft.server.Village handle;

    public CraftVillage(net.minecraft.server.Village handle) {
        this.handle = handle;
    }

    public net.minecraft.server.Village getHandle() {
        return handle;
    }

    @Override
    public World getWorld() {
        return getHandle().a.getWorld(); // PAIL name world
    }

    @Override
    public Location getCenter() {
        return new Location(getWorld(), getHandle().d.getX(), getHandle().d.getY(), getHandle().d.getZ()); // PAIL rename center
    }

    @Override
    public int getRadius() {
        return getHandle().b(); // PAIL rename getRadius
    }

    @Override
    public int getVillagerCount() {
        return getHandle().e(); // PAIL rename villagerCount
    }

    @Override
    public int getIronGolemCount() {
        return getHandle().l; // PAIL rename ironGolemCount
    }

    @Override
    public Map<OfflinePlayer, Integer> getPlayerReputations() {
        ImmutableMap.Builder<OfflinePlayer, Integer> builder = new ImmutableMap.Builder<>();
        for (Map.Entry<String, Integer> entry : getHandle().j.entrySet()) { // PAIL rename reputationMap
            OfflinePlayer player = Bukkit.getOfflinePlayer(entry.getKey());
            if (player != null) {
                builder.put(player, entry.getValue());
            }
        }
        Map<OfflinePlayer, Integer> result = builder.build();
        return result.isEmpty() ? null : result;
    }

    @Override
    public boolean isHostile(OfflinePlayer player) {
        return getHandle().d(player.getName());
    }

    @Override
    public void setPlayerReputation(OfflinePlayer player, int reputation) {
        getHandle().a(player.getName(), reputation); // PAIL setPlayerReputation
    }

    @Override
    public void resetAllReputation(int newReputation) {
        getHandle().b(newReputation); // PAIL rename resetAllReputation
    }

    @Override
    public Collection<LivingEntity> getAggressors() {
        ImmutableList.Builder<LivingEntity> builder = new ImmutableList.Builder<>();
        for (net.minecraft.server.Village.Aggressor villageAggressor : getHandle().k) {
            if (villageAggressor.a != null && villageAggressor.a.isAlive()) { // logic retrieved from Village remove aggressors check
                builder.add((LivingEntity) villageAggressor.a.getBukkitEntity());
            }
        }
        List<LivingEntity> result = builder.build();
        return result.isEmpty() ? null : result;
    }

    @Override
    public Collection<VillageDoor> getDoors() {
        ImmutableList.Builder<VillageDoor> builder = new ImmutableList.Builder<>();
        for (net.minecraft.server.VillageDoor door : getHandle().b) {
            // Re-validate door has a valid world & village
            if (door.world == null) door.world = getHandle().a;
            if (door.village == null) door.village = getHandle();
            builder.add(door.bukkitVillageDoor);
        }
        List<VillageDoor> result = builder.build();
        return result.isEmpty() ? null : result;
    }

    @Override
    public boolean isLocationWithinVillage(Location location) {
        return getHandle().a(new BlockPosition(location.getX(), location.getY(), location.getZ())); // PAIL rename isLocationWithinVillage
    }

    @Override
    public boolean isAnnihilated() {
        return getHandle().g(); // PAIL rename isAnnihilated
    }

    @Override
    public void updateVillagerCount() {
        getHandle().k(); // PAIL rename updateVillagerCount
    }

    @Override
    public void updateIronGolemCount() {
        getHandle().j(); // PAIL rename updateIronGolemCount
    }
}
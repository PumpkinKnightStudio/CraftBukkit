package org.bukkit.craftbukkit;

import com.mojang.authlib.GameProfile;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.players.WhiteListEntry;
import net.minecraft.stats.ServerStatisticManager;
import net.minecraft.world.level.storage.WorldNBTStorage;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.Statistic;
import org.bukkit.block.BlockType;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.craftbukkit.entity.memory.CraftMemoryMapper;
import org.bukkit.craftbukkit.profile.CraftPlayerProfile;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemType;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.profile.PlayerProfile;

@SerializableAs("Player")
public class CraftOfflinePlayer implements OfflinePlayer, ConfigurationSerializable {
    private final GameProfile profile;
    private final CraftServer server;
    private final WorldNBTStorage storage;

    protected CraftOfflinePlayer(CraftServer server, GameProfile profile) {
        this.server = server;
        this.profile = profile;
        this.storage = server.console.playerDataStorage;

    }

    @Override
    public boolean isOnline() {
        return getPlayer() != null;
    }

    @Override
    public String getName() {
        Player player = getPlayer();
        if (player != null) {
            return player.getName();
        }

        // This might not match lastKnownName but if not it should be more correct
        if (profile.getName() != null) {
            return profile.getName();
        }

        NBTTagCompound data = getBukkitData();

        if (data != null) {
            if (data.contains("lastKnownName")) {
                return data.getString("lastKnownName");
            }
        }

        return null;
    }

    @Override
    public UUID getUniqueId() {
        return profile.getId();
    }

    @Override
    public PlayerProfile getPlayerProfile() {
        return new CraftPlayerProfile(profile);
    }

    public Server getServer() {
        return server;
    }

    @Override
    public boolean isOp() {
        return server.getHandle().isOp(profile);
    }

    @Override
    public void setOp(boolean value) {
        if (value == isOp()) {
            return;
        }

        if (value) {
            server.getHandle().op(profile);
        } else {
            server.getHandle().deop(profile);
        }
    }

    @Override
    public boolean isBanned() {
        if (getName() == null) {
            return false;
        }

        return server.getBanList(BanList.Type.NAME).isBanned(getName());
    }

    public void setBanned(boolean value) {
        if (getName() == null) {
            return;
        }

        if (value) {
            server.getBanList(BanList.Type.NAME).addBan(getName(), null, null, null);
        } else {
            server.getBanList(BanList.Type.NAME).pardon(getName());
        }
    }

    @Override
    public boolean isWhitelisted() {
        return server.getHandle().getWhiteList().isWhiteListed(profile);
    }

    @Override
    public void setWhitelisted(boolean value) {
        if (value) {
            server.getHandle().getWhiteList().add(new WhiteListEntry(profile));
        } else {
            server.getHandle().getWhiteList().remove(profile);
        }
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new LinkedHashMap<String, Object>();

        result.put("UUID", profile.getId().toString());

        return result;
    }

    public static OfflinePlayer deserialize(Map<String, Object> args) {
        // Backwards comparability
        if (args.get("name") != null) {
            return Bukkit.getServer().getOfflinePlayer((String) args.get("name"));
        }

        return Bukkit.getServer().getOfflinePlayer(UUID.fromString((String) args.get("UUID")));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[UUID=" + profile.getId() + "]";
    }

    @Override
    public Player getPlayer() {
        return server.getPlayer(getUniqueId());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof OfflinePlayer)) {
            return false;
        }

        OfflinePlayer other = (OfflinePlayer) obj;
        if ((this.getUniqueId() == null) || (other.getUniqueId() == null)) {
            return false;
        }

        return this.getUniqueId().equals(other.getUniqueId());
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.getUniqueId() != null ? this.getUniqueId().hashCode() : 0);
        return hash;
    }

    private NBTTagCompound getData() {
        return storage.getPlayerData(getUniqueId().toString());
    }

    private NBTTagCompound getBukkitData() {
        NBTTagCompound result = getData();

        if (result != null) {
            if (!result.contains("bukkit")) {
                result.put("bukkit", new NBTTagCompound());
            }
            result = result.getCompound("bukkit");
        }

        return result;
    }

    private File getDataFile() {
        return new File(storage.getPlayerDir(), getUniqueId() + ".dat");
    }

    @Override
    public long getFirstPlayed() {
        Player player = getPlayer();
        if (player != null) return player.getFirstPlayed();

        NBTTagCompound data = getBukkitData();

        if (data != null) {
            if (data.contains("firstPlayed")) {
                return data.getLong("firstPlayed");
            } else {
                File file = getDataFile();
                return file.lastModified();
            }
        } else {
            return 0;
        }
    }

    @Override
    public long getLastPlayed() {
        Player player = getPlayer();
        if (player != null) return player.getLastPlayed();

        NBTTagCompound data = getBukkitData();

        if (data != null) {
            if (data.contains("lastPlayed")) {
                return data.getLong("lastPlayed");
            } else {
                File file = getDataFile();
                return file.lastModified();
            }
        } else {
            return 0;
        }
    }

    @Override
    public boolean hasPlayedBefore() {
        return getData() != null;
    }

    @Override
    public Location getLastDeathLocation() {
        if (getData().contains("LastDeathLocation", 10)) {
            return GlobalPos.CODEC.parse(DynamicOpsNBT.INSTANCE, getData().get("LastDeathLocation")).result().map(CraftMemoryMapper::fromNms).orElse(null);
        }
        return null;
    }

    @Override
    public Location getBedSpawnLocation() {
        NBTTagCompound data = getData();
        if (data == null) return null;

        if (data.contains("SpawnX") && data.contains("SpawnY") && data.contains("SpawnZ")) {
            String spawnWorld = data.getString("SpawnWorld");
            if (spawnWorld.equals("")) {
                spawnWorld = server.getWorlds().get(0).getName();
            }
            return new Location(server.getWorld(spawnWorld), data.getInt("SpawnX"), data.getInt("SpawnY"), data.getInt("SpawnZ"));
        }
        return null;
    }

    public void setMetadata(String metadataKey, MetadataValue metadataValue) {
        server.getPlayerMetadata().setMetadata(this, metadataKey, metadataValue);
    }

    public List<MetadataValue> getMetadata(String metadataKey) {
        return server.getPlayerMetadata().getMetadata(this, metadataKey);
    }

    public boolean hasMetadata(String metadataKey) {
        return server.getPlayerMetadata().hasMetadata(this, metadataKey);
    }

    public void removeMetadata(String metadataKey, Plugin plugin) {
        server.getPlayerMetadata().removeMetadata(this, metadataKey, plugin);
    }

    private ServerStatisticManager getStatisticManager() {
        return server.getHandle().getPlayerStats(getUniqueId(), getName());
    }

    @Override
    public void incrementStatistic(Statistic statistic) {
        if (isOnline()) {
            getPlayer().incrementStatistic(statistic);
        } else {
            ServerStatisticManager manager = getStatisticManager();
            CraftStatistic.incrementStatistic(manager, statistic);
            manager.save();
        }
    }

    @Override
    public void decrementStatistic(Statistic statistic) {
        if (isOnline()) {
            getPlayer().decrementStatistic(statistic);
        } else {
            ServerStatisticManager manager = getStatisticManager();
            CraftStatistic.decrementStatistic(manager, statistic);
            manager.save();
        }
    }

    @Override
    public int getStatistic(Statistic statistic) {
        if (isOnline()) {
            return getPlayer().getStatistic(statistic);
        } else {
            return CraftStatistic.getStatistic(getStatisticManager(), statistic);
        }
    }

    @Override
    public void incrementStatistic(Statistic statistic, int amount) {
        if (isOnline()) {
            getPlayer().incrementStatistic(statistic, amount);
        } else {
            ServerStatisticManager manager = getStatisticManager();
            CraftStatistic.incrementStatistic(manager, statistic, amount);
            manager.save();
        }
    }

    @Override
    public void decrementStatistic(Statistic statistic, int amount) {
        if (isOnline()) {
            getPlayer().decrementStatistic(statistic, amount);
        } else {
            ServerStatisticManager manager = getStatisticManager();
            CraftStatistic.decrementStatistic(manager, statistic, amount);
            manager.save();
        }
    }

    @Override
    public void setStatistic(Statistic statistic, int newValue) {
        if (isOnline()) {
            getPlayer().setStatistic(statistic, newValue);
        } else {
            ServerStatisticManager manager = getStatisticManager();
            CraftStatistic.setStatistic(manager, statistic, newValue);
            manager.save();
        }
    }

    @Override
    public void incrementStatistic(Statistic statistic, ItemType itemType) {
        if (isOnline()) {
            getPlayer().incrementStatistic(statistic, itemType);
        } else {
            ServerStatisticManager manager = getStatisticManager();
            CraftStatistic.incrementStatistic(manager, statistic, itemType);
            manager.save();
        }
    }

    @Override
    public void decrementStatistic(Statistic statistic, ItemType itemType) {
        if (isOnline()) {
            getPlayer().decrementStatistic(statistic, itemType);
        } else {
            ServerStatisticManager manager = getStatisticManager();
            CraftStatistic.decrementStatistic(manager, statistic, itemType);
            manager.save();
        }
    }

    @Override
    public int getStatistic(Statistic statistic, ItemType itemType) {
        if (isOnline()) {
            return getPlayer().getStatistic(statistic, itemType);
        } else {
            return CraftStatistic.getStatistic(getStatisticManager(), statistic, itemType);
        }
    }

    @Override
    public void incrementStatistic(Statistic statistic, ItemType itemType, int amount) {
        if (isOnline()) {
            getPlayer().incrementStatistic(statistic, itemType, amount);
        } else {
            ServerStatisticManager manager = getStatisticManager();
            CraftStatistic.incrementStatistic(manager, statistic, itemType, amount);
            manager.save();
        }
    }

    @Override
    public void decrementStatistic(Statistic statistic, ItemType itemType, int amount) {
        if (isOnline()) {
            getPlayer().decrementStatistic(statistic, itemType, amount);
        } else {
            ServerStatisticManager manager = getStatisticManager();
            CraftStatistic.decrementStatistic(manager, statistic, itemType, amount);
            manager.save();
        }
    }

    @Override
    public void setStatistic(Statistic statistic, ItemType itemType, int newValue) {
        if (isOnline()) {
            getPlayer().setStatistic(statistic, itemType, newValue);
        } else {
            ServerStatisticManager manager = getStatisticManager();
            CraftStatistic.setStatistic(manager, statistic, itemType, newValue);
            manager.save();
        }
    }

    @Override
    public void incrementStatistic(Statistic statistic, BlockType<?> blockType) {
        if (isOnline()) {
            getPlayer().incrementStatistic(statistic, blockType);
        } else {
            ServerStatisticManager manager = getStatisticManager();
            CraftStatistic.incrementStatistic(manager, statistic, blockType);
            manager.save();
        }
    }

    @Override
    public void decrementStatistic(Statistic statistic, BlockType<?> blockType) {
        if (isOnline()) {
            getPlayer().decrementStatistic(statistic, blockType);
        } else {
            ServerStatisticManager manager = getStatisticManager();
            CraftStatistic.decrementStatistic(manager, statistic, blockType);
            manager.save();
        }
    }

    @Override
    public int getStatistic(Statistic statistic, BlockType<?> blockType) {
        if (isOnline()) {
            return getPlayer().getStatistic(statistic, blockType);
        } else {
            return CraftStatistic.getStatistic(getStatisticManager(), statistic, blockType);
        }
    }

    @Override
    public void incrementStatistic(Statistic statistic, BlockType<?> blockType, int amount) {
        if (isOnline()) {
            getPlayer().incrementStatistic(statistic, blockType, amount);
        } else {
            ServerStatisticManager manager = getStatisticManager();
            CraftStatistic.incrementStatistic(manager, statistic, blockType, amount);
            manager.save();
        }
    }

    @Override
    public void decrementStatistic(Statistic statistic, BlockType<?> blockType, int amount) {
        if (isOnline()) {
            getPlayer().decrementStatistic(statistic, blockType, amount);
        } else {
            ServerStatisticManager manager = getStatisticManager();
            CraftStatistic.decrementStatistic(manager, statistic, blockType, amount);
            manager.save();
        }
    }

    @Override
    public void setStatistic(Statistic statistic, BlockType<?> blockType, int newValue) {
        if (isOnline()) {
            getPlayer().setStatistic(statistic, blockType, newValue);
        } else {
            ServerStatisticManager manager = getStatisticManager();
            CraftStatistic.setStatistic(manager, statistic, blockType, newValue);
            manager.save();
        }
    }

    @Override
    public void incrementStatistic(Statistic statistic, EntityType<?> entityType) {
        if (isOnline()) {
            getPlayer().incrementStatistic(statistic, entityType);
        } else {
            ServerStatisticManager manager = getStatisticManager();
            CraftStatistic.incrementStatistic(CraftRegistry.getMinecraftRegistry().registryOrThrow(Registries.ENTITY_TYPE), manager, statistic, entityType);
            manager.save();
        }
    }

    @Override
    public void decrementStatistic(Statistic statistic, EntityType<?> entityType) {
        if (isOnline()) {
            getPlayer().decrementStatistic(statistic, entityType);
        } else {
            ServerStatisticManager manager = getStatisticManager();
            CraftStatistic.decrementStatistic(CraftRegistry.getMinecraftRegistry().registryOrThrow(Registries.ENTITY_TYPE), manager, statistic, entityType);
            manager.save();
        }
    }

    @Override
    public int getStatistic(Statistic statistic, EntityType<?> entityType) {
        if (isOnline()) {
            return getPlayer().getStatistic(statistic, entityType);
        } else {
            return CraftStatistic.getStatistic(CraftRegistry.getMinecraftRegistry().registryOrThrow(Registries.ENTITY_TYPE), getStatisticManager(), statistic, entityType);
        }
    }

    @Override
    public void incrementStatistic(Statistic statistic, EntityType<?> entityType, int amount) {
        if (isOnline()) {
            getPlayer().incrementStatistic(statistic, entityType, amount);
        } else {
            ServerStatisticManager manager = getStatisticManager();
            CraftStatistic.incrementStatistic(CraftRegistry.getMinecraftRegistry().registryOrThrow(Registries.ENTITY_TYPE), manager, statistic, entityType, amount);
            manager.save();
        }
    }

    @Override
    public void decrementStatistic(Statistic statistic, EntityType<?> entityType, int amount) {
        if (isOnline()) {
            getPlayer().decrementStatistic(statistic, entityType, amount);
        } else {
            ServerStatisticManager manager = getStatisticManager();
            CraftStatistic.decrementStatistic(CraftRegistry.getMinecraftRegistry().registryOrThrow(Registries.ENTITY_TYPE), manager, statistic, entityType, amount);
            manager.save();
        }
    }

    @Override
    public void setStatistic(Statistic statistic, EntityType<?> entityType, int newValue) {
        if (isOnline()) {
            getPlayer().setStatistic(statistic, entityType, newValue);
        } else {
            ServerStatisticManager manager = getStatisticManager();
            CraftStatistic.setStatistic(CraftRegistry.getMinecraftRegistry().registryOrThrow(Registries.ENTITY_TYPE), manager, statistic, entityType, newValue);
            manager.save();
        }
    }
}

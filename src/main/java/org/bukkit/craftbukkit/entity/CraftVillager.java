package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import java.util.Locale;
import java.util.UUID;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.world.entity.ai.gossip.ReputationType;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.level.block.BlockBed;
import net.minecraft.world.level.block.state.IBlockData;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.entity.Villager.Type;

public class CraftVillager extends CraftAbstractVillager implements Villager {

    public CraftVillager(CraftServer server, EntityVillager entity) {
        super(server, entity);
    }

    @Override
    public EntityVillager getHandle() {
        return (EntityVillager) entity;
    }

    @Override
    public String toString() {
        return "CraftVillager";
    }

    @Override
    public EntityType getType() {
        return EntityType.VILLAGER;
    }

    @Override
    public void remove() {
        getHandle().releaseAllPois();

        super.remove();
    }

    @Override
    public Profession getProfession() {
        return CraftVillager.nmsToBukkitProfession(getHandle().getVillagerData().getProfession());
    }

    @Override
    public void setProfession(Profession profession) {
        Validate.notNull(profession);
        getHandle().setVillagerData(getHandle().getVillagerData().setProfession(CraftVillager.bukkitToNmsProfession(profession)));
    }

    @Override
    public Type getVillagerType() {
        return Type.valueOf(IRegistry.VILLAGER_TYPE.getKey(getHandle().getVillagerData().getType()).getPath().toUpperCase(Locale.ROOT));
    }

    @Override
    public void setVillagerType(Type type) {
        Validate.notNull(type);
        getHandle().setVillagerData(getHandle().getVillagerData().setType(IRegistry.VILLAGER_TYPE.get(CraftNamespacedKey.toMinecraft(type.getKey()))));
    }

    @Override
    public int getVillagerLevel() {
        return getHandle().getVillagerData().getLevel();
    }

    @Override
    public void setVillagerLevel(int level) {
        Preconditions.checkArgument(1 <= level && level <= 5, "level must be between [1, 5]");

        getHandle().setVillagerData(getHandle().getVillagerData().setLevel(level));
    }

    @Override
    public int getVillagerExperience() {
        return getHandle().getVillagerXp();
    }

    @Override
    public void setVillagerExperience(int experience) {
        Preconditions.checkArgument(experience >= 0, "Experience must be positive");

        getHandle().setVillagerXp(experience);
    }

    @Override
    public boolean sleep(Location location) {
        Preconditions.checkArgument(location != null, "Location cannot be null");
        Preconditions.checkArgument(location.getWorld() != null, "Location needs to be in a world");
        Preconditions.checkArgument(location.getWorld().equals(getWorld()), "Cannot sleep across worlds");
        Preconditions.checkState(!getHandle().generation, "Cannot sleep during world generation");

        BlockPosition position = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        IBlockData iblockdata = getHandle().level.getBlockState(position);
        if (!(iblockdata.getBlock() instanceof BlockBed)) {
            return false;
        }

        getHandle().startSleeping(position);
        return true;
    }

    @Override
    public void wakeup() {
        Preconditions.checkState(isSleeping(), "Cannot wakeup if not sleeping");
        Preconditions.checkState(!getHandle().generation, "Cannot wakeup during world generation");

        getHandle().stopSleeping();
    }

    @Override
    public void shakeHead() {
        getHandle().setUnhappy();
    }

    @Override
    public int getReputation(OfflinePlayer player, GossipType gossipType) {
        Preconditions.checkNotNull(player, "Player must not be null.");
        return getReputation(player.getUniqueId(), gossipType);
    }

    @Override
    public int getReputation(UUID uuid, GossipType gossipType) {
        Preconditions.checkNotNull(uuid, "UUID must not be null.");
        Preconditions.checkNotNull(gossipType, "gossipType must not be null.");
        return getHandle().getGossips().getReputation(uuid, reputationType -> reputationType == bukkitToNmsReputationType(gossipType));
    }

    @Override
    public void addReputation(OfflinePlayer player, GossipType gossipType, int amount) {
        Preconditions.checkNotNull(player, "Player must not be null.");
        addReputation(player.getUniqueId(), gossipType, amount);
    }

    @Override
    public void addReputation(UUID uuid, GossipType gossipType, int amount) {
        Preconditions.checkNotNull(uuid, "UUID must not be null.");
        Preconditions.checkNotNull(gossipType, "gossipType must not be null.");
        getHandle().getGossips().add(uuid, bukkitToNmsReputationType(gossipType), amount);
    }

    @Override
    public void removeReputation(OfflinePlayer player, GossipType gossipType, int amount) {
        Preconditions.checkNotNull(player, "Player must not be null.");
        removeReputation(player.getUniqueId(), gossipType, amount);
    }

    @Override
    public void removeReputation(UUID uuid, GossipType gossipType, int amount) {
        Preconditions.checkNotNull(uuid, "UUID must not be null.");
        Preconditions.checkNotNull(gossipType, "gossipType must not be null.");
        getHandle().getGossips().remove(uuid, bukkitToNmsReputationType(gossipType), amount);
    }

    public static Profession nmsToBukkitProfession(VillagerProfession nms) {
        return Profession.valueOf(IRegistry.VILLAGER_PROFESSION.getKey(nms).getPath().toUpperCase(Locale.ROOT));
    }

    public static VillagerProfession bukkitToNmsProfession(Profession bukkit) {
        return IRegistry.VILLAGER_PROFESSION.get(CraftNamespacedKey.toMinecraft(bukkit.getKey()));
    }

    public static ReputationType bukkitToNmsReputationType(GossipType gossipType) {
        return switch (gossipType) {
            case TRADING -> ReputationType.TRADING;
            case MAJOR_NEGATIVE -> ReputationType.MAJOR_NEGATIVE;
            case MINOR_NEGATIVE -> ReputationType.MINOR_NEGATIVE;
            case MAJOR_POSITIVE -> ReputationType.MAJOR_POSITIVE;
            case MINOR_POSITIVE -> ReputationType.MINOR_POSITIVE;
        };
    }
}

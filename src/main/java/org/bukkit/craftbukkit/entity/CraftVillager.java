package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import java.util.Locale;
import net.minecraft.server.BlockBed;
import net.minecraft.server.BlockPosition;
import net.minecraft.server.EntityVillager;
import net.minecraft.server.IBlockData;
import net.minecraft.server.IRegistry;
import net.minecraft.server.ReputationEvent;
import net.minecraft.server.VillagerProfession;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

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
    public Profession getProfession() {
        return CraftVillager.nmsToBukkitProfession(getHandle().getVillagerData().getProfession());
    }

    @Override
    public void setProfession(Profession profession) {
        Validate.notNull(profession);
        getHandle().setVillagerData(getHandle().getVillagerData().withProfession(CraftVillager.bukkitToNmsProfession(profession)));
    }

    @Override
    public Type getVillagerType() {
        return Type.valueOf(IRegistry.VILLAGER_TYPE.getKey(getHandle().getVillagerData().getType()).getKey().toUpperCase(Locale.ROOT));
    }

    @Override
    public void setVillagerType(Type type) {
        Validate.notNull(type);
        getHandle().setVillagerData(getHandle().getVillagerData().withType(IRegistry.VILLAGER_TYPE.get(CraftNamespacedKey.toMinecraft(type.getKey()))));
    }

    @Override
    public int getVillagerLevel() {
        return getHandle().getVillagerData().getLevel();
    }

    @Override
    public void setVillagerLevel(int level) {
        Preconditions.checkArgument(1 <= level && level <= 5, "level must be between [1, 5]");

        getHandle().setVillagerData(getHandle().getVillagerData().withLevel(level));
    }

    @Override
    public int getVillagerExperience() {
        return getHandle().getExperience();
    }

    @Override
    public void setVillagerExperience(int experience) {
        Preconditions.checkArgument(experience >= 0, "Experience must be positive");

        getHandle().setExperience(experience);
    }

    @Override
    public int getReputation(Player player) {
        return getHandle().f(((CraftPlayer) player).getHandle()); // PAIL rename getReputation
    }

    @Override
    public void changeReputation(ReputationEventType reputationEventType, Player player) {
        // a(ReputationEvent, Entity) is also called for attacking mobs, but this doesn't seem to have any effect so
        // it's probably reasonable to limit the API to players.
        getHandle().a(bukkitToNmsReputationEvent(reputationEventType), ((CraftPlayer) player).getHandle()); // PAIL rename changeReputation
    }

    @Override
    public boolean sleep(Location location) {
        Preconditions.checkArgument(location != null, "Location cannot be null");
        Preconditions.checkArgument(location.getWorld() != null, "Location needs to be in a world");
        Preconditions.checkArgument(location.getWorld().equals(getWorld()), "Cannot sleep across worlds");

        BlockPosition position = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        IBlockData iblockdata = getHandle().world.getType(position);
        if (!(iblockdata.getBlock() instanceof BlockBed)) {
            return false;
        }

        getHandle().entitySleep(position);
        return true;
    }

    @Override
    public void wakeup() {
        Preconditions.checkState(isSleeping(), "Cannot wakeup if not sleeping");

        getHandle().entityWakeup();
    }

    public static Profession nmsToBukkitProfession(VillagerProfession nms) {
        return Profession.valueOf(IRegistry.VILLAGER_PROFESSION.getKey(nms).getKey().toUpperCase(Locale.ROOT));
    }

    public static VillagerProfession bukkitToNmsProfession(Profession bukkit) {
        return IRegistry.VILLAGER_PROFESSION.get(CraftNamespacedKey.toMinecraft(bukkit.getKey()));
    }

    public static ReputationEvent bukkitToNmsReputationEvent(ReputationEventType type) {
        // Doesn't seem to be present in nms.IRegistry
        switch (type) {
            case ZOMBIE_VILLAGER_CURED:
                return ReputationEvent.a; // PAIL rename ZOMBIE_VILLAGER_CURED
            case GOLEM_KILLED:
                return ReputationEvent.b; // PAIL rename GOLEM_KILLED
            case VILLAGER_HURT:
                return ReputationEvent.c; // PAIL rename VILLAGER_HURT
            case VILLAGER_KILLED:
                return ReputationEvent.d; // PAIL rename VILLAGER_KILLED
            case TRADE:
                return ReputationEvent.e; // PAIL rename TRADE
            default:
                throw new AssertionError();
        }
    }
}

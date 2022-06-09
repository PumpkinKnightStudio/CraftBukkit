package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import net.minecraft.world.entity.monster.warden.AngerLevel;
import net.minecraft.world.entity.monster.warden.Warden;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class CraftWarden extends CraftMonster implements org.bukkit.entity.Warden {

    public CraftWarden(CraftServer server, Warden entity) {
        super(server, entity);
    }

    @Override
    public Warden getHandle() {
        return (Warden) entity;
    }

    @Override
    public String toString() {
        return "CraftWarden";
    }

    @Override
    public EntityType getType() {
        return EntityType.WARDEN;
    }

    @Override
    public int getAnger(Entity entity) {
        Preconditions.checkArgument(entity != null, "Entity cannot be null");

        return getHandle().getAngerManagement().getActiveAnger(((CraftEntity) entity).getHandle());
    }

    @Override
    public void increaseAnger(Entity entity, int increase) {
        Preconditions.checkArgument(entity != null, "Entity cannot be null");

        getHandle().getAngerManagement().increaseAnger(((CraftEntity) entity).getHandle(), increase);
    }

    @Override
    public void setAnger(Entity entity, int anger) {
        Preconditions.checkArgument(entity != null, "Entity cannot be null");

        getHandle().clearAnger(((CraftEntity) entity).getHandle());
        getHandle().getAngerManagement().increaseAnger(((CraftEntity) entity).getHandle(), anger);
    }

    @Override
    public void clearAnger(Entity entity) {
        Preconditions.checkArgument(entity != null, "Entity cannot be null");

        getHandle().clearAnger(((CraftEntity) entity).getHandle());
    }

    @Override
    public LivingEntity getEntityAngryAt() {
        return (LivingEntity) getHandle().getEntityAngryAt().map(net.minecraft.world.entity.Entity::getBukkitEntity).orElse(null);
    }

    @Override
    public LevelAnger getLevelAnger() {
        AngerLevel angerLevel = getHandle().getAngerLevel();
        return switch (angerLevel) {
            case CALM -> LevelAnger.CALM;
            case AGITATED -> LevelAnger.AGITATED;
            case ANGRY -> LevelAnger.ANGRY;
        };
    }
}

package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.Particles;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.behavior.warden.SonicBoom;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.monster.warden.WardenAi;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
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
    public int getAnger() {
        return getHandle().getAngerManagement().getActiveAnger(getHandle().getTarget());
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
    public void setDisturbanceLocation(Location location) {
        Preconditions.checkArgument(location != null, "Location cannot be null");

        WardenAi.setDisturbanceLocation(getHandle(), new BlockPosition(location.getX(), location.getY(), location.getZ()));
    }

    @Override
    public AngerLevel getAngerLevel() {
        return switch (getHandle().getAngerLevel()) {
            case CALM -> AngerLevel.CALM;
            case AGITATED -> AngerLevel.AGITATED;
            case ANGRY -> AngerLevel.ANGRY;
        };
    }
}

package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import net.minecraft.world.entity.vehicle.EntityBoat;
import org.bukkit.TreeSpecies;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Boat;
import org.bukkit.entity.EntityType;

public class CraftBoat extends CraftVehicle implements Boat {

    public CraftBoat(CraftServer server, EntityBoat entity) {
        super(server, entity);
    }

    @Override
    public TreeSpecies getWoodType() {
        return getTreeSpecies(getHandle().getBoatType());
    }

    @Override
    public void setWoodType(TreeSpecies species) {
        getHandle().setType(getBoatType(species));
    }

    @Override
    public Type getBoatType() {
        return Type.valueOf(getHandle().getBoatType().getName().toUpperCase());
    }

    @Override
    public void setBoatType(Type type) {
        Preconditions.checkArgument(type != null, "Boat.Type cannot be null");
        getHandle().setType(EntityBoat.EnumBoatType.byName(type.toString().toLowerCase()));
    }

    @Override
    public double getMaxSpeed() {
        return getHandle().maxSpeed;
    }

    @Override
    public void setMaxSpeed(double speed) {
        if (speed >= 0D) {
            getHandle().maxSpeed = speed;
        }
    }

    @Override
    public double getOccupiedDeceleration() {
        return getHandle().occupiedDeceleration;
    }

    @Override
    public void setOccupiedDeceleration(double speed) {
        if (speed >= 0D) {
            getHandle().occupiedDeceleration = speed;
        }
    }

    @Override
    public double getUnoccupiedDeceleration() {
        return getHandle().unoccupiedDeceleration;
    }

    @Override
    public void setUnoccupiedDeceleration(double speed) {
        getHandle().unoccupiedDeceleration = speed;
    }

    @Override
    public boolean getWorkOnLand() {
        return getHandle().landBoats;
    }

    @Override
    public void setWorkOnLand(boolean workOnLand) {
        getHandle().landBoats = workOnLand;
    }

    @Override
    public Status getStatus() {
        return Status.valueOf(getHandle().status.toString());
    }

    @Override
    public EntityBoat getHandle() {
        return (EntityBoat) entity;
    }

    @Override
    public String toString() {
        return "CraftBoat";
    }

    @Override
    public EntityType getType() {
        return EntityType.BOAT;
    }

    @Deprecated
    public static TreeSpecies getTreeSpecies(EntityBoat.EnumBoatType boatType) {
        switch (boatType) {
            case SPRUCE:
                return TreeSpecies.REDWOOD;
            case BIRCH:
                return TreeSpecies.BIRCH;
            case JUNGLE:
                return TreeSpecies.JUNGLE;
            case ACACIA:
                return TreeSpecies.ACACIA;
            case DARK_OAK:
                return TreeSpecies.DARK_OAK;
            case OAK:
            default:
                return TreeSpecies.GENERIC;
        }
    }

    @Deprecated
    public static EntityBoat.EnumBoatType getBoatType(TreeSpecies species) {
        switch (species) {
            case REDWOOD:
                return EntityBoat.EnumBoatType.SPRUCE;
            case BIRCH:
                return EntityBoat.EnumBoatType.BIRCH;
            case JUNGLE:
                return EntityBoat.EnumBoatType.JUNGLE;
            case ACACIA:
                return EntityBoat.EnumBoatType.ACACIA;
            case DARK_OAK:
                return EntityBoat.EnumBoatType.DARK_OAK;
            case GENERIC:
            default:
                return EntityBoat.EnumBoatType.OAK;
        }
    }
}

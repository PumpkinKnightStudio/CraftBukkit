package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sniffer;

public class CraftSniffer extends CraftAnimals implements Sniffer {

    public CraftSniffer(CraftServer server, net.minecraft.world.entity.animal.sniffer.Sniffer entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.animal.sniffer.Sniffer getHandle() {
        return (net.minecraft.world.entity.animal.sniffer.Sniffer) super.getHandle();
    }

    @Override
    public String toString() {
        return "CraftSniffer";
    }

    @Override
    public EntityType getType() {
        return EntityType.SNIFFER;
    }

    @Override
    public Collection<Location> getExploredLocations() {
        return this.getHandle().getExploredPositions().map(blockPosition -> new Location(this.getLocation().getWorld(), blockPosition.getX(), blockPosition.getY(), blockPosition.getZ())).collect(Collectors.toList());
    }

    @Override
    public void removeExploredLocation(Location location) {
        Preconditions.checkArgument(location != null, "location cannot be null");
        Collection<Location> locationsExplored = this.getExploredLocations();
        // remove locations based in the argument but only with the comparation of block x,y,z because Sniffer only use this cross every world
        locationsExplored.removeIf(locationExplored -> locationExplored.getBlockX() == location.getBlockX() && locationExplored.getBlockY() == location.getBlockY() && locationExplored.getBlockZ() == location.getBlockZ());
        this.getHandle().getBrain().setMemory(MemoryModuleType.SNIFFER_EXPLORED_POSITIONS, locationsExplored.stream().map(locationExplored -> new BlockPosition(locationExplored.getBlockX(), locationExplored.getBlockY(), locationExplored.getBlockZ())).collect(Collectors.toList()));
    }

    @Override
    public void addExploredLocation(Location location) {
        Preconditions.checkArgument(location != null, "location cannot be null");
        this.getHandle().storeExploredPosition(new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
    }

    @Override
    public Sniffer.State getState() {
        return this.stateToBukkit(this.getHandle().getState());
    }

    @Override
    public void changeState(Sniffer.State state) {
        Preconditions.checkArgument(state != null, "state cannot be null");
        this.getHandle().transitionTo(this.stateToNMS(state));
    }

    @Override
    public Location getPossibleDigLocation() {
        return this.getHandle().calculateDigPosition().map(blockPosition -> new Location(this.getLocation().getWorld(), blockPosition.getX(), blockPosition.getY(), blockPosition.getZ())).orElse(null);
    }

    @Override
    public boolean canDig() {
        return this.getHandle().canDig();
    }

    private net.minecraft.world.entity.animal.sniffer.Sniffer.a stateToNMS(Sniffer.State state) {
        return switch (state) {
            case IDLING -> net.minecraft.world.entity.animal.sniffer.Sniffer.a.IDLING;
            case FEELING_HAPPY -> net.minecraft.world.entity.animal.sniffer.Sniffer.a.FEELING_HAPPY;
            case SCENTING -> net.minecraft.world.entity.animal.sniffer.Sniffer.a.SCENTING;
            case SNIFFING -> net.minecraft.world.entity.animal.sniffer.Sniffer.a.SNIFFING;
            case SEARCHING -> net.minecraft.world.entity.animal.sniffer.Sniffer.a.SEARCHING;
            case DIGGING -> net.minecraft.world.entity.animal.sniffer.Sniffer.a.DIGGING;
            case RISING -> net.minecraft.world.entity.animal.sniffer.Sniffer.a.RISING;
        };
    }

    private Sniffer.State stateToBukkit(net.minecraft.world.entity.animal.sniffer.Sniffer.a state) {
        return switch (state) {
            case IDLING -> Sniffer.State.IDLING;
            case FEELING_HAPPY -> Sniffer.State.FEELING_HAPPY;
            case SCENTING -> Sniffer.State.SCENTING;
            case SNIFFING -> Sniffer.State.SNIFFING;
            case SEARCHING -> Sniffer.State.SEARCHING;
            case DIGGING -> Sniffer.State.DIGGING;
            case RISING -> Sniffer.State.RISING;
        };
    }
}

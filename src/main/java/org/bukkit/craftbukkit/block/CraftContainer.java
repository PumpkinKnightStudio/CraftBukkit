package org.bukkit.craftbukkit.block;

import net.md_5.bungee.api.chat.BaseComponent;
import net.minecraft.world.ChestLock;
import net.minecraft.world.level.block.entity.TileEntityContainer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Container;
import org.bukkit.craftbukkit.util.CraftChatMessage;

public abstract class CraftContainer<T extends TileEntityContainer> extends CraftBlockEntityState<T> implements Container {

    public CraftContainer(World world, T tileEntity) {
        super(world, tileEntity);
    }

    protected CraftContainer(CraftContainer<T> state, Location location) {
        super(state, location);
    }

    @Override
    public boolean isLocked() {
        return !this.getSnapshot().lockKey.key().isEmpty();
    }

    @Override
    public String getLock() {
        return this.getSnapshot().lockKey.key();
    }

    @Override
    public void setLock(String key) {
        this.getSnapshot().lockKey = (key == null) ? ChestLock.NO_LOCK : new ChestLock(key);
    }

    @Override
    public String getCustomName() {
        T container = this.getSnapshot();
        return container.name != null ? CraftChatMessage.fromComponent(container.getCustomName()) : null;
    }

    @Override
    public void setCustomName(String name) {
        this.getSnapshot().name = CraftChatMessage.fromStringOrNull(name);
    }

    @Override
    public void applyTo(T container) {
        super.applyTo(container);

        if (this.getSnapshot().name == null) {
            container.name = null;
        }
    }

    @Override
    public abstract CraftContainer<T> copy();

    @Override
    public abstract CraftContainer<T> copy(Location location);

    private final CraftComponents components = new CraftComponents();

    private final class CraftComponents implements org.bukkit.Nameable.Components {

        @Override
        public BaseComponent getCustomName() {
            return CraftChatMessage.toBungeeOrNull(getSnapshot().getCustomName());
        }

        @Override
        public void setCustomName(BaseComponent name) {
            getSnapshot().name = CraftChatMessage.fromBungeeOrNull(name);
        }
    }

    @Override
    public Components components() {
        return components;
    }
}

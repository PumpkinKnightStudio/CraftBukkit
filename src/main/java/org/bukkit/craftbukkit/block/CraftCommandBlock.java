package org.bukkit.craftbukkit.block;

import net.md_5.bungee.api.chat.BaseComponent;
import net.minecraft.world.level.block.entity.TileEntityCommand;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.CommandBlock;
import org.bukkit.craftbukkit.util.CraftChatMessage;

public class CraftCommandBlock extends CraftBlockEntityState<TileEntityCommand> implements CommandBlock {

    public CraftCommandBlock(World world, TileEntityCommand tileEntity) {
        super(world, tileEntity);
    }

    protected CraftCommandBlock(CraftCommandBlock state, Location location) {
        super(state, location);
    }

    @Override
    public String getCommand() {
        return getSnapshot().getCommandBlock().getCommand();
    }

    @Override
    public void setCommand(String command) {
        getSnapshot().getCommandBlock().setCommand(command != null ? command : "");
    }

    @Override
    public String getName() {
        return CraftChatMessage.fromComponent(getSnapshot().getCommandBlock().getName());
    }

    @Override
    public void setName(String name) {
        getSnapshot().getCommandBlock().setCustomName(CraftChatMessage.fromStringOrNull(name != null ? name : "@"));
    }

    @Override
    public CraftCommandBlock copy() {
        return new CraftCommandBlock(this, null);
    }

    @Override
    public CraftCommandBlock copy(Location location) {
        return new CraftCommandBlock(this, location);
    }

    private final CraftComponents components = new CraftComponents();

    private final class CraftComponents implements CommandBlock.Components {

        @Override
        public BaseComponent getName() {
            return CraftChatMessage.toBungee(getSnapshot().getCommandBlock().getName());
        }

        @Override
        public void setName(BaseComponent name) {
            getSnapshot().getCommandBlock().setCustomName(CraftChatMessage.fromBungeeOrNull(name));
        }
    }

    @Override
    public Components components() {
        return components;
    }
}

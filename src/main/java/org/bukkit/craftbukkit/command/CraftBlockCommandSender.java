package org.bukkit.craftbukkit.command;

import net.md_5.bungee.api.chat.BaseComponent;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.level.block.entity.TileEntity;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.ServerOperator;

/**
 * Represents input from a command block
 */
public class CraftBlockCommandSender extends ServerCommandSender implements BlockCommandSender {

    // For performance reasons, use one PermissibleBase for all command blocks.
    private static final PermissibleBase SHARED_PERM = new PermissibleBase(new ServerOperator() {

        @Override
        public boolean isOp() {
            return true;
        }

        @Override
        public void setOp(boolean value) {
            throw new UnsupportedOperationException("Cannot change operator status of a block");
        }
    });
    private final CommandListenerWrapper block;
    private final TileEntity tile;

    public CraftBlockCommandSender(CommandListenerWrapper commandBlockListenerAbstract, TileEntity tile) {
        super(SHARED_PERM);
        this.block = commandBlockListenerAbstract;
        this.tile = tile;
        this.components = new CraftComponents();
    }

    @Override
    public Block getBlock() {
        return CraftBlock.at(tile.getLevel(), tile.getBlockPos());
    }

    @Override
    public void sendMessage(String message) {
        for (IChatBaseComponent component : CraftChatMessage.fromString(message)) {
            block.source.sendSystemMessage(component);
        }
    }

    @Override
    public void sendMessage(String... messages) {
        for (String message : messages) {
            sendMessage(message);
        }
    }

    @Override
    public String getName() {
        return block.getTextName();
    }

    @Override
    public boolean isOp() {
        return SHARED_PERM.isOp();
    }

    @Override
    public void setOp(boolean value) {
        SHARED_PERM.setOp(value);
    }

    private final class CraftComponents extends ServerCommandSender.CraftComponents {

        @Override
        public void sendMessage(BaseComponent component) {
            block.source.sendSystemMessage(CraftChatMessage.fromBungee(component));
        }

    }

    public CommandListenerWrapper getWrapper() {
        return block;
    }
}

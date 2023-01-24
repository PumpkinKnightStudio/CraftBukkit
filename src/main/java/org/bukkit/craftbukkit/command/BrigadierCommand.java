package org.bukkit.craftbukkit.command;

public interface BrigadierCommand {

    /**
     * Get the brigadier command node that this command is for
     * @return The brigadier command node that this command is for
     */
    com.mojang.brigadier.tree.LiteralCommandNode<net.minecraft.commands.CommandListenerWrapper> getNode();

}

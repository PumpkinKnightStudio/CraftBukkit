package org.bukkit.craftbukkit.command;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import com.google.common.base.Preconditions;
import net.minecraft.server.ChatComponentText;
import net.minecraft.server.ICommandListener;
import net.minecraft.server.RemoteControlCommandListener;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.plugin.Plugin;

public class CraftRemoteConsoleCommandSender extends ServerCommandSender implements RemoteConsoleCommandSender {

    private final RemoteControlCommandListener listener;
    private final Set<Plugin> intents = new HashSet<>();
    private final CountDownLatch completionLatch = new CountDownLatch(1);
    private boolean acceptingNewIntents = true;
    private int pendingIntents = 0;

    public CraftRemoteConsoleCommandSender(RemoteControlCommandListener listener) {
        this.listener = listener;
    }

    public ICommandListener getHandle() {
        return listener;
    }

    public String getMessage() {
        return listener.getMessages();
    }

    public void await() throws InterruptedException {
        completionLatch.await();
    }

    public void markAsWaiting() {
        synchronized (this) {
            acceptingNewIntents = false;
            if (pendingIntents == 0) {
                completionLatch.countDown();
            }
        }
    }

    @Override
    public void sendMessage(String message) {
        listener.sendMessage(new ChatComponentText(message + "\n")); // Send a newline after each message, to preserve formatting.
    }

    @Override
    public void sendMessage(String[] messages) {
        for (String message : messages) {
            sendMessage(message);
        }
    }

    @Override
    public String getName() {
        return "Rcon";
    }

    @Override
    public boolean isOp() {
        return true;
    }

    @Override
    public void setOp(boolean value) {
        throw new UnsupportedOperationException("Cannot change operator status of remote controller.");
    }

    @Override
    public void registerIntent(Plugin plugin) {
        Preconditions.checkNotNull(plugin, "Plugin cannot be null");
        synchronized (this) {
            Preconditions.checkState(acceptingNewIntents, "Sender is not accepting new intents");
            Preconditions.checkState(intents.add(plugin), "Plugin has already registered an intent");
            pendingIntents++;
        }
    }

    @Override
    public void completeIntent(Plugin plugin) {
        Preconditions.checkNotNull(plugin, "Plugin cannot be null");
        synchronized (this) {
            Preconditions.checkState(intents.remove(plugin), "Plugin has not registered an intent");
            if (--pendingIntents == 0 && !acceptingNewIntents) {
                completionLatch.countDown();
            }
        }
    }
}

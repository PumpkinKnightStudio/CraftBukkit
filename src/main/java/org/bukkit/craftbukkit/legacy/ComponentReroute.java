package org.bukkit.craftbukkit.legacy;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.legacy.reroute.NotInBukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.WritableBookMeta;

@NotInBukkit
public class ComponentReroute {

    public static void broadcast(Object serverSpigot, BaseComponent component) {
        Bukkit.components().broadcast(component);
    }

    public static void broadcast(Object serverSpigot, BaseComponent... component) {
        Bukkit.components().broadcast(TextComponent.fromArray(component));
    }

    public static void sendMessage(CommandSender.Components components, BaseComponent... component) {
        components.sendMessage(TextComponent.fromArray(component));
    }

    public static void sendMessage(CommandSender.Components components, UUID sender, BaseComponent... component) {
        components.sendMessage(sender, TextComponent.fromArray(component));
    }

    public static BaseComponent[] getPage(WritableBookMeta.Components components, int page) {
        return new BaseComponent[] {components.getPage(page)};
    }

    public static void setPage(WritableBookMeta.Components components, int page, BaseComponent... component) {
        components.setPage(page, TextComponent.fromArray(component));
    }

    public static List<BaseComponent[]> getPages(WritableBookMeta.Components components) {
        return components.getPages().stream().map(c -> new BaseComponent[] {c}).toList();
    }

    public static void setPages(WritableBookMeta.Components components, List<BaseComponent[]> pages) {
        components.setPages(pages.stream().map(TextComponent::fromArray).toList());
    }

    public static void setPages(WritableBookMeta.Components components, BaseComponent[]... pages) {
        components.setPages(Arrays.stream(pages).map(TextComponent::fromArray).toList());
    }

    public static void addPage(WritableBookMeta.Components components, BaseComponent[]... pages) {
        components.addPages(Arrays.stream(pages).map(TextComponent::fromArray).toList());
    }

    public static void sendMessage(Player.Components components, ChatMessageType type, BaseComponent... component) {
        components.sendMessage(type, TextComponent.fromArray(component));
    }

    public static void sendMessage(Player.Components components, ChatMessageType type, UUID sender, BaseComponent... component) {
        components.sendMessage(type, sender, TextComponent.fromArray(component));
    }
}

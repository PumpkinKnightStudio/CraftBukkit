package org.bukkit.craftbukkit.util;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.server.ChatClickable;
import net.minecraft.server.ChatClickable.EnumClickAction;
import net.minecraft.server.ChatComponentText;
import net.minecraft.server.ChatMessage;
import net.minecraft.server.ChatModifier;
import net.minecraft.server.EnumChatFormat;
import net.minecraft.server.IChatBaseComponent;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Lists;
import org.bukkit.ChatColor;

/**
 * Creates, modifies, and formats chat components
 */
public final class CraftChatMessage {

    private static final Map<Character, EnumChatFormat> formatMap;

    static {
        Builder<Character, EnumChatFormat> builder = ImmutableMap.builder();
        for (EnumChatFormat format : EnumChatFormat.values()) {
            builder.put(Character.toLowerCase(format.toString().charAt(1)), format);
        }
        formatMap = builder.build();
    }

    public static EnumChatFormat getColor(ChatColor color) {
        return formatMap.get(color.getChar());
    }

    public static ChatColor getColor(EnumChatFormat format) {
        return ChatColor.getByChar(format.character);
    }

    /**
     * A regular expression that matches what could be a format code, even if it
     * is invalid.
     */
    private static final String FORMAT_CODE_REGEX = "(" + String.valueOf(org.bukkit.ChatColor.COLOR_CHAR) + "(?:.|$))";
    /**
     * A pattern that matches {@link #FORMAT_CODE_REGEX}.
     */
    private static final Pattern FORMAT_CODE_PATTERN = Pattern.compile(FORMAT_CODE_REGEX, Pattern.CASE_INSENSITIVE);
    /**
     * A regular expression that matches links with a protocol of 'http' or
     * 'https', ending the link when something that seems not to be a link is
     * matched.
     */
    private static final String LINK_REGEX = "((?:(?:https?):\\/\\/)?(?:[-\\w_\\.]{2,}\\.[a-z]{2,4}.*?(?=[\\.\\?!,;:]?(?:[" + String.valueOf(org.bukkit.ChatColor.COLOR_CHAR) + " \\n]|$))))";
    /**
     * A pattern that matches both {@link #FORMAT_CODE_REGEX} and
     * {@link #LINK_REGEX}.
     */
    private static final Pattern WORK_PATTERN = Pattern.compile(FORMAT_CODE_REGEX + "|" + LINK_REGEX, Pattern.CASE_INSENSITIVE);
    /**
     * Group IDs for use with {@link #WORK_PATTERN}.
     */
    private static final int FORMAT_CODE_GROUP = 1, LINK_GROUP = 2;

    /**
     * Converts the given formatted string into a chat component. Newlines will
     * be handled in the same way as if {@link #fromString(String, boolean)}
     * were called with false as the second parameter.
     *
     * @param message
     *            The formatted string
     * @return An array containing a component for each line in the message.
     */
    public static IChatBaseComponent[] fromString(String message) {
        return fromString(message, false);
    }

    /**
     * Converts the given formatted string into one or more chat components.
     *
     * @param message
     *            The formatted string
     * @param keepNewlinesInComponent
     *            If false, splits the message into one array entry on each new
     *            line. If true, newlines will instead be put directly into the
     *            result component, and the returned array will contain only one
     *            item.
     * @return An array containing multiple components, one for each line.
     */
    public static IChatBaseComponent[] fromString(String message, boolean keepNewlinesInComponent) {
        String[] lines;
        if (keepNewlinesInComponent) {
            lines = new String[] { message };
        } else {
            lines = splitStringOnNewlines(message);
        }
        IChatBaseComponent[] components = new IChatBaseComponent[lines.length];
        for (int i = 0; i < lines.length; i++) {
            components[i] = fixComponent(new ChatComponentText(lines[i]));
        }
        return components;
    }

    /**
     * Splits the given text on its newlines, preserving formatting.
     */
    private static String[] splitStringOnNewlines(String message) {
        Matcher matcher = FORMAT_CODE_PATTERN.matcher(message);

        String[] split = message.split("\\n", -1);
        if (matcher.find()) {
            ChatModifier modifier = new ChatModifier();
            for (int i = 0; i < split.length; i++) {
                if (!modifier.g()) { // g = isEmpty
                    // Update the line with the current format code.

                    StringBuilder newSection = new StringBuilder();
                    if (modifier.getColor() != null) {
                        newSection.append(modifier.getColor());
                    }
                    if (modifier.isBold()) {
                        newSection.append(EnumChatFormat.BOLD);
                    }
                    if (modifier.isItalic()) {
                        newSection.append(EnumChatFormat.ITALIC);
                    }
                    if (modifier.isUnderlined()) {
                        newSection.append(EnumChatFormat.UNDERLINE);
                    }
                    if (modifier.isStrikethrough()) {
                        newSection.append(EnumChatFormat.STRIKETHROUGH);
                    }
                    if (modifier.isRandom()) {
                        newSection.append(EnumChatFormat.OBFUSCATED);
                    }
                    newSection.append(split[i]);
                    split[i] = newSection.toString();
                }

                matcher.reset(split[i]);
                while (matcher.find()) {
                    modifier = updateModifierWithFormat(modifier, matcher.group());
                }
            }
        }

        return split;
    }

    private static ChatModifier updateModifierWithFormat(ChatModifier modifier,
            String formatCode) {
        EnumChatFormat format;
        if (formatCode.length() >= 2) {
            char code = Character.toLowerCase(formatCode.charAt(1));
            format = formatMap.get(code);
        } else {
            format = null;
        }

        if (format == null || format == EnumChatFormat.RESET) {
            // It's reset or an invalid code (which should be
            // treated as reset)
            modifier = new ChatModifier();
        } else if (format.isFormat()) {
            switch (format) {
            case BOLD:
                modifier.setBold(Boolean.TRUE);
                break;
            case ITALIC:
                modifier.setItalic(Boolean.TRUE);
                break;
            case STRIKETHROUGH:
                modifier.setStrikethrough(Boolean.TRUE);
                break;
            case UNDERLINE:
                modifier.setUnderline(Boolean.TRUE);
                break;
            case OBFUSCATED:
                modifier.setRandom(Boolean.TRUE);
                break;
            default:
                throw new AssertionError("Unexpected format " + format);
            }
        } else {
            // It's a color code.
            modifier = new ChatModifier();
            modifier.setColor(format);
        }
        return modifier;
    }

    /**
     * Converts the given component into a formatted string.
     *
     * @param component The component to convert
     * @return A formatted string.
     */
    public static String fromComponent(IChatBaseComponent component) {
        if (component == null) return "";
        StringBuilder out = new StringBuilder();
        
        for (IChatBaseComponent c : (Iterable<IChatBaseComponent>) component) {
            ChatModifier modi = c.getChatModifier();
            out.append(modi.getColor() == null ? EnumChatFormat.RESET : modi.getColor());
            if (modi.isBold()) {
                out.append(EnumChatFormat.BOLD);
            }
            if (modi.isItalic()) {
                out.append(EnumChatFormat.ITALIC);
            }
            if (modi.isUnderlined()) {
                out.append(EnumChatFormat.UNDERLINE);
            }
            if (modi.isStrikethrough()) {
                out.append(EnumChatFormat.STRIKETHROUGH);
            }
            if (modi.isRandom()) {
                out.append(EnumChatFormat.OBFUSCATED);
            }
            out.append(c.getText());
        }
        return out.toString().replaceFirst("^(" + EnumChatFormat.RESET + ")*", "");
    }

    /**
     * Fixes the given component, creating links and converting formatting codes
     * into styled components.
     *
     * @param component
     *            The component to adjust.
     * @return Component, with links and proper formatting.
     */
    public static IChatBaseComponent fixComponent(IChatBaseComponent component) {
        // Note: cannot iterate over the component directly because that breaks
        // translation components (which return translated data, not untranslated data)

        // Handle all siblings first
        List<IChatBaseComponent> extras = component.a();
        for (int i = 0; i < extras.size(); i++) {
            extras.set(i, fixComponent((IChatBaseComponent) extras.get(i)));
        }

        if (component instanceof ChatMessage) {
            Object[] params = ((ChatMessage) component).j();
            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                if (param instanceof IChatBaseComponent) {
                    params[i] = fixComponent((IChatBaseComponent)param);
                } else if (param instanceof String) {
                    params[i] = fromString((String) param, true)[0];
                }
            }
        }

        if (component instanceof ChatComponentText) {
            String msg = ((ChatComponentText) component).g();

            Matcher matcher = WORK_PATTERN.matcher(msg);
            if (matcher.find()) {
                ChatModifier modifier = component.getChatModifier();

                List<IChatBaseComponent> newExtras = Lists.newArrayList();

                int pos = 0;
                do {
                    int start = matcher.start();

                    if (start > pos) {
                        ChatComponentText prev = new ChatComponentText(
                                msg.substring(pos, matcher.start()));
                        prev.setChatModifier(modifier.clone().simplify());
                        newExtras.add(prev);
                    }

                    String match;
                    if ((match = matcher.group(FORMAT_CODE_GROUP)) != null) {
                        modifier = updateModifierWithFormat(modifier, match);
                        // Note: No component is appended.
                    } else {
                        match = matcher.group(LINK_GROUP);

                        if (!(match.startsWith("http://") || match.startsWith("https://"))) {
                            match = "http://" + match;
                        }

                        ChatComponentText link = new ChatComponentText(matcher.group());
                        ChatModifier linkModi = modifier.clone().simplify();
                        linkModi.setChatClickable(new ChatClickable(EnumClickAction.OPEN_URL, match));
                        link.setChatModifier(linkModi);

                        newExtras.add(link);
                    }

                    pos = matcher.end();
                } while (matcher.find());

                if (pos < msg.length()) {
                    ChatComponentText remainder = new ChatComponentText(
                            msg.substring(pos));
                    remainder.setChatModifier(modifier.simplify());
                    newExtras.add(remainder);
                }

                if (newExtras.size() == 1 && extras.size() == 0) {
                    component = newExtras.get(0);
                } else {
                    component = new ChatComponentText("");
                    for (IChatBaseComponent c : newExtras) {
                        component.addSibling(c);
                    }
                    for (IChatBaseComponent c : extras) {
                        component.addSibling(c);
                    }
                }
            } else {
                if (msg.length() == 0 && extras.size() == 1) {
                    ChatModifier origModifier = component.getChatModifier();

                    component = extras.get(0);
                    component.getChatModifier().merge(origModifier);
                }
            }
        }
        component.getChatModifier().simplify();

        return component;
    }

    private CraftChatMessage() {
    }
}

package org.bukkit.craftbukkit.util;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import net.minecraft.server.ChatModifier;
import net.minecraft.server.ChatTypeAdapterFactory;
import net.minecraft.server.IChatBaseComponent;

import org.bukkit.ChatColor;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class CraftChatMessageTest {
    private static final Gson gson;
    private static final JsonParser jsonParser;

    static {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeHierarchyAdapter(IChatBaseComponent.class, new IChatBaseComponent.ChatSerializer());
        builder.registerTypeHierarchyAdapter(ChatModifier.class, new ChatModifier.ChatModifierSerializer());
        builder.registerTypeAdapterFactory(new ChatTypeAdapterFactory());;
        gson = builder.create();
        jsonParser = new JsonParser();
    }

    @Test
    public void testFromStringSimple() {
        assertComponentsEqual(CraftChatMessage.fromString("Text"),
                "{text:\"\",extra:[{text:Text}]}");
    }

    @Test
    public void testFromStringColors() {
        assertComponentsEqual(
                CraftChatMessage.fromString(ChatColor.RED + "Text"),
                "{text:\"\",extra:[{text:Text,color:red}]}");
        assertComponentsEqual(
                CraftChatMessage.fromString(ChatColor.BLUE + "T"
                        + ChatColor.GREEN + "e" + ChatColor.AQUA + "x"
                        + ChatColor.RED + "t"),
                "{text:\"\",extra:[{text:T,color:blue},{text:e,color:green},"
                        + "{text:x,color:aqua},{text:t,color:red}]}");
        // No empty component for each color
        assertComponentsEqual(
                CraftChatMessage.fromString("" + ChatColor.BLUE
                        + ChatColor.GREEN + ChatColor.AQUA + ChatColor.RED
                        + "Text"),
                "{text:\"\",extra:[{text:Text,color:red}]}");
        // Both cases work
        assertComponentsEqual(
                CraftChatMessage.fromString(ChatColor.COLOR_CHAR + "aTe" +
                        ChatColor.COLOR_CHAR + "Cxt"),
                "{text:\"\",extra:[{text:Te,color:green},{text:xt,color:red}]}");
    }

    @Test
    public void testFromStringFormat() {
        assertComponentsEqual(
                CraftChatMessage.fromString(ChatColor.BOLD + "Text"),
                "{text:\"\",extra:[{text:Text,bold:true}]}");
        assertComponentsEqual(
                CraftChatMessage.fromString(ChatColor.RED + "" + ChatColor.BOLD + "Text"),
                "{text:\"\",extra:[{text:Text,bold:true,color:red}]}");
        // Formats can be applied after colors, without reseting the color
        assertComponentsEqual(
                CraftChatMessage.fromString(ChatColor.RED + "Te" + ChatColor.BOLD + "xt"),
                "{text:\"\",extra:[{text:Te,color:red},{text:xt,bold:true,color:red}]}");
        // Colors after formats reset the format (also, no empty component!)
        assertComponentsEqual(
                CraftChatMessage.fromString(ChatColor.BOLD + "" + ChatColor.RED + "Text"),
                "{text:\"\",extra:[{text:Text,color:red}]}");
        assertComponentsEqual(
                CraftChatMessage.fromString(ChatColor.BOLD + "Te" + ChatColor.RED + "xt"),
                "{text:\"\",extra:[{text:Te,bold:true},{text:xt,color:red}]}");
        // And reset resets formatting and color
        assertComponentsEqual(
                CraftChatMessage.fromString(ChatColor.BOLD + "Te" + ChatColor.RESET + "xt"),
                "{text:\"\",extra:[{text:Te,bold:true},{text:xt}]}");
        assertComponentsEqual(
                CraftChatMessage.fromString(ChatColor.RED + "Te" + ChatColor.RESET + "xt"),
                "{text:\"\",extra:[{text:Te,color:red},{text:xt}]}");

        // Invalid format codes are just left as-is
        assertComponentsEqual(
                CraftChatMessage.fromString(ChatColor.COLOR_CHAR + "qText"),
                "{text:\"\",extra:[{text:\"\\u00a7qText\"}]}");
    }

    @Test
    public void testFromStringLink() {
        assertComponentsEqual(
                CraftChatMessage.fromString("http://example.com"),
                "{text:\"\",extra:[{text:\"http://example.com\",clickEvent:"
                + "{action:open_url,value:\"http://example.com\"}}]}");
        assertComponentsEqual(
                CraftChatMessage.fromString(ChatColor.BLUE + "http://example.com"),
                "{text:\"\",extra:[{text:\"http://example.com\",color:blue,clickEvent:"
                + "{action:open_url,value:\"http://example.com\"}}]}");
        assertComponentsEqual(
                CraftChatMessage.fromString("https://example.com"),
                "{text:\"\",extra:[{text:\"https://example.com\",clickEvent:"
                + "{action:open_url,value:\"https://example.com\"}}]}");
        // If it looks like a link, it is automatically made into an HTTP link
        assertComponentsEqual(
                CraftChatMessage.fromString("example.com"),
                "{text:\"\",extra:[{text:\"example.com\",clickEvent:"
                + "{action:open_url,value:\"http://example.com\"}}]}");
        // MC only allows http and https as protocols; thus, irc isn't linked.
        // However, the rest _is_ still a valid link... so make it into a web link.
        assertComponentsEqual(
                CraftChatMessage.fromString("irc://irc.freenode.net/mcdevs"),
                "{text:\"\",extra:[{text:\"irc://\"},{text:\"irc.freenode.net/mcdevs\","
                + "clickEvent:{action:open_url,value:\"http://irc.freenode.net/mcdevs\"}}]}");
        // Linking shouldn't break coloration
        assertComponentsEqual(
                CraftChatMessage.fromString(ChatColor.RED + "visit example.com pls"),
                "{text:\"\",extra:[{text:\"visit \",color:red},{text:example.com,color:red,"
                + "clickEvent:{action:open_url,value:\"http://example.com\"}},"
                + "{text:\" pls\",color:red}]}");
    }

    @Test
    public void testFromStringMultiline() {
        // False - split the component

        // Middle and trailing newlines are removed, but a single leading newline is kept
        assertComponentsEqual(CraftChatMessage.fromString("\n\nTest\n\ntest\n\n", false),
                "{text:\"\"}",
                "{text:\"\",extra:[{text:Test}]}",
                "{text:\"\",extra:[{text:test}]}");
        // Color is conserved between lines
        assertComponentsEqual(CraftChatMessage.fromString(ChatColor.RED + "Test\ntest", false),
                "{text:\"\",extra:[{text:Test,color:red}]}",
                "{text:\"\",extra:[{text:test,color:red}]}");

        // True - embed the \n inside of the component

        // All newlines are conserved, in their own components
        assertComponentsEqual(CraftChatMessage.fromString("\n\nTest\n\ntest\n\n", true),
                "{text:\"\",extra:[{text:\"\\n\"},{text:\"\\n\"},{text:Test},"
                + "{text:\"\\n\"},{text:\"\\n\"},{text:test},{text:\"\\n\"},{text:\"\\n\"}]}");
        // Color is still conserved, though not on the \n component
        assertComponentsEqual(CraftChatMessage.fromString(ChatColor.RED + "Test\ntest", true),
                "{text:\"\",extra:[{text:Test,color:red},{text:\"\\n\"},{text:test,color:red}]}");
    }

    @Test
    public void testFromComponent() {
        // Red should be included since it's the default color
        assertThat(CraftChatMessage.fromComponent(
                component("{text:Test,color:red}")),
                is(ChatColor.RED + "Test"));
        assertThat(CraftChatMessage.fromComponent(
                component("{text:\"\",extra:[{text:Test1,color:red},{text:Test2,color:red}]}")),
                is(ChatColor.RED + "Test1" + ChatColor.RED + "Test2"));
        assertThat(CraftChatMessage.fromComponent(
                component("{text:Test,color:red,extra:[{text:\"Also red\"}]}")),
                is(ChatColor.RED + "Test" + ChatColor.RED + "Also red"));
        // Black is stripped by default
        assertThat(CraftChatMessage.fromComponent(
                component("{text:Test,color:black}")),
                is("Test"));
        // Only the leading instance is stripped
        assertThat(CraftChatMessage.fromComponent(
                component("{text:\"\",extra:[{text:Test1,color:black},{text:Test2,color:black}]}")),
                is("Test1" + ChatColor.BLACK + "Test2"));
        // Since the default color is black, unstyled components have that color inserted
        assertThat(CraftChatMessage.fromComponent(
                component("{text:\"\",extra:[{text:Test1,color:red},{text:Test2,color:black}]}")),
                is(ChatColor.RED + "Test1" + ChatColor.BLACK + "Test2"));
        // ... even when there is no other color.
        assertThat(CraftChatMessage.fromComponent(
                component("{text:Test1,extra:[{text:Test2}]}")),
                is("Test1" + ChatColor.BLACK + "Test2"));
    }

    @Test
    public void testFixComponent() {
        assertComponentEquals(CraftChatMessage.fixComponent(
                component("{text:\"test example.com test\"}")),
                "{text:\"\",extra:[{text:\"test \"},{text:\"example.com\","
                + "clickEvent:{action:open_url,value:\"http://example.com\"}},"
                + "{text:\" test\"}]}");
        // Extra padding empty components...
        assertComponentEquals(CraftChatMessage.fixComponent(
                component("{text:\"example.com\"}")),
                "{text:\"\",extra:[{text:\"\"},{text:\"example.com\","
                + "clickEvent:{action:open_url,value:\"http://example.com\"}},"
                + "{text:\"\"}]}");
        // Existing click events are replaced only for the link component
        assertComponentEquals(CraftChatMessage.fixComponent(
                component("{text:\"Click if you like example.com!\",clickEvent:"
                        + "{action:run_command,value:\"/me likes example.com\"}}")),
                "{text:\"\",extra:[{text:\"Click if you like \",clickEvent:"
                + "{action:run_command,value:\"/me likes example.com\"}},"
                + "{text:\"example.com\",clickEvent:{action:open_url,value:\"http://example.com\"}},"
                + "{text:\"!\",clickEvent:"
                + "{action:run_command,value:\"/me likes example.com\"}}]}");
    }

    /**
     * Converts the given JSON into a component, using
     * {@linkplain JsonReader#setLenient(boolean) lenient parsing}.  Note that
     * all vanilla commands and nearly all vanilla usages of chat components
     * use strict parsing; lenient parsing is only used here to reduce the
     * number of escaped quotes in the unit tests.
     */
    private IChatBaseComponent component(String json) {
        return gson.fromJson(json, IChatBaseComponent.class);
    }

    /**
     * Assert that the given component matches the expected JSON.
     */
    private void assertComponentEquals(IChatBaseComponent component, String expected) {
        assertComponentsEqual(new IChatBaseComponent[] { component }, expected);
    }

    /**
     * Asserts that the given component matches the expected JSON.
     * <p>
     * JSON-based comparison is used because it gives a much more concise error
     * message (unset styles are hidden), and is a lot cleaner to write the
     * expected value.
     */
    private void assertComponentsEqual(IChatBaseComponent[] components, String... expectedJson) {
        JsonElement[] componentElements = new JsonElement[components.length];
        for (int i = 0; i < components.length; i++) {
            componentElements[i] = gson.toJsonTree(components[i]);
        }
        JsonElement[] expectedElements = new JsonElement[expectedJson.length];
        for (int i = 0; i < expectedJson.length; i++) {
            expectedElements[i] = jsonParser.parse(expectedJson[i]);
        }

        assertThat(componentElements, is(arrayContaining(expectedElements)));
    }
}

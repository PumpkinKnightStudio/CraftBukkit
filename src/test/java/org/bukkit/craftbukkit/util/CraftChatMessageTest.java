package org.bukkit.craftbukkit.util;

import static org.junit.Assert.assertEquals;
import net.minecraft.server.IChatBaseComponent;
import net.minecraft.server.IChatMutableComponent;
import org.junit.Test;

public class CraftChatMessageTest {

    @Test
    public void testSimpleStrings() {
        // These should be able to go from legacy to comp to legacy back without data changing
        testString("§fFoo");
        // Color codes clear previous formatting, but we retain it for backwards conversions
        testString("§f§lFoo");
        testString("§l§fFoo");
        testString("§f§fFoo");
        testString("§fFoo§f§l"); // Keeps empty format at end
        testString("§fFoo§r");
        testString("Foo");
        testString("§r§oFoo"); // Retains reset at start (item names can use this to get rid of italics)

        testString("§fFoo§rBar");
        // Repeated resets:
        testString("§fFoo§r§rBar");
        testString("§fFoo§rBaz§rBar");
        testString("§r§rFoo");

        testString("Foo§bBar");
        testString("F§loo§b§oBa§b§lr"); // any non color formatting code implies previous color code.
        // So §l at start has no inherited color code, so that's fine, but the one at the end,
        // while Ba§l would work visually, serializing back will include the implied color

        testString("F§loo§rBa§lr"); // But if reset was used before.... then it can be standalone
        testString("§fFoo§r§lBar"); // Also works with empty text in-between
        testString("§fFoo§bBar");
        testString("§fFoo§bBar§rBaz");
    }

    @Test
    public void testNewLineBehavior() {
        // new line retain should stay as 1 comp
        testString("Hello§0\n§rFoo\n§5Test", true);
        testString("Hello§0\n§rFoo\n§5Test§0\nBar", true);
        testString("Hello§0\n§rFoo\n§5Test§0\nBar§0\n§0Baz", true);
        testString("§0Foo!\n", true);
        testString("§0Foo!§0\\n§0\\n§0Bar\n", true);

        // dont retain line returns multiple components
        IChatBaseComponent[] components = CraftChatMessage.fromString("Hello§0\n§rFoo\n§5Test§0\nBar§0\n§0Baz");
        assertEquals("Has 5 components", 5, components.length);
        assertEquals("Hello§0", CraftChatMessage.fromComponent(components[0]));
        assertEquals("§rFoo", CraftChatMessage.fromComponent(components[1]));
        assertEquals("§5Test§0", CraftChatMessage.fromComponent(components[2]));
        // Note: The color code from the end of the previous line gets copied to the next line.
        // We cannot differentiate between an explicitly set color code and one which has been inherited from the previous line.
        assertEquals("§0Bar§0", CraftChatMessage.fromComponent(components[3]));
        assertEquals("§0Baz", CraftChatMessage.fromComponent(components[4]));
    }

    @Test
    public void testComponents() {
        testComponent("Foo§bBar§rBaz", create("Foo", "§bBar", "§rBaz"));
        testComponent("§fFoo§bBar§rBaz", create("", "§fFoo", "§bBar", "§rBaz"));
        testComponent("§fFoo§bBar§rBaz", create("", "§fFoo", "§bBar", "", "§rBaz"));
        testComponent("§fFoo§bBar§rBaz", create("§fFoo", "§bBar", "§rBaz"));
        testComponent("Foo§bBar§rBaz", create("", "Foo", "§bBar", "§rBaz"));
        testComponent("§fFoo§bBar§rBaz", create("§fFoo", "§bBar", "§rBaz"));
        testComponent("F§foo§bBar§rBaz", create("F§foo", "§bBar", "§rBaz"));
    }

    private IChatBaseComponent create(String txt, String... rest) {
        IChatMutableComponent cmp = CraftChatMessage.fromString(txt, false)[0].mutableCopy();
        for (String s : rest) {
            // The root component produced by CraftChatMessage#fromString is empty.
            // We omit it here, because it would get interpreted as reset when it is part of another component.
            for (IChatBaseComponent sibling : CraftChatMessage.fromString(s, true)[0].getSiblings()) {
                cmp.addSibling(sibling);
            }
        }

        return cmp;
    }

    private void testString(String expected) {
        testString(expected, false);
    }

    private void testString(String expected, boolean keepNewLines) {
        testString(expected, expected, keepNewLines);
    }

    private void testString(String input, String expected) {
        testString(input, expected, false);
    }

    private void testString(String input, String expected, boolean keepNewLines) {
        IChatBaseComponent cmp = CraftChatMessage.fromString(input, keepNewLines)[0];
        String actual = CraftChatMessage.fromComponent(cmp);
        assertEquals("\nComponent: " + cmp + "\n", expected, actual);
    }

    private void testComponent(String expected, IChatBaseComponent cmp) {
        String actual = CraftChatMessage.fromComponent(cmp);
        assertEquals("\nComponent: " + cmp + "\n", expected, actual);

        IChatBaseComponent expectedCmp = CraftChatMessage.fromString(expected, true)[0];
        String actualExpectedCmp = CraftChatMessage.fromComponent(expectedCmp);
        assertEquals("\nComponent: " + expectedCmp + "\n", expected, actualExpectedCmp);
    }
}

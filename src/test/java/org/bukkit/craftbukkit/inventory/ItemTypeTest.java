package org.bukkit.craftbukkit.inventory;

import static org.bukkit.support.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import java.lang.reflect.Field;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.Test;

public class ItemTypeTest {

    // Ensures all ItemType constants have the correct generics
    @Test
    public void testItemMetaClasses() throws Exception {
        for (Field f : ItemType.class.getDeclaredFields()) {
            ItemType<?> type = (ItemType<?>) f.get(null);

            ItemMeta meta = new ItemStack(type.asMaterial()).getItemMeta();
            Class<?> internal = meta == null ? CraftMetaItem.class : meta.getClass();
            Class<?>[] interfaces = internal.getInterfaces();
            Class<?> expected;
            if (interfaces.length > 0) {
                expected = interfaces[0];
            } else {
                expected = ItemMeta.class;
            }

            // Currently the expected and actual for AIR are ItemMeta rather than null
            Class<?> actual = type.getItemMetaClass();
            assertThat(actual, is(expected));
        }
    }
}

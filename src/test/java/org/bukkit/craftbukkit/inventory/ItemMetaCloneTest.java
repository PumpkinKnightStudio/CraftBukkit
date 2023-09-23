package org.bukkit.craftbukkit.inventory;

import static org.bukkit.support.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Method;
import org.bukkit.Material;
import org.bukkit.inventory.ItemType;
import org.junit.jupiter.api.Test;

public class ItemMetaCloneTest {

    @Test
    public void testClone() throws Throwable {
        for (ItemType itemType : ItemStackTest.COMPOUND_ITEM_TYPES) {
            Class<?> clazz = CraftItemFactory.instance().getItemMeta(itemType).getClass();

            Method clone = clazz.getDeclaredMethod("clone");
            assertNotNull(clone, "Class " + clazz + " does not override clone()");
            assertThat(clone.getReturnType(), is(equalTo(clazz)), "Class " + clazz + " clone return type does not match");
        }
    }
}

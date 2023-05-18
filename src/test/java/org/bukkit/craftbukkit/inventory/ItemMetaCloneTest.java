package org.bukkit.craftbukkit.inventory;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import java.lang.reflect.Method;
import org.bukkit.Material;
import org.bukkit.inventory.ItemType;
import org.junit.Test;

public class ItemMetaCloneTest {

    @Test
    public void testClone() throws Throwable {
        for (ItemType itemType : ItemStackTest.COMPOUND_ITEM_TYPES) {
            Class<?> clazz = CraftItemFactory.instance().getItemMeta(itemType).getClass();

            Method clone = clazz.getDeclaredMethod("clone");
            assertNotNull("Class " + clazz + " does not override clone()", clone);
            assertThat("Class " + clazz + " clone return type does not match", clone.getReturnType(), is(equalTo(clazz)));
        }
    }
}

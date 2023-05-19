package org.bukkit.craftbukkit.inventory;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Registry;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.support.AbstractTestingBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class FactoryItemMaterialTest extends AbstractTestingBase {
    static final ItemFactory factory = CraftItemFactory.instance();
    static final StringBuilder buffer = new StringBuilder();
    static final ItemType[] itemTypes;

    static {
        itemTypes = Lists.newArrayList(Registry.ITEM).toArray(new ItemType[0]);
    }

    static String name(ItemType from, ItemType to) {
        if (from.getClass() == to.getClass()) {
            return buffer.delete(0, Integer.MAX_VALUE).append(from.getClass().getName()).append(' ').append(from.getKey()).append(" to ").append(to.getKey()).toString();
        }
        return buffer.delete(0, Integer.MAX_VALUE).append(from.getClass().getName()).append('(').append(from.getKey()).append(") to ").append(to.getClass().getName()).append('(').append(to.getKey()).append(')').toString();
    }

    @Parameters(name = "ItemType[{index}]:{0}")
    public static List<Object[]> data() {
        List<Object[]> list = new ArrayList<Object[]>();
        for (ItemType itemType : itemTypes) {
            list.add(new Object[] {itemType});
        }
        return list;
    }

    @Parameter(0) public ItemType itemType;

    @Test
    public void itemStack() {
        ItemStack bukkitStack = new ItemStack(itemType);
        CraftItemStack craftStack = CraftItemStack.asCraftCopy(bukkitStack);
        ItemMeta meta = factory.getItemMeta(itemType);
        if (meta == null) {
            assertThat(itemType, is(ItemType.AIR));
        } else {
            assertTrue(factory.isApplicable(meta, bukkitStack));
            assertTrue(factory.isApplicable(meta, craftStack));
        }
    }

    @Test
    public void generalCase() {
        CraftMetaItem meta = (CraftMetaItem) factory.getItemMeta(itemType);
        if (meta == null) {
            assertThat(itemType, is(ItemType.AIR));
        } else {
            assertTrue(factory.isApplicable(meta, itemType));
            assertTrue(meta.applicableTo(itemType));

            meta = meta.clone();
            assertTrue(factory.isApplicable(meta, itemType));
            assertTrue(meta.applicableTo(itemType));
        }
    }

    @Test
    public void asMetaFor() {
        final CraftMetaItem baseMeta = (CraftMetaItem) factory.getItemMeta(itemType);
        if (baseMeta == null) {
            assertThat(itemType, is(ItemType.AIR));
            return;
        }

        for (ItemType other : itemTypes) {
            final ItemStack bukkitStack = new ItemStack(other);
            final CraftItemStack craftStack = CraftItemStack.asCraftCopy(bukkitStack);
            final CraftMetaItem otherMeta = (CraftMetaItem) factory.asMetaFor(baseMeta, other);

            final String testName = name(itemType, other);

            if (otherMeta == null) {
                assertThat(testName, other, is(ItemType.AIR));
                continue;
            }

            assertTrue(testName, factory.isApplicable(otherMeta, craftStack));
            assertTrue(testName, factory.isApplicable(otherMeta, bukkitStack));
            assertTrue(testName, factory.isApplicable(otherMeta, other));
            assertTrue(testName, otherMeta.applicableTo(other));
        }
    }

    @Test
    public void blankEqualities() {
        if (itemType == ItemType.AIR) {
            return;
        }
        final CraftMetaItem baseMeta = (CraftMetaItem) factory.getItemMeta(itemType);
        final CraftMetaItem baseMetaClone = baseMeta.clone();

        final ItemStack baseMetaStack = new ItemStack(itemType);
        baseMetaStack.setItemMeta(baseMeta);

        assertThat(baseMeta, is(not(sameInstance(baseMetaStack.getItemMeta()))));

        assertTrue(factory.equals(baseMeta, null));
        assertTrue(factory.equals(null, baseMeta));

        assertTrue(factory.equals(baseMeta, baseMetaClone));
        assertTrue(factory.equals(baseMetaClone, baseMeta));

        assertThat(baseMeta, is(not(sameInstance(baseMetaClone))));

        assertThat(baseMeta, is(baseMetaClone));
        assertThat(baseMetaClone, is(baseMeta));

        for (ItemType other : itemTypes) {
            final String testName = name(itemType, other);

            final CraftMetaItem otherMeta = (CraftMetaItem) factory.asMetaFor(baseMetaClone, other);

            if (otherMeta == null) {
                assertThat(testName, other, is(ItemType.AIR));
                continue;
            }

            assertTrue(testName, factory.equals(baseMeta, otherMeta));
            assertTrue(testName, factory.equals(otherMeta, baseMeta));

            assertThat(testName, baseMeta, is(otherMeta));
            assertThat(testName, otherMeta, is(baseMeta));

            assertThat(testName, baseMeta.hashCode(), is(otherMeta.hashCode()));
        }
    }
}

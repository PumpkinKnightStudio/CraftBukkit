package org.bukkit.craftbukkit.inventory;

import static org.bukkit.support.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.bukkit.Registry;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.support.AbstractTestingBase;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@EnabledIfSystemProperty(named = "testEnv", matches = "full", disabledReason = "Disable for now, since Mockito's Location feature is too heavy in combination with this test")
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

    public static Stream<Arguments> data() {
        List<Arguments> list = new ArrayList<>();
        for (ItemType itemType : itemTypes) {
            list.add(Arguments.of(itemType));
        }
        return list.stream();
    }

    @ParameterizedTest
    @MethodSource("data")
    public void itemStack(ItemType itemType) {
        ItemStack bukkitStack = ItemStack.of(itemType);
        CraftItemStack craftStack = CraftItemStack.asCraftCopy(bukkitStack);
        ItemMeta meta = factory.getItemMeta(itemType);
        if (meta == null) {
            assertThat(itemType, is(ItemType.AIR));
        } else {
            assertTrue(factory.isApplicable(meta, bukkitStack));
            assertTrue(factory.isApplicable(meta, craftStack));
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void generalCase(ItemType itemType) {
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

    @ParameterizedTest
    @MethodSource("data")
    public void asMetaFor(ItemType itemType) {
        final CraftMetaItem baseMeta = (CraftMetaItem) factory.getItemMeta(itemType);
        if (baseMeta == null) {
            assertThat(itemType, is(ItemType.AIR));
            return;
        }

        for (ItemType other : itemTypes) {
            final ItemStack bukkitStack = ItemStack.of(other);
            final CraftItemStack craftStack = CraftItemStack.asCraftCopy(bukkitStack);
            final CraftMetaItem otherMeta = (CraftMetaItem) factory.asMetaFor(baseMeta, other);

            final String testName = name(itemType, other);

            if (otherMeta == null) {
                assertThat(other, is(ItemType.AIR), testName);
                continue;
            }

            assertTrue(factory.isApplicable(otherMeta, craftStack), testName);
            assertTrue(factory.isApplicable(otherMeta, bukkitStack), testName);
            assertTrue(factory.isApplicable(otherMeta, other), testName);
            assertTrue(otherMeta.applicableTo(other), testName);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void blankEqualities(ItemType itemType) {
        if (itemType == ItemType.AIR) {
            return;
        }
        final CraftMetaItem baseMeta = (CraftMetaItem) factory.getItemMeta(itemType);
        final CraftMetaItem baseMetaClone = baseMeta.clone();

        final ItemStack baseMetaStack = ItemStack.of(itemType);
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
                assertThat(other, is(ItemType.AIR), testName);
                continue;
            }

            assertTrue(factory.equals(baseMeta, otherMeta), testName);
            assertTrue(factory.equals(otherMeta, baseMeta), testName);

            assertThat(baseMeta, is(otherMeta), testName);
            assertThat(otherMeta, is(baseMeta), testName);

            assertThat(baseMeta.hashCode(), is(otherMeta.hashCode()), testName);
        }
    }
}

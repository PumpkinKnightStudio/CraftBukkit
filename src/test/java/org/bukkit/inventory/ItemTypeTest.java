package org.bukkit.inventory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.item.Item;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.support.AbstractTestingBase;
import org.junit.Assert;
import org.junit.Test;

public class ItemTypeTest extends AbstractTestingBase {

    @Test
    public void testBukkitToMinecraftFieldName() {
        for (Field field : ItemType.class.getFields()) {
            if (field.getType() != ItemType.class) {
                continue;
            }
            if (!Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            String name = field.getName();
            Assert.assertNotNull("No ItemType for field name " + name, Registry.MATERIAL.get(NamespacedKey.fromString(name.toLowerCase())));
        }
    }

    @Test
    public void testMinecraftToBukkitFieldName() {
        for (Item item : BuiltInRegistries.ITEM) {
            MinecraftKey minecraftKey = BuiltInRegistries.ITEM.getKey(item);

            try {
                Field field = ItemType.class.getField(minecraftKey.getPath().toUpperCase());
                Assert.assertSame("No Bukkit default itemType for " + minecraftKey, ItemType.class, field.getType());
                ItemType itemType = (ItemType) field.get(null);

                Assert.assertEquals("Keys are not the same for " + minecraftKey, minecraftKey, CraftNamespacedKey.toMinecraft(itemType.getKey()));
            } catch (NoSuchFieldException e) {
                Assert.fail("No Bukkit default itemType for " + minecraftKey);
            } catch (IllegalAccessException e) {
                Assert.fail("Bukkit field is not access able for " + minecraftKey);
            } catch (ClassCastException e) {
                Assert.fail("Bukkit field is not of type itemType for " + minecraftKey);
            }
        }
    }
}

package org.bukkit.craftbukkit.attribute;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.entity.ai.attributes.AttributeBase;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.support.AbstractTestingBase;
import org.junit.Assert;
import org.junit.Test;

public class AttributeTest extends AbstractTestingBase {

    @Test
    public void testBukkitToMinecraftFieldName() {
        for (Field field : Attribute.class.getFields()) {
            if (field.getType() != Attribute.class) {
                continue;
            }
            if (!Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            try {
                NamespacedKey key = ((Keyed) field.get(null)).getKey();
                Assert.assertNotNull("No Attribute for field name " + field.getName(), Registry.ATTRIBUTE.get(key));
            } catch (IllegalAccessException e) {
                Assert.fail("Can't get object for Bukkit field " + field.getName());
            }
        }
    }

    @Test
    public void testMinecraftToBukkitFieldName() {
        for (AttributeBase attributeBase : BuiltInRegistries.ATTRIBUTE) {
            MinecraftKey minecraftKey = BuiltInRegistries.ATTRIBUTE.getKey(attributeBase);

            try {
                Attribute attribute = (Attribute) Attribute.class.getField(minecraftKey.getPath().toUpperCase().replace(".", "_")).get(null);

                Assert.assertEquals("Keys are not the same for " + minecraftKey, minecraftKey, CraftNamespacedKey.toMinecraft(attribute.getKey()));
            } catch (NoSuchFieldException e) {
                Assert.fail("No Bukkit default attribute for " + minecraftKey);
            } catch (IllegalAccessException e) {
                Assert.fail("Bukkit field is not access able for " + minecraftKey);
            } catch (ClassCastException e) {
                Assert.fail("Bukkit field is not of type attribute for" + minecraftKey);
            }
        }
    }

    @Test
    public void testToBukkit() {
        for (MinecraftKey nms : BuiltInRegistries.ATTRIBUTE.keySet()) {
            Attribute bukkit = CraftAttributeMap.fromMinecraft(nms.toString());

            Assert.assertNotNull(nms.toString(), bukkit);
        }
    }

    @Test
    public void testToNMS() {
        for (Attribute attribute : Attribute.values()) {
            AttributeBase nms = CraftAttributeMap.toMinecraft(BuiltInRegistries.ATTRIBUTE, attribute);

            Assert.assertNotNull(attribute.name(), nms);
        }
    }
}

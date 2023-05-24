package org.bukkit.entity;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.entity.animal.CatVariant;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.support.AbstractTestingBase;
import org.junit.Assert;
import org.junit.Test;

public class CatTypeTest extends AbstractTestingBase {

    @Test
    public void testBukkitToMinecraftFieldName() {
        for (Field field : Cat.Type.class.getFields()) {
            if (field.getType() != Cat.Type.class) {
                continue;
            }
            if (!Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            String name = field.getName();
            Assert.assertNotNull("No cat type for field name " + name, Registry.CAT_TYPE.get(NamespacedKey.fromString(name.toLowerCase())));
        }
    }

    @Test
    public void testMinecraftToBukkitFieldName() {
        for (CatVariant catVariant : BuiltInRegistries.CAT_VARIANT) {
            MinecraftKey minecraftKey = BuiltInRegistries.CAT_VARIANT.getKey(catVariant);

            try {
                Cat.Type type = (Cat.Type) Cat.Type.class.getField(minecraftKey.getPath().toUpperCase()).get(null);

                Assert.assertEquals("Keys are not the same for " + minecraftKey, minecraftKey, CraftNamespacedKey.toMinecraft(type.getKey()));
            } catch (NoSuchFieldException e) {
                Assert.fail("No Bukkit default cat type for " + minecraftKey);
            } catch (IllegalAccessException e) {
                Assert.fail("Bukkit field is not access able for " + minecraftKey);
            } catch (ClassCastException e) {
                Assert.fail("Bukkit field is not of type cat type for" + minecraftKey);
            }
        }
    }
}

package org.bukkit.entity;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.entity.animal.FrogVariant;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.support.AbstractTestingBase;
import org.junit.Assert;
import org.junit.Test;

public class FrogVariantTest extends AbstractTestingBase {

    @Test
    public void testBukkitToMinecraftFieldName() {
        for (Field field : Frog.Variant.class.getFields()) {
            if (field.getType() != Frog.Variant.class) {
                continue;
            }
            if (!Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            String name = field.getName();
            Assert.assertNotNull("No frog variant for field name " + name, Registry.FROG_VARIANT.get(NamespacedKey.fromString(name.toLowerCase())));
        }
    }

    @Test
    public void testMinecraftToBukkitFieldName() {
        for (FrogVariant frogVariant : BuiltInRegistries.FROG_VARIANT) {
            MinecraftKey minecraftKey = BuiltInRegistries.FROG_VARIANT.getKey(frogVariant);

            try {
                Frog.Variant variant = (Frog.Variant) Frog.Variant.class.getField(minecraftKey.getPath().toUpperCase()).get(null);

                Assert.assertEquals("Keys are not the same for " + minecraftKey, minecraftKey, CraftNamespacedKey.toMinecraft(variant.getKey()));
            } catch (NoSuchFieldException e) {
                Assert.fail("No Bukkit default frog variant for " + minecraftKey);
            } catch (IllegalAccessException e) {
                Assert.fail("Bukkit field is not access able for " + minecraftKey);
            } catch (ClassCastException e) {
                Assert.fail("Bukkit field is not of type frog variant for" + minecraftKey);
            }
        }
    }
}

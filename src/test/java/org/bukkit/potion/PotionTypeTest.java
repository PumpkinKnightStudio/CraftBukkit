package org.bukkit.potion;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.item.alchemy.PotionRegistry;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.junit.Assert;
import org.junit.Test;

public class PotionTypeTest {

    @Test
    public void testBukkitToMinecraftFieldName() {
        for (Field field : PotionType.class.getFields()) {
            if (field.getType() != PotionType.class) {
                continue;
            }
            if (!Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            String name = field.getName();
            Assert.assertNotNull("No PotionType for field name " + name, Registry.POTION.get(NamespacedKey.fromString(name.toLowerCase())));
        }
    }

    @Test
    public void testMinecraftToBukkitFieldName() {
        for (PotionRegistry potionRegistry : BuiltInRegistries.POTION) {
            MinecraftKey minecraftKey = BuiltInRegistries.POTION.getKey(potionRegistry);

            try {
                PotionType potionType = (PotionType) PotionType.class.getField(minecraftKey.getPath().toUpperCase()).get(null);

                Assert.assertEquals("Keys are not the same for " + minecraftKey, minecraftKey, CraftNamespacedKey.toMinecraft(potionType.getKey()));
            } catch (NoSuchFieldException e) {
                Assert.fail("No Bukkit default potion type for " + minecraftKey);
            } catch (IllegalAccessException e) {
                Assert.fail("Bukkit field is not access able for " + minecraftKey);
            } catch (ClassCastException e) {
                Assert.fail("Bukkit field is not of type potion for" + minecraftKey);
            }
        }
    }
}

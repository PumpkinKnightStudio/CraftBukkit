package org.bukkit.enchantments;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.support.AbstractTestingBase;
import org.junit.Assert;
import org.junit.Test;

public class EnchantmentTest extends AbstractTestingBase {

    @Test
    public void testBukkitToMinecraftFieldName() {
        for (Field field : Enchantment.class.getFields()) {
            if (field.getType() != Enchantment.class) {
                continue;
            }
            if (!Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            String name = field.getName();
            Assert.assertNotNull("No enchantment for field name " + name, Registry.ENCHANTMENT.get(NamespacedKey.fromString(name.toLowerCase())));
        }
    }

    @Test
    public void testMinecraftToBukkitFieldName() {
        for (net.minecraft.world.item.enchantment.Enchantment enchantment : IRegistry.ENCHANTMENT) {
            MinecraftKey minecraftKey = IRegistry.ENCHANTMENT.getKey(enchantment);

            try {
                Enchantment bukkitEnchantment = (Enchantment) Enchantment.class.getField(minecraftKey.getKey().toUpperCase()).get(null);

                Assert.assertEquals("Keys are not the same for " + minecraftKey, minecraftKey, CraftNamespacedKey.toMinecraft(bukkitEnchantment.getKey()));
            } catch (NoSuchFieldException e) {
                Assert.fail("No Bukkit default enchantment for " + minecraftKey);
            } catch (IllegalAccessException e) {
                Assert.fail("Bukkit field is not access able for " + minecraftKey);
            } catch (ClassCastException e) {
                Assert.fail("Bukkit field is not of type enchantment for" + minecraftKey);
            }
        }
    }
}

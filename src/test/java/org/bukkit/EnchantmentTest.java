package org.bukkit;

import org.bukkit.enchantments.Enchantment;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.junit.Assert.assertTrue;

public class EnchantmentTest {

    @Test
    public void verifyEnchantmentFields() {
        for (Enchantment enchantment : Enchantment.values()) {
            try {
                Field enchantmentField = Enchantment.class.getField(enchantment.getName());
                assertTrue("Field for " + enchantment.getName() + " in org.bukkit.enchantments.Enchantment is not public", Modifier.isPublic(enchantmentField.getModifiers()));
                assertTrue("Field for " + enchantment.getName() + " in org.bukkit.enchantments.Enchantment is not static", Modifier.isStatic(enchantmentField.getModifiers()));
            } catch (NoSuchFieldException e) {
                throw new AssertionError("There is no public static field defined for the enchantment " + enchantment.getName() + " in org.bukkit.enchantments.Enchantment");
            }
        }
    }
}

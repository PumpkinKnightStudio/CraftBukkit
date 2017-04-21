package org.bukkit;

import org.bukkit.enchantments.Enchantment;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.junit.Assert.assertTrue;

public class EnchantmentTest {

    @Test
    public void verifyEnchantmentNames() throws IllegalAccessException {
        for (Field enchantField : Enchantment.class.getFields()) {
            boolean isEnchantmentField = Enchantment.class.isAssignableFrom(enchantField.getType())
                    && Modifier.isPublic(enchantField.getModifiers())
                    && Modifier.isStatic(enchantField.getModifiers())
                    && Modifier.isFinal(enchantField.getModifiers());
            if (isEnchantmentField) {
                Enchantment enchantment = (Enchantment) enchantField.get(Enchantment.class);
                assertTrue("Name of enchantment " + enchantField.getName() + " in org.bukkit.enchantments.Enchantment does not match name returned by org.bukkit.craftbukkit.enchantments.CraftEnchantment.getName() (" + enchantment.getName() + ")", enchantField.getName().equals(enchantment.getName()));
            }
        }
    }
}

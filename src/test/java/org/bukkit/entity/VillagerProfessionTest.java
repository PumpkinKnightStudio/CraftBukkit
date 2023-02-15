package org.bukkit.entity;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.entity.npc.VillagerProfession;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.support.AbstractTestingBase;
import org.junit.Assert;
import org.junit.Test;

public class VillagerProfessionTest extends AbstractTestingBase {

    @Test
    public void testBukkitToMinecraftFieldName() {
        for (Field field : Villager.Profession.class.getFields()) {
            if (field.getType() != Villager.Profession.class) {
                continue;
            }
            if (!Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            String name = field.getName();
            Assert.assertNotNull("No villager profession for field name " + name, Registry.VILLAGER_PROFESSION.get(NamespacedKey.fromString(name.toLowerCase())));
        }
    }

    @Test
    public void testMinecraftToBukkitFieldName() {
        for (VillagerProfession villagerProfession : BuiltInRegistries.VILLAGER_PROFESSION) {
            MinecraftKey minecraftKey = BuiltInRegistries.VILLAGER_PROFESSION.getKey(villagerProfession);

            try {
                Villager.Profession profession = (Villager.Profession) Villager.Profession.class.getField(minecraftKey.getPath().toUpperCase()).get(null);

                Assert.assertEquals("Keys are not the same for " + minecraftKey, minecraftKey, CraftNamespacedKey.toMinecraft(profession.getKey()));
            } catch (NoSuchFieldException e) {
                Assert.fail("No Bukkit default villager profession for " + minecraftKey);
            } catch (IllegalAccessException e) {
                Assert.fail("Bukkit field is not access able for " + minecraftKey);
            } catch (ClassCastException e) {
                Assert.fail("Bukkit field is not of type villager profession for" + minecraftKey);
            }
        }
    }
}

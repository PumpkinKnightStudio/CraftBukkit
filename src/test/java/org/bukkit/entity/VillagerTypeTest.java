package org.bukkit.entity;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.entity.npc.VillagerType;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.support.AbstractTestingBase;
import org.junit.Assert;
import org.junit.Test;

public class VillagerTypeTest extends AbstractTestingBase {

    @Test
    public void testBukkitToMinecraftFieldName() {
        for (Field field : Villager.Type.class.getFields()) {
            if (field.getType() != Villager.Type.class) {
                continue;
            }
            if (!Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            String name = field.getName();
            Assert.assertNotNull("No villager type for field name " + name, Registry.VILLAGER_TYPE.get(NamespacedKey.fromString(name.toLowerCase())));
        }
    }

    @Test
    public void testMinecraftToBukkitFieldName() {
        for (VillagerType villagerType : BuiltInRegistries.VILLAGER_TYPE) {
            MinecraftKey minecraftKey = BuiltInRegistries.VILLAGER_TYPE.getKey(villagerType);

            try {
                Villager.Type type = (Villager.Type) Villager.Type.class.getField(minecraftKey.getPath().toUpperCase()).get(null);

                Assert.assertEquals("Keys are not the same for " + minecraftKey, minecraftKey, CraftNamespacedKey.toMinecraft(type.getKey()));
            } catch (NoSuchFieldException e) {
                Assert.fail("No Bukkit default villager type for " + minecraftKey);
            } catch (IllegalAccessException e) {
                Assert.fail("Bukkit field is not access able for " + minecraftKey);
            } catch (ClassCastException e) {
                Assert.fail("Bukkit field is not of type villager type for" + minecraftKey);
            }
        }
    }
}

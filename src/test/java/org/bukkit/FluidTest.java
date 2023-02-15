package org.bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.material.FluidType;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.support.AbstractTestingBase;
import org.junit.Assert;
import org.junit.Test;

public class FluidTest extends AbstractTestingBase {

    @Test
    public void testBukkitToMinecraftFieldName() {
        for (Field field : Fluid.class.getFields()) {
            if (field.getType() != Fluid.class) {
                continue;
            }
            if (!Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            String name = field.getName();
            Assert.assertNotNull("No Fluid for field name " + name, Registry.FLUID.get(NamespacedKey.fromString(name.toLowerCase())));
        }
    }

    @Test
    public void testMinecraftToBukkitFieldName() {
        for (FluidType fluidType : BuiltInRegistries.FLUID) {
            MinecraftKey minecraftKey = BuiltInRegistries.FLUID.getKey(fluidType);

            try {
                Fluid fluid = (Fluid) Fluid.class.getField(minecraftKey.getPath().toUpperCase()).get(null);

                Assert.assertEquals("Keys are not the same for " + minecraftKey, minecraftKey, CraftNamespacedKey.toMinecraft(fluid.getKey()));
            } catch (NoSuchFieldException e) {
                Assert.fail("No Bukkit default fluid for " + minecraftKey);
            } catch (IllegalAccessException e) {
                Assert.fail("Bukkit field is not access able for " + minecraftKey);
            } catch (ClassCastException e) {
                Assert.fail("Bukkit field is not of type fluid for" + minecraftKey);
            }
        }
    }
}

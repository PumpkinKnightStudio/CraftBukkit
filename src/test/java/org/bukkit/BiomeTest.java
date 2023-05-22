package org.bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.biome.BiomeBase;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.block.CraftBiome;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.support.AbstractTestingBase;
import org.junit.Assert;
import org.junit.Test;

public class BiomeTest extends AbstractTestingBase {

    @Test
    public void testBukkitToMinecraftFieldName() {
        for (Field field : Biome.class.getFields()) {
            if (field.getType() != Biome.class) {
                continue;
            }
            if (!Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            String name = field.getName();

            if (name.equals("CUSTOM")) {
                continue;
            }

            Assert.assertNotNull("No Biome for field name " + name, Registry.BIOME.get(NamespacedKey.fromString(name.toLowerCase())));
        }
    }

    @Test
    public void testMinecraftToBukkitFieldName() {
        for (BiomeBase biomeBase : BIOMES) {
            MinecraftKey minecraftKey = BIOMES.getKey(biomeBase);

            try {
                Biome biome = (Biome) Biome.class.getField(minecraftKey.getPath().toUpperCase()).get(null);

                Assert.assertEquals("Keys are not the same for " + minecraftKey, minecraftKey, CraftNamespacedKey.toMinecraft(biome.getKey()));
            } catch (NoSuchFieldException e) {
                Assert.fail("No Bukkit default biome for " + minecraftKey);
            } catch (IllegalAccessException e) {
                Assert.fail("Bukkit field is not access able for " + minecraftKey);
            } catch (ClassCastException e) {
                Assert.fail("Bukkit field is not of type Biome for" + minecraftKey);
            }
        }
    }

    @Test
    public void testBukkitToMinecraft() {
        for (Biome biome : Biome.values()) {
            if (biome == Biome.CUSTOM) {
                continue;
            }

            Assert.assertNotNull("No NMS mapping for " + biome, CraftBiome.bukkitToMinecraft(BIOMES, biome));
        }
    }

    @Test
    public void testMinecraftToBukkit() {
        for (BiomeBase biomeBase : BIOMES) {
            // Should always return a biome, since we create the biome from the biome base
            Biome biome = CraftBiome.minecraftToBukkit(BIOMES, biomeBase);
            Assert.assertTrue("No Bukkit mapping for " + biomeBase, biome != null && biome != Biome.CUSTOM);
        }
    }
}

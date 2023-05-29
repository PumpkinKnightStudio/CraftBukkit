package org.bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.CraftParticle;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.material.MaterialData;
import org.bukkit.support.AbstractTestingBase;
import org.junit.Assert;
import org.junit.Test;

public class ParticleTest extends AbstractTestingBase {

    @Test
    public void testBukkitToMinecraftFieldName() {
        for (Field field : Particle.class.getFields()) {
            if (field.getType() != Particle.class) {
                continue;
            }
            if (!Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            String name = field.getName();
            Assert.assertNotNull("No particle for field name " + name, Registry.PARTICLE_TYPE.get(NamespacedKey.fromString(name.toLowerCase())));
        }
    }

    @Test
    public void testMinecraftToBukkitFieldName() {
        for (net.minecraft.core.particles.Particle particleType : BuiltInRegistries.PARTICLE_TYPE) {
            MinecraftKey minecraftKey = BuiltInRegistries.PARTICLE_TYPE.getKey(particleType);

            try {
                Particle particle = (Particle) Particle.class.getField(minecraftKey.getPath().toUpperCase()).get(null);

                Assert.assertEquals("Keys are not the same for " + minecraftKey, minecraftKey, CraftNamespacedKey.toMinecraft(particle.getKey()));
            } catch (NoSuchFieldException e) {
                Assert.fail("No Bukkit default particle for " + minecraftKey);
            } catch (IllegalAccessException e) {
                Assert.fail("Bukkit field is not access able for " + minecraftKey);
            } catch (ClassCastException e) {
                Assert.fail("Bukkit field is not of type particle for" + minecraftKey);
            }
        }
    }

    @Test
    public void verifyMapping() {
        for (Particle bukkit : Particle.values()) {
            Object data = null;
            if (bukkit.getDataType().equals(ItemStack.class)) {
                data = ItemStack.of(ItemType.STONE);
            } else if (bukkit.getDataType() == MaterialData.class) {
                data = new MaterialData(Material.LEGACY_STONE);
            } else if (bukkit.getDataType() == Particle.DustOptions.class) {
                data = new Particle.DustOptions(Color.BLACK, 0);
            } else if (bukkit.getDataType() == Particle.DustTransition.class) {
                data = new Particle.DustTransition(Color.BLACK, Color.WHITE, 0);
            } else if (bukkit.getDataType() == Vibration.class) {
                data = new Vibration(new Location(null, 0, 0, 0), new Vibration.Destination.BlockDestination(new Location(null, 0, 0, 0)), 0);
            } else if (bukkit.getDataType() == BlockData.class) {
                data = CraftBlockData.newData(BlockType.STONE, "");
            } else if (bukkit.getDataType() == Float.class) {
                data = 1.0F;
            } else if (bukkit.getDataType() == Integer.class) {
                data = 0;
            }

            Assert.assertNotNull("Missing Bukkit->NMS particle mapping for " + bukkit, ((CraftParticle) bukkit).createParticleParam(data));
        }
        for (net.minecraft.core.particles.Particle nms : BuiltInRegistries.PARTICLE_TYPE) {
            Assert.assertNotNull("Missing NMS->Bukkit particle mapping for " + BuiltInRegistries.PARTICLE_TYPE.getKey(nms), CraftParticle.minecraftToBukkit(nms));
        }
    }
}

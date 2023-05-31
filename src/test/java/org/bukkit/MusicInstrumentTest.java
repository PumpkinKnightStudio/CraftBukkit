package org.bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.item.Instrument;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.support.AbstractTestingBase;
import org.junit.Assert;
import org.junit.Test;

public class MusicInstrumentTest extends AbstractTestingBase {

    @Test
    public void testBukkitToMinecraftFieldName() {
        for (Field field : MusicInstrument.class.getFields()) {
            if (field.getType() != MusicInstrument.class) {
                continue;
            }
            if (!Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            String name = field.getName();
            Assert.assertNotNull("No MusicInstrument for field name " + name, Registry.INSTRUMENT.get(NamespacedKey.fromString(name.toLowerCase())));
        }
    }

    @Test
    public void testMinecraftToBukkitFieldName() {
        for (Instrument instrument : BuiltInRegistries.INSTRUMENT) {
            MinecraftKey minecraftKey = BuiltInRegistries.INSTRUMENT.getKey(instrument);

            try {
                MusicInstrument game = (MusicInstrument) MusicInstrument.class.getField(minecraftKey.getPath().toUpperCase()).get(null);

                Assert.assertEquals("Keys are not the same for " + minecraftKey, minecraftKey, CraftNamespacedKey.toMinecraft(game.getKey()));
            } catch (NoSuchFieldException e) {
                Assert.fail("No Bukkit default music instrument for " + minecraftKey);
            } catch (IllegalAccessException e) {
                Assert.fail("Bukkit field is not access able for " + minecraftKey);
            } catch (ClassCastException e) {
                Assert.fail("Bukkit field is not of type music instrument for" + minecraftKey);
            }
        }
    }
}

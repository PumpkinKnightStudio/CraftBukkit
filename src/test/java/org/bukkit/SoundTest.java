package org.bukkit;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.sounds.SoundEffect;
import org.bukkit.craftbukkit.CraftSound;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.support.AbstractTestingBase;
import org.junit.Assert;
import org.junit.Test;

public class SoundTest extends AbstractTestingBase {

    @Test
    public void testBukkitToMinecraftFieldName() {
        for (Field field : Sound.class.getFields()) {
            if (field.getType() != Sound.class) {
                continue;
            }
            if (!Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            try {
                NamespacedKey key = ((Keyed) field.get(null)).getKey();
                Assert.assertNotNull("No Sound for field name " + field.getName(), Registry.SOUNDS.get(key));
            } catch (IllegalAccessException e) {
                Assert.fail("Can't get object for Bukkit field " + field.getName());
            }
        }
    }

    @Test
    public void testMinecraftToBukkitFieldName() {
        for (SoundEffect soundEffect : BuiltInRegistries.SOUND_EVENT) {
            MinecraftKey minecraftKey = BuiltInRegistries.SOUND_EVENT.getKey(soundEffect);

            try {
                Sound sound = (Sound) Sound.class.getField(minecraftKey.getPath().toUpperCase().replace(".", "_")).get(null);

                Assert.assertEquals("Keys are not the same for " + minecraftKey, minecraftKey, CraftNamespacedKey.toMinecraft(sound.getKey()));
            } catch (NoSuchFieldException e) {
                Assert.fail("No Bukkit default sound for " + minecraftKey);
            } catch (IllegalAccessException e) {
                Assert.fail("Bukkit field is not access able for " + minecraftKey);
            } catch (ClassCastException e) {
                Assert.fail("Bukkit field is not of type sound for" + minecraftKey);
            }
        }
    }

    @Test
    public void testGetSound() {
        for (Sound sound : Sound.values()) {
            assertThat(sound.name(), CraftSound.getSoundEffect(sound), is(not(nullValue())));
        }
    }

    @Test
    public void testReverse() {
        for (MinecraftKey effect : BuiltInRegistries.SOUND_EVENT.keySet()) {
            assertNotNull(effect + "", Sound.valueOf(effect.getPath().replace('.', '_').toUpperCase(java.util.Locale.ENGLISH)));
        }
    }

    @Test
    public void testCategory() {
        for (SoundCategory category : SoundCategory.values()) {
            assertNotNull(category + "", net.minecraft.sounds.SoundCategory.valueOf(category.name()));
        }
    }

    @Test
    public void testCategoryReverse() {
        for (net.minecraft.sounds.SoundCategory category : net.minecraft.sounds.SoundCategory.values()) {
            assertNotNull(category + "", SoundCategory.valueOf(category.name()));
        }
    }
}

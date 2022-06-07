package org.bukkit;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import com.google.common.collect.Lists;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.decoration.PaintingVariant;
import org.bukkit.craftbukkit.CraftArt;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.support.AbstractTestingBase;
import org.junit.Assert;
import org.junit.Test;

public class ArtTest extends AbstractTestingBase {
    private static final int UNIT_MULTIPLIER = 16;

    @Test
    public void testBukkitToMinecraftFieldName() {
        for (Field field : Art.class.getFields()) {
            if (field.getType() != Art.class) {
                continue;
            }
            if (!Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            String name = field.getName();
            Assert.assertNotNull("No Art for field name " + name, Registry.ART.get(NamespacedKey.fromString(name.toLowerCase())));
        }
    }

    @Test
    public void testMinecraftToBukkitFieldName() {
        for (PaintingVariant painting : IRegistry.PAINTING_VARIANT) {
            MinecraftKey minecraftKey = IRegistry.PAINTING_VARIANT.getKey(painting);

            try {
                Art art = (Art) Art.class.getField(minecraftKey.getPath().toUpperCase()).get(null);

                Assert.assertEquals("Keys are not the same for " + minecraftKey, minecraftKey, CraftNamespacedKey.toMinecraft(art.getKey()));
            } catch (NoSuchFieldException e) {
                Assert.fail("No Bukkit default art for " + minecraftKey);
            } catch (IllegalAccessException e) {
                Assert.fail("Bukkit field is not access able for " + minecraftKey);
            } catch (ClassCastException e) {
                Assert.fail("Bukkit field is not of type art for" + minecraftKey);
            }
        }
    }

    @Test
    public void verifyMapping() {
        List<Art> arts = Lists.newArrayList(Art.values());

        for (ResourceKey<PaintingVariant> key : IRegistry.PAINTING_VARIANT.registryKeySet()) {
            Holder<PaintingVariant> enumArt = IRegistry.PAINTING_VARIANT.getHolderOrThrow(key);
            String name = key.location().getPath();
            int width = enumArt.value().getWidth() / UNIT_MULTIPLIER;
            int height = enumArt.value().getHeight() / UNIT_MULTIPLIER;

            Art subject = CraftArt.minecraftToBukkit(enumArt);

            String message = String.format("org.bukkit.Art is missing '%s'", name);
            assertNotNull(message, subject);

            assertThat(Art.getByName(name), is(subject));
            assertThat("Art." + subject + "'s width", subject.getBlockWidth(), is(width));
            assertThat("Art." + subject + "'s height", subject.getBlockHeight(), is(height));

            arts.remove(subject);
        }

        assertThat("org.bukkit.Art has too many arts", arts, is(Collections.EMPTY_LIST));
    }

    @Test
    public void testCraftArtToNotch() {
        Map<Holder<PaintingVariant>, Art> cache = new HashMap<>();
        for (Art art : Art.values()) {
            Holder<PaintingVariant> enumArt = CraftArt.bukkitToMinecraft(art);
            assertNotNull(art.name(), enumArt);
            assertThat(art.name(), cache.put(enumArt, art), is(nullValue()));
        }
    }

    @Test
    public void testCraftArtToBukkit() {
        Map<Art, Holder<PaintingVariant>> cache = new HashMap<>();
        for (Holder<PaintingVariant> enumArt : IRegistry.PAINTING_VARIANT.asHolderIdMap()) {
            Art art = CraftArt.minecraftToBukkit(enumArt);
            assertNotNull("Could not CraftArt.NotchToBukkit " + enumArt, art);
            assertThat("Duplicate artwork " + enumArt, cache.put(art, enumArt), is(nullValue()));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void getByNullName() {
        Art.getByName(null);
    }

    @Test
    public void getById() {
        for (Art art : Art.values()) {
            assertThat(Art.getById(art.getId()), is(art));
        }
    }

    @Test
    public void getByName() {
        for (Art art : Art.values()) {
            assertThat(Art.getByName(art.toString()), is(art));
        }
    }

    @Test
    public void getByNameWithMixedCase() {
        Art subject = Art.values()[0];
        String name = subject.toString().replace('E', 'e');

        assertThat(Art.getByName(name), is(subject));
    }
}

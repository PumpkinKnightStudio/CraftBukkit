package org.bukkit.block.banner;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.block.entity.EnumBannerPatternType;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.support.AbstractTestingBase;
import org.junit.Assert;
import org.junit.Test;

public class PatternTypeTest extends AbstractTestingBase {

    @Test
    public void testBukkitToMinecraftFieldName() {
        for (Field field : PatternType.class.getFields()) {
            if (field.getType() != PatternType.class) {
                continue;
            }
            if (!Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            String name = field.getName();
            org.junit.Assert.assertNotNull("No pattern type for field name " + name, Registry.BANNER_PATTERN.get(NamespacedKey.fromString(name.toLowerCase())));
        }
    }

    @Test
    public void testMinecraftToBukkitFieldName() {
        for (EnumBannerPatternType bannerPatternType : BuiltInRegistries.BANNER_PATTERN) {
            MinecraftKey minecraftKey = BuiltInRegistries.BANNER_PATTERN.getKey(bannerPatternType);

            try {
                PatternType patternType = (PatternType) PatternType.class.getField(minecraftKey.getPath().toUpperCase()).get(null);

                org.junit.Assert.assertEquals("Keys are not the same for " + minecraftKey, minecraftKey, CraftNamespacedKey.toMinecraft(patternType.getKey()));
            } catch (NoSuchFieldException e) {
                org.junit.Assert.fail("No Bukkit default pattern type for " + minecraftKey);
            } catch (IllegalAccessException e) {
                org.junit.Assert.fail("Bukkit field is not access able for " + minecraftKey);
            } catch (ClassCastException e) {
                Assert.fail("Bukkit field is not of type pattern type for" + minecraftKey);
            }
        }
    }
}

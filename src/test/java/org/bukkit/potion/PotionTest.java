package org.bukkit.potion;

import static org.junit.Assert.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectList;
import net.minecraft.world.item.alchemy.PotionRegistry;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.support.AbstractTestingBase;
import org.junit.Assert;
import org.junit.Test;

public class PotionTest extends AbstractTestingBase {

    @Test
    public void testBukkitToMinecraftFieldName() {
        for (Field field : PotionEffectType.class.getFields()) {
            if (field.getType() != PotionEffectType.class) {
                continue;
            }
            if (!Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            String name = field.getName();
            Assert.assertNotNull("No potionEffectType for field name " + name, Registry.POTION_EFFECT_TYPE.get(NamespacedKey.fromString(name.toLowerCase())));
        }
    }

    @Test
    public void testMinecraftToBukkitFieldName() {
        for (MobEffectList mobEffectList : IRegistry.MOB_EFFECT) {
            MinecraftKey minecraftKey = IRegistry.MOB_EFFECT.getKey(mobEffectList);

            try {
                PotionEffectType potionEffectType = (PotionEffectType) PotionEffectType.class.getField(minecraftKey.getPath().toUpperCase()).get(null);

                Assert.assertEquals("Keys are not the same for " + minecraftKey, minecraftKey, CraftNamespacedKey.toMinecraft(potionEffectType.getKey()));
            } catch (NoSuchFieldException e) {
                Assert.fail("No Bukkit default potionEffectType for " + minecraftKey);
            } catch (IllegalAccessException e) {
                Assert.fail("Bukkit field is not access able for " + minecraftKey);
            } catch (ClassCastException e) {
                Assert.fail("Bukkit field is not of type potionEffectType for" + minecraftKey);
            }
        }
    }

    @Test
    public void testEffectCompleteness() throws Throwable {
        Map<PotionType, String> effects = new EnumMap(PotionType.class);
        for (Object reg : IRegistry.POTION) {
            List<MobEffect> eff = ((PotionRegistry) reg).getEffects();
            if (eff.size() != 1) continue;
            int id = MobEffectList.getId(eff.get(0).getEffect());
            PotionEffectType type = PotionEffectType.getById(id);
            assertNotNull(String.valueOf(id), PotionEffectType.getById(id));

            PotionType enumType = PotionType.getByEffect(type);
            assertNotNull(type.getName(), enumType);

            effects.put(enumType, enumType.name());
        }

        assertEquals(effects.entrySet().size(), PotionType.values().length - /* PotionTypes with no/shared Effects */ 6);
    }

    @Test
    public void testEffectType() {
        for (MobEffectList nms : IRegistry.MOB_EFFECT) {
            MinecraftKey key = IRegistry.MOB_EFFECT.getKey(nms);

            int id = MobEffectList.getId(nms);
            PotionEffectType bukkit = PotionEffectType.getById(id);

            assertNotNull("No Bukkit type for " + key, bukkit);
            assertFalse("No name for " + key, bukkit.getName().contains("UNKNOWN"));

            PotionEffectType byName = PotionEffectType.getByName(bukkit.getName());
            assertEquals("Same type not returned by name " + key, bukkit, byName);
        }
    }
}

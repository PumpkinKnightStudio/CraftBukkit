package org.bukkit.entity;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.entity.EntityTypes;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.support.AbstractTestingBase;
import org.junit.Assert;
import org.junit.Test;

public class EntityTypesTest extends AbstractTestingBase {

    @Test
    public void testBukkitToMinecraftFieldName() {
        for (Field field : EntityType.class.getFields()) {
            if (field.getType() != EntityType.class) {
                continue;
            }
            if (!Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            String name = field.getName();
            Assert.assertNotNull("No entityType for field name " + name, Registry.ENTITY_TYPE.get(NamespacedKey.fromString(name.toLowerCase())));
        }
    }

    @Test
    public void testMinecraftToBukkitFieldName() {
        for (EntityTypes<?> entityTypes : BuiltInRegistries.ENTITY_TYPE) {
            MinecraftKey minecraftKey = BuiltInRegistries.ENTITY_TYPE.getKey(entityTypes);

            try {
                EntityType entityType = (EntityType) EntityType.class.getField(minecraftKey.getPath().toUpperCase()).get(null);

                Assert.assertEquals("Keys are not the same for " + minecraftKey, minecraftKey, CraftNamespacedKey.toMinecraft(entityType.getKey()));
            } catch (NoSuchFieldException e) {
                Assert.fail("No Bukkit default entityType for " + minecraftKey);
            } catch (IllegalAccessException e) {
                Assert.fail("Bukkit field is not access able for " + minecraftKey);
            } catch (ClassCastException e) {
                Assert.fail("Bukkit field is not of type entityType for" + minecraftKey);
            }
        }
    }

    @Test
    public void testClasses() {
       for (EntityType entityType : Registry.ENTITY_TYPE) {
           if (entityType == EntityType.UNKNOWN) {
               continue;
           }

           Assert.assertNotNull("No entity class for " + entityType.getKey(), entityType.getEntityClass());
       }
    }

    @Test
    public void testMaps() {
        Set<EntityType> allBukkit = Arrays.stream(EntityType.values()).filter((b) -> b != EntityType.UNKNOWN).collect(Collectors.toSet());

        for (EntityTypes<?> nms : BuiltInRegistries.ENTITY_TYPE) {
            MinecraftKey key = EntityTypes.getKey(nms);

            EntityType bukkit = EntityType.fromName(key.getPath());
            Assert.assertNotNull("Missing nms->bukkit " + key, bukkit);

            Assert.assertTrue("Duplicate entity nms->" + bukkit, allBukkit.remove(bukkit));
        }

        Assert.assertTrue("Unmapped bukkit entities " + allBukkit, allBukkit.isEmpty());
    }

    @Test
    public void testTranslationKey() {
        for (EntityType entityType : EntityType.values()) {
            // Currently EntityType#getTranslationKey has a validation for null name then for test skip this and check correct names.
            if (entityType.getName() != null) {
                Assert.assertNotNull("Nulllable translation key for " + entityType, entityType.getTranslationKey());
            }
        }
    }
}

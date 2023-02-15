package org.bukkit;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.bukkit.craftbukkit.legacy.CraftLegacyMaterial;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.material.MaterialData;
import org.bukkit.support.AbstractTestingBase;
import org.junit.Assert;
import org.junit.Test;

public class MaterialTest extends AbstractTestingBase {

    @Test
    public void testBukkitToMinecraftFieldName() {
        for (Field field : Material.class.getFields()) {
            if (field.getType() != Material.class) {
                continue;
            }
            if (!Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            if (field.getAnnotation(Deprecated.class) != null) {
                continue;
            }

            String name = field.getName();
            Assert.assertNotNull("No Material for field name " + name, Registry.MATERIAL.get(NamespacedKey.fromString(name.toLowerCase())));
        }
    }

    @Test
    public void testMinecraftToBukkitFieldName() {
        for (Item item : IRegistry.ITEM) {
            test(IRegistry.ITEM.getKey(item));
        }

        for (Block block : IRegistry.BLOCK) {
            test(IRegistry.BLOCK.getKey(block));
        }
    }

    private void test(MinecraftKey minecraftKey) {
        try {
            Material material = (Material) Material.class.getField(minecraftKey.getPath().toUpperCase()).get(null);

            Assert.assertEquals("Keys are not the same for " + minecraftKey, minecraftKey, CraftNamespacedKey.toMinecraft(material.getKey()));
        } catch (NoSuchFieldException e) {
            Assert.fail("No Bukkit default material for " + minecraftKey);
        } catch (IllegalAccessException e) {
            Assert.fail("Bukkit field is not access able for " + minecraftKey);
        } catch (ClassCastException e) {
            Assert.fail("Bukkit field is not of type material for" + minecraftKey);
        }
    }

    @Test
    public void getByName() {
        for (Material material : Registry.MATERIAL) {
            assertThat(Material.getMaterial(material.toString()), is(material));
        }
    }

    @Test
    public void getData() {
        for (Material material : Registry.MATERIAL) {
            if (!material.isLegacy()) {
                continue;
            }
            Class<? extends MaterialData> clazz = material.getData();

            assertThat(material.getNewData((byte) 0), is(instanceOf(clazz)));
        }
    }

    @Test
    public void matchMaterialByName() {
        for (Material material : Registry.MATERIAL) {
            assertThat(Material.matchMaterial(material.toString()), is(material));
        }
    }

    @Test
    public void matchMaterialByKey() {
        for (Material material : Registry.MATERIAL) {
            if (material.isLegacy()) {
                continue;
            }
            assertThat(Material.matchMaterial(material.getKey().toString()), is(material));
        }
    }

    @Test
    public void matchMaterialByWrongNamespace() {
        for (Material material : Registry.MATERIAL) {
            if (material.isLegacy()) {
                continue;
            }
            assertNull(Material.matchMaterial("bogus:" + material.getKey().getKey()));
        }
    }

    @Test
    public void matchMaterialByLowerCaseAndSpaces() {
        for (Material material : Registry.MATERIAL) {
            String name = material.toString().replaceAll("_", " ").toLowerCase(java.util.Locale.ENGLISH);
            assertThat(Material.matchMaterial(name), is(material));
        }
    }

    @Test
    public void verifyMapping() {
        Map<MinecraftKey, Material> materials = Maps.newHashMap();
        for (Iterator<Material> it = Iterators.concat(Registry.MATERIAL.iterator(), CraftLegacyMaterial.getLegacyMaterials().iterator()); it.hasNext(); ) {
            Material material = it.next();
            if (INVALIDATED_MATERIALS.contains(material)) {
                continue;
            }

            materials.put(CraftMagicNumbers.key(material), material);
        }

        Iterator<Item> items = BuiltInRegistries.ITEM.iterator();

        while (items.hasNext()) {
            Item item = items.next();
            if (item == null) continue;

            MinecraftKey id = BuiltInRegistries.ITEM.getKey(item);
            String name = item.getDescriptionId();

            Material material = materials.remove(id);

            assertThat("Missing " + name + "(" + id + ")", material, is(not(nullValue())));
            assertNotNull("No item mapping for " + name, CraftMagicNumbers.getMaterial(item));
        }

        assertThat(materials, is(Collections.EMPTY_MAP));
    }

    @Test
    public void verifyMaterialOrder() {
        Material[] materials = Lists.newArrayList(Iterators.concat(Registry.MATERIAL.iterator(), CraftLegacyMaterial.getLegacyMaterials().iterator())).toArray(new Material[0]);
        List<Material> expectedOrder = new ArrayList<>(materials.length);

        // Start with items in the same order as BuiltInRegistries.ITEM
        StreamSupport.stream(BuiltInRegistries.ITEM.spliterator(), false)
                .map(CraftMagicNumbers::getMaterial)
                .forEach(expectedOrder::add);

        // Then non-item blocks in the same order as BuiltInRegistries.BLOCK
        StreamSupport.stream(BuiltInRegistries.BLOCK.spliterator(), false)
                .map(CraftMagicNumbers::getMaterial)
                .filter(block -> !block.isItem())
                .forEach(expectedOrder::add);

        // Then legacy materials in order of ID
        Arrays.stream(materials)
                .filter(Material::isLegacy)
                .sorted(Comparator.comparingInt(Material::getId))
                .forEach(expectedOrder::add);

        assertArrayEquals(expectedOrder.toArray(), materials);
    }
}

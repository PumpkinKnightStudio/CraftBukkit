package org.bukkit.block;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.block.Block;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.support.AbstractTestingBase;
import org.junit.Assert;
import org.junit.Test;

public class BlockTypeTest extends AbstractTestingBase {

    @Test
    public void testBukkitToMinecraftFieldName() {
        for (Field field : BlockType.class.getFields()) {
            if (field.getType() != BlockType.class) {
                continue;
            }
            if (!Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            String name = field.getName();
            Assert.assertNotNull("No BlockType for field name " + name, Registry.MATERIAL.get(NamespacedKey.fromString(name.toLowerCase())));
        }
    }

    @Test
    public void testMinecraftToBukkitFieldName() {
        for (Block block : IRegistry.BLOCK) {
            MinecraftKey minecraftKey = IRegistry.BLOCK.getKey(block);

            try {
                Field field = BlockType.class.getField(minecraftKey.getPath().toUpperCase());
                Assert.assertSame("No Bukkit default blockType for " + minecraftKey, BlockType.class, field.getType());
                BlockType<?> blockType = (BlockType<?>) field.get(null);

                Assert.assertEquals("Keys are not the same for " + minecraftKey, minecraftKey, CraftNamespacedKey.toMinecraft(blockType.getKey()));
            } catch (NoSuchFieldException e) {
                Assert.fail("No Bukkit default blockType for " + minecraftKey);
            } catch (IllegalAccessException e) {
                Assert.fail("Bukkit field is not access able for " + minecraftKey);
            } catch (ClassCastException e) {
                Assert.fail("Bukkit field is not of type blockType for " + minecraftKey);
            }
        }
    }
}

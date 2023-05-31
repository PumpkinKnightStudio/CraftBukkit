package org.bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.support.AbstractTestingBase;
import org.junit.Assert;
import org.junit.Test;

public class GameEventTest extends AbstractTestingBase {

    @Test
    public void testBukkitToMinecraftFieldName() {
        for (Field field : GameEvent.class.getFields()) {
            if (field.getType() != GameEvent.class) {
                continue;
            }
            if (!Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            String name = field.getName();
            Assert.assertNotNull("No GameEvent for field name " + name, Registry.GAME_EVENT.get(NamespacedKey.fromString(name.toLowerCase())));
        }
    }

    @Test
    public void testMinecraftToBukkitFieldName() {
        for (net.minecraft.world.level.gameevent.GameEvent gameEvent : BuiltInRegistries.GAME_EVENT) {
            MinecraftKey minecraftKey = BuiltInRegistries.GAME_EVENT.getKey(gameEvent);

            try {
                GameEvent game = (GameEvent) GameEvent.class.getField(minecraftKey.getPath().toUpperCase()).get(null);

                Assert.assertEquals("Keys are not the same for " + minecraftKey, minecraftKey, CraftNamespacedKey.toMinecraft(game.getKey()));
            } catch (NoSuchFieldException e) {
                Assert.fail("No Bukkit default game event for " + minecraftKey);
            } catch (IllegalAccessException e) {
                Assert.fail("Bukkit field is not access able for " + minecraftKey);
            } catch (ClassCastException e) {
                Assert.fail("Bukkit field is not of type game event for" + minecraftKey);
            }
        }
    }
}

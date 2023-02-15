package org.bukkit;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import com.google.common.collect.HashMultiset;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.stats.StatisticWrapper;
import net.minecraft.world.entity.EntityTypes;
import org.bukkit.craftbukkit.CraftStatistic;
import org.bukkit.entity.EntityType;
import org.bukkit.support.AbstractTestingBase;
import org.junit.Assert;
import org.junit.Test;

public class StatisticsAndAchievementsTest extends AbstractTestingBase {

    @Test
    public void testBukkitToMinecraftFieldName() {
        for (Field field : Statistic.class.getFields()) {
            if (field.getType() != Statistic.class) {
                continue;
            }
            if (!Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            String name = field.getName();
            Assert.assertNotNull("No Statistic for field name " + name, Registry.STATISTIC.get(NamespacedKey.fromString(name.toLowerCase())));
        }
    }

    @Test
    public void testMinecraftToBukkitFieldName() {
        for (StatisticWrapper<?> statisticWrapper : IRegistry.STAT_TYPE) {
            for (net.minecraft.stats.Statistic<?> minecraft : statisticWrapper) {
                NamespacedKey bukkit = CraftStatistic.getBukkitStatistic(minecraft).getKey();

                try {
                    Statistic statistic = (Statistic) Statistic.class.getField(bukkit.getKey().toUpperCase()).get(null);

                    Assert.assertEquals("Keys are not the same for " + bukkit, bukkit, statistic.getKey());
                } catch (NoSuchFieldException e) {
                    Assert.fail("No Bukkit default statistic for " + bukkit);
                } catch (IllegalAccessException e) {
                    Assert.fail("Bukkit field is not access able for " + bukkit);
                } catch (ClassCastException e) {
                    Assert.fail("Bukkit field is not of type art for " + bukkit);
                }
            }
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void verifyEntityMapping() throws Throwable {
        for (Statistic statistic : Statistic.values()) {
            if (statistic.getType() == Statistic.Type.ENTITY) {
                for (EntityType entity : EntityType.values()) {
                    if (entity.getName() != null) {
                        assertNotNull(statistic + " missing for " + entity, CraftStatistic.getEntityStatistic(statistic, entity));
                    }
                }
            }
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void verifyStatisticMapping() throws Throwable {
        HashMultiset<Statistic> statistics = HashMultiset.create();
        for (StatisticWrapper wrapper : BuiltInRegistries.STAT_TYPE) {
            for (Object child : wrapper.getRegistry()) {
                net.minecraft.stats.Statistic<?> statistic = wrapper.get(child);
                String message = String.format("org.bukkit.Statistic is missing: '%s'", statistic);

                Statistic subject = CraftStatistic.getBukkitStatistic(statistic);
                assertThat(message, subject, is(not(nullValue())));

                if (wrapper.getRegistry() == BuiltInRegistries.BLOCK || wrapper.getRegistry() == BuiltInRegistries.ITEM) {
                    assertNotNull("Material type map missing for " + wrapper.getRegistry().getKey(child), CraftStatistic.getMaterialFromStatistic(statistic));
                } else if (wrapper.getRegistry() == BuiltInRegistries.ENTITY_TYPE) {
                    assertNotNull("Entity type map missing for " + EntityTypes.getKey((EntityTypes<?>) child), CraftStatistic.getEntityTypeFromStatistic((net.minecraft.stats.Statistic<EntityTypes<?>>) statistic));
                }

                statistics.add(subject);
            }
        }

        for (Statistic statistic : Statistic.values()) {
            String message = String.format("org.bukkit.Statistic.%s does not have a corresponding minecraft statistic", statistic.name());
            assertThat(message, statistics.remove(statistic, statistics.count(statistic)), is(greaterThan(0)));
        }
    }
}

package org.bukkit;

import static org.bukkit.support.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import com.google.common.collect.HashMultiset;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.stats.StatisticWrapper;
import net.minecraft.world.entity.EntityTypes;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftStatistic;
import org.bukkit.entity.EntityType;
import org.bukkit.support.AbstractTestingBase;
import org.junit.jupiter.api.Test;

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
            assertNotNull(Registry.STATISTIC.get(NamespacedKey.fromString(name.toLowerCase())), "No Statistic for field name " + name);
        }
    }

    @Test
    public void testMinecraftToBukkitFieldName() {
        for (StatisticWrapper<?> statisticWrapper : BuiltInRegistries.STAT_TYPE) {
            for (net.minecraft.stats.Statistic<?> minecraft : statisticWrapper) {
                NamespacedKey bukkit = CraftStatistic.getBukkitStatistic(BuiltInRegistries.STAT_TYPE, minecraft).getKey();

                try {
                    Statistic statistic = (Statistic) Statistic.class.getField(bukkit.getKey().toUpperCase()).get(null);

                    assertEquals(bukkit, statistic.getKey(), "Keys are not the same for " + bukkit);
                } catch (NoSuchFieldException e) {
                    fail("No Bukkit default statistic for " + bukkit);
                } catch (IllegalAccessException e) {
                    fail("Bukkit field is not access able for " + bukkit);
                } catch (ClassCastException e) {
                    fail("Bukkit field is not of type art for " + bukkit);
                }
            }
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void verifyEntityMapping() throws Throwable {
        for (Statistic statistic : Statistic.values()) {
            if (statistic.getType() == Statistic.Type.ENTITY) {
                for (EntityType<?> entity : EntityType.values()) {
                    if (entity.getName() != null) {
                        assertNotNull(CraftStatistic.getEntityStatistic(statistic, entity), statistic + " missing for " + entity);
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

                Statistic subject = CraftStatistic.getBukkitStatistic(CraftRegistry.getMinecraftRegistry(Registries.STAT_TYPE), statistic);
                assertThat(subject, is(not(nullValue())), message);

                if (wrapper.getRegistry() == BuiltInRegistries.ITEM) {
                    assertNotNull(CraftStatistic.getItemTypeFromStatistic(statistic), "Item type map missing for " + wrapper.getRegistry().getKey(child));
                } else if (wrapper.getRegistry() == BuiltInRegistries.BLOCK) {
                    assertNotNull(CraftStatistic.getBlockTypeFromStatistic(statistic), "Block type map missing for " + wrapper.getRegistry().getKey(child));
                } else if (wrapper.getRegistry() == BuiltInRegistries.ENTITY_TYPE) {
                    assertNotNull(CraftStatistic.getEntityTypeFromStatistic((net.minecraft.stats.Statistic<EntityTypes<?>>) statistic), "Entity type map missing for " + EntityTypes.getKey((EntityTypes<?>) child));
                }

                statistics.add(subject);
            }
        }

        for (Statistic statistic : Statistic.values()) {
            String message = String.format("org.bukkit.Statistic.%s does not have a corresponding minecraft statistic", statistic.name());
            assertThat(statistics.remove(statistic, statistics.count(statistic)), is(greaterThan(0)), message);
        }
    }
}

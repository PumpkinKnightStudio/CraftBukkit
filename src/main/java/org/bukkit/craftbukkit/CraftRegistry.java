package org.bukkit.craftbukkit;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.registries.Registries;
import org.bukkit.Art;
import org.bukkit.Fluid;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.attribute.CraftAttribute;
import org.bukkit.craftbukkit.block.CraftBiome;
import org.bukkit.craftbukkit.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.entity.CraftEntityType;
import org.bukkit.craftbukkit.entity.CraftVillager;
import org.bukkit.craftbukkit.generator.strucutre.CraftStructure;
import org.bukkit.craftbukkit.generator.strucutre.CraftStructureType;
import org.bukkit.craftbukkit.potion.CraftPotionEffectType;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.generator.structure.Structure;
import org.bukkit.generator.structure.StructureType;
import org.bukkit.potion.PotionEffectType;

public class CraftRegistry<B extends Keyed, M> implements Registry<B> {

    public static <B extends Keyed> Registry<?> createRegistry(Class<B> bukkitClass, IRegistryCustom registryHolder) {
        if (bukkitClass == Structure.class) {
            return new CraftRegistry<>(registryHolder.registryOrThrow(Registries.STRUCTURE), CraftStructure::new);
        }
        if (bukkitClass == StructureType.class) {
            return new CraftRegistry<>(registryHolder.registryOrThrow(Registries.STRUCTURE_TYPE), CraftStructureType::new);
        }
        if (bukkitClass == Biome.class) {
            return new CraftBiome.CraftBiomeRegistry(registryHolder.registryOrThrow(Registries.BIOME), CraftBiome::new);
        }
        if (bukkitClass == Art.class) {
            return new CraftRegistry<>(registryHolder.registryOrThrow(Registries.PAINTING_VARIANT), CraftArt::new);
        }
        if (bukkitClass == Fluid.class) {
            return new CraftRegistry<>(registryHolder.registryOrThrow(Registries.FLUID), CraftFluid::new);
        }
        if (bukkitClass == EntityType.class) {
            return new CraftEntityType.CraftEntityTypeRegistry(registryHolder.registryOrThrow(Registries.ENTITY_TYPE));
        }
        if (bukkitClass == Attribute.class) {
            return new CraftRegistry<>(registryHolder.registryOrThrow(Registries.ATTRIBUTE), CraftAttribute::new);
        }
        if (bukkitClass == Villager.Type.class) {
            return new CraftRegistry<>(registryHolder.registryOrThrow(Registries.VILLAGER_TYPE), CraftVillager.CraftType::new);
        }
        if (bukkitClass == Villager.Profession.class) {
            return new CraftRegistry<>(registryHolder.registryOrThrow(Registries.VILLAGER_PROFESSION), CraftVillager.CraftProfession::new);
        }
        if (bukkitClass == PotionEffectType.class) {
            return new CraftPotionEffectType.CraftPotionEffectTypeRegistry(registryHolder.registryOrThrow(Registries.MOB_EFFECT), CraftPotionEffectType::new);
        }
        if (bukkitClass == Enchantment.class) {
            return new CraftEnchantment.CraftEnchantmentRegistry(registryHolder.registryOrThrow(Registries.ENCHANTMENT), CraftEnchantment::new);
        }
        if (bukkitClass == Sound.class) {
            return new CraftRegistry<>(registryHolder.registryOrThrow(Registries.SOUND_EVENT), CraftSound::new);
        }
        if (bukkitClass == Material.class) {
            return new CraftMaterial.CraftMaterialRegistry(registryHolder.registryOrThrow(Registries.BLOCK), registryHolder.registryOrThrow(Registries.ITEM));
        }
        if (bukkitClass == Statistic.class) {
            return new CraftStatistic.CraftStatisticRegistry(registryHolder.registryOrThrow(Registries.STAT_TYPE));
        }

        return null;
    }

    private final Map<NamespacedKey, B> cache = new HashMap<>();
    private final IRegistry<M> minecraftRegistry;
    private final BiFunction<NamespacedKey, M, B> minecraftToBukkit;

    public CraftRegistry(IRegistry<M> minecraftRegistry, BiFunction<NamespacedKey, M, B> minecraftToBukkit) {
        this.minecraftRegistry = minecraftRegistry;
        this.minecraftToBukkit = minecraftToBukkit;
    }

    @Override
    public B get(NamespacedKey namespacedKey) {
        B cached = cache.get(namespacedKey);
        if (cached != null) {
            return cached;
        }

        B bukkit = createBukkit(namespacedKey, minecraftRegistry.getOptional(CraftNamespacedKey.toMinecraft(namespacedKey)).orElse(null));
        if (bukkit == null) {
            return null;
        }

        cache.put(namespacedKey, bukkit);

        return bukkit;
    }

    @Override
    public Iterator<B> iterator() {
        return values().iterator();
    }

    public B createBukkit(NamespacedKey namespacedKey, M minecraft) {
        if (minecraft == null) {
            return null;
        }

        return minecraftToBukkit.apply(namespacedKey, minecraft);
    }

    public Stream<B> values() {
        return minecraftRegistry.keySet().stream().map(minecraftKey -> get(CraftNamespacedKey.fromMinecraft(minecraftKey)));
    }
}

package org.bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockType;
import org.bukkit.block.banner.PatternType;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Cat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Frog;
import org.bukkit.entity.Villager;
import org.bukkit.generator.structure.Structure;
import org.bukkit.generator.structure.StructureType;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.support.AbstractTestingBase;
import org.junit.Assert;
import org.junit.Test;

public class RegistryConstantsTest extends AbstractTestingBase {

    @Test
    public void testStructure() {
        this.testExcessConstants(Structure.class, Registry.STRUCTURE);
        this.testMissingConstants(Structure.class, Registries.STRUCTURE);
    }

    @Test
    public void testStructureType() {
        this.testExcessConstants(StructureType.class, Registry.STRUCTURE_TYPE);
        this.testMissingConstants(StructureType.class, Registries.STRUCTURE_TYPE);
    }

    @Test
    public void testBiome() {
        this.testExcessConstants(Biome.class, Registry.BIOME);
        this.testMissingConstants(Biome.class, Registries.BIOME);
    }

    @Test
    public void testArt() {
        this.testExcessConstants(Art.class, Registry.ART);
        this.testMissingConstants(Art.class, Registries.PAINTING_VARIANT);
    }

    @Test
    public void testFluid() {
        this.testExcessConstants(Fluid.class, Registry.FLUID);
        this.testMissingConstants(Fluid.class, Registries.FLUID);
    }

    @Test
    public void testEntityType() {
        this.testExcessConstants(EntityType.class, Registry.ENTITY_TYPE);
        this.testMissingConstants(EntityType.class, Registries.ENTITY_TYPE);
    }

    @Test
    public void testAttribute() {
        this.testExcessConstants(Attribute.class, Registry.ATTRIBUTE);
        this.testMissingConstants(Attribute.class, Registries.ATTRIBUTE);
    }

    @Test
    public void testVillagerType() {
        this.testExcessConstants(Villager.Type.class, Registry.VILLAGER_TYPE);
        this.testMissingConstants(Villager.Type.class, Registries.VILLAGER_TYPE);
    }

    @Test
    public void testVillagerProfession() {
        this.testExcessConstants(Villager.Profession.class, Registry.VILLAGER_PROFESSION);
        this.testMissingConstants(Villager.Profession.class, Registries.VILLAGER_PROFESSION);
    }

    @Test
    public void testPotionEffectType() {
        this.testExcessConstants(PotionEffectType.class, Registry.POTION_EFFECT_TYPE);
        this.testMissingConstants(PotionEffectType.class, Registries.MOB_EFFECT);
    }

    @Test
    public void testEnchantment() {
        this.testExcessConstants(Enchantment.class, Registry.ENCHANTMENT);
        this.testMissingConstants(Enchantment.class, Registries.ENCHANTMENT);
    }

    @Test
    public void testSound() {
        this.testExcessConstants(Sound.class, Registry.SOUNDS);
        this.testMissingConstants(Sound.class, Registries.SOUND_EVENT);
    }

    @Test
    public void testTrimMaterial() {
        this.testExcessConstants(TrimMaterial.class, Registry.TRIM_MATERIAL);
        this.testMissingConstants(TrimMaterial.class, Registries.TRIM_MATERIAL);
    }

    @Test
    public void testTrimPattern() {
        this.testExcessConstants(TrimPattern.class, Registry.TRIM_PATTERN);
        this.testMissingConstants(TrimPattern.class, Registries.TRIM_PATTERN);
    }

    @Test
    public void testBlockType() {
        this.testExcessConstants(BlockType.class, Registry.BLOCK);
        this.testMissingConstants(BlockType.class, Registries.BLOCK);
    }

    @Test
    public void testItemType() {
        this.testExcessConstants(ItemType.class, Registry.ITEM);
        this.testMissingConstants(ItemType.class, Registries.ITEM);
    }

    @Test
    public void testFrogVariant() {
        this.testExcessConstants(Frog.Variant.class, Registry.FROG_VARIANT);
        this.testMissingConstants(Frog.Variant.class, Registries.FROG_VARIANT);
    }

    @Test
    public void testCatType() {
        this.testExcessConstants(Cat.Type.class, Registry.CAT_TYPE);
        this.testMissingConstants(Cat.Type.class, Registries.CAT_VARIANT);
    }

    @Test
    public void testPatternType() {
        this.testExcessConstants(PatternType.class, Registry.BANNER_PATTERN);
        this.testMissingConstants(PatternType.class, Registries.BANNER_PATTERN);
    }

    @Test
    public void testParticle() {
        this.testExcessConstants(Particle.class, Registry.PARTICLE_TYPE);
        this.testMissingConstants(Particle.class, Registries.PARTICLE_TYPE);
    }

    @Test
    public void testPotionType() {
        this.testExcessConstants(PotionType.class, Registry.POTION);
        this.testMissingConstants(PotionType.class, Registries.POTION);
    }

    @Test
    public void testGameEvent() {
        this.testExcessConstants(GameEvent.class, Registry.GAME_EVENT);
        this.testMissingConstants(GameEvent.class, Registries.GAME_EVENT);
    }

    @Test
    public void testMusicInstrument() {
        this.testExcessConstants(MusicInstrument.class, Registry.INSTRUMENT);
        this.testMissingConstants(MusicInstrument.class, Registries.INSTRUMENT);
    }

    private <T extends Keyed> void testExcessConstants(Class<T> clazz, Registry<? extends T> registry) {
        List<NamespacedKey> excessKeys = new ArrayList<>();

        for (Field field : clazz.getFields()) {
            if (field.getType() != clazz || !Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            // Old EntityType#UNKNOWN and Biome#CUSTOM
            if (field.getName().equals("UNKNOWN") || field.getName().equals("CUSTOM")) {
                continue;
            }

            NamespacedKey key = null;
            try {
                key = ((Keyed) field.get(null)).getKey();
            } catch (IllegalAccessException e) {
                Assert.fail(e.getMessage());
            }

            if (registry.get(key) == null) {
                excessKeys.add(key);
            }

        }

        Assert.assertTrue(excessKeys.size() + " excess constants(s) in " + clazz.getSimpleName() + " that do not exist: " + excessKeys, excessKeys.isEmpty());
    }

    private <T extends Keyed, M> void testMissingConstants(Class<T> clazz, ResourceKey<IRegistry<M>> nmsRegistryKey) {
        List<MinecraftKey> missingKeys = new ArrayList<>();

        IRegistry<M> nmsRegistry = REGISTRY_CUSTOM.registryOrThrow(nmsRegistryKey);
        for (M nmsObject : nmsRegistry) {
            MinecraftKey minecraftKey = nmsRegistry.getKey(nmsObject);

            try {
                @SuppressWarnings("unchecked")
                T bukkitObject = (T) clazz.getField(minecraftKey.getPath().toUpperCase().replace(".", "_")).get(null);

                Assert.assertEquals("Keys are not the same for " + minecraftKey, minecraftKey, CraftNamespacedKey.toMinecraft(bukkitObject.getKey()));
            } catch (NoSuchFieldException e) {
                missingKeys.add(minecraftKey);
            } catch (Exception e) {
                Assert.fail(e.getMessage());
            }
        }

        Assert.assertTrue("Missing (" + missingKeys.size() + ") constants in " + clazz.getSimpleName() + ": " + missingKeys, missingKeys.isEmpty());
    }
}

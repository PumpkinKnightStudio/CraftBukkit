package org.bukkit;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.support.AbstractTestingBase;
import org.bukkit.tag.ArtTags;
import org.bukkit.tag.BannerPatternTags;
import org.bukkit.tag.BiomeTags;
import org.bukkit.tag.BlockTags;
import org.bukkit.tag.CatVariantTags;
import org.bukkit.tag.EntityTypeTags;
import org.bukkit.tag.FluidTags;
import org.bukkit.tag.GameEventTags;
import org.bukkit.tag.InstrumentTags;
import org.bukkit.tag.ItemTags;
import org.bukkit.tag.StructureTags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public final class TagTest extends AbstractTestingBase {

    @Test
    public void testPaintingVariantTags() {
        this.testAllTagsArePresent("ART", Registry.ART, ArtTags.class);
    }

    @Test
    public void testBannerPatternTags() {
        this.testAllTagsArePresent("BANNER_PATTERN", Registry.BANNER_PATTERN, BannerPatternTags.class);
    }

    @Test
    public void testBiomeTags() {
        this.testAllTagsArePresent("BIOME", Registry.BIOME, BiomeTags.class);
    }

    @Test
    public void testBlockTags() {
        this.testAllTagsArePresent("BLOCK", Registry.BLOCK, BlockTags.class);
    }

    @Test
    public void testCatVariantTags() {
        this.testAllTagsArePresent("CAT_VARIANT", Registry.CAT_VARIANT, CatVariantTags.class);
    }

    @Test
    @Disabled // TODO: REMOVE ANNOTATION
    public void testDamageTypeTags() {
        // TODO (1.20.5): this.testAllTagsArePresent("DAMAGE_TYPE", Registry.DAMAGE_TYPE, DamageTypeTags.class);
    }

    @Test
    @Disabled // TODO: REMOVE ANNOTATION
    public void testEnchantmentTags() {
        // TODO (1.20.5): this.testAllTagsArePresent("ENCHANTMENT", Registry.ENCHANTMENT, EnchantmentTags.class);
    }

    @Test
    public void testEntityTypeTags() {
        this.testAllTagsArePresent("ENTITY_TYPE", Registry.ENTITY_TYPE, EntityTypeTags.class);
    }

    @Test
    public void testFluidTags() {
        this.testAllTagsArePresent("FLUID", Registry.FLUID, FluidTags.class);
    }

    @Test
    public void testGameEventTags() {
        this.testAllTagsArePresent("GAME_EVENT", Registry.GAME_EVENT, GameEventTags.class);
    }

    @Test
    public void testMusicInstrumentTags() {
        this.testAllTagsArePresent("INSTRUMENT", Registry.INSTRUMENT, InstrumentTags.class);
    }

    @Test
    public void testItemTags() {
        this.testAllTagsArePresent("ITEM", Registry.ITEM, ItemTags.class);
    }

    @Test
    public void testStructureTags() {
        this.testAllTagsArePresent("STRUCTURE", Registry.STRUCTURE, StructureTags.class);
    }

    private void testAllTagsArePresent(String registryName, Registry<?> registry, Class<?> tagContainerClass) {
        List<NamespacedKey> tags = registry.getTags().stream().map(Tag::getKey).toList();
        Assertions.assertFalse(tags.isEmpty(), "Cannot test registry with no tags (" + registryName + ")");

        List<NamespacedKey> missingTags = new ArrayList<>();

        for (NamespacedKey tagKey : tags) {
            String fieldName = tagKey.getKey().toUpperCase().replace('/', '_');

            Field field = tryFindField(tagContainerClass, fieldName);

            // Missing at this point
            if (field == null) {
                missingTags.add(tagKey);
            }
        }

        if (!missingTags.isEmpty()) {
            Assertions.fail("Missing " + missingTags.size() + " declared tags in class " + tagContainerClass.getName() + " for registry " + registryName + ": " + missingTags);
        }
    }

    private Field tryFindField(Class<?> clazz, String name) {
        try {
            return clazz.getField(name);
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }

}


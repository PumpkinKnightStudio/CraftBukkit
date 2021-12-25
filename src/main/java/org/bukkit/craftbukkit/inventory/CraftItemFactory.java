package org.bukkit.craftbukkit.inventory;

import org.apache.commons.lang.Validate;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.craftbukkit.util.CraftLegacy;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class CraftItemFactory implements ItemFactory {
    static final Color DEFAULT_LEATHER_COLOR = Color.fromRGB(0xA06540);
    private static final CraftItemFactory instance;

    static {
        instance = new CraftItemFactory();
        ConfigurationSerialization.registerClass(CraftMetaItem.SerializableMeta.class);
    }

    private CraftItemFactory() {
    }

    @Override
    public boolean isApplicable(ItemMeta meta, ItemStack itemstack) {
        if (itemstack == null) {
            return false;
        }
        return isApplicable(meta, itemstack.getType());
    }

    @Override
    public boolean isApplicable(ItemMeta meta, Material type) {
        type = CraftLegacy.fromLegacy(type); // This may be called from legacy item stacks, try to get the right material
        if (type == null || meta == null) {
            return false;
        }
        if (!(meta instanceof CraftMetaItem)) {
            throw new IllegalArgumentException("Meta of " + meta.getClass().toString() + " not created by " + CraftItemFactory.class.getName());
        }

        return ((CraftMetaItem) meta).applicableTo(type);
    }

    @Override
    public ItemMeta getItemMeta(Material material) {
        Validate.notNull(material, "Material cannot be null");
        return getItemMeta(material, null);
    }

    private ItemMeta getItemMeta(Material material, CraftMetaItem meta) {
        material = CraftLegacy.fromLegacy(material); // This may be called from legacy item stacks, try to get the right material

        if (material == Material.AIR) {
            return null;
        }
        if (material == Material.WRITTEN_BOOK) {
            return meta instanceof CraftMetaBookSigned ? meta : new CraftMetaBookSigned(meta);
        }
        if (material == Material.WRITABLE_BOOK) {
            return meta != null && meta.getClass().equals(CraftMetaBook.class) ? meta : new CraftMetaBook(meta);
        }
        if (material == Material.CREEPER_HEAD || material == Material.CREEPER_WALL_HEAD
                || material == Material.DRAGON_HEAD || material == Material.DRAGON_WALL_HEAD
                || material == Material.PLAYER_HEAD || material == Material.PLAYER_WALL_HEAD
                || material == Material.SKELETON_SKULL || material == Material.SKELETON_WALL_SKULL
                || material == Material.WITHER_SKELETON_SKULL || material == Material.WITHER_SKELETON_WALL_SKULL
                || material == Material.ZOMBIE_HEAD || material == Material.ZOMBIE_WALL_HEAD) {
            return meta instanceof CraftMetaSkull ? meta : new CraftMetaSkull(meta);
        }
        if (material == Material.LEATHER_HELMET || material == Material.LEATHER_HORSE_ARMOR
                || material == Material.LEATHER_CHESTPLATE || material == Material.LEATHER_LEGGINGS
                || material == Material.LEATHER_BOOTS) {
            return meta instanceof CraftMetaLeatherArmor ? meta : new CraftMetaLeatherArmor(meta);
        }
        if (material == Material.POTION || material == Material.SPLASH_POTION
                || material == Material.LINGERING_POTION || material == Material.TIPPED_ARROW) {
            return meta instanceof CraftMetaPotion ? meta : new CraftMetaPotion(meta);
        }
        if (material == Material.FILLED_MAP) {
            return meta instanceof CraftMetaMap ? meta : new CraftMetaMap(meta);
        }
        if (material == Material.FIREWORK_ROCKET) {
            return meta instanceof CraftMetaFirework ? meta : new CraftMetaFirework(meta);
        }
        if (material == Material.FIREWORK_STAR) {
            return meta instanceof CraftMetaCharge ? meta : new CraftMetaCharge(meta);
        }
        if (material == Material.ENCHANTED_BOOK) {
            return meta instanceof CraftMetaEnchantedBook ? meta : new CraftMetaEnchantedBook(meta);
        }
        if (Tag.BANNERS.isTagged(material)) {
            return meta instanceof CraftMetaBanner ? meta : new CraftMetaBanner(meta);
        }
        if (material == Material.AXOLOTL_SPAWN_EGG || material == Material.BAT_SPAWN_EGG
                || material == Material.BEE_SPAWN_EGG || material == Material.BLAZE_SPAWN_EGG
                || material == Material.CAT_SPAWN_EGG || material == Material.CAVE_SPIDER_SPAWN_EGG
                || material == Material.CHICKEN_SPAWN_EGG || material == Material.COD_SPAWN_EGG
                || material == Material.COW_SPAWN_EGG || material == Material.CREEPER_SPAWN_EGG
                || material == Material.DOLPHIN_SPAWN_EGG || material == Material.DONKEY_SPAWN_EGG
                || material == Material.DROWNED_SPAWN_EGG || material == Material.ELDER_GUARDIAN_SPAWN_EGG
                || material == Material.ENDERMAN_SPAWN_EGG || material == Material.ENDERMITE_SPAWN_EGG
                || material == Material.EVOKER_SPAWN_EGG || material == Material.FOX_SPAWN_EGG
                || material == Material.GHAST_SPAWN_EGG || material == Material.GLOW_SQUID_SPAWN_EGG
                || material == Material.GOAT_SPAWN_EGG || material == Material.GUARDIAN_SPAWN_EGG
                || material == Material.HOGLIN_SPAWN_EGG || material == Material.HORSE_SPAWN_EGG
                || material == Material.HUSK_SPAWN_EGG || material == Material.LLAMA_SPAWN_EGG
                || material == Material.MAGMA_CUBE_SPAWN_EGG || material == Material.MOOSHROOM_SPAWN_EGG
                || material == Material.MULE_SPAWN_EGG || material == Material.OCELOT_SPAWN_EGG
                || material == Material.PANDA_SPAWN_EGG || material == Material.PARROT_SPAWN_EGG
                || material == Material.PHANTOM_SPAWN_EGG || material == Material.PIGLIN_BRUTE_SPAWN_EGG
                || material == Material.PIGLIN_SPAWN_EGG || material == Material.PIG_SPAWN_EGG
                || material == Material.PILLAGER_SPAWN_EGG || material == Material.POLAR_BEAR_SPAWN_EGG
                || material == Material.PUFFERFISH_SPAWN_EGG || material == Material.RABBIT_SPAWN_EGG
                || material == Material.RAVAGER_SPAWN_EGG || material == Material.SALMON_SPAWN_EGG
                || material == Material.SHEEP_SPAWN_EGG || material == Material.SHULKER_SPAWN_EGG
                || material == Material.SILVERFISH_SPAWN_EGG || material == Material.SKELETON_HORSE_SPAWN_EGG
                || material == Material.SKELETON_SPAWN_EGG || material == Material.SLIME_SPAWN_EGG
                || material == Material.SPIDER_SPAWN_EGG || material == Material.SQUID_SPAWN_EGG
                || material == Material.STRAY_SPAWN_EGG || material == Material.STRIDER_SPAWN_EGG
                || material == Material.TRADER_LLAMA_SPAWN_EGG || material == Material.TROPICAL_FISH_SPAWN_EGG
                || material == Material.TURTLE_SPAWN_EGG || material == Material.VEX_SPAWN_EGG
                || material == Material.VILLAGER_SPAWN_EGG || material == Material.VINDICATOR_SPAWN_EGG
                || material == Material.WANDERING_TRADER_SPAWN_EGG || material == Material.WITCH_SPAWN_EGG
                || material == Material.WITHER_SKELETON_SPAWN_EGG || material == Material.WOLF_SPAWN_EGG
                || material == Material.ZOGLIN_SPAWN_EGG || material == Material.ZOMBIE_HORSE_SPAWN_EGG
                || material == Material.ZOMBIE_SPAWN_EGG || material == Material.ZOMBIE_VILLAGER_SPAWN_EGG
                || material == Material.ZOMBIFIED_PIGLIN_SPAWN_EGG) {
            return meta instanceof CraftMetaSpawnEgg ? meta : new CraftMetaSpawnEgg(meta);
        }
        if (material == Material.ARMOR_STAND) {
            return meta instanceof CraftMetaArmorStand ? meta : new CraftMetaArmorStand(meta);
        }
        if (material == Material.KNOWLEDGE_BOOK) {
            return meta instanceof CraftMetaKnowledgeBook ? meta : new CraftMetaKnowledgeBook(meta);
        }
        if (material == Material.FURNACE || material == Material.CHEST
                || material == Material.TRAPPED_CHEST || material == Material.JUKEBOX
                || material == Material.DISPENSER || material == Material.DROPPER
                || Tag.SIGNS.isTagged(material) || material == Material.SPAWNER
                || material == Material.BREWING_STAND || material == Material.ENCHANTING_TABLE
                || material == Material.COMMAND_BLOCK || material == Material.REPEATING_COMMAND_BLOCK
                || material == Material.CHAIN_COMMAND_BLOCK || material == Material.BEACON
                || material == Material.DAYLIGHT_DETECTOR || material == Material.HOPPER
                || material == Material.COMPARATOR || material == Material.SHIELD
                || material == Material.STRUCTURE_BLOCK || Tag.SHULKER_BOXES.isTagged(material)
                || material == Material.ENDER_CHEST || material == Material.BARREL
                || material == Material.BELL || material == Material.BLAST_FURNACE
                || material == Material.CAMPFIRE || material == Material.SOUL_CAMPFIRE
                || material == Material.JIGSAW || material == Material.LECTERN
                || material == Material.SMOKER || material == Material.BEEHIVE
                || material == Material.BEE_NEST || material == Material.SCULK_SENSOR) {
            return new CraftMetaBlockState(meta, material);
        }
        if (material == Material.TROPICAL_FISH_BUCKET) {
            return meta instanceof CraftMetaTropicalFishBucket ? meta : new CraftMetaTropicalFishBucket(meta);
        }
        if (material == Material.AXOLOTL_BUCKET) {
            return meta instanceof CraftMetaAxolotlBucket ? meta : new CraftMetaAxolotlBucket(meta);
        }
        if (material == Material.CROSSBOW) {
            return meta instanceof CraftMetaCrossbow ? meta : new CraftMetaCrossbow(meta);
        }
        if (material == Material.SUSPICIOUS_STEW) {
            return meta instanceof CraftMetaSuspiciousStew ? meta : new CraftMetaSuspiciousStew(meta);
        }
        if (material == Material.COD_BUCKET || material == Material.PUFFERFISH_BUCKET
                || material == Material.SALMON_BUCKET || material == Material.ITEM_FRAME
                || material == Material.GLOW_ITEM_FRAME || material == Material.PAINTING) {
            return meta instanceof CraftMetaEntityTag ? meta : new CraftMetaEntityTag(meta);
        }
        if (material == Material.COMPASS) {
            return meta instanceof CraftMetaCompass ? meta : new CraftMetaCompass(meta);
        }
        if (material == Material.BUNDLE) {
            return meta instanceof CraftMetaBundle ? meta : new CraftMetaBundle(meta);
        }

        return new CraftMetaItem(meta);
    }

    @Override
    public boolean equals(ItemMeta meta1, ItemMeta meta2) {
        if (meta1 == meta2) {
            return true;
        }
        if (meta1 != null && !(meta1 instanceof CraftMetaItem)) {
            throw new IllegalArgumentException("First meta of " + meta1.getClass().getName() + " does not belong to " + CraftItemFactory.class.getName());
        }
        if (meta2 != null && !(meta2 instanceof CraftMetaItem)) {
            throw new IllegalArgumentException("Second meta " + meta2.getClass().getName() + " does not belong to " + CraftItemFactory.class.getName());
        }
        if (meta1 == null) {
            return ((CraftMetaItem) meta2).isEmpty();
        }
        if (meta2 == null) {
            return ((CraftMetaItem) meta1).isEmpty();
        }

        return equals((CraftMetaItem) meta1, (CraftMetaItem) meta2);
    }

    boolean equals(CraftMetaItem meta1, CraftMetaItem meta2) {
        /*
         * This couldn't be done inside of the objects themselves, else force recursion.
         * This is a fairly clean way of implementing it, by dividing the methods into purposes and letting each method perform its own function.
         *
         * The common and uncommon were split, as both could have variables not applicable to the other, like a skull and book.
         * Each object needs its chance to say "hey wait a minute, we're not equal," but without the redundancy of using the 1.equals(2) && 2.equals(1) checking the 'commons' twice.
         *
         * Doing it this way fills all conditions of the .equals() method.
         */
        return meta1.equalsCommon(meta2) && meta1.notUncommon(meta2) && meta2.notUncommon(meta1);
    }

    public static CraftItemFactory instance() {
        return instance;
    }

    @Override
    public ItemMeta asMetaFor(ItemMeta meta, ItemStack stack) {
        Validate.notNull(stack, "Stack cannot be null");
        return asMetaFor(meta, stack.getType());
    }

    @Override
    public ItemMeta asMetaFor(ItemMeta meta, Material material) {
        Validate.notNull(material, "Material cannot be null");
        if (!(meta instanceof CraftMetaItem)) {
            throw new IllegalArgumentException("Meta of " + (meta != null ? meta.getClass().toString() : "null") + " not created by " + CraftItemFactory.class.getName());
        }
        return getItemMeta(material, (CraftMetaItem) meta);
    }

    @Override
    public Color getDefaultLeatherColor() {
        return DEFAULT_LEATHER_COLOR;
    }

    @Override
    public Material updateMaterial(ItemMeta meta, Material material) throws IllegalArgumentException {
        return ((CraftMetaItem) meta).updateMaterial(material);
    }
}

package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Preconditions;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.item.ArgumentParserItemStack;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemMonsterEgg;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import org.bukkit.Color;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftEntityType;
import org.bukkit.craftbukkit.util.CraftLegacy;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.ItemMeta;

public final class CraftItemFactory implements ItemFactory {
    static final Color DEFAULT_LEATHER_COLOR = Color.fromRGB(0xA06540);
    private static final CraftItemFactory instance;
    private static final RandomSource randomSource = RandomSource.create();

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
    public boolean isApplicable(ItemMeta meta, ItemType type) {
        if (type == null || meta == null) {
            return false;
        }

        Preconditions.checkArgument(meta instanceof CraftMetaItem, "Meta of %s not created by %s", meta.getClass().toString(), CraftItemFactory.class.getName());

        return ((CraftMetaItem) meta).applicableTo(type);
    }

    @Override
    public ItemMeta getItemMeta(ItemType itemType) {
        Preconditions.checkArgument(itemType != null, "ItemType cannot be null");
        return getItemMeta(itemType, null);
    }

    private ItemMeta getItemMeta(ItemType itemType, CraftMetaItem meta) {
        if (itemType == ItemType.AIR) {
            return null;
        }
        if (itemType == ItemType.WRITTEN_BOOK) {
            return meta instanceof CraftMetaBookSigned ? meta : new CraftMetaBookSigned(meta);
        }
        if (itemType == ItemType.WRITABLE_BOOK) {
            return meta != null && meta.getClass().equals(CraftMetaBook.class) ? meta : new CraftMetaBook(meta);
        }
        if (itemType == ItemType.CREEPER_HEAD || itemType == ItemType.DRAGON_HEAD
                || itemType == ItemType.PIGLIN_HEAD || itemType == ItemType.PLAYER_HEAD
                || itemType == ItemType.SKELETON_SKULL || itemType == ItemType.WITHER_SKELETON_SKULL
                || itemType == ItemType.ZOMBIE_HEAD) {
            return meta instanceof CraftMetaSkull ? meta : new CraftMetaSkull(meta);
        }
        if (itemType == ItemType.CHAINMAIL_HELMET || itemType == ItemType.CHAINMAIL_CHESTPLATE
                || itemType == ItemType.CHAINMAIL_LEGGINGS || itemType == ItemType.CHAINMAIL_BOOTS
                || itemType == ItemType.DIAMOND_HELMET || itemType == ItemType.DIAMOND_CHESTPLATE
                || itemType == ItemType.DIAMOND_LEGGINGS || itemType == ItemType.DIAMOND_BOOTS
                || itemType == ItemType.GOLDEN_HELMET || itemType == ItemType.GOLDEN_CHESTPLATE
                || itemType == ItemType.GOLDEN_LEGGINGS || itemType == ItemType.GOLDEN_BOOTS
                || itemType == ItemType.IRON_HELMET || itemType == ItemType.IRON_CHESTPLATE
                || itemType == ItemType.IRON_LEGGINGS || itemType == ItemType.IRON_BOOTS
                || itemType == ItemType.NETHERITE_HELMET || itemType == ItemType.NETHERITE_CHESTPLATE
                || itemType == ItemType.NETHERITE_LEGGINGS || itemType == ItemType.NETHERITE_BOOTS
                || itemType == ItemType.TURTLE_HELMET) {
            return meta != null && meta.getClass().equals(CraftMetaArmor.class) ? meta : new CraftMetaArmor(meta);
        }
        if (itemType == ItemType.LEATHER_HELMET || itemType == ItemType.LEATHER_CHESTPLATE
                || itemType == ItemType.LEATHER_LEGGINGS || itemType == ItemType.LEATHER_BOOTS) {
            return meta instanceof CraftMetaColorableArmor ? meta : new CraftMetaColorableArmor(meta);
        }
        if (itemType == ItemType.LEATHER_HORSE_ARMOR) {
            return meta instanceof CraftMetaLeatherArmor ? meta : new CraftMetaLeatherArmor(meta);
        }
        if (itemType == ItemType.POTION || itemType == ItemType.SPLASH_POTION
                || itemType == ItemType.LINGERING_POTION || itemType == ItemType.TIPPED_ARROW) {
            return meta instanceof CraftMetaPotion ? meta : new CraftMetaPotion(meta);
        }
        if (itemType == ItemType.FILLED_MAP) {
            return meta instanceof CraftMetaMap ? meta : new CraftMetaMap(meta);
        }
        if (itemType == ItemType.FIREWORK_ROCKET) {
            return meta instanceof CraftMetaFirework ? meta : new CraftMetaFirework(meta);
        }
        if (itemType == ItemType.FIREWORK_STAR) {
            return meta instanceof CraftMetaCharge ? meta : new CraftMetaCharge(meta);
        }
        if (itemType == ItemType.ENCHANTED_BOOK) {
            return meta instanceof CraftMetaEnchantedBook ? meta : new CraftMetaEnchantedBook(meta);
        }
        if (itemType.hasBlockType() && Tag.BANNERS.isTagged(itemType.getBlockType())) {
            return meta instanceof CraftMetaBanner ? meta : new CraftMetaBanner(meta);
        }
        if (itemType == ItemType.ALLAY_SPAWN_EGG || itemType == ItemType.AXOLOTL_SPAWN_EGG
                || itemType == ItemType.BAT_SPAWN_EGG || itemType == ItemType.BEE_SPAWN_EGG
                || itemType == ItemType.BLAZE_SPAWN_EGG || itemType == ItemType.CAT_SPAWN_EGG
                || itemType == ItemType.CAMEL_SPAWN_EGG || itemType == ItemType.CAVE_SPIDER_SPAWN_EGG
                || itemType == ItemType.CHICKEN_SPAWN_EGG || itemType == ItemType.COD_SPAWN_EGG
                || itemType == ItemType.COW_SPAWN_EGG || itemType == ItemType.CREEPER_SPAWN_EGG
                || itemType == ItemType.DOLPHIN_SPAWN_EGG || itemType == ItemType.DONKEY_SPAWN_EGG
                || itemType == ItemType.DROWNED_SPAWN_EGG || itemType == ItemType.ELDER_GUARDIAN_SPAWN_EGG
                || itemType == ItemType.ENDER_DRAGON_SPAWN_EGG || itemType == ItemType.ENDERMAN_SPAWN_EGG
                || itemType == ItemType.ENDERMITE_SPAWN_EGG || itemType == ItemType.EVOKER_SPAWN_EGG
                || itemType == ItemType.FOX_SPAWN_EGG || itemType == ItemType.FROG_SPAWN_EGG
                || itemType == ItemType.GHAST_SPAWN_EGG || itemType == ItemType.GLOW_SQUID_SPAWN_EGG
                || itemType == ItemType.GOAT_SPAWN_EGG || itemType == ItemType.GUARDIAN_SPAWN_EGG
                || itemType == ItemType.HOGLIN_SPAWN_EGG || itemType == ItemType.HORSE_SPAWN_EGG
                || itemType == ItemType.HUSK_SPAWN_EGG || itemType == ItemType.IRON_GOLEM_SPAWN_EGG
                || itemType == ItemType.LLAMA_SPAWN_EGG || itemType == ItemType.MAGMA_CUBE_SPAWN_EGG
                || itemType == ItemType.MOOSHROOM_SPAWN_EGG || itemType == ItemType.MULE_SPAWN_EGG
                || itemType == ItemType.OCELOT_SPAWN_EGG || itemType == ItemType.PANDA_SPAWN_EGG
                || itemType == ItemType.PARROT_SPAWN_EGG || itemType == ItemType.PHANTOM_SPAWN_EGG
                || itemType == ItemType.PIGLIN_BRUTE_SPAWN_EGG || itemType == ItemType.PIGLIN_SPAWN_EGG
                || itemType == ItemType.PIG_SPAWN_EGG || itemType == ItemType.PILLAGER_SPAWN_EGG
                || itemType == ItemType.POLAR_BEAR_SPAWN_EGG || itemType == ItemType.PUFFERFISH_SPAWN_EGG
                || itemType == ItemType.RABBIT_SPAWN_EGG || itemType == ItemType.RAVAGER_SPAWN_EGG
                || itemType == ItemType.SALMON_SPAWN_EGG || itemType == ItemType.SHEEP_SPAWN_EGG
                || itemType == ItemType.SHULKER_SPAWN_EGG || itemType == ItemType.SILVERFISH_SPAWN_EGG
                || itemType == ItemType.SKELETON_HORSE_SPAWN_EGG || itemType == ItemType.SKELETON_SPAWN_EGG
                || itemType == ItemType.SLIME_SPAWN_EGG || itemType == ItemType.SNIFFER_SPAWN_EGG
                || itemType == ItemType.SNOW_GOLEM_SPAWN_EGG || itemType == ItemType.SPIDER_SPAWN_EGG
                || itemType == ItemType.SQUID_SPAWN_EGG || itemType == ItemType.STRAY_SPAWN_EGG
                || itemType == ItemType.STRIDER_SPAWN_EGG || itemType == ItemType.TADPOLE_SPAWN_EGG
                || itemType == ItemType.TRADER_LLAMA_SPAWN_EGG || itemType == ItemType.TROPICAL_FISH_SPAWN_EGG
                || itemType == ItemType.TURTLE_SPAWN_EGG || itemType == ItemType.VEX_SPAWN_EGG
                || itemType == ItemType.VILLAGER_SPAWN_EGG || itemType == ItemType.VINDICATOR_SPAWN_EGG
                || itemType == ItemType.WANDERING_TRADER_SPAWN_EGG || itemType == ItemType.WARDEN_SPAWN_EGG
                || itemType == ItemType.WITCH_SPAWN_EGG || itemType == ItemType.WITHER_SKELETON_SPAWN_EGG
                || itemType == ItemType.WITHER_SPAWN_EGG || itemType == ItemType.WOLF_SPAWN_EGG
                || itemType == ItemType.ZOGLIN_SPAWN_EGG || itemType == ItemType.ZOMBIE_HORSE_SPAWN_EGG
                || itemType == ItemType.ZOMBIE_SPAWN_EGG || itemType == ItemType.ZOMBIE_VILLAGER_SPAWN_EGG
                || itemType == ItemType.ZOMBIFIED_PIGLIN_SPAWN_EGG || itemType == ItemType.BREEZE_SPAWN_EGG) {
            return meta instanceof CraftMetaSpawnEgg ? meta : new CraftMetaSpawnEgg(meta);
        }
        if (itemType == ItemType.ARMOR_STAND) {
            return meta instanceof CraftMetaArmorStand ? meta : new CraftMetaArmorStand(meta);
        }
        if (itemType == ItemType.KNOWLEDGE_BOOK) {
            return meta instanceof CraftMetaKnowledgeBook ? meta : new CraftMetaKnowledgeBook(meta);
        }
        if (itemType == ItemType.FURNACE || itemType == ItemType.CHEST
                || itemType == ItemType.TRAPPED_CHEST || itemType == ItemType.JUKEBOX
                || itemType == ItemType.DISPENSER || itemType == ItemType.DROPPER
                || (itemType.hasBlockType() && Tag.SIGNS.isTagged(itemType.getBlockType())) || itemType == ItemType.SPAWNER
                || itemType == ItemType.BREWING_STAND || itemType == ItemType.ENCHANTING_TABLE
                || itemType == ItemType.COMMAND_BLOCK || itemType == ItemType.REPEATING_COMMAND_BLOCK
                || itemType == ItemType.CHAIN_COMMAND_BLOCK || itemType == ItemType.BEACON
                || itemType == ItemType.DAYLIGHT_DETECTOR || itemType == ItemType.HOPPER
                || itemType == ItemType.COMPARATOR || itemType == ItemType.SHIELD
                || itemType == ItemType.STRUCTURE_BLOCK || (itemType.hasBlockType() && Tag.SHULKER_BOXES.isTagged(itemType.getBlockType()))
                || itemType == ItemType.ENDER_CHEST || itemType == ItemType.BARREL
                || itemType == ItemType.BELL || itemType == ItemType.BLAST_FURNACE
                || itemType == ItemType.CAMPFIRE || itemType == ItemType.SOUL_CAMPFIRE
                || itemType == ItemType.JIGSAW || itemType == ItemType.LECTERN
                || itemType == ItemType.SMOKER || itemType == ItemType.BEEHIVE
                || itemType == ItemType.BEE_NEST || itemType == ItemType.SCULK_CATALYST
                || itemType == ItemType.SCULK_SHRIEKER || itemType == ItemType.SCULK_SENSOR
                || itemType == ItemType.CALIBRATED_SCULK_SENSOR || itemType == ItemType.CHISELED_BOOKSHELF
                || itemType == ItemType.DECORATED_POT || itemType == ItemType.SUSPICIOUS_SAND
                || itemType == ItemType.SUSPICIOUS_GRAVEL || itemType == ItemType.CRAFTER
                || itemType == ItemType.TRIAL_SPAWNER) {
            return new CraftMetaBlockState(meta, itemType);
        }
        if (itemType == ItemType.TROPICAL_FISH_BUCKET) {
            return meta instanceof CraftMetaTropicalFishBucket ? meta : new CraftMetaTropicalFishBucket(meta);
        }
        if (itemType == ItemType.AXOLOTL_BUCKET) {
            return meta instanceof CraftMetaAxolotlBucket ? meta : new CraftMetaAxolotlBucket(meta);
        }
        if (itemType == ItemType.CROSSBOW) {
            return meta instanceof CraftMetaCrossbow ? meta : new CraftMetaCrossbow(meta);
        }
        if (itemType == ItemType.SUSPICIOUS_STEW) {
            return meta instanceof CraftMetaSuspiciousStew ? meta : new CraftMetaSuspiciousStew(meta);
        }
        if (itemType == ItemType.COD_BUCKET || itemType == ItemType.PUFFERFISH_BUCKET
                || itemType == ItemType.SALMON_BUCKET || itemType == ItemType.ITEM_FRAME
                || itemType == ItemType.GLOW_ITEM_FRAME || itemType == ItemType.PAINTING) {
            return meta instanceof CraftMetaEntityTag ? meta : new CraftMetaEntityTag(meta);
        }
        if (itemType == ItemType.COMPASS) {
            return meta instanceof CraftMetaCompass ? meta : new CraftMetaCompass(meta);
        }
        if (itemType == ItemType.BUNDLE) {
            return meta instanceof CraftMetaBundle ? meta : new CraftMetaBundle(meta);
        }
        if (itemType == ItemType.GOAT_HORN) {
            return meta instanceof CraftMetaMusicInstrument ? meta : new CraftMetaMusicInstrument(meta);
        }

        return new CraftMetaItem(meta);
    }

    @Override
    public boolean equals(ItemMeta meta1, ItemMeta meta2) {
        if (meta1 == meta2) {
            return true;
        }

        if (meta1 != null) {
            Preconditions.checkArgument(meta1 instanceof CraftMetaItem, "First meta of %s does not belong to %s", meta1.getClass().getName(), CraftItemFactory.class.getName());
        } else {
            return ((CraftMetaItem) meta2).isEmpty();
        }
        if (meta2 != null) {
            Preconditions.checkArgument(meta2 instanceof CraftMetaItem, "Second meta of %s does not belong to %s", meta2.getClass().getName(), CraftItemFactory.class.getName());
        } else {
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
        Preconditions.checkArgument(stack != null, "ItemStack stack cannot be null");
        return asMetaFor(meta, stack.getType());
    }

    @Override
    public ItemMeta asMetaFor(ItemMeta meta, ItemType itemType) {
        Preconditions.checkArgument(itemType != null, "ItemType cannot be null");
        Preconditions.checkArgument(meta instanceof CraftMetaItem, "ItemMeta of %s not created by %s", (meta != null ? meta.getClass().toString() : "null"), CraftItemFactory.class.getName());
        return getItemMeta(itemType, (CraftMetaItem) meta);
    }

    @Override
    public Color getDefaultLeatherColor() {
        return DEFAULT_LEATHER_COLOR;
    }

    @Override
    public ItemType updateItemType(ItemMeta meta, ItemType itemType) throws IllegalArgumentException {
        return ((CraftMetaItem) meta).updateMaterial(itemType);
    }

    @Override
    public ItemStack createItemStack(String input) throws IllegalArgumentException {
        try {
            ArgumentParserItemStack.a arg = ArgumentParserItemStack.parseForItem(CraftRegistry.getMinecraftRegistry().registryOrThrow(Registries.ITEM).asLookup(), new StringReader(input));

            Item item = arg.item().value();
            net.minecraft.world.item.ItemStack nmsItemStack = new net.minecraft.world.item.ItemStack(item);

            NBTTagCompound nbt = arg.nbt();
            if (nbt != null) {
                nmsItemStack.setTag(nbt);
            }

            return CraftItemStack.asCraftMirror(nmsItemStack);
        } catch (CommandSyntaxException ex) {
            throw new IllegalArgumentException("Could not parse ItemStack: " + input, ex);
        }
    }

    @Override
    public ItemType getSpawnEgg(EntityType type) {
        if (type == EntityType.UNKNOWN) {
            return null;
        }
        EntityTypes<?> nmsType = CraftEntityType.bukkitToMinecraft(type);
        Item nmsItem = ItemMonsterEgg.byId(nmsType);

        if (nmsItem == null) {
            return null;
        }

        return CraftItemType.minecraftToBukkit(nmsItem);
    }

    @Override
    public ItemStack enchantItem(Entity entity, ItemStack itemStack, int level, boolean allowTreasures) {
        Preconditions.checkArgument(entity != null, "The entity must not be null");

        return enchantItem(((CraftEntity) entity).getHandle().random, itemStack, level, allowTreasures);
    }

    @Override
    public ItemStack enchantItem(final World world, final ItemStack itemStack, final int level, final boolean allowTreasures) {
        Preconditions.checkArgument(world != null, "The world must not be null");

        return enchantItem(((CraftWorld) world).getHandle().random, itemStack, level, allowTreasures);
    }

    @Override
    public ItemStack enchantItem(final ItemStack itemStack, final int level, final boolean allowTreasures) {
        return enchantItem(randomSource, itemStack, level, allowTreasures);
    }

    private static ItemStack enchantItem(RandomSource source, ItemStack itemStack, int level, boolean allowTreasures) {
        Preconditions.checkArgument(itemStack != null, "ItemStack must not be null");
        Preconditions.checkArgument(itemStack.getType() != ItemType.AIR, "ItemStack must not be air");

        if (!(itemStack instanceof CraftItemStack)) {
            itemStack = CraftItemStack.asCraftCopy(itemStack);
        }

        CraftItemStack craft = (CraftItemStack) itemStack;
        EnchantmentManager.enchantItem(source, craft.handle, level, allowTreasures);
        return craft;
    }
}

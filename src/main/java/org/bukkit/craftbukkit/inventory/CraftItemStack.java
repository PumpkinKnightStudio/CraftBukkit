package org.bukkit.craftbukkit.inventory;

import static org.bukkit.craftbukkit.inventory.CraftMetaItem.*;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.util.CraftLegacy;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

@DelegateDeserialization(ItemStack.class)
public final class CraftItemStack extends ItemStack {

    public static net.minecraft.world.item.ItemStack asNMSCopy(ItemStack original) {
        if (original instanceof CraftItemStack) {
            CraftItemStack stack = (CraftItemStack) original;
            return stack.handle == null ? net.minecraft.world.item.ItemStack.EMPTY : stack.handle.copy();
        }
        if (original == null || original.getType() == Material.AIR) {
            return net.minecraft.world.item.ItemStack.EMPTY;
        }

        Item item = CraftMagicNumbers.getItem(original.getType(), original.getDurability());

        if (item == null) {
            return net.minecraft.world.item.ItemStack.EMPTY;
        }

        net.minecraft.world.item.ItemStack stack = new net.minecraft.world.item.ItemStack(item, original.getAmount());
        if (original.hasItemMeta()) {
            setItemMeta(stack, original.getItemMeta());
        }
        return stack;
    }

    public static net.minecraft.world.item.ItemStack copyNMSStack(net.minecraft.world.item.ItemStack original, int amount) {
        net.minecraft.world.item.ItemStack stack = original.copy();
        stack.setCount(amount);
        return stack;
    }

    /**
     * Copies the NMS stack to return as a strictly-Bukkit stack
     */
    public static ItemStack asBukkitCopy(net.minecraft.world.item.ItemStack original) {
        if (original.isEmpty()) {
            return new ItemStack(Material.AIR);
        }
        ItemStack stack = new ItemStack(CraftMagicNumbers.getMaterial(original.getItem()), original.getCount());
        if (hasItemMeta(original)) {
            stack.setItemMeta(getItemMeta(original));
        }
        return stack;
    }

    public static CraftItemStack asCraftMirror(net.minecraft.world.item.ItemStack original) {
        return new CraftItemStack((original == null || original.isEmpty()) ? null : original);
    }

    public static CraftItemStack asCraftCopy(ItemStack original) {
        if (original instanceof CraftItemStack) {
            CraftItemStack stack = (CraftItemStack) original;
            return new CraftItemStack(stack.handle == null ? null : stack.handle.copy());
        }
        return new CraftItemStack(original);
    }

    public static CraftItemStack asNewCraftStack(Item item) {
        return asNewCraftStack(item, 1);
    }

    public static CraftItemStack asNewCraftStack(Item item, int amount) {
        return new CraftItemStack(CraftMagicNumbers.getMaterial(item), amount, (short) 0, null);
    }

    net.minecraft.world.item.ItemStack handle;

    /**
     * Mirror
     */
    private CraftItemStack(net.minecraft.world.item.ItemStack item) {
        this.handle = item;
    }

    private CraftItemStack(ItemStack item) {
        this(item.getType(), item.getAmount(), item.getDurability(), item.hasItemMeta() ? item.getItemMeta() : null);
    }

    private CraftItemStack(Material type, int amount, short durability, ItemMeta itemMeta) {
        setType(type);
        setAmount(amount);
        setDurability(durability);
        setItemMeta(itemMeta);
    }

    @Override
    public MaterialData getData() {
        return handle != null ? CraftMagicNumbers.getMaterialData(handle.getItem()) : super.getData();
    }

    @Override
    public Material getType() {
        return handle != null ? CraftMagicNumbers.getMaterial(handle.getItem()) : Material.AIR;
    }

    @Override
    public void setType(Material type) {
        if (getType() == type) {
            return;
        } else if (type == Material.AIR) {
            handle = null;
        } else if (CraftMagicNumbers.getItem(type) == null) { // :(
            handle = null;
        } else if (handle == null) {
            handle = new net.minecraft.world.item.ItemStack(CraftMagicNumbers.getItem(type), 1);
        } else {
            handle.setItem(CraftMagicNumbers.getItem(type));
            if (hasItemMeta()) {
                // This will create the appropriate item meta, which will contain all the data we intend to keep
                setItemMeta(handle, getItemMeta(handle));
            }
        }
        setData(null);
    }

    @Override
    public int getAmount() {
        return handle != null ? handle.getCount() : 0;
    }

    @Override
    public void setAmount(int amount) {
        if (handle == null) {
            return;
        }

        handle.setCount(amount);
        if (amount == 0) {
            handle = null;
        }
    }

    @Override
    public void setDurability(final short durability) {
        // Ignore damage if item is null
        if (handle != null) {
            handle.setDamageValue(durability);
        }
    }

    @Override
    public short getDurability() {
        if (handle != null) {
            return (short) handle.getDamageValue();
        } else {
            return -1;
        }
    }

    @Override
    public int getMaxStackSize() {
        return (handle == null) ? Material.AIR.getMaxStackSize() : handle.getItem().getMaxStackSize();
    }

    @Override
    public void addUnsafeEnchantment(Enchantment ench, int level) {
        Validate.notNull(ench, "Cannot add null enchantment");

        if (!makeTag(handle)) {
            return;
        }
        NBTTagList list = getEnchantmentList(handle);
        if (list == null) {
            list = new NBTTagList();
            handle.getTag().put(ENCHANTMENTS.NBT, list);
        }
        int size = list.size();

        for (int i = 0; i < size; i++) {
            NBTTagCompound tag = (NBTTagCompound) list.get(i);
            String id = tag.getString(ENCHANTMENTS_ID.NBT);
            if (ench.getKey().equals(NamespacedKey.fromString(id))) {
                tag.putShort(ENCHANTMENTS_LVL.NBT, (short) level);
                return;
            }
        }
        NBTTagCompound tag = new NBTTagCompound();
        tag.putString(ENCHANTMENTS_ID.NBT, ench.getKey().toString());
        tag.putShort(ENCHANTMENTS_LVL.NBT, (short) level);
        list.add(tag);
    }

    static boolean makeTag(net.minecraft.world.item.ItemStack item) {
        if (item == null) {
            return false;
        }

        if (item.getTag() == null) {
            item.setTag(new NBTTagCompound());
        }

        return true;
    }

    @Override
    public boolean containsEnchantment(Enchantment ench) {
        return getEnchantmentLevel(ench) > 0;
    }

    @Override
    public int getEnchantmentLevel(Enchantment ench) {
        Validate.notNull(ench, "Cannot find null enchantment");
        if (handle == null) {
            return 0;
        }
        return EnchantmentManager.getItemEnchantmentLevel(CraftEnchantment.getRaw(ench), handle);
    }

    @Override
    public int removeEnchantment(Enchantment ench) {
        Validate.notNull(ench, "Cannot remove null enchantment");

        NBTTagList list = getEnchantmentList(handle), listCopy;
        if (list == null) {
            return 0;
        }
        int index = Integer.MIN_VALUE;
        int level = Integer.MIN_VALUE;
        int size = list.size();

        for (int i = 0; i < size; i++) {
            NBTTagCompound enchantment = (NBTTagCompound) list.get(i);
            String id = enchantment.getString(ENCHANTMENTS_ID.NBT);
            if (ench.getKey().equals(NamespacedKey.fromString(id))) {
                index = i;
                level = 0xffff & enchantment.getShort(ENCHANTMENTS_LVL.NBT);
                break;
            }
        }

        if (index == Integer.MIN_VALUE) {
            return 0;
        }
        if (size == 1) {
            handle.getTag().remove(ENCHANTMENTS.NBT);
            if (handle.getTag().isEmpty()) {
                handle.setTag(null);
            }
            return level;
        }

        // This is workaround for not having an index removal
        listCopy = new NBTTagList();
        for (int i = 0; i < size; i++) {
            if (i != index) {
                listCopy.add(list.get(i));
            }
        }
        handle.getTag().put(ENCHANTMENTS.NBT, listCopy);

        return level;
    }

    @Override
    public Map<Enchantment, Integer> getEnchantments() {
        return getEnchantments(handle);
    }

    static Map<Enchantment, Integer> getEnchantments(net.minecraft.world.item.ItemStack item) {
        NBTTagList list = (item != null && item.isEnchanted()) ? item.getEnchantmentTags() : null;

        if (list == null || list.size() == 0) {
            return ImmutableMap.of();
        }

        ImmutableMap.Builder<Enchantment, Integer> result = ImmutableMap.builder();

        for (int i = 0; i < list.size(); i++) {
            String id = ((NBTTagCompound) list.get(i)).getString(ENCHANTMENTS_ID.NBT);
            int level = 0xffff & ((NBTTagCompound) list.get(i)).getShort(ENCHANTMENTS_LVL.NBT);

            Enchantment enchant = Enchantment.getByKey(CraftNamespacedKey.fromStringOrNull(id));
            if (enchant != null) {
                result.put(enchant, level);
            }
        }

        return result.build();
    }

    static NBTTagList getEnchantmentList(net.minecraft.world.item.ItemStack item) {
        return (item != null && item.isEnchanted()) ? item.getEnchantmentTags() : null;
    }

    @Override
    public CraftItemStack clone() {
        CraftItemStack itemStack = (CraftItemStack) super.clone();
        if (this.handle != null) {
            itemStack.handle = this.handle.copy();
        }
        return itemStack;
    }

    @Override
    public ItemMeta getItemMeta() {
        return getItemMeta(handle);
    }

    public static ItemMeta getItemMeta(net.minecraft.world.item.ItemStack item) {
        if (!hasItemMeta(item)) {
            return CraftItemFactory.instance().getItemMeta(getType(item));
        }

        Material type = getType(item);
        if (type == Material.WRITTEN_BOOK) {
            return new CraftMetaBookSigned(item.getTag());
        }
        if (type == Material.WRITABLE_BOOK) {
            return new CraftMetaBook(item.getTag());
        }
        if (type == Material.CREEPER_HEAD || type == Material.CREEPER_WALL_HEAD
                || type == Material.DRAGON_HEAD || type == Material.DRAGON_WALL_HEAD
                || type == Material.PLAYER_HEAD || type == Material.PLAYER_WALL_HEAD
                || type == Material.SKELETON_SKULL || type == Material.SKELETON_WALL_SKULL
                || type == Material.WITHER_SKELETON_SKULL || type == Material.WITHER_SKELETON_WALL_SKULL
                || type == Material.ZOMBIE_HEAD || type == Material.ZOMBIE_WALL_HEAD) {
            return new CraftMetaSkull(item.getTag());
        }
        if (type == Material.LEATHER_HELMET || type == Material.LEATHER_HORSE_ARMOR
                || type == Material.LEATHER_CHESTPLATE || type == Material.LEATHER_LEGGINGS
                || type == Material.LEATHER_BOOTS) {
            return new CraftMetaLeatherArmor(item.getTag());
        }
        if (type == Material.POTION || type == Material.SPLASH_POTION
                || type == Material.LINGERING_POTION || type == Material.TIPPED_ARROW) {
            return new CraftMetaPotion(item.getTag());
        }
        if (type == Material.FILLED_MAP) {
            return new CraftMetaMap(item.getTag());
        }
        if (type == Material.FIREWORK_ROCKET) {
            return new CraftMetaFirework(item.getTag());
        }
        if (type == Material.FIREWORK_STAR) {
            return new CraftMetaCharge(item.getTag());
        }
        if (type == Material.ENCHANTED_BOOK) {
            return new CraftMetaEnchantedBook(item.getTag());
        }
        if (type == Material.BLACK_BANNER || type == Material.BLACK_WALL_BANNER
                || type == Material.BLUE_BANNER || type == Material.BLUE_WALL_BANNER
                || type == Material.BROWN_BANNER || type == Material.BROWN_WALL_BANNER
                || type == Material.CYAN_BANNER || type == Material.CYAN_WALL_BANNER
                || type == Material.GRAY_BANNER || type == Material.GRAY_WALL_BANNER
                || type == Material.GREEN_BANNER || type == Material.GREEN_WALL_BANNER
                || type == Material.LIGHT_BLUE_BANNER || type == Material.LIGHT_BLUE_WALL_BANNER
                || type == Material.LIGHT_GRAY_BANNER || type == Material.LIGHT_GRAY_WALL_BANNER
                || type == Material.LIME_BANNER || type == Material.LIME_WALL_BANNER
                || type == Material.MAGENTA_BANNER || type == Material.MAGENTA_WALL_BANNER
                || type == Material.ORANGE_BANNER || type == Material.ORANGE_WALL_BANNER
                || type == Material.PINK_BANNER || type == Material.PINK_WALL_BANNER
                || type == Material.PURPLE_BANNER || type == Material.PURPLE_WALL_BANNER
                || type == Material.RED_BANNER || type == Material.RED_WALL_BANNER
                || type == Material.WHITE_BANNER || type == Material.WHITE_WALL_BANNER
                || type == Material.YELLOW_BANNER || type == Material.YELLOW_WALL_BANNER) {
            return new CraftMetaBanner(item.getTag());
        }
        if (type == Material.AXOLOTL_SPAWN_EGG || type == Material.BAT_SPAWN_EGG
                || type == Material.BEE_SPAWN_EGG || type == Material.BLAZE_SPAWN_EGG
                || type == Material.CAT_SPAWN_EGG || type == Material.CAVE_SPIDER_SPAWN_EGG
                || type == Material.CHICKEN_SPAWN_EGG || type == Material.COD_SPAWN_EGG
                || type == Material.COW_SPAWN_EGG || type == Material.CREEPER_SPAWN_EGG
                || type == Material.DOLPHIN_SPAWN_EGG || type == Material.DONKEY_SPAWN_EGG
                || type == Material.DROWNED_SPAWN_EGG || type == Material.ELDER_GUARDIAN_SPAWN_EGG
                || type == Material.ENDERMAN_SPAWN_EGG || type == Material.ENDERMITE_SPAWN_EGG
                || type == Material.EVOKER_SPAWN_EGG || type == Material.FOX_SPAWN_EGG
                || type == Material.GHAST_SPAWN_EGG || type == Material.GLOW_SQUID_SPAWN_EGG
                || type == Material.GOAT_SPAWN_EGG || type == Material.GUARDIAN_SPAWN_EGG
                || type == Material.HOGLIN_SPAWN_EGG || type == Material.HORSE_SPAWN_EGG
                || type == Material.HUSK_SPAWN_EGG || type == Material.LLAMA_SPAWN_EGG
                || type == Material.MAGMA_CUBE_SPAWN_EGG || type == Material.MOOSHROOM_SPAWN_EGG
                || type == Material.MULE_SPAWN_EGG || type == Material.OCELOT_SPAWN_EGG
                || type == Material.PANDA_SPAWN_EGG || type == Material.PARROT_SPAWN_EGG
                || type == Material.PHANTOM_SPAWN_EGG || type == Material.PIGLIN_BRUTE_SPAWN_EGG
                || type == Material.PIGLIN_SPAWN_EGG || type == Material.PIG_SPAWN_EGG
                || type == Material.PILLAGER_SPAWN_EGG || type == Material.POLAR_BEAR_SPAWN_EGG
                || type == Material.PUFFERFISH_SPAWN_EGG || type == Material.RABBIT_SPAWN_EGG
                || type == Material.RAVAGER_SPAWN_EGG || type == Material.SALMON_SPAWN_EGG
                || type == Material.SHEEP_SPAWN_EGG || type == Material.SHULKER_SPAWN_EGG
                || type == Material.SILVERFISH_SPAWN_EGG || type == Material.SKELETON_HORSE_SPAWN_EGG
                || type == Material.SKELETON_SPAWN_EGG || type == Material.SLIME_SPAWN_EGG
                || type == Material.SPIDER_SPAWN_EGG || type == Material.SQUID_SPAWN_EGG
                || type == Material.STRAY_SPAWN_EGG || type == Material.STRIDER_SPAWN_EGG
                || type == Material.TRADER_LLAMA_SPAWN_EGG || type == Material.TROPICAL_FISH_SPAWN_EGG
                || type == Material.TURTLE_SPAWN_EGG || type == Material.VEX_SPAWN_EGG
                || type == Material.VILLAGER_SPAWN_EGG || type == Material.VINDICATOR_SPAWN_EGG
                || type == Material.WANDERING_TRADER_SPAWN_EGG || type == Material.WITCH_SPAWN_EGG
                || type == Material.WITHER_SKELETON_SPAWN_EGG || type == Material.WOLF_SPAWN_EGG
                || type == Material.ZOGLIN_SPAWN_EGG || type == Material.ZOMBIE_HORSE_SPAWN_EGG
                || type == Material.ZOMBIE_SPAWN_EGG || type == Material.ZOMBIE_VILLAGER_SPAWN_EGG
                || type == Material.ZOMBIFIED_PIGLIN_SPAWN_EGG) {
            return new CraftMetaSpawnEgg(item.getTag());
        }
        if (type == Material.ARMOR_STAND) {
            return new CraftMetaArmorStand(item.getTag());
        }
        if (type == Material.KNOWLEDGE_BOOK) {
            return new CraftMetaKnowledgeBook(item.getTag());
        }
        if (type == Material.FURNACE || type == Material.CHEST
                || type == Material.TRAPPED_CHEST || type == Material.JUKEBOX
                || type == Material.DISPENSER || type == Material.DROPPER
                || Tag.SIGNS.isTagged(type) || type == Material.SPAWNER
                || type == Material.BREWING_STAND || type == Material.ENCHANTING_TABLE
                || type == Material.COMMAND_BLOCK || type == Material.REPEATING_COMMAND_BLOCK
                || type == Material.CHAIN_COMMAND_BLOCK || type == Material.BEACON
                || type == Material.DAYLIGHT_DETECTOR || type == Material.HOPPER
                || type == Material.COMPARATOR || type == Material.SHIELD
                || type == Material.STRUCTURE_BLOCK || Tag.SHULKER_BOXES.isTagged(type)
                || type == Material.ENDER_CHEST || type == Material.BARREL
                || type == Material.BELL || type == Material.BLAST_FURNACE
                || type == Material.CAMPFIRE || type == Material.SOUL_CAMPFIRE
                || type == Material.JIGSAW || type == Material.LECTERN
                || type == Material.SMOKER || type == Material.BEEHIVE
                || type == Material.BEE_NEST || type == Material.SCULK_CATALYST
                || type == Material.SCULK_SHRIEKER || type == Material.SCULK_SENSOR) {
            return new CraftMetaBlockState(item.getTag(), CraftMagicNumbers.getMaterial(item.getItem()));
        }
        if (type == Material.TROPICAL_FISH_BUCKET) {
            return new CraftMetaTropicalFishBucket(item.getTag());
        }
        if (type == Material.AXOLOTL_BUCKET) {
            return new CraftMetaAxolotlBucket(item.getTag());
        }
        if (type == Material.CROSSBOW) {
            return new CraftMetaCrossbow(item.getTag());
        }
        if (type == Material.SUSPICIOUS_STEW) {
            return new CraftMetaSuspiciousStew(item.getTag());
        }
        if (type == Material.COD_BUCKET || type == Material.PUFFERFISH_BUCKET
                || type == Material.SALMON_BUCKET || type == Material.ITEM_FRAME
                || type == Material.GLOW_ITEM_FRAME || type == Material.PAINTING) {
            return new CraftMetaEntityTag(item.getTag());
        }
        if (type == Material.COMPASS) {
            return new CraftMetaCompass(item.getTag());
        }
        if (type == Material.BUNDLE) {
            return new CraftMetaBundle(item.getTag());
        }

        return new CraftMetaItem(item.getTag());
    }

    static Material getType(net.minecraft.world.item.ItemStack item) {
        return item == null ? Material.AIR : CraftMagicNumbers.getMaterial(item.getItem());
    }

    @Override
    public boolean setItemMeta(ItemMeta itemMeta) {
        return setItemMeta(handle, itemMeta);
    }

    public static boolean setItemMeta(net.minecraft.world.item.ItemStack item, ItemMeta itemMeta) {
        if (item == null) {
            return false;
        }
        if (CraftItemFactory.instance().equals(itemMeta, null)) {
            item.setTag(null);
            return true;
        }
        if (!CraftItemFactory.instance().isApplicable(itemMeta, getType(item))) {
            return false;
        }

        itemMeta = CraftItemFactory.instance().asMetaFor(itemMeta, getType(item));
        if (itemMeta == null) return true;

        Item oldItem = item.getItem();
        Item newItem = CraftMagicNumbers.getItem(CraftItemFactory.instance().updateMaterial(itemMeta, CraftMagicNumbers.getMaterial(oldItem)));
        if (oldItem != newItem) {
            item.setItem(newItem);
        }

        NBTTagCompound tag = new NBTTagCompound();
        item.setTag(tag);

        ((CraftMetaItem) itemMeta).applyToItem(tag);
        item.convertStack(((CraftMetaItem) itemMeta).getVersion());
        // SpigotCraft#463 this is required now by the Vanilla client, so mimic ItemStack constructor in ensuring it
        if (item.getItem() != null && item.getItem().canBeDepleted()) {
            item.setDamageValue(item.getDamageValue());
        }

        return true;
    }

    @Override
    public boolean isSimilar(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        if (stack == this) {
            return true;
        }
        if (!(stack instanceof CraftItemStack)) {
            return stack.getClass() == ItemStack.class && stack.isSimilar(this);
        }

        CraftItemStack that = (CraftItemStack) stack;
        if (handle == that.handle) {
            return true;
        }
        if (handle == null || that.handle == null) {
            return false;
        }
        Material comparisonType = CraftLegacy.fromLegacy(that.getType()); // This may be called from legacy item stacks, try to get the right material
        if (!(comparisonType == getType() && getDurability() == that.getDurability())) {
            return false;
        }
        return hasItemMeta() ? that.hasItemMeta() && handle.getTag().equals(that.handle.getTag()) : !that.hasItemMeta();
    }

    @Override
    public boolean hasItemMeta() {
        return hasItemMeta(handle) && !CraftItemFactory.instance().equals(getItemMeta(), null);
    }

    static boolean hasItemMeta(net.minecraft.world.item.ItemStack item) {
        return !(item == null || item.getTag() == null || item.getTag().isEmpty());
    }
}

package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Preconditions;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.item.ArgumentParserItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemMonsterEgg;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.craftbukkit.entity.CraftEntityType;
import org.bukkit.craftbukkit.util.CraftLegacy;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.entity.EntityType;
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

        Preconditions.checkArgument(meta instanceof CraftMetaItem, "Meta of %s not created by %s", meta.getClass().toString(), CraftItemFactory.class.getName());

        return ((CraftMetaItem) meta).applicableTo(type);
    }

    @Override
    public ItemMeta getItemMeta(Material material) {
        Preconditions.checkArgument(material != null, "Material cannot be null");
        return getItemMeta(material, null);
    }

    private ItemMeta getItemMeta(Material material, CraftMetaItem meta) {
        material = CraftLegacy.fromLegacy(material); // This may be called from legacy item stacks, try to get the right material

        if (!material.isItem()) {
            // default behavior for none items is a new CraftMetaItem
            return new CraftMetaItem(meta);
        }

        return ((CraftItemType<?>) material.asItemType()).getItemMeta(meta);
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
    public ItemMeta asMetaFor(ItemMeta meta, Material material) {
        Preconditions.checkArgument(material != null, "Material cannot be null");
        Preconditions.checkArgument(meta instanceof CraftMetaItem, "ItemMeta of %s not created by %s", (meta != null ? meta.getClass().toString() : "null"), CraftItemFactory.class.getName());
        return getItemMeta(material, (CraftMetaItem) meta);
    }

    @Override
    public Color getDefaultLeatherColor() {
        return DEFAULT_LEATHER_COLOR;
    }

    @Override
    public ItemStack createItemStack(String input) throws IllegalArgumentException {
        try {
            ArgumentParserItemStack.a arg = ArgumentParserItemStack.parseForItem(BuiltInRegistries.ITEM.asLookup(), new StringReader(input));

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
    public Material updateMaterial(ItemMeta meta, Material material) throws IllegalArgumentException {
        return ((CraftMetaItem) meta).updateMaterial(material);
    }

    @Override
    public Material getSpawnEgg(EntityType type) {
        if (type == EntityType.UNKNOWN) {
            return null;
        }
        EntityTypes<?> nmsType = CraftEntityType.bukkitToMinecraft(type);
        Item nmsItem = ItemMonsterEgg.byId(nmsType);

        if (nmsItem == null) {
            return null;
        }

        return CraftMagicNumbers.getMaterial(nmsItem);
    }
}

package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.Map;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.AttributeBase;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemBlock;
import net.minecraft.world.item.ItemRecord;
import net.minecraft.world.level.block.entity.TileEntityFurnace;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.BlockType;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.attribute.CraftAttribute;
import org.bukkit.craftbukkit.attribute.CraftAttributeInstance;
import org.bukkit.craftbukkit.block.CraftBlockType;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.inventory.CreativeCategory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;

public class CraftItemType implements ItemType {
    private final NamespacedKey key;
    private final Item item;

    public static ItemType minecraftToBukkit(Item minecraft) {
        Preconditions.checkArgument(minecraft != null);

        IRegistry<Item> registry = CraftRegistry.getMinecraftRegistry().registryOrThrow(Registries.ITEM);
        ItemType bukkit = Registry.ITEM.get(CraftNamespacedKey.fromMinecraft(registry.getKey(minecraft)));

        Preconditions.checkArgument(bukkit != null);

        return bukkit;
    }

    public static Item bukkitToMinecraft(ItemType bukkit) {
        Preconditions.checkArgument(bukkit != null);

        return ((CraftItemType) bukkit).getHandle();
    }

    public CraftItemType(NamespacedKey key, Item item) {
        this.key = key;
        this.item = item;
    }

    public Item getHandle() {
        return item;
    }

    @Override
    public boolean hasBlockType() {
        return item instanceof ItemBlock;
    }

    @NotNull
    @Override
    public BlockType<?> getBlockType() {
        if (!(item instanceof ItemBlock block)) {
            throw new IllegalStateException("The item type " + getKey() + " has no corresponding block type");
        }

        return CraftBlockType.minecraftToBukkit(block.getBlock());
    }

    @Override
    public int getMaxStackSize() {
        // Based of the material enum air is only 0, in PerMaterialTest it is also set as special case
        // the item info itself would return 64
        if (this == AIR) {
            return 0;
        }
        return item.getMaxStackSize();
    }

    @Override
    public short getMaxDurability() {
        return (short) item.getMaxDamage();
    }

    @Override
    public boolean isEdible() {
        return item.isEdible();
    }

    @Override
    public boolean isRecord() {
        return item instanceof ItemRecord;
    }

    @Override
    public boolean isFuel() {
        return TileEntityFurnace.isFuel(new net.minecraft.world.item.ItemStack(item));
    }

    @Override
    public ItemType getCraftingRemainingItem() {
        Item expectedItem = item.getCraftingRemainingItem();
        return expectedItem == null ? null : minecraftToBukkit(expectedItem);
    }

//    @Override
//    public EquipmentSlot getEquipmentSlot() {
//        return CraftEquipmentSlot.getSlot(EntityInsentient.getEquipmentSlotForItem(CraftItemStack.asNMSCopy(ItemStack.of(this))));
//    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> defaultAttributes = ImmutableMultimap.builder();

        Multimap<AttributeBase, net.minecraft.world.entity.ai.attributes.AttributeModifier> nmsDefaultAttributes = item.getDefaultAttributeModifiers(CraftEquipmentSlot.getNMS(equipmentSlot));
        for (Map.Entry<AttributeBase, net.minecraft.world.entity.ai.attributes.AttributeModifier> mapEntry : nmsDefaultAttributes.entries()) {
            Attribute attribute = CraftAttribute.minecraftToBukkit(mapEntry.getKey());
            defaultAttributes.put(attribute, CraftAttributeInstance.convert(mapEntry.getValue(), equipmentSlot));
        }

        return defaultAttributes.build();
    }

    @Override
    public CreativeCategory getCreativeCategory() {
        return CreativeCategory.BUILDING_BLOCKS;
    }

    @Override
    public boolean isEnabledByFeature(@NotNull World world) {
        Preconditions.checkNotNull(world, "World cannot be null");
        return getHandle().isEnabled(((CraftWorld) world).getHandle().enabledFeatures());
    }

    @NotNull
    @Override
    public String getTranslationKey() {
        return item.getDescriptionId();
    }

    @Override
    public NamespacedKey getKey() {
        return key;
    }

    @Override
    public Material asMaterial() {
        return Registry.MATERIAL.get(this.key);
    }
}

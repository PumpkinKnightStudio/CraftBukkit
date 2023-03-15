package org.bukkit.craftbukkit.inventory;

import static org.bukkit.craftbukkit.inventory.CraftItemFactory.*;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.inventory.meta.PlayerLeatherArmorMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@DelegateDeserialization(CraftMetaItem.SerializableMeta.class)
public class CraftMetaPlayerLeatherArmor extends CraftMetaArmor implements PlayerLeatherArmorMeta {

    private static final Set<Material> LEATHER_ARMOR_MATERIALS = Sets.newHashSet(
            Material.LEATHER_HELMET,
            Material.LEATHER_CHESTPLATE,
            Material.LEATHER_LEGGINGS,
            Material.LEATHER_BOOTS
    );

    private Color color = DEFAULT_LEATHER_COLOR;

    CraftMetaPlayerLeatherArmor(CraftMetaItem meta) {
        super(meta);
        CraftMetaLeatherArmor.readColor(this, meta);
    }

    CraftMetaPlayerLeatherArmor(NBTTagCompound tag) {
        super(tag);
        CraftMetaLeatherArmor.readColor(this, tag);
    }

    CraftMetaPlayerLeatherArmor(Map<String, Object> map) {
        super(map);
        CraftMetaLeatherArmor.readColor(this, map);
    }

    @Override
    void applyToItem(NBTTagCompound itemTag) {
        super.applyToItem(itemTag);
        CraftMetaLeatherArmor.applyColor(this, itemTag);
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && isLeatherArmorEmpty();
    }

    boolean isLeatherArmorEmpty() {
        return !(hasColor());
    }

    @Override
    boolean applicableTo(Material type) {
        return LEATHER_ARMOR_MATERIALS.contains(type);
    }

    @Override
    public CraftMetaPlayerLeatherArmor clone() {
        CraftMetaPlayerLeatherArmor clone = (CraftMetaPlayerLeatherArmor) super.clone();
        clone.color = this.color;
        return clone;
    }

    @Override
    @NotNull
    public Color getColor() {
        return color;
    }

    @Override
    public void setColor(@Nullable Color color) {
        this.color = color;
    }

    boolean hasColor() {
        return CraftMetaLeatherArmor.hasColor(this);
    }

    @Override
    Builder<String, Object> serialize(Builder<String, Object> builder) {
        super.serialize(builder);

        CraftMetaLeatherArmor.serialize(this, builder);

        return builder;
    }

    @Override
    boolean equalsCommon(CraftMetaItem meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }
        if (meta instanceof CraftMetaPlayerLeatherArmor) {
            CraftMetaPlayerLeatherArmor that = (CraftMetaPlayerLeatherArmor) meta;

            return color.equals(that.color);
        }
        return true;
    }

    @Override
    boolean notUncommon(CraftMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof CraftMetaPlayerLeatherArmor || isLeatherArmorEmpty());
    }

    @Override
    int applyHash() {
        final int original;
        int hash = original = super.applyHash();
        if (hasColor()) {
            hash ^= color.hashCode();
        }
        return original != hash ? CraftMetaPlayerLeatherArmor.class.hashCode() ^ hash : hash;
    }

}

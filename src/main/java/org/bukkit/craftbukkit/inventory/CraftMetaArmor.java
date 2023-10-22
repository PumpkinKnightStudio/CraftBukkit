package org.bukkit.craftbukkit.inventory;

import com.google.common.collect.ImmutableMap.Builder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

@DelegateDeserialization(CraftMetaItem.SerializableMeta.class)
public class CraftMetaArmor extends CraftMetaItem implements ArmorMeta {

    static final ItemMetaKey TRIM = new ItemMetaKey("Trim", "trim");
    static final ItemMetaKey TRIM_MATERIAL = new ItemMetaKey("material");
    static final ItemMetaKey TRIM_PATTERN = new ItemMetaKey("pattern");

    private ArmorTrim trim;

    CraftMetaArmor(CraftMetaItem meta) {
        super(meta);

        if (meta instanceof CraftMetaArmor armorMeta) {
            this.trim = armorMeta.trim;
        }
    }

    CraftMetaArmor(NBTTagCompound tag) {
        super(tag);

        if (tag.contains(TRIM.NBT)) {
            NBTTagCompound trimCompound = tag.getCompound(TRIM.NBT);

            if (trimCompound.contains(TRIM_MATERIAL.NBT) && trimCompound.contains(TRIM_PATTERN.NBT)) {
                TrimMaterial trimMaterial = Registry.TRIM_MATERIAL.get(NamespacedKey.fromString(trimCompound.getString(TRIM_MATERIAL.NBT)));
                TrimPattern trimPattern = Registry.TRIM_PATTERN.get(NamespacedKey.fromString(trimCompound.getString(TRIM_PATTERN.NBT)));

                this.trim = new ArmorTrim(trimMaterial, trimPattern);
            }
        }
    }

    CraftMetaArmor(Map<String, Object> map) {
        super(map);

        Map<?, ?> trimData = SerializableMeta.getObject(Map.class, map, TRIM.BUKKIT, true);
        if (trimData != null) {
            String materialKeyString = SerializableMeta.getString(trimData, TRIM_MATERIAL.BUKKIT, true);
            String patternKeyString = SerializableMeta.getString(trimData, TRIM_PATTERN.BUKKIT, true);

            if (materialKeyString != null && patternKeyString != null) {
                NamespacedKey materialKey = NamespacedKey.fromString(materialKeyString);
                NamespacedKey patternKey = NamespacedKey.fromString(patternKeyString);

                if (materialKey != null && patternKey != null) {
                    TrimMaterial trimMaterial = Registry.TRIM_MATERIAL.get(materialKey);
                    TrimPattern trimPattern = Registry.TRIM_PATTERN.get(patternKey);

                    if (trimMaterial != null && trimPattern != null) {
                        this.trim = new ArmorTrim(trimMaterial, trimPattern);
                    }
                }
            }
        }
    }

    @Override
    void applyToItem(NBTTagCompound itemTag) {
        super.applyToItem(itemTag);

        if (hasTrim()) {
            NBTTagCompound trimCompound = new NBTTagCompound();
            trimCompound.putString(TRIM_MATERIAL.NBT, trim.getMaterial().getKey().toString());
            trimCompound.putString(TRIM_PATTERN.NBT, trim.getPattern().getKey().toString());
            itemTag.put(TRIM.NBT, trimCompound);
        }
    }

    @Override
    boolean applicableTo(Material type) {
        if (!type.isItem()) {
            return false;
        }

        return type.asItemType().getItemMetaClass() == ArmorMeta.class;
    }

    @Override
    boolean equalsCommon(CraftMetaItem that) {
        if (!super.equalsCommon(that)) {
            return false;
        }

        if (that instanceof CraftMetaArmor armorMeta) {
            return Objects.equals(trim, armorMeta.trim);
        }

        return true;
    }

    @Override
    boolean notUncommon(CraftMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof CraftMetaArmor || isArmorEmpty());
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && isArmorEmpty();
    }

    private boolean isArmorEmpty() {
        return !hasTrim();
    }

    @Override
    int applyHash() {
        final int original;
        int hash = original = super.applyHash();

        if (hasTrim()) {
            hash = 61 * hash + trim.hashCode();
        }

        return original != hash ? CraftMetaArmor.class.hashCode() ^ hash : hash;
    }

    @Override
    public CraftMetaArmor clone() {
        CraftMetaArmor meta = (CraftMetaArmor) super.clone();
        meta.trim = this.trim;
        return meta;
    }

    @Override
    Builder<String, Object> serialize(Builder<String, Object> builder) {
        super.serialize(builder);

        if (hasTrim()) {
            Map<String, String> trimData = new HashMap<>();
            trimData.put(TRIM_MATERIAL.BUKKIT, trim.getMaterial().getKey().toString());
            trimData.put(TRIM_PATTERN.BUKKIT, trim.getPattern().getKey().toString());
            builder.put(TRIM.BUKKIT, trimData);
        }

        return builder;
    }

    @Override
    public boolean hasTrim() {
        return trim != null;
    }

    @Override
    public void setTrim(ArmorTrim trim) {
        this.trim = trim;
    }

    @Override
    public ArmorTrim getTrim() {
        return trim;
    }
}

package org.bukkit.craftbukkit.inventory;

import com.google.common.collect.ImmutableMap.Builder;
import java.util.Map;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.inventory.CraftMetaItem.ItemMetaKey;
import org.bukkit.craftbukkit.inventory.CraftMetaItem.SerializableMeta;
import org.bukkit.craftbukkit.util.CraftLegacy;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.material.MaterialData;

@DelegateDeserialization(CraftMetaItem.SerializableMeta.class)
public class CraftMetaSpawnEgg extends CraftMetaItem implements SpawnEggMeta {

    static final ItemMetaKey ENTITY_TAG = new ItemMetaKey("EntityTag", "entity-tag");
    @ItemMetaKey.Specific(ItemMetaKey.Specific.To.NBT)
    static final ItemMetaKey ENTITY_ID = new ItemMetaKey("id");

    private EntityType spawnedType;
    private NBTTagCompound entityTag;

    CraftMetaSpawnEgg(CraftMetaItem meta) {
        super(meta);

        if (!(meta instanceof CraftMetaSpawnEgg)) {
            return;
        }

        CraftMetaSpawnEgg egg = (CraftMetaSpawnEgg) meta;
        this.spawnedType = egg.spawnedType;

        updateMaterial(null); // Trigger type population
    }

    CraftMetaSpawnEgg(NBTTagCompound tag) {
        super(tag);

        if (tag.hasKey(ENTITY_TAG.NBT)) {
            entityTag = tag.getCompound(ENTITY_TAG.NBT);
        }
    }

    CraftMetaSpawnEgg(Map<String, Object> map) {
        super(map);

        String entityType = SerializableMeta.getString(map, ENTITY_ID.BUKKIT, true);
        if (entityType != null) {
            this.spawnedType = EntityType.fromName(entityType);
        }
    }

    @Override
    void deserializeInternal(NBTTagCompound tag, Object context) {
        super.deserializeInternal(tag, context);

        if (tag.hasKey(ENTITY_TAG.NBT)) {
            entityTag = tag.getCompound(ENTITY_TAG.NBT);

            if (context instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) context;

                // Duplicated from constructor
                String entityType = SerializableMeta.getString(map, ENTITY_ID.BUKKIT, true);
                if (entityType != null) {
                    this.spawnedType = EntityType.fromName(entityType);
                }
            }

            if (this.spawnedType != null) {
                // We have a valid spawn type, just remove the ID now
                entityTag.remove(ENTITY_ID.NBT);
            }

            // Tag still has some other data, lets try our luck with a conversion
            if (!entityTag.isEmpty()) {
                // SPIGOT-4128: This is hopeless until we start versioning stacks. RIP data.
                // entityTag = (NBTTagCompound) MinecraftServer.getServer().dataConverterManager.update(DataConverterTypes.ENTITY, new Dynamic(DynamicOpsNBT.a, entityTag), -1, CraftMagicNumbers.DATA_VERSION).getValue();
            }

            // See if we can read a converted ID tag
            if (entityTag.hasKey(ENTITY_ID.NBT)) {
                this.spawnedType = EntityType.fromName(new MinecraftKey(entityTag.getString(ENTITY_ID.NBT)).getKey());
            }
        }
    }

    @Override
    void serializeInternal(Map<String, NBTBase> internalTags) {
        if (entityTag != null && !entityTag.isEmpty()) {
            internalTags.put(ENTITY_TAG.NBT, entityTag);
        }
    }

    @Override
    void applyToItem(NBTTagCompound tag) {
        super.applyToItem(tag);

        if (!isSpawnEggEmpty() && entityTag == null) {
            entityTag = new NBTTagCompound();
        }

        if (entityTag != null) {
            tag.set(ENTITY_TAG.NBT, entityTag);
        }
    }

    @Override
    boolean applicableTo(Material type) {
        if (type == Material.BAT_SPAWN_EGG || type == Material.BEE_SPAWN_EGG
                || type == Material.BLAZE_SPAWN_EGG || type == Material.CAT_SPAWN_EGG
                || type == Material.CAVE_SPIDER_SPAWN_EGG || type == Material.CHICKEN_SPAWN_EGG
                || type == Material.COD_SPAWN_EGG || type == Material.COW_SPAWN_EGG
                || type == Material.CREEPER_SPAWN_EGG || type == Material.DOLPHIN_SPAWN_EGG
                || type == Material.DONKEY_SPAWN_EGG || type == Material.DROWNED_SPAWN_EGG
                || type == Material.ELDER_GUARDIAN_SPAWN_EGG || type == Material.ENDERMAN_SPAWN_EGG
                || type == Material.ENDERMITE_SPAWN_EGG || type == Material.EVOKER_SPAWN_EGG
                || type == Material.FOX_SPAWN_EGG || type == Material.GHAST_SPAWN_EGG
                || type == Material.GUARDIAN_SPAWN_EGG || type == Material.HOGLIN_SPAWN_EGG
                || type == Material.HORSE_SPAWN_EGG || type == Material.HUSK_SPAWN_EGG
                || type == Material.LLAMA_SPAWN_EGG || type == Material.MAGMA_CUBE_SPAWN_EGG
                || type == Material.MOOSHROOM_SPAWN_EGG || type == Material.MULE_SPAWN_EGG
                || type == Material.OCELOT_SPAWN_EGG || type == Material.PANDA_SPAWN_EGG
                || type == Material.PARROT_SPAWN_EGG || type == Material.PHANTOM_SPAWN_EGG
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
            return true;
        }

        return false;
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && isSpawnEggEmpty();
    }

    boolean isSpawnEggEmpty() {
        return !(hasSpawnedType() || entityTag != null);
    }

    boolean hasSpawnedType() {
        return spawnedType != null;
    }

    @Override
    public EntityType getSpawnedType() {
        throw new UnsupportedOperationException("Must check item type to get spawned type");
    }

    @Override
    public void setSpawnedType(EntityType type) {
        throw new UnsupportedOperationException("Must change item type to set spawned type");
    }

    @Override
    boolean equalsCommon(CraftMetaItem meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }
        if (meta instanceof CraftMetaSpawnEgg) {
            CraftMetaSpawnEgg that = (CraftMetaSpawnEgg) meta;

            return hasSpawnedType() ? that.hasSpawnedType() && this.spawnedType.equals(that.spawnedType) : !that.hasSpawnedType()
                    && entityTag != null ? that.entityTag != null && this.entityTag.equals(that.entityTag) : entityTag == null;
        }
        return true;
    }

    @Override
    boolean notUncommon(CraftMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof CraftMetaSpawnEgg || isSpawnEggEmpty());
    }

    @Override
    int applyHash() {
        final int original;
        int hash = original = super.applyHash();

        if (hasSpawnedType()) {
            hash = 73 * hash + spawnedType.hashCode();
        }
        if (entityTag != null) {
            hash = 73 * hash + entityTag.hashCode();
        }

        return original != hash ? CraftMetaSpawnEgg.class.hashCode() ^ hash : hash;
    }

    @Override
    Builder<String, Object> serialize(Builder<String, Object> builder) {
        super.serialize(builder);

        return builder;
    }

    @Override
    public CraftMetaSpawnEgg clone() {
        CraftMetaSpawnEgg clone = (CraftMetaSpawnEgg) super.clone();

        clone.spawnedType = spawnedType;
        if (entityTag != null) {
            clone.entityTag = entityTag.clone();
        }

        return clone;
    }

    @Override
    final Material updateMaterial(Material material) {
        if (spawnedType == null) {
            spawnedType = EntityType.fromId(getDamage());
            setDamage(0);
        }

        if (spawnedType != null) {
            if (entityTag != null) {
                // Remove ID tag as it is now in the material
                entityTag.remove(ENTITY_ID.NBT);
            }

            return CraftLegacy.fromLegacy(new MaterialData(Material.LEGACY_MONSTER_EGG, (byte) spawnedType.getTypeId()));
        }

        return super.updateMaterial(material);
    }
}

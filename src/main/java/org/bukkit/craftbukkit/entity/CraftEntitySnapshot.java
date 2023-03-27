package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTCompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CraftEntitySnapshot implements EntitySnapshot {

    private final EntityType type;
    private final String worldName;
    private final NBTTagCompound compoundTag;

    static {
        ConfigurationSerialization.registerClass(CraftEntitySnapshot.class);
    }

    public CraftEntitySnapshot(EntityType type, String worldName, NBTTagCompound compoundTag) {
        this.type = type;
        this.worldName = worldName;
        this.compoundTag = compoundTag;
    }

    @NotNull
    @Override
    public EntityType getType() {
        return this.type;
    }

    @Override
    public Location getLocation() {
        NBTTagList pos = this.compoundTag.getList("Pos", NBTBase.TAG_DOUBLE);
        World world = Bukkit.getWorld(this.worldName);

        if (pos == null || pos.size() < 3)
            return new Location(world, 0, 0, 0);

        return new Location(
                world,
                pos.getDouble(0),
                pos.getDouble(1),
                pos.getDouble(2));
    }

    @Override
    public boolean apply(@NotNull Entity entity) {
        Preconditions.checkArgument(entity != null, "entity cannot be null");
        org.spigotmc.AsyncCatcher.catchOp("apply");
        Preconditions.checkArgument(entity.getType() == this.type, "type of passed entity and type of entity snapshot must match");

        if (!entity.isValid())
            return false;

        Location targetPos = getLocation();

        if (targetPos.getWorld() == null || !entity.getWorld().equals(targetPos.getWorld()))
            return false;

        try {
            CraftEntity craftEntity = (CraftEntity) entity;

            craftEntity.getHandle().load(this.compoundTag);

            return true;
        } catch(Exception e) {
            throw new IllegalStateException("an error occurred while restoring EntitySnapshot (corrupted data?)", e);
        }
    }

    public static CraftEntitySnapshot fetchFrom(CraftEntity entity) {
        NBTTagCompound compoundTag = new NBTTagCompound();

        entity.getHandle().saveWithoutId(compoundTag);

        return new CraftEntitySnapshot(
                entity.getType(),
                entity.getWorld().getName(),
                compoundTag
        );
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        return new HashMap<>(){{
            put("Type", type.name());
            put("WorldName", worldName);

            try {
                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                NBTCompressedStreamTools.writeCompressed(compoundTag, buf);

                put("NBT", Base64.getEncoder().encodeToString(buf.toByteArray()));
            } catch (IOException ex) {
                Logger.getLogger(CraftEntitySnapshot.class.getName()).log(Level.SEVERE, null, ex);
            }
        }};
    }

    public static CraftEntitySnapshot deserialize(Map<String, Object> map) throws Throwable {
        Validate.notNull(map, "Cannot deserialize null map");

        if (map.containsKey("Type"))
            throw new NoSuchElementException(map + " does not contain Type");
        if (map.containsKey("WorldName"))
            throw new NoSuchElementException(map + " does not contain WorldName");
        if (map.containsKey("NBT"))
            throw new NoSuchElementException(map + " does not contain NBT");

        String serializedNBT = (String) map.get("NBT");
        ByteArrayInputStream nbtBuf = new ByteArrayInputStream(Base64.getDecoder().decode(serializedNBT));
        NBTTagCompound nbt = NBTCompressedStreamTools.readCompressed(nbtBuf);

        return new CraftEntitySnapshot(
                EntityType.valueOf((String) map.get("Type")),
                (String) map.get("WorldName"),
                nbt
        );
    }
}

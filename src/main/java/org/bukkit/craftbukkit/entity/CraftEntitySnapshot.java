package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTCompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;
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
import org.jetbrains.annotations.Nullable;

public class CraftEntitySnapshot implements EntitySnapshot {

    private final EntityType type;

    private final NBTTagCompound compoundTag;
    private String worldName; // null ignores teleportation completely

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
        synchronized (this.compoundTag) {
            if (this.worldName == null)
                return null;

            NBTTagList pos = this.compoundTag.getList("Pos", NBTBase.TAG_DOUBLE);
            NBTTagList rotation = this.compoundTag.getList("Rotation", NBTBase.TAG_FLOAT);
            World world = Bukkit.getWorld(this.worldName);

            if (pos == null || pos.size() < 3 || rotation == null || rotation.size() < 2)
                return new Location(world, 0, 0, 0);

            return new Location(
                world,
                pos.getDouble(0),
                pos.getDouble(1),
                pos.getDouble(2),
                rotation.getFloat(0),
                rotation.getFloat(1));
        }
    }

    @Override
    public void setLocation(@Nullable Location location) {
        synchronized (this.compoundTag) {
            if (location == null) {
                this.worldName = null;

            } else {
                Preconditions.checkArgument(location.getWorld() != null, "world of location cannot be null");

                this.worldName = location.getWorld().getName();
                setInternalLocation(location);
            }
        }
    }

    @NotNull
    @Override
    public Vector getVelocity() {
        synchronized (this.compoundTag) {
            NBTTagList motion = this.compoundTag.getList("Motion", NBTBase.TAG_DOUBLE);

            if (motion == null || motion.size() < 3)
                return new Vector();

            return new Vector(
                motion.getDouble(0),
                motion.getDouble(1),
                motion.getDouble(2)
            );
        }
    }

    @Override
    public void setVelocity(@NotNull Vector velocity) {
        Preconditions.checkArgument(velocity != null, "velocity cannot be null");
        velocity.checkFinite();

        synchronized (this.compoundTag) {
            NBTTagList motion = new NBTTagList();

            motion.add(NBTTagDouble.valueOf(velocity.getX()));
            motion.add(NBTTagDouble.valueOf(velocity.getY()));
            motion.add(NBTTagDouble.valueOf(velocity.getZ()));

            this.compoundTag.put("Motion", motion);
        }
    }

    private void setInternalLocation(Location loc) {
        // pos
        NBTTagList pos = new NBTTagList();

        pos.add(NBTTagDouble.valueOf(loc.getX()));
        pos.add(NBTTagDouble.valueOf(loc.getY()));
        pos.add(NBTTagDouble.valueOf(loc.getZ()));

        this.compoundTag.put("Pos", pos);

        // rotation
        NBTTagList rotation = new NBTTagList();

        rotation.add(NBTTagFloat.valueOf(loc.getYaw()));
        rotation.add(NBTTagFloat.valueOf(loc.getPitch()));

        this.compoundTag.put("Rotation", rotation);
    }

    @Override
    public float getFallDistance() {
        synchronized (this.compoundTag) {
            return this.compoundTag.getFloat("FallDistance");
        }
    }

    @Override
    public void setFallDistance(float distance) {
        synchronized (this.compoundTag) {
            this.compoundTag.putFloat("FallDistance", distance);
        }
    }

    @Override
    public int getFireTicks() {
        synchronized (this.compoundTag) {
            return this.compoundTag.getInt("Fire");
        }
    }

    @Override
    public void setFireTicks(int ticks) {
        synchronized (this.compoundTag) {
            this.compoundTag.putFloat("Fire", ticks);
        }
    }

    @Override
    public boolean isVisualFire() {
        synchronized (this.compoundTag) {
            return this.compoundTag.getBoolean("HasVisualFire");
        }
    }

    @Override
    public void setVisualFire(boolean fire) {
        synchronized (this.compoundTag) {
            this.compoundTag.putBoolean("HasVisualFire", fire);
        }
    }

    @Override
    public boolean isOnGround() {
        synchronized (this.compoundTag) {
            return this.compoundTag.getBoolean("OnGround");
        }
    }

    @Override
    public void setOnGround(boolean flag) {
        synchronized (this.compoundTag) {
            this.compoundTag.putBoolean("OnGround", flag);
        }
    }

    @Override
    public boolean isInvulnerable() {
        synchronized (this.compoundTag) {
            return this.compoundTag.getBoolean("Invulnerable");
        }
    }

    @Override
    public void setInvulnerable(boolean flag) {
        synchronized (this.compoundTag) {
            this.compoundTag.putBoolean("Invulnerable", flag);
        }
    }

    @Override
    public int getPortalCooldown() {
        synchronized (this.compoundTag) {
            return this.compoundTag.getInt("PortalCooldown");
        }
    }

    @Override
    public void setPortalCooldown(int cooldown) {
        synchronized (this.compoundTag) {
            this.compoundTag.putInt("PortalCooldown", cooldown);
        }
    }

    @Override
    public boolean isCustomNameVisible() {
        synchronized (this.compoundTag) {
            return this.compoundTag.getBoolean("CustomNameVisible");
        }
    }

    @Override
    public void setCustomNameVisible(boolean flag) {
        synchronized (this.compoundTag) {
            this.compoundTag.putBoolean("CustomNameVisible", flag);
        }
    }

    @Nullable
    @Override
    public String getCustomName() {
        String jsonName = null;

        synchronized (this.compoundTag) {
            jsonName = this.compoundTag.getString("CustomName");
        }

        if (jsonName == null)
            return null;

        return CraftChatMessage.fromJSONComponent(jsonName);
    }

    @Override
    public void setCustomName(@Nullable String name) {
        if (name == null) {
            this.compoundTag.remove("CustomName");
            return;
        }

        // sane limit for name length
        if (name.length() > 256)
            name = name.substring(0, 256);

        // format to json
        name = CraftChatMessage.fromStringToJSON(name);

        synchronized (this.compoundTag) {
            this.compoundTag.putString("CustomName", name);
        }
    }

    @Override
    public boolean isSilent() {
        synchronized (this.compoundTag) {
            return this.compoundTag.getBoolean("Silent");
        }
    }

    @Override
    public void setSilent(boolean flag) {
        synchronized (this.compoundTag) {
            this.compoundTag.putBoolean("Silent", flag);
        }
    }

    @Override
    public boolean hasGravity() {
        synchronized (this.compoundTag) {
            return !this.compoundTag.getBoolean("NoGravity");
        }
    }

    @Override
    public void setGravity(boolean gravity) {
        synchronized (this.compoundTag) {
            this.compoundTag.putBoolean("NoGravity", !gravity);
        }
    }

    @Override
    public int getFreezeTicks() {
        synchronized (this.compoundTag) {
            return this.compoundTag.getInt("TicksFrozen");
        }
    }

    @Override
    public void setFreezeTicks(int ticks) {
        synchronized (this.compoundTag) {
            this.compoundTag.putInt("TicksFrozen", ticks);
        }
    }

    @Override
    public boolean isPersistent() {
        synchronized (this.compoundTag) {
            return this.compoundTag.getBoolean("Bukkit.persist");
        }
    }

    @Override
    public void setPersistent(boolean persistent) {
        synchronized (this.compoundTag) {
            this.compoundTag.putBoolean("Bukkit.persist", persistent);
        }
    }

    @Override
    public boolean apply(@NotNull Entity entity) {
        Preconditions.checkArgument(entity != null, "entity cannot be null");
        org.spigotmc.AsyncCatcher.catchOp("apply");
        Preconditions.checkArgument(entity.getType() == this.type, "type of passed entity and type of entity snapshot must match");

        if (!entity.isValid())
            return false;

        synchronized (this.compoundTag) {
            // update world
            if (this.worldName != null) {
                Location targetPos = getLocation();

                if (targetPos.getWorld() == null || !entity.getWorld().equals(targetPos.getWorld()))
                    return false;

            } else {
                // do not move him in this case
                Location pos = entity.getLocation();

                setInternalLocation(pos);
            }

            // remove/change some properties to prevent unintended behaviour
            this.compoundTag.remove("UUID");

            // and finally apply everything...
            try {
                CraftEntity craftEntity = (CraftEntity) entity;

                craftEntity.getHandle().load(this.compoundTag);

                return true;
            } catch(Exception e) {
                throw new IllegalStateException("an error occurred while restoring EntitySnapshot (corrupted data?)", e);
            }
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
            synchronized (compoundTag) {
                put("Type", type.name());

                if (worldName != null)
                    put("WorldName", worldName);

                try {
                    ByteArrayOutputStream buf = new ByteArrayOutputStream();
                    NBTCompressedStreamTools.writeCompressed(compoundTag, buf);

                    put("NBT", Base64.getEncoder().encodeToString(buf.toByteArray()));
                } catch (IOException ex) {
                    Logger.getLogger(CraftEntitySnapshot.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }};
    }

    public static CraftEntitySnapshot deserialize(Map<String, Object> map) throws Throwable {
        Validate.notNull(map, "Cannot deserialize null map");

        if (!map.containsKey("Type"))
            throw new NoSuchElementException(map + " does not contain Type");
        if (!map.containsKey("NBT"))
            throw new NoSuchElementException(map + " does not contain NBT");

        String serializedNBT = (String) map.get("NBT");
        ByteArrayInputStream nbtBuf = new ByteArrayInputStream(Base64.getDecoder().decode(serializedNBT));
        NBTTagCompound nbt = NBTCompressedStreamTools.readCompressed(nbtBuf);

        return new CraftEntitySnapshot(
            EntityType.valueOf((String) map.get("Type")),
            (String) map.get("WorldName"), // nullable
            nbt
        );
    }
}


package org.bukkit.craftbukkit;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.craftbukkit.persistence.CraftPersistentDataContainer;
import org.bukkit.craftbukkit.persistence.CraftPersistentDataTypeRegistry;

public class CraftOfflinePlayerData {
    private static final CraftPersistentDataTypeRegistry DATA_TYPE_REGISTRY = new CraftPersistentDataTypeRegistry();

    public final CraftPersistentDataContainer persistentDataContainer = new CraftPersistentDataContainer(DATA_TYPE_REGISTRY);
    public float experienceProgress;
    public int experienceLevel;
    public int totalExperience;

    public CraftOfflinePlayerData(NBTTagCompound compound) {
        load(compound);
    }

    private void load(NBTTagCompound compound) {
        this.experienceProgress = compound.getFloat("XpP");
        this.experienceLevel = compound.getInt("XpLevel");
        this.totalExperience = compound.getInt("XpTotal");

        readBukkitValues(compound);
    }

    public void save(NBTTagCompound compound) {
        compound.putFloat("XpP", this.experienceProgress);
        compound.putInt("XpLevel", this.experienceLevel);
        compound.putInt("XpTotal", this.totalExperience);

        storeBukkitValues(compound);
    }

    private void storeBukkitValues(NBTTagCompound c) {
        if (!this.persistentDataContainer.isEmpty()) {
            c.put("BukkitValues", this.persistentDataContainer.toTagCompound());
        }
    }

    private void readBukkitValues(NBTTagCompound c) {
        NBTBase base = c.get("BukkitValues");
        if (base instanceof NBTTagCompound) {
            this.persistentDataContainer.putAll((NBTTagCompound) base);
        }
    }
}

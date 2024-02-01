package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.InclusiveRange;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry.b;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.vehicle.EntityMinecartMobSpawner;
import net.minecraft.world.level.MobSpawnerData;
import org.bukkit.block.spawner.SpawnRule;
import org.bukkit.block.spawner.SpawnerEntry;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.minecart.SpawnerMinecart;

final class CraftMinecartMobSpawner extends CraftMinecart implements SpawnerMinecart {
    CraftMinecartMobSpawner(CraftServer server, EntityMinecartMobSpawner entity) {
        super(server, entity);
    }

    @Override
    public EntityType getSpawnedType() {
        MobSpawnerData spawnData = getHandle().getSpawner().nextSpawnData;
        if (spawnData == null) {
            return null;
        }

        Optional<EntityTypes<?>> type = EntityTypes.by(spawnData.getEntityToSpawn());
        return type.map(CraftEntityType::minecraftToBukkit).orElse(null);
    }

    @Override
    public void setSpawnedType(EntityType entityType) {
        if (entityType == null) {
            getHandle().getSpawner().spawnPotentials = SimpleWeightedRandomList.empty(); // need clear the spawnPotentials to avoid nextSpawnData being replaced later
            getHandle().getSpawner().nextSpawnData = new MobSpawnerData();
            return;
        }
        Preconditions.checkArgument(entityType != EntityType.UNKNOWN, "Can't spawn EntityType %s from mob spawners!", entityType);

        RandomSource rand = getHandle().level().getRandom();
        getHandle().getSpawner().setEntityId(CraftEntityType.bukkitToMinecraft(entityType), getHandle().level(), rand, getHandle().blockPosition());
    }

    @Override
    public EntitySnapshot getSpawnedEntity() {
        MobSpawnerData spawnData = getHandle().getSpawner().nextSpawnData;
        if (spawnData == null) {
            return null;
        }

        return CraftEntitySnapshot.create(spawnData.getEntityToSpawn());
    }

    @Override
    public void setSpawnedEntity(EntitySnapshot snapshot) {
        if (snapshot == null) {
            getHandle().getSpawner().spawnPotentials = SimpleWeightedRandomList.empty(); // need clear the spawnPotentials to avoid nextSpawnData being replaced later
            getHandle().getSpawner().nextSpawnData = new MobSpawnerData();
            return;
        }
        NBTTagCompound compoundTag = ((CraftEntitySnapshot) snapshot).getData();

        getHandle().getSpawner().spawnPotentials = SimpleWeightedRandomList.empty();
        getHandle().getSpawner().nextSpawnData = new MobSpawnerData(compoundTag, Optional.empty());
    }

    @Override
    public void addPotentialSpawn(EntitySnapshot snapshot, int weight, SpawnRule spawnRule) {
        Preconditions.checkArgument(snapshot != null, "Snapshot cannot be null");

        NBTTagCompound compoundTag = ((CraftEntitySnapshot) snapshot).getData();

        SimpleWeightedRandomList.a<MobSpawnerData> builder = SimpleWeightedRandomList.builder(); // PAIL rename Builder
        getHandle().getSpawner().spawnPotentials.unwrap().forEach(entry -> builder.add(entry.getData(), entry.getWeight().asInt()));
        builder.add(new MobSpawnerData(compoundTag, Optional.ofNullable(toMinecraftRule(spawnRule))), weight);
        getHandle().getSpawner().spawnPotentials = builder.build();
    }

    @Override
    public void addPotentialSpawn(SpawnerEntry spawnerEntry) {
        Preconditions.checkArgument(spawnerEntry != null, "Entry cannot be null");

        addPotentialSpawn(spawnerEntry.getSnapshot(), spawnerEntry.getSpawnWeight(), spawnerEntry.getSpawnRule());
    }

    @Override
    public void setPotentialSpawns(Collection<SpawnerEntry> entries) {
        Preconditions.checkArgument(entries != null, "Entries cannot be null");

        SimpleWeightedRandomList.a<MobSpawnerData> builder = SimpleWeightedRandomList.builder();
        for (SpawnerEntry spawnerEntry : entries) {
            NBTTagCompound compoundTag = ((CraftEntitySnapshot) spawnerEntry.getSnapshot()).getData();
            builder.add(new MobSpawnerData(compoundTag, Optional.ofNullable(toMinecraftRule(spawnerEntry.getSpawnRule()))), spawnerEntry.getSpawnWeight());
        }
        getHandle().getSpawner().spawnPotentials = builder.build();
    }

    @Override
    public List<SpawnerEntry> getPotentialSpawns() {
        List<SpawnerEntry> entries = new ArrayList<>();

        for (b<MobSpawnerData> entry : getHandle().getSpawner().spawnPotentials.unwrap()) { // PAIL rename Wrapper
            CraftEntitySnapshot snapshot = CraftEntitySnapshot.create(entry.getData().getEntityToSpawn());

            if (snapshot != null) {
                SpawnRule rule = entry.getData().customSpawnRules().map(this::fromMinecraftRule).orElse(null);
                entries.add(new SpawnerEntry(snapshot, entry.getWeight().asInt(), rule));
            }
        }
        return entries;
    }

    private MobSpawnerData.a toMinecraftRule(SpawnRule rule) { // PAIL rename CustomSpawnRules
        if (rule == null) {
            return null;
        }
        return new MobSpawnerData.a(new InclusiveRange<>(rule.getMinBlockLight(), rule.getMaxBlockLight()), new InclusiveRange<>(rule.getMinSkyLight(), rule.getMaxSkyLight()));
    }

    private SpawnRule fromMinecraftRule(MobSpawnerData.a rule) {
       InclusiveRange<Integer> blockLight = rule.blockLightLimit();
       InclusiveRange<Integer> skyLight = rule.skyLightLimit();

       return new SpawnRule(blockLight.maxInclusive(), blockLight.maxInclusive(), skyLight.minInclusive(), skyLight.maxInclusive());
    }

    @Override
    public int getDelay() {
        return getHandle().getSpawner().spawnDelay;
    }

    @Override
    public void setDelay(int delay) {
        getHandle().getSpawner().spawnDelay = delay;
    }

    @Override
    public int getMinSpawnDelay() {
        return getHandle().getSpawner().minSpawnDelay;
    }

    @Override
    public void setMinSpawnDelay(int spawnDelay) {
        Preconditions.checkArgument(spawnDelay <= getMaxSpawnDelay(), "Minimum Spawn Delay must be less than or equal to Maximum Spawn Delay");
        getHandle().getSpawner().minSpawnDelay = spawnDelay;
    }

    @Override
    public int getMaxSpawnDelay() {
        return getHandle().getSpawner().maxSpawnDelay;
    }

    @Override
    public void setMaxSpawnDelay(int spawnDelay) {
        Preconditions.checkArgument(spawnDelay > 0, "Maximum Spawn Delay must be greater than 0.");
        Preconditions.checkArgument(spawnDelay >= getMinSpawnDelay(), "Maximum Spawn Delay must be greater than or equal to Minimum Spawn Delay");
        getHandle().getSpawner().maxSpawnDelay = spawnDelay;
    }

    @Override
    public int getMaxNearbyEntities() {
        return getHandle().getSpawner().maxNearbyEntities;
    }

    @Override
    public void setMaxNearbyEntities(int maxNearbyEntities) {
        getHandle().getSpawner().maxNearbyEntities = maxNearbyEntities;
    }

    @Override
    public int getSpawnCount() {
        return getHandle().getSpawner().spawnCount;
    }

    @Override
    public void setSpawnCount(int count) {
        getHandle().getSpawner().spawnCount = count;
    }

    @Override
    public int getRequiredPlayerRange() {
        return getHandle().getSpawner().requiredPlayerRange;
    }

    @Override
    public void setRequiredPlayerRange(int requiredPlayerRange) {
        getHandle().getSpawner().requiredPlayerRange = requiredPlayerRange;
    }

    @Override
    public int getSpawnRange() {
        return getHandle().getSpawner().spawnRange;
    }

    @Override
    public void setSpawnRange(int spawnRange) {
        getHandle().getSpawner().spawnRange = spawnRange;
    }

    @Override
    public EntityMinecartMobSpawner getHandle() {
        return (EntityMinecartMobSpawner) entity;
    }

    @Override
    public String toString() {
        return "CraftMinecartMobSpawner";
    }
}

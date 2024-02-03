package org.bukkit.craftbukkit.block;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.InclusiveRange;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry.b;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.MobSpawnerData;
import net.minecraft.world.level.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerConfig;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerData;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.TrialSpawner;
import org.bukkit.block.spawner.SpawnRule;
import org.bukkit.block.spawner.SpawnerEntry;
import org.bukkit.craftbukkit.entity.CraftEntitySnapshot;
import org.bukkit.craftbukkit.entity.CraftEntityType;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.loot.LootTable;

public class CraftTrialSpawner extends CraftBlockEntityState<TrialSpawnerBlockEntity> implements TrialSpawner {
    private final TrialSpawnerConfigWrapper config;

    public CraftTrialSpawner(World world, TrialSpawnerBlockEntity tileEntity) {
        super(world, tileEntity);
        this.config = new TrialSpawnerConfigWrapper(tileEntity.getTrialSpawner().getConfig());
    }

    protected CraftTrialSpawner(CraftTrialSpawner state) {
        super(state);
        this.config = state.config;
    }

    @Override
    public EntityType getSpawnedType() {
       SimpleWeightedRandomList<MobSpawnerData> potentials = config.spawnPotentialsDefinition;
       if (potentials.isEmpty()) {
           return null;
       }

       Optional<EntityTypes<?>> type = EntityTypes.by(potentials.unwrap().get(0).getData().getEntityToSpawn());
       return type.map(CraftEntityType::minecraftToBukkit).orElse(null);
    }

    @Override
    public void setSpawnedType(EntityType entityType) {
        if (entityType == null) {
            getTrialData().nextSpawnData = Optional.empty();
            config.spawnPotentialsDefinition = SimpleWeightedRandomList.empty(); // need clear the spawnPotentials to avoid nextSpawnData being replaced later
            return;
        }
        Preconditions.checkArgument(entityType != EntityType.UNKNOWN, "Can't spawn EntityType %s from mob spawners!", entityType);

        MobSpawnerData data = new MobSpawnerData();
        data.getEntityToSpawn().putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(CraftEntityType.bukkitToMinecraft(entityType)).toString());
        getTrialData().nextSpawnData = Optional.of(data);
        config.spawnPotentialsDefinition = SimpleWeightedRandomList.single(data);
    }

    @Override
    public float getBaseSpawnsBeforeCooldown() {
        return config.totalMobs;
    }

    @Override
    public void setBaseSpawnsBeforeCooldown(float amount) {
        config.totalMobs = amount;
    }

    @Override
    public float getBaseSimultaneousEntities() {
        return config.simultaneousMobs;
    }

    @Override
    public void setBaseSimultaneousEntities(float amount) {
        config.simultaneousMobs = amount;
    }

    @Override
    public float getAdditionalSpawnsBeforeCooldown() {
        return config.totalMobsAddedPerPlayer;
    }

    @Override
    public void setAdditionalSpawnsBeforeCooldown(float amount) {
        config.totalMobsAddedPerPlayer = amount;
    }

    @Override
    public float getAdditionalSimultaneousEntities() {
        return config.simultaneousMobsAddedPerPlayer;
    }

    @Override
    public void setAdditionalSimultaneousEntities(float amount) {
        config.simultaneousMobsAddedPerPlayer = amount;
    }

    @Override
    public int getCooldownLength() {
        return config.targetCooldownLength;
    }

    @Override
    public void setCooldownLength(int ticks) {
        config.targetCooldownLength = ticks;
    }

    @Override
    public int getDelay() {
      return config.ticksBetweenSpawn;
    }

    @Override
    public void setDelay(int delay) {
        Preconditions.checkArgument(delay >= 0, "Delay cannot be less than 0");

        config.ticksBetweenSpawn = delay;
    }

    @Override
    public int getRequiredPlayerRange() {
      return config.requiredPlayerRange;
    }

    @Override
    public void setRequiredPlayerRange(int requiredPlayerRange) {
        config.requiredPlayerRange = requiredPlayerRange;
    }

    @Override
    public int getSpawnRange() {
        return config.spawnRange;
    }

    @Override
    public void setSpawnRange(int spawnRange) {
        config.spawnRange = spawnRange;
    }

    @Override
    public Collection<Player> getTrackedPlayers() {
        Set<Player> players = new HashSet<>();

        for (UUID uuid : getTrialData().detectedPlayers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                players.add(player);
            }
        }
        return players;
    }

    @Override
    public boolean isTrackingPlayer(Player player) {
        Preconditions.checkArgument(player != null, "Player cannot be null");

        return getTrialData().detectedPlayers.contains(player.getUniqueId());
    }

    @Override
    public void startTrackingPlayer(Player player) {
        Preconditions.checkArgument(player != null, "Player cannot be null");

        getTrialData().detectedPlayers.add(player.getUniqueId());
    }

    @Override
    public void stopTrackingPlayer(Player player) {
        Preconditions.checkArgument(player != null, "Player cannot be null");

        getTrialData().detectedPlayers.remove(player.getUniqueId());
    }

    @Override
    public Collection<Entity> getTrackedEntities() {
        Set<Entity> entities = new HashSet<>();

        for (UUID uuid : getTrialData().currentMobs) {
            Entity entity = Bukkit.getEntity(uuid);
            if (entity != null) {
                entities.add(entity);
            }
        }
        return entities;
    }

    @Override
    public boolean isTrackingEntity(Entity entity) {
        Preconditions.checkArgument(entity != null, "Entity cannot be null");

        return getTrialData().currentMobs.contains(entity.getUniqueId());
    }

    @Override
    public void startTrackingEntity(Entity entity) {
        Preconditions.checkArgument(entity != null, "Entity cannot be null");

        getTrialData().currentMobs.add(entity.getUniqueId());
    }

    @Override
    public void stopTrackingEntity(Entity entity) {
        Preconditions.checkArgument(entity != null, "Entity cannot be null");

        getTrialData().currentMobs.remove(entity.getUniqueId());
    }

    @Override
    public EntitySnapshot getSpawnedEntity() {
        SimpleWeightedRandomList<MobSpawnerData> potentials = config.spawnPotentialsDefinition;
        if (potentials.isEmpty()) {
            return null;
        }

        return CraftEntitySnapshot.create(potentials.unwrap().get(0).getData().getEntityToSpawn());
    }

    @Override
    public void setSpawnedEntity(EntitySnapshot snapshot) {
        setSpawnedEntity(snapshot, null);
    }

    @Override
    public void setSpawnedEntity(EntitySnapshot snapshot, SpawnRule spawnRule) {
        if (snapshot == null) {
            getTrialData().nextSpawnData = Optional.empty();
            config.spawnPotentialsDefinition = SimpleWeightedRandomList.empty(); // need clear the spawnPotentials to avoid nextSpawnData being replaced later
            return;
        }

        NBTTagCompound compoundTag = ((CraftEntitySnapshot) snapshot).getData();
        MobSpawnerData data = new MobSpawnerData(compoundTag, Optional.ofNullable(toMinecraftRule(spawnRule)));

        getTrialData().nextSpawnData = Optional.of(data);
        config.spawnPotentialsDefinition = SimpleWeightedRandomList.single(data);
    }

    @Override
    public void setSpawnedEntity(SpawnerEntry spawnerEntry) {
        Preconditions.checkArgument(spawnerEntry != null, "Entry cannot be null");

        setSpawnedEntity(spawnerEntry.getSnapshot(), spawnerEntry.getSpawnRule());
    }

    @Override
    public void addPotentialSpawn(EntitySnapshot snapshot, int weight, SpawnRule spawnRule) {
        Preconditions.checkArgument(snapshot != null, "Snapshot cannot be null");

        NBTTagCompound compoundTag = ((CraftEntitySnapshot) snapshot).getData();

        SimpleWeightedRandomList.a<MobSpawnerData> builder = SimpleWeightedRandomList.builder(); // PAIL rename Builder
        config.spawnPotentialsDefinition.unwrap().forEach(entry -> builder.add(entry.getData(), entry.getWeight().asInt()));
        builder.add(new MobSpawnerData(compoundTag, Optional.ofNullable(toMinecraftRule(spawnRule))), weight);
        config.spawnPotentialsDefinition = builder.build();
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
        config.spawnPotentialsDefinition = builder.build();
    }

    @Override
    public List<SpawnerEntry> getPotentialSpawns() {
        List<SpawnerEntry> entries = new ArrayList<>();

        for (b<MobSpawnerData> entry : config.spawnPotentialsDefinition.unwrap()) { // PAIL rename Wrapper
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
    public Map<LootTable, Integer> getPossibleRewards() {
        Map<LootTable, Integer> tables = new HashMap<>();

        for (b<MinecraftKey> entry : config.lootTablesToEject.unwrap()) {
            LootTable table = Bukkit.getLootTable(CraftNamespacedKey.fromMinecraft(entry.getData()));
            if (table != null) {
                tables.put(table, entry.getWeight().asInt());
            }
        }

        return tables;
    }

    @Override
    public void addPossibleReward(LootTable table, int weight) {
        Preconditions.checkArgument(table != null, "Table cannot be null");
        Preconditions.checkArgument(weight >= 1, "Weight must be at least 1");

        SimpleWeightedRandomList.a<MinecraftKey> builder = SimpleWeightedRandomList.builder();
        config.lootTablesToEject.unwrap().forEach(entry -> builder.add(entry.getData(), entry.getWeight().asInt()));
        builder.add(CraftNamespacedKey.toMinecraft(table.getKey()), weight);
        config.lootTablesToEject = builder.build();
    }

    @Override
    public void removePossibleReward(NamespacedKey key) {
        Preconditions.checkArgument(key != null, "Key cannot be null");

        MinecraftKey minecraftKey = CraftNamespacedKey.toMinecraft(key);
        SimpleWeightedRandomList.a<MinecraftKey> builder = SimpleWeightedRandomList.builder();

        for (b<MinecraftKey> entry : config.lootTablesToEject.unwrap()) {
            if (!entry.getData().equals(minecraftKey)) {
                builder.add(entry.getData(), entry.getWeight().asInt());
            }
        }
        config.lootTablesToEject = builder.build();
    }

    @Override
    public void removePossibleReward(LootTable table) {
        Preconditions.checkArgument(table != null, "Table cannot be null");

        removePossibleReward(table.getKey());
    }

    @Override
    public void setPossibleRewards(Map<LootTable, Integer> rewards) {
        if (rewards == null || rewards.isEmpty()) {
            config.lootTablesToEject = SimpleWeightedRandomList.empty();
            return;
        }

        SimpleWeightedRandomList.a<MinecraftKey> builder = SimpleWeightedRandomList.builder();
        rewards.forEach((table, weight) -> {
            Preconditions.checkArgument(table != null, "Table cannot be null");
            Preconditions.checkArgument(weight >= 1, "Weight must be at least 1");

            builder.add(CraftNamespacedKey.toMinecraft(table.getKey()), weight);
        });

        config.lootTablesToEject = builder.build();
    }

    @Override
    protected void applyTo(TrialSpawnerBlockEntity tileEntity) {
        TrialSpawnerBlockEntity snapshot = getSnapshot();
        snapshot.trialSpawner = new net.minecraft.world.level.block.entity.trialspawner.TrialSpawner(config.toMinecraft(), snapshot.getTrialSpawner().getData(), snapshot.getTrialSpawner().stateAccessor, snapshot.getTrialSpawner().getPlayerDetector());

        if (tileEntity != null && tileEntity != snapshot) {
            copyData(snapshot, tileEntity);
        }
    }

    private TrialSpawnerData getTrialData() {
        return getSnapshot().getTrialSpawner().getData();
    }

    @Override
    public CraftTrialSpawner copy() {
        return new CraftTrialSpawner(this);
    }

    static class TrialSpawnerConfigWrapper {
        private int requiredPlayerRange;
        private int spawnRange;
        private float totalMobs;
        private float simultaneousMobs;
        private float totalMobsAddedPerPlayer;
        private float simultaneousMobsAddedPerPlayer;
        private int ticksBetweenSpawn;
        private int targetCooldownLength;
        private SimpleWeightedRandomList<MobSpawnerData> spawnPotentialsDefinition;
        private SimpleWeightedRandomList<MinecraftKey> lootTablesToEject;

        TrialSpawnerConfigWrapper(TrialSpawnerConfig minecraft) {
            this.requiredPlayerRange = minecraft.requiredPlayerRange();
            this.spawnRange = minecraft.spawnRange();
            this.totalMobs = minecraft.totalMobs();
            this.simultaneousMobs = minecraft.simultaneousMobs();
            this.totalMobsAddedPerPlayer = minecraft.totalMobsAddedPerPlayer();
            this.simultaneousMobsAddedPerPlayer = minecraft.simultaneousMobsAddedPerPlayer();
            this.ticksBetweenSpawn = minecraft.ticksBetweenSpawn();
            this.targetCooldownLength = minecraft.targetCooldownLength();
            this.spawnPotentialsDefinition = minecraft.spawnPotentialsDefinition();
            this.lootTablesToEject = minecraft.lootTablesToEject();
        }

        TrialSpawnerConfig toMinecraft() {
            return new TrialSpawnerConfig(requiredPlayerRange, spawnRange, totalMobs, simultaneousMobs, totalMobsAddedPerPlayer, simultaneousMobsAddedPerPlayer, ticksBetweenSpawn, targetCooldownLength, spawnPotentialsDefinition, lootTablesToEject);
        }
    }
}

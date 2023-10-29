package org.bukkit.craftbukkit;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.stats.ServerStatisticManager;
import net.minecraft.stats.StatisticList;
import net.minecraft.stats.StatisticWrapper;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Statistic;
import org.bukkit.block.BlockType;
import org.bukkit.craftbukkit.block.CraftBlockType;
import org.bukkit.craftbukkit.entity.CraftEntityType;
import org.bukkit.craftbukkit.inventory.CraftItemType;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;

public class CraftStatistic implements Statistic {
    private static int count = 0;

    public static Statistic getBukkitStatistic(IRegistry<StatisticWrapper<?>> registry, net.minecraft.stats.Statistic<?> statistic) {
        Preconditions.checkArgument(statistic != null, "NMS Statistic cannot be null");
        IRegistry statRegistry = statistic.getType().getRegistry();
        MinecraftKey nmsKey = registry.getKey(statistic.getType());

        if (statRegistry != null && statRegistry.key().equals(Registries.CUSTOM_STAT)) {
            nmsKey = (MinecraftKey) statistic.getValue();
        }

        return Registry.STATISTIC.get(CraftStatisticRegistry.convert(nmsKey));
    }

    public static net.minecraft.stats.Statistic getNMSStatistic(org.bukkit.Statistic bukkit) {
        Preconditions.checkArgument(bukkit.getType() == Statistic.Type.UNTYPED, "This method only accepts untyped statistics");

        net.minecraft.stats.Statistic<MinecraftKey> nms = StatisticList.CUSTOM.get(CraftStatisticRegistry.convert(bukkit.getKey()));
        Preconditions.checkArgument(nms != null, "NMS Statistic %s does not exist", bukkit);

        return nms;
    }

    public static net.minecraft.stats.Statistic getItemTypeStatistic(org.bukkit.Statistic stat, ItemType itemType) {
        try {
            if (stat == Statistic.CRAFT_ITEM) {
                return StatisticList.ITEM_CRAFTED.get(((CraftItemType) itemType).getHandle());
            }
            if (stat == Statistic.USE_ITEM) {
                return StatisticList.ITEM_USED.get(((CraftItemType) itemType).getHandle());
            }
            if (stat == Statistic.BREAK_ITEM) {
                return StatisticList.ITEM_BROKEN.get(((CraftItemType) itemType).getHandle());
            }
            if (stat == Statistic.PICKUP) {
                return StatisticList.ITEM_PICKED_UP.get(((CraftItemType) itemType).getHandle());
            }
            if (stat == Statistic.DROP) {
                return StatisticList.ITEM_DROPPED.get(((CraftItemType) itemType).getHandle());
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
        return null;
    }

    public static net.minecraft.stats.Statistic getBlockTypeStatistic(org.bukkit.Statistic stat, BlockType<?> blockType) {
        try {
            if (stat == Statistic.MINE_BLOCK) {
                return StatisticList.BLOCK_MINED.get(((CraftBlockType<?>) blockType).getHandle());
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
        return null;
    }

    public static net.minecraft.stats.Statistic getEntityStatistic(org.bukkit.Statistic stat, EntityType<?> entity) {
        Preconditions.checkArgument(entity != null, "EntityType cannot be null");
        if (entity.getName() != null) {
            EntityTypes<?> nmsEntity = CraftEntityType.bukkitToMinecraft(entity);

            if (stat == org.bukkit.Statistic.KILL_ENTITY) {
                return net.minecraft.stats.StatisticList.ENTITY_KILLED.get(nmsEntity);
            }
            if (stat == org.bukkit.Statistic.ENTITY_KILLED_BY) {
                return net.minecraft.stats.StatisticList.ENTITY_KILLED_BY.get(nmsEntity);
            }
        }
        return null;
    }

    public static EntityType<?> getEntityTypeFromStatistic(net.minecraft.stats.Statistic<EntityTypes<?>> statistic) {
        Preconditions.checkArgument(statistic != null, "NMS Statistic cannot be null");
        return CraftEntityType.minecraftToBukkit(statistic.getValue());
    }

    public static ItemType getItemTypeFromStatistic(net.minecraft.stats.Statistic<?> statistic) {
        if (statistic.getValue() instanceof Item statisticItemValue) {
            return CraftItemType.minecraftToBukkit(statisticItemValue);
        }
        return null;
    }

    public static BlockType<?> getBlockTypeFromStatistic(net.minecraft.stats.Statistic<?> statistic) {
        if (statistic.getValue() instanceof Block statisticBlockValue) {
            return CraftBlockType.minecraftToBukkit(statisticBlockValue);
        }
        return null;
    }

    public static void incrementStatistic(ServerStatisticManager manager, Statistic statistic) {
        incrementStatistic(manager, statistic, 1);
    }

    public static void decrementStatistic(ServerStatisticManager manager, Statistic statistic) {
        decrementStatistic(manager, statistic, 1);
    }

    public static int getStatistic(ServerStatisticManager manager, Statistic statistic) {
        Preconditions.checkArgument(statistic != null, "Statistic cannot be null");
        Preconditions.checkArgument(statistic.getType() == Type.UNTYPED, "Must supply additional parameter for this statistic");
        return manager.getValue(CraftStatistic.getNMSStatistic(statistic));
    }

    public static void incrementStatistic(ServerStatisticManager manager, Statistic statistic, int amount) {
        Preconditions.checkArgument(amount > 0, "Amount must be greater than 0");
        setStatistic(manager, statistic, getStatistic(manager, statistic) + amount);
    }

    public static void decrementStatistic(ServerStatisticManager manager, Statistic statistic, int amount) {
        Preconditions.checkArgument(amount > 0, "Amount must be greater than 0");
        setStatistic(manager, statistic, getStatistic(manager, statistic) - amount);
    }

    public static void setStatistic(ServerStatisticManager manager, Statistic statistic, int newValue) {
        Preconditions.checkArgument(statistic != null, "Statistic cannot be null");
        Preconditions.checkArgument(statistic.getType() == Type.UNTYPED, "Must supply additional parameter for this statistic");
        Preconditions.checkArgument(newValue >= 0, "Value must be greater than or equal to 0");
        net.minecraft.stats.Statistic nmsStatistic = CraftStatistic.getNMSStatistic(statistic);
        manager.setValue(null, nmsStatistic, newValue);;
    }

    public static void incrementStatistic(ServerStatisticManager manager, Statistic statistic, ItemType itemType) {
        incrementStatistic(manager, statistic, itemType, 1);
    }

    public static void decrementStatistic(ServerStatisticManager manager, Statistic statistic, ItemType itemType) {
        decrementStatistic(manager, statistic, itemType, 1);
    }

    public static int getStatistic(ServerStatisticManager manager, Statistic statistic, ItemType itemType) {
        Preconditions.checkArgument(statistic != null, "Statistic cannot be null");
        Preconditions.checkArgument(itemType != null, "ItemType cannot be null");
        Preconditions.checkArgument(statistic.getType() == Type.ITEM, "This statistic does not take a ItemType parameter");
        net.minecraft.stats.Statistic nmsStatistic = CraftStatistic.getItemTypeStatistic(statistic, itemType);
        Preconditions.checkArgument(nmsStatistic != null, "The supplied ItemType %s does not have a corresponding statistic", itemType);
        return manager.getValue(nmsStatistic);
    }

    public static void incrementStatistic(ServerStatisticManager manager, Statistic statistic, ItemType itemType, int amount) {
        Preconditions.checkArgument(amount > 0, "Amount must be greater than 0");
        setStatistic(manager, statistic, itemType, getStatistic(manager, statistic, itemType) + amount);
    }

    public static void decrementStatistic(ServerStatisticManager manager, Statistic statistic, ItemType itemType, int amount) {
        Preconditions.checkArgument(amount > 0, "Amount must be greater than 0");
        setStatistic(manager, statistic, itemType, getStatistic(manager, statistic, itemType) - amount);
    }

    public static void setStatistic(ServerStatisticManager manager, Statistic statistic, ItemType itemType, int newValue) {
        Preconditions.checkArgument(statistic != null, "Statistic cannot be null");
        Preconditions.checkArgument(itemType != null, "ItemType cannot be null");
        Preconditions.checkArgument(newValue >= 0, "Value must be greater than or equal to 0");
        Preconditions.checkArgument(statistic.getType() == Type.ITEM, "This statistic does not take a ItemType parameter");
        net.minecraft.stats.Statistic nmsStatistic = CraftStatistic.getItemTypeStatistic(statistic, itemType);
        Preconditions.checkArgument(nmsStatistic != null, "The supplied ItemType %s does not have a corresponding statistic", itemType);
        manager.setValue(null, nmsStatistic, newValue);
    }

    public static void incrementStatistic(ServerStatisticManager manager, Statistic statistic, BlockType<?> blockType) {
        incrementStatistic(manager, statistic, blockType, 1);
    }

    public static void decrementStatistic(ServerStatisticManager manager, Statistic statistic, BlockType<?> blockType) {
        decrementStatistic(manager, statistic, blockType, 1);
    }

    public static int getStatistic(ServerStatisticManager manager, Statistic statistic, BlockType<?> blockType) {
        Preconditions.checkArgument(statistic != null, "Statistic cannot be null");
        Preconditions.checkArgument(blockType != null, "BlockType cannot be null");
        Preconditions.checkArgument(statistic.getType() == Type.BLOCK, "This statistic does not take a BlockType parameter");
        net.minecraft.stats.Statistic nmsStatistic = CraftStatistic.getBlockTypeStatistic(statistic, blockType);
        Preconditions.checkArgument(nmsStatistic != null, "The supplied BlockType %s does not have a corresponding statistic", blockType);
        return manager.getValue(nmsStatistic);
    }

    public static void incrementStatistic(ServerStatisticManager manager, Statistic statistic, BlockType<?> blockType, int amount) {
        Preconditions.checkArgument(amount > 0, "Amount must be greater than 0");
        setStatistic(manager, statistic, blockType, getStatistic(manager, statistic, blockType) + amount);
    }

    public static void decrementStatistic(ServerStatisticManager manager, Statistic statistic, BlockType<?> blockType, int amount) {
        Preconditions.checkArgument(amount > 0, "Amount must be greater than 0");
        setStatistic(manager, statistic, blockType, getStatistic(manager, statistic, blockType) - amount);
    }

    public static void setStatistic(ServerStatisticManager manager, Statistic statistic, BlockType<?> blockType, int newValue) {
        Preconditions.checkArgument(statistic != null, "Statistic cannot be null");
        Preconditions.checkArgument(blockType != null, "BlocKType cannot be null");
        Preconditions.checkArgument(newValue >= 0, "Value must be greater than or equal to 0");
        Preconditions.checkArgument(statistic.getType() == Type.BLOCK, "This statistic does not take a BlockType parameter");
        net.minecraft.stats.Statistic nmsStatistic = CraftStatistic.getBlockTypeStatistic(statistic, blockType);
        Preconditions.checkArgument(nmsStatistic != null, "The supplied BlockType %s does not have a corresponding statistic", blockType);
        manager.setValue(null, nmsStatistic, newValue);
    }

    public static void incrementStatistic(ServerStatisticManager manager, Statistic statistic, EntityType<?> entityType) {
        incrementStatistic(manager, statistic, entityType, 1);
    }

    public static void decrementStatistic(ServerStatisticManager manager, Statistic statistic, EntityType<?> entityType) {
        decrementStatistic(manager, statistic, entityType, 1);
    }

    public static int getStatistic(ServerStatisticManager manager, Statistic statistic, EntityType<?> entityType) {
        Preconditions.checkArgument(statistic != null, "Statistic cannot be null");
        Preconditions.checkArgument(entityType != null, "EntityType cannot be null");
        Preconditions.checkArgument(statistic.getType() == Type.ENTITY, "This statistic does not take an EntityType parameter");
        net.minecraft.stats.Statistic nmsStatistic = CraftStatistic.getEntityStatistic(statistic, entityType);
        Preconditions.checkArgument(nmsStatistic != null, "The supplied EntityType %s does not have a corresponding statistic", entityType);
        return manager.getValue(nmsStatistic);
    }

    public static void incrementStatistic(ServerStatisticManager manager, Statistic statistic, EntityType<?> entityType, int amount) {
        Preconditions.checkArgument(amount > 0, "Amount must be greater than 0");
        setStatistic(manager, statistic, entityType, getStatistic(manager, statistic, entityType) + amount);
    }

    public static void decrementStatistic(ServerStatisticManager manager, Statistic statistic, EntityType<?> entityType, int amount) {
        Preconditions.checkArgument(amount > 0, "Amount must be greater than 0");
        setStatistic(manager, statistic, entityType, getStatistic(manager, statistic, entityType) - amount);
    }

    public static void setStatistic(ServerStatisticManager manager, Statistic statistic, EntityType<?> entityType, int newValue) {
        Preconditions.checkArgument(statistic != null, "Statistic cannot be null");
        Preconditions.checkArgument(entityType != null, "EntityType cannot be null");
        Preconditions.checkArgument(newValue >= 0, "Value must be greater than or equal to 0");
        Preconditions.checkArgument(statistic.getType() == Type.ENTITY, "This statistic does not take an EntityType parameter");
        net.minecraft.stats.Statistic nmsStatistic = CraftStatistic.getEntityStatistic(statistic, entityType);
        Preconditions.checkArgument(nmsStatistic != null, "The supplied EntityType %s does not have a corresponding statistic", entityType);
        manager.setValue(null, nmsStatistic, newValue);
    }

    private final NamespacedKey key;
    private final Type type;
    private final String name;
    private final int ordinal;

    public CraftStatistic(NamespacedKey key, Type type) {
        this.key = key;
        this.type = type;
        // For backwards compatibility, minecraft values will stile return the uppercase name without the namespace,
        // in case plugins use for example the name as key in a config file to receive statistic specific values.
        // Custom statistics will return the key with namespace. For a plugin this should look than like a new statistic
        // (which can always be added in new minecraft versions and the plugin should therefore handle it accordingly).
        if (NamespacedKey.MINECRAFT.equals(key.getNamespace())) {
            this.name = key.getKey().toUpperCase();
        } else {
            this.name = key.toString();
        }
        this.ordinal = count++;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public boolean isSubstatistic() {
        return type != Type.UNTYPED;
    }

    @Override
    public boolean isBlock() {
        return type == Type.BLOCK;
    }

    @Override
    public NamespacedKey getKey() {
        return key;
    }

    @Override
    public int compareTo(Statistic statistic) {
        return ordinal - statistic.ordinal();
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int ordinal() {
        return ordinal;
    }

    @Override
    public String toString() {
        // For backwards compatibility
        return name();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof CraftStatistic)) {
            return false;
        }

        return getKey().equals(((CraftStatistic) other).getKey());
    }

    @Override
    public int hashCode() {
        return getKey().hashCode();
    }

    public static class CraftStatisticRegistry implements Registry<Statistic> {
        private static final NamespacedKey CUSTOM_BUKKIT = NamespacedKey.minecraft("custom");
        private static final MinecraftKey CUSTOM_MINECRAFT = CraftNamespacedKey.toMinecraft(CUSTOM_BUKKIT);
        private static final BiMap<NamespacedKey, MinecraftKey> STATISTIC_KEYS = HashBiMap.create();

        private static void add(String bukkit, MinecraftKey minecraft) {
            STATISTIC_KEYS.put(NamespacedKey.fromString(bukkit), minecraft);
        }

        static {
            add("drop_count", StatisticList.DROP);
            add("drop", new MinecraftKey("dropped"));
            add("pickup", new MinecraftKey("picked_up"));
            add("play_one_minute", StatisticList.PLAY_TIME);
            add("mine_block", new MinecraftKey("mined"));
            add("use_item", new MinecraftKey("used"));
            add("break_item", new MinecraftKey("broken"));
            add("craft_item", new MinecraftKey("crafted"));
            add("kill_entity", new MinecraftKey("killed"));
            add("entity_killed_by", new MinecraftKey("killed_by"));
            add("cake_slices_eaten", StatisticList.EAT_CAKE_SLICE);
            add("cauldron_filled", StatisticList.FILL_CAULDRON);
            add("cauldron_used", StatisticList.USE_CAULDRON);
            add("armor_cleaned", StatisticList.CLEAN_ARMOR);
            add("banner_cleaned", StatisticList.CLEAN_BANNER);
            add("brewingstand_interaction", StatisticList.INTERACT_WITH_BREWINGSTAND);
            add("beacon_interaction", StatisticList.INTERACT_WITH_BEACON);
            add("dropper_inspected", StatisticList.INSPECT_DROPPER);
            add("hopper_inspected", StatisticList.INSPECT_HOPPER);
            add("dispenser_inspected", StatisticList.INSPECT_DISPENSER);
            add("noteblock_played", StatisticList.PLAY_NOTEBLOCK);
            add("noteblock_tuned", StatisticList.TUNE_NOTEBLOCK);
            add("flower_potted", StatisticList.POT_FLOWER);
            add("trapped_chest_triggered", StatisticList.TRIGGER_TRAPPED_CHEST);
            add("enderchest_opened", StatisticList.OPEN_ENDERCHEST);
            add("item_enchanted", StatisticList.ENCHANT_ITEM);
            add("record_played", StatisticList.PLAY_RECORD);
            add("furnace_interaction", StatisticList.INTERACT_WITH_FURNACE);
            add("crafting_table_interaction", StatisticList.INTERACT_WITH_CRAFTING_TABLE);
            add("chest_opened", StatisticList.OPEN_CHEST);
            add("shulker_box_opened", StatisticList.OPEN_SHULKER_BOX);
        }

        public static NamespacedKey convert(MinecraftKey minecraft) {
            NamespacedKey key = STATISTIC_KEYS.inverse().get(minecraft);

            if (key == null) {
                return CraftNamespacedKey.fromMinecraft(minecraft);
            }

            return key;
        }

        public static MinecraftKey convert(NamespacedKey bukkit) {
            MinecraftKey key = STATISTIC_KEYS.get(bukkit);

            if (key == null) {
                key = CraftNamespacedKey.toMinecraft(bukkit);
            }

            return StatisticList.CUSTOM.getRegistry().getOptional(key).orElse(key);
        }

        private final Map<NamespacedKey, Statistic> cache = new HashMap<>();
        private final IRegistry<StatisticWrapper<?>> statisticWrapperRegistry;

        public CraftStatisticRegistry(IRegistry<StatisticWrapper<?>> statisticWrapperRegistry) {
            this.statisticWrapperRegistry = statisticWrapperRegistry;
        }

        @Override
        public Statistic get(NamespacedKey namespacedKey) {
            Statistic cached = cache.get(namespacedKey);
            if (cached != null) {
                return cached;
            }

            // Custom is a collection of stats.
            if (CUSTOM_BUKKIT.equals(namespacedKey)) {
                return null;
            }

            MinecraftKey key = convert(namespacedKey);

            // First check in custom stat
            if (StatisticList.CUSTOM.contains(key)) {
                Statistic bukkit = new CraftStatistic(namespacedKey, Type.UNTYPED);
                cache.put(namespacedKey, bukkit);
                return bukkit;
            }

            StatisticWrapper<?> statisticWrapper = statisticWrapperRegistry.getOptional(key).orElse(null);

            if (statisticWrapper == null) {
                return null;
            }

            Type type = null;
            IRegistry<?> registry = statisticWrapper.getRegistry();
            if (registry != null && registry.key().equals(Registries.ITEM)) {
                type = Type.ITEM;
            } else if (registry != null && registry.key().equals(Registries.BLOCK)) {
                type = Type.BLOCK;
            } else if (registry != null && registry.key().equals(Registries.ENTITY_TYPE)) {
                type = Type.ENTITY;
            }

            Preconditions.checkArgument(type != null, "No statistic type found for registry ", statisticWrapper.getRegistry());

            Statistic bukkit = new CraftStatistic(namespacedKey, type);
            cache.put(namespacedKey, bukkit);
            return bukkit;
        }

        @NotNull
        @Override
        public Stream<Statistic> stream() {
            Stream<MinecraftKey> custom = StreamSupport.stream(StatisticList.CUSTOM.spliterator(), false).map(net.minecraft.stats.Statistic::getValue);
            Stream<MinecraftKey> type = statisticWrapperRegistry.keySet().stream().filter(key -> !CUSTOM_MINECRAFT.equals(key));

            return Stream.concat(custom, type).map(CraftStatisticRegistry::convert).map(this::get);
        }

        @Override
        public Iterator<Statistic> iterator() {
           return stream().iterator();
        }
    }
}

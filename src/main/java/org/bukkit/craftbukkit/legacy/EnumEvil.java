package org.bukkit.craftbukkit.legacy;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.lang.reflect.Array;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.world.item.Item;
import org.bukkit.Art;
import org.bukkit.Bukkit;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Fluid;
import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.RegionAccessor;
import org.bukkit.Registry;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.BlockType;
import org.bukkit.block.DecoratedPot;
import org.bukkit.block.Jukebox;
import org.bukkit.block.banner.PatternType;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.tag.CraftTag;
import org.bukkit.craftbukkit.util.ClassTraverser;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Cat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Frog;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.Player;
import org.bukkit.entity.Steerable;
import org.bukkit.entity.Villager;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.player.PlayerBucketEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.StonecuttingRecipe;
import org.bukkit.inventory.meta.BlockDataMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.packs.DataPackManager;
import org.bukkit.potion.PotionType;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.util.OldEnum;

/**
 * @deprecated only for legacy use, do not use
 */
@Deprecated
public class EnumEvil {

    private static final Map<Class<?>, Registry<?>> REGISTRIES = new HashMap<>();

    static {
        REGISTRIES.put(Biome.class, Registry.BIOME);
        REGISTRIES.put(Art.class, Registry.ART);
        REGISTRIES.put(Fluid.class, Registry.FLUID);
        REGISTRIES.put(EntityType.class, Registry.ENTITY_TYPE);
        REGISTRIES.put(Statistic.class, Registry.STATISTIC);
        REGISTRIES.put(Sound.class, Registry.SOUNDS);
        REGISTRIES.put(Attribute.class, Registry.ATTRIBUTE);
        REGISTRIES.put(Villager.Type.class, Registry.VILLAGER_TYPE);
        REGISTRIES.put(Villager.Profession.class, Registry.VILLAGER_PROFESSION);
        REGISTRIES.put(Frog.Variant.class, Registry.FROG_VARIANT);
        REGISTRIES.put(Cat.Type.class, Registry.CAT_TYPE);
        REGISTRIES.put(PatternType.class, Registry.BANNER_PATTERN);
        REGISTRIES.put(Particle.class, Registry.PARTICLE_TYPE);
        REGISTRIES.put(PotionType.class, Registry.POTION);
    }

    public static Registry<?> getRegistry(Class<?> clazz) {
        ClassTraverser it = new ClassTraverser(clazz);
        Registry registry;
        while (it.hasNext()) {
            registry = REGISTRIES.get(it.next());
            if (registry != null) {
                return registry;
            }
        }

        return null;
    }

    public static Object[] getEnumConstants(Class<?> clazz) {
        if (clazz.isEnum()) {
            return clazz.getEnumConstants();
        }

        Registry<?> registry = getRegistry(clazz);

        if (registry == null) {
            return clazz.getEnumConstants();
        }

        // Need to do this in such away to avoid ClassCastException
        List<?> values = Lists.newArrayList(registry);
        Object array = Array.newInstance(clazz, values.size());

        for (int i = 0; i < values.size(); i++) {
            Array.set(array, i, values.get(i));
        }

        return (Object[]) array;
    }

    public static Material getMaterial(BlockData blockData) {
        return CraftMagicNumbers.toMaterial(blockData.getBlockType());
    }

    public static Material getPlacementMaterial(BlockData blockData) {
        return CraftMagicNumbers.INSTANCE.toMaterial(blockData.getPlacementType());
    }

    public static Material getType(Block block) {
        return CraftMagicNumbers.toMaterial(block.getType());
    }

    public static void setType(Block block, Material material) {
        block.setType(material.asBlockType());
    }

    public static void setType(Block block, Material type, boolean applyPhysics) {
        block.setType(type.asBlockType(), applyPhysics);
    }

    public static Material getType(BlockState blockState) {
        return CraftMagicNumbers.toMaterial(blockState.getType());
    }

    public static void setType(BlockState blockState, Material material) {
        blockState.setType(material.asBlockType());
    }

    public static List<Material> getShards(DecoratedPot decoratedPot) {
        return decoratedPot.getShards().stream().map(CraftMagicNumbers.INSTANCE::toMaterial).toList();
    }

    public static void setSherd(DecoratedPot decoratedPot, DecoratedPot.Side side, Material material) {
        decoratedPot.setSherd(side, material == null ? null : material.asItemType());
    }

    public static Material getSherd(DecoratedPot decoratedPot, DecoratedPot.Side side) {
        return CraftMagicNumbers.INSTANCE.toMaterial(decoratedPot.getSherd(side));
    }

    public static Map<DecoratedPot.Side, Material> getSherds(DecoratedPot decoratedPot) {
        Map<DecoratedPot.Side, ItemType> map = decoratedPot.getSherds();
        Map<DecoratedPot.Side, Material> sherds = new EnumMap<>(DecoratedPot.Side.class);

        sherds.put(DecoratedPot.Side.BACK, CraftMagicNumbers.INSTANCE.toMaterial(map.get(DecoratedPot.Side.BACK)));
        sherds.put(DecoratedPot.Side.LEFT, CraftMagicNumbers.INSTANCE.toMaterial(map.get(DecoratedPot.Side.LEFT)));
        sherds.put(DecoratedPot.Side.RIGHT, CraftMagicNumbers.INSTANCE.toMaterial(map.get(DecoratedPot.Side.RIGHT)));
        sherds.put(DecoratedPot.Side.FRONT, CraftMagicNumbers.INSTANCE.toMaterial(map.get(DecoratedPot.Side.FRONT)));

        return sherds;
    }

    public static Material getPlaying(Jukebox jukebox) {
        return CraftMagicNumbers.INSTANCE.toMaterial(jukebox.getPlaying());
    }

    public static void setPlaying(Jukebox jukebox, Material material) {
        jukebox.setPlaying(material.asItemType());
    }

    public static boolean includes(EnchantmentTarget enchantmentTarget, Material material) {
        return enchantmentTarget.includes(material.asItemType());
    }

    public static boolean isBreedItem(Animals animals, Material material) {
        return animals.isBreedItem(material.asItemType());
    }

    public static Material getMaterial(Boat.Type type) {
        return CraftMagicNumbers.INSTANCE.toMaterial(type.getItemType());
    }

    public static Material getMaterial(FallingBlock fallingBlock) {
        return CraftMagicNumbers.toMaterial(fallingBlock.getBlockData().getBlockType());
    }

    public static boolean hasCooldown(HumanEntity humanEntity, Material material) {
        return humanEntity.hasCooldown(material.asItemType());
    }

    public static int getCooldown(HumanEntity humanEntity, Material material) {
        return humanEntity.getCooldown(material.asItemType());
    }

    public static void setCooldown(HumanEntity humanEntity, Material material, int ticks) {
        humanEntity.setCooldown(material.asItemType(), ticks);
    }

    public static List<Block> getLineOfSight(LivingEntity livingEntity, Set<Material> set, int maxDistance) {
        return livingEntity.getLineOfSight(set.stream().map(Material::asBlockType).collect(Collectors.toSet()), maxDistance);
    }

    public static Block getTargetBlock(LivingEntity livingEntity, Set<Material> set, int maxDistance) {
        return livingEntity.getTargetBlock(set.stream().map(Material::asBlockType).collect(Collectors.toSet()), maxDistance);
    }

    public static List<Block> getLastTwoTargetBlocks(LivingEntity livingEntity, Set<Material> set, int maxDistance) {
        return livingEntity.getLastTwoTargetBlocks(set.stream().map(Material::asBlockType).collect(Collectors.toSet()), maxDistance);
    }

    public static boolean addBarterMaterial(Piglin piglin, Material material) {
        return piglin.addBarterItem(material.asItemType());
    }

    public static boolean removeBarterMaterial(Piglin piglin, Material material) {
        return piglin.removeBarterItem(material.asItemType());
    }

    public static boolean addMaterialOfInterest(Piglin piglin, Material material) {
        return piglin.addItemOfInterest(material.asItemType());
    }

    public static boolean removeMaterialOfInterest(Piglin piglin, Material material) {
        return piglin.removeItemOfInterest(material.asItemType());
    }

    public static Set<Material> getInterestList(Piglin piglin) {
        return piglin.getInterestList().stream().map(CraftMagicNumbers.INSTANCE::toMaterial).collect(Collectors.toSet());
    }

    public static Set<Material> getBarterList(Piglin piglin) {
        return piglin.getBarterList().stream().map(CraftMagicNumbers.INSTANCE::toMaterial).collect(Collectors.toSet());
    }

    public static void sendBlockChange(Player player, Location loc, Material material, byte data) {
        player.sendBlockChange(loc, CraftMagicNumbers.INSTANCE.fromLegacy(material, data).getBlockType().createBlockData());
    }

    public static Material getSteerMaterial(Steerable steerable) {
        return CraftMagicNumbers.INSTANCE.toMaterial(steerable.getSteerItem());
    }

    public static Material getMaterial(BlockCanBuildEvent event) {
        return CraftMagicNumbers.toMaterial(event.getType());
    }

    public static Material getChangedType(BlockPhysicsEvent event) {
        return CraftMagicNumbers.toMaterial(event.getChangedType());
    }

    public static Material getTo(EntityChangeBlockEvent event) {
        return CraftMagicNumbers.toMaterial(event.getTo());
    }

    public static Material getBucket(PlayerBucketEvent event) {
        return CraftMagicNumbers.INSTANCE.toMaterial(event.getBucket());
    }

    public static Material getMaterial(PlayerInteractEvent event) {
        return CraftMagicNumbers.INSTANCE.toMaterial(event.getItemType());
    }

    public static Material getItemType(FurnaceExtractEvent event) {
        return CraftMagicNumbers.INSTANCE.toMaterial(event.getItemType());
    }

    public static Material getMaterial(PlayerStatisticIncrementEvent event) {
        if (event.getStatistic().getType() == Statistic.Type.ITEM) {
            return CraftMagicNumbers.INSTANCE.toMaterial(event.getItemType());
        } else if (event.getStatistic().getType() == Statistic.Type.BLOCK) {
            return CraftMagicNumbers.toMaterial(event.getBlockType());
        }

        return null;
    }

    public static void setBlock(ChunkGenerator.ChunkData chunkData, int x, int y, int z, Material material) {
        chunkData.setBlock(x, y, z, material.asBlockType());
    }

    public static void setRegion(ChunkGenerator.ChunkData chunkData, int xMin, int yMin, int zMin, int xMax, int yMax, int zMax, Material material) {
        chunkData.setRegion(xMin, yMin, zMin, xMax, yMax, zMax, material.asBlockType());
    }

    public static Material getType(ChunkGenerator.ChunkData chunkData, int x, int y, int z) {
        return CraftMagicNumbers.toMaterial(chunkData.getType(x, y, z));
    }

    public static Material getType(ItemStack itemStack) {
        return CraftMagicNumbers.INSTANCE.toMaterial(itemStack.getType());
    }

    public static void setType(ItemStack itemStack, Material material) {
        itemStack.setType(material.asItemType());
    }

    public static CookingRecipe setInput(CookingRecipe<?> cookingRecipe, Material input) {
        return cookingRecipe.setInput(input.asItemType());
    }

    public static FurnaceRecipe setInput(FurnaceRecipe furnaceRecipe, Material input) {
        return furnaceRecipe.setInput(input.asItemType());
    }

    public static FurnaceRecipe setInput(FurnaceRecipe furnaceRecipe, Material input, int data) {
        return furnaceRecipe.setInput(input.asItemType());
    }

    public static StonecuttingRecipe setInput(StonecuttingRecipe stonecuttingRecipe, Material input, int data) {
        return stonecuttingRecipe.setInput(input.asItemType());
    }

    public static boolean contains(Inventory inventory, Material material) {
        return inventory.contains(material.asItemType());
    }

    public static boolean contains(Inventory inventory, Material material, int amount) {
        return inventory.contains(material.asItemType(), amount);
    }

    public static HashMap<Integer, ? extends org.bukkit.inventory.ItemStack> all(Inventory inventory, Material material) {
        return inventory.all(material.asItemType());
    }

    public static int first(Inventory inventory, Material material) {
        return inventory.first(material.asItemType());
    }

    public static void remove(Inventory inventory, Material material) {
        inventory.remove(material.asItemType());
    }

    public static ItemMeta getItemMeta(ItemFactory itemFactory, Material material) {
        return itemFactory.getItemMeta(material.asItemType());
    }

    public static boolean isApplicable(ItemFactory itemFactory, ItemMeta meta, Material material) {
        return itemFactory.isApplicable(meta, material.asItemType());
    }

    public static ItemMeta asMetaFor(ItemFactory itemFactory, ItemMeta meta, Material material) {
        return itemFactory.asMetaFor(meta, material.asItemType());
    }

    public static ShapedRecipe setIngredient(ShapedRecipe shapedRecipe, char key, Material ingredient) {
        return shapedRecipe.setIngredient(key, ingredient.asItemType());
    }

    public static void setIngredient(ShapedRecipe shapedRecipe, char key, Material ingredient, int raw) {
        shapedRecipe.setIngredient(key, ingredient.asItemType());
    }

    public static ShapelessRecipe addIngredient(ShapelessRecipe shapelessRecipe, Material ingredient) {
        return shapelessRecipe.addIngredient(ingredient.asItemType());
    }

    public static ShapelessRecipe addIngredient(ShapelessRecipe shapelessRecipe, Material ingredient, int rawdata) {
        return shapelessRecipe.addIngredient(ingredient.asItemType());
    }

    public static ShapelessRecipe addIngredient(ShapelessRecipe shapelessRecipe, int count, Material ingredient) {
        return shapelessRecipe.addIngredient(count, ingredient.asItemType());
    }

    public static ShapelessRecipe addIngredient(ShapelessRecipe shapelessRecipe, int count, Material ingredient, int rawdata) {
        return shapelessRecipe.addIngredient(count, ingredient.asItemType());
    }

    public static ShapelessRecipe removeIngredient(ShapelessRecipe shapelessRecipe, Material ingredient) {
        return shapelessRecipe.removeIngredient(ingredient.asItemType());
    }

    public static ShapelessRecipe removeIngredient(ShapelessRecipe shapelessRecipe, int count, Material ingredient) {
        return shapelessRecipe.removeIngredient(count, ingredient.asItemType());
    }

    public static ShapelessRecipe removeIngredient(ShapelessRecipe shapelessRecipe, Material ingredient, int rawdata) {
        return shapelessRecipe.removeIngredient(ingredient.asItemType());
    }

    public static ShapelessRecipe removeIngredient(ShapelessRecipe shapelessRecipe, int count, Material ingredient, int rawdata) {
        return shapelessRecipe.removeIngredient(count, ingredient.asItemType());
    }

    public static BlockData getBlockData(BlockDataMeta blockDataMeta, Material material) {
        return blockDataMeta.getBlockData(material.asBlockType());
    }

    public static Criteria statistic(Statistic statistic, Material material) {
        if (statistic.getType() == Statistic.Type.ITEM) {
            return Criteria.statistic(statistic, material.asItemType());
        } else if (statistic.getType() == Statistic.Type.BLOCK) {
            return Criteria.statistic(statistic, material.asBlockType());
        }
        return Criteria.statistic(statistic);
    }

    public static BlockData createBlockData(Material material) {
        return Bukkit.createBlockData(material.asBlockType());
    }

    public static BlockData createBlockData(Material material, Consumer<BlockData> consumer) {
        return Bukkit.createBlockData(material.asBlockType(), consumer::accept);
    }

    public static BlockData createBlockData(Material material, String data) {
        return Bukkit.createBlockData(material.asBlockType(), data);
    }

    public static Material getBlockType(ChunkSnapshot chunkSnapshot, int x, int y, int z) {
        return CraftMagicNumbers.toMaterial(chunkSnapshot.getBlockType(x, y, z));
    }

    public static void incrementStatistic(OfflinePlayer offlinePlayer, Statistic statistic, Material material) {
        if (statistic.getType() == Statistic.Type.ITEM) {
            offlinePlayer.incrementStatistic(statistic, material.asItemType());
        } else if (statistic.getType() == Statistic.Type.BLOCK) {
            offlinePlayer.incrementStatistic(statistic, material.asBlockType());
        } else {
            offlinePlayer.incrementStatistic(statistic);
        }
    }

    public static void decrementStatistic(OfflinePlayer offlinePlayer, Statistic statistic, Material material) {
        if (statistic.getType() == Statistic.Type.ITEM) {
            offlinePlayer.decrementStatistic(statistic, material.asItemType());
        } else if (statistic.getType() == Statistic.Type.BLOCK) {
            offlinePlayer.decrementStatistic(statistic, material.asBlockType());
        } else {
            offlinePlayer.decrementStatistic(statistic);
        }
    }

    public static int getStatistic(OfflinePlayer offlinePlayer, Statistic statistic, Material material) {
        if (statistic.getType() == Statistic.Type.ITEM) {
            return offlinePlayer.getStatistic(statistic, material.asItemType());
        } else if (statistic.getType() == Statistic.Type.BLOCK) {
            return offlinePlayer.getStatistic(statistic, material.asBlockType());
        }

        return offlinePlayer.getStatistic(statistic);
    }

    public static void incrementStatistic(OfflinePlayer offlinePlayer, Statistic statistic, Material material, int amount) {
        if (statistic.getType() == Statistic.Type.ITEM) {
            offlinePlayer.incrementStatistic(statistic, material.asItemType(), amount);
        } else if (statistic.getType() == Statistic.Type.BLOCK) {
            offlinePlayer.incrementStatistic(statistic, material.asBlockType(), amount);
        } else {
            offlinePlayer.incrementStatistic(statistic, amount);
        }
    }

    public static void decrementStatistic(OfflinePlayer offlinePlayer, Statistic statistic, Material material, int amount) {
        if (statistic.getType() == Statistic.Type.ITEM) {
            offlinePlayer.incrementStatistic(statistic, material.asItemType(), amount);
        } else if (statistic.getType() == Statistic.Type.BLOCK) {
            offlinePlayer.incrementStatistic(statistic, material.asBlockType(), amount);
        } else {
            offlinePlayer.incrementStatistic(statistic, amount);
        }
    }

    public static void setStatistic(OfflinePlayer offlinePlayer, Statistic statistic, Material material, int newValue) {
        if (statistic.getType() == Statistic.Type.ITEM) {
            offlinePlayer.incrementStatistic(statistic, material.asItemType(), newValue);
        } else if (statistic.getType() == Statistic.Type.BLOCK) {
            offlinePlayer.incrementStatistic(statistic, material.asBlockType(), newValue);
        } else {
            offlinePlayer.incrementStatistic(statistic, newValue);
        }
    }

    public static Material getType(RegionAccessor regionAccessor, Location location) {
        return CraftMagicNumbers.toMaterial(regionAccessor.getType(location));
    }

    public static Material getType(RegionAccessor regionAccessor, int x, int y, int z) {
        return CraftMagicNumbers.toMaterial(regionAccessor.getType(x, y, z));
    }

    public static void setType(RegionAccessor regionAccessor, Location location, Material material) {
        regionAccessor.setType(location, material.asBlockType());
    }

    public static void setType(RegionAccessor regionAccessor, int x, int y, int z, Material material) {
        regionAccessor.setType(x, y, z, material.asBlockType());
    }

    public static BlockData createBlockData(Server server, Material material) {
        return server.createBlockData(material.asBlockType());
    }

    public static BlockData createBlockData(Server server, Material material, Consumer<BlockData> consumer) {
        return server.createBlockData(material.asBlockType(), consumer::accept);
    }

    public static BlockData createBlockData(Server server, Material material, String data) {
        return server.createBlockData(material.asBlockType(), data);
    }

    public static FallingBlock spawnFallingBlock(World world, Location location, Material material, byte data) {
        return world.spawnFallingBlock(location, material.asBlockType().createBlockData());
    }

    public static boolean isTagged(Tag<Keyed> tag, Keyed keyed) {
        if (!(keyed instanceof Material material)) {
            return tag.isTagged(keyed);
        }

        Set<Keyed> values = tag.getValues();

        if (values.isEmpty()) {
            return false;
        }

        Object value = values.iterator().next();
        if (value instanceof ItemType) {
            Item minecraft = CraftMagicNumbers.getItem(material);

            // SPIGOT-6952: A Material is not necessary an item, in this case return false
            if (minecraft == null) {
                return false;
            }

            return minecraft.builtInRegistryHolder().is(((CraftTag<Item, ?>) tag).getTagKey());
        } else if (value instanceof BlockType<?>) {
            net.minecraft.world.level.block.Block block = CraftMagicNumbers.getBlock(material);

            // SPIGOT-6952: A Material is not necessary a block, in this case return false
            if (block == null) {
                return false;
            }

            return block.builtInRegistryHolder().is(((CraftTag<net.minecraft.world.level.block.Block, ?>) tag).getTagKey());
        }

        return tag.isTagged(keyed);
    }

    public static Set<Keyed> getValues(Tag<Keyed> tag) {
        Set<Keyed> values = tag.getValues();
        if (values.isEmpty()) {
            return values;
        }

        Object value = values.iterator().next();
        if (value instanceof ItemType) {
            return values.stream().map(val -> (ItemType) val).map(CraftMagicNumbers.INSTANCE::toMaterial).collect(Collectors.toSet());
        } else if (value instanceof BlockType<?>) {
            return values.stream().map(val -> (BlockType<?>) val).map(CraftMagicNumbers::toMaterial).collect(Collectors.toSet());
        }

        return values;
    }

    public static String name(Object object) {
        if (object instanceof OldEnum<?>) {
            return ((OldEnum<?>) object).name();
        }

        return ((Enum<?>) object).name();
    }

    public static int compareTo(Object object, Object other) {
        if (object instanceof OldEnum<?>) {
            return ((OldEnum) object).compareTo((OldEnum) other);
        }

        return ((Enum) object).compareTo((Enum) other);
    }

    public static Class<?> getDeclaringClass(Object object) {
        Class<?> clazz = object.getClass();
        Class<?> zuper = clazz.getSuperclass();
        return (zuper == Enum.class) ? clazz : zuper;
    }

    public static Optional<Enum.EnumDesc> describeConstable(Object object) {
        return getDeclaringClass(object)
                .describeConstable()
                .map(c -> Enum.EnumDesc.of(c, name(object)));
    }

    public static Object valueOf(Class enumClass, String name) {
        Registry registry = getRegistry(enumClass);
        if (registry != null) {
            return registry.get(NamespacedKey.fromString(name.toLowerCase()));
        }

        return Enum.valueOf(enumClass, name);
    }

    public static String toString(Object object) {
        return object.toString();
    }

    public static int ordinal(Object object) {
        if (object instanceof OldEnum<?>) {
            return ((OldEnum<?>) object).ordinal();
        }

        return ((Enum<?>) object).ordinal();
    }

    public static Material getSpawnEgg(ItemFactory itemFactory, EntityType<?> type) {
        return CraftMagicNumbers.INSTANCE.toMaterial(itemFactory.getSpawnEgg(type));
    }

    public static boolean isEnabledByFeature(DataPackManager dataPackManager, Material material, World world) {
        Preconditions.checkNotNull(material, "material cannot be null");
        return material.isEnabledByFeature(world);
    }
}

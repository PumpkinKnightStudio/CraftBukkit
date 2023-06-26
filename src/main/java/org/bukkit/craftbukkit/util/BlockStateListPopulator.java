package org.bukkit.craftbukkit.util;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.particles.ParticleParam;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ITileEntity;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.chunk.IChunkProvider;
import net.minecraft.world.level.dimension.DimensionManager;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.ticks.LevelTickAccess;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.block.CraftBlockEntityState;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public class BlockStateListPopulator implements GeneratorAccessSeed {
    private final GeneratorAccess world;
    private final Map<BlockPosition, IBlockData> dataMap = new HashMap<>();
    private final Map<BlockPosition, TileEntity> entityMap = new HashMap<>();
    private final LinkedHashMap<BlockPosition, CraftBlockState> list;
    private WorldGenFeatureConfigured<?, ?> capturedFeature;

    public BlockStateListPopulator(GeneratorAccess world) {
        this(world, new LinkedHashMap<>());
    }

    private BlockStateListPopulator(GeneratorAccess world, LinkedHashMap<BlockPosition, CraftBlockState> list) {
        this.world = world;
        this.list = list;
    }

    @Override
    public IBlockData getBlockState(BlockPosition bp) {
        IBlockData blockData = dataMap.get(bp);
        return (blockData != null) ? blockData : world.getBlockState(bp);
    }

    @Override
    public Fluid getFluidState(BlockPosition bp) {
        IBlockData blockData = dataMap.get(bp);
        return (blockData != null) ? blockData.getFluidState() : world.getFluidState(bp);
    }

    @Override
    public TileEntity getBlockEntity(BlockPosition blockposition) {
        // The contains is important to check for null values
        if (entityMap.containsKey(blockposition)) {
            return entityMap.get(blockposition);
        }

        return world.getBlockEntity(blockposition);
    }

    @Override
    public <T extends TileEntity> Optional<T> getBlockEntity(BlockPosition var0, TileEntityTypes<T> var1) {
        return GeneratorAccessSeed.super.getBlockEntity(var0, var1);
    }

    @Override
    public boolean setBlock(BlockPosition position, IBlockData data, int flag, int maxUpdates) {
        position = position.immutable();
        // remove first to keep insertion order
        list.remove(position);

        dataMap.put(position, data);
        if (data.hasBlockEntity()) {
            entityMap.put(position, ((ITileEntity) data.getBlock()).newBlockEntity(position, data));
        } else {
            entityMap.put(position, null);
        }

        // use 'this' to ensure that the block state is the correct TileState
        CraftBlockState state = (CraftBlockState) CraftBlock.at(this, position).getState();
        state.setFlag(flag);
        // set world handle to ensure that updated calls are done to the world and not to this populator
        state.setWorldHandle(world);
        list.put(position, state);
        return true;
    }

    @Override
    public boolean removeBlock(BlockPosition blockposition, boolean flag) {
        return this.world.removeBlock(blockposition, flag);
    }

    @Override
    public boolean destroyBlock(BlockPosition blockposition, boolean flag, @Nullable Entity entity, int i) {
        return this.world.destroyBlock(blockposition, flag, entity, i);
    }

    @Override
    public long nextSubTickCount() {
        return this.world.nextSubTickCount();
    }

    @Override
    public LevelTickAccess<Block> getBlockTicks() {
        return this.world.getBlockTicks();
    }

    @Override
    public LevelTickAccess<FluidType> getFluidTicks() {
        return this.world.getFluidTicks();
    }

    @Override
    public WorldData getLevelData() {
        return this.world.getLevelData();
    }

    @Override
    public DifficultyDamageScaler getCurrentDifficultyAt(BlockPosition blockposition) {
        return this.world.getCurrentDifficultyAt(blockposition);
    }

    @Nullable
    @Override
    public MinecraftServer getServer() {
        return this.world.getServer();
    }

    @Override
    public IChunkProvider getChunkSource() {
        return this.world.getChunkSource();
    }

    @Override
    public RandomSource getRandom() {
        return this.world.getRandom();
    }

    @Override
    public void playSound(@Nullable EntityHuman entityhuman, BlockPosition blockposition, SoundEffect soundeffect, SoundCategory soundcategory, float f, float f1) {
        this.world.playSound(entityhuman, blockposition, soundeffect, soundcategory, f, f1);
    }

    @Override
    public void addParticle(ParticleParam particleparam, double d0, double d1, double d2, double d3, double d4, double d5) {
        this.world.addParticle(particleparam, d0, d1, d2, d3, d4, d5);
    }

    @Override
    public void levelEvent(@Nullable EntityHuman entityhuman, int i, BlockPosition blockposition, int j) {
        this.world.levelEvent(entityhuman, i, blockposition, j);
    }

    @Override
    public void gameEvent(GameEvent gameevent, Vec3D vec3d, GameEvent.a gameevent_a) {
        this.world.gameEvent(gameevent, vec3d, gameevent_a);
    }

    @Override
    public WorldServer getLevel() {
        return this.world.getMinecraftWorld();
    }

    @Override
    public WorldServer getMinecraftWorld() {
        return world.getMinecraftWorld();
    }

    public void refreshTiles() {
        for (CraftBlockState state : list.values()) {
            if (state instanceof CraftBlockEntityState) {
                ((CraftBlockEntityState<?>) state).refreshSnapshot();
            }
        }
    }

    public void updateList() {
        for (BlockState state : list.values()) {
            state.update(true);
        }
    }

    public Set<BlockPosition> getBlocks() {
        return list.keySet();
    }

    public List<CraftBlockState> getList() {
        return new ArrayList<>(list.values());
    }

    public GeneratorAccess getWorld() {
        return world;
    }

    // For tree generation
    @Override
    public int getMinBuildHeight() {
        return getWorld().getMinBuildHeight();
    }

    @Override
    public int getHeight() {
        return getWorld().getHeight();
    }

    @Override
    public boolean isStateAtPosition(BlockPosition blockposition, Predicate<IBlockData> predicate) {
        return predicate.test(getBlockState(blockposition));
    }

    @Override
    public boolean isFluidAtPosition(BlockPosition bp, Predicate<Fluid> prdct) {
        return world.isFluidAtPosition(bp, prdct);
    }

    @Nullable
    @Override
    public IChunkAccess getChunk(int i, int i1, ChunkStatus chunkStatus, boolean b) {
        return this.world.getChunk(i, i1, chunkStatus, b);
    }

    @Override
    public int getHeight(HeightMap.Type type, int i, int i1) {
        return this.world.getHeight(type, i, i1);
    }

    @Override
    public int getSkyDarken() {
        return this.world.getSkyDarken();
    }

    @Override
    public BiomeManager getBiomeManager() {
        return this.world.getBiomeManager();
    }

    @Override
    public Holder<BiomeBase> getUncachedNoiseBiome(int i, int i1, int i2) {
        return this.world.getUncachedNoiseBiome(i, i1, i2);
    }

    @Override
    public boolean isClientSide() {
        return false;
    }

    @Override
    public int getSeaLevel() {
        return this.world.getSeaLevel();
    }

    @Override
    public DimensionManager dimensionType() {
        return world.dimensionType();
    }

    @Override
    public IRegistryCustom registryAccess() {
        return world.registryAccess();
    }

    @Override
    public FeatureFlagSet enabledFeatures() {
        return this.world.enabledFeatures();
    }

    @Override
    public long getSeed() {
        return 0;
    }

    @Override
    public float getShade(EnumDirection enumDirection, boolean b) {
        return this.world.getShade(enumDirection, b);
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return this.world.getLightEngine();
    }

    @Override
    public WorldBorder getWorldBorder() {
        return this.world.getWorldBorder();
    }

    @Override
    public List<Entity> getEntities(@Nullable Entity entity, AxisAlignedBB axisAlignedBB, Predicate<? super Entity> predicate) {
        return this.world.getEntities(entity, axisAlignedBB, predicate);
    }

    @Override
    public <T extends Entity> List<T> getEntities(EntityTypeTest<Entity, T> entityTypeTest, AxisAlignedBB axisAlignedBB, Predicate<? super T> predicate) {
        return this.world.getEntities(entityTypeTest, axisAlignedBB, predicate);
    }

    @Override
    public List<? extends EntityHuman> players() {
        return this.world.players();
    }

    @Override
    public void logFeaturePlacement(WorldGenFeatureConfigured<?, ?> featureConfigured) {
        this.capturedFeature = featureConfigured;
    }

    public WorldGenFeatureConfigured<?, ?> getCapturedFeature() {
        return capturedFeature;
    }
}

package org.bukkit.craftbukkit;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.shorts.ShortArraySet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.SectionPosition;
import net.minecraft.network.protocol.game.PacketPlayOutMultiBlockChange;
import net.minecraft.world.level.block.state.IBlockData;
import org.bukkit.BlockChangeDelegate;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.jetbrains.annotations.NotNull;

public class CraftBlockChangeDelegate implements BlockChangeDelegate {

    private Long2ObjectMap<BlockData> changes = null;

    private final int height;
    private final boolean suppressLightUpdates;

    public CraftBlockChangeDelegate(World world, boolean suppressLightUpdates) {
        this.height = world.getMaxHeight();
        this.suppressLightUpdates = suppressLightUpdates;
    }

    @Override
    public boolean setBlockData(int x, int y, int z, @NotNull BlockData blockData) {
        Preconditions.checkArgument(blockData != null, "blockData must not be null");

        if (changes == null) {
            this.changes = new Long2ObjectOpenHashMap<>();
        }

        this.changes.put(BlockPosition.asLong(x, y, z), blockData);
        return true;
    }

    @NotNull
    @Override
    public BlockData getBlockData(int x, int y, int z) {
        if (changes == null) {
            return Material.AIR.createBlockData();
        }

        BlockData blockData = changes.get(BlockPosition.asLong(x, y, z));
        return (blockData != null) ? blockData : Material.AIR.createBlockData();
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public boolean isEmpty(int x, int y, int z) {
        if (changes == null) {
            return true;
        }

        BlockData blockData = changes.get(BlockPosition.asLong(x, y, z));
        return blockData == null || blockData.getMaterial() == Material.AIR;
    }

    public List<PacketPlayOutMultiBlockChange> createUpdatePackets() {
        if (changes == null || changes.isEmpty()) {
            return Collections.emptyList();
        }

        Map<SectionPosition, ChunkSectionChanges> finalChanges = new HashMap<>();

        for (Long2ObjectMap.Entry<BlockData> change : changes.long2ObjectEntrySet()) {
            long packedWorldCoordinates = change.getLongKey();
            BlockData blockData = change.getValue();

            // The coordinates of the block in the world
            int worldX = BlockPosition.getX(packedWorldCoordinates);
            int worldY = BlockPosition.getY(packedWorldCoordinates);
            int worldZ = BlockPosition.getZ(packedWorldCoordinates);

            // The coordinates of the chunk section in which the block is located, aka chunk x, y, and z
            long packedSectionCoordinates = SectionPosition.blockToSection(packedWorldCoordinates);
            SectionPosition sectionPosition = SectionPosition.of(packedSectionCoordinates);

            // The coordinates of the block relative to the chunk section (i.e. x, y, and z in the range of 0 - 15)
            int blockLocalX = toChunkRelativeCoordinate(worldX);
            int blockLocalY = toChunkRelativeCoordinate(worldY);
            int blockLocalZ = toChunkRelativeCoordinate(worldZ);

            // Push the block change position and block data to the final change map
            ChunkSectionChanges sectionChanges = finalChanges.computeIfAbsent(sectionPosition, ignore -> new ChunkSectionChanges());
            sectionChanges.positions().add(packBlockLocalCoordinates(blockLocalX, blockLocalY, blockLocalZ));
            sectionChanges.blockData().add(((CraftBlockData) blockData).getState());
        }

        // Construct the packets using the data allocated above
        List<PacketPlayOutMultiBlockChange> packets = new ArrayList<>();

        for (Map.Entry<SectionPosition, ChunkSectionChanges> entry : finalChanges.entrySet()) {
            ChunkSectionChanges chunkChanges = entry.getValue();
            packets.add(new PacketPlayOutMultiBlockChange(entry.getKey(), chunkChanges.positions(), chunkChanges.blockData().toArray(IBlockData[]::new), suppressLightUpdates));
        }

        return packets;
    }

    private int toChunkRelativeCoordinate(int coordinate) {
        int localCoordinate = coordinate % 16;

        if (localCoordinate < 0) {
            localCoordinate += 16;
        }

        return localCoordinate;
    }

    // Copied from SectionPosition. Packs local (0 - 15) coordinates into a single short
    private short packBlockLocalCoordinates(int localX, int localY, int localZ) {
        return (short) (localX << 8 | localZ << 4 | localY);
    }

    private record ChunkSectionChanges(ShortSet positions, List<IBlockData> blockData) {

        public ChunkSectionChanges() {
            this(new ShortArraySet(), new ArrayList<>());
        }

    }

}

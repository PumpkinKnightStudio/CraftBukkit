package org.bukkit.craftbukkit.util;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.Chunk;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.craftbukkit.block.CraftBlockStates;

import java.util.Map;

public class BlockCapturer {

    private BlockCapture currentBlockCapture = BlackholeBlockCapture.INSTANCE;
    private final World world;

    public BlockCapturer(World world) {
        this.world = world;
    }

    public SimpleBlockCapture capture() {
        if (this.isCapturing()) {
            throw new IllegalStateException("Cannot start capture whilst already capturing blocks.");
        }

        SimpleBlockCapture capture = new SimpleBlockCapture();
        this.currentBlockCapture = capture;
        return capture;
    }

    public boolean capture(BlockPosition blockposition, IBlockData iblockdata, int i, int j) {
        return this.currentBlockCapture.capture(blockposition, iblockdata, i, j);
    }

    public void revertCapture(BlockPosition blockposition) {
        this.currentBlockCapture.revertCapture(blockposition);
    }

    public boolean isCapturing() {
        return this.currentBlockCapture.isCapturing();
    }

    public interface BlockCapture extends AutoCloseable {

        public boolean capture(BlockPosition blockposition, IBlockData iblockdata, int i, int j);

        boolean isCapturing();

        void revertCapture(BlockPosition blockposition);

        @Override
        void close();
    }

    public class SimpleBlockCapture implements BlockCapture {

        private Map<BlockPosition, BlockState> capturedBlockStates = new java.util.LinkedHashMap<>();

        @Override
        public void close() {
            BlockCapturer.this.currentBlockCapture = BlackholeBlockCapture.INSTANCE;
        }

        @Override
        public boolean capture(BlockPosition blockposition, IBlockData iblockdata, int i, int j) {
            if (!this.capturedBlockStates.containsKey(blockposition)) {
                Chunk chunk = BlockCapturer.this.world.getChunkAt(blockposition);
                BlockState blockState = CraftBlockStates.getBlockState(BlockCapturer.this.world.getWorld(), blockposition.immutable(), chunk.getBlockState(blockposition), chunk.getBlockEntity(blockposition));
                this.capturedBlockStates.put(blockposition.immutable(), blockState);
                return true;
            }
            return false;
        }

        @Override
        public boolean isCapturing() {
            return true;
        }

        @Override
        public void revertCapture(BlockPosition blockposition) {
            this.capturedBlockStates.remove(blockposition);
        }

        public Map<BlockPosition, BlockState> getCapturedBlockStates() {
            return capturedBlockStates;
        }

        public void rewind() {
            // revert back all captured blocks
            world.preventPoiUpdated = true; // CraftBukkit - SPIGOT-5710
            for (BlockState blockstate : this.capturedBlockStates.values()) {
                blockstate.update(true, false);
            }
            world.preventPoiUpdated = false;
        }

        public void finalizePlacement() {
            // Post processing: onPlace (handled in chunk placement logic)
            for (Map.Entry<BlockPosition, BlockState> entry : this.capturedBlockStates.entrySet()) {
                IBlockData iBlockData = ((CraftBlockState) entry.getValue()).getHandle();
                BlockPosition position = entry.getKey();

                // Using the new block state, update with the old (captured) state
                world.getBlockState(position).onPlace(world, position, iBlockData, true);
            }
        }
    }

    private static class BlackholeBlockCapture implements BlockCapture {

        private static final BlockCapture INSTANCE = new BlackholeBlockCapture();

        @Override
        public boolean capture(BlockPosition blockposition, IBlockData iblockdata, int i, int j) {
            return false;
        }

        @Override
        public boolean isCapturing() {
            return false;
        }

        @Override
        public void revertCapture(BlockPosition blockposition) {

        }

        @Override
        public void close() {
        }
    }

}

package org.bukkit.craftbukkit.block.data.type;

import org.bukkit.block.data.type.Leaves;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

public class CraftLeaves extends CraftBlockData implements Leaves {

    private static final net.minecraft.world.level.block.state.properties.BlockStateInteger DISTANCE = getInteger("distance");
    private static final net.minecraft.world.level.block.state.properties.BlockStateBoolean PERSISTENT = getBoolean("persistent");

    private static final net.minecraft.world.level.block.state.properties.BlockStateBoolean WATERLOGGED = getBoolean(net.minecraft.world.level.block.BlockLeaves.class, "waterlogged");

    @Override
    public boolean isPersistent() {
        return get(PERSISTENT);
    }

    @Override
    public void setPersistent(boolean persistent) {
        set(PERSISTENT, persistent);
    }

    @Override
    public int getDistance() {
        return get(DISTANCE);
    }

    @Override
    public void setDistance(int distance) {
        set(DISTANCE, distance);
    }

    @Override
    public boolean isWaterlogged() {
        return get(WATERLOGGED);
    }

    @Override
    public void setWaterlogged(boolean waterlogged) {
        set(WATERLOGGED, waterlogged);
    }
}

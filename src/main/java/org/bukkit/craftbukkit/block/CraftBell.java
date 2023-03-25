package org.bukkit.craftbukkit.block;

import com.google.common.base.Preconditions;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.level.block.BlockBell;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityBell;
import org.bukkit.World;
import org.bukkit.block.Bell;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;

public class CraftBell extends CraftBlockEntityState<TileEntityBell> implements Bell {

    public CraftBell(World world, TileEntityBell tileEntity) {
        super(world, tileEntity);
    }

    @Override
    public boolean ring(Entity entity, BlockFace direction) {
        Preconditions.checkArgument(direction == null || direction.isCartesian(), "direction must be cartesian, given %s", direction);

        TileEntity tileEntity = getTileEntityFromWorld();
        if (tileEntity == null) {
            return false;
        }

        net.minecraft.world.entity.Entity nmsEntity = (entity != null) ? ((CraftEntity) entity).getHandle() : null;
        EnumDirection enumDirection = (direction != null) ? EnumDirection.valueOf(direction.name()) : null;

        return ((BlockBell) Blocks.BELL).attemptToRing(nmsEntity, world.getHandle(), getPosition(), enumDirection);
    }

    @Override
    public boolean ring(Entity entity) {
        return ring(entity, null);
    }

    @Override
    public boolean ring(BlockFace direction) {
        return ring(null, direction);
    }

    @Override
    public boolean ring() {
        return ring(null, null);
    }

    @Override
    public boolean isShaking() {
        requirePlaced();

        TileEntity tileEntity = getTileEntityFromWorld();
        return tileEntity instanceof TileEntityBell bell && bell.shaking;
    }

    @Override
    public int getShakingTicks() {
        requirePlaced();

        TileEntity tileEntity = getTileEntityFromWorld();
        return tileEntity instanceof TileEntityBell bell ? bell.ticks : 0;
    }

    @Override
    public boolean isResonating() {
        requirePlaced();

        TileEntity tileEntity = getTileEntityFromWorld();
        return tileEntity instanceof TileEntityBell bell && bell.resonating;
    }

    @Override
    public int getResonatingTicks() {
        requirePlaced();

        TileEntity tileEntity = getTileEntityFromWorld();
        return tileEntity instanceof TileEntityBell bell ? bell.resonationTicks : 0;
    }
}

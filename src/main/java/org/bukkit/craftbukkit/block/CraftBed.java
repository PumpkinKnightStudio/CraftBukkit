package org.bukkit.craftbukkit.block;

import net.minecraft.world.level.block.entity.TileEntityBed;
import org.bukkit.DyeColor;
import org.bukkit.World;
import org.bukkit.block.Bed;
import org.bukkit.block.BlockType;

public class CraftBed extends CraftBlockEntityState<TileEntityBed> implements Bed {

    public CraftBed(World world, TileEntityBed tileEntity) {
        super(world, tileEntity);
    }

    protected CraftBed(CraftBed state) {
        super(state);
    }

    @Override
    public DyeColor getColor() {
        BlockType<?> type = getType();
        if (type == BlockType.BLACK_BED) {
            return DyeColor.BLACK;
        }
        if (type == BlockType.BLUE_BED) {
            return DyeColor.BLUE;
        }
        if (type == BlockType.BROWN_BED) {
            return DyeColor.BROWN;
        }
        if (type == BlockType.CYAN_BED) {
            return DyeColor.CYAN;
        }
        if (type == BlockType.GRAY_BED) {
            return DyeColor.GRAY;
        }
        if (type == BlockType.GREEN_BED) {
            return DyeColor.GREEN;
        }
        if (type == BlockType.LIGHT_BLUE_BED) {
            return DyeColor.LIGHT_BLUE;
        }
        if (type == BlockType.LIGHT_GRAY_BED) {
            return DyeColor.LIGHT_GRAY;
        }
        if (type == BlockType.LIME_BED) {
            return DyeColor.LIME;
        }
        if (type == BlockType.MAGENTA_BED) {
            return DyeColor.MAGENTA;
        }
        if (type == BlockType.ORANGE_BED) {
            return DyeColor.ORANGE;
        }
        if (type == BlockType.PINK_BED) {
            return DyeColor.PINK;
        }
        if (type == BlockType.PURPLE_BED) {
            return DyeColor.PURPLE;
        }
        if (type == BlockType.RED_BED) {
            return DyeColor.RED;
        }
        if (type == BlockType.WHITE_BED) {
            return DyeColor.WHITE;
        }
        if (type == BlockType.YELLOW_BED) {
            return DyeColor.YELLOW;
        }

        throw new IllegalArgumentException("Unknown DyeColor for " + getType());
    }

    @Override
    public void setColor(DyeColor color) {
        throw new UnsupportedOperationException("Must set block type to appropriate bed colour");
    }

    @Override
    public CraftBed copy() {
        return new CraftBed(this);
    }
}

package org.bukkit.craftbukkit.block;

import net.minecraft.world.level.block.entity.TileEntityBed;
import org.bukkit.DyeColor;
import org.bukkit.World;
import org.bukkit.block.Bed;

public class CraftBed extends CraftBlockEntityState<TileEntityBed> implements Bed {

    public CraftBed(World world, TileEntityBed tileEntity) {
        super(world, tileEntity);
    }

    @Override
    public DyeColor getColor() {
        Material type = getType();
        if (type == Material.BLACK_BED) {
            return DyeColor.BLACK;
        }
        if (type == Material.BLUE_BED) {
            return DyeColor.BLUE;
        }
        if (type == Material.BROWN_BED) {
            return DyeColor.BROWN;
        }
        if (type == Material.CYAN_BED) {
            return DyeColor.CYAN;
        }
        if (type == Material.GRAY_BED) {
            return DyeColor.GRAY;
        }
        if (type == Material.GREEN_BED) {
            return DyeColor.GREEN;
        }
        if (type == Material.LIGHT_BLUE_BED) {
            return DyeColor.LIGHT_BLUE;
        }
        if (type == Material.LIGHT_GRAY_BED) {
            return DyeColor.LIGHT_GRAY;
        }
        if (type == Material.LIME_BED) {
            return DyeColor.LIME;
        }
        if (type == Material.MAGENTA_BED) {
            return DyeColor.MAGENTA;
        }
        if (type == Material.ORANGE_BED) {
            return DyeColor.ORANGE;
        }
        if (type == Material.PINK_BED) {
            return DyeColor.PINK;
        }
        if (type == Material.PURPLE_BED) {
            return DyeColor.PURPLE;
        }
        if (type == Material.RED_BED) {
            return DyeColor.RED;
        }
        if (type == Material.WHITE_BED) {
            return DyeColor.WHITE;
        }
        if (type == Material.YELLOW_BED) {
            return DyeColor.YELLOW;
        }

        throw new IllegalArgumentException("Unknown DyeColor for " + getType());
    }

    @Override
    public void setColor(DyeColor color) {
        throw new UnsupportedOperationException("Must set block type to appropriate bed colour");
    }
}

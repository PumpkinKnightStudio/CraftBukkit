package org.bukkit.craftbukkit.block;

import static org.junit.jupiter.api.Assertions.*;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ITileEntity;
import net.minecraft.world.level.block.entity.TileEntity;
import org.bukkit.Material;
import org.bukkit.block.BlockType;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.support.AbstractTestingBase;
import org.junit.jupiter.api.Test;

public class BlockStateTest extends AbstractTestingBase {

    @Test
    public void testTileEntityBlockStates() {
        for (Block block : BuiltInRegistries.BLOCK) {
            BlockType<?> blockType = CraftBlockType.minecraftToBukkit(block);
            Class<?> blockStateType = CraftBlockStates.getBlockStateType(blockType);
            boolean isCraftBlockEntityState = CraftBlockEntityState.class.isAssignableFrom(blockStateType);

            if (block instanceof ITileEntity) {
                assertTrue(isCraftBlockEntityState, blockType + " has BlockState of type " + blockStateType.getName() + ", but expected subtype of CraftBlockEntityState");

                // check tile entity type
                TileEntity tileEntity = ((ITileEntity) block).newBlockEntity(BlockPosition.ZERO, block.defaultBlockState());
                TileEntity materialTileEntity = CraftBlockStates.createNewTileEntity(blockType);

                if (tileEntity == null) {
                    if (CraftBlockStates.isTileEntityOptional(blockType)) {
                        continue;
                    }
                    fail(blockType + " has no tile entity, it be added to CraftBlockStates#isTileEntityOptional");
                }

                assertNotNull(materialTileEntity, blockType + " has no tile entity expected tile entity of type " + tileEntity.getClass());
                assertSame(materialTileEntity.getClass(), tileEntity.getClass(), blockType + " has unexpected tile entity type, expected " + tileEntity.getClass() + " but got " + tileEntity.getClass());
            } else {
                assertFalse(isCraftBlockEntityState, blockType + " has unexpected CraftBlockEntityState subytype " + blockStateType.getName() + " (but is not a tile)");
            }
        }
    }
}

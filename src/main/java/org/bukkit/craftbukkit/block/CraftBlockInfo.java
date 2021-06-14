package org.bukkit.craftbukkit.block;

import com.google.common.base.Preconditions;
import net.minecraft.world.level.block.state.BlockBase;
import org.bukkit.Material;
import org.bukkit.SoundGroup;
import org.bukkit.block.BlockInfo;
import org.bukkit.craftbukkit.CraftSoundGroup;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;

import java.util.HashMap;

public class CraftBlockInfo implements BlockInfo {
    private static final HashMap<BlockBase, BlockInfo> blockInfos = new HashMap<>();
    private final boolean hasCollision;
    private final SoundGroup soundGroup;
    private final float explosionResistance;
    private final float destroyTime;
    private final float friction;
    private final float speedFactor;
    private final float jumpFactor;
    private final boolean isRandomlyTicking;

    public static BlockInfo getBlockInfo(BlockBase blockBase) {
        if (!blockInfos.containsKey(blockBase)) {
            blockInfos.put(blockBase, new CraftBlockInfo(blockBase));
        }

        return blockInfos.get(blockBase);
    }

    private CraftBlockInfo(BlockBase nmsInfo) {
        this.hasCollision = nmsInfo.hasCollision;
        this.soundGroup = CraftSoundGroup.getSoundGroup(nmsInfo.soundType);
        this.explosionResistance = nmsInfo.explosionResistance;
        this.destroyTime = nmsInfo.t(); // PAIL rename getDestroyTime
        this.isRandomlyTicking = nmsInfo.isRandomlyTicking;
        this.friction = nmsInfo.friction;
        this.speedFactor = nmsInfo.speedFactor;
        this.jumpFactor = nmsInfo.jumpFactor;
    }

    public boolean isHasCollision() {
        return hasCollision;
    }

    public SoundGroup getSoundGroup() {
        return soundGroup;
    }

    public float getExplosionResistance() {
        return explosionResistance;
    }

    public float getDestroyTime() {
        return destroyTime;
    }

    public float getFriction() {
        return friction;
    }

    public float getSpeedFactor() {
        return speedFactor;
    }

    public float getJumpFactor() {
        return jumpFactor;
    }

    public boolean isRandomlyTicking() {
        return isRandomlyTicking;
    }
}

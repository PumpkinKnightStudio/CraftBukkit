package org.bukkit.craftbukkit.block;

import net.minecraft.world.level.block.state.BlockBase;
import org.bukkit.SoundGroup;
import org.bukkit.block.BlockInfo;
import org.bukkit.craftbukkit.CraftSoundGroup;

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

    @Override
    public boolean isHasCollision() {
        return hasCollision;
    }

    @Override
    public SoundGroup getSoundGroup() {
        return soundGroup;
    }

    @Override
    public float getExplosionResistance() {
        return explosionResistance;
    }

    @Override
    public float getDestroyTime() {
        return destroyTime;
    }

    @Override
    public float getFriction() {
        return friction;
    }

    @Override
    public float getSpeedFactor() {
        return speedFactor;
    }

    @Override
    public float getJumpFactor() {
        return jumpFactor;
    }

    @Override
    public boolean isRandomlyTicking() {
        return isRandomlyTicking;
    }
}

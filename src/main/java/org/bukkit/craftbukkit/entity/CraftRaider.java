package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import net.minecraft.world.entity.raid.EntityRaider;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftSound;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.entity.Raider;

public abstract class CraftRaider extends CraftMonster implements Raider {

    public CraftRaider(CraftServer server, EntityRaider entity) {
        super(server, entity);
    }

    @Override
    public EntityRaider getHandle() {
        return (EntityRaider) super.getHandle();
    }

    @Override
    public String toString() {
        return "CraftRaider";
    }

    @Override
    public Block getPatrolTarget() {
        return getHandle().getPatrolTarget() == null ? null : CraftBlock.at(getHandle().level(), getHandle().getPatrolTarget());
    }

    @Override
    public void setPatrolTarget(Block block) {
        if (block == null) {
            getHandle().setPatrolTarget(null);
        } else {
            Preconditions.checkArgument(block.getWorld().equals(this.getWorld()), "Block must be in same world");
            getHandle().setPatrolTarget(((CraftBlock) block).getPosition());
        }
    }

    @Override
    public boolean isPatrolLeader() {
        return getHandle().isPatrolLeader();
    }

    @Override
    public void setPatrolLeader(boolean leader) {
        getHandle().setPatrolLeader(leader);
    }

    @Override
    public boolean isCanJoinRaid() {
        return getHandle().canJoinRaid();
    }

    @Override
    public void setCanJoinRaid(boolean join) {
        getHandle().setCanJoinRaid(join);
    }

    @Override
    public Sound getCelebrationSound() {
        return CraftSound.minecraftToBukkit(getHandle().getCelebrateSound());
    }
}

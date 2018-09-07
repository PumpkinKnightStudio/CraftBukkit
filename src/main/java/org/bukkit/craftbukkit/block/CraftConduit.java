package org.bukkit.craftbukkit.block;

import com.google.common.base.Preconditions;
import net.minecraft.server.BlockPosition;
import net.minecraft.server.IEntitySelector;
import net.minecraft.server.TileEntityConduit;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Conduit;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.block.ConduitStatusEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CraftConduit extends CraftBlockEntityState<TileEntityConduit> implements Conduit {

    public CraftConduit(Block block) {
        super(block, TileEntityConduit.class);
    }

    public CraftConduit(Material material, TileEntityConduit te) {
        super(material, te);
    }


    @Override
    public void applyTo(TileEntityConduit conduit) {
        super.applyTo(conduit);
        conduit.isLocked = this.getSnapshot().isLocked;
        conduit.j = this.getSnapshot().j;
        if (conduit.j == null) {
            conduit.k = null;
        } else {
            conduit.k = this.getSnapshot().j.getUniqueID();
        }

        conduit.searchRadius = this.getSnapshot().searchRadius;
        conduit.attackDamage = this.getSnapshot().attackDamage;
        conduit.effectRange = this.getSnapshot().effectRange;
        conduit.effectPredicate = this.getSnapshot().effectPredicate;
        conduit.targetPredicate = this.getSnapshot().targetPredicate;

        if (conduit.c() != this.getSnapshot().c()) {
            conduit.a(this.getSnapshot().c(), ConduitStatusEvent.Reason.PLUGIN);
        }
    }

    @Override
    public void setCurrentTarget(@Nullable LivingEntity entity) {
        if (entity == null || entity.isDead()) {
            this.getSnapshot().j = null; // PAIL rename currentTarget
            this.getSnapshot().k = null; // PAIL rename targetUUID
        } else {
            this.getSnapshot().j = ((CraftLivingEntity)entity).getHandle();
            this.getSnapshot().k = entity.getUniqueId();
        }
    }

    @Override
    @Nullable
    public LivingEntity getCurrentTarget() {
        return this.getTileEntity().j == null ? null : (LivingEntity) this.getTileEntity().j.getBukkitEntity();
    }

    @Override
    public void setActive(boolean active) {
        this.getSnapshot().g = active; // PAIL rename isActive // Don't use a(..) so we don't fire the event twice
    }

    @Override
    public boolean isActive() {
        return this.getTileEntity().c(); // PAIL rename isActive
    }

    @Override
    public void setLocked(boolean locked) {
        this.getSnapshot().isLocked = locked;
    }

    @Override
    public boolean isLocked() {
        return this.getTileEntity().isLocked;
    }

    @Override
    public float getTicksSinceActivated() {
        return this.getTileEntity().ticksSinceActivated; // PAIL rename ticksSinceActivated
    }

    @Override
    public void setSearchRadius(double radius) {
        Preconditions.checkArgument(radius >= 0.0D && Double.isFinite(radius), "Search radius must be between 0.0 and %s. Received %s", Double.MAX_VALUE, radius);
        this.getSnapshot().searchRadius = radius;
    }

    @Override
    public double getSearchRadius() {
        return this.getTileEntity().searchRadius;
    }

    @Override
    public void setAttackDamage(float damage) {
        Preconditions.checkArgument(damage >= 0.0F && Float.isFinite(damage), "Attack damage must be between 0.0 and %s. Received %s", Float.MAX_VALUE, damage);
        this.getSnapshot().attackDamage = damage;
    }

    @Override
    public float getAttackDamage() {
        return this.getTileEntity().attackDamage;
    }

    @Override
    public void setEffectRange(int range) {
        Preconditions.checkArgument(range >= 0, "Effect range cannot be negative.");
        this.getSnapshot().effectRange = range;
    }

    @Override
    public int getEffectRange() {
        return this.getTileEntity().effectRange;
    }

    @Override
    public void setTargetPredicate(@Nullable Predicate<LivingEntity> predicate) {
        if (predicate == null) {
            this.getSnapshot().targetPredicate = TileEntityConduit.defaultTargetPredicate;
            return;
        }
        this.getSnapshot().targetPredicate = (entityLiving -> predicate.test((LivingEntity) entityLiving.getBukkitEntity()));
    }

    @Override
    public Predicate<LivingEntity> getTargetPredicate() {
        return livingEntity -> getTileEntity().targetPredicate.test(((CraftLivingEntity)livingEntity).getHandle());
    }

    @Override
    public void setEffectPredicate(@Nullable Predicate<Entity> predicate) {
        if (predicate == null) {
            this.getSnapshot().effectPredicate = IEntitySelector.f;
            return;
        }
        this.getSnapshot().effectPredicate = entity -> predicate.test(entity.getBukkitEntity());
    }

    @Override
    public Predicate<Entity> getEffectPredicate() {
        return entity -> getTileEntity().effectPredicate.test(((CraftEntity)entity).getHandle());
    }

    @Override
    public List<Block> getSurroundingBlocks() {
        List<BlockPosition> nms = this.getTileEntity().i;
        List<Block> bukkit = new ArrayList<>();
        if (nms == null || nms.isEmpty()) {
            return bukkit;
        }
        for (BlockPosition pos : nms) {
            if (pos == null) continue;
            bukkit.add(CraftBlock.at(((CraftWorld)getWorld()).getHandle(), pos));
        }
        return bukkit;
    }
}

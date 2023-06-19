package org.bukkit.craftbukkit.block;

import com.google.common.base.Preconditions;
import com.mojang.authlib.GameProfile;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.entity.TileEntitySkull;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockType;
import org.bukkit.block.Skull;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.profile.CraftPlayerProfile;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.profile.PlayerProfile;
import org.jetbrains.annotations.Nullable;

public class CraftSkull extends CraftBlockEntityState<TileEntitySkull> implements Skull {

    private static final int MAX_OWNER_LENGTH = 16;
    private GameProfile profile;

    public CraftSkull(World world, TileEntitySkull tileEntity) {
        super(world, tileEntity);
    }

    @Override
    public void load(TileEntitySkull skull) {
        super.load(skull);

        profile = skull.owner;
    }

    static int getSkullType(SkullType type) {
        switch (type) {
            default:
            case SKELETON:
                return 0;
            case WITHER:
                return 1;
            case ZOMBIE:
                return 2;
            case PLAYER:
                return 3;
            case CREEPER:
                return 4;
            case DRAGON:
                return 5;
        }
    }

    @Override
    public boolean hasOwner() {
        return profile != null;
    }

    @Override
    public String getOwner() {
        return hasOwner() ? profile.getName() : null;
    }

    @Override
    public boolean setOwner(String name) {
        if (name == null || name.length() > MAX_OWNER_LENGTH) {
            return false;
        }

        GameProfile profile = MinecraftServer.getServer().getProfileCache().get(name).orElse(null);
        if (profile == null) {
            return false;
        }

        this.profile = profile;
        return true;
    }

    @Override
    public OfflinePlayer getOwningPlayer() {
        if (profile != null) {
            if (profile.getId() != null) {
                return Bukkit.getOfflinePlayer(profile.getId());
            }

            if (profile.getName() != null) {
                return Bukkit.getOfflinePlayer(profile.getName());
            }
        }

        return null;
    }

    @Override
    public void setOwningPlayer(OfflinePlayer player) {
        Preconditions.checkNotNull(player, "player");

        if (player instanceof CraftPlayer) {
            this.profile = ((CraftPlayer) player).getProfile();
        } else {
            this.profile = new GameProfile(player.getUniqueId(), player.getName());
        }
    }

    @Override
    public PlayerProfile getOwnerProfile() {
        if (!hasOwner()) {
            return null;
        }

        return new CraftPlayerProfile(profile);
    }

    @Override
    public void setOwnerProfile(PlayerProfile profile) {
        if (profile == null) {
            this.profile = null;
        } else {
            this.profile = CraftPlayerProfile.validateSkullProfile(((CraftPlayerProfile) profile).buildGameProfile());
        }
    }

    @Override
    public NamespacedKey getNoteBlockSound() {
        MinecraftKey key = getSnapshot().getNoteBlockSound();
        return (key != null) ? CraftNamespacedKey.fromMinecraft(key) : null;
    }

    @Override
    public void setNoteBlockSound(@Nullable NamespacedKey namespacedKey) {
        if (namespacedKey == null) {
            this.getSnapshot().noteBlockSound = null;
            return;
        }
        this.getSnapshot().noteBlockSound = CraftNamespacedKey.toMinecraft(namespacedKey);
    }

    @Override
    public BlockFace getRotation() {
        BlockData blockData = getBlockData();
        return (blockData instanceof Rotatable) ? ((Rotatable) blockData).getRotation() : ((Directional) blockData).getFacing();
    }

    @Override
    public void setRotation(BlockFace rotation) {
        BlockData blockData = getBlockData();
        if (blockData instanceof Rotatable) {
            ((Rotatable) blockData).setRotation(rotation);
        } else {
            ((Directional) blockData).setFacing(rotation);
        }
        setBlockData(blockData);
    }

    @Override
    public SkullType getSkullType() {
        BlockType<?> type = getType();
        if (type == BlockType.SKELETON_SKULL || type == BlockType.SKELETON_WALL_SKULL) {
            return SkullType.SKELETON;
        }
        if (type == BlockType.WITHER_SKELETON_SKULL || type == BlockType.WITHER_SKELETON_WALL_SKULL) {
            return SkullType.WITHER;
        }
        if (type == BlockType.ZOMBIE_HEAD || type == BlockType.ZOMBIE_WALL_HEAD) {
            return SkullType.ZOMBIE;
        }
        if (type == BlockType.PIGLIN_HEAD || type == BlockType.PIGLIN_WALL_HEAD) {
            return SkullType.PIGLIN;
        }
        if (type == BlockType.PLAYER_HEAD || type == BlockType.PLAYER_WALL_HEAD) {
            return SkullType.PLAYER;
        }
        if (type == BlockType.CREEPER_HEAD || type == BlockType.CREEPER_WALL_HEAD) {
            return SkullType.CREEPER;
        }
        if (type == BlockType.DRAGON_HEAD || type == BlockType.DRAGON_WALL_HEAD) {
            return SkullType.DRAGON;
        }

        throw new IllegalArgumentException("Unknown SkullType for " + getType());
    }

    @Override
    public void setSkullType(SkullType skullType) {
        throw new UnsupportedOperationException("Must change block type");
    }

    @Override
    public void applyTo(TileEntitySkull skull) {
        super.applyTo(skull);

        if (getSkullType() == SkullType.PLAYER) {
            skull.setOwner(profile);
        }
    }
}

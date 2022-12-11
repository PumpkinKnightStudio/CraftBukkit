package org.bukkit.craftbukkit.block;

import com.google.common.base.Preconditions;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.entity.TileEntitySkull;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.profile.CraftPlayerProfile;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.profile.PlayerProfile;

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
        return switch (type) {
            default -> 0;
            case SKELETON -> 0;
            case WITHER -> 1;
            case ZOMBIE -> 2;
            case PLAYER -> 3;
            case CREEPER -> 4;
            case DRAGON -> 5;
        };
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

        if (player instanceof CraftPlayer craftPlayer) {
            this.profile = craftPlayer.getProfile();
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
        return (this.getSnapshot().getNoteBlockSound() == null) ? null : CraftNamespacedKey.fromMinecraft(this.getSnapshot().getNoteBlockSound());
    }

    @Override
    public BlockFace getRotation() {
        BlockData blockData = getBlockData();
        return (blockData instanceof Rotatable rotatable) ? rotatable.getRotation() : ((Directional) blockData).getFacing();
    }

    @Override
    public void setRotation(BlockFace rotation) {
        BlockData blockData = getBlockData();
        if (blockData instanceof Rotatable rotatable) {
            rotatable.setRotation(rotation);
        } else {
            ((Directional) blockData).setFacing(rotation);
        }
        setBlockData(blockData);
    }

    @Override
    public SkullType getSkullType() {
        return switch (getType()) {
            case SKELETON_SKULL, SKELETON_WALL_SKULL -> SkullType.SKELETON;
            case WITHER_SKELETON_SKULL, WITHER_SKELETON_WALL_SKULL -> SkullType.WITHER;
            case ZOMBIE_HEAD, ZOMBIE_WALL_HEAD -> SkullType.ZOMBIE;
            case PIGLIN_HEAD, PIGLIN_WALL_HEAD -> SkullType.PIGLIN;
            case PLAYER_HEAD, PLAYER_WALL_HEAD -> SkullType.PLAYER;
            case CREEPER_HEAD, CREEPER_WALL_HEAD -> SkullType.CREEPER;
            case DRAGON_HEAD, DRAGON_WALL_HEAD -> SkullType.DRAGON;
            default -> throw new IllegalArgumentException("Unknown SkullType for " + getType());
        };
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

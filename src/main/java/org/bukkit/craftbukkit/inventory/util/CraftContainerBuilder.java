package org.bukkit.craftbukkit.inventory.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import net.minecraft.core.BlockPosition;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.ITileInventory;
import net.minecraft.world.TileInventory;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ContainerAccess;
import net.minecraft.world.inventory.ContainerAnvil;
import net.minecraft.world.inventory.ContainerCartography;
import net.minecraft.world.inventory.ContainerEnchantTable;
import net.minecraft.world.inventory.ContainerGrindstone;
import net.minecraft.world.inventory.ContainerSmithing;
import net.minecraft.world.inventory.ContainerWorkbench;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityBeacon;
import net.minecraft.world.level.block.entity.TileEntityBlastFurnace;
import net.minecraft.world.level.block.entity.TileEntityBrewingStand;
import net.minecraft.world.level.block.entity.TileEntityContainer;
import net.minecraft.world.level.block.entity.TileEntityFurnaceFurnace;
import net.minecraft.world.level.block.entity.TileEntitySmoker;
import net.minecraft.world.level.block.state.IBlockData;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.inventory.CraftMenuType;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.inventory.MenuType;

public final class CraftContainerBuilder {

    public static final CraftContainerBuilder INSTANCE = new CraftContainerBuilder();

    private final Map<MenuType<?>, ContainerBuilder> builders;

    public CraftContainerBuilder() {
        this.builders = new HashMap<>();
        this.builders.put(MenuType.CRAFTER_3x3, ContainerBuilder.tileEntity(CrafterBlockEntity::new, Blocks.CRAFTER));
        this.builders.put(MenuType.ANVIL, ContainerBuilder.locationBound(ContainerAnvil::new));
        this.builders.put(MenuType.BEACON, ContainerBuilder.tileEntity(TileEntityBeacon::new, Blocks.BEACON));
        this.builders.put(MenuType.BLAST_FURNACE, ContainerBuilder.tileEntity(TileEntityBlastFurnace::new, Blocks.BLAST_FURNACE));
        this.builders.put(MenuType.BREWING_STAND, ContainerBuilder.tileEntity(TileEntityBrewingStand::new, Blocks.BREWING_STAND));
        this.builders.put(MenuType.CARTOGRAPHY_TABLE, ContainerBuilder.locationBound(ContainerCartography::new));
        this.builders.put(MenuType.CRAFTING, ContainerBuilder.locationBound(ContainerWorkbench::new));
        this.builders.put(MenuType.ENCHANTMENT, (i, playerinventory, position) -> {
            return new TileInventory((syncId, inventory, human) -> {
                return ContainerBuilder.locationBound(ContainerEnchantTable::new).build(syncId, inventory, position);
            }, IChatBaseComponent.empty()).createMenu(i, playerinventory, playerinventory.player);
        });
        this.builders.put(MenuType.FURNACE, ContainerBuilder.tileEntity(TileEntityFurnaceFurnace::new, Blocks.FURNACE));
        this.builders.put(MenuType.GRINDSTONE, ContainerBuilder.locationBound(ContainerGrindstone::new));
        this.builders.put(MenuType.SMITHING, ContainerBuilder.locationBound(ContainerSmithing::new));
        this.builders.put(MenuType.SMOKER, ContainerBuilder.tileEntity(TileEntitySmoker::new, Blocks.SMOKER));
    }

    public Container create(final CraftMenuType<?> type, int syncId, PlayerInventory inventory) {
        final ContainerBuilder builder = builders.get(type);
        return builder == null ? type.getHandle().create(syncId, inventory) : builder.build(syncId, inventory, inventory.player.blockPosition());
    }

    public Container create(final CraftMenuType<?> type, int syncId, PlayerInventory inventory, Location location) {
        final ContainerBuilder builder = builders.get(type);
        final Container container;
        if (builder == null) {
            final CraftWorld world = (CraftWorld) location.getWorld();
            TileEntity entity = world.getHandle().getBlockEntity(CraftLocation.toBlockPosition(location));
            Container temp = null;
            if (entity instanceof TileEntityContainer entityContainer) {
                temp = entityContainer.createMenu(syncId, inventory, inventory.player);
            }

            if (temp != null && temp.getType() == type.getHandle()) {
                container = temp;
            } else {
                container = type.getHandle().create(syncId, inventory);
            }
        } else {
            container = builder.build(syncId, inventory, CraftLocation.toBlockPosition(location));
        }
        return container;
    }

    public Container create(final CraftMenuType<?> type, int syncId, PlayerInventory inventory, String title) {
        final Container container = create(type, syncId, inventory);
        container.setTitle(CraftChatMessage.fromStringOrNull(title));
        return container;
    }

    public Container create(final CraftMenuType<?> type, int syncId, PlayerInventory inventory, Location location, String title) {
        final Container container = create(type, syncId, inventory, location);
        container.setTitle(CraftChatMessage.fromStringOrNull(title));
        return container;
    }

    private interface ContainerBuilder {
        Container build(int syncId, PlayerInventory inventory, BlockPosition position);

        static ContainerBuilder locationBound(LocationBoundContainerBuilder builder) {
            return (syncId, inventory, position) -> builder.build(syncId, inventory, ContainerAccess.create(inventory.player.level(), position));
        }

        static ContainerBuilder tileEntity(BiFunction<BlockPosition, IBlockData, ITileInventory> builder, Block block) {
            return (syncId, inventory, position) -> {
                final EntityHuman player = inventory.player;
                final ITileInventory tileInventory;
                final IBlockData data = player.level().getBlockState(position);
                if (data.getBlock() == block) {
                    tileInventory = player.level().getBlockState(position).getMenuProvider(player.level(), position);
                } else {
                    tileInventory = builder.apply(position, block.defaultBlockState());
                }
                return tileInventory.createMenu(syncId, inventory, player);
            };
        }
    }

    private interface LocationBoundContainerBuilder {
        Container build(int syncId, PlayerInventory inventory, ContainerAccess access);
    }
}

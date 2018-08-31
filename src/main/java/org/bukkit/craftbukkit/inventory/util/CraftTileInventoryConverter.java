package org.bukkit.craftbukkit.inventory.util;

import net.minecraft.server.DimensionManager;
import net.minecraft.server.ITileInventory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TileEntityBeacon;
import net.minecraft.server.TileEntityBrewingStand;
import net.minecraft.server.TileEntityDispenser;
import net.minecraft.server.TileEntityDropper;
import net.minecraft.server.TileEntityFurnace;
import net.minecraft.server.TileEntityHopper;
import net.minecraft.server.TileEntityLootable;
import org.bukkit.craftbukkit.inventory.CraftInventoryBeacon;
import org.bukkit.craftbukkit.inventory.CraftInventoryBrewer;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.BeaconInventory;

public abstract class CraftTileInventoryConverter implements CraftInventoryCreator.InventoryConverter {

    public abstract ITileInventory getTileEntity(InventoryHolder holder);

    @Override
    public Inventory createInventory(InventoryType type) {
        return createInventory(null, type);
    }

    @Override
    public Inventory createInventory(InventoryType type, String title) {
        return createInventory(null, type, title);
    }

    @Override
    @Deprecated
    public Inventory createInventory(InventoryHolder holder, InventoryType type) {
        return getInventory(getTileEntity(holder));
    }

    @Override
    @Deprecated
    public Inventory createInventory(InventoryHolder holder, InventoryType type, String title) {
        ITileInventory te = getTileEntity(holder);
        if (te instanceof TileEntityLootable) {
            ((TileEntityLootable) te).setCustomName(CraftChatMessage.fromStringOrNull(title));
        }

        return getInventory(te);
    }

    public Inventory getInventory(ITileInventory tileEntity) {
        return tileEntity.getBukkitInventory();
    }

    public static class Furnace extends CraftTileInventoryConverter {

        @Override
        public ITileInventory getTileEntity(InventoryHolder owner) {
            TileEntityFurnace furnace = new TileEntityFurnace();
            furnace.setOwner(owner);
            furnace.setWorld(MinecraftServer.getServer().getWorldServer(DimensionManager.OVERWORLD)); // TODO: customize this if required
            return furnace;
        }

        @Override
        public Inventory createInventory(InventoryHolder owner, InventoryType type, String title) {
            ITileInventory tileEntity = getTileEntity(owner);
            ((TileEntityFurnace) tileEntity).setCustomName(CraftChatMessage.fromStringOrNull(title));
            return getInventory(tileEntity);
        }
    }

    public static class BrewingStand extends CraftTileInventoryConverter {

        @Override
        public ITileInventory getTileEntity(InventoryHolder owner) {
            TileEntityBrewingStand tileEntityBrewingStand = new TileEntityBrewingStand();
            tileEntityBrewingStand.setOwner(owner);
            return tileEntityBrewingStand;
        }

        @Override
        public Inventory createInventory(InventoryHolder holder, InventoryType type, String title) {
            // BrewingStand does not extend TileEntityLootable
            ITileInventory tileEntity = getTileEntity(holder);
            if (tileEntity instanceof TileEntityBrewingStand) {
                ((TileEntityBrewingStand) tileEntity).setCustomName(CraftChatMessage.fromStringOrNull(title));
            }
            return getInventory(tileEntity);
        }
    }

    public static class Beacon extends CraftTileInventoryConverter {

        @Override
        public ITileInventory getTileEntity(InventoryHolder owner) {
            TileEntityBeacon tileEntity = new TileEntityBeacon();
            tileEntity.setOwner(owner);
            return tileEntity;
        }
    }

    public static class Dispenser extends CraftTileInventoryConverter {

        @Override
        public ITileInventory getTileEntity(InventoryHolder owner) {
            TileEntityDispenser tileEntity = new TileEntityDispenser();
            tileEntity.setOwner(owner);
            return tileEntity;
        }
    }

    public static class Dropper extends CraftTileInventoryConverter {

        @Override
        public ITileInventory getTileEntity(InventoryHolder owner) {
            TileEntityDropper tileEntity = new TileEntityDropper();
            tileEntity.setOwner(owner);
            return tileEntity;
        }
    }

    public static class Hopper extends CraftTileInventoryConverter {

        @Override
        public ITileInventory getTileEntity(InventoryHolder owner) {
            TileEntityHopper tileEntity = new TileEntityHopper();
            tileEntity.setOwner(owner);
            return tileEntity;
        }
    }
}

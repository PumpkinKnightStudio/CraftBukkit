package org.bukkit.craftbukkit.inventory.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.ITileInventory;
import net.minecraft.world.InventorySubcontainer;
import net.minecraft.world.inventory.InventoryCraftResult;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntityBlastFurnace;
import net.minecraft.world.level.block.entity.TileEntityBrewingStand;
import net.minecraft.world.level.block.entity.TileEntityFurnaceFurnace;
import net.minecraft.world.level.block.entity.TileEntityShulkerBox;
import net.minecraft.world.level.block.entity.TileEntitySmoker;
import net.minecraft.world.level.block.state.IBlockData;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.inventory.CraftInventoryAnvil;
import org.bukkit.craftbukkit.inventory.CraftInventoryBeacon;
import org.bukkit.craftbukkit.inventory.CraftInventoryBrewer;
import org.bukkit.craftbukkit.inventory.CraftInventoryCartography;
import org.bukkit.craftbukkit.inventory.CraftInventoryCrafting;
import org.bukkit.craftbukkit.inventory.CraftInventoryEnchanting;
import org.bukkit.craftbukkit.inventory.CraftInventoryFurnace;
import org.bukkit.craftbukkit.inventory.CraftInventoryGeneral;
import org.bukkit.craftbukkit.inventory.CraftInventoryGrindstone;
import org.bukkit.craftbukkit.inventory.CraftInventoryLectern;
import org.bukkit.craftbukkit.inventory.CraftInventoryLoom;
import org.bukkit.craftbukkit.inventory.CraftInventorySmithing;
import org.bukkit.craftbukkit.inventory.CraftInventoryStonecutter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.MenuType;

public final class CraftInventoryMaker {

    public static final CraftInventoryMaker INSTANCE = new CraftInventoryMaker();
    private static final InventoryMaker DEFAULT_MAKER = CraftInventoryGeneral::new;
    private final Map<MenuType<?>, InventoryMaker> makers;

    private CraftInventoryMaker() {
        this.makers = new HashMap<>();
        this.makers.put(MenuType.GENERIC_9x1, DEFAULT_MAKER);
        this.makers.put(MenuType.GENERIC_9x2, DEFAULT_MAKER);
        this.makers.put(MenuType.GENERIC_9x3, DEFAULT_MAKER);
        this.makers.put(MenuType.GENERIC_9x4, DEFAULT_MAKER);
        this.makers.put(MenuType.GENERIC_9x5, DEFAULT_MAKER);
        this.makers.put(MenuType.GENERIC_9x6, DEFAULT_MAKER);
        this.makers.put(MenuType.GENERIC_3x3, DEFAULT_MAKER);
        this.makers.put(MenuType.ANVIL, (holder, type) -> new CraftInventoryAnvil(null, new InventorySubcontainer(2), new InventoryCraftResult()));
        this.makers.put(MenuType.BEACON, (holder, type) -> new CraftInventoryBeacon(new InventorySubcontainer(1)));
        this.makers.put(MenuType.BLAST_FURNACE, (holder, type) -> makeTileBacked(CraftInventoryFurnace::new, TileEntityBlastFurnace::new, Blocks.BLAST_FURNACE));
        this.makers.put(MenuType.BREWING_STAND, (holder, type) -> makeTileBacked(CraftInventoryBrewer::new, TileEntityBrewingStand::new, Blocks.BREWING_STAND));
        this.makers.put(MenuType.CRAFTING, (holder, type) -> new CraftInventoryCrafting(new InventorySubcontainer(9), new InventoryCraftResult()));
        this.makers.put(MenuType.ENCHANTMENT, (holder, type) -> new CraftInventoryEnchanting(new InventorySubcontainer(2)));
        this.makers.put(MenuType.FURNACE, (holder, type) -> makeTileBacked(CraftInventoryFurnace::new, TileEntityFurnaceFurnace::new, Blocks.FURNACE));
        this.makers.put(MenuType.GRINDSTONE, (holder, type) -> new CraftInventoryGrindstone(new InventorySubcontainer(2), new InventoryCraftResult()));
        this.makers.put(MenuType.HOPPER, DEFAULT_MAKER);
        this.makers.put(MenuType.LECTERN, (holder, type) -> new CraftInventoryLectern(new InventorySubcontainer(1)));
        this.makers.put(MenuType.LOOM, (holder, type) -> new CraftInventoryLoom(new InventorySubcontainer(3), new InventoryCraftResult()));
        this.makers.put(MenuType.MERCHANT, DEFAULT_MAKER); // special case where it doesn't actually return it's correct, this is okay though merchants shouldn't be made like this
        this.makers.put(MenuType.SHULKER_BOX, (holder, type) -> makeTileBacked(CraftInventory::new, TileEntityShulkerBox::new, Blocks.SHULKER_BOX));
        this.makers.put(MenuType.SMITHING, (holder, type) -> new CraftInventorySmithing(null, new InventorySubcontainer(3), new InventoryCraftResult()));
        this.makers.put(MenuType.SMOKER, (holder, type) -> makeTileBacked(CraftInventoryFurnace::new, TileEntitySmoker::new, Blocks.SMOKER));
        this.makers.put(MenuType.CARTOGRAPHY_TABLE, (holder, type) -> new CraftInventoryCartography(new InventorySubcontainer(2), new InventoryCraftResult()));
        this.makers.put(MenuType.STONECUTTER, (holder, type) -> new CraftInventoryStonecutter(new InventorySubcontainer(1), new InventoryCraftResult()));
    }

    public Inventory createInventory(InventoryHolder holder, MenuType<?> type) {
        return makers.get(type).createInventory(holder, type);
    }

    private <T extends ITileInventory> Inventory makeTileBacked(Function<T, Inventory> inv, BiFunction<BlockPosition, IBlockData, T> tileinv, Block block) {
        return inv.apply((T) tileinv.apply(BlockPosition.ZERO, block.defaultBlockState()));
    }


    interface InventoryMaker {
        Inventory createInventory(InventoryHolder holder, MenuType<?> type);
    }

}

package org.bukkit.craftbukkit.inventory.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import net.minecraft.core.BlockPosition;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.ITileInventory;
import net.minecraft.world.TileInventory;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ContainerAccess;
import net.minecraft.world.inventory.ContainerAnvil;
import net.minecraft.world.inventory.ContainerEnchantTable;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntityBlastFurnace;
import net.minecraft.world.level.block.entity.TileEntityBrewingStand;
import net.minecraft.world.level.block.entity.TileEntityFurnaceFurnace;
import net.minecraft.world.level.block.entity.TileEntitySmoker;
import org.bukkit.craftbukkit.inventory.CraftMenuType;
import org.bukkit.inventory.MenuType;

public final class CraftContainerCreator {

    public static final CraftContainerCreator INSTANCE = new CraftContainerCreator();

    private final Map<MenuType<?>, BiFunction<Integer, PlayerInventory, Container>> creator;

    public CraftContainerCreator() {
        this.creator = new HashMap<>();
        this.creator.put(MenuType.FURNACE, construct((i, playerinventory) -> new TileEntityFurnaceFurnace(BlockPosition.ZERO, Blocks.FURNACE.defaultBlockState())));
        this.creator.put(MenuType.SMOKER, construct((i, playerinventory) -> new TileEntitySmoker(BlockPosition.ZERO, Blocks.SMOKER.defaultBlockState())));
        this.creator.put(MenuType.BLAST_FURNACE, construct((i, playerinventory) -> new TileEntityBlastFurnace(BlockPosition.ZERO, Blocks.SMOKER.defaultBlockState())));
        this.creator.put(MenuType.BREWING_STAND, construct((i, playerinventory) -> new TileEntityBrewingStand(BlockPosition.ZERO, Blocks.BREWING_STAND.defaultBlockState())));
        this.creator.put(MenuType.ENCHANTMENT, construct((i, playerinventory) -> {
            return new TileInventory((syncId, pi, entityhuman) -> {
                return new ContainerEnchantTable(i, playerinventory, ContainerAccess.create(entityhuman.level(), entityhuman.blockPosition()));
            }, IChatBaseComponent.empty());
        }));
        this.creator.put(MenuType.ANVIL, (i, playerinventory) -> new ContainerAnvil(i, playerinventory, ContainerAccess.create(playerinventory.player.level(), playerinventory.player.blockPosition())));
    }

    public Container create(final CraftMenuType<?> type, int syncId, PlayerInventory inventory, String title) {
        final var function = creator.get(type);
        final Container container;
        if (function == null) {
            container = type.getHandle().create(syncId, inventory);
        } else {
            container = function.apply(syncId, inventory);
        }
        container.setTitle(IChatBaseComponent.literal(title));
        return container;
    }

    private static BiFunction<Integer, PlayerInventory, Container> construct(BiFunction<Integer, PlayerInventory, ITileInventory> entityFunction) {
        return (i, playerinventory) -> entityFunction.apply(i, playerinventory).createMenu(i, playerinventory, playerinventory.player);
    }
}

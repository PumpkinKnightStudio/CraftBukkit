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
import net.minecraft.world.inventory.ContainerCartography;
import net.minecraft.world.inventory.ContainerEnchantTable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntityBlastFurnace;
import net.minecraft.world.level.block.entity.TileEntityFurnaceFurnace;
import net.minecraft.world.level.block.entity.TileEntitySmoker;
import net.minecraft.world.level.block.state.IBlockData;
import org.bukkit.craftbukkit.inventory.CraftMenuType;
import org.bukkit.inventory.MenuType;

public final class CraftContainerCreator {

    public static final CraftContainerCreator INSTANCE = new CraftContainerCreator();

    private final Map<MenuType<?>, BiFunction<Integer, PlayerInventory, Container>> creator;

    public CraftContainerCreator() {
        this.creator = new HashMap<>();
        this.creator.put(MenuType.FURNACE, construct(TileEntityFurnaceFurnace::new, Blocks.FURNACE));
        this.creator.put(MenuType.SMOKER, construct(TileEntitySmoker::new, Blocks.SMOKER));
        this.creator.put(MenuType.BLAST_FURNACE, construct(TileEntityBlastFurnace::new, Blocks.BLAST_FURNACE));
        this.creator.put(MenuType.ENCHANTMENT, custom((i, playerinventory) -> new TileInventory((syncId, pi, entityhuman) -> construct(ContainerAnvil::new).apply(syncId, pi), IChatBaseComponent.empty())));
        this.creator.put(MenuType.CARTOGRAPHY_TABLE, construct(ContainerCartography::new));
        this.creator.put(MenuType.ANVIL, construct(ContainerAnvil::new));
        this.creator.put(MenuType.GRINDSTONE, construct(ContainerAnvil::new));
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

    private static BiFunction<Integer, PlayerInventory, Container> custom(BiFunction<Integer, PlayerInventory, ITileInventory> entityFunction) {
        return (i, playerinventory) -> entityFunction.apply(i, playerinventory).createMenu(i, playerinventory, playerinventory.player);
    }

    private static BiFunction<Integer, PlayerInventory, Container> construct(ITileInventoryBuilder builder, Block block) {
        return (i, playerinventory) -> builder.build(playerinventory.player.blockPosition(), block.defaultBlockState()).createMenu(i, playerinventory, playerinventory.player);
    }

    private static BiFunction<Integer, PlayerInventory, Container> construct(ContainerAccessBuilder builder) {
        return (i, playerinventory) -> builder.build(i, playerinventory, ContainerAccess.create(playerinventory.player.level(), playerinventory.player.blockPosition()));
    }

    private interface ContainerAccessBuilder {
        Container build(int syncId, PlayerInventory inventory, ContainerAccess access);
    }

    private interface ITileInventoryBuilder {
        ITileInventory build(BlockPosition position, IBlockData data);
    }
}

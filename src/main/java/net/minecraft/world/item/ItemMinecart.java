package net.minecraft.world.item;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.Holder;
import net.minecraft.core.dispenser.DispenseBehaviorItem;
import net.minecraft.core.dispenser.IDispenseBehavior;
import net.minecraft.core.dispenser.SourceBlock;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.vehicle.EntityMinecartAbstract;
import net.minecraft.world.item.context.ItemActionContext;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.BlockDispenser;
import net.minecraft.world.level.block.BlockMinecartTrackAbstract;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockPropertyTrackPosition;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3D;

// CraftBukkit start
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.block.BlockDispenseEvent;
// CraftBukkit end

public class ItemMinecart extends Item {

    private static final IDispenseBehavior DISPENSE_ITEM_BEHAVIOR = new DispenseBehaviorItem() {
        private final DispenseBehaviorItem defaultDispenseItemBehavior = new DispenseBehaviorItem();

        @Override
        public ItemStack execute(SourceBlock sourceblock, ItemStack itemstack) {
            EnumDirection enumdirection = (EnumDirection) sourceblock.state().getValue(BlockDispenser.FACING);
            WorldServer worldserver = sourceblock.level();
            Vec3D vec3d = sourceblock.center();
            double d0 = vec3d.x() + (double) enumdirection.getStepX() * 1.125D;
            double d1 = Math.floor(vec3d.y()) + (double) enumdirection.getStepY();
            double d2 = vec3d.z() + (double) enumdirection.getStepZ() * 1.125D;
            BlockPosition blockposition = sourceblock.pos().relative(enumdirection);
            IBlockData iblockdata = worldserver.getBlockState(blockposition);
            BlockPropertyTrackPosition blockpropertytrackposition = iblockdata.getBlock() instanceof BlockMinecartTrackAbstract ? (BlockPropertyTrackPosition) iblockdata.getValue(((BlockMinecartTrackAbstract) iblockdata.getBlock()).getShapeProperty()) : BlockPropertyTrackPosition.NORTH_SOUTH;
            double d3;

            if (iblockdata.is(TagsBlock.RAILS)) {
                if (blockpropertytrackposition.isAscending()) {
                    d3 = 0.6D;
                } else {
                    d3 = 0.1D;
                }
            } else {
                if (!iblockdata.isAir() || !worldserver.getBlockState(blockposition.below()).is(TagsBlock.RAILS)) {
                    return this.defaultDispenseItemBehavior.dispense(sourceblock, itemstack);
                }

                IBlockData iblockdata1 = worldserver.getBlockState(blockposition.below());
                BlockPropertyTrackPosition blockpropertytrackposition1 = iblockdata1.getBlock() instanceof BlockMinecartTrackAbstract ? (BlockPropertyTrackPosition) iblockdata1.getValue(((BlockMinecartTrackAbstract) iblockdata1.getBlock()).getShapeProperty()) : BlockPropertyTrackPosition.NORTH_SOUTH;

                if (enumdirection != EnumDirection.DOWN && blockpropertytrackposition1.isAscending()) {
                    d3 = -0.4D;
                } else {
                    d3 = -0.9D;
                }
            }

            // CraftBukkit start
            // EntityMinecartAbstract entityminecartabstract = EntityMinecartAbstract.createMinecart(worldserver, d0, d1 + d3, d2, ((ItemMinecart) itemstack.getItem()).type);
            ItemStack itemstack1 = itemstack.split(1);
            org.bukkit.block.Block block2 = CraftBlock.at(worldserver, sourceblock.pos());
            CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack1);

            BlockDispenseEvent event = new BlockDispenseEvent(block2, craftItem.clone(), new org.bukkit.util.Vector(d0, d1 + d3, d2));
            if (!BlockDispenser.eventFired) {
                worldserver.getCraftServer().getPluginManager().callEvent(event);
            }

            if (event.isCancelled()) {
                itemstack.grow(1);
                return itemstack;
            }

            if (!event.getItem().equals(craftItem)) {
                itemstack.grow(1);
                // Chain to handler for new item
                ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                IDispenseBehavior idispensebehavior = (IDispenseBehavior) BlockDispenser.DISPENSER_REGISTRY.get(eventStack.getItem());
                if (idispensebehavior != IDispenseBehavior.NOOP && idispensebehavior != this) {
                    idispensebehavior.dispense(sourceblock, eventStack);
                    return itemstack;
                }
            }

            itemstack1 = CraftItemStack.asNMSCopy(event.getItem());
            EntityMinecartAbstract entityminecartabstract = EntityMinecartAbstract.createMinecart(worldserver, event.getVelocity().getX(), event.getVelocity().getY(), event.getVelocity().getZ(), ((ItemMinecart) itemstack1.getItem()).type, itemstack1, (EntityHuman) null);

            if (!worldserver.addFreshEntity(entityminecartabstract)) itemstack.grow(1);
            // itemstack.shrink(1); // CraftBukkit - handled during event processing
            // CraftBukkit end
            return itemstack;
        }

        @Override
        protected void playSound(SourceBlock sourceblock) {
            sourceblock.level().levelEvent(1000, sourceblock.pos(), 0);
        }
    };
    final EntityMinecartAbstract.EnumMinecartType type;

    public ItemMinecart(EntityMinecartAbstract.EnumMinecartType entityminecartabstract_enumminecarttype, Item.Info item_info) {
        super(item_info);
        this.type = entityminecartabstract_enumminecarttype;
        BlockDispenser.registerBehavior(this, ItemMinecart.DISPENSE_ITEM_BEHAVIOR);
    }

    @Override
    public EnumInteractionResult useOn(ItemActionContext itemactioncontext) {
        World world = itemactioncontext.getLevel();
        BlockPosition blockposition = itemactioncontext.getClickedPos();
        IBlockData iblockdata = world.getBlockState(blockposition);

        if (!iblockdata.is(TagsBlock.RAILS)) {
            return EnumInteractionResult.FAIL;
        } else {
            ItemStack itemstack = itemactioncontext.getItemInHand();

            if (world instanceof WorldServer) {
                WorldServer worldserver = (WorldServer) world;
                BlockPropertyTrackPosition blockpropertytrackposition = iblockdata.getBlock() instanceof BlockMinecartTrackAbstract ? (BlockPropertyTrackPosition) iblockdata.getValue(((BlockMinecartTrackAbstract) iblockdata.getBlock()).getShapeProperty()) : BlockPropertyTrackPosition.NORTH_SOUTH;
                double d0 = 0.0D;

                if (blockpropertytrackposition.isAscending()) {
                    d0 = 0.5D;
                }

                EntityMinecartAbstract entityminecartabstract = EntityMinecartAbstract.createMinecart(worldserver, (double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.0625D + d0, (double) blockposition.getZ() + 0.5D, this.type, itemstack, itemactioncontext.getPlayer());

                // CraftBukkit start
                if (org.bukkit.craftbukkit.event.CraftEventFactory.callEntityPlaceEvent(itemactioncontext, entityminecartabstract).isCancelled()) {
                    return EnumInteractionResult.FAIL;
                }
                // CraftBukkit end
                if (!worldserver.addFreshEntity(entityminecartabstract)) return EnumInteractionResult.PASS; // CraftBukkit
                worldserver.gameEvent((Holder) GameEvent.ENTITY_PLACE, blockposition, GameEvent.a.of(itemactioncontext.getPlayer(), worldserver.getBlockState(blockposition.below())));
            }

            itemstack.shrink(1);
            return EnumInteractionResult.sidedSuccess(world.isClientSide);
        }
    }
}

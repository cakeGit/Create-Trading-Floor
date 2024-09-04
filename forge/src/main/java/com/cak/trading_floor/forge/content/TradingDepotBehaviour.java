package com.cak.trading_floor.forge.content;

import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.chute.SmartChuteFilterSlotPositioning;
import com.simibubi.create.content.logistics.funnel.FunnelFilterSlotPositioning;
import com.simibubi.create.content.redstone.FilteredDetectorFilterSlot;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.VersionedInventoryTrackerBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.NBTHelper;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

public class TradingDepotBehaviour extends BlockEntityBehaviour {
    
    public static final BehaviourType<TradingDepotBehaviour> TYPE = new BehaviourType<>();
    
    TransportedItemStack input;
    List<ItemStack> output;
    List<TransportedItemStack> incoming;

    TradingDepotItemHandler itemHandler;
    LazyOptional<TradingDepotItemHandler> itemHandlerLazyOptional;

    VersionedInventoryTrackerBehaviour invVersionTracker;

    public TradingDepotBehaviour(SmartBlockEntity be) {
        super(be);
        itemHandler = new TradingDepotItemHandler(this);
        itemHandlerLazyOptional = LazyOptional.of(() -> itemHandler);
        output = new ArrayList<>();
        incoming = new ArrayList<>();
    }

    @Override
    public void tick() {
        super.tick();

        Level world = blockEntity.getLevel();

        for (Iterator<TransportedItemStack> iterator = incoming.iterator(); iterator.hasNext();) {
            TransportedItemStack ts = iterator.next();
            if (!tick(ts))
                continue;
            if (world.isClientSide && !blockEntity.isVirtual())
                continue;
            if (input == null) {
                input = ts;
            } else {
                if (!ItemHelper.canItemStackAmountsStack(input.stack, ts.stack)) {
                    Vec3 vec = VecHelper.getCenterOf(blockEntity.getBlockPos());
                    Containers.dropItemStack(blockEntity.getLevel(), vec.x, vec.y + .5f, vec.z, ts.stack);
                } else {
                    input.stack.grow(ts.stack.getCount());
                }
            }
            iterator.remove();
            blockEntity.notifyUpdate();
        }

        if (input == null)
            return;
        tick(input);
    }

    protected boolean tick(TransportedItemStack input) {
        input.prevBeltPosition = input.beltPosition;
        input.prevSideOffset = input.sideOffset;
        float diff = .5f - input.beltPosition;
        if (diff > 1 / 512f) {
            if (diff > 1 / 32f && !BeltHelper.isItemUpright(input.stack))
                input.angle += 1;
            input.beltPosition += diff / 4f;
        }
        return diff < 1 / 16f;
    }

    public void addAdditionalBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(new DirectBeltInputBehaviour(blockEntity)
                .allowingBeltFunnels()
                .onlyInsertWhen(side -> blockEntity.getBlockState().getValue(FACING).getOpposite() == side)
                .setInsertionHandler(this::tryInsertingFromSide));
        behaviours.add(new FilteringBehaviour(blockEntity, new TradingDepotFilterSlotPositioning())
                .withCallback($ -> invVersionTracker.reset()));
        behaviours.add(invVersionTracker = new VersionedInventoryTrackerBehaviour(blockEntity));
    }

    private ItemStack tryInsertingFromSide(TransportedItemStack transportedStack, Direction side, boolean simulate) {
        ItemStack inserted = transportedStack.stack;

        int size = transportedStack.stack.getCount();
        transportedStack = transportedStack.copy();
        transportedStack.beltPosition = side.getAxis()
                .isVertical() ? .5f : 0;
        transportedStack.insertedFrom = side;
        transportedStack.prevSideOffset = transportedStack.sideOffset;
        transportedStack.prevBeltPosition = transportedStack.beltPosition;
        ItemStack remainder = insert(transportedStack, simulate);
        if (remainder.getCount() != size)
            blockEntity.notifyUpdate();

        return remainder;
    }

    public int getPresentStackSize() {
        int cumulativeStackSize = 0;
        cumulativeStackSize += getInputStack().getCount();
        for (ItemStack stack : output)
            cumulativeStackSize += stack
                    .getCount();
        return cumulativeStackSize;
    }

    public int getRemainingSpace() {
        int cumulativeStackSize = getPresentStackSize();
        for (TransportedItemStack transportedItemStack : incoming)
            cumulativeStackSize += transportedItemStack.stack.getCount();
        return 64 - cumulativeStackSize;
    }

    public ItemStack insert(TransportedItemStack input, boolean simulate) {
            int remainingSpace = getRemainingSpace();
            ItemStack inserted = input.stack;
            if (remainingSpace <= 0)
                return inserted;
            if (this.input != null && !ItemHelper.canItemStackAmountsStack(this.input.stack, inserted))
                return inserted;

            ItemStack returned = ItemStack.EMPTY;
            if (remainingSpace < inserted.getCount()) {
                returned = ItemHandlerHelper.copyStackWithSize(input.stack, inserted.getCount() - remainingSpace);
                if (!simulate) {
                    TransportedItemStack copy = input.copy();
                    copy.stack.setCount(remainingSpace);
                    if (this.input != null)
                        incoming.add(copy);
                    else
                        this.input = copy;
                }
            } else {
                if (!simulate) {
                    if (this.input != null)
                        incoming.add(input);
                    else
                        this.input = input;
                }
            }
            return returned;
    }

    public boolean isEmpty() {
        return input == null && isOutputEmpty();
    }

    public boolean isOutputEmpty() {
        for (ItemStack stack : output)
            if (!stack.isEmpty())
                return false;
        return true;
    }


    @Override
    public void destroy() {
        super.destroy();
        Level level = getWorld();
        BlockPos pos = getPos();
        ItemHelper.dropContents(level, pos, itemHandler);
    }
    
    @Override
    public void unload() {
        if (itemHandlerLazyOptional != null)
            itemHandlerLazyOptional.invalidate();
    }
    
    @Override
    public void read(CompoundTag nbt, boolean clientPacket) {
        input = null;
        if (nbt.contains("Input"))
            input = TransportedItemStack.read(nbt.getCompound("Input"));
        
        int outputCount = nbt.getInt("OutputCount");
        output = new ArrayList<>(outputCount);
        
        for (int i = 0; i < outputCount; i++) {
            output.add(ItemStack.of(nbt.getCompound("Output" + i)));
        }

        ListTag list = nbt.getList("Incoming", Tag.TAG_COMPOUND);
        incoming = NBTHelper.readCompoundList(list, TransportedItemStack::read);
    }
    
    @Override
    public void write(CompoundTag nbt, boolean clientPacket) {
        if (input != null)
            nbt.put("Input", input.serializeNBT());
        
        nbt.putInt("OutputCount", output.size());
        for (int i = 0; i < output.size(); i++) {
            nbt.put("Output" + i, output.get(i).save(new CompoundTag()));
        }

        if (!incoming.isEmpty())
            nbt.put("Incoming", NBTHelper.writeCompoundList(incoming, TransportedItemStack::serializeNBT));
    }
    
    public LazyOptional<TradingDepotItemHandler> getItemHandler() {
        return itemHandlerLazyOptional;
    }

    public ItemStack getInputStack() {
        return input == null ? ItemStack.EMPTY : input.stack;
    }

    public void setInputStack(TransportedItemStack input) {
        this.input = input;
    }

    public void removeInputStack() {
        this.input = null;
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }
    
}

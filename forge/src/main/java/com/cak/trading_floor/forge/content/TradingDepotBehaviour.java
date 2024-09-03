package com.cak.trading_floor.forge.content;

import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.NBTHelper;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.LazyOptional;

import java.util.ArrayList;
import java.util.Collection;
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
    }

    protected boolean tick(TransportedItemStack heldItem) {
        heldItem.prevBeltPosition = heldItem.beltPosition;
        heldItem.prevSideOffset = heldItem.sideOffset;
        float diff = .5f - heldItem.beltPosition;
        if (diff > 1 / 512f) {
            if (diff > 1 / 32f && !BeltHelper.isItemUpright(heldItem.stack))
                heldItem.angle += 1;
            heldItem.beltPosition += diff / 4f;
        }
        return diff < 1 / 16f;
    }

    public void addAdditionalBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(new DirectBeltInputBehaviour(blockEntity)
                .allowingBeltFunnels()
                .onlyInsertWhen(side -> blockEntity.getBlockState().getValue(FACING).getOpposite() == side)
                .setInsertionHandler(itemHandler::insertItem));
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

    public ItemStack getHeldItemStack() {
        return input == null ? ItemStack.EMPTY : input.stack;
    }

    public void setHeldItem(TransportedItemStack heldItem) {
        this.input = heldItem;
    }

    public void removeHeldItem() {
        this.input = null;
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }
    
}

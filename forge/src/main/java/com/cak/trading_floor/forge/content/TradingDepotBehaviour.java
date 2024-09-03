package com.cak.trading_floor.forge.content;

import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.util.LazyOptional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TradingDepotBehaviour extends BlockEntityBehaviour {
    
    public static final BehaviourType<TradingDepotBehaviour> TYPE = new BehaviourType<>();
    
    ItemStack input = ItemStack.EMPTY;
    List<ItemStack> output = new ArrayList<>();
    
    TradingDepotItemHandler itemHandler;
    LazyOptional<TradingDepotItemHandler> itemHandlerLazyOptional;
    
    public TradingDepotBehaviour(SmartBlockEntity be) {
        super(be);
        itemHandler = new TradingDepotItemHandler(this);
        itemHandlerLazyOptional = LazyOptional.of(() -> itemHandler);
    }
    
    public void addAdditionalBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(new DirectBeltInputBehaviour(blockEntity).allowingBeltFunnels()
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
        super.read(nbt, clientPacket);
        input = ItemStack.of(nbt.getCompound("Input"));
        
        int outputCount = nbt.getInt("OutputCount");
        output = new ArrayList<>(outputCount);
        
        for (int i = 0; i < outputCount; i++) {
            output.add(ItemStack.of(nbt.getCompound("Output" + i)));
        }
    }
    
    @Override
    public void write(CompoundTag nbt, boolean clientPacket) {
        super.write(nbt, clientPacket);
        nbt.put("Input", input.save(new CompoundTag()));
        
        nbt.putInt("OutputCount", output.size());
        for (int i = 0; i < output.size(); i++) {
            nbt.put("Output" + i, output.get(i).save(new CompoundTag()));
        }
    }
    
    public LazyOptional<TradingDepotItemHandler> getItemHandler() {
        return itemHandlerLazyOptional;
    }
    
    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }
    
}

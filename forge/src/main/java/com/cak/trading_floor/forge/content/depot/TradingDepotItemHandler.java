package com.cak.trading_floor.forge.content.depot;

import com.cak.trading_floor.forge.content.depot.behavior.TradingDepotBehaviour;
import com.cak.trading_floor.foundation.ItemCopyWithCount;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

public class TradingDepotItemHandler implements IItemHandler {
    
    TradingDepotBehaviour behaviour;
    
    public TradingDepotItemHandler(TradingDepotBehaviour behaviour) {
        this.behaviour = behaviour;
    }
    
    @Override
    public int getSlots() {
        return 1 + behaviour.getResults().size();
    }
    
    @Override
    public @NotNull ItemStack getStackInSlot(int i) {
        return i == 0 ? behaviour.getOfferStack() :
            (i - 1 < behaviour.getResults().size() ? behaviour.getResults().get(i - 1) : ItemStack.EMPTY);
    }
    
    @Override
    public @NotNull ItemStack insertItem(int i, @NotNull ItemStack arg, boolean bl) {
        if (i != 0) return arg;
        
        if (!behaviour.getOfferStack().isEmpty() && !ItemHandlerHelper.canItemStacksStack(behaviour.getOfferStack(), arg))
            return arg;
        
        ItemStack existingStack = behaviour.getOfferStack();
        
        int oldCount = existingStack.getCount();
        int newCount = Math.min(existingStack.getMaxStackSize(), oldCount + arg.getCount());
        int added = newCount - oldCount;
        int remaining = arg.getCount() - added;
        
        if (!bl) {
            behaviour.setOfferStack(ItemCopyWithCount.of(arg, newCount));
            if (newCount != oldCount)
                behaviour.spinOfferOrSomething();
            behaviour.blockEntity.sendData();
        }
        
        return ItemCopyWithCount.of(arg, remaining);
    }
    
    @Override
    public @NotNull ItemStack extractItem(int i, int j, boolean bl) {
        if (i == 0) return ItemStack.EMPTY;
        
        int listIndex = i - 1;
        if (listIndex >= behaviour.getResults().size()) return ItemStack.EMPTY;
        
        ItemStack currentStack = behaviour.getResults().get(listIndex);
        
        int extractedCount = Math.min(currentStack.getCount(), j);
        
        ItemStack resultStack = ItemCopyWithCount.of(currentStack, extractedCount);
        ItemStack remainderStack = ItemCopyWithCount.of(currentStack, currentStack.getCount() - extractedCount);
        
        if (!bl) {
            this.behaviour.getResults().set(listIndex, remainderStack);
            this.behaviour.doPruneEmptyStacksNextTick();
            behaviour.blockEntity.sendData();
        }
        
        return resultStack;
    }
    
    @Override
    public int getSlotLimit(int i) {
        return 64;
    }
    
    @Override
    public boolean isItemValid(int i, @NotNull ItemStack arg) {
        return true;
    }
    
    public ItemStack insertItem(TransportedItemStack transportedItemStack, Direction direction, boolean b) {
        return insertItem(0, transportedItemStack.stack, b);
    }
    
}

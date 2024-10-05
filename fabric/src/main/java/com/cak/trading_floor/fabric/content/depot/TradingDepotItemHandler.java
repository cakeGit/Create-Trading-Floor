package com.cak.trading_floor.fabric.content.depot;

import com.cak.trading_floor.fabric.content.depot.behavior.TradingDepotBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

import java.util.Iterator;

public class TradingDepotItemHandler implements Storage<ItemStack> {
    
    TradingDepotBehaviour behaviour;
    
    public TradingDepotItemHandler(TradingDepotBehaviour behaviour) {
        this.behaviour = behaviour;
    }
//
//    @Override
//    public int getSlots() {
//        return 1 + behaviour.getResults().size();
//    }
//
//    @Override
//    public @NotNull ItemStack getStackInSlot(int i) {
//        return i == 0 ? behaviour.getOfferStack() : behaviour.getResults().get(i - 1);
//    }
//
//    @Override
//    public @NotNull ItemStack insertItem(int i, @NotNull ItemStack arg, boolean bl) {
//        if (i != 0) return arg;
//
//        if (!behaviour.getOfferStack().isEmpty() && !ItemHandlerHelper.canItemStacksStack(behaviour.getOfferStack(), arg))
//            return arg;
//
//        ItemStack incomingStack = behaviour.getOfferStack();
//
//        int newCount = Math.min(incomingStack.getMaxStackSize(), incomingStack.getCount() + arg.getCount());
//        int added = newCount - incomingStack.getCount();
//        int remaining = arg.getCount() - added;
//
//        if (!bl) {
//            behaviour.setOfferStack(new TransportedItemStack(arg.copyWithCount(newCount)));
//            behaviour.blockEntity.sendData();
//        }
//
//        return arg.copyWithCount(remaining);
//    }
//
//    @Override
//    public @NotNull ItemStack extractItem(int i, int j, boolean bl) {
//    }
//
//    @Override
//    public int getSlotLimit(int i) {
//        return 64;
//    }
//
//    @Override
//    public boolean isItemValid(int i, @NotNull ItemStack arg) {
//        return true;
//    }
    
    public ItemStack insertItem(TransportedItemStack transportedItemStack, Direction direction, boolean b) {
        return insertItem(0, transportedItemStack.stack, b);
    }
    
    @Override
    public long insert(ItemStack resource, long maxAmount, TransactionContext transaction) {
        return 0;
    }
    
    @Override
    public long extract(ItemStack resource, long maxAmount, TransactionContext transaction) {
        int targetIndex = -1;
        for (int i = 0; i < behaviour.getResults().size(); i++) {
            if (resource.isEmpty())
                targetIndex = i;
            if (resource.is(behaviour.getResults().get(i).getItem()))
                targetIndex = i;
            
            if (targetIndex != -1)
                break;
        }
        if (targetIndex == -1) return 0;
        
        ItemStack currentStack = behaviour.getResults().get(targetIndex);

        int extractedCount = (int) Math.min(currentStack.getCount(), maxAmount);

        ItemStack remainderStack = currentStack.copyWithCount(currentStack.getCount() - extractedCount);
        int finalTargetIndex = targetIndex;
        transaction.addCloseCallback((context, result) -> {
            if (result.wasCommitted()) {
                if (remainderStack.isEmpty())
                    this.behaviour.getResults().remove(finalTargetIndex);
                else
                    this.behaviour.getResults().set(finalTargetIndex, remainderStack);
                behaviour.blockEntity.sendData();
            }
        });

        return extractedCount;
    }
    
    @Override
    public Iterator<StorageView<ItemStack>> iterator() {
        return null;
    }
    
}

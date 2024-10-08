package com.cak.trading_floor.fabric.content.depot;

import com.cak.trading_floor.fabric.content.depot.behavior.TradingDepotBehaviour;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TradingDepotItemHandler implements Storage<ItemVariant> {
    
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
    
//    public ItemStack insertTransportedStack(TransportedItemStack transportedItemStack, Direction direction, boolean b) {
//        return insert(transportedItemStack.stack, transportedItemStack.stack.getCount(),);
//    }
    
    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        ItemStack currentStack = behaviour.getOfferStack();
        
        int oldCount = currentStack.getCount();
        
        int resultCount = (int) Math.min(currentStack.getCount() + maxAmount, currentStack.getMaxStackSize());
        int stackChange = resultCount - currentStack.getCount();
        
        ItemStack resultStack = currentStack.copyWithCount(resultCount);
        transaction.addCloseCallback((context, result) -> {
            if (result.wasCommitted()) {
                behaviour.setOfferStack(resultStack);
                if (resultCount != oldCount)
                    behaviour.spinOfferOrSomething();
                behaviour.blockEntity.sendData();
            }
        });
        
        return stackChange;
    }
    
    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        int targetIndex = -1;
        for (int i = 0; i < behaviour.getResults().size(); i++) {
            if (resource.matches(behaviour.getResults().get(i)))
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
    public Iterator<StorageView<ItemVariant>> iterator() {
        ArrayList<StorageView<ItemVariant>> combined = new ArrayList<>();
        combined.add(new StorageViewAccess(behaviour::getOfferStack, behaviour::setOfferStack) {
            @Override
            protected void onExtract() {
                behaviour.blockEntity.sendData();
            }
        });
        for (int i = 0; i < behaviour.getResults().size(); i++) {
            int finalI = i;
            combined.add(new StorageViewAccess(() -> behaviour.getResults().get(finalI), (stack) -> behaviour.getResults().set(finalI, stack)) {
                @Override
                protected void onExtract() {
                    behaviour.queueResultStackPrune();
                    behaviour.blockEntity.sendData();
                }
            });
        }
        return combined.iterator();
    }
    
    private static class StorageViewAccess implements StorageView<ItemVariant> {
        
        Supplier<ItemStack> getter;
        Consumer<ItemStack> setter;
        
        public StorageViewAccess(Supplier<ItemStack> getter, Consumer<ItemStack> setter) {
            this.getter = getter;
            this.setter = setter;
        }
        
        
        @Override
        public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            if (!resource.matches(getter.get()))
                return 0;
            
            ItemStack currentStack = getter.get();
            
            int extractedCount = (int) Math.min(currentStack.getCount(), maxAmount);
            ItemStack remainderStack = currentStack.copyWithCount(currentStack.getCount() - extractedCount);
            
            transaction.addCloseCallback((context, result) -> {
                if (result.wasCommitted()) {
                    setter.accept(remainderStack);
                    onExtract();
                }
            });
            
            return extractedCount;
        }
        
        protected void onExtract() {}
        
        @Override
        public boolean isResourceBlank() {
            return getter.get().isEmpty();
        }
        
        @Override
        public ItemVariant getResource() {
            return ItemVariant.of(getter.get());
        }
        
        @Override
        public long getAmount() {
            return getter.get().isEmpty() ? 0 : getter.get().getCount();
        }
        
        @Override
        public long getCapacity() {
            return getter.get().isEmpty() ? 64 : getter.get().getMaxStackSize();
        }
        
    }
    
}

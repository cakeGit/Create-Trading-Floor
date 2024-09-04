package com.cak.trading_floor.forge.content;

import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TradingDepotBlockEntity extends SmartBlockEntity {
    
    TradingDepotBehaviour tradingDepotBehaviour;
    FilteringBehaviour filtering;
    
    public TradingDepotBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }
    
    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(tradingDepotBehaviour = new TradingDepotBehaviour(this));
        behaviours.add(filtering = new FilteringBehaviour(this, new TradingDepotFilterSlotPositioning())
            .withCallback($ -> tradingDepotBehaviour.invVersionTracker.reset()));
        tradingDepotBehaviour.filtering = filtering;
        tradingDepotBehaviour.addAdditionalBehaviours(behaviours);
    }
    
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER)
            return tradingDepotBehaviour.getItemHandler().cast();
        return super.getCapability(cap, side);
    }
    
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        tradingDepotBehaviour.itemHandlerLazyOptional.invalidate();
    }
    
    /**
     * Try perform for the offer, return false if it was not completed (due to not having enough money), and put the
     * output into the cost A depot
     */
    protected boolean tryTakeMerchantOffer(MerchantOffer offer, TradingDepotBehaviour costASource, List<TradingDepotBehaviour> costBSources) {
        //Quickly check if the A cost matches, if not dont bother with anything else
        if (!ItemStack.isSameItem(offer.getBaseCostA(), costASource.input.stack)) return false;
        
        //Check the second cost if it's there
        ItemStack totalCostBSource = ItemStack.EMPTY;
        if (!offer.getCostB().isEmpty()) {
            costBSources = costBSources.stream()
                .filter(depot -> ItemStack.isSameItem(offer.getCostB(), depot.input.stack))
                .toList();
            
            if (costBSources.isEmpty()) return false;
            
            int totalCostB = 0;
            for (TradingDepotBehaviour depot : costBSources)
                totalCostB += depot.input.stack.getCount();
            
            if (offer.getCostB().getCount() > totalCostB) return false;
            
            totalCostBSource = costBSources.get(0).input.stack.copyWithCount(totalCostB);
        }
        
        //Check both match
        if (!satisfiedBaseCostBy(offer, costASource.input.stack, totalCostBSource)) return false;
        
        //Perform transaction
        costASource.input.stack = costASource.input.stack
            .copyWithCount(costASource.input.stack.getCount() - offer.getBaseCostA().getCount());
        takeTotalFromSources(costBSources, offer.getCostB().getCount());
        
        costASource.output.add(offer.assemble());
        
        return true;
    }
    
    protected void takeTotalFromSources(List<TradingDepotBehaviour> costBSources, int totalExtractCount) {
        int i = 0;
        
        while (totalExtractCount > 0) {
            if (costBSources.size() <= i) return;
            
            TradingDepotBehaviour costSource = costBSources.get(i);
            
            int currentCount = costSource.input.stack.getCount();
            int extractCount = Math.min(totalExtractCount, currentCount);
            
            costSource.input.stack = costSource.input.stack.copyWithCount(currentCount - extractCount);
            
            totalExtractCount -= extractCount;
            i++;
        }
    }
    
    public boolean tryTradeWith(Villager villager, List<TradingDepotBehaviour> allDepots) {
        if (!tradingDepotBehaviour.isOutputEmpty()) return false;
        
        //Don't use self
        List<TradingDepotBehaviour> costBSources = allDepots.stream()
            .filter(depot -> depot != tradingDepotBehaviour)
            .toList();
        
        boolean hadSuccessfulTrade = false;
        boolean hasSpace = true;
        
        for (MerchantOffer offer : villager.getOffers()) {
            if (!hasSpace) break;
            
            if (!filtering.test(offer.getResult())) continue;
            List<TradingDepotBehaviour> filteredCostBSources = costBSources.stream().filter(depot -> depot.canBeUsedFor(offer)).toList();
            
            boolean trading = true;
            while (trading) {
                if (tradingDepotBehaviour.output.size() >= 8) {
                    tradingDepotBehaviour.combineOutputs();
                    if (tradingDepotBehaviour.output.size() >= 8) {
                        hasSpace = false;
                        break;
                    }
                }
                
                trading = tryTakeMerchantOffer(offer, tradingDepotBehaviour, filteredCostBSources);
                hadSuccessfulTrade = hadSuccessfulTrade || trading;
            }
        }
        
        if (hadSuccessfulTrade) {
            tradingDepotBehaviour.combineOutputs();
            
            villager.playCelebrateSound();
            tradingDepotBehaviour.blockEntity.notifyUpdate();
        }
        
        return true;
    }
    
    public static boolean satisfiedBaseCostBy(MerchantOffer offer, ItemStack playerOfferA, ItemStack playerOfferB) {
        return isRequiredItem(playerOfferA, offer.getBaseCostA()) &&
            playerOfferA.getCount() >= offer.getBaseCostA().getCount() &&
            isRequiredItem(playerOfferB, offer.getCostB()) &&
            playerOfferB.getCount() >= offer.getCostB().getCount();
    }
    
    private static boolean isRequiredItem(ItemStack available, ItemStack cost) {
        if (cost.isEmpty() && available.isEmpty()) {
            return true;
        }
        ItemStack itemstack = available.copy();
        if (itemstack.getItem().isDamageable(itemstack)) {
            itemstack.setDamageValue(itemstack.getDamageValue());
        }
        return net.minecraft.world.item.ItemStack.isSameItem(itemstack, cost) && (!cost.hasTag() || itemstack.hasTag() && NbtUtils.compareNbt(cost.getTag(), itemstack.getTag(), false));
    }
    
    public boolean hasInputStack() {
        return tradingDepotBehaviour.input != null && !tradingDepotBehaviour.input.stack.isEmpty();
    }
    
}

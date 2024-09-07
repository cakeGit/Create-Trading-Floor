package com.cak.trading_floor.forge.content;

import com.cak.trading_floor.forge.foundation.AttachedTradingDepotFinder;
import com.cak.trading_floor.forge.foundation.MerchantOfferInfo;
import com.cak.trading_floor.forge.foundation.TFLang;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.utility.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TradingDepotBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {
    
    TradingDepotBehaviour tradingDepotBehaviour;
    FilteringBehaviour filtering;
    
    public TradingDepotBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }
    
    List<BlockEntity> tradingDepotsForDisplay = new ArrayList<>();
    
    /**
     * Note that last trade is not set to null after a failed trade, only the count changes
     */
    @Nullable
    MerchantOfferInfo lastTrade;
    int lastTradeCount = 0;
    
    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        BlockPos attachedWorkstationPosition = getBlockPos().relative(
            getBlockState().getValue(TradingDepotBlock.FACING).getOpposite()
        );
        updateOtherSourcesForTooltip(attachedWorkstationPosition);
        
        tradingDepotBehaviour.addContentsToTooltip(tooltip);
        
        if (lastTrade != null) {
            TFLang.translate("tooltip.trading_depot.last_trade")
                .add(TFLang.text(" (x" + lastTradeCount + ")").color(lastTradeCount == 0 ? 0xFF5555 : 0x55FFFF))
                .forGoggles(tooltip);
            
            addTradeToGoggles(tooltip, lastTrade);
        }
        
        int tradingDepotOtherSourceCount = tradingDepotsForDisplay.size() - 1;
        
        if (tradingDepotOtherSourceCount > 0) {
            TFLang.text("").forGoggles(tooltip);
            TFLang.translate("tooltip.trading_depot.connected_to_other")
                .add(TFLang.text(" " + tradingDepotOtherSourceCount + " ").style(ChatFormatting.AQUA))
                .translate("tooltip.trading_depot.other_trading_depot" + (tradingDepotOtherSourceCount > 1 ? "s" : ""))
                .style(ChatFormatting.DARK_GRAY)
                .forGoggles(tooltip);
        }
        return true;
    }
    
    private void addTradeToGoggles(List<Component> tooltip, MerchantOfferInfo trade) {
        LangBuilder costText = TFLang.itemStack(trade.getCostA());
        
        if (!trade.getCostB().isEmpty())
            costText.text(" + ")
                .add(TFLang.itemStack(trade.getCostB()))
                .style(ChatFormatting.GRAY);
        
        costText.forGoggles(tooltip, 1);
        
        TFLang.text("→ ")
            .add(TFLang.itemStack(trade.getResult()))
            .style(ChatFormatting.WHITE)
            .forGoggles(tooltip, 2);
    }
    
    private void updateOtherSourcesForTooltip(BlockPos attachedWorkstationPosition) {
        if (level == null) {
            tradingDepotsForDisplay = new ArrayList<>();
            return;
        }
        
        tradingDepotsForDisplay = AttachedTradingDepotFinder.lookForTradingDepots(level, attachedWorkstationPosition).stream()
            .map(blockPos -> level.getBlockEntity(blockPos))
            .filter(blockEntity -> blockEntity instanceof TradingDepotBlockEntity)
            .toList();
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
    
    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        if (tag.contains("LastTrade"))
            lastTrade = MerchantOfferInfo.read(tag.getCompound("LastTrade"));
        lastTradeCount = tag.getInt("LastTradeCount");
    }
    
    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        if (lastTrade != null)
            tag.put("LastTrade", lastTrade.write(new CompoundTag()));
        tag.putInt("LastTradeCount", lastTradeCount);
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
    
    public void tryTradeWith(Villager villager, List<TradingDepotBehaviour> allDepots) {
        if (!tradingDepotBehaviour.isOutputEmpty()) return;
        
        //Don't use self
        List<TradingDepotBehaviour> costBSources = allDepots.stream()
            .filter(depot -> depot != tradingDepotBehaviour)
            .toList();
        
        boolean hadSuccessfulTrade = false;
        boolean hasSpace = true;
        
        lastTradeCount = 0;
        
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
                
                if (trading) {
                    lastTrade = new MerchantOfferInfo(offer);
                    lastTradeCount++;
                }
                
                hadSuccessfulTrade = hadSuccessfulTrade || trading;
            }
            
            //Only do one type of trade per cycle
            if (hadSuccessfulTrade) break;
        }
        
        if (hadSuccessfulTrade) {
            tradingDepotBehaviour.combineOutputs();
            villager.playCelebrateSound();
        }
        
        notifyUpdate();
        
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

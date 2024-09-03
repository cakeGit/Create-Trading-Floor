package com.cak.trading_floor.forge.content;

import com.simibubi.create.content.logistics.depot.DepotBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

import java.util.List;

public class TradingDepotBlockEntity extends SmartBlockEntity {
    
    TradingDepotBehaviour tradingDepotBehaviour;
    
    public TradingDepotBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }
    
    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(tradingDepotBehaviour = new TradingDepotBehaviour(this));
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
    
    public void tryTradeWith(Villager villager) {
        if (!tradingDepotBehaviour.itemHandler.behaviour.output.isEmpty()) return;
        
        ItemStack offering = tradingDepotBehaviour.input;
        
        MerchantOffer selectedOffer = villager.getOffers().getRecipeFor(offering, ItemStack.EMPTY, 0);
        if (selectedOffer == null) return;
        
        int ittr = 0;
        while (ittr < 100) {
            ittr ++;
            
            offering = tradingDepotBehaviour.input;
            ItemStack cost = selectedOffer.getBaseCostA();
            
            if (offering.getCount() < cost.getCount()) break;
            
            tradingDepotBehaviour.input  = offering.copyWithCount(offering.getCount() - cost.getCount());
            tradingDepotBehaviour.output.add(selectedOffer.assemble());
        }
        
    }
    
}

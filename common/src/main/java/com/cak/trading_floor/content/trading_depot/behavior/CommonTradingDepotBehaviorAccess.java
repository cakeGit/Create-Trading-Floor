package com.cak.trading_floor.content.trading_depot.behavior;

import com.cak.trading_floor.foundation.advancement.TFAdvancementBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface CommonTradingDepotBehaviorAccess {
    
    TransportedItemStack getOffer();
    
    List<TransportedItemStack> getIncoming();
    
    void setOfferStack(ItemStack stack);
    
    List<ItemStack> getResults();
    
}

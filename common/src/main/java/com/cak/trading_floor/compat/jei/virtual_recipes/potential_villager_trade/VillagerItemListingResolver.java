package com.cak.trading_floor.compat.jei.virtual_recipes.potential_villager_trade;

import com.cak.trading_floor.foundation.access.ResolvableItemListing;
import net.minecraft.world.entity.npc.VillagerTrades;

public class VillagerItemListingResolver {
    
    public static PotentialMerchantOfferInfo tryResolve(VillagerTrades.ItemListing listing) {
        try {
            ResolvableItemListing resolver = (ResolvableItemListing) listing;
            return resolver.create_trading_floor$resolve();
        } catch (ClassCastException e) {
            return null;
        }
    }
    
}

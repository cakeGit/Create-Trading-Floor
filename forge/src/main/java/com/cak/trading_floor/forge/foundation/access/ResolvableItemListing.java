package com.cak.trading_floor.forge.foundation.access;

import com.cak.trading_floor.forge.compat.jei.virtual_recipes.potential_villager_trade.PotentialMerchantOfferInfo;
import org.jetbrains.annotations.Nullable;

public interface ResolvableItemListing {
    @Nullable
    default PotentialMerchantOfferInfo create_trading_floor$resolve() {
        return null;
    }
}

package com.cak.trading_floor.forge.foundation.access;

import com.cak.trading_floor.forge.compat.jei.virtual_recipes.potential_villager_trade.PotentialMerchantOfferInfo;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface ResolvableItemListing {
    @Nullable
    default PotentialMerchantOfferInfo create_trading_floor$resolve() {
        return null;
    }
    @Nullable
    default ItemStack create_trading_floor$copy_with_count(ItemStack source, int count) {
        ItemStack newStack = source.copy();
        newStack.setCount(count);
        return newStack;
    }
}

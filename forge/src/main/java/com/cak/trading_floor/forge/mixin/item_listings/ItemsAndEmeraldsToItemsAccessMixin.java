package com.cak.trading_floor.forge.mixin.item_listings;

import com.cak.trading_floor.compat.jei.virtual_recipes.potential_villager_trade.PotentialMerchantOfferInfo;
import com.cak.trading_floor.foundation.access.ResolvableItemListing;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net.minecraft.world.entity.npc.VillagerTrades$ItemsAndEmeraldsToItems")
public class ItemsAndEmeraldsToItemsAccessMixin implements ResolvableItemListing {
    
    @Shadow @Final private int emeraldCost;
    
    @Shadow @Final private ItemStack fromItem;
    
    @Shadow @Final private ItemStack toItem;
    
    @Shadow @Final private int toCount;
    
    @Shadow @Final private int fromCount;
    
    @Override
    public @Nullable PotentialMerchantOfferInfo create_trading_floor$resolve() {
        return new PotentialMerchantOfferInfo(
            Items.EMERALD.getDefaultInstance().copyWithCount(emeraldCost),
            fromItem.copyWithCount(fromCount),
            toItem.copyWithCount(toCount)
        );
    }
    
}

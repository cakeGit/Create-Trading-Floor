package com.cak.trading_floor.mixin.item_listings;

import com.cak.trading_floor.compat.jei.virtual_recipes.potential_villager_trade.PotentialMerchantOfferInfo;
import com.cak.trading_floor.foundation.access.ResolvableItemListing;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net.minecraft.world.entity.npc.VillagerTrades$EmeraldForItems")
public class EmeraldForItemsAccessMixin implements ResolvableItemListing {
    
    @Shadow @Final private int cost;
    
    @Shadow @Final private Item item;
    
    @Override
    public @Nullable PotentialMerchantOfferInfo create_trading_floor$resolve() {
        ItemStack itemStack = new ItemStack(this.item, this.cost);
        return new PotentialMerchantOfferInfo(itemStack, ItemStack.EMPTY, new ItemStack(Items.EMERALD));
    }
    
}

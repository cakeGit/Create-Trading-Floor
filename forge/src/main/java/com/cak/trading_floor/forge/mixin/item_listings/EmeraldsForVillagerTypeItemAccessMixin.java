package com.cak.trading_floor.forge.mixin.item_listings;

import com.cak.trading_floor.compat.jei.virtual_recipes.potential_villager_trade.PotentialMerchantOfferInfo;
import com.cak.trading_floor.foundation.access.ResolvableItemListing;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(targets = "net.minecraft.world.entity.npc.VillagerTrades$EmeraldsForVillagerTypeItem")
public class EmeraldsForVillagerTypeItemAccessMixin implements ResolvableItemListing {
    
    @Shadow @Final private int cost;
    
    @Shadow @Final private Map<VillagerType, Item> trades;
    
    @Override
    public @Nullable PotentialMerchantOfferInfo create_trading_floor$resolve() {
        return new PotentialMerchantOfferInfo(
            Items.EMERALD.getDefaultInstance().copyWithCount(cost),
            ItemStack.EMPTY,
            trades.values().stream().map(Item::getDefaultInstance).toList()
        ).noteVillagerTypeSpecific();
    }
    
}

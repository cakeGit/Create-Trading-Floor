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

@Mixin(targets = "net.minecraft.world.entity.npc.VillagerTrades$DyedArmorForEmeralds")
public class DyedArmorForEmeraldsAccessMixin implements ResolvableItemListing {
    
    @Shadow @Final private int value;
    
    @Shadow @Final private Item item;
    
    @Override
    public @Nullable PotentialMerchantOfferInfo create_trading_floor$resolve() {
        return new PotentialMerchantOfferInfo(
            Items.EMERALD.getDefaultInstance().copyWithCount(value),
            ItemStack.EMPTY,
            item.getDefaultInstance()
        ).noteRandomisedDyeColor();
    }
    
}

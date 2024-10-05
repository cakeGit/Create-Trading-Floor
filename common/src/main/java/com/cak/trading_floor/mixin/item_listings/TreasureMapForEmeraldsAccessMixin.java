package com.cak.trading_floor.mixin.item_listings;

import com.cak.trading_floor.compat.jei.virtual_recipes.potential_villager_trade.PotentialMerchantOfferInfo;
import com.cak.trading_floor.foundation.access.ResolvableItemListing;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net.minecraft.world.entity.npc.VillagerTrades$TreasureMapForEmeralds")
public class TreasureMapForEmeraldsAccessMixin implements ResolvableItemListing {
    
    @Shadow @Final private int emeraldCost;
    
    @Shadow @Final private MapDecoration.Type destinationType;
    
    @Shadow @Final private String displayName;
    
    @Override
    public @Nullable PotentialMerchantOfferInfo create_trading_floor$resolve() {
        ItemStack mapStack = Items.MAP.getDefaultInstance();
        MapItemSavedData.addTargetDecoration(mapStack, new BlockPos(0, 0, 0), "+", destinationType);
        mapStack.setHoverName(Component.translatable(displayName));
        
        return new PotentialMerchantOfferInfo(
            Items.EMERALD.getDefaultInstance().copyWithCount(emeraldCost),
            Items.COMPASS.getDefaultInstance(),
            mapStack
        );
    }
    
}

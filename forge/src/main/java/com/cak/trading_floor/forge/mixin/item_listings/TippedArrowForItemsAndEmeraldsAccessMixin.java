package com.cak.trading_floor.forge.mixin.item_listings;

import com.cak.trading_floor.forge.compat.jei.virtual_recipes.potential_villager_trade.PotentialMerchantOfferInfo;
import com.cak.trading_floor.forge.foundation.access.ResolvableItemListing;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(targets = "net.minecraft.world.entity.npc.VillagerTrades$TippedArrowForItemsAndEmeralds")
public class TippedArrowForItemsAndEmeraldsAccessMixin implements ResolvableItemListing {
    
    @Shadow @Final private int emeraldCost;
    
    @Shadow @Final private Item fromItem;
    
    @Shadow @Final private int fromCount;
    
    @Shadow @Final private ItemStack toItem;
    
    @Shadow @Final private int toCount;
    
    @Override
    public @Nullable PotentialMerchantOfferInfo create_trading_floor$resolve() {
        List<Potion> list = ForgeRegistries.POTIONS.getValues().stream()
            .filter((potion) -> !potion.getEffects().isEmpty() && PotionBrewing.isBrewablePotion(potion))
            .toList();
        
        ItemStack toItemBase = toItem.copy();
        toItemBase.setCount(toCount);
        
        return new PotentialMerchantOfferInfo(
            create_trading_floor$copy_with_count(Items.EMERALD.getDefaultInstance(), emeraldCost),
            create_trading_floor$copy_with_count(fromItem.getDefaultInstance(), fromCount),
            list.stream().map(potion -> PotionUtils.setPotion(toItemBase.copy(), potion)).toList()
        );
    }
    
}

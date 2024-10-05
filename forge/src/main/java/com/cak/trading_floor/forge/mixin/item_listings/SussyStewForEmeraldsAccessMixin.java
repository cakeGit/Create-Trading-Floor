package com.cak.trading_floor.forge.mixin.item_listings;

import com.cak.trading_floor.compat.jei.virtual_recipes.potential_villager_trade.PotentialMerchantOfferInfo;
import com.cak.trading_floor.foundation.access.ResolvableItemListing;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SuspiciousStewItem;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net.minecraft.world.entity.npc.VillagerTrades$SuspiciousStewForEmerald")
public class SussyStewForEmeraldsAccessMixin implements ResolvableItemListing {
    
    @Shadow @Final private MobEffect effect;
    
    @Shadow @Final private int duration;
    
    @Override
    public @Nullable PotentialMerchantOfferInfo create_trading_floor$resolve() {
        ItemStack itemStack = new ItemStack(Items.SUSPICIOUS_STEW, 1);
        SuspiciousStewItem.saveMobEffect(itemStack, effect, duration);
        return new PotentialMerchantOfferInfo(
            Items.EMERALD.getDefaultInstance(),
            ItemStack.EMPTY,
            itemStack
        );
    }
    
}

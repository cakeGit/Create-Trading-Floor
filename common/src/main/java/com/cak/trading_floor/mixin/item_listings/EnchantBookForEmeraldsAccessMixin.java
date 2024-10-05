package com.cak.trading_floor.mixin.item_listings;

import com.cak.trading_floor.compat.jei.virtual_recipes.potential_villager_trade.PotentialMerchantOfferInfo;
import com.cak.trading_floor.foundation.access.ResolvableItemListing;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.List;

@Mixin(targets = "net.minecraft.world.entity.npc.VillagerTrades$EnchantBookForEmeralds")
public class EnchantBookForEmeraldsAccessMixin implements ResolvableItemListing {
    
    @Override
    public @Nullable PotentialMerchantOfferInfo create_trading_floor$resolve() {
        List<Enchantment> enchantments = BuiltInRegistries.ENCHANTMENT.stream().filter(Enchantment::isTradeable).toList();
        
        List<ItemStack> booksList = new ArrayList<>();
        
        for (Enchantment enchantment : enchantments) {
            for (int i = enchantment.getMinLevel(); i <= enchantment.getMaxLevel(); i++)
                booksList.add(
                    EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, i))
                );
        }
        
        return new PotentialMerchantOfferInfo(
            Items.EMERALD.getDefaultInstance(),
            ItemStack.EMPTY,
            booksList
        ).noteRandomisedEmeraldCost();
    }
    
}

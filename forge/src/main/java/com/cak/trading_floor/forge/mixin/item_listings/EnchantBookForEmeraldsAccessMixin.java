package com.cak.trading_floor.forge.mixin.item_listings;

import com.cak.trading_floor.forge.compat.jei.virtual_recipes.potential_villager_trade.PotentialMerchantOfferInfo;
import com.cak.trading_floor.forge.foundation.access.ResolvableItemListing;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(targets = "net.minecraft.world.entity.npc.VillagerTrades$EnchantBookForEmeralds")
public class EnchantBookForEmeraldsAccessMixin implements ResolvableItemListing {
    
    @Override
    public @Nullable PotentialMerchantOfferInfo create_trading_floor$resolve() {
        List<Enchantment> enchantments = ForgeRegistries.ENCHANTMENTS.getValues().stream().filter(Enchantment::isTradeable).toList();
        
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

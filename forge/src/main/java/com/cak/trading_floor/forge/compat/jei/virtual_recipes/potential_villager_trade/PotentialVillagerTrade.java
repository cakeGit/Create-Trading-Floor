package com.cak.trading_floor.forge.compat.jei.virtual_recipes.potential_villager_trade;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PotentialVillagerTrade implements Recipe<RecipeWrapper> {
    
    public static final List<PotentialVillagerTrade> RECIPES = buildPotentialTrades();

    private static List<PotentialVillagerTrade> buildPotentialTrades() {
        ArrayList<PotentialVillagerTrade> trades = new ArrayList<>();
        for (Map.Entry<VillagerProfession, Int2ObjectMap<VillagerTrades.ItemListing[]>> professionOffers : VillagerTrades.TRADES.entrySet()) {
            for (Int2ObjectMap.Entry<VillagerTrades.ItemListing[]> levelOffers : professionOffers.getValue().int2ObjectEntrySet()) {
                int index = 0;
                for (VillagerTrades.ItemListing listing : levelOffers.getValue()) {
                    try {
                        MerchantOffer offer = listing.getOffer(null, null);
                        
                        if (offer != null) {
                            trades.add(new PotentialVillagerTrade(
                                new ResourceLocation("trade_" + professionOffers.getKey().name() + "_level_" + levelOffers.getIntKey() + "_" + index),
                                levelOffers.getIntKey(),
                                professionOffers.getKey(),
                                offer.getBaseCostA(),
                                offer.getCostB(),
                                offer.getResult()
                                ));
                        }
                        index++;
                    } catch (NullPointerException ignored) {}
                }
            }
        }
        return trades;
    }
    
    ResourceLocation id;
    int villagerLevel;
    VillagerProfession profession;
    ItemStack costA;
    ItemStack costB;
    ItemStack result;
    
    public PotentialVillagerTrade(ResourceLocation id, int villagerLevel, VillagerProfession profession, ItemStack costA, ItemStack costB, ItemStack result) {
        this.id = id;
        this.villagerLevel = villagerLevel;
        this.profession = profession;
        this.costA = costA;
        this.costB = costB;
        this.result = result;
    }
    
    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return result;
    }
    
    @Override
    public ResourceLocation getId() {
        return id;
    }
    
    @Override
    public RecipeSerializer<?> getSerializer() {
//        return TFRecipeTypes.POTENTIAL_VILLAGER_TRADE.getSerializer();
        throw new UnsupportedOperationException();
    }
    
    @Override
    public RecipeType<?> getType() {
//        return TFRecipeTypes.POTENTIAL_VILLAGER_TRADE.getType();
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean matches(RecipeWrapper container, Level level) {
        return false;
    }
    
    @Override
    public ItemStack assemble(RecipeWrapper container, RegistryAccess registryAccess) {
        return null;
    }
    
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }
    
}

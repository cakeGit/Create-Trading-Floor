package com.cak.trading_floor.compat.jei.virtual_recipes.potential_villager_trade;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public class PotentialMerchantOfferInfo {
    
    ItemStack costA, costB;
    
    List<ItemStack> possibleResults;
    
    boolean implyEnchantedVariants = false;
    boolean noteVillagerTypeSpecific = false;
    boolean noteRandomisedEmeraldCost = false;
    boolean noteRandomisedDyeColor = false;
    
    public PotentialMerchantOfferInfo(ItemStack costA, ItemStack costB, ItemStack possibleResults) {
        this(costA, costB, List.of(possibleResults));
    }
    
    public PotentialMerchantOfferInfo(ItemStack costA, ItemStack costB, List<ItemStack> possibleResults) {
        this.costA = costA;
        this.costB = costB;
        this.possibleResults = possibleResults;
    }
    
    public PotentialMerchantOfferInfo withImpliedEnchantVariants() {
        this.implyEnchantedVariants = true;
        return this;
    }
    
    public PotentialMerchantOfferInfo noteVillagerTypeSpecific() {
        this.noteVillagerTypeSpecific = true;
        return this;
    }
    
    public PotentialMerchantOfferInfo noteRandomisedEmeraldCost() {
        this.noteRandomisedEmeraldCost = true;
        return this;
    }
    
    public PotentialMerchantOfferInfo noteRandomisedDyeColor() {
        this.noteRandomisedDyeColor = true;
        return this;
    }
    
    public ItemStack getCostA() {
        return costA;
    }
    
    public ItemStack getCostB() {
        return costB;
    }
    
    public List<ItemStack> getPossibleResults() {
        return possibleResults;
    }
    
    public boolean isImplyEnchantedVariants() {
        return implyEnchantedVariants;
    }
    
    public boolean isNoteVillagerTypeSpecific() {
        return noteVillagerTypeSpecific;
    }
    
    public boolean isNoteRandomisedEmeraldCost() {
        return noteRandomisedEmeraldCost;
    }
    
    public boolean isNoteRandomisedDyeColor() {
        return noteRandomisedDyeColor;
    }
    
}

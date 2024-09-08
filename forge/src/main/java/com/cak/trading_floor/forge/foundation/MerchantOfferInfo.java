package com.cak.trading_floor.forge.foundation;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;

import java.util.Objects;

public class MerchantOfferInfo {
    
    final ItemStack costA;
    final ItemStack costB;
    final ItemStack result;
    
    public MerchantOfferInfo(MerchantOffer offer) {
        this.costA = offer.getBaseCostA();
        this.costB = offer.getCostB();
        this.result = offer.getResult();
    }
    
    protected MerchantOfferInfo(CompoundTag tag) {
        this.costA = ItemStack.of(tag.getCompound("CostA"));
        this.costB = ItemStack.of(tag.getCompound("CostB"));
        this.result = ItemStack.of(tag.getCompound("Result"));
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MerchantOfferInfo that)) return false;
        return ItemStack.isSameItemSameTags(costA, that.costA) && ItemStack.isSameItemSameTags(costB, that.costB) && ItemStack.isSameItemSameTags(result, that.result);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(costA, costB, result);
    }
    
    public static MerchantOfferInfo read(CompoundTag tag) {
        return new MerchantOfferInfo(tag);
    }
    
    public Tag write(CompoundTag tag) {
        tag.put("CostA", costA.serializeNBT());
        tag.put("CostB", costB.serializeNBT());
        tag.put("Result", result.serializeNBT());
        return tag;
    }
    
    public ItemStack getCostA() {
        return costA;
    }
    
    public ItemStack getCostB() {
        return costB;
    }
    
    public ItemStack getResult() {
        return result;
    }
    
}

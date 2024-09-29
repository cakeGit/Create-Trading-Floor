package com.cak.trading_floor.forge.content.depot;

import net.minecraft.world.item.ItemStack;

public class ItemCopyWithCount {
    
    public static ItemStack of(ItemStack stack, int count) {
        ItemStack newStack = stack.copy();
        newStack.setCount(count);
        return newStack;
    }
    
}

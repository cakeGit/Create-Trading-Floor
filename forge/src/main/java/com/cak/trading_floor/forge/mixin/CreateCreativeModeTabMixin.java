package com.cak.trading_floor.forge.mixin;

import com.cak.trading_floor.forge.registry.TFTabInsertions;
import com.simibubi.create.infrastructure.item.CreateCreativeModeTab;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CreateCreativeModeTab.class, remap = false)
public class CreateCreativeModeTabMixin {
    
    @Inject(method = "addBlocks", at = @At(value = "TAIL"))
    public void fillItemCategoryAdditional_Blocks(NonNullList<ItemStack> items, CallbackInfo ci) {
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i).getItem();
            if (
                TFTabInsertions.getAllInsertsAfter()
                    .containsKey(item)
            ) {
                items.add(i+1, TFTabInsertions.getAllInsertsAfter()
                    .get(item).getDefaultInstance());
                i++;
            }
        }
    }
    
    
}


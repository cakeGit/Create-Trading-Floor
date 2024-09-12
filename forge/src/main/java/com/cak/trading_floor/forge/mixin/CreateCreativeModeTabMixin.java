package com.cak.trading_floor.forge.mixin;

import com.cak.trading_floor.forge.registry.TFTabInsertions;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Function;

@Mixin(remap = false, targets = "com.simibubi.create.AllCreativeModeTabs$RegistrateDisplayItemsGenerator")
public class CreateCreativeModeTabMixin {
    
    @Inject(method = "outputAll", at = @At(value = "HEAD"))
    private static void outputAll(CreativeModeTab.Output output, List<Item> items, Function<Item, ItemStack> stackFunc, Function<Item, ?> visibilityFunc, CallbackInfo ci) {
        for (int i = 0; i < items.size(); i++) {
            Item itemToAdd = items.get(i);
            
            if (
                TFTabInsertions.getAllInsertsAfter()
                    .containsKey(itemToAdd)
            ) {
                items.add(
                    i + 1,
                    TFTabInsertions.getAllInsertsAfter()
                        .get(itemToAdd)
                );
                i++;
            }
        }
    }
    
}


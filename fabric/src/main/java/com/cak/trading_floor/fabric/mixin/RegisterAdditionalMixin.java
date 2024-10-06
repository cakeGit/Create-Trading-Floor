package com.cak.trading_floor.fabric.mixin;

import com.cak.trading_floor.foundation.advancement.TFAdvancements;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AllAdvancements.class, remap = false)
public class RegisterAdditionalMixin {
    
    @Inject(method = "register", at = @At("TAIL"))
    private static void register(CallbackInfo ci) {
        TFAdvancements.register();
    }
    
}

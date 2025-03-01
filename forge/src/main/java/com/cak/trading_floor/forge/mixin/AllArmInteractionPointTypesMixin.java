package com.cak.trading_floor.forge.mixin;

import com.cak.trading_floor.registry.TFArmInteractionPointTypes;
import com.simibubi.create.content.kinetics.mechanicalArm.AllArmInteractionPointTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AllArmInteractionPointTypes.class, remap = false)
public class AllArmInteractionPointTypesMixin {

    @Inject(method = "init", at = @At("HEAD"))
    private static void init(CallbackInfo ci) {
        TFArmInteractionPointTypes.addToRegister();
    }

}

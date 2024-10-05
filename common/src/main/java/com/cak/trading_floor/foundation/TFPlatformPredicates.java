package com.cak.trading_floor.foundation;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.entity.LivingEntity;

public class TFPlatformPredicates {
    
    @ExpectPlatform
    public static boolean isFakePlayer(LivingEntity player) {
        throw new AssertionError();
    }
    
}

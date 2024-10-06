package com.cak.trading_floor.foundation.fabric;

import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.world.entity.LivingEntity;

public class TFPlatformPredicatesImpl {
    
    public static boolean isFakePlayer(LivingEntity player) {
        return !(player instanceof FakePlayer);
    }
    
}

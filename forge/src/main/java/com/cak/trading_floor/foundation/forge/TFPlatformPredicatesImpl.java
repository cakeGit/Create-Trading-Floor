package com.cak.trading_floor.foundation.forge;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.FakePlayer;

public class TFPlatformPredicatesImpl {
    
    public static boolean isFakePlayer(LivingEntity player) {
        return !(player instanceof FakePlayer);
    }
    
}

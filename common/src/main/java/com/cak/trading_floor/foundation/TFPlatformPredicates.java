package com.cak.trading_floor.foundation;

import net.minecraft.world.entity.LivingEntity;

public class TFPlatformPredicates {

    public static TFPlatformPredicatesImplementor PLATFORM;

    public static boolean isFakePlayer(LivingEntity player) {
        return PLATFORM.isFakePlayer(player);
    }

    public interface TFPlatformPredicatesImplementor {
        boolean isFakePlayer(LivingEntity player);
    }
}

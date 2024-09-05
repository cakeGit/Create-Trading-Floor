package com.cak.trading_floor;

import com.simibubi.create.Create;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TradingFloor {
    
    public static final String MOD_ID = "trading_floor";
    public static final String NAME = "Create: Trading Floor";
    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    public static void init() {
        LOGGER.info("{} initializing! Create version: {} on platform: {}", NAME, Create.VERSION, TFExpectPlatform.platformName());
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
    
}

package com.cak.trading_floor;

import com.cak.trading_floor.foundation.advancement.TFAdvancements;
import com.cak.trading_floor.registry.*;
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
        TFRegistry.init();
        TFArmInteractionPointTypes.register();
        TFParticleEmitters.register();
        
        TFLangEntries.addEntries();
    }
    
    public static void clientInit() {
        TFPonderTags.register();
        TFPonderIndex.register();
    }
    
    public static ResourceLocation asResource(String id) {
        return new ResourceLocation(MOD_ID, id);
    }
    
}

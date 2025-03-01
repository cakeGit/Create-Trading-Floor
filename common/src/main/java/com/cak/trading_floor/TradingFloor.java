package com.cak.trading_floor;

import com.cak.trading_floor.foundation.ponder_scenes.TFPonderPlugin;
import com.cak.trading_floor.registry.TFDisplaySources;
import com.cak.trading_floor.registry.TFLangEntries;
import com.cak.trading_floor.registry.TFParticleEmitters;
import com.cak.trading_floor.registry.TFRegistry;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TradingFloor {
    
    public static final String MOD_ID = "trading_floor";
    public static final String NAME = "Create: Trading Floor";
    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    public static String PLATFORM = "Unknown";

    public static void init() {
        LOGGER.info("{} initializing! Platform: {}", NAME, PLATFORM);
        TFRegistry.init();
        TFParticleEmitters.register();
        
        TFLangEntries.addEntries();
        TFDisplaySources.register();
    }
    
    public static void clientInit() {
        PonderIndex.addPlugin(new TFPonderPlugin());
    }
    
    public static ResourceLocation asResource(String id) {
        return new ResourceLocation(MOD_ID, id);
    }
    
}

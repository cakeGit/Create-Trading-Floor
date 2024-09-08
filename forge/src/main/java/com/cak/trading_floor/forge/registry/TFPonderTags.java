package com.cak.trading_floor.forge.registry;

import com.cak.trading_floor.TradingFloor;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.ponder.PonderRegistry;
import com.simibubi.create.foundation.ponder.PonderTag;

public class TFPonderTags {
    
    public static final PonderTag
        ALL_TRADING_FLOOR_PONDERS = create("base").item(AllBlocks.COGWHEEL.get())
        .defaultLang("Create: Trading Floor", "Special trading depot to automatically trade with villagers")
        .addToIndex();
    
    private static PonderTag create(String id) {
        return new PonderTag(TradingFloor.asResource(id));
    }
    
    public static void register() {
        // Add items to tags here
        
        PonderRegistry.TAGS.forTag(ALL_TRADING_FLOOR_PONDERS)
            .add(TFRegistry.TRADING_DEPOT);
        
    }
    
}

package com.cak.trading_floor.forge.registry;

import com.cak.trading_floor.TradingFloor;
import com.cak.trading_floor.forge.foundation.ponder_scenes.TradingDepotScenes;
import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;

/**
 * Client only
 */
public class TFPonderIndex {
    
    static final PonderRegistrationHelper HELPER = new PonderRegistrationHelper(TradingFloor.MOD_ID);
    
    public static void register() {
        HELPER.forComponents(TFRegistry.TRADING_DEPOT)
            .addStoryBoard("trading_depot_trading", TradingDepotScenes::trading);
    }
    
}

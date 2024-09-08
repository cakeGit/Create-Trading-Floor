package com.cak.trading_floor.fabric;

import com.cak.trading_floor.TradingFloor;
import net.fabricmc.api.ModInitializer;

/**
 * This platform is non-functioning, in future this might be made to work
 * */
public class TradingFloorFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        TradingFloor.init();
    }
    
}

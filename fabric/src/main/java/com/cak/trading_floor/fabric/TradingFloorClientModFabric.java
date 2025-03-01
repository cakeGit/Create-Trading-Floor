package com.cak.trading_floor.fabric;

import com.cak.trading_floor.TradingFloor;
import com.cak.trading_floor.fabric.network.TFPackets;
import net.fabricmc.api.ClientModInitializer;

public class TradingFloorClientModFabric implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        TradingFloor.clientInit();
        TFPackets.getChannel().initClientListener();
    }
    
}

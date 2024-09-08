package com.cak.trading_floor.forge.registry;

import com.cak.trading_floor.TradingFloor;
import com.cak.trading_floor.forge.foundation.advancement.TFAdvancements;

import java.util.Map;

public class TFLangEntries {
    
    public static void addEntries() {
        addIdLangEntries(Map.of(
            "tooltip.trading_depot.trading_depot_info", "Trading Depot Info:",
            "tooltip.trading_depot.contents", "Trading Depot Contents:",
            "tooltip.trading_depot.contents.input", "Input (This depot only):",
            "tooltip.trading_depot.contents.output", "Output:",
            "tooltip.trading_depot.last_trade", "Last Trade:",
            "tooltip.trading_depot.connected_to_other", "Connected To",
            "tooltip.trading_depot.other_trading_depot", "other Trading Depot",
            "tooltip.trading_depot.other_trading_depots", "other Trading Depots",
            
            "display_link.trading_depot.no_trade", "No Trade",
            "display_link.trading_depot.trades_completed", "Trades completed:"
        ));
    }

    public static void addIdLangEntries(Map<String, String> entries) {
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            TFRegistry.REGISTRATE.addRawLang(TradingFloor.MOD_ID + "." + entry.getKey(), entry.getValue());
        }
    }
    
}

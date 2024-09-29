package com.cak.trading_floor.forge;

import com.cak.trading_floor.forge.foundation.advancement.TFAdvancements;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;

public class TradingFloorData {
    
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        generator.addProvider(true, new TFAdvancements(generator));
    }
    
}

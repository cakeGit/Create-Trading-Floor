package com.cak.trading_floor.forge;

import com.cak.trading_floor.TradingFloor;
import com.cak.trading_floor.forge.registry.TFArmInteractionPointTypes;
import com.cak.trading_floor.forge.registry.TFLangEntries;
import com.cak.trading_floor.forge.registry.TFRegistry;
import com.cak.trading_floor.forge.foundation.advancement.TFAdvancements;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(TradingFloor.MOD_ID)
public class TradingFloorForge {
    
    public TradingFloorForge() {
        // registrate must be given the mod event bus on forge before registration
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        TFRegistry.REGISTRATE.registerEventListeners(eventBus);
        
        TradingFloor.init();
        TFRegistry.init();
        TFArmInteractionPointTypes.register();
        TFLangEntries.addEntries();
        
        eventBus.addListener(TradingFloorData::gatherData);
        eventBus.addListener(TradingFloorForge::init);
        
        TradingFloor.LOGGER.info("Finished Initialisation For Mod: " + TradingFloor.MOD_ID);
    }
    
    
    public static void init(final FMLCommonSetupEvent event) {
        event.enqueueWork(TFAdvancements::register);
    }
    
}

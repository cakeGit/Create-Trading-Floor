package com.cak.trading_floor.fabric;

import com.cak.trading_floor.TradingFloor;
import com.cak.trading_floor.fabric.network.TFPackets;
import net.fabricmc.api.ModInitializer;

public class TradingFloorFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        TradingFloor.init();
        TFPackets.register();
        TradingFloor.commonEnqueuedInit();
        TradingFloor.LOGGER.info("Finished Initialisation For Mod: " + TradingFloor.MOD_ID);
    }
    
}
//
//public TradingFloorForge() {
//    // registrate must be given the mod event bus on forge before registration
//    IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
//
//    TFRegistry.REGISTRATE.registerEventListeners(eventBus);
//    TFRegistry.REGISTRATE.addDataGenerator(ProviderType.LANG, TradingFloorForge::addPostInitLang);
//
//    TradingFloor.init();
//    TFPackets.register();
//
//    eventBus.addListener(TradingFloorData::gatherData);
//    eventBus.addListener(TradingFloorForge::init);
//    eventBus.addListener(TradingFloorForge::clientInit);
//
//    TradingFloor.LOGGER.info("Finished Initialisation For Mod: " + TradingFloor.MOD_ID);
//}
//
//private static void addPostInitLang(RegistrateLangProvider registrateLangProvider) {
//    TFPonderTags.register();
//    TFPonderIndex.register();
//
//    SharedText.gatherText();
//    PonderLocalization.generateSceneLang();
//
//    PonderLocalization.provideLang(TradingFloor.MOD_ID, registrateLangProvider::add);
//
//    TFAdvancements.provideLang(registrateLangProvider::add);
//}
//
//public static void init(final FMLCommonSetupEvent event) {
//    event.enqueueWork(TradingFloor::commonEnqueuedInit);
//}
//
//public static void clientInit(final FMLClientSetupEvent event) {
//    TradingFloor.clientInit();
//}
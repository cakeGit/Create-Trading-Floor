package com.cak.trading_floor.forge;

import com.cak.trading_floor.TradingFloor;
import com.cak.trading_floor.foundation.advancement.TFAdvancements;
import com.cak.trading_floor.forge.network.TFPackets;
import com.cak.trading_floor.foundation.forge.TFPlatformPacketsImpl;
import com.cak.trading_floor.foundation.forge.TFPlatformPredicatesImpl;
import com.cak.trading_floor.foundation.ponder_scenes.TFPonderPlugin;
import com.cak.trading_floor.registry.*;
import com.cak.trading_floor.registry.forge.TFPlatformRegistryImpl;
import com.simibubi.create.AllKeys;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.ponder.CreatePonderPlugin;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import net.createmod.ponder.api.registration.SharedTextRegistrationHelper;
import net.createmod.ponder.foundation.PonderIndex;
import net.createmod.ponder.foundation.registration.PonderLocalization;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.function.BiConsumer;

@Mod(TradingFloor.MOD_ID)
@SuppressWarnings("unused")
public class TradingFloorForge {

    public TradingFloorForge() {
        // registrate must be given the mod event bus on forge before registration
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        TFPlatformPredicatesImpl.register();
        TFPlatformPacketsImpl.register();
        TFPlatformRegistryImpl.register();

        TFRegistry.REGISTRATE.registerEventListeners(eventBus);
        TFRegistry.REGISTRATE.addDataGenerator(ProviderType.LANG, TradingFloorForge::addPostInitLang);

        TradingFloor.PLATFORM = "Forge";
        TradingFloor.init();
        TFPackets.register();

        eventBus.addListener(TradingFloorData::gatherData);
        eventBus.addListener(TradingFloorForge::clientInit);
        eventBus.addListener(TradingFloorForge::commonInit);

        TradingFloor.LOGGER.info("Finished Initialisation For Mod: " + TradingFloor.MOD_ID);
    }

    private static void addPostInitLang(RegistrateLangProvider registrateLangProvider) {

        // Register this since FMLClientSetupEvent does not run during datagen
        PonderIndex.addPlugin(new TFPonderPlugin());
        PonderIndex.getLangAccess().provideLang(TradingFloor.MOD_ID, registrateLangProvider::add);

        TFAdvancements.provideLang(registrateLangProvider::add);

    }

    public static void commonInit(final FMLCommonSetupEvent event) {
        event.enqueueWork(TFAdvancements::register);
    }

    public static void clientInit(final FMLClientSetupEvent event) {
        TradingFloor.clientInit();
    }

}

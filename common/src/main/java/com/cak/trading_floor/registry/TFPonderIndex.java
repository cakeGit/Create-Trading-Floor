package com.cak.trading_floor.registry;

import com.cak.trading_floor.foundation.ponder_scenes.TradingDepotScenes;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

/**
 * Client only
 */
public class TFPonderIndex {

    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderSceneRegistrationHelper<ItemProviderEntry<?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

        HELPER.forComponents(TFRegistry.TRADING_DEPOT)
            .addStoryBoard("trading_depot_trading", TradingDepotScenes::trading, TFPonderTags.ALL_TRADING_FLOOR_PONDERS)
            .addStoryBoard("trading_depot_double_trading", TradingDepotScenes::trading_double, TFPonderTags.ALL_TRADING_FLOOR_PONDERS);

    }
    
}

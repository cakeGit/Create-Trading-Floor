package com.cak.trading_floor.foundation.ponder_scenes;

import com.cak.trading_floor.TradingFloor;
import com.cak.trading_floor.registry.TFPonderIndex;
import com.cak.trading_floor.registry.TFPonderTags;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class TFPonderPlugin implements PonderPlugin {
    @Override
    public String getModId() {
        return TradingFloor.MOD_ID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        TFPonderIndex.register(helper);
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        TFPonderTags.register(helper);
    }

}

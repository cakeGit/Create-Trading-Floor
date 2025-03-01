package com.cak.trading_floor.registry;

import com.simibubi.create.AllBlocks;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.catnip.platform.CatnipServices;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;

public class TFPonderTags {

    public static final ResourceLocation ALL_TRADING_FLOOR_PONDERS = new ResourceLocation("base");

    public static void register(PonderTagRegistrationHelper<ResourceLocation> helper) {
        PonderTagRegistrationHelper<RegistryEntry<?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);
        PonderTagRegistrationHelper<ItemLike> itemHelper = helper.withKeyFunction(
            CatnipServices.REGISTRIES::getKeyOrThrow);

        helper.registerTag(ALL_TRADING_FLOOR_PONDERS)
            .addToIndex()
            .item(AllBlocks.COGWHEEL.get(), true, false)
            .title("Create: Trading Floor")
            .description("Special trading depot to automatically trade with villagers")
            .register();


    }
    
}

package com.cak.trading_floor.registry;

import com.cak.trading_floor.content.trading_depot.CommonTradingDepotBlock;
import com.cak.trading_floor.content.trading_depot.CommonTradingDepotBlockEntity;
import com.tterrag.registrate.builders.BlockEntityBuilder;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class TFPlatformRegistry {

    public static TFPlatformRegistryImplementor PLATFORM;

    public static NonNullFunction<BlockBehaviour.Properties, CommonTradingDepotBlock> getTradingDepotBlock() {
        return PLATFORM.getTradingDepotBlock();
    }

    public static BlockEntityBuilder.BlockEntityFactory<CommonTradingDepotBlockEntity> getTradingDepotBlockEntity() {
        return PLATFORM.getTradingDepotBlockEntity();
    }

    public interface TFPlatformRegistryImplementor {
        NonNullFunction<BlockBehaviour.Properties, CommonTradingDepotBlock> getTradingDepotBlock();
        BlockEntityBuilder.BlockEntityFactory<CommonTradingDepotBlockEntity> getTradingDepotBlockEntity();
    }
}

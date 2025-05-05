package com.cak.trading_floor.registry.fabric;

import com.cak.trading_floor.content.trading_depot.CommonTradingDepotBlock;
import com.cak.trading_floor.content.trading_depot.CommonTradingDepotBlockEntity;
import com.cak.trading_floor.fabric.content.depot.TradingDepotBlock;
import com.cak.trading_floor.fabric.content.depot.TradingDepotBlockEntity;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.BlockEntityBuilder;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class TFPlatformRegistryImpl {
    
    public static NonNullFunction<BlockBehaviour.Properties, CommonTradingDepotBlock> getTradingDepotBlock() {
        return TradingDepotBlock::new;
    }
    
    public static BlockEntityBuilder.BlockEntityFactory<CommonTradingDepotBlockEntity> getTradingDepotBlockEntity() {
        return TradingDepotBlockEntity::new;
    }

    public static <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, BlockBuilder<T, P>> transformBlockState() {
        return b -> b;
    }

}

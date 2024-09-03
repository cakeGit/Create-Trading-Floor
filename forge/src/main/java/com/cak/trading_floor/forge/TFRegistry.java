package com.cak.trading_floor.forge;

import com.cak.trading_floor.TradingFloor;
import com.cak.trading_floor.forge.content.TradingDepotBlock;
import com.cak.trading_floor.forge.content.TradingDepotBlockEntity;
import com.cak.trading_floor.forge.content.TradingDepotBlockEntityRenderer;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.List;

public class TFRegistry {
	public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(TradingFloor.MOD_ID);

	public static final BlockEntry<TradingDepotBlock> TRADING_DEPOT = REGISTRATE
		.block("trading_depot", TradingDepotBlock::new)
		.properties(BlockBehaviour.Properties::noOcclusion)
		.blockstate((ctx, prov) -> prov.horizontalBlock(ctx.getEntry(), AssetLookup.standardModel(ctx, prov)))
		.simpleItem()
		.register();
	
	public static final BlockEntityEntry<TradingDepotBlockEntity> TRADING_DEPOT_BLOCK_ENTITY = REGISTRATE
		.blockEntity("trading_depot", TradingDepotBlockEntity::new)
		.validBlocksDeferred(() -> List.of(TRADING_DEPOT))
		.renderer(() -> TradingDepotBlockEntityRenderer::new)
		.register();
	
	
	public static void init() {
		TradingFloor.LOGGER.info("Registering blocks for " + TradingFloor.NAME);
	}
}

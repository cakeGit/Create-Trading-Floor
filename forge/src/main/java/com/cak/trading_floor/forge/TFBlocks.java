package com.cak.trading_floor.forge;

import com.cak.trading_floor.TradingFloor;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.level.block.Block;

public class TFBlocks {
	public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(TradingFloor.MOD_ID);

	public static final BlockEntry<Block> TRADING_DEPOT = REGISTRATE
		.block("trading_depot", Block::new)
		.simpleItem()
		.register();

	public static void init() {
		TradingFloor.LOGGER.info("Registering blocks for " + TradingFloor.NAME);
	}
}

package com.cak.trading_floor.registry;

import com.cak.trading_floor.TradingFloor;
import com.cak.trading_floor.content.trading_depot.CommonTradingDepotBlock;
import com.cak.trading_floor.content.trading_depot.CommonTradingDepotBlockEntity;
import com.cak.trading_floor.content.trading_depot.TradingDepotRenderer;
import com.cak.trading_floor.content.trading_depot.displays.CurrentTradeCompletedCountDisplay;
import com.cak.trading_floor.content.trading_depot.displays.TradeProductSumDisplay;
import com.simibubi.create.content.redstone.displayLink.AllDisplayBehaviours;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.simibubi.create.foundation.utility.FontHelper;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.List;

public class TFRegistry {
    
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(TradingFloor.MOD_ID);
    static {
        REGISTRATE.setTooltipModifierFactory(item -> {
            return new ItemDescription.Modifier(item, TooltipHelper.Palette.STANDARD_CREATE)
                .andThen(TooltipModifier.mapNull(KineticStats.create(item)));
        });
    }
    
    public static final BlockEntry<CommonTradingDepotBlock> TRADING_DEPOT = REGISTRATE
        .block("trading_depot", TFPlatformRegistry.getTradingDepotBlock())
        .properties(BlockBehaviour.Properties::noOcclusion)
        .onRegister(AllDisplayBehaviours.assignDataBehaviour(new TradeProductSumDisplay(), "trade_product_sum"))
        .onRegister(AllDisplayBehaviours.assignDataBehaviour(new CurrentTradeCompletedCountDisplay(), "trade_completed_count"))
        .transform(TFPlatformRegistry.transformBlockState())
        .simpleItem()
        .register();
    
    public static final BlockEntityEntry<CommonTradingDepotBlockEntity> TRADING_DEPOT_BLOCK_ENTITY = REGISTRATE
        .blockEntity("trading_depot", TFPlatformRegistry.getTradingDepotBlockEntity())
        .validBlocksDeferred(() -> List.of(TRADING_DEPOT))
        .renderer(() -> TradingDepotRenderer::new)
        .register();
    
    public static void init() {
        TradingFloor.LOGGER.info("Registering all " + TradingFloor.NAME + " entries");
    }
    
}

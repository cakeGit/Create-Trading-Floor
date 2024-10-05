package com.cak.trading_floor.content.trading_depot.displays;

import com.cak.trading_floor.content.trading_depot.CommonTradingDepotBlockEntity;
import com.cak.trading_floor.foundation.TFLang;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.DisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class TradeProductSumDisplay extends DisplaySource {
    
    @Override
    public List<MutableComponent> provideText(DisplayLinkContext context, DisplayTargetStats stats) {
        if (!(context.getSourceBlockEntity() instanceof CommonTradingDepotBlockEntity depot))
            return List.of();
        
        if (depot.getLastTrade() == null)
            return List.of(TFLang.translate("display_link.trading_depot.no_trade").component());
        
        ItemStack stack = depot.getLastTrade().getResult().copyWithCount(depot.getTradeOutputSum());
        
        return List.of(TFLang.itemStack(stack).component());
    }
    
}

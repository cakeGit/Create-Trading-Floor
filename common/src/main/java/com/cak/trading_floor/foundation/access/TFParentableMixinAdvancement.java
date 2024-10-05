package com.cak.trading_floor.foundation.access;

import net.minecraft.advancements.Advancement;

public interface TFParentableMixinAdvancement extends TFParentableAdvancement {
    
    default Advancement getDatagenResult() {
        return create_trading_floor$getMixinDatagenResult();
    }
    
    Advancement create_trading_floor$getMixinDatagenResult();
    
}

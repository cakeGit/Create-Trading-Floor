package com.cak.trading_floor.content.trading_depot;

import com.cak.trading_floor.content.trading_depot.behavior.CommonTradingDepotBehaviorAccess;
import com.cak.trading_floor.foundation.MerchantOfferInfo;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public abstract class CommonTradingDepotBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {
    
    public CommonTradingDepotBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    
    public abstract CommonTradingDepotBehaviorAccess getCommonTradingDepotBehaviour();
    
    public abstract int getCurrentTradeCompletedCount();
    
    public abstract MerchantOfferInfo getLastTrade();
    
    public abstract int getTradeOutputSum();
    
    public abstract boolean hasInputStack();
    
    public abstract void tryTradeWith(Villager villager, List<CommonTradingDepotBehaviorAccess> tradingDepotBehaviours);
    
}

package com.cak.trading_floor.foundation;

import com.cak.trading_floor.content.trading_depot.CommonTradingDepotBlock;
import com.cak.trading_floor.registry.TFRegistry;
import com.simibubi.create.foundation.utility.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class AttachedTradingDepotFinder {
    
    public static List<BlockPos> lookForTradingDepots(LevelAccessor level, BlockPos jobSitePos) {
        List<BlockPos> foundBlockPositions = new ArrayList<>();
        
        for (Direction direction : Iterate.horizontalDirections) {
            BlockPos position = jobSitePos.relative(direction);
            
            BlockState state = level.getBlockState(position);
            if (state.is(TFRegistry.TRADING_DEPOT.get())) {
                if (state.getValue(CommonTradingDepotBlock.FACING).equals(direction)) {
                    foundBlockPositions.add(position);
                }
            }
        }
        
        return foundBlockPositions;
    }
    
}

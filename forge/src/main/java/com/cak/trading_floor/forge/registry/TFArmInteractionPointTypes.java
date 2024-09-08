package com.cak.trading_floor.forge.registry;

import com.cak.trading_floor.TradingFloor;
import com.simibubi.create.content.kinetics.mechanicalArm.AllArmInteractionPointTypes;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;

public class TFArmInteractionPointTypes {
    
    public static final TradingDepotType TRADING_DEPOT = register("trading_depot", TradingDepotType::new);
    
    public static class TradingDepotType extends ArmInteractionPointType {
        
        public TradingDepotType(ResourceLocation id) {
            super(id);
        }
        
        @Override
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return TFRegistry.TRADING_DEPOT.has(state);
        }
        
        @Override
        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new AllArmInteractionPointTypes.DeployerPoint(this, level, pos, state);
        }
        
    }
    
    private static <T extends ArmInteractionPointType> T register(String id, Function<ResourceLocation, T> factory) {
        T type = factory.apply(TradingFloor.asResource(id));
        ArmInteractionPointType.register(type);
        return type;
    }
    
    public static void register() {}
    
}

package com.cak.trading_floor.forge.mixin;

import com.cak.trading_floor.forge.TFRegistry;
import com.cak.trading_floor.forge.content.TradingDepotBlock;
import com.cak.trading_floor.forge.content.TradingDepotBlockEntity;
import com.simibubi.create.foundation.utility.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.WorkAtPoi;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(WorkAtPoi.class)
public class WorkAtPoiMixin {
    
    @Shadow private long lastCheck;
    
    @Inject(method = "checkExtraStartConditions(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/npc/Villager;)Z", at = @At("TAIL"))
    public void checkExtraStartConditions(ServerLevel level, Villager owner, CallbackInfoReturnable<Boolean> cir) {
        boolean hasReducedCooldown = false;
        
        Optional<GlobalPos> jobSite = owner.getBrain().getMemory(MemoryModuleType.JOB_SITE);
        if (jobSite.isEmpty()) return;
        
        BlockPos jobSitePos = jobSite.get().pos();
        
        for (BlockPos pos : lookForTradingDepots(level, jobSitePos)) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof TradingDepotBlockEntity tbe) {
                hasReducedCooldown = true;
            }
        }
        
        if (hasReducedCooldown) {
            lastCheck -= 250;
        }
    }
    
    
    @Inject(method = "useWorkstation", at = @At("HEAD"))
    public void useWorkstation(ServerLevel level, Villager villager, CallbackInfo ci) {
        trading_floor$innerUseWorkstation(level, villager, ci);
    }
    
    @Unique
    private void trading_floor$innerUseWorkstation(ServerLevel level, Villager villager, CallbackInfo ci) {
        Optional<GlobalPos> jobSite = villager.getBrain().getMemory(MemoryModuleType.JOB_SITE);
        if (jobSite.isEmpty()) return;
        
        BlockPos jobSitePos = jobSite.get().pos();
        
        for (BlockPos pos : lookForTradingDepots(level, jobSitePos)) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof TradingDepotBlockEntity tbe) {
                tbe.tryTradeWith(villager);
            }
        }
    }
    
    @Unique
    private List<BlockPos> lookForTradingDepots(ServerLevel level, BlockPos jobSitePos) {
        List<BlockPos> foundBlockPositions = new ArrayList<>();
        
        for (Direction direction : Iterate.horizontalDirections) {
            BlockPos position = jobSitePos.relative(direction);
            
            BlockState state = level.getBlockState(position);
            if (state.is(TFRegistry.TRADING_DEPOT.get())) {
                if (state.getValue(TradingDepotBlock.FACING).equals(direction)) {
                    foundBlockPositions.add(position);
                }
            }
        }
        
        return foundBlockPositions;
    }
    
}

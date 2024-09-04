package com.cak.trading_floor.forge.content;

import com.simibubi.create.content.equipment.goggles.IHaveHoveringInformation;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TradingDepotBlockEntity extends SmartBlockEntity {
    
    TradingDepotBehaviour tradingDepotBehaviour;
    FilteringBehaviour filtering;

    public TradingDepotBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }
    
    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(tradingDepotBehaviour = new TradingDepotBehaviour(this));
        tradingDepotBehaviour.addAdditionalBehaviours(behaviours);
    }
    
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER)
            return tradingDepotBehaviour.getItemHandler().cast();
        return super.getCapability(cap, side);
    }
    
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        tradingDepotBehaviour.itemHandlerLazyOptional.invalidate();
    }

    public boolean tryTradeWith(Villager villager) {
        if (!tradingDepotBehaviour.isOutputEmpty()) return false;
        
        ItemStack offering = tradingDepotBehaviour.getInputStack();
        
        MerchantOffer selectedOffer = villager.getOffers().getRecipeFor(offering, ItemStack.EMPTY, 0);
        if (selectedOffer == null) return false;

        ItemStack cost = selectedOffer.getBaseCostA();

        tradingDepotBehaviour.setInputStack(new TransportedItemStack(offering.copyWithCount(offering.getCount() - cost.getCount())));
        tradingDepotBehaviour.output.add(selectedOffer.assemble());
        villager.playWorkSound();
        villager.playCelebrateSound();
        tradingDepotBehaviour.blockEntity.notifyUpdate();
        return true;
    }

    public void tryTradeWithMultiple(Villager villager, List<BlockPos> depotPositions) {
        MerchantOffer selectedOffer = null;
        ItemStack offeringA = tradingDepotBehaviour.getInputStack();
        ItemStack offeringB = ItemStack.EMPTY;
        TradingDepotBlockEntity depotB = null;
        for (BlockPos pos : depotPositions) {
            if (pos != getBlockPos()) {
                depotB = (TradingDepotBlockEntity) level.getBlockEntity(pos);
                offeringB = depotB.tradingDepotBehaviour.getInputStack();
            }
            selectedOffer = (villager.getOffers().getRecipeFor(offeringA, offeringB, 0));
            if (selectedOffer != null) break;
        }

        if (selectedOffer == null) return;

        ItemStack costA = selectedOffer.getBaseCostA();
        ItemStack costB = selectedOffer.getCostB();

        tradingDepotBehaviour.setInputStack(new TransportedItemStack(offeringA.copyWithCount(offeringA.getCount() - costA.getCount())));
        depotB.tradingDepotBehaviour.setInputStack(new TransportedItemStack(offeringB.copyWithCount(offeringB.getCount() - costB.getCount())));

        tradingDepotBehaviour.output.add(selectedOffer.assemble());
        villager.playWorkSound();
        villager.playCelebrateSound();
        tradingDepotBehaviour.blockEntity.notifyUpdate();
        depotB.tradingDepotBehaviour.blockEntity.notifyUpdate();
    }
}

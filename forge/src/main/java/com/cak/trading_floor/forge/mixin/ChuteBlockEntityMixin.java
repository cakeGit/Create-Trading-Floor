package com.cak.trading_floor.forge.mixin;

import com.cak.trading_floor.forge.content.depot.TradingDepotBlockEntity;
import com.cak.trading_floor.forge.content.depot.TradingDepotItemHandler;
import com.simibubi.create.content.logistics.chute.ChuteBlockEntity;
import com.simibubi.create.content.logistics.chute.ChuteItemHandler;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.checkerframework.checker.units.qual.A;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = ChuteBlockEntity.class, remap = false)
public abstract class ChuteBlockEntityMixin extends SmartBlockEntity {
    
    @Shadow private LazyOptional<IItemHandler> capBelow;
    @Shadow private ChuteItemHandler itemHandler;
    
    @Shadow protected abstract void handleInput(IItemHandler par1, float par2);
    
    public ChuteBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    
    @Inject(method = "grabCapability", at = @At("RETURN"), cancellable = true)
    public void grabCapability(Direction side, CallbackInfoReturnable<LazyOptional<IItemHandler>> cir) {
        if (cir.getReturnValue().isPresent() || side != Direction.DOWN)
            return;
        
        BlockEntity be = level.getBlockEntity(getBlockPos().relative(Direction.DOWN, 2));
        if (!(be instanceof TradingDepotBlockEntity tbe))
            return;
        cir.setReturnValue(tbe.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.DOWN));
    }
    
    @Redirect(method = "handleInput", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/chute/ChuteBlockEntity;setItem(Lnet/minecraft/world/item/ItemStack;F)V"))
    private void redirect_handleInputFromBelow(ChuteBlockEntity instance, ItemStack stack, float insertionPos) {
        if (capBelow.orElse(null) instanceof TradingDepotItemHandler)
            instance.setItem(stack, insertionPos - 1f);
        else
            instance.setItem(stack, insertionPos);
    }
    
    @Shadow
    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    
    }
    
}

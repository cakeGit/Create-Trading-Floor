package com.cak.trading_floor.fabric.mixin;

import com.cak.trading_floor.fabric.content.depot.TradingDepotBlockEntity;
import com.simibubi.create.content.logistics.chute.ChuteBlockEntity;
import com.simibubi.create.content.logistics.chute.ChuteItemHandler;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import io.github.fabricators_of_create.porting_lib.util.StorageProvider;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**Extends the fan reach of chutes by one block when dealing with trading depots*/
@Mixin(value = ChuteBlockEntity.class, remap = false)
public abstract class ChuteBlockEntityMixin extends SmartBlockEntity {
    
    @Shadow private ChuteItemHandler itemHandler;
    
    public ChuteBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    @Unique
    StorageProvider<ItemVariant> capBelowBelow;
    @Inject(method = "setLevel", at = @At("TAIL"), remap = true)
    public void setLevel(Level level, CallbackInfo ci) {
        capBelowBelow = StorageProvider.createForItems(level, worldPosition.below().below());
    }
    
    @Inject(method = "grabCapability", at = @At("RETURN"), cancellable = true)
    public void grabCapability(Direction side, CallbackInfoReturnable<Storage<ItemVariant>> cir) {
        if (level == null || cir.getReturnValue() != null || side != Direction.DOWN)
            return;
        
        BlockEntity be = level.getBlockEntity(getBlockPos().relative(Direction.DOWN, 2));
        if (!(be instanceof TradingDepotBlockEntity))
            return;
        cir.setReturnValue(capBelowBelow.get(Direction.DOWN));
    }
    
    @Redirect(method = "handleInput", at = @At(value = "INVOKE", remap = true, target = "Lcom/simibubi/create/content/logistics/chute/ChuteBlockEntity;setItem(Lnet/minecraft/world/item/ItemStack;F)V"))
    private void redirect_handleInputFromBelow(ChuteBlockEntity instance, ItemStack stack, float insertionPos) {
        if (capBelowBelow.get(Direction.DOWN) == null) //If there was an injection, must be colluded with above
            instance.setItem(stack, insertionPos - 1f);
        else
            instance.setItem(stack, insertionPos);
    }
    
    @Shadow
    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    
    }
    
}

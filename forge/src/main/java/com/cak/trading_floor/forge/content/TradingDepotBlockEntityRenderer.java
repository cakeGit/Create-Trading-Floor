package com.cak.trading_floor.forge.content;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.logistics.depot.DepotRenderer;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class TradingDepotBlockEntityRenderer extends SmartBlockEntityRenderer<TradingDepotBlockEntity> {
    
    public TradingDepotBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }
    
    @Override
    protected void renderSafe(TradingDepotBlockEntity blockEntity, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(blockEntity, partialTicks, ms, buffer, light, overlay);
        
        ItemStack visibleStack = blockEntity.tradingDepotBehaviour.input;
        
        if (visibleStack.isEmpty()) return;
    
        ms.pushPose();
        
        ms.translate(0.5, 1, 0.5);
        
        TransformStack.cast(ms)
            .rotateY(90 - blockEntity.getBlockState().getValue(TradingDepotBlock.FACING).get2DDataValue() * 90)
            .rotateZ(22.5);
        
        DepotRenderer.renderItem(blockEntity.getLevel(), ms, buffer, light, overlay, visibleStack, 0, new Random(0), new Vec3(0, 0, 0));
       
        ms.popPose();
    }
    
}

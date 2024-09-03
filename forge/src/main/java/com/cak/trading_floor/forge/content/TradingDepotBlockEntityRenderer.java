package com.cak.trading_floor.forge.content;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.depot.DepotRenderer;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

import static com.simibubi.create.content.logistics.depot.DepotRenderer.renderItem;

public class TradingDepotBlockEntityRenderer extends SmartBlockEntityRenderer<TradingDepotBlockEntity> {
    
    public TradingDepotBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }
    
    @Override
    protected void renderSafe(TradingDepotBlockEntity blockEntity, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(blockEntity, partialTicks, ms, buffer, light, overlay);

        TransportedItemStack transported = blockEntity.tradingDepotBehaviour.input;
        TransformStack msr = TransformStack.cast(ms);
        Vec3 itemPosition = VecHelper.getCenterOf(blockEntity.getBlockPos());

        ms.pushPose();
        ms.translate(.5, 1, .5);

        if (transported != null)
            blockEntity.tradingDepotBehaviour.incoming.add(transported);

        // Render main items
        for (TransportedItemStack tis : blockEntity.tradingDepotBehaviour.incoming) {
            ms.pushPose();
            msr.nudge(0);
            float offset = Mth.lerp(partialTicks, tis.prevBeltPosition, tis.beltPosition);
            float sideOffset = Mth.lerp(partialTicks, tis.prevSideOffset, tis.sideOffset);

            if (tis.insertedFrom.getAxis()
                    .isHorizontal()) {
                Vec3 offsetVec = Vec3.atLowerCornerOf(tis.insertedFrom.getOpposite()
                        .getNormal()).scale(.5f - offset);
                ms.translate(offsetVec.x, offsetVec.y, offsetVec.z);
                boolean alongX = tis.insertedFrom.getClockWise()
                        .getAxis() == Direction.Axis.X;
            }

            ItemStack itemStack = tis.stack;
            int angle = tis.angle;
            Random r = new Random(0);

            TransformStack.cast(ms)
                    .rotateY(90 - blockEntity.getBlockState().getValue(TradingDepotBlock.FACING).get2DDataValue() * 90)
                    .rotateZ(22.5);

            renderItem(blockEntity.getLevel(), ms, buffer, light, overlay, itemStack, angle, r, itemPosition);
            ms.popPose();
        }

        if (transported != null)
            blockEntity.tradingDepotBehaviour.incoming.remove(transported);
//
//        ItemStack visibleStack = blockEntity.tradingDepotBehaviour.getHeldItemStack();
//
//        if (visibleStack.isEmpty()) return;
//
//        ms.pushPose();
//

//

//
//        renderItem(blockEntity.getLevel(), ms, buffer, light, overlay, visibleStack, 0, new Random(0), new Vec3(0, 0, 0));
       
        ms.popPose();
    }
    
}

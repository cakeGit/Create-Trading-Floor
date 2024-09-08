package com.cak.trading_floor.forge.content.depot;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.simibubi.create.content.logistics.depot.DepotRenderer.renderItem;

public class TradingDepotRenderer extends SmartBlockEntityRenderer<TradingDepotBlockEntity> {
    
    public TradingDepotRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }
    
    @Override
    protected void renderSafe(TradingDepotBlockEntity blockEntity, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(blockEntity, partialTicks, ms, buffer, light, overlay);

        TransportedItemStack transported = blockEntity.tradingDepotBehaviour.getOffer();
        TransformStack msr = TransformStack.cast(ms);
        Vec3 itemPosition = VecHelper.getCenterOf(blockEntity.getBlockPos());

        ms.pushPose();
        ms.translate(.5, 1, .5);
        
        List<TransportedItemStack> tisStacks = new ArrayList<>();
        tisStacks.addAll(blockEntity.tradingDepotBehaviour.getIncoming());
        if (transported != null)
            tisStacks.add(transported);
        
        // Render main items
        for (TransportedItemStack tis : tisStacks) {
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
            
            boolean renderUpright = BeltHelper.isItemUpright(tis.stack);
            ItemStack itemStack = tis.stack;
            int angle = tis.angle;
            Random r = new Random(0);

            TransformStack.cast(ms)
                    .rotateY(90 - blockEntity.getBlockState().getValue(TradingDepotBlock.FACING).get2DDataValue() * 90);
            if (!renderUpright)
                TransformStack.cast(ms)
                    .rotateZ(22.5);
            else
                ms.translate(0, 0.025, 0);

            renderItem(blockEntity.getLevel(), ms, buffer, light, overlay, itemStack, angle, r, itemPosition);
            ms.popPose();
        }

        // Render output items
        for (int i = 0; i < blockEntity.tradingDepotBehaviour.getRealItemHandler().behaviour.getResults().size(); i++) {
            ItemStack stack = blockEntity.tradingDepotBehaviour.getRealItemHandler().behaviour.getResults().get(i);
            if (stack.isEmpty())
                continue;
            ms.pushPose();

            TransformStack.cast(ms)
                    .rotateY(90 - blockEntity.getBlockState().getValue(TradingDepotBlock.FACING).get2DDataValue() * 90)
                    .rotateZ(22.5);

            msr.nudge(i);

            boolean renderUpright = BeltHelper.isItemUpright(stack);
            msr.rotateY(360 / 8f * i);
            ms.translate(.35, .01/(i+1), 0);
            if (renderUpright)
                msr.rotateY(-(360 / 8f * i));
            Random r = new Random(i + 1);
            int angle = (int) (360 * r.nextFloat());

            renderItem(blockEntity.getLevel(), ms, buffer, light, overlay, stack, renderUpright ? angle + 90 : angle, r, itemPosition);
            ms.popPose();
        }
       
        ms.popPose();
    }
    
}

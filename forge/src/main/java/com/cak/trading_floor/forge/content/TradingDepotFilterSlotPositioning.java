package com.cak.trading_floor.forge.content;

import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

public class TradingDepotFilterSlotPositioning extends ValueBoxTransform.Sided {

    @Override
    public Vec3 getLocalOffset(BlockState state) {
        Direction side = getSide();
        float horizontalAngle = AngleHelper.horizontalAngle(side);
        Vec3 southLocation = VecHelper.voxelSpace(8, 4, 15.5f);
        return VecHelper.rotateCentered(southLocation, horizontalAngle, Direction.Axis.Y);
    }

    @Override
    protected boolean isSideActive(BlockState state, Direction direction) {
        if (direction == state.getValue(FACING).getOpposite()) return false;
        return direction.getAxis().isHorizontal();
    }

    @Override
    protected Vec3 getSouthLocation() {
        return Vec3.ZERO;
    }

}

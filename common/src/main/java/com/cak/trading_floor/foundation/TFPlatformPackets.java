package com.cak.trading_floor.foundation;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public class TFPlatformPackets {
    
    @ExpectPlatform
    public static void sendEmitParticlesToNear(ServerLevel level, ParticleEmitter particleEmitter, Vec3 origin, int count, BlockPos pos, int sendPacketRange) {
        throw new AssertionError();
    }
    
}

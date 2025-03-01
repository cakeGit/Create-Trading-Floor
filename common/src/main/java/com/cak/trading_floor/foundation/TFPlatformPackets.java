package com.cak.trading_floor.foundation;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public class TFPlatformPackets {

    public static TFPlatformRegistryImplementor PLATFORM;

    public static void sendEmitParticlesToNear(ServerLevel level, ParticleEmitter particleEmitter, Vec3 origin, int count, BlockPos pos, int sendPacketRange) {
        PLATFORM.sendEmitParticlesToNear(level, particleEmitter, origin, count, pos, sendPacketRange);
    }

    public interface TFPlatformRegistryImplementor {
        void sendEmitParticlesToNear(ServerLevel level, ParticleEmitter particleEmitter, Vec3 origin, int count, BlockPos pos, int sendPacketRange);
    }

}

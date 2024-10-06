package com.cak.trading_floor.foundation.fabric;

import com.cak.trading_floor.fabric.network.TFPackets;
import com.cak.trading_floor.fabric.network.packets.EmitParticlesFromInstancePacket;
import com.cak.trading_floor.foundation.ParticleEmitter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public class TFPlatformPacketsImpl {
    
    public static void sendEmitParticlesToNear(ServerLevel level, ParticleEmitter particleEmitter, Vec3 origin, int count, BlockPos pos, int sendPacketRange) {
        TFPackets.sendToNear(level, pos, sendPacketRange, new EmitParticlesFromInstancePacket(particleEmitter, origin, count));
    }
    
}

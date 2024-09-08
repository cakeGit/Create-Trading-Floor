package com.cak.trading_floor.forge.foundation.network.packets;

import com.cak.trading_floor.forge.foundation.ParticleEmitter;
import com.cak.trading_floor.forge.registry.TFParticleEmitters;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

public class EmitParticlesFromInstancePacket extends SimplePacketBase {
    
    final ParticleEmitter emitter;
    final Vec3 origin;
    final int count;
    
    int emitterHash = -1;
    
    public EmitParticlesFromInstancePacket(ParticleEmitter emitter, Vec3 origin, int count) {
        this.emitter = emitter;
        this.origin = origin;
        this.count = count;
    }
    
    public EmitParticlesFromInstancePacket(FriendlyByteBuf byteBuf) {
        emitterHash = byteBuf.readInt();
        emitter = TFParticleEmitters.INSTANCES_BY_HASH.get(emitterHash);
        
        count = byteBuf.readInt();
        
        origin = new Vec3(
            byteBuf.readDouble(),
            byteBuf.readDouble(),
            byteBuf.readDouble()
        );
    }
    
    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(emitter.hashCode());
        buffer.writeInt(count);
        buffer.writeDouble(origin.x);
        buffer.writeDouble(origin.y);
        buffer.writeDouble(origin.z);
    }
    
    @Override
    public boolean handle(NetworkEvent.Context context) {
        if (emitter == null)
            throw new RuntimeException("Couldn't resolve local emitter instance, expected " + emitterHash);
        
        emitter.emit(Minecraft.getInstance().level, origin, count);
        return true;
    }
    
}

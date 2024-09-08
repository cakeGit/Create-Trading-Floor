package com.cak.trading_floor.forge.foundation;

import com.cak.trading_floor.forge.foundation.network.TFPackets;
import com.cak.trading_floor.forge.foundation.network.packets.EmitParticlesFromInstancePacket;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Simple util class to emit particles with no fuss, MIT so go forth and copy
 *
 * @author cake
 */
public class ParticleEmitter {
    
    final SimpleParticleType particleType;
    
    AABB volume = new AABB(0, 0, 0, 0, 0, 0);
    Vec3 randomVelocityStrength = Vec3.ZERO;
    Vec3 emitFromCenterStrength = Vec3.ZERO;
    
    int sendPacketRange = 16;
    
    public ParticleEmitter(SimpleParticleType particleType) {
        this.particleType = particleType;
    }
    
    public void emit(ServerLevel level, Vec3 origin, int count) {
        TFPackets.sendToNear(
            level,
            BlockPos.containing(origin),
            sendPacketRange,
            new EmitParticlesFromInstancePacket(this, origin, count)
        );
    }
    
    public void emit(ClientLevel level, Vec3 origin, int count) {
        boolean hasAABB = !volume.equals(new AABB(0, 0, 0, 0, 0, 0));
        boolean doEmitFromCenter = !emitFromCenterStrength.equals(Vec3.ZERO);
        boolean hasVelocity = !randomVelocityStrength.equals(Vec3.ZERO);
        
        for (int i = 0; i < count; i++) {
            Vec3 position = generateRandomPosition(hasAABB, origin);
            Vec3 velocity = hasVelocity ? generateRandomVelocityOfPosition(doEmitFromCenter, origin, position) : Vec3.ZERO;
            
            level.addParticle(particleType, position.x, position.y, position.z, velocity.x, velocity.y, velocity.z);
        }
    }
    
    protected Vec3 generateRandomVelocityOfPosition(boolean doEmitFromCenter, Vec3 origin, Vec3 position) {
        Vec3 baseVelocity = new Vec3(
            randomVelocityStrength.x * Math.random(),
            randomVelocityStrength.y * Math.random(),
            randomVelocityStrength.z * Math.random()
        );
        
        if (!doEmitFromCenter) return baseVelocity;
        
        Vec3 offset = position.subtract(origin).normalize();
        if (offset.equals(Vec3.ZERO)) return baseVelocity;
        
        baseVelocity = baseVelocity.add(
            emitFromCenterStrength.x * offset.x,
            emitFromCenterStrength.y * offset.y,
            emitFromCenterStrength.z * offset.z
        );
        
        return baseVelocity;
    }
    
    protected Vec3 generateRandomPosition(boolean hasAABB, Vec3 origin) {
        if (!hasAABB) return origin; //Skip generating volume data
        
        Vec3 offset = new Vec3(
            volume.minX + volume.getXsize() * Math.random(),
            volume.minY + volume.getYsize() * Math.random(),
            volume.minZ + volume.getZsize() * Math.random()
        );
        
        return origin.add(offset);
    }
    
    public ParticleEmitter setVolume(@NotNull AABB volume) {
        this.volume = volume;
        return this;
    }
    
    public ParticleEmitter setRandomVelocityStrength(@NotNull Vec3 randomVelocityStrength) {
        this.randomVelocityStrength = randomVelocityStrength;
        return this;
    }
    
    public ParticleEmitter setRandomVelocityStrength(float randomVelocityStrength) {
        return setRandomVelocityStrength(new Vec3(randomVelocityStrength, randomVelocityStrength, randomVelocityStrength));
    }
    
    public ParticleEmitter setEmitFromCenterStrength(@NotNull Vec3 emitFromCenterStrength) {
        this.emitFromCenterStrength = emitFromCenterStrength;
        return this;
    }
    
    public ParticleEmitter setEmitFromCenterStrength(float emitFromCenterStrength) {
        return setRandomVelocityStrength(new Vec3(emitFromCenterStrength, emitFromCenterStrength, emitFromCenterStrength));
    }
    
    public ParticleEmitter setSendPacketRange(int sendPacketRange) {
        this.sendPacketRange = sendPacketRange;
        return this;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParticleEmitter that)) return false;
        return Objects.equals(emitFromCenterStrength, that.emitFromCenterStrength) && sendPacketRange == that.sendPacketRange && Objects.equals(particleType, that.particleType) && Objects.equals(volume, that.volume) && Objects.equals(randomVelocityStrength, that.randomVelocityStrength);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(BuiltInRegistries.PARTICLE_TYPE.getKey(particleType), volume, randomVelocityStrength, emitFromCenterStrength, sendPacketRange);
    }
    
}

package com.cak.trading_floor.forge.registry;

import com.cak.trading_floor.TradingFloor;
import com.cak.trading_floor.forge.foundation.ParticleEmitter;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;

public class TFParticleEmitters {
    
    //Must be kept the same on client and server
    public static final HashMap<Integer, ParticleEmitter> INSTANCES_BY_HASH = new HashMap<>();
    
    public static final ParticleEmitter
        TRADE_COMPLETED = registerInstance(new ParticleEmitter(ParticleTypes.HAPPY_VILLAGER)
        .setEmitFromCenterStrength(new Vec3(0.5f, 0.2f, 0.5f))
        .setRandomVelocityStrength(0.05f)
        .setVolume(new AABB(-0.5, 0, -0.5, 0.5, 0.25, 0.5)));
    
    private static ParticleEmitter registerInstance(ParticleEmitter particleEmitter) {
        INSTANCES_BY_HASH.put(particleEmitter.hashCode(), particleEmitter);
        return particleEmitter;
    }
    
    public static void register() {
        TradingFloor.LOGGER.info("Registering Particle Emitters for: " + TradingFloor.NAME);
    }
    
}

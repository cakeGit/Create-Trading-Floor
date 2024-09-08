package com.cak.trading_floor.forge.mixin;

import com.cak.trading_floor.forge.foundation.access.TFParentableAdvancement;
import com.simibubi.create.foundation.advancement.CreateAdvancement;
import net.minecraft.advancements.Advancement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Consumer;

@Mixin(value = CreateAdvancement.class, remap = false)
public abstract class AdvancementAccessMixin implements TFParentableAdvancement {
    
    @Shadow
    private Advancement datagenResult;
    
    @Shadow
    abstract void save(Consumer<Advancement> t);
    
    @Override
    public Advancement getDatagenResult() {
        //Ensure generated
        save((a) -> {});
        
        return datagenResult;
    }
    
}

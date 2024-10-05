package com.cak.trading_floor.fabric.mixin;

import com.cak.trading_floor.foundation.access.TFParentableMixinAdvancement;
import com.simibubi.create.foundation.advancement.CreateAdvancement;
import net.minecraft.advancements.Advancement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Consumer;

@Mixin(value = CreateAdvancement.class, remap = false)
public abstract class AdvancementAccessMixin implements TFParentableMixinAdvancement {
    
    @Shadow
    Advancement datagenResult;
    
    @Shadow
    abstract void save(Consumer<Advancement> t);
    
    @Override
    public Advancement create_trading_floor$getMixinDatagenResult() {
        //Ensure generated
        save((a) -> {});
        
        return datagenResult;
    }
    
}

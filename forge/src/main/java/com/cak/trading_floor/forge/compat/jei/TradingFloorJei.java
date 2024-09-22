package com.cak.trading_floor.forge.compat.jei;

import com.cak.trading_floor.TradingFloor;
import com.cak.trading_floor.forge.compat.jei.virtual_recipes.potential_villager_trade.PotentialVillagerTrade;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@JeiPlugin
public class TradingFloorJei implements IModPlugin {
    
    private static final ResourceLocation ID = TradingFloor.asResource("jei_plugin");
    
    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(new RecipeType<>(TradingFloor.asResource("potential_villager_trade"), PotentialVillagerTrade.class), PotentialVillagerTrade.RECIPES);
    }
    
    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IModPlugin.super.registerCategories(registration);
    }
    
    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ID;
    }
    
}

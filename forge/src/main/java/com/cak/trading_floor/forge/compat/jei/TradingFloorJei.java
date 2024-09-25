package com.cak.trading_floor.forge.compat.jei;

import com.cak.trading_floor.TradingFloor;
import com.cak.trading_floor.forge.compat.jei.virtual_recipes.potential_villager_trade.PotentialVillagerTrade;
import com.cak.trading_floor.forge.compat.jei.virtual_recipes.potential_villager_trade.PotentialVillagerTradeCategory;
import com.cak.trading_floor.forge.registry.TFRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@JeiPlugin
public class TradingFloorJei implements IModPlugin {
    
    private static final ResourceLocation ID = TradingFloor.asResource("jei_plugin");
    
    public static RecipeType<PotentialVillagerTrade> POTENTIAL_TRADE_TYPE = new RecipeType<>(TradingFloor.asResource("potential_villager_trade"), PotentialVillagerTrade.class);
    
    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(POTENTIAL_TRADE_TYPE, PotentialVillagerTrade.RECIPES);
    }
    
    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new PotentialVillagerTradeCategory());
    }
    
    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(TFRegistry.TRADING_DEPOT.asStack(), POTENTIAL_TRADE_TYPE);
    }
    
    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ID;
    }
    
}

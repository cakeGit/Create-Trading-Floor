package com.cak.trading_floor.forge.compat.jei.virtual_recipes.potential_villager_trade;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

/**I WORKED SO HARD ON THIS AND I REALISED ITS USELESS*/
public class PotentialVillagerTradeSerializer implements RecipeSerializer<PotentialVillagerTrade> {
    
    @Override
    public PotentialVillagerTrade fromJson(ResourceLocation recipeId, JsonObject serializedRecipe) {
        throw new RuntimeException("Potential Villager Trades do not support JSON entries! Contact mod author");
    }
    
    @Override
    public @Nullable PotentialVillagerTrade fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        
        int villagerLevel = buffer.readInt();
        int strLen = buffer.readInt();
        StringBuilder professionId = new StringBuilder();
        
        for (int i = 0; i < strLen; i++)
            professionId.append(buffer.readChar());
        
        System.out.println("READ #1234567892345 WITH: " + professionId);
        
        return new PotentialVillagerTrade(recipeId,
            villagerLevel,
            ForgeRegistries.VILLAGER_PROFESSIONS.getValue(new ResourceLocation(professionId.toString())),
            buffer.readItem(), buffer.readItem(), buffer.readItem()
        );
    }
    
    @Override
    public void toNetwork(FriendlyByteBuf buffer, PotentialVillagerTrade recipe) {
        buffer.writeInt(recipe.villagerLevel);
        
        String name = recipe.profession.name();
        buffer.writeInt(name.length());
        for (int i = 0; i < name.length(); i++)
            buffer.writeChar(name.charAt(i));
        
        buffer.writeItem(recipe.costA);
        buffer.writeItem(recipe.costB);
        buffer.writeItem(recipe.result);
    }
    
}

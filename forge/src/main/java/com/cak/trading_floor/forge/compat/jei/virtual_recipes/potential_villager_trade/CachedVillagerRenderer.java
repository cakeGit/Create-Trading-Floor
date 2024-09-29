package com.cak.trading_floor.forge.compat.jei.virtual_recipes.potential_villager_trade;

import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerType;

import java.util.List;

public class CachedVillagerRenderer {
    
    private static Villager him = null;
    public static final List<VillagerType> ALL_BASE_VILLAGER_TYPES = List.of(
        VillagerType.DESERT,
        VillagerType.JUNGLE,
        VillagerType.PLAINS,
        VillagerType.SAVANNA,
        VillagerType.SNOW,
        VillagerType.SWAMP,
        VillagerType.TAIGA
    );
    
    public static void renderVillagerForRecipe(int x, int y, int scale, float targetX, float targetY, PotentialVillagerTrade recipe) {
        if (him == null && Minecraft.getInstance().level != null)
            him = new Villager(EntityType.VILLAGER, Minecraft.getInstance().level);
        
        if (him == null) return;
        
        VillagerData recipeSpecificData = him.getVillagerData()
            .setProfession(recipe.getProfession())
            .setLevel(recipe.getVillagerLevel());
        
        if (recipe.offer.isNoteVillagerTypeSpecific())
            recipeSpecificData = setCycleOfVillagerType(recipeSpecificData);
        else
            recipeSpecificData = recipeSpecificData.setType(VillagerType.PLAINS);
        
        him.setVillagerData(recipeSpecificData);
        
        InventoryScreen.renderEntityInInventory(x, y, scale, targetX, targetY, him);
        
    }
    
    private static VillagerData setCycleOfVillagerType(VillagerData recipeSpecificData) {
        int villagerTypeIndex = (AnimationTickHolder.getTicks() / 20) % ALL_BASE_VILLAGER_TYPES.size();
        return recipeSpecificData.setType(ALL_BASE_VILLAGER_TYPES.get(villagerTypeIndex));
    }
    
}

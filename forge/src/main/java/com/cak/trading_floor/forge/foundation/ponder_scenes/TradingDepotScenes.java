package com.cak.trading_floor.forge.foundation.ponder_scenes;

import com.simibubi.create.foundation.ponder.ElementLink;
import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import com.simibubi.create.foundation.ponder.element.EntityElement;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;

public class TradingDepotScenes {
    
    public static void trading(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("trading_depot_trading", "Trading with trading depots");
        scene.configureBasePlate(0, 0, 3);
        scene.showBasePlate();
        
        scene.idle(10);
        
        ElementLink<EntityElement> villager = scene.world.createEntity(level -> {
            Villager newEntity = new Villager(EntityType.VILLAGER, level);
            newEntity.setPos(2.5, 1, 2.5);
            newEntity.setYHeadRot(90f);
            newEntity.setYBodyRot(90f);
            newEntity.setOldPosAndRot();
            newEntity.tick();
            newEntity.setVillagerData(newEntity.getVillagerData().setProfession(VillagerProfession.FLETCHER));
            return newEntity;
        });
        
        scene.world.showSection(util.select.fromTo(0, 1, 0, 3, 3, 3), Direction.DOWN);
        
        scene.idle(20);
        scene.world.modifyEntity(villager, entity -> {
            Villager villagerEntity = (Villager) entity;
            villagerEntity.setXRot(10f);
        });
        
        scene.idle(1);
        scene.world.modifyEntity(villager, entity -> {
            Villager villagerEntity = (Villager) entity;
            villagerEntity.setXRot(20f);
        });
        
        
        scene.markAsFinished();
    }
    
}

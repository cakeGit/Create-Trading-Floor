package com.cak.trading_floor.forge.foundation.ponder_scenes;

import com.cak.trading_floor.forge.content.depot.TradingDepotBlockEntity;
import com.cak.trading_floor.forge.content.depot.behavior.TradingDepotBehaviour;
import com.cak.trading_floor.forge.registry.TFParticleEmitters;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.ponder.ElementLink;
import com.simibubi.create.foundation.ponder.PonderPalette;
import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import com.simibubi.create.foundation.ponder.element.EntityElement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public class TradingDepotScenes {
    
    public static void trading(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("trading_depot_trading", "Trading with trading depots");
        scene.configureBasePlate(0, 0, 3);
        scene.showBasePlate();
        
        scene.idle(10);
        
        scene.world.showSection(util.select.position(1, 1, 1), Direction.DOWN);
        
        scene.idle(10);
        
        scene.overlay.showText(80)
            .placeNearTarget()
            .pointAt(new Vec3(1.5, 1.5, 1.5))
            .text("To use a trading depot, attach it to a villager workstation");
        
        scene.addKeyframe();
        
        scene.idle(80);
        
        scene.world.showSection(util.select.position(1, 1, 2), Direction.DOWN);
        
        scene.idle(40);
        
        scene.overlay.showOutline(PonderPalette.GREEN, "Depot Highlight", util.select.position(1,1,1), 30);
        
        scene.idle(1);
        
        scene.overlay.showOutline(PonderPalette.WHITE, "Workstation Highlight", util.select.position(1,1,2), 28);
        
        scene.addKeyframe();
        scene.idle(80);
        
        scene.world.modifyBlockEntity(new BlockPos(1, 1, 1), TradingDepotBlockEntity.class, be -> {
            TransportedItemStack tis = new TransportedItemStack(Items.FLINT.getDefaultInstance().copyWithCount(32));
            tis.insertedFrom = Direction.SOUTH;
            be.getBehaviour(TradingDepotBehaviour.TYPE).getIncoming()
                .add(tis);
        });
        
        scene.idle(20);
        
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
        
        scene.idle(40);
        
        scene.overlay.showText(80)
            .placeNearTarget()
            .pointAt(new Vec3(1.5, 1.5, 1.5))
            .text("When a villager next works at their workstation they will trade with the attached depot");
        
        scene.addKeyframe();
        scene.idle(40);
        
        // "smoothly" look at the table
        scene.world.modifyEntity(villager, entity -> {
            Villager villagerEntity = (Villager) entity;
            villagerEntity.setXRot(10f);
        });
        scene.idle(1);
        scene.world.modifyEntity(villager, entity -> {
            Villager villagerEntity = (Villager) entity;
            villagerEntity.setXRot(20f);
        });
        
        scene.idle(20);
        
        scene.addInstruction(activeScene -> {
            TFParticleEmitters.TRADE_COMPLETED.emitWithConsumer(activeScene.getWorld()::addParticle, new Vec3(1.5, 1.9, 1.5), 4);
        });
        
        scene.world.modifyBlockEntity(new BlockPos(1, 1, 1), TradingDepotBlockEntity.class, be -> {
            be.getBehaviour(TradingDepotBehaviour.TYPE).setOfferStack(ItemStack.EMPTY);
            be.getBehaviour(TradingDepotBehaviour.TYPE).getResults().add(Items.EMERALD.getDefaultInstance());
        });
        
        scene.markAsFinished();
    }
    
}

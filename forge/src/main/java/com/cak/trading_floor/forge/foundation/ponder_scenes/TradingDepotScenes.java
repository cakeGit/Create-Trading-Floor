package com.cak.trading_floor.forge.foundation.ponder_scenes;

import com.cak.trading_floor.forge.content.depot.ItemCopyWithCount;
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
        
        scene.overlay.showOutline(PonderPalette.GREEN, "Depot Highlight", util.select.position(1, 1, 1), 30);
        
        scene.idle(1);
        
        scene.overlay.showOutline(PonderPalette.WHITE, "Workstation Highlight", util.select.position(1, 1, 2), 28);
        
        scene.addKeyframe();
        scene.idle(80);
        
        scene.world.modifyBlockEntity(new BlockPos(1, 1, 1), TradingDepotBlockEntity.class, be -> {
            TransportedItemStack tis = new TransportedItemStack(ItemCopyWithCount.of(Items.FLINT.getDefaultInstance(), 32));
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
    
    public static void trading_double(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("trading_depot_double_trading", "Trading with multiple trading depots");
        scene.configureBasePlate(0, 0, 6);
        scene.showBasePlate();
        
        scene.idle(20);
        
        scene.world.showSection(
            util.select.fromTo(6, 0, 0, 6, 2, 5),
            Direction.WEST
        );
        
        scene.idle(20);
        
        scene.world.showSection(util.select.layer(1)
            .substract(util.select.fromTo(6, 0, 0, 6, 2, 5))
            .substract(util.select.position(0, 1, 3)), Direction.DOWN
        );
        
        scene.idle(20);
        
        scene.world.showSection(util.select.layer(2)
                .substract(util.select.fromTo(6, 0, 0, 6, 2, 5))
                .substract(util.select.position(2, 2, 2))
                .substract(util.select.position(1, 2, 3)),
            Direction.DOWN
        );
        
        scene.world.showSection(util.select.position(0, 1, 3)
                .add(util.select.position(1, 2, 3)),
            Direction.DOWN
        );
        
        scene.addKeyframe();
        scene.idle(20);
        
        scene.overlay.showText(80)
            .placeNearTarget()
            .pointAt(new Vec3(3.5, 2.5, 3.5))
            .text("To complete trades with multiple inputs, 2 depots can be used together");
        
        scene.idle(90);
        
        scene.overlay.showText(80)
            .placeNearTarget()
            .pointAt(new Vec3(3.5, 2.5, 2.5))
            .text("While not required, you should set the filter on the first input to avoid other trades");
        
        scene.idle(90);
        
        scene.world.createItemOnBelt(
            new BlockPos(3, 2, 0), Direction.NORTH, Items.EMERALD.getDefaultInstance()
        );
        scene.world.createItemOnBelt(
            new BlockPos(0, 1, 3), Direction.WEST, ItemCopyWithCount.of(Items.GRAVEL.getDefaultInstance(), 10)
        );
        
        scene.idle(60);
        
        ElementLink<EntityElement> villager = scene.world.createEntity(level -> {
            Villager newEntity = new Villager(EntityType.VILLAGER, level);
            newEntity.setPos(4.5, 2, 4.5);
            newEntity.setYHeadRot(135f);
            newEntity.setYBodyRot(135f);
            newEntity.setOldPosAndRot();
            newEntity.tick();
            newEntity.setVillagerData(newEntity.getVillagerData().setProfession(VillagerProfession.FLETCHER));
            return newEntity;
        });
        
        scene.addKeyframe();
        scene.idle(20);
        
        scene.overlay.showText(80)
            .placeNearTarget()
            .pointAt(new Vec3(3.5, 2.5, 2.5))
            .text("Note that trading depots will only share contents if they have matching filters, or the other is empty");
        
        scene.idle(90);
        
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
        
        scene.addKeyframe();
        scene.idle(20);
        
        scene.addInstruction(activeScene -> {
            TFParticleEmitters.TRADE_COMPLETED.emitWithConsumer(activeScene.getWorld()::addParticle, new Vec3(3.5, 2.9, 2.5), 4);
        });
        
        scene.world.modifyBlockEntity(new BlockPos(3, 2, 2), TradingDepotBlockEntity.class, tradingDepotBlockEntity ->
            tradingDepotBlockEntity.getBehaviour(TradingDepotBehaviour.TYPE).setOfferStack(ItemStack.EMPTY)
        );
        scene.world.modifyBlockEntity(new BlockPos(2, 2, 3), TradingDepotBlockEntity.class, tradingDepotBlockEntity ->
            tradingDepotBlockEntity.getBehaviour(TradingDepotBehaviour.TYPE).setOfferStack(ItemStack.EMPTY)
        );
        scene.world.modifyBlockEntity(new BlockPos(3, 2, 2), TradingDepotBlockEntity.class, tradingDepotBlockEntity ->
            tradingDepotBlockEntity.getBehaviour(TradingDepotBehaviour.TYPE).getResults().add(ItemCopyWithCount.of(Items.FLINT.getDefaultInstance(), 10))
        );
        
        scene.idle(20);
        
        scene.overlay.showText(80)
            .placeNearTarget()
            .pointAt(new Vec3(3.5, 2.5, 2.5))
            .text("The output then goes to whichever depot has the first item of the trade");
        
        scene.idle(90);
        
        scene.world.showSection(util.select.position(2, 2, 2), Direction.DOWN);
        
        scene.idle(20);
        
        scene.world.modifyBlockEntity(new BlockPos(3, 2, 2), TradingDepotBlockEntity.class, tradingDepotBlockEntity ->
            tradingDepotBlockEntity.getBehaviour(TradingDepotBehaviour.TYPE).getResults().clear()
        );
        scene.world.createItemOnBelt(new BlockPos(2, 1, 2), Direction.EAST, ItemCopyWithCount.of(Items.FLINT.getDefaultInstance(), 10));
        
        scene.idle(20);
        
        scene.markAsFinished();
    }
    
}

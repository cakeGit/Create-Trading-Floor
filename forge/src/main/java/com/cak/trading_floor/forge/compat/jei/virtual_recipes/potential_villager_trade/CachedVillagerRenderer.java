package com.cak.trading_floor.forge.compat.jei.virtual_recipes.potential_villager_trade;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
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
    
    public static void renderVillagerForRecipe(int x, int y, int scale, float targetX, float targetY, PotentialVillagerTrade recipe, PoseStack posestack) {
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
        
        renderEntityInInventory(x, y, scale, targetX, targetY, him, posestack);
    }
    
    private static VillagerData setCycleOfVillagerType(VillagerData recipeSpecificData) {
        int villagerTypeIndex = (AnimationTickHolder.getTicks() / 20) % ALL_BASE_VILLAGER_TYPES.size();
        return recipeSpecificData.setType(ALL_BASE_VILLAGER_TYPES.get(villagerTypeIndex));
    }
    
    
    public static void renderEntityInInventory(int posX, int posY, int scale, float mouseX, float mouseY, LivingEntity livingEntity, PoseStack posestack) {
        float f = (float) Math.atan((double) (mouseX / 40.0F));
        float f1 = (float) Math.atan((double) (mouseY / 40.0F));
        renderEntityInInventoryRaw(posX, posY, scale, f, f1, livingEntity, posestack);
    }
    
    public static void renderEntityInInventoryRaw(int i, int j, int k, float angleXComponent, float angleYComponent, LivingEntity arg, PoseStack posestack) {
        float f = angleXComponent;
        float f1 = angleYComponent;
        posestack.pushPose();
        posestack.translate((double) i, (double) j, 1050.0);
        posestack.scale(1.0F, 1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();
        posestack.translate(0.0, 0.0, 1000.0);
        posestack.scale((float) k, (float) k, (float) k);
        Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
        Quaternion quaternion1 = Vector3f.XP.rotationDegrees(f1 * 20.0F);
        quaternion.mul(quaternion1);
        posestack.mulPose(quaternion);
        float f2 = arg.yBodyRot;
        float f3 = arg.getYRot();
        float f4 = arg.getXRot();
        float f5 = arg.yHeadRotO;
        float f6 = arg.yHeadRot;
        arg.yBodyRot = 180.0F + f * 20.0F;
        arg.setYRot(180.0F + f * 40.0F);
        arg.setXRot(-f1 * 20.0F);
        arg.yHeadRot = arg.getYRot();
        arg.yHeadRotO = arg.getYRot();
//        Lighting.setupForEntityInInventory();
        Lighting.setupForFlatItems();
        EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        quaternion1.conj();
        entityrenderdispatcher.overrideCameraOrientation(quaternion1);
        entityrenderdispatcher.setRenderShadow(false);
        MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() -> {
            entityrenderdispatcher.render(arg, 0.0, 0.0, 0.0, 0f, 1.0F, posestack, multibuffersource$buffersource, 0x0000FF);
        });
        multibuffersource$buffersource.endBatch();
        entityrenderdispatcher.setRenderShadow(true);
        arg.yBodyRot = f2;
        arg.setYRot(f3);
        arg.setXRot(f4);
        arg.yHeadRotO = f5;
        arg.yHeadRot = f6;
        posestack.popPose();
        RenderSystem.applyModelViewMatrix();
        Lighting.setupFor3DItems();
    }
    
}

package com.cak.trading_floor.forge.content.depot;

import com.cak.trading_floor.forge.content.depot.behavior.TradingDepotBehaviour;
import com.cak.trading_floor.forge.foundation.advancement.TFAdvancementBehaviour;
import com.cak.trading_floor.forge.registry.TFRegistry;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TradingDepotBlock extends HorizontalDirectionalBlock implements IBE<TradingDepotBlockEntity>, IWrenchable {
    
    public TradingDepotBlock(Properties properties) {
        super(properties);
    }
    
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
    
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }
    
    protected static TradingDepotBehaviour get(BlockGetter worldIn, BlockPos pos) {
        return BlockEntityBehaviour.get(worldIn, pos, TradingDepotBehaviour.TYPE);
    }
    
    @Override
    public @NotNull InteractionResult use(BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand,
                                          BlockHitResult ray) {
        if (ray.getDirection() == state.getValue(FACING).getOpposite())
            return InteractionResult.PASS;
        if (world.isClientSide)
            return InteractionResult.SUCCESS;
        
        TradingDepotBehaviour behaviour = get(world, pos);
        if (behaviour == null)
            return InteractionResult.PASS;
        
        ItemStack heldItem = player.getItemInHand(hand);
        boolean wasEmptyHanded = heldItem.isEmpty();
        boolean skipItemPlacement = AllBlocks.MECHANICAL_ARM.isIn(heldItem);
        
        if (wasEmptyHanded) {
            if (!behaviour.getResults().isEmpty()) {
                for (ItemStack stack : behaviour.getResults()) {
                    player.getInventory().placeItemBackInInventory(stack);
                }
                behaviour.getResults().clear();
            } else if (!behaviour.getOfferStack().isEmpty()) {
                player.getInventory().placeItemBackInInventory(behaviour.getOfferStack());
                behaviour.setOfferStack(ItemStack.EMPTY);
            }
        } else if (!skipItemPlacement) {
            TransportedItemStack transported = new TransportedItemStack(heldItem);
            
            transported.insertedFrom = player.getDirection();
            transported.prevBeltPosition = .25f;
            transported.beltPosition = .25f;
            
            if (!behaviour.getOfferStack().isEmpty()) {
                player.getInventory().placeItemBackInInventory(behaviour.getOfferStack());
                behaviour.setOfferStack(ItemStack.EMPTY);
            }
            
            behaviour.setOfferStack(transported);
            player.setItemInHand(hand, ItemStack.EMPTY);
            
            AllSoundEvents.DEPOT_SLIDE.playOnServer(world, pos);
        }
        
        behaviour.blockEntity.notifyUpdate();
        return InteractionResult.SUCCESS;
    }
    
    @Override
    public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean movedByPiston) {
        IBE.onRemove(state, level, pos, newState);
    }
    
    @Override
    public @NotNull VoxelShape getVisualShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Shapes.empty();
    }
    
    @Override
    public boolean propagatesSkylightDown(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return true;
    }
    
    @Override
    public Class<TradingDepotBlockEntity> getBlockEntityClass() {
        return TradingDepotBlockEntity.class;
    }
    
    @Override
    public BlockEntityType<? extends TradingDepotBlockEntity> getBlockEntityType() {
        return TFRegistry.TRADING_DEPOT_BLOCK_ENTITY.get();
    }
    
    @Override
    public void setPlacedBy(@NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pState, LivingEntity pPlacer, @NotNull ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        TFAdvancementBehaviour.setPlacedBy(pLevel, pPos, pPlacer);
    }
    
}

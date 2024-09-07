package com.cak.trading_floor.forge.content.depot;

import com.cak.trading_floor.forge.foundation.advancement.TFAdvancementBehaviour;
import com.cak.trading_floor.forge.registry.TFRegistry;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
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
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand,
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
        boolean shouldntPlaceItem = AllBlocks.MECHANICAL_ARM.isIn(heldItem);

        ItemStack mainItemStack = behaviour.getOfferStack();
        if (!mainItemStack.isEmpty()) {
            player.getInventory()
                    .placeItemBackInInventory(mainItemStack);
            behaviour.removeOfferStack();
            world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f,
                    1f + world.random.nextFloat());
        }
        if (!behaviour.isOutputEmpty()) {
            for (int i = 0; i < behaviour.result.size(); i++)
                player.getInventory().placeItemBackInInventory(behaviour.result.get(i));
            world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f,
                    1f + world.random.nextFloat());
        }

        if (!wasEmptyHanded && !shouldntPlaceItem) {
            TransportedItemStack transported = new TransportedItemStack(heldItem);
            transported.insertedFrom = player.getDirection();
            transported.prevBeltPosition = .25f;
            transported.beltPosition = .25f;
            behaviour.setOfferStack(transported);
            player.setItemInHand(hand, ItemStack.EMPTY);
            AllSoundEvents.DEPOT_SLIDE.playOnServer(world, pos);
        }

        behaviour.blockEntity.notifyUpdate();
        return InteractionResult.SUCCESS;
    }

    public static void onLanded(BlockGetter worldIn, Entity entityIn) {
        if (!(entityIn instanceof ItemEntity))
            return;
        if (!entityIn.isAlive())
            return;
        if (entityIn.level().isClientSide)
            return;

        ItemEntity itemEntity = (ItemEntity) entityIn;
        DirectBeltInputBehaviour inputBehaviour =
                BlockEntityBehaviour.get(worldIn, entityIn.blockPosition(), DirectBeltInputBehaviour.TYPE);
        if (inputBehaviour == null)
            return;
        ItemStack remainder = inputBehaviour.handleInsertion(itemEntity.getItem(), Direction.DOWN, false);
        itemEntity.setItem(remainder);
        if (remainder.isEmpty())
            itemEntity.discard();
    }


    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        IBE.onRemove(state, level, pos, newState);
    }
    
    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }
    
    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
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
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        TFAdvancementBehaviour.setPlacedBy(pLevel, pPos, pPlacer);
    }
    
}

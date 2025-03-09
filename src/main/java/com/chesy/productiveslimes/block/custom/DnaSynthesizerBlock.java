package com.chesy.productiveslimes.block.custom;

import com.chesy.productiveslimes.block.entity.DnaSynthesizerBlockEntity;
import com.chesy.productiveslimes.datacomponent.ModDataComponents;
import com.chesy.productiveslimes.util.ContainerUtils;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DnaSynthesizerBlock extends Block implements BlockEntityProvider {
    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;

    public DnaSynthesizerBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends DnaSynthesizerBlock> getCodec() {
        return createCodec(DnaSynthesizerBlock::new);
    }

    @Nullable
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DnaSynthesizerBlockEntity(pos, state);
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof DnaSynthesizerBlockEntity dnaSynthesizerBlockEntity) {
            ContainerUtils.dropContents(world, pos, dnaSynthesizerBlockEntity);
        }
        super.onStateReplaced(state, world, pos, moved);
    }

    @Override
    protected List<ItemStack> getDroppedStacks(BlockState state, LootWorldContext.Builder builder) {
        List<ItemStack> drops = super.getDroppedStacks(state, builder);
        BlockEntity blockEntity = builder.getOptional(LootContextParameters.BLOCK_ENTITY);

        if (blockEntity instanceof DnaSynthesizerBlockEntity dnaSynthesizerBlockEntity) {
            ItemStack stack = new ItemStack(this);

            stack.set(ModDataComponents.ENERGY, dnaSynthesizerBlockEntity.getEnergyHandler().getAmountStored());

            drops.clear();
            drops.add(stack);
        }

        return drops;
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient){
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof DnaSynthesizerBlockEntity dnaSynthesizerBlockEntity){
                player.openHandledScreen(dnaSynthesizerBlockEntity);
            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }

        return ActionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient){
            return null;
        }

        return ((world1, pos, state1, blockEntity) -> {
            if(blockEntity instanceof DnaSynthesizerBlockEntity dnaSynthesizerBlockEntity) {
                dnaSynthesizerBlockEntity.tick(world1, pos, state1);
            }
        });
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof DnaSynthesizerBlockEntity dnaSynthesizerBlockEntity) {
            int energy = itemStack.getOrDefault(ModDataComponents.ENERGY, 0);

            dnaSynthesizerBlockEntity.getEnergyHandler().setAmount(energy);
        }

        super.onPlaced(world, pos, state, placer, itemStack);
    }
}

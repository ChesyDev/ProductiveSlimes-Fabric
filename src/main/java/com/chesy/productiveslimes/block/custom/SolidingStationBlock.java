package com.chesy.productiveslimes.block.custom;

import com.chesy.productiveslimes.block.entity.EnergyGeneratorBlockEntity;
import com.chesy.productiveslimes.block.entity.SolidingStationBlockEntity;
import com.chesy.productiveslimes.datacomponent.ModDataComponents;
import com.chesy.productiveslimes.util.ContainerUtils;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
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
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SolidingStationBlock extends Block implements BlockEntityProvider {
    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;

    public SolidingStationBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends SolidingStationBlock> getCodec() {
        return createCodec(SolidingStationBlock::new);
    }

    @Nullable
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SolidingStationBlockEntity(pos, state);
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
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof SolidingStationBlockEntity solidingStationBlockEntity) {
            ContainerUtils.dropContents(world, pos, solidingStationBlockEntity);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected List<ItemStack> getDroppedStacks(BlockState state, LootWorldContext.Builder builder) {
        List<ItemStack> drops = super.getDroppedStacks(state, builder);
        BlockEntity blockEntity = builder.getOptional(LootContextParameters.BLOCK_ENTITY);

        if (blockEntity instanceof SolidingStationBlockEntity solidingStationBlockEntity) {
            ItemStack stack = new ItemStack(this);
            stack.set(ModDataComponents.ENERGY, solidingStationBlockEntity.getEnergyHandler().getAmountStored());
            drops.clear();
            drops.add(stack);
        }

        return drops;
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient()) {
            BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof SolidingStationBlockEntity solidingStationBlockEntity) {
                player.openHandledScreen(solidingStationBlockEntity);
            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World pWorld, BlockState pState, BlockEntityType<T> type) {
        if(pWorld.isClient()) {
            return null;
        }

        return ((world, pos, state, blockEntity) -> {
            if(blockEntity instanceof SolidingStationBlockEntity) {
                ((SolidingStationBlockEntity) blockEntity).tick(world, pos, state);
            }
        });
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof SolidingStationBlockEntity solidingStationBlockEntity) {
            int energy = itemStack.getOrDefault(ModDataComponents.ENERGY, 0);

            solidingStationBlockEntity.getEnergyHandler().setAmount(energy);
        }

        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
        super.appendTooltip(stack, context, tooltip, options);

        if (stack.getOrDefault(ModDataComponents.ENERGY, 0) != 0) {
            int energy = stack.getOrDefault(ModDataComponents.ENERGY, 0);
            tooltip.add(Text.translatable("tooltip.productiveslimes.energy_stored")
                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x00FF00)))
                    .append(Text.translatable("tooltip.productiveslimes.energy_amount", energy)
                            .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFF)))));
        }
    }
}

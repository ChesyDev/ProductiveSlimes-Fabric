package com.chesy.productiveslimes.block.custom;

import com.chesy.productiveslimes.block.entity.EnergyGeneratorBlockEntity;
import com.chesy.productiveslimes.util.ContainerUtils;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EnergyGeneratorBlock extends Block implements BlockEntityProvider {
    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;

    public EnergyGeneratorBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape SHAPE = VoxelShapes.cuboid(0.25D, 0.0D, 0D, 0.75D, 1D, 1D);
        if (state.get(FACING) == Direction.WEST || state.get(FACING) == Direction.EAST) {
            SHAPE = VoxelShapes.cuboid(0D, 0.0D, 0.25D, 1D, 1D, 0.75D);
        }
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof EnergyGeneratorBlockEntity energyGeneratorBlockEntity) {
            ContainerUtils.dropContents(world, pos, energyGeneratorBlockEntity);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
        List<ItemStack> drops = super.getDroppedStacks(state, builder);
        BlockEntity blockEntity = builder.getOptional(LootContextParameters.BLOCK_ENTITY);

        if (blockEntity instanceof EnergyGeneratorBlockEntity energyGeneratorBlockEntity) {
            ItemStack stack = new ItemStack(this);

            NbtCompound nbt = stack.getOrCreateNbt();
            nbt.putInt("energy", energyGeneratorBlockEntity.getEnergyHandler().getAmountStored());
            stack.setNbt(nbt);

            drops.clear();
            drops.add(stack);
        }

        return drops;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient()) {
            BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof EnergyGeneratorBlockEntity energyGeneratorBlockEntity) {
                player.openHandledScreen(energyGeneratorBlockEntity);
            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EnergyGeneratorBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World pWorld, BlockState pState, BlockEntityType<T> type) {
        if(pWorld.isClient()) {
            return null;
        }

        return ((world, pos, state, blockEntity) -> {
            if(blockEntity instanceof EnergyGeneratorBlockEntity) {
                ((EnergyGeneratorBlockEntity) blockEntity).tick(world, pos, state);
            }
        });
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
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
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof EnergyGeneratorBlockEntity energyGeneratorBlockEntity) {
            if(itemStack.hasNbt() && itemStack.getNbt() != null && itemStack.getNbt().contains("energy")){
                energyGeneratorBlockEntity.getEnergyHandler().setAmount(itemStack.getOrCreateNbt().getInt("energy"));
            }
        }

        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        super.appendTooltip(stack, world, tooltip, options);

        if (stack.hasNbt() && stack.getNbt() != null && stack.getNbt().getInt("energy") != 0) {
            int energy = stack.getNbt().getInt("energy");
            tooltip.add(Text.translatable("tooltip.productiveslimes.energy_stored")
                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x00FF00)))
                    .append(Text.translatable("tooltip.productiveslimes.energy_amount", energy)
                            .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFF)))));
        }
    }
}

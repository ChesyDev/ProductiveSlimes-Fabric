package com.chesy.productiveslimes.block.custom;

import com.chesy.productiveslimes.block.entity.CableBlockEntity;
import com.chesy.productiveslimes.block.entity.EnergyGeneratorBlockEntity;
import com.chesy.productiveslimes.block.entity.ModBlockEntities;
import com.chesy.productiveslimes.handler.NetworkManager;
import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.tick.ScheduledTickView;
import org.apache.logging.log4j.core.Core;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;

import java.util.List;
import java.util.stream.Stream;

public class CableBlock extends Block implements BlockEntityProvider {
    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;

    public static final BooleanProperty UP = BooleanProperty.of("up");
    public static final BooleanProperty DOWN = BooleanProperty.of("down");
    public static final BooleanProperty NORTH = BooleanProperty.of("north");
    public static final BooleanProperty SOUTH = BooleanProperty.of("south");
    public static final BooleanProperty EAST = BooleanProperty.of("east");
    public static final BooleanProperty WEST = BooleanProperty.of("west");
    private static final VoxelShape CORE_SHAPE = VoxelShapes.cuboid(.3125D, .3125D, .3125D, .6875D, .6875D, .6875D);
    private static final VoxelShape UP_SHAPE = VoxelShapes.cuboid(.3125D, .3125, .3125D, .6875D, 1D, .6875D);
    private static final VoxelShape DOWN_SHAPE = VoxelShapes.cuboid(.3125D, 0D, .3125D, .6875D, .6875D, .6875D);
    private static final VoxelShape NORTH_SHAPE = VoxelShapes.cuboid(.3125D, .3125D, 0D, .6875D, .6875D, .6875D);
    private static final VoxelShape SOUTH_SHAPE = VoxelShapes.cuboid(.3125D, .3125D, .3125D, .6875D, .6875D, 1D);
    private static final VoxelShape EAST_SHAPE = VoxelShapes.cuboid(.3125D, .3125D, .3125D, 1D, .6875D, .6875D);
    private static final VoxelShape WEST_SHAPE = VoxelShapes.cuboid(0D, .3125D, .3125D, .6875D, .6875D, .6875D);

    public CableBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(UP, false)
                .with(DOWN, false)
                .with(NORTH, false)
                .with (SOUTH, false)
                .with(EAST, false)
                .with(WEST, false));
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if(world instanceof ServerWorld pWorld) {
            boolean canConnect = this.canConnectTo(pWorld, neighborPos, direction);
            NetworkManager.rebuildNetwork(pWorld, pos);
            return state.with(getPropertyForDirection(direction), canConnect);
        }

        return state;
    }

    @Override
    protected MapCodec<? extends CableBlock> getCodec() {
        return createCodec(CableBlock::new);
    }

    @Nullable
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CableBlockEntity(pos, state);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        List<VoxelShape> shapes = Lists.newArrayList(CORE_SHAPE);
        if (state.get(UP)) {
            shapes.add(UP_SHAPE);
        }
        if (state.get(DOWN)) {
            shapes.add(DOWN_SHAPE);
        }
        if (state.get(NORTH)) {
            shapes.add(NORTH_SHAPE);
        }
        if (state.get(SOUTH)) {
            shapes.add(SOUTH_SHAPE);
        }
        if (state.get(EAST)) {
            shapes.add(EAST_SHAPE);
        }
        if (state.get(WEST)) {
            shapes.add(WEST_SHAPE);
        }

        VoxelShape SHAPE = Stream.of(CORE_SHAPE).reduce(VoxelShapes::union).get();
        for (VoxelShape shape : shapes) {
            SHAPE = VoxelShapes.union(SHAPE, shape);
        }

        return SHAPE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(UP, DOWN, NORTH, SOUTH, EAST, WEST);
    }

    private boolean canConnectToBlock(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        return block instanceof CableBlock;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        updateConnections(world, pos, state);
    }

    private void updateConnections(World world, BlockPos pos, BlockState state) {
        if (!world.isClient()) {
            BlockState newState = state;
            for (Direction direction : Direction.values()) {
                BlockPos neighborPos = pos.offset(direction);
                BooleanProperty property = getPropertyForDirection(direction);
                boolean canConnect = canConnectTo(world, neighborPos, direction);
                newState = newState.with(property, canConnect);
            }
            world.setBlockState(pos, newState, 2);
        }
    }

    private BooleanProperty getPropertyForDirection(Direction direction) {
        switch (direction) {
            case UP: return UP;
            case DOWN: return DOWN;
            case NORTH: return NORTH;
            case SOUTH: return SOUTH;
            case EAST: return EAST;
            case WEST: return WEST;
            default: throw new IllegalArgumentException("Invalid direction: " + direction);
        }
    }

    private boolean canConnectTo(World world, BlockPos pos, Direction direction) {
        EnergyStorage energyStorage = EnergyStorage.SIDED.find(world, pos, direction);
        if (energyStorage != null) {
            return true;
        } else {
            BlockState state = world.getBlockState(pos);
            return state.getBlock() instanceof CableBlock;
        }
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        return (BlockState)this.getDefaultState()
                .with(UP, this.canConnectToBlock(world, pos.up()))
                .with(DOWN, this.canConnectToBlock(world, pos.down()))
                .with(NORTH, this.canConnectToBlock(world, pos.north()))
                .with(SOUTH, this.canConnectToBlock(world, pos.south()))
                .with(EAST, this.canConnectToBlock(world, pos.east()))
                .with(WEST, this.canConnectToBlock(world, pos.west()));
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return (wrld, pos, blockState, t) -> {
            if (t instanceof CableBlockEntity blockEntity) {
                CableBlockEntity.tick(wrld, pos, blockState, blockEntity);
            }
        };
    }
    /*@Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity == null) {
            return;
        }

        if (blockEntity instanceof CableBlockEntity cableBlockEntity) {
            cableBlockEntity.onRemoved();
        }
    }*/
}

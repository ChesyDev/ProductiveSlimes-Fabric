package com.chesy.productiveslimes.block.custom;

import com.chesy.productiveslimes.block.entity.PipeBlockEntity;
import com.chesy.productiveslimes.util.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

public class PipeBlock extends Block implements BlockEntityProvider {
    public static final BooleanProperty UP    = BooleanProperty.of("up");
    public static final BooleanProperty DOWN  = BooleanProperty.of("down");
    public static final BooleanProperty NORTH = BooleanProperty.of("north");
    public static final BooleanProperty SOUTH = BooleanProperty.of("south");
    public static final BooleanProperty EAST  = BooleanProperty.of("east");
    public static final BooleanProperty WEST  = BooleanProperty.of("west");

    private static final VoxelShape CORE_SHAPE  = Block.createCuboidShape(5, 5, 5, 11, 11, 11);
    private static final VoxelShape UP_SHAPE    = Block.createCuboidShape(5, 11, 5, 11, 15, 11);
    private static final VoxelShape DOWN_SHAPE  = Block.createCuboidShape(5, 0, 5, 11, 5, 11);
    private static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(5, 5, 0, 11, 11, 5);
    private static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(5, 5, 11, 11, 11, 15);
    private static final VoxelShape EAST_SHAPE  = Block.createCuboidShape(11, 5, 5, 15, 11, 11);
    private static final VoxelShape WEST_SHAPE  = Block.createCuboidShape(0, 5, 5, 5, 11, 11);


    public PipeBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState()
                .with(UP, false)
                .with(DOWN, false)
                .with(NORTH, false)
                .with(SOUTH, false)
                .with(EAST, false)
                .with(WEST, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(UP, DOWN, NORTH, SOUTH, EAST, WEST);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        WorldAccess level = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        return this.getDefaultState()
                .with(UP, canConnectToBlock(level, pos.up()))
                .with(DOWN, canConnectToBlock(level, pos.down()))
                .with(NORTH, canConnectToBlock(level, pos.north()))
                .with(SOUTH, canConnectToBlock(level, pos.south()))
                .with(EAST, canConnectToBlock(level, pos.east()))
                .with(WEST, canConnectToBlock(level, pos.west()));
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape shape = CORE_SHAPE;
        if (state.get(UP))    shape = VoxelShapes.union(shape, UP_SHAPE);
        if (state.get(DOWN))  shape = VoxelShapes.union(shape, DOWN_SHAPE);
        if (state.get(NORTH)) shape = VoxelShapes.union(shape, NORTH_SHAPE);
        if (state.get(SOUTH)) shape = VoxelShapes.union(shape, SOUTH_SHAPE);
        if (state.get(EAST))  shape = VoxelShapes.union(shape, EAST_SHAPE);
        if (state.get(WEST))  shape = VoxelShapes.union(shape, WEST_SHAPE);
        return shape;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (!world.isClient) {
            // Instead of updating immediately, schedule a tick update.
            world.scheduleBlockTick(pos, this, 1);
        }
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        if (!world.isClient()) {
            world.scheduleBlockTick(pos, this, 1);
            for (Direction direction : Direction.values()) {
                BlockPos neighborPos = pos.offset(direction);
                world.scheduleBlockTick(neighborPos, this, 1);
            }
        }
    }

    @Override
    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        super.onStateReplaced(state, world, pos, moved);
        if (!world.isClient) {
            for (Direction direction : Direction.values()) {
                BlockPos neighborPos = pos.offset(direction);
                world.scheduleBlockTick(neighborPos, this, 1);
            }
        }
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        updateConnections(world, pos, state);
    }

    private void updateConnections(World level, BlockPos pos, BlockState state) {
        if (!level.isClient) {
            BlockState newState = state;
            for (Direction direction : Direction.values()) {
                BlockPos neighborPos = pos.offset(direction);
                BooleanProperty property = getPropertyForDirection(direction);
                boolean canConnect = canConnectTo(level, pos, neighborPos, direction);
                newState = newState.with(property, canConnect);
            }
            level.setBlockState(pos, newState, 2);
        }
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (world instanceof World lvl) {
            boolean canConnect = canConnectTo(lvl, pos, neighborPos, direction);
            return state.with(getPropertyForDirection(direction), canConnect);
        }
        return state;
    }

    private BooleanProperty getPropertyForDirection(Direction direction) {
        return switch (direction) {
            case UP -> UP;
            case DOWN -> DOWN;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
        };
    }

    private boolean canConnectTo(World level, BlockPos currentPos, BlockPos pos, Direction direction) {
        // Get neighbor's handler from the side facing current block.
        Storage<FluidVariant> neighborHandler = FluidStorage.SIDED.find(level, pos, direction.getOpposite());
        // Get current block's handler from the side facing neighbor.
        Storage<FluidVariant> currentHandler = FluidStorage.SIDED.find(level, currentPos, direction);

        if (!(level.getBlockEntity(pos) instanceof PipeBlockEntity)) {
            return neighborHandler != null;
        }

        if (neighborHandler != null && currentHandler != null) {
            FluidVariant currentFluid = FluidVariant.blank();
            FluidVariant neighborFluid = FluidVariant.blank();

            for (StorageView<FluidVariant> view : neighborHandler) {
                currentFluid = view.getResource();
                if (!currentFluid.isBlank()) break;
            }

            for (StorageView<FluidVariant> view : currentHandler) {
                neighborFluid = view.getResource();
                if (!neighborFluid.isBlank()) break;
            }

            // Both empty: connect
            if (currentFluid.isBlank() && neighborFluid.isBlank()) {
                return true;
            }
            // One empty but not the other: do not connect
            if (currentFluid.isBlank() || neighborFluid.isBlank()) {
                return false;
            }
            // Otherwise, connect only if the fluids are the same
            return currentFluid.getFluid().equals(neighborFluid.getFluid());
        }

        // Fallback: if neighbor is a PipeBlock, try to re-acquire the handlers.
        BlockState neighborState = level.getBlockState(pos);
        if (neighborState.getBlock() instanceof PipeBlock) {
            Storage<FluidVariant> pipeNeighborHandler = FluidStorage.SIDED.find(level, pos, direction.getOpposite());
            Storage<FluidVariant> pipeCurrentHandler  = FluidStorage.SIDED.find(level, currentPos, direction);

            if (pipeNeighborHandler != null && pipeCurrentHandler != null) {
                FluidVariant currentFluid = FluidVariant.blank();
                FluidVariant neighborFluid = FluidVariant.blank();

                for (StorageView<FluidVariant> view : pipeNeighborHandler) {
                    currentFluid = view.getResource();
                    if (!currentFluid.isBlank()) break;
                }

                for (StorageView<FluidVariant> view : pipeCurrentHandler) {
                    neighborFluid = view.getResource();
                    if (!neighborFluid.isBlank()) break;
                }

                if (currentFluid.isBlank() && neighborFluid.isBlank()) {
                    return true;
                }
                if (currentFluid.isBlank() || neighborFluid.isBlank()) {
                    return false;
                }
                return currentFluid.getFluid().equals(neighborFluid.getFluid());
            }
            // Fallback for pipes if handlers still aren’t available.
            return true;
        }

        return false;
    }

    private boolean canConnectToBlock(WorldAccess level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        return block instanceof PipeBlock || canConnectBasedOnBlock((World) level, pos);
    }

    private boolean canConnectBasedOnBlock(World level, BlockPos pos) {
        Storage<FluidVariant> block = FluidStorage.SIDED.find(level, pos,null);
        return block != null;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PipeBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return (lvl, pos, blockState, t) -> {
            if (t instanceof PipeBlockEntity blockEntity) {
                PipeBlockEntity.tick(lvl, pos, blockEntity);
            }
        };
    }
}

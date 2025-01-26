package com.chesy.productiveslimes.block.custom;

import com.chesy.productiveslimes.block.entity.SlimeballCollectorBlockEntity;
import com.chesy.productiveslimes.block.entity.SolidingStationBlockEntity;
import com.chesy.productiveslimes.util.ContainerUtils;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class SlimeballCollectorBlock extends Block implements BlockEntityProvider {
    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;

    private static final VoxelShape SOUTH_SHAPE = Stream.of(
            Stream.of(
                    Block.createCuboidShape(3, 3, 6, 4, 13, 8),
                    Block.createCuboidShape(12, 3, 6, 13, 13, 8),
                    Block.createCuboidShape(4, 3, 6, 12, 4, 8),
                    Block.createCuboidShape(4, 12, 6, 12, 13, 8)
            ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(),
            Stream.of(
                    Block.createCuboidShape(3, 3, 8, 4, 13, 10),
                    Block.createCuboidShape(12, 3, 8, 13, 13, 10),
                    Block.createCuboidShape(4, 3, 8, 12, 4, 10),
                    Block.createCuboidShape(4, 12, 8, 12, 13, 10)
            ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(),
            Stream.of(
                    Block.createCuboidShape(1, 1, 12, 2, 15, 14),
                    Block.createCuboidShape(14, 1, 12, 15, 15, 14),
                    Block.createCuboidShape(2, 1, 12, 14, 2, 14),
                    Block.createCuboidShape(2, 14, 12, 14, 15, 14)
            ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(),
            Stream.of(
                    Block.createCuboidShape(0, 0, 14, 16, 1, 16),
                    Block.createCuboidShape(15, 1, 14, 16, 15, 16),
                    Block.createCuboidShape(0, 1, 14, 1, 15, 16),
                    Block.createCuboidShape(0, 15, 14, 16, 16, 16)
            ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(),
            Stream.of(
                    Block.createCuboidShape(0, 0, 0, 16, 1, 2),
                    Block.createCuboidShape(15, 1, 0, 16, 15, 2),
                    Block.createCuboidShape(0, 1, 0, 1, 15, 2),
                    Block.createCuboidShape(0, 15, 0, 16, 16, 2)
            ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(),
            Stream.of(
                    Block.createCuboidShape(1, 1, 2, 2, 15, 4),
                    Block.createCuboidShape(14, 1, 2, 15, 15, 4),
                    Block.createCuboidShape(2, 1, 2, 14, 2, 4),
                    Block.createCuboidShape(2, 14, 2, 14, 15, 4)
            ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(),
            Stream.of(
                    Block.createCuboidShape(3, 13, 10, 13, 14, 12),
                    Block.createCuboidShape(3, 2, 10, 13, 3, 12),
                    Block.createCuboidShape(2, 2, 10, 3, 14, 12),
                    Block.createCuboidShape(13, 2, 10, 14, 14, 12)
            ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(),
            Stream.of(
                    Block.createCuboidShape(3, 13, 4, 13, 14, 6),
                    Block.createCuboidShape(3, 2, 4, 13, 3, 6),
                    Block.createCuboidShape(2, 2, 4, 3, 14, 6),
                    Block.createCuboidShape(13, 2, 4, 14, 14, 6)
            ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(),
            VoxelShapes.combineAndSimplify(Block.createCuboidShape(5, 4, 5, 11, 10, 11), Block.createCuboidShape(7.5, 6.5, 11, 8.5, 8.5, 12), BooleanBiFunction.OR)).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

    private static final VoxelShape NORTH_SHAPE = Stream.of(
            Stream.of(
                    Block.createCuboidShape(12, 3, 8, 13, 13, 10),
                    Block.createCuboidShape(3, 3, 8, 4, 13, 10),
                    Block.createCuboidShape(4, 3, 8, 12, 4, 10),
                    Block.createCuboidShape(4, 12, 8, 12, 13, 10)
            ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(),
            Stream.of(
                    Block.createCuboidShape(12, 3, 6, 13, 13, 8),
                    Block.createCuboidShape(3, 3, 6, 4, 13, 8),
                    Block.createCuboidShape(4, 3, 6, 12, 4, 8),
                    Block.createCuboidShape(4, 12, 6, 12, 13, 8)
            ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(),
            Stream.of(
                    Block.createCuboidShape(14, 1, 2, 15, 15, 4),
                    Block.createCuboidShape(1, 1, 2, 2, 15, 4),
                    Block.createCuboidShape(2, 1, 2, 14, 2, 4),
                    Block.createCuboidShape(2, 14, 2, 14, 15, 4)
            ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(),
            Stream.of(
                    Block.createCuboidShape(0, 0, 0, 16, 1, 2),
                    Block.createCuboidShape(0, 1, 0, 1, 15, 2),
                    Block.createCuboidShape(15, 1, 0, 16, 15, 2),
                    Block.createCuboidShape(0, 15, 0, 16, 16, 2)
            ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(),
            Stream.of(
                    Block.createCuboidShape(0, 0, 14, 16, 1, 16),
                    Block.createCuboidShape(0, 1, 14, 1, 15, 16),
                    Block.createCuboidShape(15, 1, 14, 16, 15, 16),
                    Block.createCuboidShape(0, 15, 14, 16, 16, 16)
            ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(),
            Stream.of(
                    Block.createCuboidShape(14, 1, 12, 15, 15, 14),
                    Block.createCuboidShape(1, 1, 12, 2, 15, 14),
                    Block.createCuboidShape(2, 1, 12, 14, 2, 14),
                    Block.createCuboidShape(2, 14, 12, 14, 15, 14)
            ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(),
            Stream.of(
                    Block.createCuboidShape(3, 13, 4, 13, 14, 6),
                    Block.createCuboidShape(3, 2, 4, 13, 3, 6),
                    Block.createCuboidShape(13, 2, 4, 14, 14, 6),
                    Block.createCuboidShape(2, 2, 4, 3, 14, 6)
            ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(),
            Stream.of(
                    Block.createCuboidShape(3, 13, 10, 13, 14, 12),
                    Block.createCuboidShape(3, 2, 10, 13, 3, 12),
                    Block.createCuboidShape(13, 2, 10, 14, 14, 12),
                    Block.createCuboidShape(2, 2, 10, 3, 14, 12)
            ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(),
            VoxelShapes.combineAndSimplify(Block.createCuboidShape(5, 4, 5, 11, 10, 11), Block.createCuboidShape(7.5, 6.5, 4, 8.5, 8.5, 5), BooleanBiFunction.OR)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

    private static final VoxelShape EAST_SHAPE = Stream.of(
            Stream.of(
                    Block.createCuboidShape(6, 3, 12, 8, 13, 13),
                    Block.createCuboidShape(6, 3, 3, 8, 13, 4),
                    Block.createCuboidShape(6, 3, 4, 8, 4, 12),
                    Block.createCuboidShape(6, 12, 4, 8, 13, 12)
            ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(),
            Stream.of(
                    Block.createCuboidShape(8, 3, 12, 10, 13, 13),
                    Block.createCuboidShape(8, 3, 3, 10, 13, 4),
                    Block.createCuboidShape(8, 3, 4, 10, 4, 12),
                    Block.createCuboidShape(8, 12, 4, 10, 13, 12)
            ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(),
            Stream.of(
                    Block.createCuboidShape(12, 1, 14, 14, 15, 15),
                    Block.createCuboidShape(12, 1, 1, 14, 15, 2),
                    Block.createCuboidShape(12, 1, 2, 14, 2, 14),
                    Block.createCuboidShape(12, 14, 2, 14, 15, 14)
            ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(),
            Stream.of(
                    Block.createCuboidShape(14, 0, 0, 16, 1, 16),
                    Block.createCuboidShape(14, 1, 0, 16, 15, 1),
                    Block.createCuboidShape(14, 1, 15, 16, 15, 16),
                    Block.createCuboidShape(14, 15, 0, 16, 16, 16)
            ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(),
            Stream.of(
                    Block.createCuboidShape(0, 0, 0, 2, 1, 16),
                    Block.createCuboidShape(0, 1, 0, 2, 15, 1),
                    Block.createCuboidShape(0, 1, 15, 2, 15, 16),
                    Block.createCuboidShape(0, 15, 0, 2, 16, 16)
            ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(),
            Stream.of(
                    Block.createCuboidShape(2, 1, 14, 4, 15, 15),
                    Block.createCuboidShape(2, 1, 1, 4, 15, 2),
                    Block.createCuboidShape(2, 1, 2, 4, 2, 14),
                    Block.createCuboidShape(2, 14, 2, 4, 15, 14)
            ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(),
            Stream.of(
                    Block.createCuboidShape(10, 13, 3, 12, 14, 13),
                    Block.createCuboidShape(10, 2, 3, 12, 3, 13),
                    Block.createCuboidShape(10, 2, 13, 12, 14, 14),
                    Block.createCuboidShape(10, 2, 2, 12, 14, 3)
            ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(),
            Stream.of(
                    Block.createCuboidShape(4, 13, 3, 6, 14, 13),
                    Block.createCuboidShape(4, 2, 3, 6, 3, 13),
                    Block.createCuboidShape(4, 2, 13, 6, 14, 14),
                    Block.createCuboidShape(4, 2, 2, 6, 14, 3)
            ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(),
            VoxelShapes.combineAndSimplify(Block.createCuboidShape(5, 4, 5, 11, 10, 11), Block.createCuboidShape(11, 6.5, 7.5, 12, 8.5, 8.5), BooleanBiFunction.OR)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

    private static final VoxelShape WEST_SHAPE = Stream.of(
            Stream.of(
                    Block.createCuboidShape(8, 3, 3, 10, 13, 4),
                    Block.createCuboidShape(8, 3, 12, 10, 13, 13),
                    Block.createCuboidShape(8, 3, 4, 10, 4, 12),
                    Block.createCuboidShape(8, 12, 4, 10, 13, 12)
            ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(),
            Stream.of(
                    Block.createCuboidShape(6, 3, 3, 8, 13, 4),
                    Block.createCuboidShape(6, 3, 12, 8, 13, 13),
                    Block.createCuboidShape(6, 3, 4, 8, 4, 12),
                    Block.createCuboidShape(6, 12, 4, 8, 13, 12)
            ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(),
            Stream.of(
                    Block.createCuboidShape(2, 1, 1, 4, 15, 2),
                    Block.createCuboidShape(2, 1, 14, 4, 15, 15),
                    Block.createCuboidShape(2, 1, 2, 4, 2, 14),
                    Block.createCuboidShape(2, 14, 2, 4, 15, 14)
            ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(),
            Stream.of(
                    Block.createCuboidShape(0, 0, 0, 2, 1, 16),
                    Block.createCuboidShape(0, 1, 15, 2, 15, 16),
                    Block.createCuboidShape(0, 1, 0, 2, 15, 1),
                    Block.createCuboidShape(0, 15, 0, 2, 16, 16)
            ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(),
            Stream.of(
                    Block.createCuboidShape(14, 0, 0, 16, 1, 16),
                    Block.createCuboidShape(14, 1, 15, 16, 15, 16),
                    Block.createCuboidShape(14, 1, 0, 16, 15, 1),
                    Block.createCuboidShape(14, 15, 0, 16, 16, 16)
            ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(),
            Stream.of(
                    Block.createCuboidShape(12, 1, 1, 14, 15, 2),
                    Block.createCuboidShape(12, 1, 14, 14, 15, 15),
                    Block.createCuboidShape(12, 1, 2, 14, 2, 14),
                    Block.createCuboidShape(12, 14, 2, 14, 15, 14)
            ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(),
            Stream.of(
                    Block.createCuboidShape(4, 13, 3, 6, 14, 13),
                    Block.createCuboidShape(4, 2, 3, 6, 3, 13),
                    Block.createCuboidShape(4, 2, 2, 6, 14, 3),
                    Block.createCuboidShape(4, 2, 13, 6, 14, 14)
            ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(),
            Stream.of(
                    Block.createCuboidShape(10, 13, 3, 12, 14, 13),
                    Block.createCuboidShape(10, 2, 3, 12, 3, 13),
                    Block.createCuboidShape(10, 2, 2, 12, 14, 3),
                    Block.createCuboidShape(10, 2, 13, 12, 14, 14)
            ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(),
            VoxelShapes.combineAndSimplify(Block.createCuboidShape(5, 4, 5, 11, 10, 11), Block.createCuboidShape(4, 6.5, 7.5, 5, 8.5, 8.5), BooleanBiFunction.OR)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

    public SlimeballCollectorBlock(Settings properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends Block> getCodec() {
        return createCodec(SlimeballCollectorBlock::new);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SlimeballCollectorBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient) {
            return null;
        }

        return ((world1, pos, state1, blockEntity) -> {
            if(blockEntity instanceof SlimeballCollectorBlockEntity slimeballCollectorBlockEntity) {
                slimeballCollectorBlockEntity.tick(world1, pos, state1);
            }
        });
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState pState, BlockView pLevel, BlockPos pPos, ShapeContext pContext) {
        switch (pState.get(FACING)) {
            case NORTH:
                return NORTH_SHAPE;
            case SOUTH:
                return SOUTH_SHAPE;
            case EAST:
                return EAST_SHAPE;
            case WEST:
                return WEST_SHAPE;
            default:
                return VoxelShapes.fullCube();
        }
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING);
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof SlimeballCollectorBlockEntity slimeballCollectorBlockEntity) {
            ContainerUtils.dropContents(world, pos, slimeballCollectorBlockEntity);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient()) {
            BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof SlimeballCollectorBlockEntity slimeballCollectorBlockEntity) {
                player.openHandledScreen(slimeballCollectorBlockEntity);
            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }

        return ActionResult.SUCCESS;
    }
}
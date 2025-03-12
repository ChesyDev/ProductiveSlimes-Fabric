package com.chesy.productiveslimes.util;


import com.chesy.productiveslimes.block.ModBlocks;
import com.chesy.productiveslimes.block.custom.SlimyPortalBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Predicate;

public class SlimyPortalShape {
    private static final int MIN_WIDTH = 2;
    public static final int MAX_WIDTH = 21;
    private static final int MIN_HEIGHT = 3;
    public static final int MAX_HEIGHT = 21;
    private static final AbstractBlock.ContextPredicate FRAME = (state, level, pos) -> state.isOf(ModBlocks.SLIMY_PORTAL_FRAME);
    private static final float SAFE_TRAVEL_MAX_ENTITY_XY = 4.0F;
    private static final double SAFE_TRAVEL_MAX_VERTICAL_DELTA = 1.0;
    private final Direction.Axis axis;
    private final Direction rightDir;
    private final int numPortalBlocks;
    private final BlockPos bottomLeft;
    private final int height;
    private final int width;

    private SlimyPortalShape(Direction.Axis axis, int numPortalBlocks, Direction rightDir, BlockPos bottomLeft, int width, int height) {
        this.axis = axis;
        this.numPortalBlocks = numPortalBlocks;
        this.rightDir = rightDir;
        this.bottomLeft = bottomLeft;
        this.width = width;
        this.height = height;
    }

    public static Optional<SlimyPortalShape> findEmptyPortalShape(WorldAccess level, BlockPos bottomLeft, Direction.Axis axis) {
        return findPortalShape(level, bottomLeft, slimyPortalShape -> slimyPortalShape.isValid() && slimyPortalShape.numPortalBlocks == 0, axis);
    }

    public static Optional<SlimyPortalShape> findPortalShape(WorldAccess level, BlockPos bottomLeft, Predicate<SlimyPortalShape> predicate, Direction.Axis axis) {
        Optional<SlimyPortalShape> optional = Optional.of(findAnyShape(level, bottomLeft, axis)).filter(predicate);

        if (optional.isPresent()) {
            return optional;
        } else {
            Direction.Axis direction$axis = axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
            return Optional.of(findAnyShape(level, bottomLeft, direction$axis)).filter(predicate);
        }
    }

    public static SlimyPortalShape findAnyShape(BlockView level, BlockPos bottomLeft, Direction.Axis axis) {
        Direction direction = axis == Direction.Axis.X ? Direction.WEST : Direction.SOUTH;
        BlockPos blockpos = calculateBottomLeft(level, direction, bottomLeft);
        if (blockpos == null) {
            return new SlimyPortalShape(axis, 0, direction, bottomLeft, 0, 0);
        } else {
            int i = calculateWidth(level, blockpos, direction);
            if (i == 0) {
                return new SlimyPortalShape(axis, 0, direction, blockpos, 0, 0);
            } else {
                MutableInt mutableint = new MutableInt();
                int j = calculateHeight(level, blockpos, direction, i, mutableint);
                return new SlimyPortalShape(axis, mutableint.getValue(), direction, blockpos, i, j);
            }
        }
    }

    @Nullable
    private static BlockPos calculateBottomLeft(BlockView level, Direction p_direction, BlockPos pos) {
        int i = Math.max(63, pos.getY() - 21);

        while (pos.getY() > i && isEmpty(level.getBlockState(pos.down()))) {
            pos = pos.down();
        }

        Direction direction = p_direction.getOpposite();
        int j = getDistanceUntilEdgeAboveFrame(level, pos, direction) - 1;
        return j < 0 ? null : pos.offset(direction, j);
    }

    private static int calculateWidth(BlockView level, BlockPos bottomLeft, Direction direction) {
        int i = getDistanceUntilEdgeAboveFrame(level, bottomLeft, direction);
        return i >= 2 && i <= 21 ? i : 0;
    }

    private static int getDistanceUntilEdgeAboveFrame(BlockView level, BlockPos pos, Direction direction) {
        BlockPos.Mutable blockpos$mutableblockpos = new BlockPos.Mutable();

        for (int i = 0; i <= 21; i++) {
            blockpos$mutableblockpos.set(pos).move(direction, i);
            BlockState blockstate = level.getBlockState(blockpos$mutableblockpos);
            if (!isEmpty(blockstate)) {
                if (FRAME.test(blockstate, level, blockpos$mutableblockpos)) {
                    return i;
                }
                break;
            }

            BlockState blockstate1 = level.getBlockState(blockpos$mutableblockpos.move(Direction.DOWN));
            if (!FRAME.test(blockstate1, level, blockpos$mutableblockpos)) {
                break;
            }
        }

        return 0;
    }

    private static int calculateHeight(BlockView level, BlockPos pos, Direction direction, int width, MutableInt portalBlocks) {
        BlockPos.Mutable blockpos$mutableblockpos = new BlockPos.Mutable();
        int i = getDistanceUntilTop(level, pos, direction, blockpos$mutableblockpos, width, portalBlocks);
        return i >= 3 && i <= 21 && hasTopFrame(level, pos, direction, blockpos$mutableblockpos, width, i) ? i : 0;
    }

    private static boolean hasTopFrame(
            BlockView level, BlockPos pos, Direction direction, BlockPos.Mutable checkPos, int width, int distanceUntilTop
    ) {
        for (int i = 0; i < width; i++) {
            BlockPos.Mutable blockpos$mutableblockpos = checkPos.set(pos).move(Direction.UP, distanceUntilTop).move(direction, i);
            if (!FRAME.test(level.getBlockState(blockpos$mutableblockpos), level, blockpos$mutableblockpos)) {
                return false;
            }
        }

        return true;
    }

    private static int getDistanceUntilTop(
            BlockView level, BlockPos pos, Direction direction, BlockPos.Mutable checkPos, int width, MutableInt portalBlocks
    ) {
        for (int i = 0; i < 21; i++) {
            checkPos.set(pos).move(Direction.UP, i).move(direction, -1);
            if (!FRAME.test(level.getBlockState(checkPos), level, checkPos)) {
                return i;
            }

            checkPos.set(pos).move(Direction.UP, i).move(direction, width);
            if (!FRAME.test(level.getBlockState(checkPos), level, checkPos)) {
                return i;
            }

            for (int j = 0; j < width; j++) {
                checkPos.set(pos).move(Direction.UP, i).move(direction, j);
                BlockState blockstate = level.getBlockState(checkPos);
                if (!isEmpty(blockstate)) {
                    return i;
                }

                if (blockstate.isOf(ModBlocks.SLIMY_PORTAL)) {
                    portalBlocks.increment();
                }
            }
        }

        return 21;
    }

    private static boolean isEmpty(BlockState state) {
        return state.isAir() || state.isIn(BlockTags.FIRE) || state.isOf(ModBlocks.SLIMY_PORTAL);
    }

    public boolean isValid() {
        return this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
    }

    public void createPortalBlocks(WorldAccess level) {
        BlockState blockstate = ModBlocks.SLIMY_PORTAL.getDefaultState().with(SlimyPortalBlock.AXIS, this.axis);
        BlockPos.iterate(this.bottomLeft, this.bottomLeft.offset(Direction.UP, this.height - 1).offset(this.rightDir, this.width - 1))
                .forEach(p_374024_ -> level.setBlockState(p_374024_, blockstate, 18));
    }

    public boolean isComplete() {
        return this.isValid() && this.numPortalBlocks == this.width * this.height;
    }

    public static Vec3d getRelativePosition(BlockLocating.Rectangle foundRectangle, Direction.Axis axis, Vec3d pos, EntityDimensions entityDimensions) {
        double d0 = (double)foundRectangle.width - (double)entityDimensions.width();
        double d1 = (double)foundRectangle.height - (double)entityDimensions.height();
        BlockPos blockpos = foundRectangle.lowerLeft;
        double d2;
        if (d0 > 0.0) {
            double d3 = (double)blockpos.getComponentAlongAxis(axis) + (double)entityDimensions.width() / 2.0;
            d2 = MathHelper.clamp(MathHelper.getLerpProgress(pos.getComponentAlongAxis(axis) - d3, 0.0, d0), 0.0, 1.0);
        } else {
            d2 = 0.5;
        }

        double d5;
        if (d1 > 0.0) {
            Direction.Axis direction$axis = Direction.Axis.Y;
            d5 = MathHelper.clamp(MathHelper.getLerpProgress(pos.getComponentAlongAxis(direction$axis) - (double)blockpos.getComponentAlongAxis(direction$axis), 0.0, d1), 0.0, 1.0);
        } else {
            d5 = 0.0;
        }

        Direction.Axis direction$axis1 = axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
        double d4 = pos.getComponentAlongAxis(direction$axis1) - ((double)blockpos.getComponentAlongAxis(direction$axis1) + 0.5);
        return new Vec3d(d2, d5, d4);
    }

    public static Vec3d findCollisionFreePosition(Vec3d pos, ServerWorld level, Entity entity, EntityDimensions dimensions) {
        if (!(dimensions.width() > 4.0F) && !(dimensions.height() > 4.0F)) {
            double d0 = (double)dimensions.height() / 2.0;
            Vec3d vec3 = pos.add(0.0, d0, 0.0);
            VoxelShape voxelshape = VoxelShapes.cuboid(
                    Box.of(vec3, (double)dimensions.width(), 0.0, (double)dimensions.width()).stretch(0.0, 1.0, 0.0).expand(1.0E-6)
            );
            Optional<Vec3d> optional = level.findClosestCollision(
                    entity, voxelshape, vec3, (double)dimensions.width(), (double)dimensions.height(), (double)dimensions.width()
            );
            Optional<Vec3d> optional1 = optional.map(p_259019_ -> p_259019_.subtract(0.0, d0, 0.0));
            return optional1.orElse(pos);
        } else {
            return pos;
        }
    }
}